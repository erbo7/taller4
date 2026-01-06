package cx;

import modelo.Agenda;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class AgendaDAO {

    private final conexion cx = new conexion();

    // ✅ Crear agenda usando procedimiento almacenado (NUEVO)
    public String crear(Agenda agenda) {
        System.out.println("=== AGENDADAO.crear() INICIANDO ===");
        System.out.println("  Veterinario ID: " + agenda.getIdVeterinario());
        System.out.println("  Fecha: " + agenda.getFecha());
        System.out.println("  Hora Inicio: " + agenda.getHoraInicio());
        System.out.println("  Hora Fin: " + agenda.getHoraFin());
        System.out.println("  Tipo Turno ID: " + agenda.getIdTipoTurno());
        System.out.println("  Observaciones: " + agenda.getObservaciones());
        System.out.println("  Creado Por: " + agenda.getCreadoPor());

        if (esFeriado(agenda.getFecha())) {
            System.out.println("❌ ERROR: Es feriado!");
            return "ERROR|No se puede registrar agenda en fechas feriadas";
        }

        System.out.println("✅ Fecha NO es feriado");

        String sql = "{CALL sp_registrar_turnos_agenda(?, ?, ?, ?, ?, ?, ?)}";
        System.out.println("  SQL a ejecutar: " + sql);

        try (Connection conn = cx.conectar()) {
            System.out.println("✅ Conexión a BD establecida");

            try (CallableStatement cs = conn.prepareCall(sql)) {
                System.out.println("✅ PreparedStatement creado");

                // Setear parámetros
                cs.setInt(1, agenda.getIdVeterinario());
                cs.setDate(2, java.sql.Date.valueOf(agenda.getFecha()));
                cs.setTime(3, java.sql.Time.valueOf(agenda.getHoraInicio()));
                cs.setTime(4, java.sql.Time.valueOf(agenda.getHoraFin()));
                cs.setInt(5, agenda.getIdTipoTurno());
                cs.setString(6, agenda.getObservaciones());
                cs.setInt(7, agenda.getCreadoPor());

                System.out.println("✅ Parámetros seteados");
                System.out.println("⏳ Ejecutando stored procedure...");

                // ✅ AHORA SÍ: usar executeUpdate porque el SP ya NO devuelve ResultSet
                int filasAfectadas = cs.executeUpdate();

                System.out.println("✅ Stored procedure ejecutado. Filas afectadas: " + filasAfectadas);

                // ✅ Confirmar éxito
                if (filasAfectadas >= 0) {
                    System.out.println("🎉 Agenda creada exitosamente");
                    return "OK|Agenda creada exitosamente";
                } else {
                    System.out.println("❌ Ninguna fila afectada");
                    return "ERROR|No se pudo crear la agenda";
                }

            } catch (SQLException e) {
                System.err.println("❌ SQLException en crear agenda:");
                System.err.println("   Mensaje: " + e.getMessage());
                System.err.println("   SQL State: " + e.getSQLState());
                System.err.println("   Error Code: " + e.getErrorCode());

                String msg = e.getMessage();
                if (msg != null) {
                    if (msg.contains("feriados")) {
                        return "ERROR|No se puede registrar agenda en fechas feriadas";
                    } else if (msg.contains("Conflicto de horarios")) {
                        return "ERROR|Conflicto de horarios: ya existe agenda en ese rango";
                    } else if (msg.contains("Tipo de turno no válido")) {
                        return "ERROR|Tipo de turno no válido o inactivo";
                    } else if (msg.contains("Hora de inicio")) {
                        return "ERROR|" + msg;
                    }
                }

                e.printStackTrace();
                return "ERROR|" + e.getMessage();

            }
        } catch (SQLException e) {
            System.err.println("❌ Error al conectar a la BD: " + e.getMessage());
            e.printStackTrace();
            return "ERROR|No se pudo conectar a la base de datos";
        } catch (Exception e) {
            System.err.println("❌ Error inesperado en crear agenda: " + e.getMessage());
            e.printStackTrace();
            return "ERROR|Error inesperado: " + e.getMessage();
        }
    }

    // ✅ ACTUALIZAR agenda (CORREGIDO con logs)
    // ✅ ACTUALIZAR agenda (CORREGIDO - sin esperar ResultSet)
    public String actualizar(Agenda agenda, int modificadoPor) {
        System.out.println("=== AGENDADAO.actualizar() ===");
        System.out.println("  ID Agenda: " + agenda.getIdAgenda());
        System.out.println("  Veterinario: " + agenda.getIdVeterinario());
        System.out.println("  Fecha: " + agenda.getFecha());
        System.out.println("  Hora Inicio: " + agenda.getHoraInicio());
        System.out.println("  Hora Fin: " + agenda.getHoraFin());
        System.out.println("  Tipo Turno: " + agenda.getIdTipoTurno());
        System.out.println("  Modificado Por: " + modificadoPor);

        String sql = "{CALL sp_editar_agenda(?, ?, ?, ?, ?, ?, ?, ?)}";
        try (Connection conn = cx.conectar(); CallableStatement cs = conn.prepareCall(sql)) {
            cs.setInt(1, agenda.getIdAgenda());
            cs.setInt(2, agenda.getIdVeterinario());
            cs.setDate(3, java.sql.Date.valueOf(agenda.getFecha()));
            cs.setTime(4, java.sql.Time.valueOf(agenda.getHoraInicio()));
            cs.setTime(5, java.sql.Time.valueOf(agenda.getHoraFin()));
            cs.setInt(6, agenda.getIdTipoTurno());
            cs.setString(7, agenda.getObservaciones());
            cs.setInt(8, modificadoPor);

            System.out.println("  Ejecutando sp_editar_agenda...");
            // ✅ Usa executeUpdate() porque NO hay ResultSet
            int filas = cs.executeUpdate();
            System.out.println("  Filas afectadas: " + filas);
            return "OK|Agenda actualizada exitosamente";

        } catch (SQLException e) {
            System.err.println("❌ SQLException en actualizar agenda:");
            System.err.println("   Mensaje: " + e.getMessage());
            // ... (mensaje de error como tenés)
            String msg = e.getMessage();
            if (msg != null) {
                if (msg.contains("existen citas asignadas")) {
                    return "ERROR|No se puede editar: existen citas asignadas a esta agenda";
                } else if (msg.contains("Conflicto de horarios")) {
                    return "ERROR|Conflicto de horarios al editar";
                }
            }
            e.printStackTrace();
            return "ERROR|" + e.getMessage();
        } catch (Exception e) {
            System.err.println("❌ Exception en actualizar agenda: " + e.getMessage());
            e.printStackTrace();
            return "ERROR|" + e.getMessage();
        }
    }

// ✅ ANULAR agenda (CORREGIDO con logs)
    public String anular(int idAgenda, String motivo, int anuladoPor) {
        System.out.println("=== AGENDADAO.anular() ===");
        System.out.println("  ID Agenda: " + idAgenda);
        System.out.println("  Motivo: " + motivo);
        System.out.println("  Anulado Por: " + anuladoPor);

        String sql = "{CALL sp_anular_agenda(?, ?, ?)}";
        try (Connection conn = cx.conectar(); CallableStatement cs = conn.prepareCall(sql)) {
            cs.setInt(1, idAgenda);
            cs.setString(2, motivo);
            cs.setInt(3, anuladoPor);

            System.out.println("  Ejecutando sp_anular_agenda...");
            boolean tieneResultados = cs.execute(); // Usa execute() por si acaso
            System.out.println("  Stored procedure ejecutado. ¿Tiene resultados? " + tieneResultados);

            return "OK|Agenda anulada exitosamente";
        } catch (SQLException e) {
            System.err.println("❌ SQLException en anular agenda:");
            System.err.println("   Mensaje: " + e.getMessage());
            System.err.println("   SQL State: " + e.getSQLState());
            System.err.println("   Error Code: " + e.getErrorCode());

            String msg = e.getMessage();
            if (msg != null) {
                if (msg.contains("existen citas asignadas")) {
                    return "ERROR|No se puede anular: existen citas asignadas a esta agenda";
                } else if (msg.contains("Agenda no encontrada")) {
                    return "ERROR|Agenda no encontrada o ya anulada";
                }
            }
            e.printStackTrace();
            return "ERROR|" + e.getMessage();
        } catch (Exception e) {
            System.err.println("❌ Exception en anular agenda: " + e.getMessage());
            e.printStackTrace();
            return "ERROR|" + e.getMessage();
        }
    }

    // ✅ Obtener agenda por veterinario y fecha (CORREGIDO - SIN DURACION_MINUTOS)
    public List<Agenda> obtenerPorVeterinarioYFecha(int idVeterinario, LocalDate fecha) {
        List<Agenda> agendas = new ArrayList<>();
        // ✅ QUITADO: tt.DURACION_MINUTOS
        String sql = "SELECT a.*, v.NOMBRE AS nombre_veterinario, "
                + "tt.NOMBRE AS nombre_tipo_turno " // <-- SIN DURACION_MINUTOS
                + "FROM agenda a "
                + "LEFT JOIN veterinarios v ON a.ID_VETERINARIO = v.ID_VETERINARIO "
                + "LEFT JOIN tipos_turno tt ON a.ID_TIPO_TURNO = tt.ID_TIPO_TURNO "
                + "WHERE a.ID_VETERINARIO = ? AND a.FECHA = ? AND a.ESTADO = 'ACTIVO' "
                + "ORDER BY a.HORA_INICIO ASC";

        try (Connection conn = cx.conectar(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idVeterinario);
            ps.setDate(2, java.sql.Date.valueOf(fecha));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Agenda agenda = mapearAgendaCompleta(rs);
                    agendas.add(agenda);
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al obtener agenda por veterinario: " + e.getMessage());
            e.printStackTrace();
        }
        return agendas;
    }

    // ✅ Obtener todas las agendas activas (CORREGIDO - SIN DURACION_MINUTOS)
    public List<Agenda> obtenerTodas() {
        List<Agenda> agendas = new ArrayList<>();
        // ✅ QUITADO: tt.DURACION_MINUTOS
        String sql = "SELECT a.*, v.NOMBRE AS nombre_veterinario, "
                + "tt.NOMBRE AS nombre_tipo_turno, " // <-- SIN DURACION_MINUTOS
                + "u.NOMBRE AS nombre_usuario "
                + "FROM agenda a "
                + "LEFT JOIN veterinarios v ON a.ID_VETERINARIO = v.ID_VETERINARIO "
                + "LEFT JOIN tipos_turno tt ON a.ID_TIPO_TURNO = tt.ID_TIPO_TURNO "
                + "LEFT JOIN usuarios u ON a.CREADO_POR = u.ID_USUARIO "
                + "WHERE a.ESTADO = 'ACTIVO' "
                + "ORDER BY a.FECHA DESC, a.HORA_INICIO ASC";

        try (Connection conn = cx.conectar(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Agenda agenda = mapearAgendaCompleta(rs);
                agenda.setNombreUsuario(rs.getString("nombre_usuario"));
                agendas.add(agenda);
            }
            System.out.println("✅ Agendas cargadas: " + agendas.size());
        } catch (SQLException e) {
            System.err.println("❌ Error en obtenerTodas(): " + e.getMessage());
            e.printStackTrace();
        }
        return agendas;
    }

    // ✅ Obtener agenda por ID (CORREGIDO - SIN DURACION_MINUTOS)
    public Agenda obtenerPorId(int idAgenda) {
        // ✅ QUITADO: tt.DURACION_MINUTOS
        String sql = "SELECT a.*, v.NOMBRE AS nombre_veterinario, "
                + "tt.NOMBRE AS nombre_tipo_turno " // <-- SIN DURACION_MINUTOS
                + "FROM agenda a "
                + "LEFT JOIN veterinarios v ON a.ID_VETERINARIO = v.ID_VETERINARIO "
                + "LEFT JOIN tipos_turno tt ON a.ID_TIPO_TURNO = tt.ID_TIPO_TURNO "
                + "WHERE a.ID_AGENDA = ?";

        try (Connection conn = cx.conectar(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idAgenda);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearAgendaCompleta(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener agenda por ID: " + e.getMessage());
        }
        return null;
    }

    // ✅ Obtener agendas por rango de fechas (CORREGIDO - SIN DURACION_MINUTOS)
    public List<Agenda> obtenerPorRangoFechas(LocalDate fechaInicio, LocalDate fechaFin) {
        List<Agenda> agendas = new ArrayList<>();
        // ✅ QUITADO: tt.DURACION_MINUTOS
        String sql = "SELECT a.*, v.NOMBRE AS nombre_veterinario, "
                + "tt.NOMBRE AS nombre_tipo_turno " // <-- SIN DURACION_MINUTOS
                + "FROM agenda a "
                + "LEFT JOIN veterinarios v ON a.ID_VETERINARIO = v.ID_VETERINARIO "
                + "LEFT JOIN tipos_turno tt ON a.ID_TIPO_TURNO = tt.ID_TIPO_TURNO "
                + "WHERE a.FECHA BETWEEN ? AND ? AND a.ESTADO = 'ACTIVO' "
                + "ORDER BY a.FECHA ASC, a.HORA_INICIO ASC";

        try (Connection conn = cx.conectar(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, java.sql.Date.valueOf(fechaInicio));
            ps.setDate(2, java.sql.Date.valueOf(fechaFin));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    agendas.add(mapearAgendaCompleta(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al obtener agendas por rango: " + e.getMessage());
        }
        return agendas;
    }

    // ✅ Verificar si un veterinario tiene agenda en un horario específico
    public boolean existeConflictoHorario(int idVeterinario, LocalDate fecha,
            LocalTime horaInicio, LocalTime horaFin,
            Integer excluirIdAgenda) {
        String sql = "SELECT COUNT(*) FROM agenda "
                + "WHERE ID_VETERINARIO = ? AND FECHA = ? AND ESTADO = 'ACTIVO' "
                + "AND (? < HORA_FIN AND ? > HORA_INICIO)";

        if (excluirIdAgenda != null) {
            sql += " AND ID_AGENDA != ?";
        }

        try (Connection conn = cx.conectar(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idVeterinario);
            ps.setDate(2, java.sql.Date.valueOf(fecha));
            ps.setTime(3, java.sql.Time.valueOf(horaInicio));
            ps.setTime(4, java.sql.Time.valueOf(horaFin));

            if (excluirIdAgenda != null) {
                ps.setInt(5, excluirIdAgenda);
            }

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al verificar conflicto horario: " + e.getMessage());
        }
        return false;
    }

    // ✅ Verifica si una fecha es feriado
    public boolean esFeriado(LocalDate fecha) {
        String sql = "SELECT COUNT(*) FROM feriados WHERE FECHA = ?";
        try (Connection conn = cx.conectar(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, java.sql.Date.valueOf(fecha));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al verificar feriado: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    // ✅ Método privado para mapear ResultSet a Agenda (CORREGIDO)
    private Agenda mapearAgendaCompleta(ResultSet rs) throws SQLException {
        Agenda agenda = new Agenda();
        agenda.setIdAgenda(rs.getInt("ID_AGENDA"));
        agenda.setIdVeterinario(rs.getInt("ID_VETERINARIO"));
        agenda.setFecha(rs.getDate("FECHA").toLocalDate());
        agenda.setHoraInicio(rs.getTime("HORA_INICIO").toLocalTime());
        agenda.setHoraFin(rs.getTime("HORA_FIN").toLocalTime());
        agenda.setIdTipoTurno(rs.getInt("ID_TIPO_TURNO"));
        agenda.setObservaciones(rs.getString("OBSERVACIONES"));
        agenda.setEstado(rs.getString("ESTADO"));
        agenda.setCreadoPor(rs.getInt("CREADO_POR"));
        agenda.setCreadoEn(rs.getTimestamp("CREADO_EN") != null
                ? rs.getTimestamp("CREADO_EN").toLocalDateTime() : null);

        // Trazabilidad
        agenda.setModificadoPor(rs.getObject("MODIFICADO_POR", Integer.class));
        if (rs.getTimestamp("MODIFICADO_EN") != null) {
            agenda.setModificadoEn(rs.getTimestamp("MODIFICADO_EN").toLocalDateTime());
        }

        agenda.setAnuladoPor(rs.getObject("ANULADO_POR", Integer.class));
        if (rs.getTimestamp("ANULADO_EN") != null) {
            agenda.setAnuladoEn(rs.getTimestamp("ANULADO_EN").toLocalDateTime());
        }

        agenda.setMotivoAnulacion(rs.getString("MOTIVO_ANULACION"));

        // Datos adicionales
        agenda.setNombreVeterinario(rs.getString("nombre_veterinario"));
        agenda.setNombreTipoTurno(rs.getString("nombre_tipo_turno"));
        // ✅ IMPORTANTE: NO intentes obtener DURACION_MINUTOS porque ya no está en el SELECT
        // agenda.setDuracionMinutos(rs.getObject("DURACION_MINUTOS", Integer.class));

        return agenda;
    }

    // En tu AgendaDAO.java, agrega este método:
    public List<Agenda> obtenerPorVeterinario(int idVeterinario) {
        List<Agenda> agendas = new ArrayList<>();
        String sql = "SELECT * FROM agenda WHERE id_veterinario = ? AND estado = 'ACTIVO' ORDER BY fecha, hora_inicio";

        // CORRECCIÓN: USA new conexion().conectar() COMO EN TUS OTROS MÉTODOS
        try (Connection conn = new conexion().conectar(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idVeterinario);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Agenda agenda = new Agenda();
                agenda.setIdAgenda(rs.getInt("id_agenda"));
                agenda.setIdVeterinario(rs.getInt("id_veterinario"));
                agenda.setFecha(rs.getDate("fecha").toLocalDate());
                agenda.setHoraInicio(rs.getTime("hora_inicio").toLocalTime());
                agenda.setHoraFin(rs.getTime("hora_fin").toLocalTime());
                agenda.setIdTipoTurno(rs.getInt("id_tipo_turno"));
                agenda.setObservaciones(rs.getString("observaciones"));
                agenda.setEstado(rs.getString("estado"));
                agendas.add(agenda);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return agendas;
    }
    
}

package cx;

import modelo.Cita;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CitaDAO {

    private conexion conexionDB = new conexion();

    // ===================== CREAR CITA =====================
    public String crear(Cita cita) {
        String sql = "INSERT INTO citas (id_cliente, id_mascota, id_veterinario, id_slot, "
                + "motivo, observaciones, estado, creado_por, creado_en) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, NOW())";

        try (Connection conn = conexionDB.conectar(); PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, cita.getIdCliente());
            ps.setInt(2, cita.getIdMascota());
            ps.setInt(3, cita.getIdVeterinario());
            ps.setInt(4, cita.getIdSlot()); // ← CORREGIDO: era getIdAgenda()
            ps.setString(5, cita.getMotivo());
            ps.setString(6, cita.getObservaciones());
            ps.setString(7, cita.getEstado());
            ps.setInt(8, cita.getCreadoPor());

            int affectedRows = ps.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        int idGenerado = rs.getInt(1);
                        return "OK|Cita reservada exitosamente|" + idGenerado;
                    }
                }
            }
            return "ERROR|No se pudo reservar la cita";

        } catch (SQLException e) {
            e.printStackTrace();
            return "ERROR|Error en la base de datos: " + e.getMessage();
        }
    }

    public String confirmar(int idCita, int modificadoPor) {
        // ✅ Primero verificar el estado actual de la cita
        String sqlVerificar = "SELECT estado FROM citas WHERE id_cita = ?";

        try (Connection conn = conexionDB.conectar()) {

            // 1️⃣ Verificar estado actual
            try (PreparedStatement psVerificar = conn.prepareStatement(sqlVerificar)) {
                psVerificar.setInt(1, idCita);
                ResultSet rs = psVerificar.executeQuery();

                if (!rs.next()) {
                    return "ERROR|La cita no existe";
                }

                String estadoActual = rs.getString("estado");

                // ⭐ VALIDACIÓN: Solo permitir confirmar si está RESERVADA o REPROGRAMADA
                if ("FINALIZADA".equals(estadoActual)) {
                    return "ERROR|Esta cita ya fue atendida y no puede ser modificada";
                }

                if ("CANCELADA".equals(estadoActual)) {
                    return "ERROR|Esta cita fue cancelada y no puede ser confirmada";
                }

                if ("CONFIRMADA".equals(estadoActual)) {
                    return "ERROR|Esta cita ya está confirmada";
                }

                // Si llegamos aquí, el estado es RESERVADA o REPROGRAMADA → OK para confirmar
            }

            // 2️⃣ Actualizar a CONFIRMADA
            String sqlActualizar = "UPDATE citas SET estado = 'CONFIRMADA', "
                    + "modificado_por = ?, modificado_en = NOW() "
                    + "WHERE id_cita = ? AND estado IN ('RESERVADA', 'REPROGRAMADA')";

            try (PreparedStatement psActualizar = conn.prepareStatement(sqlActualizar)) {
                psActualizar.setInt(1, modificadoPor);
                psActualizar.setInt(2, idCita);

                int affectedRows = psActualizar.executeUpdate();

                if (affectedRows > 0) {
                    return "OK|Cita confirmada exitosamente";
                } else {
                    return "ERROR|No se pudo confirmar la cita";
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return "ERROR|Error en la base de datos: " + e.getMessage();
        }
    }

    // ===================== REPROGRAMAR CITA =====================
    public String reprogramar(int idCita, int nuevoSlot, int nuevoVeterinario, // ← nombre corregido
            String motivo, String observaciones, int modificadoPor) {
        String sql = "UPDATE citas SET id_slot = ?, id_veterinario = ?, "
                + "motivo = ?, observaciones = ?, estado = 'REPROGRAMADA', "
                + "modificado_por = ?, modificado_en = NOW() " // ✅ modificado_en
                + "WHERE id_cita = ? AND estado IN ('RESERVADA', 'CONFIRMADA')";

        try (Connection conn = conexionDB.conectar(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, nuevoSlot); // ← era nuevaAgenda
            ps.setInt(2, nuevoVeterinario);
            ps.setString(3, motivo);
            ps.setString(4, observaciones);
            ps.setInt(5, modificadoPor);
            ps.setInt(6, idCita);

            int affectedRows = ps.executeUpdate();

            if (affectedRows > 0) {
                return "OK|Cita reprogramada exitosamente";
            } else {
                return "ERROR|No se pudo reprogramar la cita";
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return "ERROR|Error en la base de datos: " + e.getMessage();
        }
    }

    // ===================== ANULAR CITA =====================
    public String anular(int idCita, String motivo, int anuladoPor) {
        String sql = "UPDATE citas SET estado = 'CANCELADA', "
                + "motivo_anulacion = ?, "
                + "anulado_por = ?, "
                + "anulado_en = NOW() "
                + "WHERE id_cita = ? AND estado IN ('RESERVADA', 'CONFIRMADA', 'REPROGRAMADA')";

        try (Connection conn = conexionDB.conectar(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, motivo);
            ps.setInt(2, anuladoPor);
            ps.setInt(3, idCita);

            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                return "OK|Cita anulada exitosamente";
            } else {
                return "ERROR|No se pudo anular la cita (puede que ya esté cancelada o no esté en estado anulable)";
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return "ERROR|Error en la base de datos: " + e.getMessage();
        }
    }

    // ===================== COMPLETAR CITA =====================
    public String completar(int idCita, String diagnostico, String tratamiento, int completadoPor) {
        String sql = "UPDATE citas SET estado = 'COMPLETADA', "
                + "modificado_por = ?, modificado_en = NOW() "
                + "WHERE id_cita = ? AND estado IN ('CONFIRMADA')";

        try (Connection conn = conexionDB.conectar(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, diagnostico);
            ps.setString(2, tratamiento);
            ps.setInt(3, completadoPor);
            ps.setInt(4, idCita);

            int affectedRows = ps.executeUpdate();

            if (affectedRows > 0) {
                return "OK|Cita completada exitosamente";
            } else {
                return "ERROR|No se pudo completar la cita";
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return "ERROR|Error en la base de datos: " + e.getMessage();
        }
    }

    // ===================== LISTAR TODAS LAS CITAS CON DETALLES =====================
    public List<Cita> obtenerTodasConAgenda() {
        List<Cita> citas = new ArrayList<>();

        String sql
                = "SELECT "
                + "c.ID_CITA, c.ID_CLIENTE, c.ID_MASCOTA, c.ID_VETERINARIO, c.ID_SLOT, "
                + "c.ESTADO, c.MOTIVO, c.OBSERVACIONES, "
                + "cl.NOMBRE AS nombre_cliente, "
                + "m.NOMBRE AS nombre_mascota, "
                + "v.NOMBRE AS nombre_veterinario, "
                + "DATE(s.FECHA_HORA_INICIO) AS fecha, "
                + "DATE_FORMAT(s.FECHA_HORA_INICIO, '%H:%i') AS hora_cita "
                + "FROM citas c "
                + "INNER JOIN agenda_slots s ON c.ID_SLOT = s.ID_SLOT "
                + "LEFT JOIN clientes cl ON c.ID_CLIENTE = cl.ID_CLIENTE "
                + "LEFT JOIN mascotas m ON c.ID_MASCOTA = m.ID_MASCOTA "
                + "LEFT JOIN veterinarios v ON c.ID_VETERINARIO = v.ID_VETERINARIO "
                + "WHERE c.ESTADO != 'CANCELADA' "
                + "AND DATE(s.FECHA_HORA_INICIO) >= CURDATE() "
                + "ORDER BY s.FECHA_HORA_INICIO ASC";

        try (Connection conn = conexionDB.conectar(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Cita cita = new Cita();
                cita.setIdCita(rs.getInt("ID_CITA"));
                cita.setIdCliente(rs.getInt("ID_CLIENTE"));
                cita.setIdMascota(rs.getInt("ID_MASCOTA"));
                cita.setIdVeterinario(rs.getInt("ID_VETERINARIO"));
                cita.setIdSlot(rs.getInt("ID_SLOT"));
                cita.setEstado(rs.getString("ESTADO"));
                cita.setMotivo(rs.getString("MOTIVO"));
                cita.setObservaciones(rs.getString("OBSERVACIONES"));

                cita.setNombreCliente(rs.getString("nombre_cliente"));
                cita.setNombreMascota(rs.getString("nombre_mascota"));
                cita.setNombreVeterinario(rs.getString("nombre_veterinario"));
                cita.setFechaCita(rs.getString("fecha"));
                cita.setHoraCita(rs.getString("hora_cita"));

                citas.add(cita);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return citas;
    }

    // ===================== OBTENER UNA CITA CON DETALLES =====================
    public Cita obtenerPorIdConDetalles(int idCita) {
        String sql = "SELECT "
                + "c.ID_CITA, c.ID_CLIENTE, c.ID_MASCOTA, c.ID_VETERINARIO, c.ID_SLOT, "
                + "c.MOTIVO, c.OBSERVACIONES, c.DIAGNOSTICO, c.TRATAMIENTO, c.ESTADO, "
                + "c.CREADO_POR, c.CREADO_EN, c.MODIFICADO_POR, c.MODIFICADO_EN, "
                + "cl.NOMBRE AS nombre_cliente, "
                + "m.NOMBRE AS nombre_mascota, "
                + "v.NOMBRE AS nombre_veterinario, "
                + "e.NOMBRE AS nombre_especie, "
                + // ← ESPECIE
                "r.NOMBRE AS nombre_raza, "
                + // ← RAZA
                "DATE(s.FECHA_HORA_INICIO) AS fecha, "
                + "DATE_FORMAT(s.FECHA_HORA_INICIO, '%H:%i') AS hora_cita "
                + "FROM citas c "
                + "LEFT JOIN agenda_slots s ON c.ID_SLOT = s.ID_SLOT "
                + "LEFT JOIN clientes cl ON c.ID_CLIENTE = cl.ID_CLIENTE "
                + "LEFT JOIN mascotas m ON c.ID_MASCOTA = m.ID_MASCOTA "
                + "LEFT JOIN veterinarios v ON c.ID_VETERINARIO = v.ID_VETERINARIO "
                + "LEFT JOIN razas r ON m.ID_RAZA = r.ID_RAZA "
                + // ← JOIN razas
                "LEFT JOIN especies e ON r.ID_ESPECIE = e.ID_ESPECIE "
                + // ← JOIN especies
                "WHERE c.ID_CITA = ?";

        try (Connection conn = conexionDB.conectar(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idCita);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Cita cita = new Cita();
                cita.setIdCita(rs.getInt("ID_CITA"));
                cita.setIdCliente(rs.getInt("ID_CLIENTE"));
                cita.setIdMascota(rs.getInt("ID_MASCOTA"));
                cita.setIdVeterinario(rs.getInt("ID_VETERINARIO"));
                cita.setIdSlot(rs.getInt("ID_SLOT"));
                cita.setMotivo(rs.getString("MOTIVO"));
                cita.setObservaciones(rs.getString("OBSERVACIONES"));
                cita.setDiagnostico(rs.getString("DIAGNOSTICO"));
                cita.setTratamiento(rs.getString("TRATAMIENTO"));
                cita.setEstado(rs.getString("ESTADO"));
                cita.setCreadoPor(rs.getInt("CREADO_POR"));
                cita.setFechaCreacion(rs.getTimestamp("CREADO_EN") != null ? rs.getTimestamp("CREADO_EN").toLocalDateTime() : null);
                cita.setModificadoPor(rs.getInt("MODIFICADO_POR"));
                cita.setFechaModificacion(rs.getTimestamp("MODIFICADO_EN") != null ? rs.getTimestamp("MODIFICADO_EN").toLocalDateTime() : null);

                cita.setNombreCliente(rs.getString("nombre_cliente"));
                cita.setNombreMascota(rs.getString("nombre_mascota"));
                cita.setNombreVeterinario(rs.getString("nombre_veterinario"));
                // ✅ ASIGNAR ESPECIE Y RAZA
                cita.setNombreEspecie(rs.getString("nombre_especie")); // Asegúrate de tener este setter en Cita
                cita.setNombreRaza(rs.getString("nombre_raza"));       // Asegúrate de tener este setter en Cita

                cita.setFechaCita(rs.getString("fecha"));
                cita.setHoraCita(rs.getString("hora_cita"));

                return cita;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // ===================== CONTAR CITAS POR SLOT =====================
    public int contarCitasPorSlot(int idSlot) {
        String sql = "SELECT COUNT(*) FROM citas WHERE id_slot = ? AND estado NOT IN ('CANCELADA')";

        try (Connection conn = conexionDB.conectar(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idSlot);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

    // ===================== CONTAR CITAS POR FECHA =====================
    public int contarCitasPorFecha(LocalDate fecha) {
        String sql = "SELECT COUNT(*) FROM citas c "
                + "JOIN agenda_slots s ON c.id_slot = s.id_slot "
                + "WHERE DATE(s.FECHA_HORA_INICIO) = ? AND c.estado NOT IN ('CANCELADA')";

        try (Connection conn = conexionDB.conectar(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDate(1, java.sql.Date.valueOf(fecha));
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

    // ===================== CONTAR CITAS POR FECHA Y ESTADO =====================
    public int contarCitasPorFechaYEstado(LocalDate fecha, String estado) {
        String sql = "SELECT COUNT(*) FROM citas c "
                + "JOIN agenda_slots s ON c.id_slot = s.id_slot "
                + "WHERE DATE(s.FECHA_HORA_INICIO) = ? AND c.estado = ?";

        try (Connection conn = conexionDB.conectar(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDate(1, java.sql.Date.valueOf(fecha));
            ps.setString(2, estado);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

    // ===================== MÉTODO ALIAS PARA COMPATIBILIDAD =====================
    public List<Cita> obtenerTodasConDetalles() {
        return obtenerTodasConAgenda();
    }

    public List<Cita> filtrarCitas(Integer idVeterinario, LocalDate fecha, String estado) {
        // Si no implementas esto, el Servlet usará el fallback manual
        // Implementación sugerida:
        List<Cita> todas = obtenerTodasConAgenda();
        List<Cita> filtradas = new ArrayList<>();

        for (Cita c : todas) {
            boolean ok = true;

            if (idVeterinario != null && c.getIdVeterinario() != idVeterinario) {
                ok = false;
            }
            if (fecha != null && c.getFechaCita() != null) {
                if (!c.getFechaCita().equals(fecha.toString())) {
                    ok = false;
                }
            }
            if (estado != null && !estado.isEmpty() && !estado.equals(c.getEstado())) {
                ok = false;
            }

            if (ok) {
                filtradas.add(c);
            }
        }

        return filtradas;
    }

    public List<Cita> obtenerFiltroAgenda() {
        List<Cita> citas = new ArrayList<>();

        String sql
                = "SELECT "
                + "c.ID_CITA, c.ID_CLIENTE, c.ID_MASCOTA, c.ID_VETERINARIO, c.ID_SLOT, "
                + "c.ESTADO, c.MOTIVO, c.OBSERVACIONES, "
                + "cl.NOMBRE AS nombre_cliente, "
                + "m.NOMBRE AS nombre_mascota, "
                + "v.NOMBRE AS nombre_veterinario, "
                + "DATE(s.FECHA_HORA_INICIO) AS fecha, "
                + "DATE_FORMAT(s.FECHA_HORA_INICIO, '%H:%i') AS hora_cita "
                + "FROM citas c "
                + "INNER JOIN agenda_slots s ON c.ID_SLOT = s.ID_SLOT "
                + "LEFT JOIN clientes cl ON c.ID_CLIENTE = cl.ID_CLIENTE "
                + "LEFT JOIN mascotas m ON c.ID_MASCOTA = m.ID_MASCOTA "
                + "LEFT JOIN veterinarios v ON c.ID_VETERINARIO = v.ID_VETERINARIO "
                + "WHERE c.ESTADO != 'CANCELADA' "
                /* + "AND DATE(s.FECHA_HORA_INICIO) >= CURDATE() "   */
                + "ORDER BY s.FECHA_HORA_INICIO ASC";

        try (Connection conn = conexionDB.conectar(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Cita cita = new Cita();
                cita.setIdCita(rs.getInt("ID_CITA"));
                cita.setIdCliente(rs.getInt("ID_CLIENTE"));
                cita.setIdMascota(rs.getInt("ID_MASCOTA"));
                cita.setIdVeterinario(rs.getInt("ID_VETERINARIO"));
                cita.setIdSlot(rs.getInt("ID_SLOT"));
                cita.setEstado(rs.getString("ESTADO"));
                cita.setMotivo(rs.getString("MOTIVO"));
                cita.setObservaciones(rs.getString("OBSERVACIONES"));

                cita.setNombreCliente(rs.getString("nombre_cliente"));
                cita.setNombreMascota(rs.getString("nombre_mascota"));
                cita.setNombreVeterinario(rs.getString("nombre_veterinario"));
                cita.setFechaCita(rs.getString("fecha"));
                cita.setHoraCita(rs.getString("hora_cita"));

                citas.add(cita);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return citas;
    }
}

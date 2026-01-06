package cx;

import cx.conexion;
import modelo.Recordatorio;
import modelo.RecordatorioConfig;
import modelo.EstadisticaRecordatorio;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecordatorioDAO {

    private conexion cx;

    public RecordatorioDAO() {
        this.cx = new conexion();
    }

    // ========================================
    // 1. GENERAR RECORDATORIOS DIARIOS
    // ========================================
    public Map<String, Object> generarRecordatoriosDiarios() {
        Connection conn = null;
        CallableStatement cstmt = null;
        Map<String, Object> resultado = new HashMap<>();

        try {
            conn = cx.conectar();
            if (conn == null) {
                resultado.put("error", "No se pudo conectar a la base de datos");
                resultado.put("exito", false);
                return resultado;
            }

            cstmt = conn.prepareCall("{CALL sp_generar_recordatorios_diarios()}");
            cstmt.execute();

            resultado.put("exito", true);
            resultado.put("mensaje", "Recordatorios generados exitosamente");

        } catch (SQLException e) {
            System.err.println("Error al generar recordatorios: " + e.getMessage());
            e.printStackTrace();
            resultado.put("error", e.getMessage());
            resultado.put("exito", false);
        } finally {
            cerrarRecursos(conn, cstmt, null);
        }

        return resultado;
    }

    // ========================================
    // 2. PROCESAR ENVÍOS PENDIENTES
    // ========================================
    // ========================================
// 2. PROCESAR ENVÍOS PENDIENTES
// ========================================
    public Map<String, Object> procesarEnviosPendientes() {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Map<String, Object> resultado = new HashMap<>();

        EmailService emailService = new EmailService();

        int totalProcesados = 0;
        int enviados = 0;
        int fallados = 0;

        try {
            conn = cx.conectar();
            if (conn == null) {
                resultado.put("error", "No se pudo conectar a la base de datos");
                resultado.put("exito", false);
                return resultado;
            }

            // ✅ CORRECCIÓN: Usar JOIN con agenda_slots para obtener fecha_hora
            String sql
                    = "SELECT r.*, "
                    + "  s.FECHA_HORA_INICIO as fecha_hora_cita, "
                    + // ← CORRECCIÓN AQUÍ
                    "  c.MOTIVO, "
                    + "  cl.NOMBRE as nombre_cliente, cl.EMAIL, "
                    + "  m.NOMBRE as nombre_mascota, "
                    + "  v.NOMBRE as nombre_veterinario "
                    + "FROM recordatorios r "
                    + "JOIN citas c ON r.ID_CITA = c.ID_CITA "
                    + "JOIN agenda_slots s ON c.ID_SLOT = s.ID_SLOT "
                    + // ← AGREGAR ESTE JOIN
                    "JOIN clientes cl ON c.ID_CLIENTE = cl.ID_CLIENTE "
                    + "JOIN mascotas m ON c.ID_MASCOTA = m.ID_MASCOTA "
                    + "JOIN veterinarios v ON c.ID_VETERINARIO = v.ID_VETERINARIO "
                    + "WHERE r.estado_envio = 'PENDIENTE' "
                    + "  AND r.fecha_envio <= NOW()";

            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                totalProcesados++;

                int idRecordatorio = rs.getInt("ID_RECORDATORIO");
                String email = rs.getString("EMAIL");
                String cliente = rs.getString("nombre_cliente");
                String mascota = rs.getString("nombre_mascota");
                String veterinario = rs.getString("nombre_veterinario");
                Timestamp fechaCita = rs.getTimestamp("fecha_hora_cita");
                String motivo = rs.getString("MOTIVO");

                // Enviar email
                boolean exitoEnvio = false;
                String resultadoEnvio = "";

                if (email != null && !email.trim().isEmpty()) {
                    SimpleDateFormat sdfFecha = new SimpleDateFormat("dd/MM/yyyy");
                    SimpleDateFormat sdfHora = new SimpleDateFormat("HH:mm");

                    exitoEnvio = emailService.enviarRecordatorio(
                            email,
                            cliente,
                            mascota,
                            veterinario,
                            sdfFecha.format(fechaCita),
                            sdfHora.format(fechaCita),
                            motivo
                    );

                    resultadoEnvio = exitoEnvio ? "Enviado correctamente" : "Error al enviar email";

                    if (exitoEnvio) {
                        enviados++;
                    } else {
                        fallados++;
                    }
                } else {
                    fallados++;
                    resultadoEnvio = "Email no disponible";
                }

                // Actualizar estado en BD
                String updateSql
                        = "UPDATE recordatorios "
                        + "SET estado_envio = ?, "
                        + "    intentos_realizados = intentos_realizados + 1, "
                        + "    resultado_envio = ?, "
                        + "    fecha_hora_envio_real = NOW() "
                        + "WHERE ID_RECORDATORIO = ?";

                PreparedStatement updateStmt = conn.prepareStatement(updateSql);
                updateStmt.setString(1, exitoEnvio ? "ENVIADO" : "FALLADO");
                updateStmt.setString(2, resultadoEnvio);
                updateStmt.setInt(3, idRecordatorio);
                updateStmt.executeUpdate();
                updateStmt.close();
            }

            resultado.put("exito", true);
            resultado.put("total_procesados", totalProcesados);
            resultado.put("enviados", enviados);
            resultado.put("fallados", fallados);
            resultado.put("mensaje",
                    "Procesados: " + totalProcesados
                    + " | Enviados: " + enviados
                    + " | Fallados: " + fallados);

        } catch (SQLException e) {
            System.err.println("❌ Error al procesar envíos: " + e.getMessage());
            e.printStackTrace();
            resultado.put("error", e.getMessage());
            resultado.put("exito", false);
            resultado.put("mensaje", "Error: " + e.getMessage());
        } finally {
            cerrarRecursos(conn, pstmt, rs);
        }

        return resultado;
    }

    // ========================================
    // 3. REINTENTAR ENVÍOS FALLADOS
    // ========================================
    public Map<String, Object> reintentarEnviosFallados() {
        Connection conn = null;
        CallableStatement cstmt = null;
        ResultSet rs = null;
        Map<String, Object> resultado = new HashMap<>();

        try {
            conn = cx.conectar();
            if (conn == null) {
                resultado.put("error", "No se pudo conectar a la base de datos");
                resultado.put("exito", false);
                return resultado;
            }

            cstmt = conn.prepareCall("{CALL sp_reintentar_envios_fallados()}");
            rs = cstmt.executeQuery();

            if (rs.next()) {
                resultado.put("reintentos_programados", rs.getInt("reintentos_programados"));
                resultado.put("exito", true);
                resultado.put("mensaje", "Reintentos programados: " + rs.getInt("reintentos_programados"));
            } else {
                resultado.put("exito", false);
                resultado.put("mensaje", "No se pudieron programar reintentos");
            }

        } catch (SQLException e) {
            System.err.println("Error al reintentar envíos: " + e.getMessage());
            e.printStackTrace();
            resultado.put("error", e.getMessage());
            resultado.put("exito", false);
        } finally {
            cerrarRecursos(conn, cstmt, rs);
        }

        return resultado;
    }

    // ========================================
    // 4. OBTENER RECORDATORIOS FILTRADOS
    // ========================================
    public List<Recordatorio> obtenerRecordatoriosFiltrados(String estado, String canal,
            Date fecha, Integer idCita) {
        Connection conn = null;
        CallableStatement cstmt = null;
        ResultSet rs = null;
        List<Recordatorio> recordatorios = new ArrayList<>();

        try {
            conn = cx.conectar();
            if (conn == null) {
                System.err.println("No se pudo conectar a la base de datos");
                return recordatorios;
            }

            cstmt = conn.prepareCall("{CALL sp_obtener_recordatorios_filtrados(?, ?, ?, ?)}");

            // Estado: si es null o vacío, enviar null
            if (estado == null || estado.trim().isEmpty()) {
                cstmt.setNull(1, Types.VARCHAR);
            } else {
                cstmt.setString(1, estado);
            }

            // Canal: si es null o vacío, enviar null
            if (canal == null || canal.trim().isEmpty()) {
                cstmt.setNull(2, Types.VARCHAR);
            } else {
                cstmt.setString(2, canal);
            }

            // Fecha: si es null, enviar null
            if (fecha == null) {
                cstmt.setNull(3, Types.DATE);
            } else {
                cstmt.setDate(3, fecha);
            }

            // ID Cita: si es null, enviar null
            if (idCita == null) {
                cstmt.setNull(4, Types.INTEGER);
            } else {
                cstmt.setInt(4, idCita);
            }

            rs = cstmt.executeQuery();

            while (rs.next()) {
                Recordatorio rec = new Recordatorio();
                rec.setIdRecordatorio(rs.getInt("ID_RECORDATORIO"));
                rec.setIdCita(rs.getInt("ID_CITA"));
                rec.setCanal(rs.getString("CANAL"));
                rec.setFechaEnvio(rs.getTimestamp("FECHA_ENVIO"));
                rec.setEstadoEnvio(rs.getString("estado_envio"));
                rec.setRespuestaCliente(rs.getString("RESPUESTA_CLIENTE"));
                rec.setIntento(rs.getInt("INTENTO"));
                rec.setIntentosRealizados(rs.getInt("intentos_realizados"));
                rec.setMensajeEnviado(rs.getString("mensaje_enviado"));
                rec.setResultadoEnvio(rs.getString("resultado_envio"));
                rec.setFechaHoraEnvioReal(rs.getTimestamp("fecha_hora_envio_real"));
                rec.setFechaProximoIntento(rs.getTimestamp("fecha_proximo_intento"));
                rec.setErrorDetalle(rs.getString("error_detalle"));
                rec.setObservaciones(rs.getString("OBSERVACIONES"));

                // Datos de la cita
                rec.setFechaHoraCita(rs.getTimestamp("fecha_hora_cita"));
                rec.setEstadoCita(rs.getString("estado_cita"));
                rec.setMotivoCita(rs.getString("motivo_cita"));

                // Datos del cliente
                rec.setNombreCliente(rs.getString("nombre_cliente"));
                rec.setTelefonoCliente(rs.getString("TELEFONO"));
                rec.setEmailCliente(rs.getString("EMAIL"));

                // Datos de la mascota
                rec.setNombreMascota(rs.getString("nombre_mascota"));
                rec.setEspecie(rs.getString("especie"));

                // Datos del veterinario
                rec.setNombreVeterinario(rs.getString("nombre_veterinario"));

                recordatorios.add(rec);
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener recordatorios: " + e.getMessage());
            e.printStackTrace();
        } finally {
            cerrarRecursos(conn, cstmt, rs);
        }

        return recordatorios;
    }

    // ========================================
    // 5. OBTENER ESTADÍSTICAS
    // ========================================
    public EstadisticaRecordatorio obtenerEstadisticasRecordatorios(Date fecha) {
        Connection conn = null;
        CallableStatement cstmt = null;
        ResultSet rs = null;
        EstadisticaRecordatorio stats = null;

        try {
            conn = cx.conectar();
            if (conn == null) {
                System.err.println("No se pudo conectar a la base de datos");
                return null;
            }

            cstmt = conn.prepareCall("{CALL sp_obtener_estadisticas_recordatorios(?)}");
            cstmt.setDate(1, fecha);
            rs = cstmt.executeQuery();

            if (rs.next()) {
                stats = new EstadisticaRecordatorio();
                stats.setFecha(fecha);

                // ✅ Usar nombres de columnas correctos
                stats.setTotalRecordatorios(rs.getInt("total"));
                stats.setEnviados(rs.getInt("enviados"));
                stats.setFallados(rs.getInt("fallados"));
                stats.setPendientes(rs.getInt("pendientes"));
                stats.setReintentos(rs.getInt("reintentos"));
                stats.setPromedioIntentos(rs.getDouble("avg_intentos"));

                // Los porcentajes se calculan automáticamente en el setter
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener estadísticas: " + e.getMessage());
            e.printStackTrace();
        } finally {
            cerrarRecursos(conn, cstmt, rs);
        }

        return stats;
    }

    // ========================================
    // 6. OBTENER CONFIGURACIÓN
    // ========================================
    public RecordatorioConfig obtenerConfiguracion() {
        Connection conn = null;
        CallableStatement cstmt = null;
        ResultSet rs = null;
        RecordatorioConfig config = null;

        System.out.println("🔍 DAO: Iniciando obtenerConfiguracion()");

        try {
            conn = cx.conectar();
            if (conn == null) {
                System.err.println("❌ DAO: No se pudo conectar a la base de datos");
                return null;
            }
            System.out.println("✅ DAO: Conexión establecida");

            cstmt = conn.prepareCall("{CALL sp_obtener_configuracion_recordatorios()}");
            System.out.println("📋 DAO: Llamando procedimiento sp_obtener_configuracion_recordatorios");

            rs = cstmt.executeQuery();

            if (rs.next()) {
                System.out.println("✅ DAO: Configuración encontrada en BD");
                config = new RecordatorioConfig();
                config.setIdConfig(rs.getInt("id_config"));
                config.setHorasAntes(rs.getInt("horas_antes"));
                config.setHoraEnvio(rs.getTime("hora_envio"));
                config.setCanalPrincipal(rs.getString("canal_principal"));
                config.setPlantillaMensaje(rs.getString("plantilla_mensaje"));
                config.setMaxReintentos(rs.getInt("max_reintentos"));
                config.setHabilitado(rs.getBoolean("habilitado"));

                config.setModificadoPor(rs.getInt("actualizado_por"));
                config.setModificadoEn(rs.getTimestamp("actualizado_en"));

                System.out.println("📊 Configuración cargada: " + config);
            } else {
                System.out.println("⚠ DAO: NO hay configuración en la BD");
                // Crear configuración por defecto si no existe
                config = new RecordatorioConfig();
                config.setIdConfig(1);
                config.setHorasAntes(24);
                config.setHoraEnvio(Time.valueOf("09:00:00"));
                config.setCanalPrincipal("WHATSAPP");
                config.setPlantillaMensaje("Recordatorio de cita para {MASCOTA}");
                config.setMaxReintentos(3);
                config.setHabilitado(true);
            }

        } catch (SQLException e) {
            System.err.println("❌ DAO: Error al obtener configuración: " + e.getMessage());
            e.printStackTrace();
        } finally {
            cerrarRecursos(conn, cstmt, rs);
        }

        return config;
    }

    // ========================================
    // 7. ACTUALIZAR CONFIGURACIÓN
    // ========================================
    public boolean actualizarConfiguracion(RecordatorioConfig config) {
        Connection conn = null;
        CallableStatement cstmt = null;

        try {
            conn = cx.conectar();
            if (conn == null) {
                System.err.println("No se pudo conectar a la base de datos");
                return false;
            }

            cstmt = conn.prepareCall("{CALL sp_actualizar_configuracion_recordatorios(?, ?, ?, ?, ?, ?, ?)}");
            cstmt.setInt(1, config.getHorasAntes());
            cstmt.setTime(2, config.getHoraEnvio());
            cstmt.setString(3, config.getCanalPrincipal());
            cstmt.setString(4, config.getPlantillaMensaje());
            cstmt.setInt(5, config.getMaxReintentos());
            cstmt.setBoolean(6, config.isHabilitado());
            cstmt.setInt(7, config.getModificadoPor());

            cstmt.execute();
            return true;

        } catch (SQLException e) {
            System.err.println("Error al actualizar configuración: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            cerrarRecursos(conn, cstmt, null);
        }
    }

    // ========================================
    // 8. OBTENER ESTADÍSTICAS SEMANALES
    // ========================================
    public List<EstadisticaRecordatorio> obtenerEstadisticasSemanales() {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<EstadisticaRecordatorio> stats = new ArrayList<>();

        try {
            conn = cx.conectar();
            if (conn == null) {
                System.err.println("No se pudo conectar a la base de datos");
                return stats;
            }

            // ✅ Usar la vista con los nombres correctos
            String sql = "SELECT * FROM vw_estadisticas_recordatorios_diarias "
                    + "ORDER BY fecha DESC LIMIT 7";

            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                EstadisticaRecordatorio stat = new EstadisticaRecordatorio();
                stat.setFecha(rs.getDate("fecha"));
                stat.setTotalRecordatorios(rs.getInt("total"));
                stat.setEnviados(rs.getInt("enviados"));
                stat.setFallados(rs.getInt("fallados"));
                stat.setPendientes(rs.getInt("pendientes"));
                stat.setReintentos(rs.getInt("reintentos"));
                stat.setPromedioIntentos(rs.getDouble("promedio_intentos"));

                stats.add(stat);
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener estadísticas semanales: " + e.getMessage());
            e.printStackTrace();
        } finally {
            cerrarRecursos(conn, pstmt, rs);
        }

        return stats;
    }

    // ========================================
    // 9. OBTENER PENDIENTES DE HOY
    // ========================================
    public List<Recordatorio> obtenerPendientesHoy() {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        List<Recordatorio> pendientes = new ArrayList<>();

        try {
            conn = cx.conectar();
            if (conn == null) {
                System.err.println("No se pudo conectar a la base de datos");
                return pendientes;
            }

            String sql = "SELECT * FROM vw_recordatorios_pendientes_hoy ORDER BY fecha_hora_cita";
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                Recordatorio rec = new Recordatorio();
                rec.setIdRecordatorio(rs.getInt("ID_RECORDATORIO"));
                rec.setIdCita(rs.getInt("ID_CITA"));
                rec.setCanal(rs.getString("CANAL"));
                rec.setEstadoEnvio(rs.getString("estado_envio"));
                rec.setIntentosRealizados(rs.getInt("intentos_realizados"));
                rec.setNombreCliente(rs.getString("cliente"));
                rec.setTelefonoCliente(rs.getString("TELEFONO"));
                rec.setEmailCliente(rs.getString("EMAIL"));
                rec.setNombreMascota(rs.getString("mascota"));
                rec.setNombreVeterinario(rs.getString("veterinario"));
                rec.setFechaHoraCita(rs.getTimestamp("fecha_hora_cita"));
                rec.setMensajeEnviado(rs.getString("mensaje_enviado"));
                rec.setResultadoEnvio(rs.getString("resultado_envio"));

                pendientes.add(rec);
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener pendientes de hoy: " + e.getMessage());
            e.printStackTrace();
        } finally {
            cerrarRecursos(conn, pstmt, rs);
        }

        return pendientes;
    }

    // ========================================
    // 10. OBTENER RECORDATORIOS PENDIENTES HOY (ALIAS para el Servlet)
    // ========================================
    public List<Recordatorio> obtenerRecordatoriosPendientesHoy() {
        return obtenerPendientesHoy();
    }

    // ========================================
    // 11. OBTENER ESTADÍSTICAS DIARIAS (ALIAS para el Servlet)
    // ========================================
    public List<EstadisticaRecordatorio> obtenerEstadisticasDiarias(int dias) {
        // Por ahora, devolver siempre los últimos 7 días
        return obtenerEstadisticasSemanales();
    }

    // ========================================
    // MÉTODO AUXILIAR: CERRAR RECURSOS
    // ========================================
    private void cerrarRecursos(Connection conn, Statement stmt, ResultSet rs) {
        try {
            if (rs != null) {
                rs.close();
            }
            if (stmt != null) {
                stmt.close();
            }
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
            System.err.println("Error al cerrar recursos: " + e.getMessage());
        }
    }
}

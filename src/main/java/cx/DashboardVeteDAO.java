// cx/DashboardVeteDAO.java
package cx;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DashboardVeteDAO {

    private conexion conexionDB = new conexion();

    // === OBTENER CANTIDAD DE CITAS HOY ===
    public int obtenerCitasHoy(int idVeterinario) {
        int cantidad = 0;
        String sql = "SELECT COUNT(*) AS total FROM citas ci "
                + "JOIN agenda_slots s ON ci.ID_SLOT = s.ID_SLOT "
                + "JOIN veterinarios v ON ci.ID_VETERINARIO = v.ID_VETERINARIO "
                + "WHERE v.ID_USUARIO = ? "
                + "AND DATE(s.FECHA_HORA_INICIO) = CURDATE() "
                + "AND ci.ESTADO = 'CONFIRMADA'";

        try (Connection conn = conexionDB.conectar(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idVeterinario);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                cantidad = rs.getInt("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cantidad;
    }

    // === OBTENER CANTIDAD DE CONSULTAS EN CURSO ===
    public int obtenerConsultasEnCurso(int idVeterinario) {
        int cantidad = 0;
        String sql = "SELECT COUNT(*) AS total FROM consultas c "
                + "JOIN veterinarios v ON c.ID_VETERINARIO = v.ID_VETERINARIO "
                + "WHERE v.ID_USUARIO = ? AND c.ESTADO = 'EN_CURSO'";

        try (Connection conn = conexionDB.conectar(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idVeterinario);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                cantidad = rs.getInt("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cantidad;
    }

    // === OBTENER CANTIDAD DE CONSULTAS FINALIZADAS HOY ===
    public int obtenerConsultasFinalizadasHoy(int idUsuario) {
        int cantidad = 0;
        String sql = "SELECT COUNT(*) AS total "
                + "FROM consultas c "
                + "JOIN veterinarios v ON c.ID_VETERINARIO = v.ID_VETERINARIO "
                + "WHERE v.ID_USUARIO = ? "
                + "AND DATE(c.FECHA_HORA_INICIO) = CURDATE() "
                + "AND c.ESTADO = 'FINALIZADA'";

        try (Connection conn = conexionDB.conectar(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                cantidad = rs.getInt("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cantidad;
    }

    // === OBTENER CANTIDAD DE PACIENTES ATENDIDOS ESTE MES ===
    public int obtenerPacientesAtendidosMes(int idVeterinario) {
        int cantidad = 0;
        String sql = "SELECT COUNT(DISTINCT c.ID_MASCOTA) AS total FROM consultas c "
                + "JOIN veterinarios v ON c.ID_VETERINARIO = v.ID_VETERINARIO "
                + "WHERE v.ID_USUARIO = ? "
                + "AND MONTH(c.FECHA_HORA_INICIO) = MONTH(CURDATE()) "
                + "AND YEAR(c.FECHA_HORA_INICIO) = YEAR(CURDATE()) "
                + "AND c.ESTADO = 'FINALIZADA'";

        try (Connection conn = conexionDB.conectar(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idVeterinario);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                cantidad = rs.getInt("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cantidad;
    }

    // === OBTENER AGENDA DE CITAS HOY ===
    public List<Map<String, Object>> obtenerAgendaHoy(int idVeterinario) {
        List<Map<String, Object>> citas = new ArrayList<>();
        String sql = "SELECT ci.ID_CITA, s.FECHA_HORA_INICIO, ci.MOTIVO, ci.ESTADO, "
                + "m.NOMBRE AS mascota, r.NOMBRE AS raza, cl.NOMBRE AS cliente "
                + "FROM citas ci "
                + "JOIN agenda_slots s ON ci.ID_SLOT = s.ID_SLOT "
                + "JOIN mascotas m ON ci.ID_MASCOTA = m.ID_MASCOTA "
                + "LEFT JOIN razas r ON m.ID_RAZA = r.ID_RAZA "
                + "JOIN clientes cl ON m.ID_CLIENTE = cl.ID_CLIENTE "
                + "JOIN veterinarios v ON ci.ID_VETERINARIO = v.ID_VETERINARIO "
                + "WHERE v.ID_USUARIO = ? "
                + "AND DATE(s.FECHA_HORA_INICIO) = CURDATE() "
                + "AND ci.ESTADO = 'CONFIRMADA' "
                + "ORDER BY s.FECHA_HORA_INICIO";

        try (Connection conn = conexionDB.conectar(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idVeterinario);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Map<String, Object> cita = new HashMap<>();
                cita.put("idCita", rs.getInt("ID_CITA"));
                cita.put("fechaHora", rs.getTimestamp("FECHA_HORA_INICIO"));
                cita.put("motivo", rs.getString("MOTIVO"));
                cita.put("estado", rs.getString("ESTADO"));
                cita.put("mascota", rs.getString("mascota"));
                cita.put("raza", rs.getString("raza"));
                cita.put("cliente", rs.getString("cliente"));
                citas.add(cita);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return citas;
    }

    // === OBTENER CONSULTAS POR DÍA (ÚLTIMA SEMANA) ===
    public List<Map<String, Object>> obtenerConsultasPorDia(int idVeterinario) {
        List<Map<String, Object>> datos = new ArrayList<>();
        String sql = "SELECT DATE(FECHA_HORA_INICIO) AS fecha, COUNT(*) AS total FROM consultas WHERE ID_VETERINARIO = ? AND FECHA_HORA_INICIO >= DATE_SUB(CURDATE(), INTERVAL 7 DAY) AND ESTADO = 'FINALIZADA' GROUP BY DATE(FECHA_HORA_INICIO) ORDER BY fecha";

        try (Connection conn = conexionDB.conectar(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idVeterinario);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Map<String, Object> dato = new HashMap<>();
                dato.put("fecha", rs.getDate("fecha"));
                dato.put("total", rs.getInt("total"));
                datos.add(dato);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return datos;
    }

    // === OBTENER TOP 5 RAZAS MÁS ATENDIDAS ===
    public List<Map<String, Object>> obtenerTopRazas(int idVeterinario) {
        List<Map<String, Object>> razas = new ArrayList<>();
        String sql = "SELECT r.NOMBRE AS raza, COUNT(DISTINCT c.ID_MASCOTA) AS total FROM consultas c JOIN mascotas m ON c.ID_MASCOTA = m.ID_MASCOTA LEFT JOIN razas r ON m.ID_RAZA = r.ID_RAZA WHERE c.ID_VETERINARIO = ? AND c.ESTADO = 'FINALIZADA' AND MONTH(c.FECHA_HORA_INICIO) = MONTH(CURDATE()) AND YEAR(c.FECHA_HORA_INICIO) = YEAR(CURDATE()) GROUP BY r.ID_RAZA, r.NOMBRE ORDER BY total DESC LIMIT 5";

        try (Connection conn = conexionDB.conectar(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idVeterinario);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Map<String, Object> raza = new HashMap<>();
                String nombreRaza = rs.getString("raza");
                raza.put("raza", nombreRaza != null ? nombreRaza : "Sin especificar");
                raza.put("total", rs.getInt("total"));
                razas.add(raza);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return razas;
    }

    // === OBTENER ÚLTIMAS 5 CONSULTAS FINALIZADAS ===
    public List<Map<String, Object>> obtenerUltimasConsultas(int idVeterinario) {
        List<Map<String, Object>> consultas = new ArrayList<>();
        String sql = "SELECT c.ID_CONSULTA, c.FECHA_HORA_INICIO, c.MOTIVO, m.NOMBRE AS mascota, cl.NOMBRE AS cliente FROM consultas c JOIN mascotas m ON c.ID_MASCOTA = m.ID_MASCOTA JOIN clientes cl ON m.ID_CLIENTE = cl.ID_CLIENTE WHERE c.ID_VETERINARIO = ? AND c.ESTADO = 'FINALIZADA' ORDER BY c.FECHA_HORA_INICIO DESC LIMIT 5";

        try (Connection conn = conexionDB.conectar(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idVeterinario);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Map<String, Object> consulta = new HashMap<>();
                consulta.put("idConsulta", rs.getInt("ID_CONSULTA"));
                consulta.put("fechaHora", rs.getTimestamp("FECHA_HORA_INICIO"));
                consulta.put("motivo", rs.getString("MOTIVO"));
                consulta.put("mascota", rs.getString("mascota"));
                consulta.put("cliente", rs.getString("cliente"));
                consultas.add(consulta);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return consultas;
    }

    // === VERIFICAR SI EXISTE CONSULTA PARA UNA CITA ===
    public boolean tieneConsultaIniciada(int idCita) {
        String sql = "SELECT COUNT(*) AS total FROM consultas WHERE ID_CITA = ?";
        try (Connection conn = conexionDB.conectar(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idCita);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("total") > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // === OBTENER ESTADO DE CONSULTA POR CITA ===
    public String obtenerEstadoConsulta(int idCita) {
        String sql = "SELECT ESTADO FROM consultas WHERE ID_CITA = ?";
        try (Connection conn = conexionDB.conectar(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idCita);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("ESTADO");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}

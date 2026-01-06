// cx/TratamientoDAO.java
package cx;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TratamientoDAO {

    private conexion conexionDB = new conexion();

    // === BUSCAR MASCOTAS ===
    public List<Map<String, Object>> buscarMascotas(String texto) {
        List<Map<String, Object>> mascotas = new ArrayList<>();
        String sql = "SELECT DISTINCT m.ID_MASCOTA, m.NOMBRE AS mascota, e.NOMBRE AS especie, r.NOMBRE AS raza, cl.NOMBRE AS cliente, cl.TELEFONO FROM mascotas m JOIN clientes cl ON m.ID_CLIENTE = cl.ID_CLIENTE LEFT JOIN razas r ON m.ID_RAZA = r.ID_RAZA LEFT JOIN especies e ON r.ID_ESPECIE = e.ID_ESPECIE WHERE (m.NOMBRE LIKE ? OR cl.NOMBRE LIKE ?) ORDER BY m.NOMBRE LIMIT 20";

        try (Connection conn = conexionDB.conectar(); PreparedStatement ps = conn.prepareStatement(sql)) {
            String patron = "%" + texto + "%";
            ps.setString(1, patron);
            ps.setString(2, patron);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Map<String, Object> mascota = new HashMap<>();
                mascota.put("idMascota", rs.getInt("ID_MASCOTA"));
                mascota.put("nombreMascota", rs.getString("mascota"));
                mascota.put("especie", rs.getString("especie"));
                mascota.put("raza", rs.getString("raza"));
                mascota.put("nombreCliente", rs.getString("cliente"));
                mascota.put("telefono", rs.getString("TELEFONO"));
                mascotas.add(mascota);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return mascotas;
    }

    // === LISTAR TRATAMIENTOS DE UNA MASCOTA ===
    public List<Map<String, Object>> listarTratamientos(int idMascota) {
        List<Map<String, Object>> tratamientos = new ArrayList<>();
        String sql = "SELECT t.ID_TRATAMIENTO, t.fecha_inicio, t.fecha_fin, t.PLAN_TERAPEUTICO, t.EVOLUCION, t.FECHA_CONTROL, t.OBSERVACIONES, t.ESTADO, t.FECHA_REGISTRO, v.NOMBRE AS veterinario, c.FECHA_HORA_INICIO AS fecha_consulta FROM tratamientos t LEFT JOIN veterinarios v ON t.ID_VETERINARIO = v.ID_VETERINARIO LEFT JOIN consultas c ON t.ID_CONSULTA = c.ID_CONSULTA WHERE t.ID_MASCOTA = ? ORDER BY t.FECHA_REGISTRO DESC";

        try (Connection conn = conexionDB.conectar(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idMascota);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Map<String, Object> tratamiento = new HashMap<>();
                tratamiento.put("idTratamiento", rs.getInt("ID_TRATAMIENTO"));
                tratamiento.put("fechaInicio", rs.getDate("fecha_inicio"));
                tratamiento.put("fechaFin", rs.getDate("fecha_fin"));
                tratamiento.put("planTerapeutico", rs.getString("PLAN_TERAPEUTICO"));
                tratamiento.put("evolucion", rs.getString("EVOLUCION"));
                tratamiento.put("fechaControl", rs.getDate("FECHA_CONTROL"));
                tratamiento.put("observaciones", rs.getString("OBSERVACIONES"));
                tratamiento.put("estado", rs.getString("ESTADO"));
                tratamiento.put("fechaRegistro", rs.getTimestamp("FECHA_REGISTRO"));
                tratamiento.put("veterinario", rs.getString("veterinario"));
                tratamiento.put("fechaConsulta", rs.getTimestamp("fecha_consulta"));
                tratamientos.add(tratamiento);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tratamientos;
    }

    // === OBTENER CONSULTAS FINALIZADAS ===
    public List<Map<String, Object>> obtenerConsultasFinalizadas(int idMascota) {
        List<Map<String, Object>> consultas = new ArrayList<>();
        String sql = "SELECT ID_CONSULTA, FECHA_HORA_INICIO, MOTIVO FROM consultas WHERE ID_MASCOTA = ? AND ESTADO = 'FINALIZADA' ORDER BY FECHA_HORA_INICIO DESC";

        try (Connection conn = conexionDB.conectar(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idMascota);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Map<String, Object> consulta = new HashMap<>();
                consulta.put("idConsulta", rs.getInt("ID_CONSULTA"));
                consulta.put("fechaHora", rs.getTimestamp("FECHA_HORA_INICIO"));
                consulta.put("motivo", rs.getString("MOTIVO"));
                consultas.add(consulta);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return consultas;
    }

    // === REGISTRAR TRATAMIENTO ===
    public boolean registrarTratamiento(int idConsulta, int idMascota, int idVeterinario, String fechaInicio, String fechaFin, String planTerapeutico, String evolucion, String fechaControl, String observaciones) {
        String sql = "INSERT INTO tratamientos (ID_CONSULTA, ID_MASCOTA, ID_VETERINARIO, fecha_inicio, fecha_fin, PLAN_TERAPEUTICO, EVOLUCION, FECHA_CONTROL, OBSERVACIONES, ESTADO) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, 'ACTIVO')";

        try (Connection conn = conexionDB.conectar(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idConsulta);
            ps.setInt(2, idMascota);
            ps.setInt(3, idVeterinario);
            ps.setString(4, fechaInicio.isEmpty() ? null : fechaInicio);
            ps.setString(5, fechaFin.isEmpty() ? null : fechaFin);
            ps.setString(6, planTerapeutico);
            ps.setString(7, evolucion);
            ps.setString(8, fechaControl.isEmpty() ? null : fechaControl);
            ps.setString(9, observaciones);

            int filasAfectadas = ps.executeUpdate();
            return filasAfectadas > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // === ACTUALIZAR TRATAMIENTO ===
    public boolean actualizarTratamiento(int idTratamiento, String fechaInicio, String fechaFin, String planTerapeutico, String evolucion, String fechaControl, String observaciones) {
        String sql = "UPDATE tratamientos SET fecha_inicio = ?, fecha_fin = ?, PLAN_TERAPEUTICO = ?, EVOLUCION = ?, FECHA_CONTROL = ?, OBSERVACIONES = ?, VERSION = VERSION + 1 WHERE ID_TRATAMIENTO = ? AND ESTADO = 'ACTIVO'";

        try (Connection conn = conexionDB.conectar(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, fechaInicio.isEmpty() ? null : fechaInicio);
            ps.setString(2, fechaFin.isEmpty() ? null : fechaFin);
            ps.setString(3, planTerapeutico);
            ps.setString(4, evolucion);
            ps.setString(5, fechaControl.isEmpty() ? null : fechaControl);
            ps.setString(6, observaciones);
            ps.setInt(7, idTratamiento);

            int filasAfectadas = ps.executeUpdate();
            return filasAfectadas > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // === ANULAR TRATAMIENTO ===
    public boolean anularTratamiento(int idTratamiento, String motivo, int idUsuario) {
        String sql = "UPDATE tratamientos SET ESTADO = 'ANULADO', MOTIVO_ANULACION = ?, FECHA_ANULACION = NOW(), ID_USUARIO_ANULACION = ? WHERE ID_TRATAMIENTO = ? AND ESTADO = 'ACTIVO'";

        try (Connection conn = conexionDB.conectar(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, motivo);
            ps.setInt(2, idUsuario);
            ps.setInt(3, idTratamiento);

            int filasAfectadas = ps.executeUpdate();
            return filasAfectadas > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // === OBTENER DETALLES DE TRATAMIENTO ===
    public Map<String, Object> obtenerDetalles(int idTratamiento) {
        Map<String, Object> detalles = new HashMap<>();
        String sql = "SELECT fecha_inicio, fecha_fin, PLAN_TERAPEUTICO, EVOLUCION, FECHA_CONTROL, OBSERVACIONES FROM tratamientos WHERE ID_TRATAMIENTO = ?";

        try (Connection conn = conexionDB.conectar(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idTratamiento);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                detalles.put("fechaInicio", rs.getDate("fecha_inicio"));
                detalles.put("fechaFin", rs.getDate("fecha_fin"));
                detalles.put("planTerapeutico", rs.getString("PLAN_TERAPEUTICO"));
                detalles.put("evolucion", rs.getString("EVOLUCION"));
                detalles.put("fechaControl", rs.getDate("FECHA_CONTROL"));
                detalles.put("observaciones", rs.getString("OBSERVACIONES"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return detalles;
    }
}

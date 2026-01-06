package cx;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecetaDAO {

    private conexion conexionDB = new conexion();

    // === BUSCAR MASCOTAS CON CONSULTAS ===
    public List<Map<String, Object>> buscarMascotasConConsultas(String texto) {
        List<Map<String, Object>> mascotas = new ArrayList<>();
        String sql = "SELECT DISTINCT "
                + "    m.ID_MASCOTA, "
                + "    m.NOMBRE AS mascota, "
                + "    e.NOMBRE AS especie, "
                + "    r.NOMBRE AS raza, "
                + "    cl.NOMBRE AS cliente, "
                + "    cl.TELEFONO, "
                + "    COUNT(DISTINCT c.ID_CONSULTA) AS total_consultas "
                + "FROM mascotas m "
                + "JOIN clientes cl ON m.ID_CLIENTE = cl.ID_CLIENTE "
                + "LEFT JOIN consultas c ON m.ID_MASCOTA = c.ID_MASCOTA "
                + "LEFT JOIN razas r ON m.ID_RAZA = r.ID_RAZA "
                + "LEFT JOIN especies e ON r.ID_ESPECIE = e.ID_ESPECIE "
                + "WHERE (m.NOMBRE LIKE ? OR cl.NOMBRE LIKE ?) "
                + "  AND c.ESTADO = 'FINALIZADA' "
                + "GROUP BY m.ID_MASCOTA "
                + "HAVING total_consultas > 0 "
                + "ORDER BY m.NOMBRE "
                + "LIMIT 20";

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
                mascota.put("totalConsultas", rs.getInt("total_consultas"));
                mascotas.add(mascota);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return mascotas;
    }

    // === OBTENER CONSULTAS FINALIZADAS DE UNA MASCOTA ===
    public List<Map<String, Object>> obtenerConsultasFinalizadas(int idMascota) {
        List<Map<String, Object>> consultas = new ArrayList<>();
        String sql = "SELECT "
                + "    c.ID_CONSULTA, "
                + "    c.FECHA_HORA_INICIO, "
                + "    c.MOTIVO, "
                + "    v.NOMBRE AS veterinario, "
                + "    COUNT(r.ID_RECETA) AS total_recetas "
                + "FROM consultas c "
                + "JOIN veterinarios v ON c.ID_VETERINARIO = v.ID_VETERINARIO "
                + "LEFT JOIN recetas r ON c.ID_CONSULTA = r.ID_CONSULTA AND r.ESTADO = 'ACTIVO' "
                + "WHERE c.ID_MASCOTA = ? AND c.ESTADO = 'FINALIZADA' "
                + "GROUP BY c.ID_CONSULTA "
                + "ORDER BY c.FECHA_HORA_INICIO DESC";

        try (Connection conn = conexionDB.conectar(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idMascota);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Map<String, Object> consulta = new HashMap<>();
                consulta.put("idConsulta", rs.getInt("ID_CONSULTA"));
                consulta.put("fechaInicio", rs.getTimestamp("FECHA_HORA_INICIO"));
                consulta.put("motivo", rs.getString("MOTIVO"));
                consulta.put("veterinario", rs.getString("veterinario"));
                consulta.put("totalRecetas", rs.getInt("total_recetas"));
                consultas.add(consulta);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return consultas;
    }

    // === LISTAR RECETAS DE UNA MASCOTA ===
    public List<Map<String, Object>> listarRecetas(int idMascota) {
        List<Map<String, Object>> recetas = new ArrayList<>();
        String sql = "SELECT "
                + "    r.ID_RECETA, "
                + "    r.FECHA_REGISTRO, "
                + "    r.MEDICAMENTO, "
                + "    r.DOSIS, "
                + "    r.FRECUENCIA, "
                + "    r.DURACION, "
                + "    r.ESTADO, "
                + "    c.FECHA_HORA_INICIO AS fecha_consulta, "
                + "    c.MOTIVO AS motivo_consulta, "
                + "    v.NOMBRE AS veterinario "
                + "FROM recetas r "
                + "JOIN consultas c ON r.ID_CONSULTA = c.ID_CONSULTA "
                + "JOIN veterinarios v ON r.ID_VETERINARIO = v.ID_VETERINARIO "
                + "WHERE r.ID_MASCOTA = ? "
                + "ORDER BY r.FECHA_REGISTRO DESC";

        try (Connection conn = conexionDB.conectar(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idMascota);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Map<String, Object> receta = new HashMap<>();
                receta.put("idReceta", rs.getInt("ID_RECETA"));
                receta.put("fechaRegistro", rs.getTimestamp("FECHA_REGISTRO"));
                receta.put("medicamento", rs.getString("MEDICAMENTO"));
                receta.put("dosis", rs.getString("DOSIS"));
                receta.put("frecuencia", rs.getString("FRECUENCIA"));
                receta.put("duracion", rs.getString("DURACION"));
                receta.put("estado", rs.getString("ESTADO"));
                receta.put("fechaConsulta", rs.getTimestamp("fecha_consulta"));
                receta.put("motivoConsulta", rs.getString("motivo_consulta"));
                receta.put("veterinario", rs.getString("veterinario"));
                recetas.add(receta);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return recetas;
    }

    // === REGISTRAR RECETA ===
    public boolean registrarReceta(int idConsulta, int idMascota, int idVeterinario,
            String medicamento, String dosis, String frecuencia,
            String duracion, String indicaciones, String observaciones) {
        String sql = "INSERT INTO recetas "
                + "(ID_CONSULTA, ID_MASCOTA, ID_VETERINARIO, MEDICAMENTO, DOSIS, FRECUENCIA, "
                + " DURACION, INDICACIONES, OBSERVACIONES) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = conexionDB.conectar(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idConsulta);
            ps.setInt(2, idMascota);
            ps.setInt(3, idVeterinario);
            ps.setString(4, medicamento);
            ps.setString(5, dosis);
            ps.setString(6, frecuencia);
            ps.setString(7, duracion);
            ps.setString(8, indicaciones);
            ps.setString(9, observaciones);

            int filasAfectadas = ps.executeUpdate();
            return filasAfectadas > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // === EDITAR RECETA ===
    public boolean editarReceta(int idReceta, String medicamento, String dosis, String frecuencia,
            String duracion, String indicaciones, String observaciones) {
        String sql = "UPDATE recetas SET "
                + "MEDICAMENTO = ?, "
                + "DOSIS = ?, "
                + "FRECUENCIA = ?, "
                + "DURACION = ?, "
                + "INDICACIONES = ?, "
                + "OBSERVACIONES = ?, "
                + "VERSION = VERSION + 1 "
                + "WHERE ID_RECETA = ? AND ESTADO = 'ACTIVO'";

        try (Connection conn = conexionDB.conectar(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, medicamento);
            ps.setString(2, dosis);
            ps.setString(3, frecuencia);
            ps.setString(4, duracion);
            ps.setString(5, indicaciones);
            ps.setString(6, observaciones);
            ps.setInt(7, idReceta);

            int filasAfectadas = ps.executeUpdate();
            return filasAfectadas > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // === ANULAR RECETA ===
    public boolean anularReceta(int idReceta, String motivo, int idUsuario) {
        String sql = "UPDATE recetas SET "
                + "ESTADO = 'ANULADO', "
                + "MOTIVO_ANULACION = ?, "
                + "FECHA_ANULACION = NOW(), "
                + "ID_USUARIO_ANULACION = ? "
                + "WHERE ID_RECETA = ? AND ESTADO = 'ACTIVO'";

        try (Connection conn = conexionDB.conectar(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, motivo);
            ps.setInt(2, idUsuario);
            ps.setInt(3, idReceta);

            int filasAfectadas = ps.executeUpdate();
            return filasAfectadas > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}

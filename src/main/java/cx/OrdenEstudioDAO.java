package cx;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrdenEstudioDAO {

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
                + "    COUNT(oe.ID_ORDEN_ESTUDIO) AS total_ordenes "
                + "FROM consultas c "
                + "JOIN veterinarios v ON c.ID_VETERINARIO = v.ID_VETERINARIO "
                + "LEFT JOIN ordenes_estudios oe ON c.ID_CONSULTA = oe.ID_CONSULTA AND oe.ESTADO = 'EMITIDA' "
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
                consulta.put("totalOrdenes", rs.getInt("total_ordenes"));
                consultas.add(consulta);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return consultas;
    }

    // === LISTAR ORDENES DE ESTUDIOS DE UNA MASCOTA ===
    public List<Map<String, Object>> listarOrdenes(int idMascota) {
        List<Map<String, Object>> ordenes = new ArrayList<>();
        String sql = "SELECT "
                + "    oe.ID_ORDEN_ESTUDIO, "
                + "    oe.FECHA_ORDEN, "
                + "    te.NOMBRE AS tipo_estudio, "
                + "    oe.MOTIVO, "
                + "    oe.OBSERVACIONES, "
                + "    oe.ESTADO, "
                + "    c.FECHA_HORA_INICIO AS fecha_consulta, "
                + "    c.MOTIVO AS motivo_consulta, "
                + "    v.NOMBRE AS veterinario "
                + "FROM ordenes_estudios oe "
                + "JOIN tipo_estudio te ON oe.ID_TIPO_ESTUDIO = te.ID_TIPO_ESTUDIO "
                + "JOIN consultas c ON oe.ID_CONSULTA = c.ID_CONSULTA "
                + "JOIN veterinarios v ON oe.ID_VETERINARIO = v.ID_VETERINARIO "
                + "WHERE oe.ID_MASCOTA = ? "
                + "ORDER BY oe.FECHA_ORDEN DESC";

        try (Connection conn = conexionDB.conectar(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idMascota);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Map<String, Object> orden = new HashMap<>();
                orden.put("idOrden", rs.getInt("ID_ORDEN_ESTUDIO"));
                orden.put("fechaOrden", rs.getTimestamp("FECHA_ORDEN"));
                orden.put("tipoEstudio", rs.getString("tipo_estudio"));
                orden.put("motivo", rs.getString("MOTIVO"));
                orden.put("observaciones", rs.getString("OBSERVACIONES"));
                orden.put("estado", rs.getString("ESTADO"));
                orden.put("fechaConsulta", rs.getTimestamp("fecha_consulta"));
                orden.put("motivoConsulta", rs.getString("motivo_consulta"));
                orden.put("veterinario", rs.getString("veterinario"));
                ordenes.add(orden);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ordenes;
    }

    // === OBTENER TIPOS DE ESTUDIOS ===
    public List<Map<String, Object>> obtenerTiposEstudios() {
        List<Map<String, Object>> tipos = new ArrayList<>();
        String sql = "SELECT ID_TIPO_ESTUDIO, NOMBRE FROM tipo_estudio ORDER BY NOMBRE";

        try (Connection conn = conexionDB.conectar(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Map<String, Object> tipo = new HashMap<>();
                tipo.put("idTipo", rs.getInt("ID_TIPO_ESTUDIO"));
                tipo.put("nombre", rs.getString("NOMBRE"));
                tipos.add(tipo);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tipos;
    }

    // === REGISTRAR ORDEN DE ESTUDIO ===
    public boolean registrarOrden(int idConsulta, int idMascota, int idVeterinario,
            int idTipoEstudio, String motivo, String observaciones) {
        String sql = "INSERT INTO ordenes_estudios "
                + "(ID_CONSULTA, ID_MASCOTA, ID_VETERINARIO, ID_TIPO_ESTUDIO, MOTIVO, OBSERVACIONES) "
                + "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = conexionDB.conectar(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idConsulta);
            ps.setInt(2, idMascota);
            ps.setInt(3, idVeterinario);
            ps.setInt(4, idTipoEstudio);
            ps.setString(5, motivo);
            ps.setString(6, observaciones);

            int filasAfectadas = ps.executeUpdate();
            return filasAfectadas > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // === EDITAR ORDEN DE ESTUDIO ===
    public boolean editarOrden(int idOrden, int idTipoEstudio, String motivo, String observaciones) {
        String sql = "UPDATE ordenes_estudios SET "
                + "ID_TIPO_ESTUDIO = ?, "
                + "MOTIVO = ?, "
                + "OBSERVACIONES = ?, "
                + "VERSION = VERSION + 1 "
                + "WHERE ID_ORDEN_ESTUDIO = ? AND ESTADO = 'EMITIDA'";

        try (Connection conn = conexionDB.conectar(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idTipoEstudio);
            ps.setString(2, motivo);
            ps.setString(3, observaciones);
            ps.setInt(4, idOrden);

            int filasAfectadas = ps.executeUpdate();
            return filasAfectadas > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // === ANULAR ORDEN DE ESTUDIO ===
    public boolean anularOrden(int idOrden, int idUsuario, String motivoAnulacion) {
        String sql = "UPDATE ordenes_estudios SET "
                + "ESTADO = 'ANULADA', "
                + "ID_USUARIO_ANULACION = ?, "
                + "MOTIVO_ANULACION = ?, "
                + "FECHA_ANULACION = NOW() "
                + "WHERE ID_ORDEN_ESTUDIO = ? AND ESTADO = 'EMITIDA'";

        try (Connection conn = conexionDB.conectar(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            ps.setString(2, motivoAnulacion);
            ps.setInt(3, idOrden);

            int filasAfectadas = ps.executeUpdate();
            return filasAfectadas > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}

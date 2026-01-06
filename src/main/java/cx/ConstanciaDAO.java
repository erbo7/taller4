// cx/ConstanciaDAO.java
package cx;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConstanciaDAO {

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

    // === LISTAR CONSTANCIAS DE UNA MASCOTA ===
    public List<Map<String, Object>> listarConstancias(int idMascota) {
        List<Map<String, Object>> constancias = new ArrayList<>();
        String sql = "SELECT c.ID_CONSTANCIA, c.FECHA_EMISION, tc.NOMBRE AS tipo_constancia, c.MOTIVO, c.DESCRIPCION, c.OBSERVACIONES, c.ESTADO, v.NOMBRE AS veterinario, con.FECHA_HORA_INICIO AS fecha_consulta FROM constancias c LEFT JOIN tipo_constancia tc ON c.ID_TIPO_CONSTANCIA = tc.ID_TIPO_CONSTANCIA LEFT JOIN veterinarios v ON c.ID_VETERINARIO = v.ID_VETERINARIO LEFT JOIN consultas con ON c.ID_CONSULTA = con.ID_CONSULTA WHERE c.ID_MASCOTA = ? ORDER BY c.FECHA_EMISION DESC";

        try (Connection conn = conexionDB.conectar(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idMascota);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Map<String, Object> constancia = new HashMap<>();
                constancia.put("idConstancia", rs.getInt("ID_CONSTANCIA"));
                constancia.put("fechaEmision", rs.getTimestamp("FECHA_EMISION"));
                constancia.put("tipoConstancia", rs.getString("tipo_constancia"));
                constancia.put("motivo", rs.getString("MOTIVO"));
                constancia.put("descripcion", rs.getString("DESCRIPCION"));
                constancia.put("observaciones", rs.getString("OBSERVACIONES"));
                constancia.put("estado", rs.getString("ESTADO"));
                constancia.put("veterinario", rs.getString("veterinario"));
                constancia.put("fechaConsulta", rs.getTimestamp("fecha_consulta"));
                constancias.add(constancia);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return constancias;
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

    // === OBTENER TIPOS DE CONSTANCIA ===
    public List<Map<String, Object>> obtenerTiposConstancia() {
        List<Map<String, Object>> tipos = new ArrayList<>();
        String sql = "SELECT ID_TIPO_CONSTANCIA, NOMBRE FROM tipo_constancia ORDER BY NOMBRE";

        try (Connection conn = conexionDB.conectar(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Map<String, Object> tipo = new HashMap<>();
                tipo.put("idTipo", rs.getInt("ID_TIPO_CONSTANCIA"));
                tipo.put("nombre", rs.getString("NOMBRE"));
                tipos.add(tipo);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tipos;
    }

    // === REGISTRAR CONSTANCIA ===
    public boolean registrarConstancia(int idConsulta, int idMascota, int idVeterinario, int idTipoConstancia, String motivo, String descripcion, String observaciones) {
        String sql = "INSERT INTO constancias (ID_CONSULTA, ID_MASCOTA, ID_VETERINARIO, ID_TIPO_CONSTANCIA, MOTIVO, DESCRIPCION, OBSERVACIONES, ESTADO) VALUES (?, ?, ?, ?, ?, ?, ?, 'EMITIDA')";

        try (Connection conn = conexionDB.conectar(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setObject(1, idConsulta == 0 ? null : idConsulta);
            ps.setInt(2, idMascota);
            ps.setInt(3, idVeterinario);
            ps.setInt(4, idTipoConstancia);
            ps.setString(5, motivo);
            ps.setString(6, descripcion);
            ps.setString(7, observaciones);

            int filasAfectadas = ps.executeUpdate();
            return filasAfectadas > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // === ACTUALIZAR CONSTANCIA ===
    public boolean actualizarConstancia(int idConstancia, int idTipoConstancia, String motivo, String descripcion, String observaciones) {
        String sql = "UPDATE constancias SET ID_TIPO_CONSTANCIA = ?, MOTIVO = ?, DESCRIPCION = ?, OBSERVACIONES = ?, VERSION = VERSION + 1 WHERE ID_CONSTANCIA = ? AND ESTADO = 'EMITIDA'";

        try (Connection conn = conexionDB.conectar(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idTipoConstancia);
            ps.setString(2, motivo);
            ps.setString(3, descripcion);
            ps.setString(4, observaciones);
            ps.setInt(5, idConstancia);

            int filasAfectadas = ps.executeUpdate();
            return filasAfectadas > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // === ANULAR CONSTANCIA ===
    public boolean anularConstancia(int idConstancia, String motivo, int idUsuario) {
        String sql = "UPDATE constancias SET ESTADO = 'ANULADA', MOTIVO_ANULACION = ?, FECHA_ANULACION = NOW(), ID_USUARIO_ANULACION = ? WHERE ID_CONSTANCIA = ? AND ESTADO = 'EMITIDA'";

        try (Connection conn = conexionDB.conectar(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, motivo);
            ps.setInt(2, idUsuario);
            ps.setInt(3, idConstancia);

            int filasAfectadas = ps.executeUpdate();
            return filasAfectadas > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // === OBTENER DETALLES DE CONSTANCIA ===
    public Map<String, Object> obtenerDetalles(int idConstancia) {
        Map<String, Object> detalles = new HashMap<>();
        String sql = "SELECT ID_TIPO_CONSTANCIA, MOTIVO, DESCRIPCION, OBSERVACIONES FROM constancias WHERE ID_CONSTANCIA = ?";

        try (Connection conn = conexionDB.conectar(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idConstancia);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                detalles.put("idTipoConstancia", rs.getInt("ID_TIPO_CONSTANCIA"));
                detalles.put("motivo", rs.getString("MOTIVO"));
                detalles.put("descripcion", rs.getString("DESCRIPCION"));
                detalles.put("observaciones", rs.getString("OBSERVACIONES"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return detalles;
    }
}

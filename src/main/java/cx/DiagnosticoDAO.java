// cx/DiagnosticoDAO.java
package cx;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DiagnosticoDAO {

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
                + "    COUNT(d.ID_DIAGNOSTICO) AS total_diagnosticos "
                + "FROM consultas c "
                + "JOIN veterinarios v ON c.ID_VETERINARIO = v.ID_VETERINARIO "
                + "LEFT JOIN diagnosticos d ON c.ID_CONSULTA = d.ID_CONSULTA AND d.ESTADO = 'ACTIVO' "
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
                consulta.put("totalDiagnosticos", rs.getInt("total_diagnosticos"));
                consultas.add(consulta);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return consultas;
    }

    // === LISTAR DIAGNÓSTICOS DE UNA MASCOTA ===
    public List<Map<String, Object>> listarDiagnosticos(int idMascota) {
        List<Map<String, Object>> diagnosticos = new ArrayList<>();
        String sql = "SELECT "
                + "    d.ID_DIAGNOSTICO, "
                + "    d.FECHA_REGISTRO, "
                + "    d.DIAGNOSTICO, "
                + "    d.TIPO_DIAGNOSTICO, "
                + "    d.ESTADO, "
                + "    c.FECHA_HORA_INICIO AS fecha_consulta, "
                + "    c.MOTIVO AS motivo_consulta, "
                + "    v.NOMBRE AS veterinario "
                + "FROM diagnosticos d "
                + "JOIN consultas c ON d.ID_CONSULTA = c.ID_CONSULTA "
                + "JOIN veterinarios v ON d.ID_VETERINARIO = v.ID_VETERINARIO "
                + "WHERE d.ID_MASCOTA = ? "
                + "ORDER BY d.FECHA_REGISTRO DESC";

        try (Connection conn = conexionDB.conectar(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idMascota);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Map<String, Object> diagnostico = new HashMap<>();
                diagnostico.put("idDiagnostico", rs.getInt("ID_DIAGNOSTICO"));
                diagnostico.put("fechaRegistro", rs.getTimestamp("FECHA_REGISTRO"));
                diagnostico.put("diagnostico", rs.getString("DIAGNOSTICO"));
                diagnostico.put("tipoDiagnostico", rs.getString("TIPO_DIAGNOSTICO"));
                diagnostico.put("estado", rs.getString("ESTADO"));
                diagnostico.put("fechaConsulta", rs.getTimestamp("fecha_consulta"));
                diagnostico.put("motivoConsulta", rs.getString("motivo_consulta"));
                diagnostico.put("veterinario", rs.getString("veterinario"));
                diagnosticos.add(diagnostico);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return diagnosticos;
    }

    // === OBTENER DETALLES DE UN DIAGNÓSTICO ===
    public Map<String, Object> obtenerDetallesDiagnostico(int idDiagnostico) {
        Map<String, Object> datos = new HashMap<>();
        String sql = "SELECT "
                + "    d.*, "
                + "    c.MOTIVO AS motivo_consulta, "
                + "    v.NOMBRE AS veterinario, "
                + "    m.NOMBRE AS mascota "
                + "FROM diagnosticos d "
                + "JOIN consultas c ON d.ID_CONSULTA = c.ID_CONSULTA "
                + "JOIN veterinarios v ON d.ID_VETERINARIO = v.ID_VETERINARIO "
                + "JOIN mascotas m ON d.ID_MASCOTA = m.ID_MASCOTA "
                + "WHERE d.ID_DIAGNOSTICO = ?";

        try (Connection conn = conexionDB.conectar(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idDiagnostico);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                datos.put("idDiagnostico", rs.getInt("ID_DIAGNOSTICO"));
                datos.put("idConsulta", rs.getInt("ID_CONSULTA"));
                datos.put("diagnostico", rs.getString("DIAGNOSTICO"));
                datos.put("tipoDiagnostico", rs.getString("TIPO_DIAGNOSTICO"));
                datos.put("hallazgos", rs.getString("HALLAZGOS"));
                datos.put("sintomas", rs.getString("SINTOMAS"));
                datos.put("observaciones", rs.getString("OBSERVACIONES"));
                datos.put("motivoConsulta", rs.getString("motivo_consulta"));
                datos.put("veterinario", rs.getString("veterinario"));
                datos.put("mascota", rs.getString("mascota"));
                datos.put("fechaRegistro", rs.getTimestamp("FECHA_REGISTRO"));
                datos.put("version", rs.getInt("VERSION"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return datos;
    }

    // === REGISTRAR DIAGNÓSTICO ===
    public boolean registrarDiagnostico(int idConsulta, int idMascota, int idVeterinario,
            String diagnostico, String tipoDiagnostico,
            String hallazgos, String sintomas, String observaciones) {
        String sql = "INSERT INTO diagnosticos "
                + "(ID_CONSULTA, ID_MASCOTA, ID_VETERINARIO, DIAGNOSTICO, TIPO_DIAGNOSTICO, "
                + " HALLAZGOS, SINTOMAS, OBSERVACIONES) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = conexionDB.conectar(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idConsulta);
            ps.setInt(2, idMascota);
            ps.setInt(3, idVeterinario);
            ps.setString(4, diagnostico);
            ps.setString(5, tipoDiagnostico);
            ps.setString(6, hallazgos);
            ps.setString(7, sintomas);
            ps.setString(8, observaciones);

            int filasAfectadas = ps.executeUpdate();
            return filasAfectadas > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // === EDITAR DIAGNÓSTICO ===
    public boolean editarDiagnostico(int idDiagnostico, String diagnostico, String tipoDiagnostico,
            String hallazgos, String sintomas, String observaciones) {
        String sql = "UPDATE diagnosticos SET "
                + "DIAGNOSTICO = ?, "
                + "TIPO_DIAGNOSTICO = ?, "
                + "HALLAZGOS = ?, "
                + "SINTOMAS = ?, "
                + "OBSERVACIONES = ?, "
                + "VERSION = VERSION + 1 "
                + "WHERE ID_DIAGNOSTICO = ? AND ESTADO = 'ACTIVO'";

        try (Connection conn = conexionDB.conectar(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, diagnostico);
            ps.setString(2, tipoDiagnostico);
            ps.setString(3, hallazgos);
            ps.setString(4, sintomas);
            ps.setString(5, observaciones);
            ps.setInt(6, idDiagnostico);

            int filasAfectadas = ps.executeUpdate();
            return filasAfectadas > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // === ANULAR DIAGNÓSTICO ===
    public boolean anularDiagnostico(int idDiagnostico, String motivo, int idUsuario) {
        String sql = "UPDATE diagnosticos SET "
                + "ESTADO = 'ANULADO', "
                + "MOTIVO_ANULACION = ?, "
                + "FECHA_ANULACION = NOW(), "
                + "ID_USUARIO_ANULACION = ? "
                + "WHERE ID_DIAGNOSTICO = ? AND ESTADO = 'ACTIVO'";

        try (Connection conn = conexionDB.conectar(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, motivo);
            ps.setInt(2, idUsuario);
            ps.setInt(3, idDiagnostico);

            int filasAfectadas = ps.executeUpdate();
            return filasAfectadas > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // === LISTAR DIAGNÓSTICOS POR CONSULTA ESPECÍFICA ===
    public List<Map<String, Object>> listarDiagnosticosPorConsulta(int idConsulta) {
        List<Map<String, Object>> diagnosticos = new ArrayList<>();

        String sql = "SELECT d.ID_DIAGNOSTICO, d.FECHA_REGISTRO, d.DIAGNOSTICO, "
                + "d.TIPO_DIAGNOSTICO, d.ESTADO, d.HALLAZGOS, d.SINTOMAS, "
                + "d.OBSERVACIONES, v.NOMBRE AS veterinario "
                + "FROM diagnosticos d "
                + "JOIN veterinarios v ON d.ID_VETERINARIO = v.ID_VETERINARIO "
                + "WHERE d.ID_CONSULTA = ? "
                + "ORDER BY d.FECHA_REGISTRO DESC";

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = conexionDB.conectar();
            ps = conn.prepareStatement(sql);
            ps.setInt(1, idConsulta);
            rs = ps.executeQuery();

            while (rs.next()) {
                Map<String, Object> diagnostico = new HashMap<>();
                diagnostico.put("idDiagnostico", rs.getInt("ID_DIAGNOSTICO"));
                diagnostico.put("fechaRegistro", rs.getTimestamp("FECHA_REGISTRO"));
                diagnostico.put("diagnostico", rs.getString("DIAGNOSTICO"));
                diagnostico.put("tipoDiagnostico", rs.getString("TIPO_DIAGNOSTICO"));
                diagnostico.put("estado", rs.getString("ESTADO"));
                diagnostico.put("hallazgos", rs.getString("HALLAZGOS"));
                diagnostico.put("sintomas", rs.getString("SINTOMAS"));
                diagnostico.put("observaciones", rs.getString("OBSERVACIONES"));
                diagnostico.put("veterinario", rs.getString("veterinario"));
                diagnosticos.add(diagnostico);
            }
        } catch (SQLException e) {
            System.err.println("ERROR en listarDiagnosticosPorConsulta: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return diagnosticos;
    }

}

package cx;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProcedimientoDAO {

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
                + "    COUNT(p.ID_PROCEDIMIENTO) AS total_procedimientos "
                + "FROM consultas c "
                + "JOIN veterinarios v ON c.ID_VETERINARIO = v.ID_VETERINARIO "
                + "LEFT JOIN procedimientos p ON c.ID_CONSULTA = p.ID_CONSULTA AND p.ESTADO = 'ACTIVO' "
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
                consulta.put("totalProcedimientos", rs.getInt("total_procedimientos"));
                consultas.add(consulta);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return consultas;
    }

    // === LISTAR PROCEDIMIENTOS DE UNA MASCOTA ===
    public List<Map<String, Object>> listarProcedimientos(int idMascota) {
        List<Map<String, Object>> procedimientos = new ArrayList<>();
        String sql = "SELECT "
                + "    p.ID_PROCEDIMIENTO, "
                + "    p.FECHA_REGISTRO, "
                + "    p.TIPO_PROCEDIMIENTO, "
                + "    p.DESCRIPCION, "
                + "    p.ESTADO, "
                + "    c.FECHA_HORA_INICIO AS fecha_consulta, "
                + "    c.MOTIVO AS motivo_consulta, "
                + "    v.NOMBRE AS veterinario "
                + "FROM procedimientos p "
                + "JOIN consultas c ON p.ID_CONSULTA = c.ID_CONSULTA "
                + "JOIN veterinarios v ON p.ID_VETERINARIO = v.ID_VETERINARIO "
                + "WHERE p.ID_MASCOTA = ? "
                + "ORDER BY p.FECHA_REGISTRO DESC";

        try (Connection conn = conexionDB.conectar(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idMascota);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Map<String, Object> procedimiento = new HashMap<>();
                procedimiento.put("idProcedimiento", rs.getInt("ID_PROCEDIMIENTO"));
                procedimiento.put("fechaRegistro", rs.getTimestamp("FECHA_REGISTRO"));
                procedimiento.put("tipoProcedimiento", rs.getString("TIPO_PROCEDIMIENTO"));
                procedimiento.put("descripcion", rs.getString("DESCRIPCION"));
                procedimiento.put("estado", rs.getString("ESTADO"));
                procedimiento.put("fechaConsulta", rs.getTimestamp("fecha_consulta"));
                procedimiento.put("motivoConsulta", rs.getString("motivo_consulta"));
                procedimiento.put("veterinario", rs.getString("veterinario"));
                procedimientos.add(procedimiento);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return procedimientos;
    }

    // === LISTAR INSUMOS DISPONIBLES ===
    public List<Map<String, Object>> listarInsumosDisponibles() {
        List<Map<String, Object>> insumos = new ArrayList<>();
        String sql = "SELECT i.ID_INSUMO, i.NOMBRE, i.STOCK, u.NOMBRE AS unidad "
                + "FROM insumos i "
                + "LEFT JOIN unidades_medida u ON i.ID_UNIDAD = u.ID_UNIDAD "
                + "WHERE i.ESTADO = 'ACTIVO' AND i.STOCK > 0 "
                + "ORDER BY i.NOMBRE";

        try (Connection conn = conexionDB.conectar(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Map<String, Object> insumo = new HashMap<>();
                insumo.put("idInsumo", rs.getInt("ID_INSUMO"));
                insumo.put("nombre", rs.getString("NOMBRE"));
                insumo.put("stock", rs.getInt("STOCK"));
                insumo.put("unidad", rs.getString("unidad"));
                insumos.add(insumo);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return insumos;
    }

    // === REGISTRAR PROCEDIMIENTO CON INSUMOS ===
    public boolean registrarProcedimiento(int idConsulta, int idMascota, int idVeterinario,
            String tipoProcedimiento, String descripcion, String observaciones,
            List<Map<String, Integer>> insumos) {

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = conexionDB.conectar();
            conn.setAutoCommit(false); // Iniciar transacción

            // 1. Insertar procedimiento
            String sqlProc = "INSERT INTO procedimientos "
                    + "(ID_CONSULTA, ID_MASCOTA, ID_VETERINARIO, TIPO_PROCEDIMIENTO, "
                    + " DESCRIPCION, OBSERVACIONES) "
                    + "VALUES (?, ?, ?, ?, ?, ?)";

            ps = conn.prepareStatement(sqlProc, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, idConsulta);
            ps.setInt(2, idMascota);
            ps.setInt(3, idVeterinario);
            ps.setString(4, tipoProcedimiento);
            ps.setString(5, descripcion);
            ps.setString(6, observaciones);

            int filasAfectadas = ps.executeUpdate();

            if (filasAfectadas == 0) {
                conn.rollback();
                return false;
            }

            // Obtener ID del procedimiento insertado
            rs = ps.getGeneratedKeys();
            int idProcedimiento = 0;
            if (rs.next()) {
                idProcedimiento = rs.getInt(1);
            }

            rs.close();
            ps.close();

            // 2. Insertar insumos y descontar stock
            if (insumos != null && !insumos.isEmpty()) {
                System.out.println("🔍 Cantidad de insumos a procesar: " + insumos.size());

                String sqlInsumo = "INSERT INTO procedimiento_insumos (ID_PROCEDIMIENTO, ID_INSUMO, CANTIDAD) VALUES (?, ?, ?)";
                String sqlStock = "UPDATE insumos SET STOCK = STOCK - ? WHERE ID_INSUMO = ?";

                for (Map<String, Integer> insumo : insumos) {
                    int idInsumo = insumo.get("idInsumo");
                    int cantidad = insumo.get("cantidad");

                    System.out.println("📦 Procesando insumo - ID: " + idInsumo + ", Cantidad: " + cantidad);

                    // Insertar relación
                    ps = conn.prepareStatement(sqlInsumo);
                    ps.setInt(1, idProcedimiento);
                    ps.setInt(2, idInsumo);
                    ps.setInt(3, cantidad);
                    int filasInsumo = ps.executeUpdate();
                    System.out.println("✅ Insumo insertado en procedimiento_insumos - Filas: " + filasInsumo);
                    ps.close();

                    // Descontar stock
                    ps = conn.prepareStatement(sqlStock);
                    ps.setInt(1, cantidad);
                    ps.setInt(2, idInsumo);
                    int filasStock = ps.executeUpdate();
                    System.out.println("✅ Stock descontado - Filas afectadas: " + filasStock);
                    ps.close();
                }

                System.out.println("✅ Todos los insumos procesados correctamente");
            } else {
                System.out.println("⚠️ No hay insumos para procesar (lista vacía o null)");
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // === EDITAR PROCEDIMIENTO ===
    public boolean editarProcedimiento(int idProcedimiento, String tipoProcedimiento,
            String descripcion, String observaciones) {
        String sql = "UPDATE procedimientos SET "
                + "TIPO_PROCEDIMIENTO = ?, "
                + "DESCRIPCION = ?, "
                + "OBSERVACIONES = ?, "
                + "VERSION = VERSION + 1 "
                + "WHERE ID_PROCEDIMIENTO = ? AND ESTADO = 'ACTIVO'";

        try (Connection conn = conexionDB.conectar(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, tipoProcedimiento);
            ps.setString(2, descripcion);
            ps.setString(3, observaciones);
            ps.setInt(4, idProcedimiento);

            int filasAfectadas = ps.executeUpdate();
            return filasAfectadas > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // === ANULAR PROCEDIMIENTO ===
    public boolean anularProcedimiento(int idProcedimiento, String motivo, int idUsuario) {
        Connection conn = null;
        PreparedStatement ps = null;

        try {
            conn = conexionDB.conectar();
            conn.setAutoCommit(false);

            // 1. Anular procedimiento
            String sqlAnular = "UPDATE procedimientos SET "
                    + "ESTADO = 'ANULADO', "
                    + "MOTIVO_ANULACION = ?, "
                    + "FECHA_ANULACION = NOW(), "
                    + "ID_USUARIO_ANULACION = ? "
                    + "WHERE ID_PROCEDIMIENTO = ? AND ESTADO = 'ACTIVO'";

            ps = conn.prepareStatement(sqlAnular);
            ps.setString(1, motivo);
            ps.setInt(2, idUsuario);
            ps.setInt(3, idProcedimiento);

            int filasAfectadas = ps.executeUpdate();
            ps.close();

            if (filasAfectadas == 0) {
                conn.rollback();
                return false;
            }

            // 2. Revertir stock de insumos
            String sqlInsumos = "SELECT ID_INSUMO, CANTIDAD FROM procedimiento_insumos WHERE ID_PROCEDIMIENTO = ?";
            ps = conn.prepareStatement(sqlInsumos);
            ps.setInt(1, idProcedimiento);
            ResultSet rs = ps.executeQuery();

            String sqlStock = "UPDATE insumos SET STOCK = STOCK + ? WHERE ID_INSUMO = ?";
            PreparedStatement psStock = conn.prepareStatement(sqlStock);

            while (rs.next()) {
                int idInsumo = rs.getInt("ID_INSUMO");
                int cantidad = rs.getInt("CANTIDAD");

                psStock.setInt(1, cantidad);
                psStock.setInt(2, idInsumo);
                psStock.executeUpdate();
            }

            rs.close();
            ps.close();
            psStock.close();

            conn.commit();
            return true;

        } catch (SQLException e) {
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // === OBTENER INSUMOS DE UN PROCEDIMIENTO ===
    public List<Map<String, Object>> obtenerInsumosPorProcedimiento(int idProcedimiento) {
        List<Map<String, Object>> insumos = new ArrayList<>();
        String sql = "SELECT "
                + "    pi.ID_INSUMO, "
                + "    pi.CANTIDAD, "
                + "    i.NOMBRE AS nombre_insumo, "
                + "    i.STOCK AS stock_actual, "
                + "    u.NOMBRE AS unidad "
                + "FROM procedimiento_insumos pi "
                + "JOIN insumos i ON pi.ID_INSUMO = i.ID_INSUMO "
                + "LEFT JOIN unidades_medida u ON i.ID_UNIDAD = u.ID_UNIDAD "
                + "WHERE pi.ID_PROCEDIMIENTO = ? "
                + "ORDER BY i.NOMBRE";

        try (Connection conn = conexionDB.conectar(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idProcedimiento);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Map<String, Object> insumo = new HashMap<>();
                insumo.put("idInsumo", rs.getInt("ID_INSUMO"));
                insumo.put("cantidad", rs.getInt("CANTIDAD"));
                insumo.put("nombreInsumo", rs.getString("nombre_insumo"));
                insumo.put("stockActual", rs.getInt("stock_actual"));
                insumo.put("unidad", rs.getString("unidad"));
                insumos.add(insumo);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return insumos;
    }

    // === CORREGIR PROCEDIMIENTO (CREAR UNO NUEVO VINCULADO) ===
    public String corregirProcedimiento(int idProcedimientoOriginal, int idConsulta, int idMascota,
            String tipoProcedimiento, String descripcion, String observaciones,
            List<Integer> idsInsumos, List<Integer> cantidades) {

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = conexionDB.conectar();
            conn.setAutoCommit(false);

            System.out.println("🔄 [DAO] Iniciando corrección de procedimiento ID: " + idProcedimientoOriginal);

            // 1️⃣ DEVOLVER STOCK DE LOS INSUMOS DEL PROCEDIMIENTO ORIGINAL
            String sqlInsumosOriginales = "SELECT ID_INSUMO, CANTIDAD FROM procedimiento_insumos WHERE ID_PROCEDIMIENTO = ?";
            ps = conn.prepareStatement(sqlInsumosOriginales);
            ps.setInt(1, idProcedimientoOriginal);
            rs = ps.executeQuery();

            System.out.println("📦 [DAO] Devolviendo stock de insumos del procedimiento original...");
            while (rs.next()) {
                int idInsumo = rs.getInt("ID_INSUMO");
                int cantidad = rs.getInt("CANTIDAD");

                String sqlDevolverStock = "UPDATE insumos SET STOCK = STOCK + ? WHERE ID_INSUMO = ?";
                PreparedStatement psDev = conn.prepareStatement(sqlDevolverStock);
                psDev.setInt(1, cantidad);
                psDev.setInt(2, idInsumo);
                int filasDev = psDev.executeUpdate();
                psDev.close();

                System.out.println("   ✅ Stock devuelto - Insumo ID: " + idInsumo + ", Cantidad: " + cantidad + ", Filas: " + filasDev);
            }
            rs.close();
            ps.close();

            // 2️⃣ MARCAR EL PROCEDIMIENTO ORIGINAL COMO "CORREGIDO" (estado = ANULADO)
            String sqlMarcarOriginal = "UPDATE procedimientos SET ESTADO = 'ANULADO', "
                    + "MOTIVO_ANULACION = 'Procedimiento corregido - Ver procedimiento vinculado' "
                    + "WHERE ID_PROCEDIMIENTO = ?";
            ps = conn.prepareStatement(sqlMarcarOriginal);
            ps.setInt(1, idProcedimientoOriginal);
            ps.executeUpdate();
            ps.close();
            System.out.println("✅ [DAO] Procedimiento original marcado como ANULADO");

            // 3️⃣ CREAR NUEVO PROCEDIMIENTO (LA CORRECCIÓN)
            String sqlInsertProc = "INSERT INTO procedimientos "
                    + "(ID_CONSULTA, ID_MASCOTA, ID_VETERINARIO, TIPO_PROCEDIMIENTO, DESCRIPCION, "
                    + "OBSERVACIONES, ID_PROCEDIMIENTO_ORIGINAL, ES_CORRECCION, ESTADO) "
                    + "VALUES (?, ?, (SELECT ID_VETERINARIO FROM consultas WHERE ID_CONSULTA = ?), ?, ?, ?, ?, TRUE, 'ACTIVO')";

            ps = conn.prepareStatement(sqlInsertProc, PreparedStatement.RETURN_GENERATED_KEYS);
            ps.setInt(1, idConsulta);
            ps.setInt(2, idMascota);
            ps.setInt(3, idConsulta);
            ps.setString(4, tipoProcedimiento);
            ps.setString(5, descripcion);
            ps.setString(6, observaciones + " [CORRECCIÓN DE PROCEDIMIENTO #" + idProcedimientoOriginal + "]");
            ps.setInt(7, idProcedimientoOriginal);
            ps.executeUpdate();

            rs = ps.getGeneratedKeys();
            int idNuevoProcedimiento = 0;
            if (rs.next()) {
                idNuevoProcedimiento = rs.getInt(1);
            }
            rs.close();
            ps.close();

            System.out.println("✅ [DAO] Nuevo procedimiento creado - ID: " + idNuevoProcedimiento);

            // 4️⃣ REGISTRAR INSUMOS DEL NUEVO PROCEDIMIENTO Y DESCONTAR STOCK
            if (idsInsumos != null && !idsInsumos.isEmpty()) {
                System.out.println("📦 [DAO] Registrando " + idsInsumos.size() + " insumos en el nuevo procedimiento...");

                String sqlInsertInsumo = "INSERT INTO procedimiento_insumos (ID_PROCEDIMIENTO, ID_INSUMO, CANTIDAD) VALUES (?, ?, ?)";
                String sqlDescontarStock = "UPDATE insumos SET STOCK = STOCK - ? WHERE ID_INSUMO = ?";

                for (int i = 0; i < idsInsumos.size(); i++) {
                    int idInsumo = idsInsumos.get(i);
                    int cantidad = cantidades.get(i);

                    // Insertar en procedimiento_insumos
                    PreparedStatement psIns = conn.prepareStatement(sqlInsertInsumo);
                    psIns.setInt(1, idNuevoProcedimiento);
                    psIns.setInt(2, idInsumo);
                    psIns.setInt(3, cantidad);
                    psIns.executeUpdate();
                    psIns.close();

                    // Descontar stock
                    PreparedStatement psStock = conn.prepareStatement(sqlDescontarStock);
                    psStock.setInt(1, cantidad);
                    psStock.setInt(2, idInsumo);
                    int filasStock = psStock.executeUpdate();
                    psStock.close();

                    System.out.println("   ✅ Insumo ID: " + idInsumo + ", Cantidad: " + cantidad + ", Stock descontado: " + filasStock);
                }
            } else {
                System.out.println("⚠️ [DAO] No se registraron insumos (corrección sin insumos)");
            }

            // 5️⃣ COMMIT
            conn.commit();
            System.out.println("✅ [DAO] Corrección completada exitosamente");

            return "OK|Procedimiento corregido exitosamente. El procedimiento original fue anulado y se creó uno nuevo.";

        } catch (SQLException e) {
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            return "ERROR|Error al corregir procedimiento: " + e.getMessage();
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

}

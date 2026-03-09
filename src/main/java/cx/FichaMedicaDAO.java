package cx;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FichaMedicaDAO {

    private conexion conexionDB = new conexion();

    // === BUSCAR MASCOTAS POR NOMBRE O DUEÑO ===
    public List<Map<String, Object>> buscarMascotas(String texto) {
        List<Map<String, Object>> mascotas = new ArrayList<>();
        String sql = "SELECT DISTINCT m.ID_MASCOTA, m.NOMBRE AS mascota, e.NOMBRE AS especie, r.NOMBRE AS raza, m.EDAD, m.SEXO, cl.NOMBRE AS cliente, cl.TELEFONO, cl.EMAIL, f.ID_FICHA, f.FECHA_CREACION AS fecha_ficha, f.ESTADO AS estado_ficha FROM mascotas m JOIN clientes cl ON m.ID_CLIENTE = cl.ID_CLIENTE LEFT JOIN ficha_mascota f ON m.ID_MASCOTA = f.ID_MASCOTA AND f.ESTADO = 'ACTIVA' LEFT JOIN razas r ON m.ID_RAZA = r.ID_RAZA LEFT JOIN especies e ON r.ID_ESPECIE = e.ID_ESPECIE WHERE (m.NOMBRE LIKE ? OR cl.NOMBRE LIKE ?) ORDER BY m.NOMBRE LIMIT 20";

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
                mascota.put("edad", rs.getInt("EDAD"));
                mascota.put("sexo", rs.getString("SEXO"));
                mascota.put("nombreCliente", rs.getString("cliente"));
                mascota.put("telefono", rs.getString("TELEFONO"));
                mascota.put("email", rs.getString("EMAIL"));
                mascota.put("idFicha", rs.getObject("ID_FICHA"));
                mascota.put("fechaFicha", rs.getTimestamp("fecha_ficha"));
                mascota.put("estadoFicha", rs.getString("estado_ficha"));
                mascotas.add(mascota);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return mascotas;
    }

    // === OBTENER DATOS BÁSICOS DE LA MASCOTA ===
    public Map<String, Object> obtenerDatosMascota(int idMascota) {
        Map<String, Object> datos = new HashMap<>();
        String sql = "SELECT m.ID_MASCOTA, m.NOMBRE, m.EDAD, m.SEXO, e.NOMBRE AS especie, r.NOMBRE AS raza, cl.NOMBRE AS dueno, cl.TELEFONO AS telefono, cl.DIRECCION AS direccion, cl.EMAIL AS email, f.ID_FICHA, f.ESTADO AS estado_ficha, f.FECHA_CREACION AS fecha_creacion_ficha FROM mascotas m JOIN clientes cl ON m.ID_CLIENTE = cl.ID_CLIENTE LEFT JOIN ficha_mascota f ON m.ID_MASCOTA = f.ID_MASCOTA AND f.ESTADO = 'ACTIVA' LEFT JOIN razas r ON m.ID_RAZA = r.ID_RAZA LEFT JOIN especies e ON r.ID_ESPECIE = e.ID_ESPECIE WHERE m.ID_MASCOTA = ?";

        try (Connection conn = conexionDB.conectar(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idMascota);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                datos.put("id", rs.getInt("ID_MASCOTA"));
                datos.put("nombre", rs.getString("NOMBRE"));
                datos.put("edad", rs.getInt("EDAD"));
                datos.put("sexo", rs.getString("SEXO"));
                datos.put("especie", rs.getString("especie"));
                datos.put("raza", rs.getString("raza"));
                datos.put("dueno", rs.getString("dueno"));
                datos.put("telefono", rs.getString("telefono"));
                datos.put("direccion", rs.getString("direccion"));
                datos.put("email", rs.getString("email"));
                datos.put("idFicha", rs.getInt("ID_FICHA"));
                datos.put("estadoFicha", rs.getString("estado_ficha"));
                datos.put("fechaCreacionFicha", rs.getString("fecha_creacion_ficha"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return datos;
    }

    // === OBTENER HISTORIAL DE CONSULTAS ===
    public List<Map<String, Object>> obtenerHistorialConsultas(int idMascota) {
        List<Map<String, Object>> consultas = new ArrayList<>();
        String sql = "SELECT c.ID_CONSULTA, c.FECHA_HORA_INICIO, c.FECHA_HORA_FIN, c.MOTIVO, c.PESO, c.TEMPERATURA, c.ESTADO, v.NOMBRE AS veterinario, v.MATRICULA FROM consultas c JOIN veterinarios v ON c.ID_VETERINARIO = v.ID_VETERINARIO WHERE c.ID_MASCOTA = ? AND c.ESTADO != 'ANULADA' ORDER BY c.FECHA_HORA_INICIO DESC";

        try (Connection conn = conexionDB.conectar(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idMascota);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Map<String, Object> consulta = new HashMap<>();
                consulta.put("idConsulta", rs.getInt("ID_CONSULTA"));
                consulta.put("fechaInicio", rs.getTimestamp("FECHA_HORA_INICIO"));
                consulta.put("fechaFin", rs.getTimestamp("FECHA_HORA_FIN"));
                consulta.put("motivo", rs.getString("MOTIVO"));
                consulta.put("peso", rs.getBigDecimal("PESO"));
                consulta.put("temperatura", rs.getBigDecimal("TEMPERATURA"));
                consulta.put("estado", rs.getString("ESTADO"));
                consulta.put("veterinario", rs.getString("veterinario"));
                consulta.put("matricula", rs.getString("MATRICULA"));
                consultas.add(consulta);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return consultas;
    }

    // === OBTENER DIAGNÓSTICOS ===
    public List<Map<String, Object>> obtenerDiagnosticos(int idMascota) {
        List<Map<String, Object>> diagnosticos = new ArrayList<>();

        System.out.println("🔍 [DIAGNOSTICOS] Buscando para mascota ID: " + idMascota);

        // ✅ QUERY SIMPLE QUE SABEMOS QUE FUNCIONA - TODAS LAS COLUMNAS NECESARIAS
        String sql = "SELECT d.ID_DIAGNOSTICO, d.FECHA_REGISTRO, d.DIAGNOSTICO, "
                + "d.TIPO_DIAGNOSTICO, d.ESTADO, d.ID_VETERINARIO "
                + "FROM diagnosticos d "
                + "WHERE d.ID_MASCOTA = ? AND d.ESTADO = 'ACTIVO' "
                + "ORDER BY d.FECHA_REGISTRO DESC";

        try (Connection conn = conexionDB.conectar(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idMascota);

            System.out.println("🔍 SQL: " + sql.replace("?", String.valueOf(idMascota)));

            try (ResultSet rs = ps.executeQuery()) {
                System.out.println("✅ Query ejecutada");

                int count = 0;
                while (rs.next()) {
                    count++;
                    Map<String, Object> diagnostico = new HashMap<>();

                    // COLUMNAS QUE NECESITA EL JSP:
                    diagnostico.put("idDiagnostico", rs.getInt("ID_DIAGNOSTICO"));
                    diagnostico.put("fechaRegistro", rs.getTimestamp("FECHA_REGISTRO"));  // Para "Fecha"
                    diagnostico.put("diagnostico", rs.getString("DIAGNOSTICO"));         // Para "Diagnóstico"
                    diagnostico.put("tipoDiagnostico", rs.getString("TIPO_DIAGNOSTICO")); // Para "Tipo"
                    diagnostico.put("estado", rs.getString("ESTADO"));

                    // Veterinario - obtener nombre por ID
                    int idVet = rs.getInt("ID_VETERINARIO");
                    String nombreVet = "Sin veterinario";
                    if (idVet > 0) {
                        nombreVet = obtenerNombreVeterinario(idVet);
                    }
                    diagnostico.put("veterinario", nombreVet);  // Para "Veterinario"

                    diagnosticos.add(diagnostico);

                    System.out.println("   📄 Dx " + count + ": " + rs.getString("DIAGNOSTICO")
                            + " | Tipo: " + rs.getString("TIPO_DIAGNOSTICO")
                            + " | Vet: " + nombreVet);
                }

                System.out.println("✅ Total encontrados: " + count);

                if (count == 0) {
                    System.out.println("⚠️ No hay diagnósticos ACTIVOS");
                }
            }

        } catch (SQLException e) {
            System.err.println("❌ ERROR SQL: " + e.getMessage());
            e.printStackTrace();

            // Si hay error, devolver lista vacía para que no rompa
            return new ArrayList<>();

        } catch (Exception e) {
            System.err.println("❌ ERROR GENERAL: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }

        return diagnosticos;
    }

// === MÉTODO AUXILIAR PARA OBTENER NOMBRE DEL VETERINARIO ===
    private String obtenerNombreVeterinario(int idVeterinario) {
        if (idVeterinario <= 0) {
            return "Sin veterinario";
        }

        String sql = "SELECT NOMBRE FROM veterinarios WHERE ID_VETERINARIO = ?";

        try (Connection conn = conexionDB.conectar(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idVeterinario);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getString("NOMBRE");
            }
        } catch (Exception e) {
            System.err.println("⚠️ Error al obtener veterinario ID " + idVeterinario + ": " + e.getMessage());
        }

        return "Veterinario ID: " + idVeterinario;
    }

// Método para diagnóstico adicional
    private void diagnosticarError(Connection conn, int idMascota) {
        System.out.println("🔧 DIAGNÓSTICO DE ERROR - Verificando estado BD:");

        if (conn == null) {
            System.out.println("   ❌ Conexión es NULL");
            return;
        }

        // Probar diferentes enfoques
        String[] testQueries = {
            "SHOW TABLES LIKE 'diagnosticos'",
            "SELECT COUNT(*) as existe FROM information_schema.tables WHERE table_name = 'diagnosticos'",
            "SELECT 1"
        };

        for (int i = 0; i < testQueries.length; i++) {
            try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(testQueries[i])) {
                System.out.println("   ✅ Test " + (i + 1) + " [" + testQueries[i] + "]: OK");
                if (rs.next()) {
                    System.out.println("      Resultado: " + rs.getString(1));
                }
            } catch (SQLException e) {
                System.err.println("   ❌ Test " + (i + 1) + " [" + testQueries[i] + "]: FALLÓ - " + e.getMessage());
            }
        }
    }

    // === OBTENER PROCEDIMIENTOS ===
    public List<Map<String, Object>> obtenerProcedimientos(int idMascota) {
        List<Map<String, Object>> procedimientos = new ArrayList<>();
        String sql = "SELECT p.ID_PROCEDIMIENTO, p.FECHA_PROCEDIMIENTO, p.TIPO_PROCEDIMIENTO AS tipo_procedimiento, p.DESCRIPCION, p.OBSERVACIONES, p.ESTADO, v.NOMBRE AS veterinario, c.FECHA_HORA_INICIO AS fecha_consulta FROM procedimientos p JOIN consultas c ON p.ID_CONSULTA = c.ID_CONSULTA JOIN veterinarios v ON c.ID_VETERINARIO = v.ID_VETERINARIO WHERE c.ID_MASCOTA = ? AND p.ESTADO != 'ANULADO' ORDER BY p.FECHA_PROCEDIMIENTO DESC";

        try (Connection conn = conexionDB.conectar(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idMascota);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Map<String, Object> procedimiento = new HashMap<>();
                procedimiento.put("idProcedimiento", rs.getInt("ID_PROCEDIMIENTO"));
                procedimiento.put("fechaProcedimiento", rs.getTimestamp("FECHA_PROCEDIMIENTO"));
                procedimiento.put("tipoProcedimiento", rs.getString("tipo_procedimiento"));
                procedimiento.put("descripcion", rs.getString("DESCRIPCION"));
                procedimiento.put("observaciones", rs.getString("OBSERVACIONES"));
                procedimiento.put("estado", rs.getString("ESTADO"));
                procedimiento.put("veterinario", rs.getString("veterinario"));
                procedimiento.put("fechaConsulta", rs.getTimestamp("fecha_consulta"));
                procedimientos.add(procedimiento);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return procedimientos;
    }

    // === OBTENER RECETAS ===
    public List<Map<String, Object>> obtenerRecetas(int idMascota) {
        List<Map<String, Object>> recetas = new ArrayList<>();
        String sql = "SELECT r.ID_RECETA, r.FECHA_REGISTRO, r.MEDICAMENTO, r.DOSIS, r.FRECUENCIA, r.DURACION, r.INDICACIONES, r.ESTADO, v.NOMBRE AS veterinario, c.FECHA_HORA_INICIO AS fecha_consulta FROM recetas r JOIN consultas c ON r.ID_CONSULTA = c.ID_CONSULTA JOIN veterinarios v ON c.ID_VETERINARIO = v.ID_VETERINARIO WHERE c.ID_MASCOTA = ? AND r.ESTADO != 'ANULADA' ORDER BY r.FECHA_REGISTRO DESC";

        try (Connection conn = conexionDB.conectar(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idMascota);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Map<String, Object> receta = new HashMap<>();
                receta.put("idReceta", rs.getInt("ID_RECETA"));
                receta.put("fechaEmision", rs.getTimestamp("FECHA_REGISTRO"));
                receta.put("medicamento", rs.getString("MEDICAMENTO"));
                receta.put("dosis", rs.getString("DOSIS"));
                receta.put("frecuencia", rs.getString("FRECUENCIA"));
                receta.put("duracion", rs.getString("DURACION"));
                receta.put("indicaciones", rs.getString("INDICACIONES"));
                receta.put("estado", rs.getString("ESTADO"));
                receta.put("veterinario", rs.getString("veterinario"));
                receta.put("fechaConsulta", rs.getTimestamp("fecha_consulta"));
                recetas.add(receta);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return recetas;
    }

    // === OBTENER ÓRDENES DE ESTUDIOS ===
    public List<Map<String, Object>> obtenerOrdenesEstudios(int idMascota) {
        List<Map<String, Object>> estudios = new ArrayList<>();
        String sql = "SELECT oe.ID_ORDEN_ESTUDIO, oe.FECHA_ORDEN, te.NOMBRE AS tipo_estudio, oe.MOTIVO, oe.OBSERVACIONES, oe.ESTADO, v.NOMBRE AS veterinario, c.FECHA_HORA_INICIO AS fecha_consulta FROM ordenes_estudios oe JOIN tipo_estudio te ON oe.ID_TIPO_ESTUDIO = te.ID_TIPO_ESTUDIO JOIN consultas c ON oe.ID_CONSULTA = c.ID_CONSULTA JOIN veterinarios v ON c.ID_VETERINARIO = v.ID_VETERINARIO WHERE c.ID_MASCOTA = ? AND oe.ESTADO != 'ANULADA' ORDER BY oe.FECHA_ORDEN DESC";

        try (Connection conn = conexionDB.conectar(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idMascota);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Map<String, Object> estudio = new HashMap<>();
                estudio.put("idOrdenEstudio", rs.getInt("ID_ORDEN_ESTUDIO"));
                estudio.put("fechaEmision", rs.getTimestamp("FECHA_ORDEN"));
                estudio.put("tipoEstudio", rs.getString("tipo_estudio"));
                estudio.put("motivo", rs.getString("MOTIVO"));
                estudio.put("observaciones", rs.getString("OBSERVACIONES"));
                estudio.put("estado", rs.getString("ESTADO"));
                estudio.put("veterinario", rs.getString("veterinario"));
                estudio.put("fechaConsulta", rs.getTimestamp("fecha_consulta"));
                estudios.add(estudio);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return estudios;
    }

    // === OBTENER ÓRDENES DE ANÁLISIS ===
    public List<Map<String, Object>> obtenerOrdenesAnalisis(int idMascota) {
        List<Map<String, Object>> analisis = new ArrayList<>();
        String sql = "SELECT oa.ID_ANALISIS, oa.FECHA_ORDEN, ta.NOMBRE AS tipo_analisis, oa.MOTIVO, oa.OBSERVACIONES, oa.ESTADO, v.NOMBRE AS veterinario, c.FECHA_HORA_INICIO AS fecha_consulta FROM ordenes_analisis oa JOIN tipo_analisis ta ON oa.ID_TIPO_ANALISIS = ta.ID_TIPO_ANALISIS JOIN consultas c ON oa.ID_CONSULTA = c.ID_CONSULTA JOIN veterinarios v ON c.ID_VETERINARIO = v.ID_VETERINARIO WHERE c.ID_MASCOTA = ? AND oa.ESTADO != 'ANULADA' ORDER BY oa.FECHA_ORDEN DESC";

        try (Connection conn = conexionDB.conectar(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idMascota);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Map<String, Object> analisi = new HashMap<>();
                analisi.put("idAnalisis", rs.getInt("ID_ANALISIS"));
                analisi.put("fechaEmision", rs.getTimestamp("FECHA_ORDEN"));
                analisi.put("tipoAnalisis", rs.getString("tipo_analisis"));
                analisi.put("motivo", rs.getString("MOTIVO"));
                analisi.put("observaciones", rs.getString("OBSERVACIONES"));
                analisi.put("estado", rs.getString("ESTADO"));
                analisi.put("veterinario", rs.getString("veterinario"));
                analisi.put("fechaConsulta", rs.getTimestamp("fecha_consulta"));
                analisis.add(analisi);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return analisis;
    }

    // === OBTENER TRATAMIENTOS (VERSIÓN CORREGIDA) ===
    public List<Map<String, Object>> obtenerTratamientos(int idMascota) {
        List<Map<String, Object>> tratamientos = new ArrayList<>();

        System.out.println("🔍 [TRATAMIENTOS] Buscando para mascota ID: " + idMascota);

        // ✅ Misma query que funciona en TratamientoDAO.listarTratamientos()
        String sql = "SELECT t.ID_TRATAMIENTO, t.fecha_inicio, t.fecha_fin, t.PLAN_TERAPEUTICO, "
                + "t.EVOLUCION, t.FECHA_CONTROL, t.OBSERVACIONES, t.ESTADO, "
                + "t.FECHA_REGISTRO, v.NOMBRE AS veterinario, "
                + "c.FECHA_HORA_INICIO AS fecha_consulta "
                + "FROM tratamientos t "
                + "LEFT JOIN veterinarios v ON t.ID_VETERINARIO = v.ID_VETERINARIO "
                + "LEFT JOIN consultas c ON t.ID_CONSULTA = c.ID_CONSULTA "
                + "WHERE t.ID_MASCOTA = ? AND t.ESTADO != 'ANULADO' "
                + "ORDER BY t.FECHA_REGISTRO DESC";

        try (Connection conn = conexionDB.conectar(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idMascota);

            System.out.println("🔍 SQL: " + sql.replace("?", String.valueOf(idMascota)));

            try (ResultSet rs = ps.executeQuery()) {
                System.out.println("✅ Query ejecutada");

                int count = 0;
                while (rs.next()) {
                    count++;
                    Map<String, Object> tratamiento = new HashMap<>();

                    // Mapeo de columnas (igual que en TratamientoDAO)
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

                    System.out.println("   📋 Tratamiento " + count + ": "
                            + (rs.getString("PLAN_TERAPEUTICO") != null
                            ? rs.getString("PLAN_TERAPEUTICO").substring(0, Math.min(50, rs.getString("PLAN_TERAPEUTICO").length()))
                            : "Sin plan") + "...");
                }

                System.out.println("✅ Total tratamientos encontrados: " + count);

                if (count == 0) {
                    System.out.println("⚠️ No hay tratamientos ACTIVOS para esta mascota");
                }
            }

        } catch (SQLException e) {
            System.err.println("❌ ERROR SQL en obtenerTratamientos: " + e.getMessage());
            System.err.println("   SQL State: " + e.getSQLState());
            System.err.println("   Error Code: " + e.getErrorCode());
            e.printStackTrace();

            // Si hay error, devolver lista vacía
            return new ArrayList<>();

        } catch (Exception e) {
            System.err.println("❌ ERROR GENERAL: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }

        return tratamientos;
    }

    // === OBTENER CONSTANCIAS ===
    public List<Map<String, Object>> obtenerConstancias(int idMascota) {
        List<Map<String, Object>> constancias = new ArrayList<>();

        System.out.println("🔍 [CONSTANCIAS] Buscando para mascota ID: " + idMascota);

        // ✅ Misma query que funciona en ConstanciaDAO.listarConstancias()
        String sql = "SELECT c.ID_CONSTANCIA, c.FECHA_EMISION, tc.NOMBRE AS tipo_constancia, "
                + "c.MOTIVO, c.DESCRIPCION, c.OBSERVACIONES, c.ESTADO, "
                + "v.NOMBRE AS veterinario, con.FECHA_HORA_INICIO AS fecha_consulta "
                + "FROM constancias c "
                + "LEFT JOIN tipo_constancia tc ON c.ID_TIPO_CONSTANCIA = tc.ID_TIPO_CONSTANCIA "
                + "LEFT JOIN veterinarios v ON c.ID_VETERINARIO = v.ID_VETERINARIO "
                + "LEFT JOIN consultas con ON c.ID_CONSULTA = con.ID_CONSULTA "
                + "WHERE c.ID_MASCOTA = ? AND c.ESTADO != 'ANULADA' "
                + "ORDER BY c.FECHA_EMISION DESC";

        try (Connection conn = conexionDB.conectar(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idMascota);

            System.out.println("🔍 SQL: " + sql.replace("?", String.valueOf(idMascota)));

            try (ResultSet rs = ps.executeQuery()) {
                System.out.println("✅ Query ejecutada");

                int count = 0;
                while (rs.next()) {
                    count++;
                    Map<String, Object> constancia = new HashMap<>();

                    // Mapeo de columnas (igual que en ConstanciaDAO)
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

                    System.out.println("   📄 Constancia " + count + ": "
                            + rs.getString("tipo_constancia") + " - "
                            + (rs.getString("MOTIVO") != null
                            ? rs.getString("MOTIVO").substring(0, Math.min(50, rs.getString("MOTIVO").length()))
                            : "Sin motivo"));
                }

                System.out.println("✅ Total constancias encontradas: " + count);

                if (count == 0) {
                    System.out.println("⚠️ No hay constancias EMITIDAS para esta mascota");
                }
            }

        } catch (SQLException e) {
            System.err.println("❌ ERROR SQL en obtenerConstancias: " + e.getMessage());
            System.err.println("   SQL State: " + e.getSQLState());
            System.err.println("   Error Code: " + e.getErrorCode());
            e.printStackTrace();

            // Si hay error, devolver lista vacía
            return new ArrayList<>();

        } catch (Exception e) {
            System.err.println("❌ ERROR GENERAL: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }

        return constancias;
    }

    // === EDITAR FICHA MÉDICA ===
    public boolean editarFicha(int idFicha, String observaciones, int idUsuario) {
        String sql = "UPDATE ficha_mascota SET OBSERVACIONES_GENERALES = ?, VERSION = VERSION + 1, FECHA_ACTUALIZACION = NOW() WHERE ID_FICHA = ? AND ESTADO = 'ACTIVA'";

        try (Connection conn = conexionDB.conectar(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, observaciones);
            ps.setInt(2, idFicha);
            int filasAfectadas = ps.executeUpdate();
            return filasAfectadas > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // === ANULAR FICHA MÉDICA ===
    public boolean anularFicha(int idFicha, String motivo, int idUsuario) {
        String sql = "UPDATE ficha_mascota SET ESTADO = 'ANULADA', MOTIVO_ANULACION = ?, FECHA_ANULACION = NOW(), ID_USUARIO_ANULACION = ? WHERE ID_FICHA = ? AND ESTADO = 'ACTIVA'";

        try (Connection conn = conexionDB.conectar(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, motivo);
            ps.setInt(2, idUsuario);
            ps.setInt(3, idFicha);
            int filasAfectadas = ps.executeUpdate();
            return filasAfectadas > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // === OBTENER DETALLES DE LA FICHA ===
    public Map<String, Object> obtenerDetallesFicha(int idFicha) {
        Map<String, Object> datos = new HashMap<>();
        String sql = "SELECT OBSERVACIONES_GENERALES, VERSION, FECHA_ACTUALIZACION, ESTADO FROM ficha_mascota WHERE ID_FICHA = ?";

        try (Connection conn = conexionDB.conectar(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idFicha);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                datos.put("observaciones", rs.getString("OBSERVACIONES_GENERALES"));
                datos.put("version", rs.getInt("VERSION"));
                datos.put("fechaModificacion", rs.getTimestamp("FECHA_ACTUALIZACION"));
                datos.put("estado", rs.getString("ESTADO"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return datos;
    }
}

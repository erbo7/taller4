// cx/ConsultaDAO.java - VERSIÓN CORREGIDA
package cx;

import java.sql.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import modelo.Consulta;

public class ConsultaDAO {

    private conexion conexionDB = new conexion();

    // === INICIAR CONSULTA (CON FICHA ÚNICA POR MASCOTA) ===
    public String iniciarConsulta(Consulta c) {
        Connection conn = null;
        try {
            conn = conexionDB.conectar();
            conn.setAutoCommit(false); // Iniciar transacción

            // 1. Verificar si la mascota ya tiene ficha médica
            int idFicha = obtenerOCrearFichaMascota(conn, c.getIdMascota());

            if (idFicha == 0) {
                conn.rollback();
                return "ERROR|No se pudo obtener o crear la ficha médica";
            }

            // 2. Crear la consulta en estado "EN_CURSO"
            String sqlConsulta = """
                INSERT INTO consultas (ID_CITA, ID_VETERINARIO, ID_MASCOTA, ID_FICHA,
                    MOTIVO, PESO, temperatura, frecuencia_cardiaca, frecuencia_respiratoria,
                    mucosas, cap_reflejo, condicion_corporal, OBSERVACIONES, 
                    ESTADO, FECHA_HORA_INICIO)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 'EN_CURSO', NOW())
                """;

            PreparedStatement psConsulta = conn.prepareStatement(sqlConsulta, Statement.RETURN_GENERATED_KEYS);
            psConsulta.setInt(1, c.getIdCita());
            psConsulta.setInt(2, c.getIdVeterinario());
            psConsulta.setInt(3, c.getIdMascota());
            psConsulta.setInt(4, idFicha);
            psConsulta.setString(5, c.getMotivo());
            psConsulta.setBigDecimal(6, c.getPeso());
            psConsulta.setBigDecimal(7, c.getTemperatura());
            psConsulta.setObject(8, c.getFrecuenciaCardiaca());
            psConsulta.setObject(9, c.getFrecuenciaRespiratoria());
            psConsulta.setString(10, c.getMucosas());
            psConsulta.setString(11, c.getCapReflejo());
            psConsulta.setString(12, c.getCondicionCorporal());
            psConsulta.setString(13, c.getObservaciones());

            int filas = psConsulta.executeUpdate();

            if (filas == 0) {
                conn.rollback();
                return "ERROR|No se pudo crear la consulta";
            }

            // Obtener el ID de la consulta recién creada
            ResultSet rsConsulta = psConsulta.getGeneratedKeys();
            int idConsulta = 0;
            if (rsConsulta.next()) {
                idConsulta = rsConsulta.getInt(1);
            }

            // 3. Registrar en el historial
            registrarHistorialConConexion(conn, idConsulta, "CREACION", "Consulta iniciada - Vinculada a ficha #" + idFicha);

            // 4. Confirmar la transacción
            conn.commit();

            return "OK|Consulta iniciada exitosamente|" + idConsulta;

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
            return "ERROR|Error al iniciar la consulta: " + e.getMessage();
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // === OBTENER O CREAR FICHA MÉDICA (UNA POR MASCOTA) ===
    private int obtenerOCrearFichaMascota(Connection conn, int idMascota) throws SQLException {
        // 1. Verificar si ya existe ficha para esta mascota
        String sqlBuscar = "SELECT ID_FICHA FROM ficha_mascota WHERE ID_MASCOTA = ? AND ESTADO = 'ACTIVA'";
        try (PreparedStatement ps = conn.prepareStatement(sqlBuscar)) {
            ps.setInt(1, idMascota);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("ID_FICHA"); // Ya existe, retornar su ID
            }
        }

        // 2. No existe, crear nueva ficha
        String sqlCrear = """
    INSERT INTO ficha_mascota (ID_MASCOTA, FECHA_CREACION, ESTADO)
    VALUES (?, NOW(), 'ACTIVA')
    """;
        try (PreparedStatement ps = conn.prepareStatement(sqlCrear, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, idMascota);
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1); // Retornar el ID de la ficha recién creada
            }
        }

        return 0; // Error
    }

    // === FINALIZAR CONSULTA ===
    public String finalizarConsulta(int idConsulta) {
        Connection conn = null;
        try {
            conn = conexionDB.conectar();
            conn.setAutoCommit(false);

            // Actualizar estado de la consulta a FINALIZADA
            String sqlConsulta = """
                UPDATE consultas 
                SET ESTADO = 'FINALIZADA', FECHA_HORA_FIN = NOW()
                WHERE ID_CONSULTA = ?
                """;

            PreparedStatement psConsulta = conn.prepareStatement(sqlConsulta);
            psConsulta.setInt(1, idConsulta);
            int filas = psConsulta.executeUpdate();

            if (filas == 0) {
                conn.rollback();
                return "ERROR|No se encontró la consulta";
            }

            // Registrar en historial
            registrarHistorialConConexion(conn, idConsulta, "FINALIZACION", "Consulta finalizada");

            conn.commit();
            return "OK|Consulta finalizada exitosamente";

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
            return "ERROR|Error al finalizar la consulta: " + e.getMessage();
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // === EDITAR CONSULTA ===
    public String editarConsulta(Consulta c) {
        String sql = """
            UPDATE consultas SET
                MOTIVO = ?, PESO = ?, temperatura = ?, frecuencia_cardiaca = ?,
                frecuencia_respiratoria = ?, mucosas = ?, cap_reflejo = ?,
                condicion_corporal = ?, OBSERVACIONES = ?
            WHERE ID_CONSULTA = ?
            """;
        try (Connection conn = conexionDB.conectar(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, c.getMotivo());
            ps.setBigDecimal(2, c.getPeso());
            ps.setBigDecimal(3, c.getTemperatura());
            ps.setObject(4, c.getFrecuenciaCardiaca());
            ps.setObject(5, c.getFrecuenciaRespiratoria());
            ps.setString(6, c.getMucosas());
            ps.setString(7, c.getCapReflejo());
            ps.setString(8, c.getCondicionCorporal());
            ps.setString(9, c.getObservaciones());
            ps.setInt(10, c.getIdConsulta());
            int filas = ps.executeUpdate();
            if (filas > 0) {
                registrarHistorial(c.getIdConsulta(), "EDICION", "Campos actualizados");
                return "OK|Consulta actualizada exitosamente";
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "ERROR|No se pudo actualizar la consulta";
    }

    // === ANULAR CONSULTA (SIN ANULAR LA FICHA) ===
    public String anularConsulta(int idConsulta, String motivo) {
        if (tieneDerivados(idConsulta)) {
            return "ERROR|No se puede anular: la consulta ya tiene diagnósticos, recetas u otros registros asociados.";
        }

        Connection conn = null;
        try {
            conn = conexionDB.conectar();
            conn.setAutoCommit(false);

            // Solo anular la consulta (LA FICHA PERMANECE ACTIVA)
            String sqlConsulta = "UPDATE consultas SET ESTADO = 'ANULADA' WHERE ID_CONSULTA = ?";
            PreparedStatement psConsulta = conn.prepareStatement(sqlConsulta);
            psConsulta.setInt(1, idConsulta);
            int filas = psConsulta.executeUpdate();

            if (filas == 0) {
                conn.rollback();
                return "ERROR|No se encontró la consulta";
            }

            // Registrar en historial
            registrarHistorialConConexion(conn, idConsulta, "ANULACION", motivo);

            conn.commit();
            return "OK|Consulta anulada exitosamente";

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
            return "ERROR|Error al anular la consulta";
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // === VERIFICAR SI TIENE DERIVADOS ===
    public boolean tieneDerivados(int idConsulta) {
        String sql = """
            SELECT COUNT(*) FROM (
                SELECT ID_DIAGNOSTICO FROM diagnosticos WHERE ID_CONSULTA = ? UNION ALL
                SELECT ID_RECETA FROM recetas WHERE ID_CONSULTA = ? UNION ALL
                SELECT ID_PROCEDIMIENTO FROM procedimientos WHERE ID_CONSULTA = ? UNION ALL
                SELECT ID_TRATAMIENTO FROM tratamientos WHERE ID_CONSULTA = ?
            ) AS derivados
            """;
        try (Connection conn = conexionDB.conectar(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idConsulta);
            ps.setInt(2, idConsulta);
            ps.setInt(3, idConsulta);
            ps.setInt(4, idConsulta);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // === OBTENER CONSULTA POR ID ===
    public Consulta obtenerConsultaPorId(int idConsulta) {
        String sql = """
            SELECT c.*, m.NOMBRE AS mascota, cl.NOMBRE AS cliente 
            FROM consultas c
            JOIN mascotas m ON c.ID_MASCOTA = m.ID_MASCOTA
            JOIN clientes cl ON m.ID_CLIENTE = cl.ID_CLIENTE
            WHERE c.ID_CONSULTA = ?
            """;
        try (Connection conn = conexionDB.conectar(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idConsulta);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Consulta c = new Consulta();
                c.setIdConsulta(rs.getInt("ID_CONSULTA"));
                c.setIdCita(rs.getInt("ID_CITA"));
                c.setIdVeterinario(rs.getInt("ID_VETERINARIO"));
                c.setIdMascota(rs.getInt("ID_MASCOTA"));
                c.setMotivo(rs.getString("MOTIVO"));
                c.setPeso(rs.getBigDecimal("PESO"));
                c.setTemperatura(rs.getBigDecimal("temperatura"));
                c.setFrecuenciaCardiaca((Integer) rs.getObject("frecuencia_cardiaca"));
                c.setFrecuenciaRespiratoria((Integer) rs.getObject("frecuencia_respiratoria"));
                c.setMucosas(rs.getString("mucosas"));
                c.setCapReflejo(rs.getString("cap_reflejo"));
                c.setCondicionCorporal(rs.getString("condicion_corporal"));
                c.setObservaciones(rs.getString("OBSERVACIONES"));
                c.setEstado(rs.getString("ESTADO"));
                return c;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // === BUSCAR HISTORIAL DE CONSULTAS POR MASCOTA/CLIENTE ===
    public List<Consulta> buscarHistorial(String texto) {
        List<Consulta> lista = new ArrayList<>();
        String sql = """
            String sql = \"""
                SELECT c.ID_CONSULTA, c.FECHA_HORA_INICIO, c.MOTIVO, c.ESTADO, c.ID_MASCOTA,
            m.NOMBRE AS mascota, cl.NOMBRE AS cliente, v.NOMBRE AS veterinario
            FROM consultas c
            JOIN mascotas m ON c.ID_MASCOTA = m.ID_MASCOTA
            JOIN clientes cl ON m.ID_CLIENTE = cl.ID_CLIENTE
            JOIN veterinarios v ON c.ID_VETERINARIO = v.ID_VETERINARIO
            WHERE (m.NOMBRE LIKE ? OR cl.NOMBRE LIKE ?)
              AND c.ESTADO IN ('FINALIZADA', 'EN_CURSO')
            ORDER BY c.FECHA_HORA_INICIO DESC
            """;
        try (Connection conn = conexionDB.conectar(); PreparedStatement ps = conn.prepareStatement(sql)) {
            String patron = "%" + texto + "%";
            ps.setString(1, patron);
            ps.setString(2, patron);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Consulta c = new Consulta();
                c.setIdConsulta(rs.getInt("ID_CONSULTA"));
                c.setFechaConsulta(rs.getString("FECHA_HORA_INICIO"));
                c.setMotivo(rs.getString("MOTIVO"));
                c.setEstado(rs.getString("ESTADO"));
                c.setIdMascota(rs.getInt("ID_MASCOTA"));
                lista.add(c);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    // === OBTENER CITAS DEL DÍA (SIN CONSULTA INICIADA) ===
    // === OBTENER CITAS DEL DÍA (SIN CONSULTA INICIADA) ===
    public List<CitaResumen> obtenerCitasHoy(int idUsuario) {
        List<CitaResumen> citas = new ArrayList<>();
        String sql = """
        SELECT c.ID_CITA, c.ID_MASCOTA, m.NOMBRE AS mascota, cl.NOMBRE AS cliente,
               s.FECHA_HORA_INICIO, s.FECHA_HORA_FIN
        FROM citas c
        JOIN agenda_slots s ON c.ID_SLOT = s.ID_SLOT
        JOIN mascotas m ON c.ID_MASCOTA = m.ID_MASCOTA
        JOIN clientes cl ON m.ID_CLIENTE = cl.ID_CLIENTE
        JOIN veterinarios v ON c.ID_VETERINARIO = v.ID_VETERINARIO
        LEFT JOIN consultas cons ON c.ID_CITA = cons.ID_CITA
        WHERE v.ID_USUARIO = ?  -- ← CAMBIO AQUÍ: de ID_VETERINARIO a ID_USUARIO
          AND DATE(s.FECHA_HORA_INICIO) = CURDATE()
          AND c.ESTADO IN ('RESERVADA', 'CONFIRMADA')
          AND cons.ID_CONSULTA IS NULL
        ORDER BY s.FECHA_HORA_INICIO
        """;
        try (Connection conn = conexionDB.conectar(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idUsuario); // Pasa el ID_USUARIO
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                CitaResumen cr = new CitaResumen();
                cr.setIdCita(rs.getInt("ID_CITA"));
                cr.setIdMascota(rs.getInt("ID_MASCOTA"));
                cr.setNombreMascota(rs.getString("mascota"));
                cr.setNombreCliente(rs.getString("cliente"));
                cr.setHoraInicio(rs.getString("FECHA_HORA_INICIO").split(" ")[1].substring(0, 5));
                citas.add(cr);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return citas;
    }

    // === REGISTRAR EN HISTORIAL (CON CONEXIÓN EXISTENTE) ===
    private void registrarHistorialConConexion(Connection conn, int idConsulta, String tipoAccion, String detalles) throws SQLException {
        String sql = "INSERT INTO historial_consultas (ID_CONSULTA, TIPO_ACCION, DETALLES) VALUES (?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idConsulta);
            ps.setString(2, tipoAccion);
            ps.setString(3, detalles);
            ps.executeUpdate();
        }
    }

    // === REGISTRAR EN HISTORIAL (STANDALONE) ===
    private void registrarHistorial(int idConsulta, String tipoAccion, String detalles) {
        String sql = "INSERT INTO historial_consultas (ID_CONSULTA, TIPO_ACCION, DETALLES) VALUES (?, ?, ?)";
        try (Connection conn = conexionDB.conectar(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idConsulta);
            ps.setString(2, tipoAccion);
            ps.setString(3, detalles);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // === OBTENER CONSULTAS EN CURSO ===
    public List<Consulta> obtenerConsultasEnCurso(int idUsuario) {
        List<Consulta> consultas = new ArrayList<>();
        String sql = """
        SELECT c.ID_CONSULTA, c.ID_MASCOTA, c.MOTIVO, c.PESO, c.temperatura,
               c.FECHA_HORA_INICIO, c.ESTADO,
               m.NOMBRE AS mascota, cl.NOMBRE AS cliente
        FROM consultas c
        JOIN mascotas m ON c.ID_MASCOTA = m.ID_MASCOTA
        JOIN clientes cl ON m.ID_CLIENTE = cl.ID_CLIENTE
        JOIN veterinarios v ON c.ID_VETERINARIO = v.ID_VETERINARIO
        WHERE v.ID_USUARIO = ? AND c.ESTADO = 'EN_CURSO'
        ORDER BY c.FECHA_HORA_INICIO DESC
        """;
        try (Connection conn = conexionDB.conectar(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Consulta consulta = new Consulta();
                consulta.setIdConsulta(rs.getInt("ID_CONSULTA"));
                consulta.setIdMascota(rs.getInt("ID_MASCOTA"));
                consulta.setMotivo(rs.getString("MOTIVO"));
                consulta.setPeso(rs.getBigDecimal("PESO"));
                consulta.setTemperatura(rs.getBigDecimal("temperatura"));
                consulta.setEstado(rs.getString("ESTADO"));
                consulta.setObservaciones(rs.getString("mascota") + " - " + rs.getString("cliente"));
                consultas.add(consulta);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return consultas;
    }

    // === CLASE AUXILIAR PARA RESUMEN DE CITAS ===
    public static class CitaResumen {

        private int idCita;
        private int idMascota;
        private String nombreMascota;
        private String nombreCliente;
        private String horaInicio;

        // Getters y Setters
        public int getIdCita() {
            return idCita;
        }

        public void setIdCita(int idCita) {
            this.idCita = idCita;
        }

        public int getIdMascota() {
            return idMascota;
        }

        public void setIdMascota(int idMascota) {
            this.idMascota = idMascota;
        }

        public String getNombreMascota() {
            return nombreMascota;
        }

        public void setNombreMascota(String nombreMascota) {
            this.nombreMascota = nombreMascota;
        }

        public String getNombreCliente() {
            return nombreCliente;
        }

        public void setNombreCliente(String nombreCliente) {
            this.nombreCliente = nombreCliente;
        }

        public String getHoraInicio() {
            return horaInicio;
        }

        public void setHoraInicio(String horaInicio) {
            this.horaInicio = horaInicio;
        }
    }
}

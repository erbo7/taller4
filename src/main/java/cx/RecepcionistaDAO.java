package cx;

import cx.conexion;
import modelo.Cita;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import modelo.RecepcionistaStats;

public class RecepcionistaDAO {

    private conexion cn = new conexion();

    /* ==========================
       CONTADORES DASHBOARD
       ========================== */
    public int contarCitasHoy() {
        System.out.println("=== DEBUG contarCitasHoy() INICIANDO ===");

        String sql = "SELECT COUNT(*) FROM citas c JOIN agenda_slots s ON c.ID_SLOT = s.ID_SLOT WHERE DATE(s.FECHA_HORA_INICIO) = CURDATE()";
        System.out.println("SQL: " + sql);

        try (Connection con = cn.conectar()) {
            System.out.println("Conexión establecida");

            try (PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    int resultado = rs.getInt(1);
                    System.out.println("DEBUG contarCitasHoy() RESULTADO = " + resultado);
                    return resultado;
                } else {
                    System.out.println("DEBUG contarCitasHoy() NO HAY RESULTADOS");
                    return 0;
                }

            } catch (SQLException e) {
                System.err.println("ERROR en executeQuery contarCitasHoy: " + e.getMessage());
                e.printStackTrace();
                return 0;
            }
        } catch (SQLException e) {
            System.err.println("ERROR en conexión contarCitasHoy: " + e.getMessage());
            e.printStackTrace();
            return 0;
        }
    }

    public int contarRecordatoriosPendientes() {
        System.out.println("=== DEBUG contarRecordatoriosPendientes() INICIANDO ===");

        String sql = "SELECT COUNT(*) FROM recordatorios WHERE estado_envio = 'PENDIENTE'";
        System.out.println("SQL: " + sql);

        try (Connection con = cn.conectar()) {
            System.out.println("Conexión establecida");

            try (PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    int resultado = rs.getInt(1);
                    System.out.println("DEBUG contarRecordatoriosPendientes() RESULTADO = " + resultado);
                    return resultado;
                } else {
                    System.out.println("DEBUG contarRecordatoriosPendientes() NO HAY RESULTADOS");
                    return 0;
                }

            } catch (SQLException e) {
                System.err.println("ERROR en executeQuery contarRecordatoriosPendientes: " + e.getMessage());
                e.printStackTrace();
                return 0;
            }
        } catch (SQLException e) {
            System.err.println("ERROR en conexión contarRecordatoriosPendientes: " + e.getMessage());
            e.printStackTrace();
            return 0;
        }
    }

    public int contarClientesAtendidosHoy() {
        System.out.println("=== DEBUG contarClientesAtendidosHoy() INICIANDO ===");

        String sql = "SELECT COUNT(DISTINCT c.ID_CLIENTE) "
                + "FROM citas c "
                + "JOIN agenda_slots s ON c.ID_SLOT = s.ID_SLOT "
                + "WHERE DATE(s.FECHA_HORA_INICIO) = CURDATE() "
                + // ✅ Solo hoy
                "AND c.ESTADO = 'FINALIZADA'";

        System.out.println("SQL: " + sql);

        try (Connection con = cn.conectar()) {
            System.out.println("Conexión establecida");

            try (PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    int resultado = rs.getInt(1);
                    System.out.println("DEBUG contarClientesAtendidosHoy() RESULTADO = " + resultado);
                    return resultado;
                } else {
                    System.out.println("DEBUG contarClientesAtendidosHoy() NO HAY RESULTADOS");
                    return 0;
                }

            } catch (SQLException e) {
                System.err.println("ERROR en executeQuery contarClientesAtendidosHoy: " + e.getMessage());
                e.printStackTrace();
                return 0;
            }
        } catch (SQLException e) {
            System.err.println("ERROR en conexión contarClientesAtendidosHoy: " + e.getMessage());
            e.printStackTrace();
            return 0;
        }
    }

    /* ==========================
       LISTADO DE CITAS DE HOY
       ========================== */
    public List<Cita> listarCitasHoy() {
        System.out.println("=== DEBUG listarCitasHoy() INICIANDO ===");

        List<Cita> lista = new ArrayList<>();

        String sql = "SELECT c.ID_CITA, c.ESTADO, cl.NOMBRE AS nombre_cliente, m.NOMBRE AS nombre_mascota, v.NOMBRE AS nombre_veterinario, DATE(s.FECHA_HORA_INICIO) AS fecha_cita, TIME(s.FECHA_HORA_INICIO) AS hora_cita FROM citas c JOIN agenda_slots s ON c.ID_SLOT = s.ID_SLOT JOIN clientes cl ON c.ID_CLIENTE = cl.ID_CLIENTE JOIN mascotas m ON c.ID_MASCOTA = m.ID_MASCOTA JOIN veterinarios v ON c.ID_VETERINARIO = v.ID_VETERINARIO WHERE DATE(s.FECHA_HORA_INICIO) = CURDATE() ORDER BY s.FECHA_HORA_INICIO";

        System.out.println("SQL: " + sql);

        try (Connection con = cn.conectar()) {
            System.out.println("Conexión establecida");

            try (PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

                System.out.println("DEBUG listarCitasHoy() - Ejecutando consulta...");
                int contador = 0;

                while (rs.next()) {
                    Cita c = new Cita();
                    c.setIdCita(rs.getInt("ID_CITA"));
                    c.setEstado(rs.getString("ESTADO"));
                    c.setNombreCliente(rs.getString("nombre_cliente"));
                    c.setNombreMascota(rs.getString("nombre_mascota"));
                    c.setNombreVeterinario(rs.getString("nombre_veterinario"));
                    c.setFechaCita(rs.getString("fecha_cita"));
                    c.setHoraCita(rs.getString("hora_cita"));

                    lista.add(c);
                    contador++;

                    System.out.println("DEBUG Cita " + contador + ": "
                            + c.getNombreCliente() + " - "
                            + c.getHoraCita() + " - "
                            + c.getEstado());
                }

                System.out.println("DEBUG Total citas encontradas: " + contador);

            } catch (SQLException e) {
                System.err.println("ERROR en executeQuery listarCitasHoy: " + e.getMessage());
                e.printStackTrace();
            }
        } catch (SQLException e) {
            System.err.println("ERROR en conexión listarCitasHoy: " + e.getMessage());
            e.printStackTrace();
        }

        return lista;
    }

    // =====================================================================
// MÉTODOS ADICIONALES PARA RecepcionistaDAO.java
// Agregar estos métodos a tu clase RecepcionistaDAO existente
// =====================================================================
    /**
     * Obtiene estadísticas completas del dashboard
     */
    public RecepcionistaStats obtenerEstadisticasCompletas() {
        System.out.println("=== DEBUG obtenerEstadisticasCompletas() INICIANDO ===");

        RecepcionistaStats stats = new RecepcionistaStats();

        try (Connection con = cn.conectar()) {
            System.out.println("Conexión establecida");

            // 1. Citas de hoy (ya existe, reutilizamos)
            stats.setCitasHoy(contarCitasHoy());

            // 2. Citas de esta semana
            String sqlSemana = "SELECT COUNT(*) FROM citas c "
                    + "JOIN agenda_slots s ON c.ID_SLOT = s.ID_SLOT "
                    + "WHERE YEARWEEK(DATE(s.FECHA_HORA_INICIO), 1) = YEARWEEK(CURDATE(), 1) "
                    + "AND c.ESTADO NOT IN ('ANULADA')";

            try (PreparedStatement ps = con.prepareStatement(sqlSemana); ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    stats.setCitasSemana(rs.getInt(1));
                    System.out.println("DEBUG Citas semana: " + stats.getCitasSemana());
                }
            }

            // 3. Recordatorios pendientes (ya existe, reutilizamos)
            stats.setRecordatoriosPendientes(contarRecordatoriosPendientes());

            // 4. Clientes atendidos hoy (ya existe, reutilizamos)
            stats.setClientesAtendidosHoy(contarClientesAtendidosHoy());

            // 5. Clientes atendidos este mes
            String sqlMes = "SELECT COUNT(DISTINCT c.ID_CLIENTE) FROM citas c "
                    + "JOIN agenda_slots s ON c.ID_SLOT = s.ID_SLOT "
                    + "WHERE MONTH(DATE(s.FECHA_HORA_INICIO)) = MONTH(CURDATE()) "
                    + "AND YEAR(DATE(s.FECHA_HORA_INICIO)) = YEAR(CURDATE()) "
                    + "AND c.ESTADO = 'COMPLETADA'";

            try (PreparedStatement ps = con.prepareStatement(sqlMes); ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    stats.setClientesAtendidosMes(rs.getInt(1));
                    System.out.println("DEBUG Clientes mes: " + stats.getClientesAtendidosMes());
                }
            }

            // 6. Citas pendientes de confirmación
            String sqlPendientes = "SELECT COUNT(*) FROM citas WHERE ESTADO = 'RESERVADA'";

            try (PreparedStatement ps = con.prepareStatement(sqlPendientes); ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    stats.setCitasPendientes(rs.getInt(1));
                    System.out.println("DEBUG Citas pendientes: " + stats.getCitasPendientes());
                }
            }

            // 7. Citas confirmadas
            String sqlConfirmadas = "SELECT COUNT(*) FROM citas WHERE ESTADO = 'CONFIRMADA'";

            try (PreparedStatement ps = con.prepareStatement(sqlConfirmadas); ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    stats.setCitasConfirmadas(rs.getInt(1));
                    System.out.println("DEBUG Citas confirmadas: " + stats.getCitasConfirmadas());
                }
            }

            System.out.println("DEBUG Estadísticas completas: " + stats);

        } catch (SQLException e) {
            System.err.println("ERROR en obtenerEstadisticasCompletas: " + e.getMessage());
            e.printStackTrace();
        }

        return stats;
    }

    /**
     * Lista las próximas citas de la semana (excluyendo hoy)
     */
    public List<Cita> listarProximasCitasSemana() {
        System.out.println("=== DEBUG listarProximasCitasSemana() INICIANDO ===");

        List<Cita> lista = new ArrayList<>();

        String sql = "SELECT c.ID_CITA, c.ESTADO, c.MOTIVO, "
                + "cl.NOMBRE AS nombre_cliente, cl.TELEFONO AS telefono_cliente, "
                + "m.NOMBRE AS nombre_mascota, "
                + "v.NOMBRE AS nombre_veterinario, "
                + "DATE(s.FECHA_HORA_INICIO) AS fecha_cita, "
                + "TIME(s.FECHA_HORA_INICIO) AS hora_cita "
                + "FROM citas c "
                + "JOIN agenda_slots s ON c.ID_SLOT = s.ID_SLOT "
                + "JOIN clientes cl ON c.ID_CLIENTE = cl.ID_CLIENTE "
                + "JOIN mascotas m ON c.ID_MASCOTA = m.ID_MASCOTA "
                + "JOIN veterinarios v ON c.ID_VETERINARIO = v.ID_VETERINARIO "
                + "WHERE DATE(s.FECHA_HORA_INICIO) > CURDATE() "
                + "AND DATE(s.FECHA_HORA_INICIO) <= DATE_ADD(CURDATE(), INTERVAL 7 DAY) "
                + "AND c.ESTADO NOT IN ('ANULADA') "
                + "ORDER BY s.FECHA_HORA_INICIO ASC "
                + "LIMIT 5";

        System.out.println("SQL: " + sql);

        try (Connection con = cn.conectar()) {
            System.out.println("Conexión establecida");

            try (PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

                System.out.println("DEBUG listarProximasCitasSemana() - Ejecutando consulta...");
                int contador = 0;

                while (rs.next()) {
                    Cita c = new Cita();
                    c.setIdCita(rs.getInt("ID_CITA"));
                    c.setEstado(rs.getString("ESTADO"));
                    c.setMotivo(rs.getString("MOTIVO"));
                    c.setNombreCliente(rs.getString("nombre_cliente"));
                    c.setNombreMascota(rs.getString("nombre_mascota"));
                    c.setNombreVeterinario(rs.getString("nombre_veterinario"));
                    c.setFechaCita(rs.getString("fecha_cita"));
                    c.setHoraCita(rs.getString("hora_cita"));

                    lista.add(c);
                    contador++;

                    System.out.println("DEBUG Cita " + contador + ": "
                            + c.getNombreCliente() + " - "
                            + c.getFechaCita() + " " + c.getHoraCita());
                }

                System.out.println("DEBUG Total próximas citas encontradas: " + contador);

            } catch (SQLException e) {
                System.err.println("ERROR en executeQuery listarProximasCitasSemana: " + e.getMessage());
                e.printStackTrace();
            }
        } catch (SQLException e) {
            System.err.println("ERROR en conexión listarProximasCitasSemana: " + e.getMessage());
            e.printStackTrace();
        }

        return lista;
    }

    /**
     * Lista las citas que necesitan confirmación (estado RESERVADA)
     */
    public List<Cita> listarCitasPorConfirmar() {
        System.out.println("=== DEBUG listarCitasPorConfirmar() INICIANDO ===");

        List<Cita> lista = new ArrayList<>();

        String sql = "SELECT c.ID_CITA, c.ESTADO, c.MOTIVO, "
                + "cl.NOMBRE AS nombre_cliente, cl.TELEFONO AS telefono_cliente, "
                + "m.NOMBRE AS nombre_mascota, "
                + "v.NOMBRE AS nombre_veterinario, "
                + "DATE(s.FECHA_HORA_INICIO) AS fecha_cita, "
                + "TIME(s.FECHA_HORA_INICIO) AS hora_cita "
                + "FROM citas c "
                + "JOIN agenda_slots s ON c.ID_SLOT = s.ID_SLOT "
                + "JOIN clientes cl ON c.ID_CLIENTE = cl.ID_CLIENTE "
                + "JOIN mascotas m ON c.ID_MASCOTA = m.ID_MASCOTA "
                + "JOIN veterinarios v ON c.ID_VETERINARIO = v.ID_VETERINARIO "
                + "WHERE c.ESTADO = 'RESERVADA' "
                + "AND DATE(s.FECHA_HORA_INICIO) >= CURDATE() "
                + "ORDER BY s.FECHA_HORA_INICIO ASC "
                + "LIMIT 10";

        System.out.println("SQL: " + sql);

        try (Connection con = cn.conectar()) {
            System.out.println("Conexión establecida");

            try (PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

                System.out.println("DEBUG listarCitasPorConfirmar() - Ejecutando consulta...");
                int contador = 0;

                while (rs.next()) {
                    Cita c = new Cita();
                    c.setIdCita(rs.getInt("ID_CITA"));
                    c.setEstado(rs.getString("ESTADO"));
                    c.setMotivo(rs.getString("MOTIVO"));
                    c.setNombreCliente(rs.getString("nombre_cliente"));
                    c.setNombreMascota(rs.getString("nombre_mascota"));
                    c.setNombreVeterinario(rs.getString("nombre_veterinario"));
                    c.setFechaCita(rs.getString("fecha_cita"));
                    c.setHoraCita(rs.getString("hora_cita"));

                    lista.add(c);
                    contador++;

                    System.out.println("DEBUG Cita por confirmar " + contador + ": "
                            + c.getNombreCliente() + " - "
                            + c.getFechaCita() + " " + c.getHoraCita());
                }

                System.out.println("DEBUG Total citas por confirmar: " + contador);

            } catch (SQLException e) {
                System.err.println("ERROR en executeQuery listarCitasPorConfirmar: " + e.getMessage());
                e.printStackTrace();
            }
        } catch (SQLException e) {
            System.err.println("ERROR en conexión listarCitasPorConfirmar: " + e.getMessage());
            e.printStackTrace();
        }

        return lista;
    }

// =====================================================================
// IMPORTS QUE NECESITAS AGREGAR AL INICIO DE RecepcionistaDAO.java
// =====================================================================
// import modelo.RecepcionistaStats;
// (Los demás imports ya los tienes)
}

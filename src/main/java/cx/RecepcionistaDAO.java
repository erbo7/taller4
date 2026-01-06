package cx;

import cx.conexion;
import modelo.Cita;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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
            
            try (PreparedStatement ps = con.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {

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
            
            try (PreparedStatement ps = con.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {

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
        
        String sql = "SELECT COUNT(DISTINCT c.ID_CLIENTE) FROM citas c JOIN agenda_slots s ON c.ID_SLOT = s.ID_SLOT WHERE DATE(s.FECHA_HORA_INICIO) = CURDATE() AND c.ESTADO = 'COMPLETADA'";
        System.out.println("SQL: " + sql);
        
        try (Connection con = cn.conectar()) {
            System.out.println("Conexión establecida");
            
            try (PreparedStatement ps = con.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {

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
            
            try (PreparedStatement ps = con.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {

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
                    
                    System.out.println("DEBUG Cita " + contador + ": " + 
                        c.getNombreCliente() + " - " + 
                        c.getHoraCita() + " - " + 
                        c.getEstado());
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
}
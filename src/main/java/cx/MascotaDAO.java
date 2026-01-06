package cx;

import modelo.Mascota;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MascotaDAO {

    private final conexion cx = new conexion();

    public boolean crear(Mascota mascota) {
        String sql = "INSERT INTO mascotas (ID_CLIENTE, NOMBRE, ID_RAZA, EDAD, SEXO) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = cx.conectar(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, mascota.getIdCliente());
            ps.setString(2, mascota.getNombre());
            ps.setInt(3, mascota.getIdRaza()); // ← CORREGIDO: ID_RAZA
            ps.setObject(4, mascota.getEdad()); // ← CORREGIDO: EDAD (puede ser null)
            ps.setString(5, mascota.getSexo()); // ← CORREGIDO: SEXO

            System.out.println("🔍 DAO - Insertando mascota:");
            System.out.println("  ID_CLIENTE: " + mascota.getIdCliente());
            System.out.println("  NOMBRE: " + mascota.getNombre());
            System.out.println("  ID_RAZA: " + mascota.getIdRaza());
            System.out.println("  EDAD: " + mascota.getEdad());
            System.out.println("  SEXO: " + mascota.getSexo());

            int resultado = ps.executeUpdate();
            System.out.println("✅ Filas afectadas: " + resultado);
            return resultado > 0;
        } catch (SQLException e) {
            System.err.println("❌ Error SQL al crear mascota: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Método para obtener mascotas por cliente (mejorado)
    public List<Mascota> porCliente(int idCliente) {
        List<Mascota> mascotas = new ArrayList<>();
        String sql = """
            SELECT m.*, r.nombre as raza, e.nombre as especie 
            FROM mascotas m
            LEFT JOIN razas r ON m.id_raza = r.id_raza
            LEFT JOIN especies e ON r.id_especie = e.id_especie
            WHERE m.ID_CLIENTE = ?
            """;
        try (Connection conn = cx.conectar(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idCliente);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Mascota mascota = new Mascota();
                    mascota.setIdMascota(rs.getInt("ID_MASCOTA"));
                    mascota.setIdCliente(rs.getInt("ID_CLIENTE"));
                    mascota.setNombre(rs.getString("NOMBRE"));
                    mascota.setIdRaza(rs.getInt("ID_RAZA"));
                    mascota.setEdad(rs.getObject("EDAD") != null ? rs.getInt("EDAD") : null);
                    mascota.setSexo(rs.getString("SEXO"));
                    mascota.setEspecie(rs.getString("especie")); // Para mostrar en lista
                    mascotas.add(mascota);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener mascotas: " + e.getMessage());
            e.printStackTrace();
        }
        return mascotas;
    }
    
    
    
    
    public List<Mascota> obtenerPorCliente(int idCliente) {
        List<Mascota> mascotas = new ArrayList<>();
        String sql = "SELECT m.ID_MASCOTA, m.NOMBRE, m.EDAD, m.SEXO, " +
                    "e.NOMBRE as especie, r.NOMBRE as raza, m.ID_RAZA " +
                    "FROM mascotas m " +
                    "INNER JOIN razas r ON m.ID_RAZA = r.ID_RAZA " +
                    "INNER JOIN especies e ON r.ID_ESPECIE = e.ID_ESPECIE " +
                    "WHERE m.ID_CLIENTE = ? AND m.ACTIVO = 1 " +
                    "ORDER BY m.NOMBRE";
        
        try (Connection conn = cx.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, idCliente);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Mascota mascota = new Mascota();
                    mascota.setIdMascota(rs.getInt("ID_MASCOTA"));
                    mascota.setNombre(rs.getString("NOMBRE"));
                    mascota.setEdad(rs.getInt("EDAD"));
                    mascota.setSexo(rs.getString("SEXO"));
                    mascota.setIdRaza(rs.getInt("ID_RAZA"));
                    // Puedes agregar más campos según tu modelo Mascota
                    mascotas.add(mascota);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener mascotas: " + e.getMessage());
        }
        return mascotas;
    }
    
    public Mascota obtenerPorId(int idMascota) {
        String sql = "SELECT m.*, fm.ID_FICHA " +
                    "FROM mascotas m " +
                    "LEFT JOIN ficha_mascota fm ON m.ID_MASCOTA = fm.ID_MASCOTA " +
                    "WHERE m.ID_MASCOTA = ?";
        
        try (Connection conn = cx.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, idMascota);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Mascota mascota = new Mascota();
                    mascota.setIdMascota(rs.getInt("ID_MASCOTA"));
                    mascota.setNombre(rs.getString("NOMBRE"));
                    // Agrega más campos según necesites
                    return mascota;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener mascota: " + e.getMessage());
        }
        return null;
    }
}

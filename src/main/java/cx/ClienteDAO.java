package cx;

import modelo.Cliente;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClienteDAO {

    private final conexion cx = new conexion();

    public boolean crear(Cliente cliente) {
        String sql = """
        INSERT INTO clientes
        (NOMBRE, TELEFONO, EMAIL, DIRECCION, TIPO_DOCUMENTO, NRO_DOCUMENTO)
        VALUES (?, ?, ?, ?, ?, ?)
    """;

        try (Connection conn = cx.conectar(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, cliente.getNombre());
            ps.setString(2, cliente.getTelefono());
            ps.setString(3, cliente.getEmail());
            ps.setString(4, cliente.getDireccion());
            ps.setString(5, cliente.getTipo_documento()); // 👈 CLAVE
            ps.setString(6, cliente.getNro_documento());  // 👈 CLAVE

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Cliente> listar() {
        List<Cliente> clientes = new ArrayList<>();
        String sql = """
        SELECT c.*, td.nombre as nombre_tipo_documento 
        FROM clientes c
        LEFT JOIN tipo_documento td ON c.tipo_documento = td.id_tipo_documento
        """;
        try (Connection conn = cx.conectar(); Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Cliente cliente = new Cliente(
                        rs.getInt("ID_CLIENTE"),
                        rs.getString("NOMBRE"),
                        rs.getString("TELEFONO"),
                        rs.getString("EMAIL"),
                        rs.getString("DIRECCION"),
                        rs.getString("tipo_documento"), // ID
                        rs.getString("NRO_DOCUMENTO")
                );
                // Podrías agregar un campo extra en Cliente para el nombre del tipo
                clientes.add(cliente);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return clientes;
    }
    
    
    public List<Cliente> obtenerTodos() {
        List<Cliente> clientes = new ArrayList<>();
        String sql = "SELECT ID_CLIENTE, NOMBRE, TELEFONO, EMAIL FROM clientes ORDER BY NOMBRE";
        
        try (Connection conn = cx.conectar();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            
            while (rs.next()) {
                Cliente cliente = new Cliente();
                cliente.setIdCliente(rs.getInt("ID_CLIENTE"));
                cliente.setNombre(rs.getString("NOMBRE"));
                cliente.setTelefono(rs.getString("TELEFONO"));
                cliente.setEmail(rs.getString("EMAIL"));
                clientes.add(cliente);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener clientes: " + e.getMessage());
        }
        return clientes;
    }
    
    
}

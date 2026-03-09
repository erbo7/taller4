package cx;

import modelo.Veterinario;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VeterinarioDAO {

    private final conexion cx = new conexion();

    public List<Veterinario> obtenerTodos() {
        List<Veterinario> veterinarios = new ArrayList<>();
        String sql = "SELECT ID_VETERINARIO, NOMBRE FROM veterinarios WHERE ESTADO = 'ACTIVO' ORDER BY NOMBRE";
        try (Connection conn = cx.conectar();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Veterinario vet = new Veterinario();
                vet.setIdVeterinario(rs.getInt("ID_VETERINARIO"));
                vet.setNombre(rs.getString("NOMBRE"));
                veterinarios.add(vet);
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al obtener veterinarios: " + e.getMessage());
            e.printStackTrace();
        }
        return veterinarios;
    }

    // ✅ Nuevo: Obtener veterinario por ID
    public Veterinario obtenerPorId(int idVeterinario) {
        String sql = "SELECT * FROM veterinarios WHERE ID_VETERINARIO = ? AND ESTADO = 'ACTIVO'";
        
        try (Connection conn = cx.conectar(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idVeterinario);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Veterinario vet = new Veterinario();
                    vet.setIdVeterinario(rs.getInt("ID_VETERINARIO"));
                    vet.setNombre(rs.getString("NOMBRE"));
                    vet.setMatricula(rs.getString("MATRICULA"));
                    vet.setTelefono(rs.getString("TELEFONO"));
                    vet.setEmail(rs.getString("EMAIL"));
                    vet.setEstado(rs.getString("ESTADO"));
                    vet.setIdEspecialidad(rs.getInt("ID_ESPECIALIDAD"));
                    return vet;
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al obtener veterinario por ID: " + e.getMessage());
        }
        return null;
    }
}
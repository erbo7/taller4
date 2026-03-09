package cx;

import modelo.Especialidad;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EspecialidadDAO {

    private final conexion cx = new conexion(); // ✅ instancia

    // Obtener todas las especialidades
    public List<Especialidad> obtenerTodas() {
        List<Especialidad> especialidades = new ArrayList<>();
        String sql = "SELECT id_especialidad, nombre, descripcion FROM especialidades";
        try (Connection conn = cx.conectar(); // ✅
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Especialidad esp = new Especialidad(
                    rs.getInt("id_especialidad"),
                    rs.getString("nombre"),
                    rs.getString("descripcion")
                );
                especialidades.add(esp);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener especialidades: " + e.getMessage());
        }
        return especialidades;
    }

    // Obtener por ID
    public Especialidad obtenerPorId(int id) {
        String sql = "SELECT id_especialidad, nombre, descripcion FROM especialidades WHERE id_especialidad = ?";
        try (Connection conn = cx.conectar(); // ✅
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Especialidad(
                        rs.getInt("id_especialidad"),
                        rs.getString("nombre"),
                        rs.getString("descripcion")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener especialidad: " + e.getMessage());
        }
        return null;
    }
}
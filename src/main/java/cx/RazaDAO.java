package cx;

import modelo.Raza;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RazaDAO {
    private conexion cx = new conexion();

    public List<Raza> obtenerPorEspecie(int idEspecie) {
        List<Raza> razas = new ArrayList<>();
        String sql = "SELECT * FROM razas WHERE ID_ESPECIE = ? ORDER BY NOMBRE";
        try (Connection conn = cx.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idEspecie);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    razas.add(new Raza(
                        rs.getInt("ID_RAZA"),
                        rs.getInt("ID_ESPECIE"),
                        rs.getString("NOMBRE")
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener razas: " + e.getMessage());
            e.printStackTrace();
        }
        return razas;
    }
}
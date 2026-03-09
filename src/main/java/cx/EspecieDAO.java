package cx;

import modelo.Especie;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EspecieDAO {
    private conexion cx = new conexion();

    public List<Especie> obtenerTodas() {
        List<Especie> especies = new ArrayList<>();
        String sql = "SELECT * FROM especies ORDER BY NOMBRE";
        try (Connection conn = cx.conectar();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                especies.add(new Especie(
                    rs.getInt("ID_ESPECIE"),
                    rs.getString("NOMBRE")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener especies: " + e.getMessage());
            e.printStackTrace();
        }
        return especies;
    }
}
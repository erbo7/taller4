package cx;

import modelo.TipoDocumento;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TipoDocumentoDAO {

    private final conexion cx = new conexion(); // ✅ instancia

    // Obtener todos los tipos de documentos
    public List<TipoDocumento> obtenerTodos() {
        List<TipoDocumento> tipos = new ArrayList<>();
        String sql = "SELECT id_tipo_documento, nombre FROM tipo_documento";
        try (Connection conn = cx.conectar(); // ✅
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                TipoDocumento tipo = new TipoDocumento(
                    rs.getInt("id_tipo_documento"),
                    rs.getString("nombre")
                );
                tipos.add(tipo);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener tipos de documentos: " + e.getMessage());
        }
        return tipos;
    }

    // Obtener por ID
    public TipoDocumento obtenerPorId(int id) {
        String sql = "SELECT id_tipo_documento, nombre FROM tipo_documento WHERE id_tipo_documento = ?";
        try (Connection conn = cx.conectar(); // ✅
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new TipoDocumento(
                        rs.getInt("id_tipo_documento"),
                        rs.getString("nombre")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener tipo de documento: " + e.getMessage());
        }
        return null;
    }
}
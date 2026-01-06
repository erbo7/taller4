package cx;

import modelo.TipoDocumentoVeterinario;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TipoDocumentoVeterinarioDAO {

    private final conexion cx = new conexion();

    /**
     * Obtiene todos los tipos de documentos veterinarios activos
     */
    public List<TipoDocumentoVeterinario> obtenerTodos() {
        List<TipoDocumentoVeterinario> tipos = new ArrayList<>();
        String sql = "SELECT id_tipo_doc_vet, nombre, es_obligatorio, activo " +
                    "FROM tipo_documento_veterinario " +
                    "WHERE activo = 1 " +
                    "ORDER BY nombre";
        
        try (Connection conn = cx.conectar();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            
            while (rs.next()) {
                TipoDocumentoVeterinario tipo = new TipoDocumentoVeterinario(
                    rs.getInt("id_tipo_doc_vet"),
                    rs.getString("nombre"),
                    rs.getBoolean("es_obligatorio"),
                    rs.getBoolean("activo")
                );
                tipos.add(tipo);
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al obtener tipos de documentos: " + e.getMessage());
        }
        return tipos;
    }

    /**
     * Obtiene un tipo de documento por ID
     */
    public TipoDocumentoVeterinario obtenerPorId(int id) {
        String sql = "SELECT id_tipo_doc_vet, nombre, es_obligatorio, activo " +
                    "FROM tipo_documento_veterinario " +
                    "WHERE id_tipo_doc_vet = ?";
        
        try (Connection conn = cx.conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, id);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new TipoDocumentoVeterinario(
                        rs.getInt("id_tipo_doc_vet"),
                        rs.getString("nombre"),
                        rs.getBoolean("es_obligatorio"),
                        rs.getBoolean("activo")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al obtener tipo de documento: " + e.getMessage());
        }
        return null;
    }
}
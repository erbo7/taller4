package cx;

import modelo.Documento;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DocumentoDAO {

    private final conexion cx = new conexion();

    public boolean registrarDocumento(Documento doc, int creadoPor) {
        String sql = "{CALL sp_registrar_documento_veterinario(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";

        try (Connection conn = cx.conectar(); CallableStatement cs = conn.prepareCall(sql)) {

            cs.setInt(1, doc.getIdMascota());

            if (doc.getIdFicha() != null) {
                cs.setInt(2, doc.getIdFicha());
            } else {
                cs.setNull(2, Types.INTEGER);
            }

            cs.setInt(3, doc.getIdTipoDocVet());
            cs.setString(4, doc.getNombreArchivo());
            cs.setString(5, doc.getRutaArchivo());
            cs.setString(6, doc.getFormato());
            cs.setLong(7, doc.getTamanoBytes());
            cs.setDate(8, java.sql.Date.valueOf(doc.getFechaDocumento()));
            cs.setString(9, doc.getObservaciones());

            if (doc.getIdConsulta() != null) {
                cs.setInt(10, doc.getIdConsulta());
            } else {
                cs.setNull(10, Types.INTEGER);
            }

            cs.setInt(11, creadoPor);

            cs.execute();
            return true;

        } catch (SQLException e) {
            System.err.println("❌ Error al registrar documento: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Obtiene documentos activos de una mascota con información completa
     */
    public List<Documento> obtenerPorMascota(int idMascota) {
        List<Documento> documentos = new ArrayList<>();
        String sql = "SELECT d.ID_DOCUMENTO, d.ID_MASCOTA, d.ID_FICHA, d.id_tipo_doc_vet, "
                + "d.NOMBRE_ARCHIVO, d.RUTA_ARCHIVO, d.FORMATO, d.TAMANO_BYTES, d.TAMANO_KB, "
                + "d.FECHA_DOCUMENTO, d.OBSERVACIONES, d.ESTADO, d.CREADO_EN, d.ID_CONSULTA, "
                + "tdv.nombre AS tipo_nombre, tdv.es_obligatorio, "
                + "m.NOMBRE AS mascota_nombre, c.NOMBRE AS cliente_nombre "
                + "FROM documentos d "
                + "INNER JOIN tipo_documento_veterinario tdv ON d.id_tipo_doc_vet = tdv.id_tipo_doc_vet "
                + "INNER JOIN mascotas m ON d.ID_MASCOTA = m.ID_MASCOTA "
                + "INNER JOIN clientes c ON m.ID_CLIENTE = c.ID_CLIENTE "
                + "WHERE d.ID_MASCOTA = ? AND (d.ESTADO = 'ACTIVO' OR d.ESTADO IS NULL OR d.ESTADO = '') "
                + "ORDER BY d.FECHA_DOCUMENTO DESC, d.CREADO_EN DESC";

        try (Connection conn = cx.conectar(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idMascota);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Documento doc = new Documento();
                    doc.setIdDocumento(rs.getInt("ID_DOCUMENTO"));
                    doc.setIdMascota(rs.getInt("ID_MASCOTA"));
                    doc.setIdFicha(rs.getInt("ID_FICHA"));
                    doc.setIdTipoDocVet(rs.getInt("id_tipo_doc_vet"));
                    doc.setNombreArchivo(rs.getString("NOMBRE_ARCHIVO"));
                    doc.setRutaArchivo(rs.getString("RUTA_ARCHIVO"));
                    doc.setFormato(rs.getString("FORMATO"));
                    doc.setTamanoBytes(rs.getLong("TAMANO_BYTES"));
                    doc.setTamanoKb(rs.getInt("TAMANO_KB"));
                    doc.setFechaDocumento(rs.getDate("FECHA_DOCUMENTO").toLocalDate());
                    doc.setObservaciones(rs.getString("OBSERVACIONES"));
                    doc.setEstado(rs.getString("ESTADO"));
                    doc.setCreadoEn(rs.getTimestamp("CREADO_EN").toLocalDateTime());

                    Integer idConsulta = rs.getInt("ID_CONSULTA");
                    if (!rs.wasNull()) {
                        doc.setIdConsulta(idConsulta);
                    }

                    doc.setNombreTipoDocumento(rs.getString("tipo_nombre"));
                    doc.setEsObligatorio(rs.getBoolean("es_obligatorio"));
                    doc.setNombreMascota(rs.getString("mascota_nombre"));
                    doc.setNombreCliente(rs.getString("cliente_nombre"));

                    documentos.add(doc);
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al obtener documentos: " + e.getMessage());
            e.printStackTrace();
        }
        return documentos;
    }

    /**
     * Actualiza un documento usando el procedimiento almacenado
     */
    public boolean actualizarDocumento(int idDocumento, int idTipoDocVet,
            LocalDate fechaDocumento, String observaciones,
            int modificadoPor) {
        String sql = "{CALL sp_editar_documento_veterinario(?, ?, ?, ?, ?)}";

        try (Connection conn = cx.conectar(); CallableStatement cs = conn.prepareCall(sql)) {

            cs.setInt(1, idDocumento);
            cs.setInt(2, idTipoDocVet);
            cs.setDate(3, java.sql.Date.valueOf(fechaDocumento));
            cs.setString(4, observaciones);
            cs.setInt(5, modificadoPor);

            cs.execute();
            return true;

        } catch (SQLException e) {
            System.err.println("❌ Error al actualizar documento: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Elimina un documento usando el procedimiento almacenado (soft delete)
     */
    public boolean eliminarDocumento(int idDocumento, String motivoEliminacion,
            int eliminadoPor) {
        String sql = "{CALL sp_eliminar_documento_veterinario(?, ?, ?)}";

        try (Connection conn = cx.conectar(); CallableStatement cs = conn.prepareCall(sql)) {

            cs.setInt(1, idDocumento);
            cs.setString(2, motivoEliminacion);
            cs.setInt(3, eliminadoPor);

            cs.execute();
            return true;

        } catch (SQLException e) {
            // Verificar si es un documento obligatorio
            if (e.getMessage().contains("obligatorio")) {
                throw new RuntimeException("Este documento no puede eliminarse por ser obligatorio");
            }
            System.err.println("❌ Error al eliminar documento: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Obtiene un documento por ID
     */
    public Documento obtenerPorId(int idDocumento) {
        String sql = "SELECT d.*, tdv.nombre AS tipo_nombre, tdv.es_obligatorio, "
                + "m.NOMBRE AS mascota_nombre, c.NOMBRE AS cliente_nombre "
                + "FROM documentos d "
                + "INNER JOIN tipo_documento_veterinario tdv ON d.id_tipo_doc_vet = tdv.id_tipo_doc_vet "
                + "INNER JOIN mascotas m ON d.ID_MASCOTA = m.ID_MASCOTA "
                + "INNER JOIN clientes c ON m.ID_CLIENTE = c.ID_CLIENTE "
                + "WHERE d.ID_DOCUMENTO = ?";

        try (Connection conn = cx.conectar(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idDocumento);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Documento doc = new Documento();
                    doc.setIdDocumento(rs.getInt("ID_DOCUMENTO"));
                    doc.setIdMascota(rs.getInt("ID_MASCOTA"));
                    doc.setIdFicha(rs.getInt("ID_FICHA"));
                    doc.setIdTipoDocVet(rs.getInt("id_tipo_doc_vet"));
                    doc.setNombreArchivo(rs.getString("NOMBRE_ARCHIVO"));
                    doc.setRutaArchivo(rs.getString("RUTA_ARCHIVO"));
                    doc.setFormato(rs.getString("FORMATO"));
                    doc.setTamanoBytes(rs.getLong("TAMANO_BYTES"));
                    doc.setTamanoKb(rs.getInt("TAMANO_KB"));
                    doc.setFechaDocumento(rs.getDate("FECHA_DOCUMENTO").toLocalDate());
                    doc.setObservaciones(rs.getString("OBSERVACIONES"));
                    doc.setEstado(rs.getString("ESTADO"));
                    doc.setNombreTipoDocumento(rs.getString("tipo_nombre"));
                    doc.setEsObligatorio(rs.getBoolean("es_obligatorio"));
                    doc.setNombreMascota(rs.getString("mascota_nombre"));
                    doc.setNombreCliente(rs.getString("cliente_nombre"));
                    return doc;
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al obtener documento: " + e.getMessage());
        }
        return null;
    }

    /**
     * Valida formato de archivo
     */
    public boolean validarFormato(String extension) {
        String sql = "SELECT COUNT(*) FROM formatos_permitidos "
                + "WHERE EXTENSION = ? AND ACTIVO = 1";

        try (Connection conn = cx.conectar(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, extension.toUpperCase());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al validar formato: " + e.getMessage());
        }
        return false;
    }

    /**
     * Obtiene el tamaño máximo permitido para un formato
     */
    public int obtenerTamanoMaximo(String extension) {
        String sql = "SELECT TAMANO_MAX_MB FROM formatos_permitidos "
                + "WHERE EXTENSION = ? AND ACTIVO = 1";

        try (Connection conn = cx.conectar(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, extension.toUpperCase());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("TAMANO_MAX_MB");
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al obtener tamaño máximo: " + e.getMessage());
        }
        return 10; // Default 10MB
    }
}

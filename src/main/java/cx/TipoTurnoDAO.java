package cx;

import modelo.TipoTurno;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TipoTurnoDAO {

    private final conexion cx = new conexion();

    // ✅ Obtener todos los tipos de turno activos
    public List<TipoTurno> obtenerTodosActivos() {
        List<TipoTurno> tipos = new ArrayList<>();
        String sql = "SELECT * FROM tipos_turno WHERE ACTIVO = 1 ORDER BY NOMBRE";
        
        try (Connection conn = cx.conectar();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            
            while (rs.next()) {
                TipoTurno tipo = new TipoTurno();
                tipo.setIdTipoTurno(rs.getInt("ID_TIPO_TURNO"));
                tipo.setNombre(rs.getString("NOMBRE"));
                tipo.setDuracionMinutos(rs.getInt("DURACION_MINUTOS"));
                tipo.setActivo(rs.getBoolean("ACTIVO"));
                tipos.add(tipo);
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al obtener tipos de turno: " + e.getMessage());
            e.printStackTrace();
        }
        return tipos;
    }

    // ✅ Obtener tipo de turno por ID
    public TipoTurno obtenerPorId(int idTipoTurno) {
        String sql = "SELECT * FROM tipos_turno WHERE ID_TIPO_TURNO = ?";
        
        try (Connection conn = cx.conectar(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idTipoTurno);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    TipoTurno tipo = new TipoTurno();
                    tipo.setIdTipoTurno(rs.getInt("ID_TIPO_TURNO"));
                    tipo.setNombre(rs.getString("NOMBRE"));
                    tipo.setDuracionMinutos(rs.getInt("DURACION_MINUTOS"));
                    tipo.setActivo(rs.getBoolean("ACTIVO"));
                    return tipo;
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al obtener tipo de turno por ID: " + e.getMessage());
        }
        return null;
    }

    // ✅ Obtener tipos de turno por duración mínima
    public List<TipoTurno> obtenerPorDuracionMinima(int duracionMinima) {
        List<TipoTurno> tipos = new ArrayList<>();
        String sql = "SELECT * FROM tipos_turno WHERE DURACION_MINUTOS >= ? AND ACTIVO = 1 ORDER BY DURACION_MINUTOS";
        
        try (Connection conn = cx.conectar(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, duracionMinima);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    TipoTurno tipo = new TipoTurno();
                    tipo.setIdTipoTurno(rs.getInt("ID_TIPO_TURNO"));
                    tipo.setNombre(rs.getString("NOMBRE"));
                    tipo.setDuracionMinutos(rs.getInt("DURACION_MINUTOS"));
                    tipo.setActivo(rs.getBoolean("ACTIVO"));
                    tipos.add(tipo);
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al obtener tipos por duración: " + e.getMessage());
        }
        return tipos;
    }
}
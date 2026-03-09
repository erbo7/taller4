package cx;

import modelo.Usuario;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {

    private conexion cx;

    public UsuarioDAO() {
        this.cx = new conexion();
    }

    // Crear usuario
    public boolean crearUsuario(Usuario u) {
        String sql = "INSERT INTO usuarios (nombre, usuario, contrasena, id_rol, email, activo) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = cx.conectar(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, u.getNombre());
            ps.setString(2, u.getUsuario());
            ps.setString(3, u.getContrasena());
            ps.setInt(4, u.getIdRol());
            ps.setString(5, u.getEmail());
            ps.setBoolean(6, u.isActivo());

            int filasAfectadas = ps.executeUpdate();
            return filasAfectadas > 0;
        } catch (SQLException e) {
            System.err.println("Error al crear usuario: " + e.getMessage());
            return false;
        }
    }

    // Obtener usuario por ID
    public Usuario obtenerPorId(int id) {
        String sql = "SELECT * FROM usuarios WHERE id_usuario = ?";
        try (Connection conn = cx.conectar(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearUsuario(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener usuario: " + e.getMessage());
        }
        return null;
    }

    // Obtener usuario por nombre de usuario (para login)
    public Usuario obtenerPorUsuario(String usuario) {
        String sql = "SELECT * FROM usuarios WHERE USUARIO = ? AND ACTIVO = 1";
        try (Connection conn = cx.conectar(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, usuario);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearUsuario(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener usuario por nombre: " + e.getMessage());
        }
        return null;
    }

    // Validar credenciales de login
    // Validar credenciales de login
    public Usuario validarLogin(String usuario, String contrasena) {
        String sql = "SELECT * FROM usuarios WHERE USUARIO = ? AND CONTRASENA = ? AND ACTIVO = 1";

        try (Connection conn = cx.conectar(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, usuario);
            ps.setString(2, contrasena);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Usuario u = mapearUsuario(rs);

                    // 🔥 DEBUG
                    System.out.println("✅ Login exitoso - ID Usuario: " + u.getIdUsuario());
                    System.out.println("✅ Nombre: " + u.getNombre());
                    System.out.println("✅ Rol: " + u.getIdRol());

                    return u;
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al validar login: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    // Obtener todos los usuarios
    public List<Usuario> obtenerTodos() {
        List<Usuario> usuarios = new ArrayList<>();
        String sql = "SELECT * FROM usuarios WHERE ACTIVO = 1";
        try (Connection conn = cx.conectar(); Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                usuarios.add(mapearUsuario(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener usuarios: " + e.getMessage());
        }
        return usuarios;
    }

    // Actualizar usuario
    public boolean actualizar(Usuario u) {
        String sql = "UPDATE usuarios SET nombre = ?, contrasena = ?, id_rol = ?, email = ? WHERE id_usuario = ?";
        try (Connection conn = cx.conectar(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, u.getNombre());
            ps.setString(2, u.getContrasena());
            ps.setInt(3, u.getIdRol());
            ps.setString(4, u.getEmail());
            ps.setInt(5, u.getIdUsuario());

            int filasAfectadas = ps.executeUpdate();
            return filasAfectadas > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar usuario: " + e.getMessage());
            return false;
        }
    }

    // Desactivar usuario (borrado lógico)
    public boolean desactivar(int idUsuario) {
        String sql = "UPDATE usuarios SET ACTIVO = 0 WHERE ID_USUARIO = ?";
        try (Connection conn = cx.conectar(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idUsuario);
            int filasAfectadas = ps.executeUpdate();
            return filasAfectadas > 0;
        } catch (SQLException e) {
            System.err.println("Error al desactivar usuario: " + e.getMessage());
            return false;
        }
    }

    // ============================================
// MÉTODOS NUEVOS PARA BLOQUEO DE INTENTOS
// ============================================
    /**
     * Incrementa los intentos fallidos de un usuario
     */
    public boolean incrementarIntentosFallidos(String usuario) {
        String sql = "UPDATE usuarios SET intentos_fallidos = intentos_fallidos + 1 WHERE USUARIO = ?";
        try (Connection conn = cx.conectar(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, usuario);
            int filasAfectadas = ps.executeUpdate();
            return filasAfectadas > 0;
        } catch (SQLException e) {
            System.err.println("Error al incrementar intentos fallidos: " + e.getMessage());
            return false;
        }
    }

    /**
     * Bloquea un usuario estableciendo el tiempo de bloqueo
     */
    public boolean bloquearUsuario(String usuario) {
        String sql = "UPDATE usuarios SET tiempo_bloqueo = ? WHERE USUARIO = ?";
        try (Connection conn = cx.conectar(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, System.currentTimeMillis());
            ps.setString(2, usuario);
            int filasAfectadas = ps.executeUpdate();
            return filasAfectadas > 0;
        } catch (SQLException e) {
            System.err.println("Error al bloquear usuario: " + e.getMessage());
            return false;
        }
    }

    /**
     * Resetea los intentos fallidos y desbloquea al usuario
     */
    public boolean resetearBloqueo(String usuario) {
        String sql = "UPDATE usuarios SET intentos_fallidos = 0, tiempo_bloqueo = NULL WHERE USUARIO = ?";
        try (Connection conn = cx.conectar(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, usuario);
            int filasAfectadas = ps.executeUpdate();
            return filasAfectadas > 0;
        } catch (SQLException e) {
            System.err.println("Error al resetear bloqueo: " + e.getMessage());
            return false;
        }
    }

    /**
     * Obtiene los intentos fallidos de un usuario
     */
    public int obtenerIntentosFallidos(String usuario) {
        String sql = "SELECT intentos_fallidos FROM usuarios WHERE USUARIO = ?";
        try (Connection conn = cx.conectar(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, usuario);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt("intentos_fallidos");
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener intentos fallidos: " + e.getMessage());
        }
        return 0;
    }

    /**
     * Obtiene el tiempo de bloqueo de un usuario
     */
    public Long obtenerTiempoBloqueo(String usuario) {
        String sql = "SELECT tiempo_bloqueo FROM usuarios WHERE USUARIO = ?";
        try (Connection conn = cx.conectar(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, usuario);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                long tiempo = rs.getLong("tiempo_bloqueo");
                return rs.wasNull() ? null : tiempo;
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener tiempo de bloqueo: " + e.getMessage());
        }
        return null;
    }

    /**
     * Verifica si existe un usuario con ese nombre de usuario
     */
    public boolean existeUsuario(String usuario) {
        String sql = "SELECT COUNT(*) FROM usuarios WHERE USUARIO = ?";
        try (Connection conn = cx.conectar(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, usuario);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error al verificar existencia de usuario: " + e.getMessage());
        }
        return false;
    }

    // Mapear ResultSet a objeto Usuario
    private Usuario mapearUsuario(ResultSet rs) throws SQLException {
        return new Usuario(
                rs.getInt("ID_USUARIO"), // ✅ MAYÚSCULAS
                rs.getString("NOMBRE"), // ✅ MAYÚSCULAS
                rs.getString("USUARIO"), // ✅ MAYÚSCULAS
                rs.getString("CONTRASENA"), // ✅ MAYÚSCULAS
                rs.getInt("id_rol"), // ✅ minúsculas (así está en tu BD)
                rs.getString("EMAIL"), // ✅ MAYÚSCULAS
                rs.getBoolean("ACTIVO") // ✅ MAYÚSCULAS
        );
    }
}

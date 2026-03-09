package interfaces;

import modelo.Usuario;

/**
 * Interface para validación de usuarios
 * Sistema Veterinario - DiazPet
 */
public interface validar {
    
    /**
     * Método para validar credenciales de usuario
     * @param usuario nombre de usuario
     * @param contrasena contraseña del usuario
     * @return Usuario objeto usuario si las credenciales son correctas, null si son incorrectas
     */
    public Usuario validarUsuario(String usuario, String contrasena);
    
    /**
     * Método para verificar si un usuario existe
     * @param usuario nombre de usuario
     * @return boolean true si existe, false si no existe
     */
    public boolean existeUsuario(String usuario);
    
    /**
     * Método para verificar si un email está registrado
     * @param email correo electrónico
     * @return boolean true si existe, false si no existe
     */
    public boolean existeEmail(String email);
}
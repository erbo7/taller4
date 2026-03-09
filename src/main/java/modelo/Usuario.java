package modelo;

/**
 * Clase modelo Usuario
 * Mapea la tabla 'usuarios' de la base de datos
 * Sistema Veterinario - DiazPet
 */
public class Usuario {
    
    // Atributos que corresponden a las columnas de la tabla 'usuarios'
    private int idUsuario;
    private String nombre;
    private String usuario;
    private String contrasena;
    private int idRol;
    private String email;
    private boolean activo;
    
    // Atributos adicionales (no están en la BD pero son útiles)
    private String nombreRol; // Para mostrar el nombre del rol en la interfaz
    
    // Constructor vacío
    public Usuario() {
    }
    
    // Constructor con todos los parámetros
    public Usuario(int idUsuario, String nombre, String usuario, String contrasena, 
                   int idRol, String email, boolean activo) {
        this.idUsuario = idUsuario;
        this.nombre = nombre;
        this.usuario = usuario;
        this.contrasena = contrasena;
        this.idRol = idRol;
        this.email = email;
        this.activo = activo;
    }
    
    // Constructor sin ID (para insertar nuevos registros)
    public Usuario(String nombre, String usuario, String contrasena, 
                   int idRol, String email, boolean activo) {
        this.nombre = nombre;
        this.usuario = usuario;
        this.contrasena = contrasena;
        this.idRol = idRol;
        this.email = email;
        this.activo = activo;
    }
    
    // Getters y Setters
    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public int getIdRol() {
        return idRol;
    }

    public void setIdRol(int idRol) {
        this.idRol = idRol;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public String getNombreRol() {
        return nombreRol;
    }

    public void setNombreRol(String nombreRol) {
        this.nombreRol = nombreRol;
    }
    
    // Método toString para debugging
    @Override
    public String toString() {
        return "Usuario{" +
                "idUsuario=" + idUsuario +
                ", nombre='" + nombre + '\'' +
                ", usuario='" + usuario + '\'' +
                ", email='" + email + '\'' +
                ", activo=" + activo +
                '}';
    }
}
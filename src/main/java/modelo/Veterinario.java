package modelo;

public class Veterinario {
    private int idVeterinario;
    private int idUsuario;
    private String nombre;
    private String matricula;
    private String telefono;
    private String email;
    private String estado;
    private int idEspecialidad;

    // Constructor vacío
    public Veterinario() {
    }

    // Constructor con parámetros (para insertar)
    public Veterinario(int idUsuario, String nombre, String matricula, 
                       String telefono, String email, int idEspecialidad) {
        this.idUsuario = idUsuario;
        this.nombre = nombre;
        this.matricula = matricula;
        this.telefono = telefono;
        this.email = email;
        this.estado = "Activo";
        this.idEspecialidad = idEspecialidad;
    }

    // Constructor completo
    public Veterinario(int idVeterinario, int idUsuario, String nombre, String matricula,
                       String telefono, String email, String estado, int idEspecialidad) {
        this.idVeterinario = idVeterinario;
        this.idUsuario = idUsuario;
        this.nombre = nombre;
        this.matricula = matricula;
        this.telefono = telefono;
        this.email = email;
        this.estado = estado;
        this.idEspecialidad = idEspecialidad;
    }

    // Getters y Setters
    public int getIdVeterinario() {
        return idVeterinario;
    }

    public void setIdVeterinario(int idVeterinario) {
        this.idVeterinario = idVeterinario;
    }

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

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public int getIdEspecialidad() {
        return idEspecialidad;
    }

    public void setIdEspecialidad(int idEspecialidad) {
        this.idEspecialidad = idEspecialidad;
    }

    @Override
    public String toString() {
        return "Veterinario{" +
                "idVeterinario=" + idVeterinario +
                ", nombre='" + nombre + '\'' +
                ", matricula='" + matricula + '\'' +
                ", estado='" + estado + '\'' +
                '}';
    }
}

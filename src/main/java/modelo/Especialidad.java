package modelo;

public class Especialidad {
    private int idEspecialidad;
    private String nombre;
    private String descripcion;
    
    // Constructor vacío
    public Especialidad() {
    }
    
    // Constructor sin descripción
    public Especialidad(int idEspecialidad, String nombre) {
        this.idEspecialidad = idEspecialidad;
        this.nombre = nombre;
    }
    
    // Constructor completo
    public Especialidad(int idEspecialidad, String nombre, String descripcion) {
        this.idEspecialidad = idEspecialidad;
        this.nombre = nombre;
        this.descripcion = descripcion;
    }
    
    // Getters y Setters
    public int getIdEspecialidad() { return idEspecialidad; }
    public void setIdEspecialidad(int idEspecialidad) { this.idEspecialidad = idEspecialidad; }
    
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
}
package modelo;

public class Especie {
    private int idEspecie;
    private String nombre;

    // Constructor vacío
    public Especie() {}

    // Constructor completo
    public Especie(int idEspecie, String nombre) {
        this.idEspecie = idEspecie;
        this.nombre = nombre;
    }

    // Getters y Setters
    public int getIdEspecie() { return idEspecie; }
    public void setIdEspecie(int idEspecie) { this.idEspecie = idEspecie; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
}
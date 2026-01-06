package modelo;

public class Raza {
    private int idRaza;
    private int idEspecie;
    private String nombre;

    // Constructor vacío
    public Raza() {}

    // Constructor completo
    public Raza(int idRaza, int idEspecie, String nombre) {
        this.idRaza = idRaza;
        this.idEspecie = idEspecie;
        this.nombre = nombre;
    }

    // Getters y Setters
    public int getIdRaza() { return idRaza; }
    public void setIdRaza(int idRaza) { this.idRaza = idRaza; }

    public int getIdEspecie() { return idEspecie; }
    public void setIdEspecie(int idEspecie) { this.idEspecie = idEspecie; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
}
package modelo;

public class Mascota {

    private int idMascota;
    private int idCliente;
    private int idRaza;   // 👈 CLAVE
    private String nombre;
    private Integer edad;
    private String sexo;
    private String especie;  // FALTABA DECLARAR

    public Mascota() {
    }

    public Mascota(int idMascota, int idCliente, String nombre, Integer edad, String sexo) {
        this.idMascota = idMascota;
        this.idCliente = idCliente;
        this.nombre = nombre;
        this.edad = edad;
        this.sexo = sexo;
    }

    // Getters y setters
    public int getIdMascota() {
        return idMascota;
    }

    public void setIdMascota(int idMascota) {
        this.idMascota = idMascota;
    }

    public int getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(int idCliente) {
        this.idCliente = idCliente;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Integer getEdad() {
        return edad;
    }

    public void setEdad(Integer edad) {
        this.edad = edad;
    }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public String getEspecie() {
        return especie;
    }

    public void setEspecie(String especie) {
        this.especie = especie;
    }

    public int getIdRaza() {
        return idRaza;
    }

    public void setIdRaza(int idRaza) {
        this.idRaza = idRaza;
    }
}

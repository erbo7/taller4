package modelo;

public class TipoDocumento {
    private int idTipoDocumento;
    private String nombre;
    
    // Constructor vacío
    public TipoDocumento() {
    }
    
    // Constructor con parámetros
    public TipoDocumento(int idTipoDocumento, String nombre) {
        this.idTipoDocumento = idTipoDocumento;
        this.nombre = nombre;
    }
    
    // Getters y Setters
    public int getIdTipoDocumento() { return idTipoDocumento; }
    public void setIdTipoDocumento(int idTipoDocumento) { this.idTipoDocumento = idTipoDocumento; }
    
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
}

package modelo;

public class TipoDocumentoVeterinario {
    private int idTipoDocVet;
    private String nombre;
    private boolean esObligatorio;
    private boolean activo;
    
    public TipoDocumentoVeterinario() {}
    
    public TipoDocumentoVeterinario(int idTipoDocVet, String nombre, 
                                    boolean esObligatorio, boolean activo) {
        this.idTipoDocVet = idTipoDocVet;
        this.nombre = nombre;
        this.esObligatorio = esObligatorio;
        this.activo = activo;
    }
    
    // Getters y Setters
    public int getIdTipoDocVet() { return idTipoDocVet; }
    public void setIdTipoDocVet(int idTipoDocVet) { this.idTipoDocVet = idTipoDocVet; }
    
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    
    public boolean isEsObligatorio() { return esObligatorio; }
    public void setEsObligatorio(boolean esObligatorio) { this.esObligatorio = esObligatorio; }
    
    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
}
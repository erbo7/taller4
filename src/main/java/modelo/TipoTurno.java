package modelo;

public class TipoTurno {
    
    private int idTipoTurno;
    private String nombre;
    private int duracionMinutos;
    private boolean activo;
    
    // Constructor vacío
    public TipoTurno() {
    }
    
    // Constructor completo
    public TipoTurno(int idTipoTurno, String nombre, int duracionMinutos, boolean activo) {
        this.idTipoTurno = idTipoTurno;
        this.nombre = nombre;
        this.duracionMinutos = duracionMinutos;
        this.activo = activo;
    }
    
    // Constructor sin ID (para insertar)
    public TipoTurno(String nombre, int duracionMinutos, boolean activo) {
        this.nombre = nombre;
        this.duracionMinutos = duracionMinutos;
        this.activo = activo;
    }
    
    // Getters y Setters
    public int getIdTipoTurno() {
        return idTipoTurno;
    }
    
    public void setIdTipoTurno(int idTipoTurno) {
        this.idTipoTurno = idTipoTurno;
    }
    
    public String getNombre() {
        return nombre;
    }
    
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    
    public int getDuracionMinutos() {
        return duracionMinutos;
    }
    
    public void setDuracionMinutos(int duracionMinutos) {
        this.duracionMinutos = duracionMinutos;
    }
    
    public boolean isActivo() {
        return activo;
    }
    
    public void setActivo(boolean activo) {
        this.activo = activo;
    }
    
    @Override
    public String toString() {
        return nombre + " (" + duracionMinutos + " min)";
    }
}
package modelo;

import java.sql.Time;
import java.sql.Timestamp;

public class RecordatorioConfig {
    
    private int idConfig;
    private int horasAntes;
    private Time horaEnvio;
    private String canalPrincipal;
    private String plantillaMensaje;
    private int maxReintentos;
    private boolean habilitado;
    private int modificadoPor;
    private Timestamp modificadoEn;
    
    // Constructores
    public RecordatorioConfig() {
    }
    
    public RecordatorioConfig(int idConfig, int horasAntes, Time horaEnvio, 
                             String canalPrincipal, String plantillaMensaje, 
                             int maxReintentos, boolean habilitado) {
        this.idConfig = idConfig;
        this.horasAntes = horasAntes;
        this.horaEnvio = horaEnvio;
        this.canalPrincipal = canalPrincipal;
        this.plantillaMensaje = plantillaMensaje;
        this.maxReintentos = maxReintentos;
        this.habilitado = habilitado;
    }
    
    // Getters y Setters
    public int getIdConfig() {
        return idConfig;
    }
    
    public void setIdConfig(int idConfig) {
        this.idConfig = idConfig;
    }
    
    public int getHorasAntes() {
        return horasAntes;
    }
    
    public void setHorasAntes(int horasAntes) {
        this.horasAntes = horasAntes;
    }
    
    public Time getHoraEnvio() {
        return horaEnvio;
    }
    
    public void setHoraEnvio(Time horaEnvio) {
        this.horaEnvio = horaEnvio;
    }
    
    public String getCanalPrincipal() {
        return canalPrincipal;
    }
    
    public void setCanalPrincipal(String canalPrincipal) {
        this.canalPrincipal = canalPrincipal;
    }
    
    public String getPlantillaMensaje() {
        return plantillaMensaje;
    }
    
    public void setPlantillaMensaje(String plantillaMensaje) {
        this.plantillaMensaje = plantillaMensaje;
    }
    
    public int getMaxReintentos() {
        return maxReintentos;
    }
    
    public void setMaxReintentos(int maxReintentos) {
        this.maxReintentos = maxReintentos;
    }
    
    public boolean isHabilitado() {
        return habilitado;
    }
    
    public void setHabilitado(boolean habilitado) {
        this.habilitado = habilitado;
    }
    
    public int getModificadoPor() {
        return modificadoPor;
    }
    
    public void setModificadoPor(int modificadoPor) {
        this.modificadoPor = modificadoPor;
    }
    
    public Timestamp getModificadoEn() {
        return modificadoEn;
    }
    
    public void setModificadoEn(Timestamp modificadoEn) {
        this.modificadoEn = modificadoEn;
    }
    
    @Override
    public String toString() {
        return "RecordatorioConfig{" +
                "idConfig=" + idConfig +
                ", horasAntes=" + horasAntes +
                ", horaEnvio=" + horaEnvio +
                ", canalPrincipal='" + canalPrincipal + '\'' +
                ", maxReintentos=" + maxReintentos +
                ", habilitado=" + habilitado +
                '}';
    }
}
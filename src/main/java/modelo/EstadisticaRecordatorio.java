package modelo;

import java.sql.Date;

public class EstadisticaRecordatorio {
    
    private Date fecha;
    private int totalRecordatorios;
    private int enviados;
    private int fallados;
    private int pendientes;
    private int reintentos;
    private double promedioIntentos;
    
    // Campos para porcentajes (calculados, no persistidos)
    private transient double porcentajeEnviados;
    private transient double porcentajeFallados;
    private transient double porcentajePendientes;
    
    // Constructor vacío
    public EstadisticaRecordatorio() {
    }
    
    // Constructor con todos los campos
    public EstadisticaRecordatorio(Date fecha, int totalRecordatorios, 
                                   int enviados, int fallados, int pendientes,
                                   int reintentos, double promedioIntentos) {
        this.fecha = fecha;
        this.totalRecordatorios = totalRecordatorios;
        this.enviados = enviados;
        this.fallados = fallados;
        this.pendientes = pendientes;
        this.reintentos = reintentos;
        this.promedioIntentos = promedioIntentos;
        calcularPorcentajes();
    }
    
    // Getters y Setters
    public Date getFecha() {
        return fecha;
    }
    
    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }
    
    public int getTotalRecordatorios() {
        return totalRecordatorios;
    }
    
    public void setTotalRecordatorios(int totalRecordatorios) {
        this.totalRecordatorios = totalRecordatorios;
        calcularPorcentajes();
    }
    
    public int getEnviados() {
        return enviados;
    }
    
    public void setEnviados(int enviados) {
        this.enviados = enviados;
        calcularPorcentajes();
    }
    
    public int getFallados() {
        return fallados;
    }
    
    public void setFallados(int fallados) {
        this.fallados = fallados;
        calcularPorcentajes();
    }
    
    public int getPendientes() {
        return pendientes;
    }
    
    public void setPendientes(int pendientes) {
        this.pendientes = pendientes;
        calcularPorcentajes();
    }
    
    public int getReintentos() {
        return reintentos;
    }
    
    public void setReintentos(int reintentos) {
        this.reintentos = reintentos;
    }
    
    public double getPromedioIntentos() {
        return promedioIntentos;
    }
    
    public void setPromedioIntentos(double promedioIntentos) {
        this.promedioIntentos = promedioIntentos;
    }
    
    // Métodos para porcentajes (solo getters, se calculan automáticamente)
    public double getPorcentajeEnviados() {
        return porcentajeEnviados;
    }
    
    public double getPorcentajeFallados() {
        return porcentajeFallados;
    }
    
    public double getPorcentajePendientes() {
        return porcentajePendientes;
    }
    
    // Método privado para calcular porcentajes
    private void calcularPorcentajes() {
        if (totalRecordatorios == 0) {
            porcentajeEnviados = 0.0;
            porcentajeFallados = 0.0;
            porcentajePendientes = 0.0;
        } else {
            porcentajeEnviados = (enviados * 100.0) / totalRecordatorios;
            porcentajeFallados = (fallados * 100.0) / totalRecordatorios;
            porcentajePendientes = (pendientes * 100.0) / totalRecordatorios;
        }
    }
    
    // Setters para porcentajes (opcional, si alguien los quiere settear manualmente)
    public void setPorcentajeEnviados(double porcentajeEnviados) {
        this.porcentajeEnviados = porcentajeEnviados;
    }
    
    public void setPorcentajeFallados(double porcentajeFallados) {
        this.porcentajeFallados = porcentajeFallados;
    }
    
    public void setPorcentajePendientes(double porcentajePendientes) {
        this.porcentajePendientes = porcentajePendientes;
    }
    
    @Override
    public String toString() {
        return "EstadisticaRecordatorio{" +
                "fecha=" + fecha +
                ", totalRecordatorios=" + totalRecordatorios +
                ", enviados=" + enviados +
                ", fallados=" + fallados +
                ", pendientes=" + pendientes +
                ", reintentos=" + reintentos +
                ", promedioIntentos=" + promedioIntentos +
                ", porcentajeEnviados=" + porcentajeEnviados +
                ", porcentajeFallados=" + porcentajeFallados +
                ", porcentajePendientes=" + porcentajePendientes +
                '}';
    }
}
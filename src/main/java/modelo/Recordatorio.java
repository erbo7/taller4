package modelo;

import java.sql.Timestamp;

public class Recordatorio {
    
    // Campos principales
    private int idRecordatorio;
    private int idCita;
    private int idConfig;
    private String canal;
    private Timestamp fechaEnvio;
    private String estadoEnvio;
    private int intentosRealizados;
    private String mensajeEnviado;
    private String resultadoEnvio;
    private String observaciones;
    private Timestamp creadoEn;
    
    // Campos adicionales para detalles
    private String respuestaCliente;
    private Integer intento;
    private Timestamp fechaHoraEnvioReal;
    private Timestamp fechaProximoIntento;
    private String errorDetalle;
    private String especie;
    
    // Campos adicionales para la vista (joins)
    private String nombreCliente;
    private String telefonoCliente;
    private String emailCliente;
    private String nombreMascota;
    private String nombreVeterinario;
    private Timestamp fechaHoraCita;
    private String estadoCita;
    private String motivoCita;
    
    // Constructores
    public Recordatorio() {
    }
    
    public Recordatorio(int idRecordatorio, int idCita, String canal, 
                       String estadoEnvio, Timestamp fechaEnvio) {
        this.idRecordatorio = idRecordatorio;
        this.idCita = idCita;
        this.canal = canal;
        this.estadoEnvio = estadoEnvio;
        this.fechaEnvio = fechaEnvio;
    }
    
    // Getters y Setters principales
    public int getIdRecordatorio() {
        return idRecordatorio;
    }
    
    public void setIdRecordatorio(int idRecordatorio) {
        this.idRecordatorio = idRecordatorio;
    }
    
    public int getIdCita() {
        return idCita;
    }
    
    public void setIdCita(int idCita) {
        this.idCita = idCita;
    }
    
    public int getIdConfig() {
        return idConfig;
    }
    
    public void setIdConfig(int idConfig) {
        this.idConfig = idConfig;
    }
    
    public String getCanal() {
        return canal;
    }
    
    public void setCanal(String canal) {
        this.canal = canal;
    }
    
    public Timestamp getFechaEnvio() {
        return fechaEnvio;
    }
    
    public void setFechaEnvio(Timestamp fechaEnvio) {
        this.fechaEnvio = fechaEnvio;
    }
    
    public String getEstadoEnvio() {
        return estadoEnvio;
    }
    
    public void setEstadoEnvio(String estadoEnvio) {
        this.estadoEnvio = estadoEnvio;
    }
    
    public int getIntentosRealizados() {
        return intentosRealizados;
    }
    
    public void setIntentosRealizados(int intentosRealizados) {
        this.intentosRealizados = intentosRealizados;
    }
    
    public String getMensajeEnviado() {
        return mensajeEnviado;
    }
    
    public void setMensajeEnviado(String mensajeEnviado) {
        this.mensajeEnviado = mensajeEnviado;
    }
    
    public String getResultadoEnvio() {
        return resultadoEnvio;
    }
    
    public void setResultadoEnvio(String resultadoEnvio) {
        this.resultadoEnvio = resultadoEnvio;
    }
    
    public String getObservaciones() {
        return observaciones;
    }
    
    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }
    
    public Timestamp getCreadoEn() {
        return creadoEn;
    }
    
    public void setCreadoEn(Timestamp creadoEn) {
        this.creadoEn = creadoEn;
    }
    
    // Getters y Setters de campos adicionales
    public String getRespuestaCliente() {
        return respuestaCliente;
    }
    
    public void setRespuestaCliente(String respuestaCliente) {
        this.respuestaCliente = respuestaCliente;
    }
    
    public Integer getIntento() {
        return intento;
    }
    
    public void setIntento(Integer intento) {
        this.intento = intento;
    }
    
    public Timestamp getFechaHoraEnvioReal() {
        return fechaHoraEnvioReal;
    }
    
    public void setFechaHoraEnvioReal(Timestamp fechaHoraEnvioReal) {
        this.fechaHoraEnvioReal = fechaHoraEnvioReal;
    }
    
    public Timestamp getFechaProximoIntento() {
        return fechaProximoIntento;
    }
    
    public void setFechaProximoIntento(Timestamp fechaProximoIntento) {
        this.fechaProximoIntento = fechaProximoIntento;
    }
    
    public String getErrorDetalle() {
        return errorDetalle;
    }
    
    public void setErrorDetalle(String errorDetalle) {
        this.errorDetalle = errorDetalle;
    }
    
    public String getEspecie() {
        return especie;
    }
    
    public void setEspecie(String especie) {
        this.especie = especie;
    }
    
    public String getNombreCliente() {
        return nombreCliente;
    }
    
    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }
    
    public String getTelefonoCliente() {
        return telefonoCliente;
    }
    
    public void setTelefonoCliente(String telefonoCliente) {
        this.telefonoCliente = telefonoCliente;
    }
    
    public String getEmailCliente() {
        return emailCliente;
    }
    
    public void setEmailCliente(String emailCliente) {
        this.emailCliente = emailCliente;
    }
    
    public String getNombreMascota() {
        return nombreMascota;
    }
    
    public void setNombreMascota(String nombreMascota) {
        this.nombreMascota = nombreMascota;
    }
    
    public String getNombreVeterinario() {
        return nombreVeterinario;
    }
    
    public void setNombreVeterinario(String nombreVeterinario) {
        this.nombreVeterinario = nombreVeterinario;
    }
    
    public Timestamp getFechaHoraCita() {
        return fechaHoraCita;
    }
    
    public void setFechaHoraCita(Timestamp fechaHoraCita) {
        this.fechaHoraCita = fechaHoraCita;
    }
    
    public String getEstadoCita() {
        return estadoCita;
    }
    
    public void setEstadoCita(String estadoCita) {
        this.estadoCita = estadoCita;
    }
    
    public String getMotivoCita() {
        return motivoCita;
    }
    
    public void setMotivoCita(String motivoCita) {
        this.motivoCita = motivoCita;
    }
    
    @Override
    public String toString() {
        return "Recordatorio{" +
                "idRecordatorio=" + idRecordatorio +
                ", idCita=" + idCita +
                ", canal='" + canal + '\'' +
                ", estadoEnvio='" + estadoEnvio + '\'' +
                ", nombreCliente='" + nombreCliente + '\'' +
                ", nombreMascota='" + nombreMascota + '\'' +
                '}';
    }
}
package modelo;

import java.time.LocalDateTime;

public class AgendaSlot {
    
    private int idSlot;
    private int idAgenda;
    private int idVeterinario;
    private LocalDateTime fechaHoraInicio;
    private LocalDateTime fechaHoraFin;
    private boolean disponible;
    private Integer idCita;
    private LocalDateTime creadoEn;
    private LocalDateTime modificadoEn;
    
    // Datos adicionales para consultas
    private String nombreVeterinario;
    private String tipoTurno;
    private Integer duracionMinutos;
    
    // Constructor vacío
    public AgendaSlot() {
    }
    
    // Constructor para insertar
    public AgendaSlot(int idAgenda, int idVeterinario, LocalDateTime fechaHoraInicio, 
                     LocalDateTime fechaHoraFin, boolean disponible) {
        this.idAgenda = idAgenda;
        this.idVeterinario = idVeterinario;
        this.fechaHoraInicio = fechaHoraInicio;
        this.fechaHoraFin = fechaHoraFin;
        this.disponible = disponible;
    }
    
    // Getters y Setters
    public int getIdSlot() {
        return idSlot;
    }
    
    public void setIdSlot(int idSlot) {
        this.idSlot = idSlot;
    }
    
    public int getIdAgenda() {
        return idAgenda;
    }
    
    public void setIdAgenda(int idAgenda) {
        this.idAgenda = idAgenda;
    }
    
    public int getIdVeterinario() {
        return idVeterinario;
    }
    
    public void setIdVeterinario(int idVeterinario) {
        this.idVeterinario = idVeterinario;
    }
    
    public LocalDateTime getFechaHoraInicio() {
        return fechaHoraInicio;
    }
    
    public void setFechaHoraInicio(LocalDateTime fechaHoraInicio) {
        this.fechaHoraInicio = fechaHoraInicio;
    }
    
    public LocalDateTime getFechaHoraFin() {
        return fechaHoraFin;
    }
    
    public void setFechaHoraFin(LocalDateTime fechaHoraFin) {
        this.fechaHoraFin = fechaHoraFin;
    }
    
    public boolean isDisponible() {
        return disponible;
    }
    
    public void setDisponible(boolean disponible) {
        this.disponible = disponible;
    }
    
    public Integer getIdCita() {
        return idCita;
    }
    
    public void setIdCita(Integer idCita) {
        this.idCita = idCita;
    }
    
    public LocalDateTime getCreadoEn() {
        return creadoEn;
    }
    
    public void setCreadoEn(LocalDateTime creadoEn) {
        this.creadoEn = creadoEn;
    }
    
    public LocalDateTime getModificadoEn() {
        return modificadoEn;
    }
    
    public void setModificadoEn(LocalDateTime modificadoEn) {
        this.modificadoEn = modificadoEn;
    }
    
    public String getNombreVeterinario() {
        return nombreVeterinario;
    }
    
    public void setNombreVeterinario(String nombreVeterinario) {
        this.nombreVeterinario = nombreVeterinario;
    }
    
    public String getTipoTurno() {
        return tipoTurno;
    }
    
    public void setTipoTurno(String tipoTurno) {
        this.tipoTurno = tipoTurno;
    }
    
    public Integer getDuracionMinutos() {
        return duracionMinutos;
    }
    
    public void setDuracionMinutos(Integer duracionMinutos) {
        this.duracionMinutos = duracionMinutos;
    }
}
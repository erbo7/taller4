package modelo;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;

public class Agenda {

    private int idAgenda;
    private int idVeterinario;
    private LocalDate fecha;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private int idTipoTurno;
    private String observaciones;
    private String estado;
    private int creadoPor;
    private LocalDateTime creadoEn;
    
    // Trazabilidad
    private Integer modificadoPor;
    private LocalDateTime modificadoEn;
    private Integer anuladoPor;
    private LocalDateTime anuladoEn;
    private String motivoAnulacion;

    // Datos adicionales para consultas (no están en la tabla)
    private String nombreVeterinario;
    private String nombreUsuario;
    private String nombreTipoTurno;  // ✅ NUEVO: Para mostrar el nombre del tipo de turno
    private Integer duracionMinutos; // ✅ NUEVO: Para mostrar duración del tipo de turno

    // Constructor vacío
    public Agenda() {
    }

    // ✅ Constructor CORREGIDO para insertar (con todos los parámetros)
    public Agenda(int idVeterinario, LocalDate fecha, LocalTime horaInicio, 
                  LocalTime horaFin, int idTipoTurno, String observaciones, 
                  String estado, int creadoPor) {
        this.idVeterinario = idVeterinario;
        this.fecha = fecha;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
        this.idTipoTurno = idTipoTurno;
        this.observaciones = observaciones;
        this.estado = estado;
        this.creadoPor = creadoPor;
    }

    // ✅ Constructor para editar (con ID)
    public Agenda(int idAgenda, int idVeterinario, LocalDate fecha, LocalTime horaInicio,
                  LocalTime horaFin, int idTipoTurno, String observaciones) {
        this.idAgenda = idAgenda;
        this.idVeterinario = idVeterinario;
        this.fecha = fecha;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
        this.idTipoTurno = idTipoTurno;
        this.observaciones = observaciones;
    }

    // ✅ Constructor para consultas completas
    public Agenda(int idAgenda, int idVeterinario, String nombreVeterinario,
                  LocalDate fecha, LocalTime horaInicio, LocalTime horaFin,
                  int idTipoTurno, String nombreTipoTurno, Integer duracionMinutos,
                  String observaciones, String estado, String nombreUsuario) {
        this.idAgenda = idAgenda;
        this.idVeterinario = idVeterinario;
        this.nombreVeterinario = nombreVeterinario;
        this.fecha = fecha;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
        this.idTipoTurno = idTipoTurno;
        this.nombreTipoTurno = nombreTipoTurno;
        this.duracionMinutos = duracionMinutos;
        this.observaciones = observaciones;
        this.estado = estado;
        this.nombreUsuario = nombreUsuario;
    }

    // Getters y Setters
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

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public LocalTime getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(LocalTime horaInicio) {
        this.horaInicio = horaInicio;
    }

    public LocalTime getHoraFin() {
        return horaFin;
    }

    public void setHoraFin(LocalTime horaFin) {
        this.horaFin = horaFin;
    }

    public int getIdTipoTurno() {
        return idTipoTurno;
    }

    public void setIdTipoTurno(int idTipoTurno) {
        this.idTipoTurno = idTipoTurno;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public int getCreadoPor() {
        return creadoPor;
    }

    public void setCreadoPor(int creadoPor) {
        this.creadoPor = creadoPor;
    }

    public LocalDateTime getCreadoEn() {
        return creadoEn;
    }

    public void setCreadoEn(LocalDateTime creadoEn) {
        this.creadoEn = creadoEn;
    }

    public String getNombreVeterinario() {
        return nombreVeterinario;
    }

    public void setNombreVeterinario(String nombreVeterinario) {
        this.nombreVeterinario = nombreVeterinario;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public Integer getModificadoPor() {
        return modificadoPor;
    }

    public void setModificadoPor(Integer modificadoPor) {
        this.modificadoPor = modificadoPor;
    }

    public LocalDateTime getModificadoEn() {
        return modificadoEn;
    }

    public void setModificadoEn(LocalDateTime modificadoEn) {
        this.modificadoEn = modificadoEn;
    }

    public Integer getAnuladoPor() {
        return anuladoPor;
    }

    public void setAnuladoPor(Integer anuladoPor) {
        this.anuladoPor = anuladoPor;
    }

    public LocalDateTime getAnuladoEn() {
        return anuladoEn;
    }

    public void setAnuladoEn(LocalDateTime anuladoEn) {
        this.anuladoEn = anuladoEn;
    }

    public String getMotivoAnulacion() {
        return motivoAnulacion;
    }

    public void setMotivoAnulacion(String motivoAnulacion) {
        this.motivoAnulacion = motivoAnulacion;
    }

    // ✅ NUEVOS Getters y Setters
    public String getNombreTipoTurno() {
        return nombreTipoTurno;
    }

    public void setNombreTipoTurno(String nombreTipoTurno) {
        this.nombreTipoTurno = nombreTipoTurno;
    }

    public Integer getDuracionMinutos() {
        return duracionMinutos;
    }

    public void setDuracionMinutos(Integer duracionMinutos) {
        this.duracionMinutos = duracionMinutos;
    }

    // ✅ Método para debugging
    @Override
    public String toString() {
        return "Agenda{" +
                "idAgenda=" + idAgenda +
                ", veterinario=" + nombreVeterinario +
                ", fecha=" + fecha +
                ", horaInicio=" + horaInicio +
                ", horaFin=" + horaFin +
                ", tipoTurno=" + nombreTipoTurno +
                ", estado=" + estado +
                '}';
    }

    // ✅ Método útil para mostrar en combobox
    public String getDescripcionCompleta() {
        return nombreVeterinario + " - " + fecha + " " + horaInicio + " a " + horaFin + 
               " (" + nombreTipoTurno + ")";
    }
}
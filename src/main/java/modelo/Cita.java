package modelo;

import java.time.LocalDateTime;

public class Cita {

    private int idCita;
    private int idCliente;
    private int idMascota;
    private int idVeterinario;
    private int idSlot; // Cambiado de idAgenda a idSlot para coincidir con la BD
    private String motivo;
    private String observaciones;
    private String diagnostico;
    private String tratamiento;
    private String estado; // RESERVADA, CONFIRMADA, COMPLETADA, CANCELADA
    private LocalDateTime fechaCreacion;
    private int creadoPor;
    private LocalDateTime fechaModificacion;
    private int modificadoPor;
    private String nombreEspecie;
    private String nombreRaza;

    // Campos para mostrar en listados (no en BD)
    private String nombreCliente;
    private String nombreMascota;
    private String nombreVeterinario;
    private String fechaCita;  // formato "yyyy-MM-dd"
    private String horaCita;   // formato "HH:mm" (24 horas)

    // Constructores
    public Cita() {
    }

    public Cita(int idCliente, int idMascota, int idVeterinario, int idSlot,
            String motivo, int creadoPor) {
        this.idCliente = idCliente;
        this.idMascota = idMascota;
        this.idVeterinario = idVeterinario;
        this.idSlot = idSlot;
        this.motivo = motivo;
        this.creadoPor = creadoPor;
        this.estado = "RESERVADA";
    }

    // Getters y Setters
    public int getIdCita() {
        return idCita;
    }

    public void setIdCita(int idCita) {
        this.idCita = idCita;
    }

    public int getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(int idCliente) {
        this.idCliente = idCliente;
    }

    public int getIdMascota() {
        return idMascota;
    }

    public void setIdMascota(int idMascota) {
        this.idMascota = idMascota;
    }

    public int getIdVeterinario() {
        return idVeterinario;
    }

    public void setIdVeterinario(int idVeterinario) {
        this.idVeterinario = idVeterinario;
    }

    public int getIdSlot() {
        return idSlot;
    }

    public void setIdSlot(int idSlot) {
        this.idSlot = idSlot;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public String getDiagnostico() {
        return diagnostico;
    }

    public void setDiagnostico(String diagnostico) {
        this.diagnostico = diagnostico;
    }

    public String getTratamiento() {
        return tratamiento;
    }

    public void setTratamiento(String tratamiento) {
        this.tratamiento = tratamiento;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public int getCreadoPor() {
        return creadoPor;
    }

    public void setCreadoPor(int creadoPor) {
        this.creadoPor = creadoPor;
    }

    public LocalDateTime getFechaModificacion() {
        return fechaModificacion;
    }

    public void setFechaModificacion(LocalDateTime fechaModificacion) {
        this.fechaModificacion = fechaModificacion;
    }

    public int getModificadoPor() {
        return modificadoPor;
    }

    public void setModificadoPor(int modificadoPor) {
        this.modificadoPor = modificadoPor;
    }

    // Campos para mostrar
    public String getNombreCliente() {
        return nombreCliente;
    }

    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
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

    public String getFechaCita() {
        return fechaCita;
    }

    public void setFechaCita(String fechaCita) {
        this.fechaCita = fechaCita;
    }

    public String getHoraCita() {
        return horaCita;
    }

    public void setHoraCita(String horaCita) {
        this.horaCita = horaCita;
    }

    public String getNombreEspecie() {
        return nombreEspecie;
    }

    public void setNombreEspecie(String nombreEspecie) {
        this.nombreEspecie = nombreEspecie;
    }

    public String getNombreRaza() {
        return nombreRaza;
    }

    public void setNombreRaza(String nombreRaza) {
        this.nombreRaza = nombreRaza;
    }
}

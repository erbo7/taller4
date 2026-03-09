package modelo;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Documento {

    private int idDocumento;
    private int idMascota;
    private Integer idFicha; // ← con mayúscula, tipo objeto
    private int idTipoDocVet; // ✅ Cambio para coincidir con BD
    private String nombreArchivo;
    private String rutaArchivo;
    private String formato;
    private long tamanoBytes;
    private Integer tamanoKb; // Calculado
    private LocalDate fechaDocumento;
    private String observaciones;
    private String estado;
    private LocalDateTime creadoEn;
    private Integer idConsulta;

    // Datos adicionales para consultas
    private String nombreMascota;
    private String nombreCliente;
    private String nombreTipoDocumento;
    private boolean esObligatorio;

    // Constructor vacío
    public Documento() {
    }

    // Constructor para insertar
    // Constructor para insertar
    public Documento(int idMascota, Integer idFicha, int idTipoDocVet,
            String nombreArchivo, String rutaArchivo, String formato,
            long tamanoBytes, LocalDate fechaDocumento,
            String observaciones, Integer idConsulta) {
        this.idMascota = idMascota;
        this.idFicha = idFicha;
        this.idTipoDocVet = idTipoDocVet;
        this.nombreArchivo = nombreArchivo;
        // 👇 SOLO GUARDA EL NOMBRE DEL ARCHIVO (no la ruta completa)
        this.rutaArchivo = nombreArchivo; // ← CORREGIDO
        this.formato = formato;
        this.tamanoBytes = tamanoBytes;
        this.fechaDocumento = fechaDocumento;
        this.observaciones = observaciones;
        this.idConsulta = idConsulta;
    }

    // Getters y Setters
    public int getIdDocumento() {
        return idDocumento;
    }

    public void setIdDocumento(int idDocumento) {
        this.idDocumento = idDocumento;
    }

    public int getIdMascota() {
        return idMascota;
    }

    public void setIdMascota(int idMascota) {
        this.idMascota = idMascota;
    }

    public Integer getIdFicha() {
        return idFicha;
    }

    public void setIdFicha(Integer idFicha) {
        this.idFicha = idFicha;
    }

    public int getIdTipoDocVet() {
        return idTipoDocVet;
    }

    public void setIdTipoDocVet(int idTipoDocVet) {
        this.idTipoDocVet = idTipoDocVet;
    }

    public String getNombreArchivo() {
        return nombreArchivo;
    }

    public void setNombreArchivo(String nombreArchivo) {
        this.nombreArchivo = nombreArchivo;
    }

    public String getRutaArchivo() {
        return rutaArchivo;
    }

    public void setRutaArchivo(String rutaArchivo) {
        this.rutaArchivo = rutaArchivo;
    }

    public String getFormato() {
        return formato;
    }

    public void setFormato(String formato) {
        this.formato = formato;
    }

    public long getTamanoBytes() {
        return tamanoBytes;
    }

    public void setTamanoBytes(long tamanoBytes) {
        this.tamanoBytes = tamanoBytes;
    }

    public Integer getTamanoKb() {
        return tamanoKb;
    }

    public void setTamanoKb(Integer tamanoKb) {
        this.tamanoKb = tamanoKb;
    }

    public LocalDate getFechaDocumento() {
        return fechaDocumento;
    }

    public void setFechaDocumento(LocalDate fechaDocumento) {
        this.fechaDocumento = fechaDocumento;
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

    public LocalDateTime getCreadoEn() {
        return creadoEn;
    }

    public void setCreadoEn(LocalDateTime creadoEn) {
        this.creadoEn = creadoEn;
    }

    public Integer getIdConsulta() {
        return idConsulta;
    }

    public void setIdConsulta(Integer idConsulta) {
        this.idConsulta = idConsulta;
    }

    public String getNombreMascota() {
        return nombreMascota;
    }

    public void setNombreMascota(String nombreMascota) {
        this.nombreMascota = nombreMascota;
    }

    public String getNombreCliente() {
        return nombreCliente;
    }

    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }

    public String getNombreTipoDocumento() {
        return nombreTipoDocumento;
    }

    public void setNombreTipoDocumento(String nombreTipoDocumento) {
        this.nombreTipoDocumento = nombreTipoDocumento;
    }

    public boolean isEsObligatorio() {
        return esObligatorio;
    }

    public void setEsObligatorio(boolean esObligatorio) {
        this.esObligatorio = esObligatorio;
    }
}

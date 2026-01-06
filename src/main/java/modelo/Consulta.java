// modelo/Consulta.java
package modelo;

import java.math.BigDecimal;

public class Consulta {
    private int idConsulta;
    private Integer idCita;
    private int idVeterinario;
    private int idMascota;
    private String motivo;
    private BigDecimal peso;
    private BigDecimal temperatura;
    private Integer frecuenciaCardiaca;
    private Integer frecuenciaRespiratoria;
    private String mucosas;
    private String capReflejo;
    private String condicionCorporal;
    private String observaciones;
    private String estado; // "EN CURSO", "FINALIZADA", "ANULADA"
    private String fechaConsulta;

    // Getters y Setters
    public int getIdConsulta() { return idConsulta; }
    public void setIdConsulta(int idConsulta) { this.idConsulta = idConsulta; }

    public Integer getIdCita() { return idCita; }
    public void setIdCita(Integer idCita) { this.idCita = idCita; }

    public int getIdVeterinario() { return idVeterinario; }
    public void setIdVeterinario(int idVeterinario) { this.idVeterinario = idVeterinario; }

    public int getIdMascota() { return idMascota; }
    public void setIdMascota(int idMascota) { this.idMascota = idMascota; }

    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }

    public BigDecimal getPeso() { return peso; }
    public void setPeso(BigDecimal peso) { this.peso = peso; }

    public BigDecimal getTemperatura() { return temperatura; }
    public void setTemperatura(BigDecimal temperatura) { this.temperatura = temperatura; }

    public Integer getFrecuenciaCardiaca() { return frecuenciaCardiaca; }
    public void setFrecuenciaCardiaca(Integer frecuenciaCardiaca) { this.frecuenciaCardiaca = frecuenciaCardiaca; }

    public Integer getFrecuenciaRespiratoria() { return frecuenciaRespiratoria; }
    public void setFrecuenciaRespiratoria(Integer frecuenciaRespiratoria) { this.frecuenciaRespiratoria = frecuenciaRespiratoria; }

    public String getMucosas() { return mucosas; }
    public void setMucosas(String mucosas) { this.mucosas = mucosas; }

    public String getCapReflejo() { return capReflejo; }
    public void setCapReflejo(String capReflejo) { this.capReflejo = capReflejo; }

    public String getCondicionCorporal() { return condicionCorporal; }
    public void setCondicionCorporal(String condicionCorporal) { this.condicionCorporal = condicionCorporal; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getFechaConsulta() { return fechaConsulta; }
    public void setFechaConsulta(String fechaConsulta) { this.fechaConsulta = fechaConsulta; }
}
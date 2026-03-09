package modelo;

/**
 * Clase modelo RecepcionistaStats Contiene las estadísticas del dashboard del
 * recepcionista
 */
public class RecepcionistaStats {

    private int citasHoy;
    private int citasSemana;
    private int recordatoriosPendientes;
    private int clientesAtendidosHoy;
    private int clientesAtendidosMes;
    private int citasPendientes;
    private int citasConfirmadas;

    // Constructor vacío
    public RecepcionistaStats() {
    }

    // Constructor con parámetros principales
    public RecepcionistaStats(int citasHoy, int recordatoriosPendientes, int clientesAtendidosHoy) {
        this.citasHoy = citasHoy;
        this.recordatoriosPendientes = recordatoriosPendientes;
        this.clientesAtendidosHoy = clientesAtendidosHoy;
    }

    // Getters y Setters
    public int getCitasHoy() {
        return citasHoy;
    }

    public void setCitasHoy(int citasHoy) {
        this.citasHoy = citasHoy;
    }

    public int getCitasSemana() {
        return citasSemana;
    }

    public void setCitasSemana(int citasSemana) {
        this.citasSemana = citasSemana;
    }

    public int getRecordatoriosPendientes() {
        return recordatoriosPendientes;
    }

    public void setRecordatoriosPendientes(int recordatoriosPendientes) {
        this.recordatoriosPendientes = recordatoriosPendientes;
    }

    public int getClientesAtendidosHoy() {
        return clientesAtendidosHoy;
    }

    public void setClientesAtendidosHoy(int clientesAtendidosHoy) {
        this.clientesAtendidosHoy = clientesAtendidosHoy;
    }

    public int getClientesAtendidosMes() {
        return clientesAtendidosMes;
    }

    public void setClientesAtendidosMes(int clientesAtendidosMes) {
        this.clientesAtendidosMes = clientesAtendidosMes;
    }

    public int getCitasPendientes() {
        return citasPendientes;
    }

    public void setCitasPendientes(int citasPendientes) {
        this.citasPendientes = citasPendientes;
    }

    public int getCitasConfirmadas() {
        return citasConfirmadas;
    }

    public void setCitasConfirmadas(int citasConfirmadas) {
        this.citasConfirmadas = citasConfirmadas;
    }

    @Override
    public String toString() {
        return "RecepcionistaStats{"
                + "citasHoy=" + citasHoy
                + ", citasSemana=" + citasSemana
                + ", recordatoriosPendientes=" + recordatoriosPendientes
                + ", clientesAtendidosHoy=" + clientesAtendidosHoy
                + ", clientesAtendidosMes=" + clientesAtendidosMes
                + ", citasPendientes=" + citasPendientes
                + ", citasConfirmadas=" + citasConfirmadas
                + '}';
    }
}

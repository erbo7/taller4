package controlador;

import cx.RecordatorioDAO;
import modelo.Recordatorio;
import modelo.RecordatorioConfig;
import modelo.EstadisticaRecordatorio;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Date;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

@WebServlet("/RecordatoriosServlet")
public class RecordatorioServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private RecordatorioDAO dao;
    private SimpleDateFormat sdfFechaHora = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    private SimpleDateFormat sdfFecha = new SimpleDateFormat("dd/MM/yyyy");

    @Override
    public void init() throws ServletException {
        dao = new RecordatorioDAO();
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");
        request.setCharacterEncoding("UTF-8");

        String accion = request.getParameter("accion");

        if (accion == null || accion.trim().isEmpty()) {
            accion = "listar";
        }

        try {
            switch (accion) {
                case "listar":
                    listarRecordatorios(request, response);
                    break;

                case "generar":
                    generarRecordatorios(request, response);
                    break;

                case "procesar":
                    procesarEnvios(request, response);
                    break;

                case "reintentar":
                    reintentarFallados(request, response);
                    break;

                case "obtenerConfiguracion":
                    obtenerConfiguracion(request, response);
                    break;

                case "actualizarConfig":
                    actualizarConfiguracion(request, response);
                    break;

                case "obtenerEstadisticas":
                    obtenerEstadisticas(request, response);
                    break;

                case "filtrar":
                    filtrarRecordatorios(request, response);
                    break;

                case "pendientesHoy":
                    obtenerPendientesHoy(request, response);
                    break;

                case "obtenerEstadisticasSemanales":
                    obtenerEstadisticasSemanales(request, response);
                    break;

                default:
                    enviarErrorHTML(response, "Acción no válida: " + accion);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            enviarErrorHTML(response, "Error en el servidor: " + e.getMessage());
        }
    }

    // ==============================================
    // 1. LISTAR RECORDATORIOS (devuelve HTML de tabla)
    // ==============================================
    private void listarRecordatorios(HttpServletRequest request, HttpServletResponse response)
            throws Exception {

        String estado = request.getParameter("estado");
        String canal = request.getParameter("canal");
        String fechaStr = request.getParameter("fecha");
        String idCitaStr = request.getParameter("idCita");

        // Convertir parámetros
        Date fecha = null;
        if (fechaStr != null && !fechaStr.isEmpty()) {
            fecha = Date.valueOf(fechaStr);
        }

        Integer idCita = null;
        if (idCitaStr != null && !idCitaStr.isEmpty()) {
            idCita = Integer.parseInt(idCitaStr);
        }

        // Obtener datos
        List<Recordatorio> recordatorios = dao.obtenerRecordatoriosFiltrados(estado, canal, fecha, idCita);

        // Generar HTML
        StringBuilder html = new StringBuilder();

        if (recordatorios.isEmpty()) {
            html.append("<tr>")
                    .append("<td colspan='10' class='text-center py-5'>")
                    .append("<div class='empty-state'>")
                    .append("<i class='fas fa-inbox'></i>")
                    .append("<h4>No hay recordatorios</h4>")
                    .append("<p>No se encontraron recordatorios con los filtros aplicados</p>")
                    .append("</div>")
                    .append("</td>")
                    .append("</tr>");
        } else {
            for (Recordatorio rec : recordatorios) {
                html.append("<tr>")
                        .append("<td>").append(rec.getIdRecordatorio()).append("</td>")
                        .append("<td><strong>#").append(rec.getIdCita()).append("</strong></td>")
                        .append("<td>")
                        .append("<div>").append(rec.getNombreCliente() != null ? rec.getNombreCliente() : "").append("</div>");

                if (rec.getTelefonoCliente() != null && !rec.getTelefonoCliente().isEmpty()) {
                    html.append("<small class='text-muted'>")
                            .append("<i class='fas fa-phone'></i> ").append(rec.getTelefonoCliente())
                            .append("</small>");
                }

                html.append("</td>")
                        .append("<td>").append(rec.getNombreMascota() != null ? rec.getNombreMascota() : "").append("</td>")
                        .append("<td>").append(rec.getNombreVeterinario() != null ? rec.getNombreVeterinario() : "").append("</td>")
                        .append("<td>");

                if (rec.getFechaHoraCita() != null) {
                    html.append(sdfFechaHora.format(rec.getFechaHoraCita()));
                }

                html.append("</td>")
                        .append("<td>");

                // Badge de canal
                if (rec.getCanal() != null) {
                    String canalClass = "";
                    switch (rec.getCanal().toUpperCase()) {
                        case "WHATSAPP":
                            canalClass = "canal-whatsapp";
                            break;
                        case "SMS":
                            canalClass = "canal-sms";
                            break;
                        case "EMAIL":
                            canalClass = "canal-email";
                            break;
                        default:
                            canalClass = "badge-secondary";
                    }
                    html.append("<span class='canal-badge ").append(canalClass).append("'>")
                            .append("<i class='fas ")
                            .append(rec.getCanal().equalsIgnoreCase("WHATSAPP") ? "fa-whatsapp"
                                    : rec.getCanal().equalsIgnoreCase("SMS") ? "fa-sms" : "fa-envelope")
                            .append("'></i> ")
                            .append(rec.getCanal())
                            .append("</span>");
                }

                html.append("</td>")
                        .append("<td>");

                // Badge de estado
                if (rec.getEstadoEnvio() != null) {
                    String estadoClass = "";
                    String estadoText = rec.getEstadoEnvio();

                    switch (estadoText.toUpperCase()) {
                        case "ENVIADO":
                            estadoClass = "badge-enviado";
                            break;
                        case "PENDIENTE":
                            estadoClass = "badge-pendiente";
                            break;
                        case "FALLADO":
                            estadoClass = "badge-fallado";
                            break;
                        case "REINTENTO":
                            estadoClass = "badge-reintento";
                            break;
                        default:
                            estadoClass = "badge-secondary";
                    }
                    html.append("<span class='badge-estado ").append(estadoClass).append("'>")
                            .append(estadoText)
                            .append("</span>");
                }

                html.append("</td>")
                        .append("<td class='text-center'>")
                        .append("<span class='badge bg-secondary'>").append(rec.getIntentosRealizados()).append("</span>")
                        .append("</td>")
                        .append("<td>");

                if (rec.getFechaEnvio() != null) {
                    html.append(sdfFechaHora.format(rec.getFechaEnvio()));
                }

                html.append("</td>")
                        .append("</tr>");
            }
        }

        response.getWriter().write(html.toString());
    }

    // ==============================================
    // 2. GENERAR RECORDATORIOS (devuelve mensaje simple)
    // ==============================================
    private void generarRecordatorios(HttpServletRequest request, HttpServletResponse response)
            throws Exception {

        Map<String, Object> resultado = dao.generarRecordatoriosDiarios();
        boolean exito = resultado.containsKey("exito") && (Boolean) resultado.get("exito");
        String mensaje = resultado.containsKey("mensaje") ? (String) resultado.get("mensaje") : "Sin mensaje";

        // Devolver "OK|mensaje" o "ERROR|mensaje"
        response.getWriter().write((exito ? "OK" : "ERROR") + "|" + mensaje);
    }

    // ==============================================
    // 3. PROCESAR ENVÍOS (devuelve mensaje simple)
    // ==============================================
    private void procesarEnvios(HttpServletRequest request, HttpServletResponse response)
            throws Exception {

        Map<String, Object> resultado = dao.procesarEnviosPendientes();
        boolean exito = resultado.containsKey("exito") && (Boolean) resultado.get("exito");
        String mensaje = resultado.containsKey("mensaje") ? (String) resultado.get("mensaje") : "Sin mensaje";

        response.getWriter().write((exito ? "OK" : "ERROR") + "|" + mensaje);
    }

    // ==============================================
    // 4. REINTENTAR FALLADOS (devuelve mensaje simple)
    // ==============================================
    private void reintentarFallados(HttpServletRequest request, HttpServletResponse response)
            throws Exception {

        Map<String, Object> resultado = dao.reintentarEnviosFallados();
        boolean exito = resultado.containsKey("exito") && (Boolean) resultado.get("exito");
        String mensaje = resultado.containsKey("mensaje") ? (String) resultado.get("mensaje") : "Sin mensaje";

        response.getWriter().write((exito ? "OK" : "ERROR") + "|" + mensaje);
    }

    // ==============================================
    // 5. OBTENER CONFIGURACIÓN (devuelve HTML del formulario)
    // ==============================================
    private void obtenerConfiguracion(HttpServletRequest request, HttpServletResponse response)
            throws Exception {

        RecordatorioConfig config = dao.obtenerConfiguracion();

        StringBuilder html = new StringBuilder();

        if (config != null) {
            html.append("<form id='formConfig' onsubmit='return false;'>")
                    .append("<input type='hidden' name='accion' value='actualizarConfig'>")
                    .append("<input type='hidden' name='idConfig' value='").append(config.getIdConfig()).append("'>")
                    .append("<div class='row'>")
                    .append("<div class='col-md-6 mb-3'>")
                    .append("<label class='form-label'>Horas antes de la cita</label>")
                    .append("<input type='number' name='horasAntes' class='form-control' ")
                    .append("value='").append(config.getHorasAntes()).append("' min='1' max='168' required>")
                    .append("<small class='text-muted'>Tiempo de anticipación para enviar el recordatorio (1-168 horas)</small>")
                    .append("</div>")
                    .append("<div class='col-md-6 mb-3'>")
                    .append("<label class='form-label'>Hora de envío</label>")
                    .append("<input type='time' name='horaEnvio' class='form-control' ")
                    .append("value='").append(config.getHoraEnvio() != null ? config.getHoraEnvio().toString().substring(0, 5) : "09:00").append("' required>")
                    .append("<small class='text-muted'>Hora en la que se procesarán los envíos automáticamente</small>")
                    .append("</div>")
                    .append("</div>")
                    .append("<div class='row'>")
                    .append("<div class='col-md-6 mb-3'>")
                    .append("<label class='form-label'>Canal principal</label>")
                    .append("<select name='canalPrincipal' class='form-select' required>")
                    .append("<option value='WHATSAPP' ").append("WHATSAPP".equals(config.getCanalPrincipal()) ? "selected" : "").append(">WhatsApp</option>")
                    .append("<option value='SMS' ").append("SMS".equals(config.getCanalPrincipal()) ? "selected" : "").append(">SMS</option>")
                    .append("<option value='EMAIL' ").append("EMAIL".equals(config.getCanalPrincipal()) ? "selected" : "").append(">Email</option>")
                    .append("</select>")
                    .append("</div>")
                    .append("<div class='col-md-6 mb-3'>")
                    .append("<label class='form-label'>Máximo de reintentos</label>")
                    .append("<input type='number' name='maxReintentos' class='form-control' ")
                    .append("value='").append(config.getMaxReintentos()).append("' min='1' max='10' required>")
                    .append("<small class='text-muted'>Cantidad de reintentos en caso de fallo (1-10)</small>")
                    .append("</div>")
                    .append("</div>")
                    .append("<div class='mb-3'>")
                    .append("<label class='form-label'>Plantilla de mensaje</label>")
                    .append("<textarea name='plantillaMensaje' class='form-control' rows='4' required>")
                    .append(config.getPlantillaMensaje() != null ? config.getPlantillaMensaje() : "")
                    .append("</textarea>")
                    .append("<small class='text-muted'>Variables disponibles: {CLIENTE}, {MASCOTA}, {FECHA}, {HORA}, {VETERINARIO}</small>")
                    .append("</div>")
                    .append("<div class='mb-3'>")
                    .append("<div class='form-check form-switch'>")
                    .append("<input class='form-check-input' type='checkbox' name='habilitado' id='habilitado' ")
                    .append(config.isHabilitado() ? "checked" : "").append(">")
                    .append("<label class='form-check-label' for='habilitado'>Sistema de recordatorios habilitado</label>")
                    .append("</div>")
                    .append("</div>");

            if (config.getModificadoEn() != null) {
                html.append("<div class='alert alert-info'>")
                        .append("<i class='fas fa-info-circle'></i> Última modificación: ")
                        .append(sdfFechaHora.format(config.getModificadoEn()))
                        .append("</div>");
            }

            html.append("<div class='text-end'>")
                    .append("<button type='button' class='btn-primary-custom btn-lg' onclick='guardarConfiguracion()'>")
                    .append("<i class='fas fa-save'></i> Guardar Configuración")
                    .append("</button>")
                    .append("</div>")
                    .append("</form>");
        } else {
            html.append("<div class='alert alert-danger'>No se pudo cargar la configuración</div>");
        }

        response.getWriter().write(html.toString());
    }

    // ==============================================
    // 6. ACTUALIZAR CONFIGURACIÓN
    // ==============================================
    // ==============================================
// 6. ACTUALIZAR CONFIGURACIÓN
// ==============================================
    private void actualizarConfiguracion(HttpServletRequest request, HttpServletResponse response)
            throws Exception {

        HttpSession session = request.getSession();

        try {
            System.out.println("====== ACTUALIZAR CONFIG ======");

            // Obtener parámetros
            String horasAntesStr = request.getParameter("horasAntes");
            String horaEnvioStr = request.getParameter("horaEnvio");
            String canalPrincipal = request.getParameter("canalPrincipal");
            String plantillaMensaje = request.getParameter("plantillaMensaje");
            String maxReintentosStr = request.getParameter("maxReintentos");
            String habilitadoStr = request.getParameter("habilitado");

            System.out.println("Parámetros recibidos:");
            System.out.println("  horasAntes: " + horasAntesStr);
            System.out.println("  horaEnvio: " + horaEnvioStr);
            System.out.println("  canalPrincipal: " + canalPrincipal);
            System.out.println("  maxReintentos: " + maxReintentosStr);
            System.out.println("  habilitado: " + habilitadoStr);

            int horasAntes = Integer.parseInt(horasAntesStr);
            int maxReintentos = Integer.parseInt(maxReintentosStr);
            boolean habilitado = "on".equals(habilitadoStr);

            // Convertir hora
            Time horaEnvio = Time.valueOf(horaEnvioStr + ":00");

            // Obtener ID de usuario
            Integer idUsuario = (Integer) session.getAttribute("idUsuario");
            if (idUsuario == null) {
                idUsuario = 1;
            }

            System.out.println("  idUsuario: " + idUsuario);

            // Crear y actualizar configuración
            RecordatorioConfig config = new RecordatorioConfig();
            config.setHorasAntes(horasAntes);
            config.setHoraEnvio(horaEnvio);
            config.setCanalPrincipal(canalPrincipal);
            config.setPlantillaMensaje(plantillaMensaje);
            config.setMaxReintentos(maxReintentos);
            config.setHabilitado(habilitado);
            config.setModificadoPor(idUsuario);

            System.out.println("Llamando a DAO.actualizarConfiguracion...");
            boolean exito = dao.actualizarConfiguracion(config);
            System.out.println("Resultado: " + exito);

            String respuesta = exito ? "OK|Configuración actualizada correctamente"
                    : "ERROR|Error al actualizar configuración";

            System.out.println("Respuesta a enviar: " + respuesta);
            response.getWriter().write(respuesta);

        } catch (Exception e) {
            System.err.println("ERROR en actualizarConfiguracion: " + e.getMessage());
            e.printStackTrace();
            response.getWriter().write("ERROR|Error: " + e.getMessage());
        }
    }

    // ==============================================
    // 7. OBTENER ESTADÍSTICAS (devuelve HTML de stats)
    // ==============================================
    private void obtenerEstadisticas(HttpServletRequest request, HttpServletResponse response)
            throws Exception {

        String fechaStr = request.getParameter("fecha");
        Date fecha = (fechaStr != null && !fechaStr.isEmpty())
                ? Date.valueOf(fechaStr) : new Date(System.currentTimeMillis());

        EstadisticaRecordatorio stats = dao.obtenerEstadisticasRecordatorios(fecha);

        StringBuilder html = new StringBuilder();

        if (stats != null) {
            html.append("<div class='stats-grid'>")
                    .append("<div class='stat-card blue'>")
                    .append("<div class='stat-card-icon'><i class='fas fa-list'></i></div>")
                    .append("<h6>TOTAL</h6>")
                    .append("<h2>").append(stats.getTotalRecordatorios()).append("</h2>")
                    .append("</div>")
                    .append("<div class='stat-card green'>")
                    .append("<div class='stat-card-icon'><i class='fas fa-check'></i></div>")
                    .append("<h6>ENVIADOS</h6>")
                    .append("<h2>").append(stats.getEnviados()).append("</h2>")
                    .append("<small>").append(String.format("%.1f", stats.getPorcentajeEnviados())).append("%</small>")
                    .append("</div>")
                    .append("<div class='stat-card red'>")
                    .append("<div class='stat-card-icon'><i class='fas fa-times'></i></div>")
                    .append("<h6>FALLADOS</h6>")
                    .append("<h2>").append(stats.getFallados()).append("</h2>")
                    .append("<small>").append(String.format("%.1f", stats.getPorcentajeFallados())).append("%</small>")
                    .append("</div>")
                    .append("<div class='stat-card orange'>")
                    .append("<div class='stat-card-icon'><i class='fas fa-clock'></i></div>")
                    .append("<h6>PENDIENTES</h6>")
                    .append("<h2>").append(stats.getPendientes()).append("</h2>")
                    .append("<small>").append(String.format("%.1f", stats.getPorcentajePendientes())).append("%</small>")
                    .append("</div>")
                    .append("</div>")
                    .append("<div class='alert alert-info'>")
                    .append("<i class='fas fa-info-circle'></i>")
                    .append(" Promedio de intentos: <strong>").append(String.format("%.2f", stats.getPromedioIntentos())).append("</strong>")
                    .append("</div>");
        } else {
            html.append("<div class='alert alert-warning'>No hay estadísticas disponibles</div>");
        }

        response.getWriter().write(html.toString());
    }

    // ==============================================
    // 8. FILTRAR RECORDATORIOS (igual que listar)
    // ==============================================
    private void filtrarRecordatorios(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        listarRecordatorios(request, response);
    }

    // ==============================================
    // 9. OBTENER PENDIENTES HOY
    // ==============================================
    private void obtenerPendientesHoy(HttpServletRequest request, HttpServletResponse response)
            throws Exception {

        List<Recordatorio> pendientes = dao.obtenerRecordatoriosPendientesHoy();

        StringBuilder html = new StringBuilder();

        if (pendientes.isEmpty()) {
            html.append("<tr>")
                    .append("<td colspan='8' class='text-center py-5'>")
                    .append("<div class='empty-state'>")
                    .append("<i class='fas fa-check-circle text-success'></i>")
                    .append("<h4>¡Todo al día!</h4>")
                    .append("<p>No hay recordatorios pendientes para hoy</p>")
                    .append("</div>")
                    .append("</td>")
                    .append("</tr>");
        } else {
            for (Recordatorio rec : pendientes) {
                html.append("<tr>")
                        .append("<td>").append(rec.getNombreCliente() != null ? rec.getNombreCliente() : "").append("</td>")
                        .append("<td>");

                if (rec.getTelefonoCliente() != null && !rec.getTelefonoCliente().isEmpty()) {
                    html.append("<div><i class='fas fa-phone'></i> ").append(rec.getTelefonoCliente()).append("</div>");
                }
                if (rec.getEmailCliente() != null && !rec.getEmailCliente().isEmpty()) {
                    html.append("<div><i class='fas fa-envelope'></i> ").append(rec.getEmailCliente()).append("</div>");
                }

                html.append("</td>")
                        .append("<td>").append(rec.getNombreMascota() != null ? rec.getNombreMascota() : "").append("</td>")
                        .append("<td>").append(rec.getNombreVeterinario() != null ? rec.getNombreVeterinario() : "").append("</td>")
                        .append("<td>");

                if (rec.getFechaHoraCita() != null) {
                    html.append(sdfFechaHora.format(rec.getFechaHoraCita()));
                }

                html.append("</td>")
                        .append("<td>");

                if (rec.getCanal() != null) {
                    String canalClass = "";
                    switch (rec.getCanal().toUpperCase()) {
                        case "WHATSAPP":
                            canalClass = "canal-whatsapp";
                            break;
                        case "SMS":
                            canalClass = "canal-sms";
                            break;
                        case "EMAIL":
                            canalClass = "canal-email";
                            break;
                    }
                    html.append("<span class='canal-badge ").append(canalClass).append("'>")
                            .append("<i class='fas ")
                            .append(rec.getCanal().equalsIgnoreCase("WHATSAPP") ? "fa-whatsapp"
                                    : rec.getCanal().equalsIgnoreCase("SMS") ? "fa-sms" : "fa-envelope")
                            .append("'></i>")
                            .append("</span>");
                }

                html.append("</td>")
                        .append("<td>");

                if (rec.getEstadoEnvio() != null) {
                    String estadoClass = "";
                    switch (rec.getEstadoEnvio().toUpperCase()) {
                        case "PENDIENTE":
                            estadoClass = "badge-pendiente";
                            break;
                        default:
                            estadoClass = "badge-secondary";
                    }
                    html.append("<span class='badge-estado ").append(estadoClass).append("'>")
                            .append(rec.getEstadoEnvio())
                            .append("</span>");
                }

                html.append("</td>")
                        .append("<td>")
                        .append("<button class='btn btn-sm btn-info' onclick=\"verMensaje('")
                        .append(rec.getMensajeEnviado() != null ? rec.getMensajeEnviado().replace("'", "\\'") : "")
                        .append("')\">")
                        .append("<i class='fas fa-eye'></i> Ver")
                        .append("</button>")
                        .append("</td>")
                        .append("</tr>");
            }
        }

        response.getWriter().write(html.toString());
    }

    // ==============================================
    // 10. OBTENER ESTADÍSTICAS SEMANALES
    // ==============================================
    private void obtenerEstadisticasSemanales(HttpServletRequest request, HttpServletResponse response)
            throws Exception {

        List<EstadisticaRecordatorio> statsList = dao.obtenerEstadisticasSemanales();

        StringBuilder html = new StringBuilder();

        if (statsList.isEmpty()) {
            html.append("<tr><td colspan='6' class='text-center text-muted'>No hay estadísticas disponibles</td></tr>");
        } else {
            for (EstadisticaRecordatorio stat : statsList) {
                html.append("<tr>")
                        .append("<td>").append(stat.getFecha() != null ? sdfFecha.format(stat.getFecha()) : "").append("</td>")
                        .append("<td><strong>").append(stat.getTotalRecordatorios()).append("</strong></td>")
                        .append("<td>")
                        .append("<span class='text-success'>").append(stat.getEnviados()).append("</span>")
                        .append("<small class='text-muted'> (")
                        .append(String.format("%.1f", stat.getPorcentajeEnviados()))
                        .append("%)</small>")
                        .append("</td>")
                        .append("<td>")
                        .append("<span class='text-danger'>").append(stat.getFallados()).append("</span>")
                        .append("<small class='text-muted'> (")
                        .append(String.format("%.1f", stat.getPorcentajeFallados()))
                        .append("%)</small>")
                        .append("</td>")
                        .append("<td>")
                        .append("<span class='text-warning'>").append(stat.getPendientes()).append("</span>")
                        .append("<small class='text-muted'> (")
                        .append(String.format("%.1f", stat.getPorcentajePendientes()))
                        .append("%)</small>")
                        .append("</td>")
                        .append("<td>").append(String.format("%.2f", stat.getPromedioIntentos())).append("</td>")
                        .append("</tr>");
            }
        }

        response.getWriter().write(html.toString());
    }

    // ==============================================
    // MÉTODOS AUXILIARES
    // ==============================================
    private void enviarErrorHTML(HttpServletResponse response, String mensaje) throws IOException {
        response.getWriter().write("<div class='alert alert-danger'><i class='fas fa-exclamation-circle'></i> " + mensaje + "</div>");
    }

    private String getCanalColor(String canal) {
        if (canal == null) {
            return "secondary";
        }
        switch (canal.toUpperCase()) {
            case "WHATSAPP":
                return "success";
            case "SMS":
                return "primary";
            case "EMAIL":
                return "warning";
            default:
                return "secondary";
        }
    }

    private String getEstadoColor(String estado) {
        if (estado == null) {
            return "secondary";
        }
        switch (estado.toUpperCase()) {
            case "ENVIADO":
                return "success";
            case "PENDIENTE":
                return "warning";
            case "FALLADO":
                return "danger";
            case "REINTENTO":
                return "info";
            default:
                return "secondary";
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }
}

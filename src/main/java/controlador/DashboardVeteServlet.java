package controlador;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import cx.DashboardVeteDAO;

@WebServlet("/DashboardVeteServlet")
public class DashboardVeteServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        String accion = request.getParameter("accion");

        if (accion == null) {
            out.print("ERROR|Acción no especificada");
            return;
        }

        HttpSession session = request.getSession();
        Integer idUsuario = (Integer) session.getAttribute("idUsuario");
        if (idUsuario == null) {
            out.print("ERROR|Sesión no válida");
            return;
        }

        DashboardVeteDAO dao = new DashboardVeteDAO();

        switch (accion) {
            case "obtenerEstadisticas": {
                int citasHoy = dao.obtenerCitasHoy(idUsuario);
                int consultasEnCurso = dao.obtenerConsultasEnCurso(idUsuario);
                int consultasFinalizadasHoy = dao.obtenerConsultasFinalizadasHoy(idUsuario);
                int pacientesAtendidos = dao.obtenerPacientesAtendidosMes(idUsuario);

                out.print(citasHoy + "|" + consultasEnCurso + "|" + consultasFinalizadasHoy + "|" + pacientesAtendidos);
                break;
            }

            case "obtenerAgendaHoy": {
                List<Map<String, Object>> citas = dao.obtenerAgendaHoy(idUsuario);
                SimpleDateFormat sdfHora = new SimpleDateFormat("HH:mm");

                if (citas.isEmpty()) {
                    out.print("<div class='alert alert-info text-center'>");
                    out.print("<i class='fas fa-calendar-times' style='font-size: 48px; margin-bottom: 15px;'></i>");
                    out.print("<p class='mb-0'>No tienes citas programadas para hoy</p>");
                    out.print("</div>");
                    return;
                }

                StringBuilder html = new StringBuilder();

                for (Map<String, Object> cita : citas) {
                    int idCita = (Integer) cita.get("idCita");
                    String hora = sdfHora.format(cita.get("fechaHora"));
                    String mascota = String.valueOf(cita.get("mascota"));
                    String raza = String.valueOf(cita.get("raza"));
                    String cliente = String.valueOf(cita.get("cliente"));
                    String motivo = String.valueOf(cita.get("motivo"));

                    boolean tieneConsulta = dao.tieneConsultaIniciada(idCita);
                    String estadoConsulta = tieneConsulta ? dao.obtenerEstadoConsulta(idCita) : null;

                    String estadoTexto = "";
                    String estadoClass = "";
                    String botonHtml = "";

                    if (estadoConsulta != null) {
                        if (estadoConsulta.equals("EN_CURSO")) {
                            estadoTexto = "En curso";
                            estadoClass = "status-inprogress";
                            botonHtml = "<button class='btn-primary-custom ms-3' onclick='window.location.href=\"gestionarConsultas.jsp\"'>Ver Consulta</button>";
                        } else if (estadoConsulta.equals("FINALIZADA")) {
                            estadoTexto = "Completada";
                            estadoClass = "status-completed";
                            botonHtml = "<button class='btn-outline-custom ms-3' onclick='window.location.href=\"gestionarConsultas.jsp\"'>Ver Resumen</button>";
                        } else {
                            estadoTexto = "En espera";
                            estadoClass = "status-waiting";
                            botonHtml = "<button class='btn-outline-custom ms-3' onclick='window.location.href=\"gestionarConsultas.jsp\"'>Iniciar Consulta</button>";
                        }
                    } else {
                        estadoTexto = "En espera";
                        estadoClass = "status-waiting";
                        botonHtml = "<button class='btn-outline-custom ms-3' onclick='window.location.href=\"gestionarConsultas.jsp\"'>Iniciar Consulta</button>";
                    }

                    html.append("<div class='patient-item'>");
                    html.append("<div class='patient-time'>").append(hora).append("</div>");
                    html.append("<div class='patient-info'>");
                    html.append("<h6>").append(escape(mascota));
                    if (raza != null && !raza.equals("null")) {
                        html.append(" (").append(escape(raza)).append(")");
                    }
                    html.append("</h6>");
                    html.append("<p>Dueño: ").append(escape(cliente));
                    if (motivo != null && !motivo.equals("null")) {
                        html.append(" • ").append(escape(motivo));
                    }
                    html.append("</p>");
                    html.append("</div>");
                    html.append("<span class='patient-status ").append(estadoClass).append("'>").append(estadoTexto).append("</span>");
                    html.append(botonHtml);
                    html.append("</div>");
                }

                out.print(html.toString());
                break;
            }

            case "obtenerDatosGrafico": {
                List<Map<String, Object>> datos = dao.obtenerConsultasPorDia(idUsuario);
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM");

                StringBuilder labels = new StringBuilder();
                StringBuilder values = new StringBuilder();

                for (int i = 0; i < datos.size(); i++) {
                    Map<String, Object> dato = datos.get(i);
                    labels.append(sdf.format(dato.get("fecha")));
                    values.append(dato.get("total"));

                    if (i < datos.size() - 1) {
                        labels.append(",");
                        values.append(",");
                    }
                }

                out.print(labels.toString() + "|" + values.toString());
                break;
            }

            case "obtenerTopRazas": {
                List<Map<String, Object>> razas = dao.obtenerTopRazas(idUsuario);

                if (razas.isEmpty()) {
                    out.print("<div class='alert alert-info text-center'>");
                    out.print("<p class='mb-0'>No hay datos suficientes este mes</p>");
                    out.print("</div>");
                    return;
                }

                StringBuilder html = new StringBuilder();
                html.append("<div class='list-group'>");

                int maxTotal = razas.isEmpty() ? 1 : (Integer) razas.get(0).get("total");

                for (Map<String, Object> raza : razas) {
                    String nombreRaza = String.valueOf(raza.get("raza"));
                    int total = (Integer) raza.get("total");
                    int porcentaje = (int) ((total * 100.0) / maxTotal);

                    html.append("<div class='list-group-item'>");
                    html.append("<div class='d-flex justify-content-between align-items-center mb-2'>");
                    html.append("<span class='fw-bold'>").append(escape(nombreRaza)).append("</span>");
                    html.append("<span class='badge bg-primary'>").append(total).append(" pacientes</span>");
                    html.append("</div>");
                    html.append("<div class='progress' style='height: 8px;'>");
                    html.append("<div class='progress-bar bg-success' style='width: ").append(porcentaje).append("%'></div>");
                    html.append("</div>");
                    html.append("</div>");
                }

                html.append("</div>");
                out.print(html.toString());
                break;
            }

            case "obtenerUltimasConsultas": {
                List<Map<String, Object>> consultas = dao.obtenerUltimasConsultas(idUsuario);
                SimpleDateFormat sdfHora = new SimpleDateFormat("dd/MM HH:mm");

                if (consultas.isEmpty()) {
                    out.print("<div class='alert alert-info text-center'>");
                    out.print("<p class='mb-0'>No hay consultas finalizadas recientemente</p>");
                    out.print("</div>");
                    return;
                }

                StringBuilder html = new StringBuilder();
                html.append("<div class='list-group'>");

                for (Map<String, Object> consulta : consultas) {
                    String mascota = String.valueOf(consulta.get("mascota"));
                    String cliente = String.valueOf(consulta.get("cliente"));
                    String motivo = String.valueOf(consulta.get("motivo"));
                    String fecha = sdfHora.format(consulta.get("fechaHora"));

                    html.append("<div class='list-group-item'>");
                    html.append("<div class='d-flex justify-content-between'>");
                    html.append("<div>");
                    html.append("<h6 class='mb-1'>").append(escape(mascota)).append("</h6>");
                    html.append("<small class='text-muted'>").append(escape(cliente)).append("</small>");
                    html.append("<p class='mb-0 mt-1'><small>").append(escape(motivo)).append("</small></p>");
                    html.append("</div>");
                    html.append("<small class='text-muted'>").append(fecha).append("</small>");
                    html.append("</div>");
                    html.append("</div>");
                }

                html.append("</div>");
                out.print(html.toString());
                break;
            }

            default:
                out.print("ERROR|Acción no soportada");
        }
    }

    private String escape(String s) {
        return s == null || s.equals("null") ? "" : s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;");
    }
}

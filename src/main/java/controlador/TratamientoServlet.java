// controlador/TratamientoServlet.java
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
import cx.TratamientoDAO;

@WebServlet("/TratamientoServlet")
public class TratamientoServlet extends HttpServlet {

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

        TratamientoDAO dao = new TratamientoDAO();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        switch (accion) {
            case "buscarMascotas": {
                String texto = request.getParameter("texto");
                if (texto == null || texto.trim().isEmpty()) {
                    out.print("<div class='alert alert-warning'>Ingresa un nombre de mascota o cliente</div>");
                    return;
                }

                List<Map<String, Object>> mascotas = dao.buscarMascotas(texto);

                if (mascotas.isEmpty()) {
                    out.print("<div class='alert alert-info'>No se encontraron mascotas con ese criterio</div>");
                    return;
                }

                StringBuilder tabla = new StringBuilder();
                tabla.append("<table class='table table-hover'>");
                tabla.append("<thead><tr><th>Mascota</th><th>Especie</th><th>Raza</th><th>Dueño</th><th>Acción</th></tr></thead>");
                tabla.append("<tbody>");

                for (Map<String, Object> m : mascotas) {
                    tabla.append("<tr>");
                    tabla.append("<td>").append(escape(String.valueOf(m.get("nombreMascota")))).append("</td>");
                    tabla.append("<td>").append(escape(String.valueOf(m.get("especie")))).append("</td>");
                    tabla.append("<td>").append(escape(String.valueOf(m.get("raza")))).append("</td>");
                    tabla.append("<td>").append(escape(String.valueOf(m.get("nombreCliente")))).append("</td>");
                    tabla.append("<td>");
                    tabla.append("<button class='btn btn-sm btn-primary' onclick='verTratamientos(")
                            .append(m.get("idMascota")).append(")'>Ver Tratamientos</button>");
                    tabla.append("</td>");
                    tabla.append("</tr>");
                }

                tabla.append("</tbody></table>");
                out.print(tabla.toString());
                break;
            }

            case "verTratamientos": {
                int idMascota = Integer.parseInt(request.getParameter("idMascota"));
                List<Map<String, Object>> tratamientos = dao.listarTratamientos(idMascota);

                if (tratamientos.isEmpty()) {
                    out.print("<div class='alert alert-info'>No hay tratamientos registrados para esta mascota</div>");
                    return;
                }

                StringBuilder tabla = new StringBuilder();
                tabla.append("<table class='table table-hover'>");
                tabla.append("<thead><tr><th>Fecha Inicio</th><th>Fecha Fin</th><th>Plan Terapéutico</th><th>Estado</th><th>Veterinario</th><th>Acciones</th></tr></thead>");
                tabla.append("<tbody>");

                for (Map<String, Object> t : tratamientos) {
                    String estadoClass = t.get("estado").equals("ACTIVO") ? "success" : "danger";

                    tabla.append("<tr>");
                    tabla.append("<td>").append(t.get("fechaInicio") != null ? sdf.format(t.get("fechaInicio")) : "-").append("</td>");
                    tabla.append("<td>").append(t.get("fechaFin") != null ? sdf.format(t.get("fechaFin")) : "-").append("</td>");

                    String plan = String.valueOf(t.get("planTerapeutico"));
                    String planCorto = plan.length() > 50 ? plan.substring(0, 50) + "..." : plan;
                    tabla.append("<td>").append(escape(planCorto)).append("</td>");

                    tabla.append("<td><span class='badge bg-").append(estadoClass).append("'>")
                            .append(t.get("estado")).append("</span></td>");
                    tabla.append("<td>").append(escape(String.valueOf(t.get("veterinario")))).append("</td>");
                    tabla.append("<td>");

                    if (t.get("estado").equals("ACTIVO")) {
                        tabla.append("<button class='btn btn-sm btn-info' onclick='abrirModalEditar(")
                                .append(t.get("idTratamiento")).append(")' title='Editar'><i class='fas fa-edit'></i></button> ");
                        tabla.append("<button class='btn btn-sm btn-danger' onclick='abrirModalAnular(")
                                .append(t.get("idTratamiento")).append(")' title='Anular'><i class='fas fa-ban'></i></button>");
                    } else {
                        tabla.append("<span class='text-muted'>Anulado</span>");
                    }

                    tabla.append("</td>");
                    tabla.append("</tr>");
                }

                tabla.append("</tbody></table>");
                out.print(tabla.toString());
                break;
            }

            case "cargarConsultas": {
                int idMascota = Integer.parseInt(request.getParameter("idMascota"));
                List<Map<String, Object>> consultas = dao.obtenerConsultasFinalizadas(idMascota);

                if (consultas.isEmpty()) {
                    out.print("<option value=''>No hay consultas finalizadas</option>");
                    return;
                }

                StringBuilder opciones = new StringBuilder();
                opciones.append("<option value=''>Seleccione una consulta</option>");

                SimpleDateFormat sdfHora = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                for (Map<String, Object> c : consultas) {
                    opciones.append("<option value='").append(c.get("idConsulta")).append("'>");
                    opciones.append(sdfHora.format(c.get("fechaHora")));
                    if (c.get("motivo") != null) {
                        opciones.append(" - ").append(escape(String.valueOf(c.get("motivo"))));
                    }
                    opciones.append("</option>");
                }

                out.print(opciones.toString());
                break;
            }

            case "obtenerDetalles": {
                int idTratamiento = Integer.parseInt(request.getParameter("idTratamiento"));
                Map<String, Object> detalles = dao.obtenerDetalles(idTratamiento);

                if (detalles.isEmpty()) {
                    out.print("ERROR|Tratamiento no encontrado");
                    return;
                }

                StringBuilder json = new StringBuilder();
                json.append(detalles.get("fechaInicio") != null ? sdf.format(detalles.get("fechaInicio")) : "").append("|");
                json.append(detalles.get("fechaFin") != null ? sdf.format(detalles.get("fechaFin")) : "").append("|");
                json.append(escape(String.valueOf(detalles.get("planTerapeutico")))).append("|");
                json.append(escape(String.valueOf(detalles.get("evolucion")))).append("|");
                json.append(detalles.get("fechaControl") != null ? sdf.format(detalles.get("fechaControl")) : "").append("|");
                json.append(escape(String.valueOf(detalles.get("observaciones"))));

                out.print(json.toString());
                break;
            }

            default:
                out.print("<div class='alert alert-danger'>Acción no soportada</div>");
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
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
        Integer idVeterinario = (Integer) session.getAttribute("idUsuario");

        if (idUsuario == null) {
            out.print("ERROR|Sesión no válida");
            return;
        }

        TratamientoDAO dao = new TratamientoDAO();

        switch (accion) {
            case "registrar": {
                try {
                    int idConsulta = Integer.parseInt(request.getParameter("idConsulta"));
                    int idMascota = Integer.parseInt(request.getParameter("idMascota"));
                    String fechaInicio = request.getParameter("fechaInicio");
                    String fechaFin = request.getParameter("fechaFin");
                    String planTerapeutico = request.getParameter("planTerapeutico");
                    String evolucion = request.getParameter("evolucion");
                    String fechaControl = request.getParameter("fechaControl");
                    String observaciones = request.getParameter("observaciones");

                    if (planTerapeutico == null || planTerapeutico.trim().isEmpty()) {
                        out.print("ERROR|El plan terapéutico es obligatorio");
                        return;
                    }

                    boolean exito = dao.registrarTratamiento(idConsulta, idMascota, idVeterinario, fechaInicio, fechaFin, planTerapeutico, evolucion, fechaControl, observaciones);

                    if (exito) {
                        out.print("OK|Tratamiento registrado correctamente");
                    } else {
                        out.print("ERROR|No se pudo registrar el tratamiento");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    out.print("ERROR|Error al registrar: " + e.getMessage());
                }
                break;
            }

            case "actualizar": {
                try {
                    int idTratamiento = Integer.parseInt(request.getParameter("idTratamiento"));
                    String fechaInicio = request.getParameter("fechaInicio");
                    String fechaFin = request.getParameter("fechaFin");
                    String planTerapeutico = request.getParameter("planTerapeutico");
                    String evolucion = request.getParameter("evolucion");
                    String fechaControl = request.getParameter("fechaControl");
                    String observaciones = request.getParameter("observaciones");

                    if (planTerapeutico == null || planTerapeutico.trim().isEmpty()) {
                        out.print("ERROR|El plan terapéutico es obligatorio");
                        return;
                    }

                    boolean exito = dao.actualizarTratamiento(idTratamiento, fechaInicio, fechaFin, planTerapeutico, evolucion, fechaControl, observaciones);

                    if (exito) {
                        out.print("OK|Tratamiento actualizado correctamente");
                    } else {
                        out.print("ERROR|No se pudo actualizar el tratamiento");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    out.print("ERROR|Error al actualizar: " + e.getMessage());
                }
                break;
            }

            case "anular": {
                try {
                    int idTratamiento = Integer.parseInt(request.getParameter("idTratamiento"));
                    String motivo = request.getParameter("motivo");

                    if (motivo == null || motivo.trim().isEmpty()) {
                        out.print("ERROR|Debe ingresar el motivo de anulación");
                        return;
                    }

                    boolean exito = dao.anularTratamiento(idTratamiento, motivo, idUsuario);

                    if (exito) {
                        out.print("OK|Tratamiento anulado correctamente");
                    } else {
                        out.print("ERROR|No se pudo anular el tratamiento");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    out.print("ERROR|Error al anular: " + e.getMessage());
                }
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

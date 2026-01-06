// controlador/ConstanciaServlet.java
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
import cx.ConstanciaDAO;

@WebServlet("/ConstanciaServlet")
public class ConstanciaServlet extends HttpServlet {

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

        ConstanciaDAO dao = new ConstanciaDAO();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");

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
                    tabla.append("<button class='btn btn-sm btn-primary' onclick='verConstancias(")
                            .append(m.get("idMascota")).append(")'>Ver Constancias</button>");
                    tabla.append("</td>");
                    tabla.append("</tr>");
                }

                tabla.append("</tbody></table>");
                out.print(tabla.toString());
                break;
            }

            case "verConstancias": {
                int idMascota = Integer.parseInt(request.getParameter("idMascota"));
                List<Map<String, Object>> constancias = dao.listarConstancias(idMascota);

                if (constancias.isEmpty()) {
                    out.print("<div class='alert alert-info'>No hay constancias emitidas para esta mascota</div>");
                    return;
                }

                StringBuilder tabla = new StringBuilder();
                tabla.append("<table class='table table-hover'>");
                tabla.append("<thead><tr><th>Fecha Emisión</th><th>Tipo</th><th>Motivo</th><th>Estado</th><th>Veterinario</th><th>Acciones</th></tr></thead>");
                tabla.append("<tbody>");

                for (Map<String, Object> c : constancias) {
                    String estadoClass = c.get("estado").equals("EMITIDA") ? "success" : "danger";

                    tabla.append("<tr>");
                    tabla.append("<td>").append(sdf.format(c.get("fechaEmision"))).append("</td>");
                    tabla.append("<td>").append(escape(String.valueOf(c.get("tipoConstancia")))).append("</td>");

                    String motivo = String.valueOf(c.get("motivo"));
                    String motivoCorto = motivo.length() > 50 ? motivo.substring(0, 50) + "..." : motivo;
                    tabla.append("<td>").append(escape(motivoCorto)).append("</td>");

                    tabla.append("<td><span class='badge bg-").append(estadoClass).append("'>")
                            .append(c.get("estado")).append("</span></td>");
                    tabla.append("<td>").append(escape(String.valueOf(c.get("veterinario")))).append("</td>");
                    tabla.append("<td>");

                    if (c.get("estado").equals("EMITIDA")) {
                        tabla.append("<button class='btn btn-sm btn-info me-1' onclick='verDetalleConstancia(")
                                .append(c.get("idConstancia")).append(")' title='Ver'><i class='fas fa-eye'></i></button> ");
                        tabla.append("<button class='btn btn-sm btn-warning me-1' onclick='abrirModalEditar(")
                                .append(c.get("idConstancia")).append(")' title='Editar'><i class='fas fa-edit'></i></button> ");
                        tabla.append("<button class='btn btn-sm btn-danger' onclick='abrirModalAnular(")
                                .append(c.get("idConstancia")).append(")' title='Anular'><i class='fas fa-ban'></i></button>");
                    } else {
                        tabla.append("<span class='text-muted'>Anulada</span>");
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

                StringBuilder opciones = new StringBuilder();
                opciones.append("<option value=''>Sin consulta asociada (opcional)</option>");

                for (Map<String, Object> c : consultas) {
                    opciones.append("<option value='").append(c.get("idConsulta")).append("'>");
                    opciones.append(sdf.format(c.get("fechaHora")));
                    if (c.get("motivo") != null) {
                        opciones.append(" - ").append(escape(String.valueOf(c.get("motivo"))));
                    }
                    opciones.append("</option>");
                }

                out.print(opciones.toString());
                break;
            }

            case "cargarTiposConstancia": {
                List<Map<String, Object>> tipos = dao.obtenerTiposConstancia();

                StringBuilder opciones = new StringBuilder();
                opciones.append("<option value=''>Seleccione un tipo</option>");

                for (Map<String, Object> t : tipos) {
                    opciones.append("<option value='").append(t.get("idTipo")).append("'>");
                    opciones.append(escape(String.valueOf(t.get("nombre"))));
                    opciones.append("</option>");
                }

                out.print(opciones.toString());
                break;
            }

            case "obtenerDetalles": {
                int idConstancia = Integer.parseInt(request.getParameter("idConstancia"));
                Map<String, Object> detalles = dao.obtenerDetalles(idConstancia);

                if (detalles.isEmpty()) {
                    out.print("ERROR|Constancia no encontrada");
                    return;
                }

                StringBuilder json = new StringBuilder();
                json.append(detalles.get("idTipoConstancia")).append("|");
                json.append(escape(String.valueOf(detalles.get("motivo")))).append("|");
                json.append(escape(String.valueOf(detalles.get("descripcion")))).append("|");
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

        ConstanciaDAO dao = new ConstanciaDAO();

        switch (accion) {
            case "registrar": {
                try {
                    String idConsultaStr = request.getParameter("idConsulta");
                    int idConsulta = (idConsultaStr == null || idConsultaStr.isEmpty()) ? 0 : Integer.parseInt(idConsultaStr);
                    int idMascota = Integer.parseInt(request.getParameter("idMascota"));
                    int idTipoConstancia = Integer.parseInt(request.getParameter("idTipoConstancia"));
                    String motivo = request.getParameter("motivo");
                    String descripcion = request.getParameter("descripcion");
                    String observaciones = request.getParameter("observaciones");

                    if (motivo == null || motivo.trim().isEmpty()) {
                        out.print("ERROR|El motivo es obligatorio");
                        return;
                    }

                    if (descripcion == null || descripcion.trim().isEmpty()) {
                        out.print("ERROR|La descripción es obligatoria");
                        return;
                    }

                    boolean exito = dao.registrarConstancia(idConsulta, idMascota, idVeterinario, idTipoConstancia, motivo, descripcion, observaciones);

                    if (exito) {
                        out.print("OK|Constancia emitida correctamente");
                    } else {
                        out.print("ERROR|No se pudo emitir la constancia");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    out.print("ERROR|Error al registrar: " + e.getMessage());
                }
                break;
            }

            case "actualizar": {
                try {
                    int idConstancia = Integer.parseInt(request.getParameter("idConstancia"));
                    int idTipoConstancia = Integer.parseInt(request.getParameter("idTipoConstancia"));
                    String motivo = request.getParameter("motivo");
                    String descripcion = request.getParameter("descripcion");
                    String observaciones = request.getParameter("observaciones");

                    if (motivo == null || motivo.trim().isEmpty()) {
                        out.print("ERROR|El motivo es obligatorio");
                        return;
                    }

                    if (descripcion == null || descripcion.trim().isEmpty()) {
                        out.print("ERROR|La descripción es obligatoria");
                        return;
                    }

                    boolean exito = dao.actualizarConstancia(idConstancia, idTipoConstancia, motivo, descripcion, observaciones);

                    if (exito) {
                        out.print("OK|Constancia actualizada correctamente");
                    } else {
                        out.print("ERROR|No se pudo actualizar la constancia");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    out.print("ERROR|Error al actualizar: " + e.getMessage());
                }
                break;
            }

            case "anular": {
                try {
                    int idConstancia = Integer.parseInt(request.getParameter("idConstancia"));
                    String motivo = request.getParameter("motivo");

                    if (motivo == null || motivo.trim().isEmpty()) {
                        out.print("ERROR|Debe ingresar el motivo de anulación");
                        return;
                    }

                    boolean exito = dao.anularConstancia(idConstancia, motivo, idUsuario);

                    if (exito) {
                        out.print("OK|Constancia anulada correctamente");
                    } else {
                        out.print("ERROR|No se pudo anular la constancia");
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

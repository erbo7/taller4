// controlador/DiagnosticoServlet.java
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
import cx.DiagnosticoDAO;

@WebServlet("/DiagnosticoServlet")
public class DiagnosticoServlet extends HttpServlet {

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
        Integer idVeterinario = (Integer) session.getAttribute("idVeterinario");

        if (idUsuario == null) {
            out.print("ERROR|Sesión no válida");
            return;
        }

        DiagnosticoDAO dao = new DiagnosticoDAO();
        SimpleDateFormat sdfHora = new SimpleDateFormat("dd/MM/yyyy HH:mm");

        switch (accion) {
            case "buscarMascotas": {
                String texto = request.getParameter("texto");
                if (texto == null || texto.trim().isEmpty()) {
                    out.print("<div class='alert alert-warning'>Ingresa un nombre de mascota o cliente</div>");
                    return;
                }

                List<Map<String, Object>> mascotas = dao.buscarMascotasConConsultas(texto);

                if (mascotas.isEmpty()) {
                    out.print("<div class='alert alert-info'>No se encontraron mascotas con consultas finalizadas</div>");
                    return;
                }

                StringBuilder tabla = new StringBuilder();
                tabla.append("<table class='table table-hover'>");
                tabla.append("<thead><tr><th>Mascota</th><th>Especie</th><th>Raza</th><th>Dueño</th><th>Consultas</th><th>Acción</th></tr></thead>");
                tabla.append("<tbody>");

                for (Map<String, Object> m : mascotas) {
                    tabla.append("<tr>");
                    tabla.append("<td>").append(escape(String.valueOf(m.get("nombreMascota")))).append("</td>");
                    tabla.append("<td>").append(escape(String.valueOf(m.get("especie")))).append("</td>");
                    tabla.append("<td>").append(escape(String.valueOf(m.get("raza")))).append("</td>");
                    tabla.append("<td>").append(escape(String.valueOf(m.get("nombreCliente")))).append("</td>");
                    tabla.append("<td><span class='badge bg-info'>").append(m.get("totalConsultas")).append("</span></td>");
                    tabla.append("<td>");
                    tabla.append("<button class='btn btn-sm btn-primary' onclick='verDiagnosticos(")
                            .append(m.get("idMascota")).append(")'>Ver Diagnósticos</button>");
                    tabla.append("</td>");
                    tabla.append("</tr>");
                }

                tabla.append("</tbody></table>");
                out.print(tabla.toString());
                break;
            }

            case "listarDiagnosticos": {
                // ✅ LOG 1: Ver qué llega
                String idMascotaParam = request.getParameter("idMascota");
                System.out.println("🔍 [listarDiagnosticos] Parámetro recibido: " + idMascotaParam);

                int idMascota = Integer.parseInt(idMascotaParam);
                System.out.println("✅ [listarDiagnosticos] ID Mascota parseado: " + idMascota);

                List<Map<String, Object>> diagnosticos = dao.listarDiagnosticos(idMascota);
                System.out.println("📊 [listarDiagnosticos] Diagnósticos encontrados: " + diagnosticos.size());

                StringBuilder html = new StringBuilder();
                html.append("<div class='mb-3'>");
                html.append("<button class='btn btn-success' onclick='abrirModalRegistrar()'>");
                html.append("<i class='fas fa-plus'></i> Registrar Diagnóstico");
                html.append("</button>");
                html.append("</div>");
                if (diagnosticos.isEmpty()) {
                    html.append("<div class='alert alert-info'>No hay diagnósticos registrados para esta mascota</div>");
                } else {
                    html.append("<div class='table-responsive'>");
                    html.append("<table class='table table-hover'>");
                    html.append("<thead><tr><th>Fecha</th><th>Diagnóstico</th><th>Tipo</th><th>Consulta</th><th>Veterinario</th><th>Estado</th><th>Acciones</th></tr></thead>");
                    html.append("<tbody>");
                    for (Map<String, Object> d : diagnosticos) {
                        String estadoClass = d.get("estado").equals("ACTIVO") ? "badge-success" : "badge-anulada";
                        String tipoClass = d.get("tipoDiagnostico").equals("DEFINITIVO") ? "text-success fw-bold" : "text-warning";
                        html.append("<tr>");
                        html.append("<td>").append(sdfHora.format(d.get("fechaRegistro"))).append("</td>");
                        html.append("<td>").append(escape(String.valueOf(d.get("diagnostico")))).append("</td>");
                        html.append("<td><span class='").append(tipoClass).append("'>").append(d.get("tipoDiagnostico")).append("</span></td>");
                        html.append("<td>").append(sdfHora.format(d.get("fechaConsulta"))).append("<br><small class='text-muted'>").append(escape(String.valueOf(d.get("motivoConsulta")))).append("</small></td>");
                        html.append("<td>").append(escape(String.valueOf(d.get("veterinario")))).append("</td>");
                        html.append("<td><span class='badge ").append(estadoClass).append("'>").append(d.get("estado")).append("</span></td>");
                        html.append("<td>");
                        if (d.get("estado").equals("ACTIVO")) {
                            html.append("<button class='btn btn-sm btn-warning me-1' onclick='abrirModalEditar(")
                                    .append(d.get("idDiagnostico")).append(")'><i class='fas fa-edit'></i></button>");
                            html.append("<button class='btn btn-sm btn-danger' onclick='abrirModalAnular(")
                                    .append(d.get("idDiagnostico")).append(")'><i class='fas fa-ban'></i></button>");
                        } else {
                            html.append("<span class='text-muted'>Anulado</span>");
                        }
                        html.append("</td>");
                        html.append("</tr>");
                    }
                    html.append("</tbody></table>");
                    html.append("</div>");
                }

                System.out.println("📄 [listarDiagnosticos] HTML generado, longitud: " + html.length());
                out.print(html.toString());
                break;
            }

            case "obtenerConsultasFinalizadas": {
                int idMascota = Integer.parseInt(request.getParameter("idMascota"));

                List<Map<String, Object>> consultas = dao.obtenerConsultasFinalizadas(idMascota);

                if (consultas.isEmpty()) {
                    out.print("<option value=''>No hay consultas finalizadas</option>");
                    return;
                }

                StringBuilder options = new StringBuilder();
                options.append("<option value=''>Seleccione una consulta</option>");

                for (Map<String, Object> c : consultas) {
                    options.append("<option value='").append(c.get("idConsulta")).append("'>");
                    options.append(sdfHora.format(c.get("fechaInicio")));
                    options.append(" - ").append(escape(String.valueOf(c.get("motivo"))));
                    options.append(" (").append(c.get("totalDiagnosticos")).append(" diagnósticos)");
                    options.append("</option>");
                }

                out.print(options.toString());
                break;
            }

            case "obtenerDetalles": {
                int idDiagnostico = Integer.parseInt(request.getParameter("idDiagnostico"));

                Map<String, Object> detalles = dao.obtenerDetallesDiagnostico(idDiagnostico);

                if (detalles.isEmpty()) {
                    out.print("ERROR|Diagnóstico no encontrado");
                    return;
                }

                StringBuilder json = new StringBuilder();
                json.append("{");
                json.append("\"diagnostico\":\"").append(escape(String.valueOf(detalles.get("diagnostico")))).append("\",");
                json.append("\"tipoDiagnostico\":\"").append(detalles.get("tipoDiagnostico")).append("\",");
                json.append("\"hallazgos\":\"").append(escape(String.valueOf(detalles.get("hallazgos") != null ? detalles.get("hallazgos") : ""))).append("\",");
                json.append("\"sintomas\":\"").append(escape(String.valueOf(detalles.get("sintomas") != null ? detalles.get("sintomas") : ""))).append("\",");
                json.append("\"observaciones\":\"").append(escape(String.valueOf(detalles.get("observaciones") != null ? detalles.get("observaciones") : ""))).append("\"");
                json.append("}");

                out.print(json.toString());
                break;
            }

            case "abrirModalDesdeConsulta": {
                String idMascotaParam = request.getParameter("idMascota");
                String idConsultaParam = request.getParameter("idConsulta");

                System.out.println("🔍 [abrirModalDesdeConsulta] Mascota: " + idMascotaParam + ", Consulta: " + idConsultaParam);

                if (idMascotaParam == null || idConsultaParam == null) {
                    out.print("<div class='alert alert-danger'>Error: Parámetros faltantes</div>");
                    return;
                }

                int idMascota = Integer.parseInt(idMascotaParam);
                int idConsulta = Integer.parseInt(idConsultaParam);

                // Generar el HTML del modal completo
                StringBuilder modal = new StringBuilder();
                modal.append("<div class='modal fade' id='modalDiagnosticoDirecto' tabindex='-1'>");
                modal.append("<div class='modal-dialog modal-lg'>");
                modal.append("<div class='modal-content'>");
                modal.append("<div class='modal-header' style='background: linear-gradient(135deg, #10b981 0%, #059669 100%); color: white;'>");
                modal.append("<h5 class='modal-title'><i class='fas fa-plus'></i> Registrar Diagnóstico</h5>");
                modal.append("<button type='button' class='btn-close btn-close-white' data-bs-dismiss='modal'></button>");
                modal.append("</div>");
                modal.append("<div class='modal-body'>");
                modal.append("<div id='mensajeModal'></div>");

                // Consulta pre-seleccionada (hidden porque ya viene de la consulta actual)
                modal.append("<input type='hidden' id='idConsultaModal' value='").append(idConsulta).append("'>");
                modal.append("<input type='hidden' id='idMascotaModal' value='").append(idMascota).append("'>");

                modal.append("<div class='alert alert-info'>");
                modal.append("<i class='fas fa-info-circle'></i> Diagnóstico para la <strong>consulta actual en curso</strong>");
                modal.append("</div>");

                modal.append("<div class='mb-3'>");
                modal.append("<label class='form-label fw-bold'>Diagnóstico: <span class='text-danger'>*</span></label>");
                modal.append("<textarea class='form-control' id='txtDiagnosticoModal' rows='3' ");
                modal.append("placeholder='Ej: Gastroenteritis aguda, Dermatitis alérgica, etc.' required></textarea>");
                modal.append("</div>");

                modal.append("<div class='mb-3'>");
                modal.append("<label class='form-label fw-bold'>Tipo de Diagnóstico: <span class='text-danger'>*</span></label>");
                modal.append("<select class='form-select' id='selectTipoModal' required>");
                modal.append("<option value='TENTATIVO'>Tentativo</option>");
                modal.append("<option value='DEFINITIVO'>Definitivo</option>");
                modal.append("</select>");
                modal.append("<small class='text-muted'>Tentativo: requiere confirmación. Definitivo: diagnóstico confirmado</small>");
                modal.append("</div>");

                modal.append("<div class='mb-3'>");
                modal.append("<label class='form-label fw-bold'>Hallazgos:</label>");
                modal.append("<textarea class='form-control' id='txtHallazgosModal' rows='3' ");
                modal.append("placeholder='Hallazgos clínicos encontrados durante la consulta...'></textarea>");
                modal.append("</div>");

                modal.append("<div class='mb-3'>");
                modal.append("<label class='form-label fw-bold'>Síntomas:</label>");
                modal.append("<textarea class='form-control' id='txtSintomasModal' rows='3' ");
                modal.append("placeholder='Síntomas presentados por la mascota...'></textarea>");
                modal.append("</div>");

                modal.append("<div class='mb-3'>");
                modal.append("<label class='form-label fw-bold'>Observaciones:</label>");
                modal.append("<textarea class='form-control' id='txtObservacionesModal' rows='3' ");
                modal.append("placeholder='Observaciones adicionales...'></textarea>");
                modal.append("</div>");

                modal.append("</div>");
                modal.append("<div class='modal-footer'>");
                modal.append("<button type='button' class='btn btn-secondary' data-bs-dismiss='modal'>Cancelar</button>");
                modal.append("<button type='button' class='btn btn-primary' style='background: linear-gradient(135deg, #10b981 0%, #059669 100%); border: none;' ");
                modal.append("onclick='guardarDiagnosticoDirecto()'>");
                modal.append("<i class='fas fa-save'></i> Guardar Diagnóstico</button>");
                modal.append("</div>");
                modal.append("</div>");
                modal.append("</div>");
                modal.append("</div>");

                out.print(modal.toString());
                break;
            }

            case "listarDiagnosticosParaTab": {
                String idConsultaParam = request.getParameter("idConsulta");

                System.out.println("🔍 [listarDiagnosticosParaTab] Consulta: " + idConsultaParam);

                if (idConsultaParam == null) {
                    out.print("<div class='alert alert-danger'>Error: ID consulta faltante</div>");
                    return;
                }

                int idConsulta = Integer.parseInt(idConsultaParam);

                List<Map<String, Object>> diagnosticos = dao.listarDiagnosticosPorConsulta(idConsulta);
                System.out.println("📊 [listarDiagnosticosParaTab] Encontrados: " + diagnosticos.size());

                StringBuilder html = new StringBuilder();

                if (diagnosticos.isEmpty()) {
                    html.append("<div class='alert alert-info'>");
                    html.append("<i class='fas fa-info-circle'></i> Aún no hay diagnósticos para esta consulta");
                    html.append("</div>");
                } else {
                    html.append("<div class='table-responsive'>");
                    html.append("<table class='table table-sm table-hover table-bordered'>");
                    html.append("<thead class='table-light'><tr><th>Diagnóstico</th><th>Tipo</th></tr></thead>");
                    html.append("<tbody>");

                    for (Map<String, Object> d : diagnosticos) {
                        String tipoBadge = d.get("tipoDiagnostico").equals("DEFINITIVO") ? "bg-success" : "bg-warning";
                        html.append("<tr>");
                        html.append("<td>").append(escape(String.valueOf(d.get("diagnostico")))).append("</td>");
                        html.append("<td><span class='badge ").append(tipoBadge).append("'>")
                                .append(d.get("tipoDiagnostico")).append("</span></td>");
                        html.append("</tr>");
                    }

                    html.append("</tbody></table></div>");
                }

                out.print(html.toString());
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
        Integer idVeterinario = (Integer) session.getAttribute("idVeterinario");

        // ✅ CAMBIO: Solo validar idUsuario
        if (idUsuario == null) {
            out.print("ERROR|Sesión no válida");
            return;
        }

        // ✅ Si no hay idVeterinario en sesión, obtenerlo de la BD
        if (idVeterinario == null) {
            idVeterinario = obtenerIdVeterinarioPorUsuario(idUsuario);
            if (idVeterinario == null) {
                out.print("ERROR|No se pudo identificar al veterinario");
                return;
            }
        }

        DiagnosticoDAO dao = new DiagnosticoDAO();

        switch (accion) {
            case "registrarDiagnostico": {
                try {
                    int idConsulta = Integer.parseInt(request.getParameter("idConsulta"));
                    int idMascota = Integer.parseInt(request.getParameter("idMascota"));
                    String diagnostico = request.getParameter("diagnostico");
                    String tipoDiagnostico = request.getParameter("tipoDiagnostico");
                    String hallazgos = request.getParameter("hallazgos");
                    String sintomas = request.getParameter("sintomas");
                    String observaciones = request.getParameter("observaciones");

                    if (diagnostico == null || diagnostico.trim().isEmpty()) {
                        out.print("ERROR|El diagnóstico no puede estar vacío");
                        return;
                    }

                    boolean exito = dao.registrarDiagnostico(idConsulta, idMascota, idVeterinario,
                            diagnostico, tipoDiagnostico,
                            hallazgos, sintomas, observaciones);

                    if (exito) {
                        out.print("OK|Diagnóstico registrado correctamente");
                    } else {
                        out.print("ERROR|No se pudo registrar el diagnóstico");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    out.print("ERROR|Error al registrar: " + e.getMessage());
                }
                break;
            }

            case "editarDiagnostico": {
                try {
                    int idDiagnostico = Integer.parseInt(request.getParameter("idDiagnostico"));
                    String diagnostico = request.getParameter("diagnostico");
                    String tipoDiagnostico = request.getParameter("tipoDiagnostico");
                    String hallazgos = request.getParameter("hallazgos");
                    String sintomas = request.getParameter("sintomas");
                    String observaciones = request.getParameter("observaciones");

                    if (diagnostico == null || diagnostico.trim().isEmpty()) {
                        out.print("ERROR|El diagnóstico no puede estar vacío");
                        return;
                    }

                    boolean exito = dao.editarDiagnostico(idDiagnostico, diagnostico, tipoDiagnostico,
                            hallazgos, sintomas, observaciones);

                    if (exito) {
                        out.print("OK|Diagnóstico actualizado correctamente");
                    } else {
                        out.print("ERROR|No se pudo actualizar el diagnóstico");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    out.print("ERROR|Error al actualizar: " + e.getMessage());
                }
                break;
            }

            case "anularDiagnostico": {
                try {
                    int idDiagnostico = Integer.parseInt(request.getParameter("idDiagnostico"));
                    String motivo = request.getParameter("motivo");

                    if (motivo == null || motivo.trim().isEmpty()) {
                        out.print("ERROR|Debe ingresar el motivo de anulación");
                        return;
                    }

                    boolean exito = dao.anularDiagnostico(idDiagnostico, motivo, idUsuario);

                    if (exito) {
                        out.print("OK|Diagnóstico anulado correctamente");
                    } else {
                        out.print("ERROR|No se pudo anular el diagnóstico");
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

// ✅ AGREGAR ESTE MÉTODO AL FINAL DE LA CLASE (antes del último })
    private Integer obtenerIdVeterinarioPorUsuario(int idUsuario) {
        try {
            cx.conexion conn = new cx.conexion();
            java.sql.Connection conexion = conn.conectar();
            String sql = "SELECT ID_VETERINARIO FROM veterinarios WHERE ID_USUARIO = ?";
            java.sql.PreparedStatement ps = conexion.prepareStatement(sql);
            ps.setInt(1, idUsuario);
            java.sql.ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                int idVet = rs.getInt("ID_VETERINARIO");
                rs.close();
                ps.close();
                conexion.close();
                return idVet;
            }

            rs.close();
            ps.close();
            conexion.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private String escape(String s) {
        return s == null || s.equals("null") ? "" : s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;");
    }
}

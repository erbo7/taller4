// controlador/FichaMedicaServlet.java
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
import cx.FichaMedicaDAO;

@WebServlet("/FichaMedicaServlet")
public class FichaMedicaServlet extends HttpServlet {

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

        FichaMedicaDAO dao = new FichaMedicaDAO();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat sdfHora = new SimpleDateFormat("dd/MM/yyyy HH:mm");

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
                tabla.append("<thead><tr><th>Mascota</th><th>Especie</th><th>Raza</th><th>Dueño</th><th>Ficha</th><th>Acción</th></tr></thead>");
                tabla.append("<tbody>");

                for (Map<String, Object> m : mascotas) {
                    tabla.append("<tr>");
                    tabla.append("<td>").append(escape(String.valueOf(m.get("nombreMascota")))).append("</td>");
                    tabla.append("<td>").append(escape(String.valueOf(m.get("especie")))).append("</td>");
                    tabla.append("<td>").append(escape(String.valueOf(m.get("raza")))).append("</td>");
                    tabla.append("<td>").append(escape(String.valueOf(m.get("nombreCliente")))).append("</td>");

                    Object idFicha = m.get("idFicha");
                    if (idFicha != null) {
                        tabla.append("<td><span class='badge bg-success'>Activa</span></td>");
                    } else {
                        tabla.append("<td><span class='badge bg-secondary'>Sin ficha</span></td>");
                    }

                    tabla.append("<td>");
                    tabla.append("<button class='btn btn-sm btn-primary' onclick='verFicha(")
                            .append(m.get("idMascota")).append(")'>Ver Ficha</button>");
                    tabla.append("</td>");
                    tabla.append("</tr>");
                }

                tabla.append("</tbody></table>");
                out.print(tabla.toString());
                break;
            }

            case "verFicha": {
                int idMascota = Integer.parseInt(request.getParameter("idMascota"));

                // Obtener datos básicos
                Map<String, Object> datosMascota = dao.obtenerDatosMascota(idMascota);
                if (datosMascota == null || datosMascota.isEmpty()) {
                    out.print("<div class='alert alert-danger'>Mascota no encontrada</div>");
                    return;
                }

                // Verificar si tiene ficha
                Object idFichaObj = datosMascota.get("idFicha");
                if (idFichaObj == null || (idFichaObj instanceof Integer && (Integer) idFichaObj == 0)) {
                    out.print("<div class='alert alert-warning'>Esta mascota no tiene una ficha médica activa</div>");
                    return;
                }

                int idFicha = (Integer) idFichaObj;

                // Obtener detalles de la ficha
                Map<String, Object> detallesFicha = dao.obtenerDetallesFicha(idFicha);

                StringBuilder html = new StringBuilder();

                // Agregar el ID de ficha como atributo data para JavaScript
                html.append("<input type='hidden' id='hiddenIdFicha' value='").append(idFicha).append("'>");
                html.append("<input type='hidden' id='hiddenIdMascota' value='").append(idMascota).append("'>");

                // === DATOS BÁSICOS ===
                html.append("<div class='ficha-section'>");
                html.append("<h5 class='section-title'><i class='fas fa-paw'></i> Datos Básicos</h5>");
                html.append("<div class='row'>");
                html.append("<div class='col-md-6'>");
                html.append("<p><strong>Nombre:</strong> ").append(escape(String.valueOf(datosMascota.get("nombre")))).append("</p>");
                html.append("<p><strong>Especie:</strong> ").append(escape(String.valueOf(datosMascota.get("especie")))).append("</p>");
                html.append("<p><strong>Raza:</strong> ").append(escape(String.valueOf(datosMascota.get("raza")))).append("</p>");
                html.append("<p><strong>Sexo:</strong> ").append(escape(String.valueOf(datosMascota.get("sexo")))).append("</p>");

                Object edadObj = datosMascota.get("edad");
                if (edadObj != null) {
                    html.append("<p><strong>Edad:</strong> ").append(edadObj).append(" años</p>");
                }
                html.append("</div>");

                html.append("<div class='col-md-6'>");
                html.append("<p><strong>Propietario:</strong> ").append(escape(String.valueOf(datosMascota.get("dueno")))).append("</p>");
                html.append("<p><strong>Teléfono:</strong> ").append(escape(String.valueOf(datosMascota.get("telefono")))).append("</p>");

                Object emailObj = datosMascota.get("email");
                if (emailObj != null && !String.valueOf(emailObj).equals("null")) {
                    html.append("<p><strong>Email:</strong> ").append(escape(String.valueOf(emailObj))).append("</p>");
                }

                Object direccionObj = datosMascota.get("direccion");
                if (direccionObj != null && !String.valueOf(direccionObj).equals("null")) {
                    html.append("<p><strong>Dirección:</strong> ").append(escape(String.valueOf(direccionObj))).append("</p>");
                }

                Object fechaFichaObj = datosMascota.get("fechaCreacionFicha");
                if (fechaFichaObj != null && !String.valueOf(fechaFichaObj).equals("null")) {
                    html.append("<p><strong>Ficha creada:</strong> ").append(fechaFichaObj).append("</p>");
                }

                // Mostrar versión y fecha de modificación si existe
                if (detallesFicha != null && !detallesFicha.isEmpty()) {
                    Object versionObj = detallesFicha.get("version");
                    if (versionObj != null && (Integer) versionObj > 1) {
                        html.append("<p><strong>Versión:</strong> ").append(versionObj).append("</p>");
                    }

                    Object fechaModObj = detallesFicha.get("fechaModificacion");
                    if (fechaModObj != null) {
                        html.append("<p><strong>Última modificación:</strong> ").append(sdfHora.format(fechaModObj)).append("</p>");
                    }
                }

                html.append("</div>");
                html.append("</div>");

                // Mostrar observaciones generales si existen
                if (detallesFicha != null && detallesFicha.get("observaciones") != null) {
                    String obs = String.valueOf(detallesFicha.get("observaciones"));
                    if (!obs.equals("null") && !obs.trim().isEmpty()) {
                        html.append("<div style='margin-top: 15px; padding: 15px; background: #fff; border-left: 4px solid #3b82f6; border-radius: 5px;'>");
                        html.append("<p style='margin: 0;'><strong><i class='fas fa-clipboard'></i> Observaciones Generales:</strong></p>");
                        html.append("<p style='margin: 10px 0 0 0; color: #4b5563;'>").append(escape(obs)).append("</p>");
                        html.append("</div>");
                    }
                }

                html.append("</div>");

                // === HISTORIAL DE CONSULTAS ===
                List<Map<String, Object>> consultas = dao.obtenerHistorialConsultas(idMascota);
                html.append("<div class='ficha-section'>");
                html.append("<h5 class='section-title'><i class='fas fa-stethoscope'></i> Historial de Consultas (")
                        .append(consultas.size()).append(")</h5>");

                if (consultas.isEmpty()) {
                    html.append("<p class='text-muted'>No hay consultas registradas</p>");
                } else {
                    html.append("<div class='table-responsive'>");
                    html.append("<table class='table table-sm table-hover'>");
                    html.append("<thead><tr><th>Fecha</th><th>Motivo</th><th>Peso</th><th>Temperatura</th><th>Estado</th><th>Veterinario</th></tr></thead>");
                    html.append("<tbody>");
                    for (Map<String, Object> c : consultas) {
                        String estadoClass = c.get("estado").equals("FINALIZADA") ? "badge-finalizada"
                                : c.get("estado").equals("EN_CURSO") ? "badge-en-curso"
                                : "badge-anulada";
                        html.append("<tr>");
                        html.append("<td>").append(sdfHora.format(c.get("fechaInicio"))).append("</td>");

                        Object motivoObj = c.get("motivo");
                        html.append("<td>").append(motivoObj != null ? escape(String.valueOf(motivoObj)) : "-").append("</td>");

                        Object pesoObj = c.get("peso");
                        html.append("<td>").append(pesoObj != null ? pesoObj + " kg" : "-").append("</td>");

                        Object tempObj = c.get("temperatura");
                        html.append("<td>").append(tempObj != null ? tempObj + " °C" : "-").append("</td>");

                        html.append("<td><span class='badge-estado ").append(estadoClass).append("'>")
                                .append(c.get("estado")).append("</span></td>");
                        html.append("<td>").append(escape(String.valueOf(c.get("veterinario")))).append("</td>");
                        html.append("</tr>");
                    }
                    html.append("</tbody></table>");
                    html.append("</div>");
                }
                html.append("</div>");

                // === DIAGNÓSTICOS ===
                List<Map<String, Object>> diagnosticos = dao.obtenerDiagnosticos(idMascota);
                html.append("<div class='ficha-section'>");
                html.append("<h5 class='section-title'><i class='fas fa-diagnoses'></i> Diagnósticos (")
                        .append(diagnosticos.size()).append(")</h5>");

                if (diagnosticos.isEmpty()) {
                    html.append("<p class='text-muted'>No hay diagnósticos registrados</p>");
                } else {
                    html.append("<div class='table-responsive'>");
                    html.append("<table class='table table-sm table-hover'>");
                    html.append("<thead><tr><th>Fecha</th><th>Diagnóstico</th><th>Tipo</th><th>Veterinario</th></tr></thead>");
                    html.append("<tbody>");
                    for (Map<String, Object> d : diagnosticos) {
                        html.append("<tr>");
                        html.append("<td>").append(sdfHora.format(d.get("fechaRegistro"))).append("</td>");
                        html.append("<td>").append(escape(String.valueOf(d.get("diagnostico")))).append("</td>");
                        html.append("<td>").append(escape(String.valueOf(d.get("tipoDiagnostico")))).append("</td>");
                        html.append("<td>").append(escape(String.valueOf(d.get("veterinario")))).append("</td>");
                        html.append("</tr>");
                    }
                    html.append("</tbody></table>");
                    html.append("</div>");
                }
                html.append("</div>");

                // === PROCEDIMIENTOS ===
                List<Map<String, Object>> procedimientos = dao.obtenerProcedimientos(idMascota);
                html.append("<div class='ficha-section'>");
                html.append("<h5 class='section-title'><i class='fas fa-syringe'></i> Procedimientos (")
                        .append(procedimientos.size()).append(")</h5>");

                if (procedimientos.isEmpty()) {
                    html.append("<p class='text-muted'>No hay procedimientos registrados</p>");
                } else {
                    html.append("<div class='table-responsive'>");
                    html.append("<table class='table table-sm table-hover'>");
                    html.append("<thead><tr><th>Fecha</th><th>Tipo</th><th>Descripción</th><th>Veterinario</th></tr></thead>");
                    html.append("<tbody>");
                    for (Map<String, Object> p : procedimientos) {
                        html.append("<tr>");
                        html.append("<td>").append(sdfHora.format(p.get("fechaProcedimiento"))).append("</td>");
                        html.append("<td>").append(escape(String.valueOf(p.get("tipoProcedimiento")))).append("</td>");
                        html.append("<td>").append(escape(String.valueOf(p.get("descripcion")))).append("</td>");
                        html.append("<td>").append(escape(String.valueOf(p.get("veterinario")))).append("</td>");
                        html.append("</tr>");
                    }
                    html.append("</tbody></table>");
                    html.append("</div>");
                }
                html.append("</div>");

                // === RECETAS ===
                List<Map<String, Object>> recetas = dao.obtenerRecetas(idMascota);
                html.append("<div class='ficha-section'>");
                html.append("<h5 class='section-title'><i class='fas fa-prescription'></i> Recetas (")
                        .append(recetas.size()).append(")</h5>");

                if (recetas.isEmpty()) {
                    html.append("<p class='text-muted'>No hay recetas registradas</p>");
                } else {
                    html.append("<div class='table-responsive'>");
                    html.append("<table class='table table-sm table-hover'>");
                    html.append("<thead><tr><th>Fecha</th><th>Medicamento</th><th>Dosis</th><th>Frecuencia</th><th>Veterinario</th></tr></thead>");
                    html.append("<tbody>");
                    for (Map<String, Object> r : recetas) {
                        html.append("<tr>");
                        html.append("<td>").append(sdfHora.format(r.get("fechaEmision"))).append("</td>");
                        html.append("<td>").append(escape(String.valueOf(r.get("medicamento")))).append("</td>");
                        html.append("<td>").append(escape(String.valueOf(r.get("dosis")))).append("</td>");
                        html.append("<td>").append(escape(String.valueOf(r.get("frecuencia")))).append("</td>");
                        html.append("<td>").append(escape(String.valueOf(r.get("veterinario")))).append("</td>");
                        html.append("</tr>");
                    }
                    html.append("</tbody></table>");
                    html.append("</div>");
                }
                html.append("</div>");

                // === TRATAMIENTOS ===
                List<Map<String, Object>> tratamientos = dao.obtenerTratamientos(idMascota);
                html.append("<div class='ficha-section'>");
                html.append("<h5 class='section-title'><i class='fas fa-notes-medical'></i> Tratamientos (")
                        .append(tratamientos.size()).append(")</h5>");

                if (tratamientos.isEmpty()) {
                    html.append("<p class='text-muted'>No hay tratamientos registrados</p>");
                } else {
                    html.append("<div class='table-responsive'>");
                    html.append("<table class='table table-sm table-hover'>");
                    html.append("<thead><tr><th>Fecha Inicio</th><th>Plan Terapéutico</th><th>Estado</th><th>Veterinario</th></tr></thead>");
                    html.append("<tbody>");
                    for (Map<String, Object> t : tratamientos) {
                        html.append("<tr>");
                        html.append("<td>").append(sdf.format(t.get("fechaInicio"))).append("</td>");
                        html.append("<td>").append(escape(String.valueOf(t.get("planTerapeutico")))).append("</td>");
                        html.append("<td>").append(escape(String.valueOf(t.get("estado")))).append("</td>");
                        html.append("<td>").append(escape(String.valueOf(t.get("veterinario")))).append("</td>");
                        html.append("</tr>");
                    }
                    html.append("</tbody></table>");
                    html.append("</div>");
                }
                html.append("</div>");

                // === ÓRDENES DE ESTUDIOS ===
                List<Map<String, Object>> estudios = dao.obtenerOrdenesEstudios(idMascota);
                html.append("<div class='ficha-section'>");
                html.append("<h5 class='section-title'><i class='fas fa-x-ray'></i> Órdenes de Estudios (")
                        .append(estudios.size()).append(")</h5>");

                if (estudios.isEmpty()) {
                    html.append("<p class='text-muted'>No hay órdenes de estudios registradas</p>");
                } else {
                    html.append("<div class='table-responsive'>");
                    html.append("<table class='table table-sm table-hover'>");
                    html.append("<thead><tr><th>Fecha</th><th>Tipo</th><th>Motivo</th><th>Veterinario</th></tr></thead>");
                    html.append("<tbody>");
                    for (Map<String, Object> e : estudios) {
                        html.append("<tr>");
                        html.append("<td>").append(sdfHora.format(e.get("fechaEmision"))).append("</td>");
                        html.append("<td>").append(escape(String.valueOf(e.get("tipoEstudio")))).append("</td>");
                        html.append("<td>").append(escape(String.valueOf(e.get("motivo")))).append("</td>");
                        html.append("<td>").append(escape(String.valueOf(e.get("veterinario")))).append("</td>");
                        html.append("</tr>");
                    }
                    html.append("</tbody></table>");
                    html.append("</div>");
                }
                html.append("</div>");

                // === ÓRDENES DE ANÁLISIS ===
                List<Map<String, Object>> analisis = dao.obtenerOrdenesAnalisis(idMascota);
                html.append("<div class='ficha-section'>");
                html.append("<h5 class='section-title'><i class='fas fa-flask'></i> Órdenes de Análisis (")
                        .append(analisis.size()).append(")</h5>");

                if (analisis.isEmpty()) {
                    html.append("<p class='text-muted'>No hay órdenes de análisis registradas</p>");
                } else {
                    html.append("<div class='table-responsive'>");
                    html.append("<table class='table table-sm table-hover'>");
                    html.append("<thead><tr><th>Fecha</th><th>Tipo</th><th>Motivo</th><th>Veterinario</th></tr></thead>");
                    html.append("<tbody>");
                    for (Map<String, Object> a : analisis) {
                        html.append("<tr>");
                        html.append("<td>").append(sdfHora.format(a.get("fechaEmision"))).append("</td>");
                        html.append("<td>").append(escape(String.valueOf(a.get("tipoAnalisis")))).append("</td>");
                        html.append("<td>").append(escape(String.valueOf(a.get("motivo")))).append("</td>");
                        html.append("<td>").append(escape(String.valueOf(a.get("veterinario")))).append("</td>");
                        html.append("</tr>");
                    }
                    html.append("</tbody></table>");
                    html.append("</div>");
                }
                html.append("</div>");

                // === CONSTANCIAS ===
                List<Map<String, Object>> constancias = dao.obtenerConstancias(idMascota);
                html.append("<div class='ficha-section'>");
                html.append("<h5 class='section-title'><i class='fas fa-file-medical'></i> Constancias (")
                        .append(constancias.size()).append(")</h5>");

                if (constancias.isEmpty()) {
                    html.append("<p class='text-muted'>No hay constancias emitidas</p>");
                } else {
                    html.append("<div class='table-responsive'>");
                    html.append("<table class='table table-sm table-hover'>");
                    html.append("<thead><tr><th>Fecha</th><th>Tipo</th><th>Descripción</th><th>Veterinario</th></tr></thead>");
                    html.append("<tbody>");
                    for (Map<String, Object> co : constancias) {
                        html.append("<tr>");
                        html.append("<td>").append(sdfHora.format(co.get("fechaEmision"))).append("</td>");
                        html.append("<td>").append(escape(String.valueOf(co.get("tipoConstancia")))).append("</td>");
                        html.append("<td>").append(escape(String.valueOf(co.get("descripcion")))).append("</td>");
                        html.append("<td>").append(escape(String.valueOf(co.get("veterinario")))).append("</td>");
                        html.append("</tr>");
                    }
                    html.append("</tbody></table>");
                    html.append("</div>");
                }
                html.append("</div>");

                out.print(html.toString());
                break;
            }

            case "obtenerDetallesFicha": {
                int idFicha = Integer.parseInt(request.getParameter("idFicha"));
                Map<String, Object> detalles = dao.obtenerDetallesFicha(idFicha);

                if (detalles != null && !detalles.isEmpty()) {
                    String observaciones = detalles.get("observaciones") != null
                            ? String.valueOf(detalles.get("observaciones")) : "";
                    out.print(observaciones);
                } else {
                    out.print("");
                }
                break;
            }

            case "verificarAnulacion": {
                // Siempre permitir anulación (sin restricción de tiempo)
                out.print("OK");
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

        // LOGS DE DEPURACIÓN
        System.out.println("✅ doPost llamado");
        System.out.println("📝 Content-Type: " + request.getContentType());
        System.out.println("📝 Method: " + request.getMethod());

        String accion = request.getParameter("accion");
        System.out.println("📝 Acción recibida: " + accion);

        // Mostrar todos los parámetros
        System.out.println("📝 Todos los parámetros:");
        request.getParameterMap().forEach((key, value)
                -> System.out.println("  - " + key + " = " + String.join(", ", value))
        );

        if (accion == null) {
            System.out.println("❌ Acción es NULL");
            out.print("ERROR|Acción no especificada");
            return;
        }

        HttpSession session = request.getSession();
        Integer idUsuario = (Integer) session.getAttribute("idUsuario");
        if (idUsuario == null) {
            System.out.println("❌ Sesión no válida");
            out.print("ERROR|Sesión no válida");
            return;
        }

        System.out.println("✅ Usuario ID: " + idUsuario);
        FichaMedicaDAO dao = new FichaMedicaDAO();

        switch (accion) {
            case "editarFicha": {
                System.out.println("🔹 Entrando a editarFicha");
                try {
                    int idFicha = Integer.parseInt(request.getParameter("idFicha"));
                    String observaciones = request.getParameter("observaciones");

                    System.out.println("📝 ID Ficha: " + idFicha);
                    System.out.println("📝 Observaciones: " + observaciones);

                    if (observaciones == null || observaciones.trim().isEmpty()) {
                        out.print("ERROR|Las observaciones no pueden estar vacías");
                        return;
                    }

                    boolean exito = dao.editarFicha(idFicha, observaciones, idUsuario);
                    System.out.println("✅ Resultado edición: " + exito);

                    if (exito) {
                        out.print("OK|Ficha médica actualizada correctamente");
                    } else {
                        out.print("ERROR|No se pudo actualizar la ficha. Verifique que esté activa.");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    out.print("ERROR|Error al actualizar la ficha: " + e.getMessage());
                }
                break;
            }

            case "anularFicha": {
                System.out.println("🔹 Entrando a anularFicha");
                try {
                    int idFicha = Integer.parseInt(request.getParameter("idFicha"));
                    String motivo = request.getParameter("motivo");

                    System.out.println("📝 ID Ficha: " + idFicha);
                    System.out.println("📝 Motivo: " + motivo);

                    if (motivo == null || motivo.trim().isEmpty()) {
                        out.print("ERROR|Debe ingresar el motivo de anulación");
                        return;
                    }

                    boolean exito = dao.anularFicha(idFicha, motivo, idUsuario);
                    System.out.println("✅ Resultado anulación: " + exito);

                    if (exito) {
                        out.print("OK|Ficha médica anulada correctamente");
                    } else {
                        out.print("ERROR|No se pudo anular la ficha. Verifique que esté activa.");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    out.print("ERROR|Error al anular la ficha: " + e.getMessage());
                }
                break;
            }

            default:
                System.out.println("❌ Acción no reconocida: " + accion);
                out.print("ERROR|Acción no soportada");
        }
    }

    private String escape(String s) {
        return s == null || s.equals("null") ? "" : s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;");
    }
}

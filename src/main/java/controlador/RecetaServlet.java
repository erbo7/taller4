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
import cx.RecetaDAO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@WebServlet("/RecetaServlet")
public class RecetaServlet extends HttpServlet {

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

        RecetaDAO dao = new RecetaDAO();
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
                    tabla.append("<button class='btn btn-sm btn-primary' onclick='verRecetas(")
                            .append(m.get("idMascota")).append(")'>Ver Recetas</button>");
                    tabla.append("</td>");
                    tabla.append("</tr>");
                }

                tabla.append("</tbody></table>");
                out.print(tabla.toString());
                break;
            }

            case "listarRecetas": {
                String idMascotaParam = request.getParameter("idMascota");
                System.out.println("🔍 [listarRecetas] Parámetro recibido: " + idMascotaParam);

                int idMascota = Integer.parseInt(idMascotaParam);
                System.out.println("✅ [listarRecetas] ID Mascota parseado: " + idMascota);

                List<Map<String, Object>> recetas = dao.listarRecetas(idMascota);
                System.out.println("📊 [listarRecetas] Recetas encontradas: " + recetas.size());

                StringBuilder html = new StringBuilder();
                html.append("<div class='mb-3'>");
                html.append("<button class='btn btn-success' onclick='abrirModalRegistrar()'>");
                html.append("<i class='fas fa-plus'></i> Registrar Receta");
                html.append("</button>");
                html.append("</div>");

                if (recetas.isEmpty()) {
                    html.append("<div class='alert alert-info'>No hay recetas registradas para esta mascota</div>");
                } else {
                    html.append("<div class='table-responsive'>");
                    html.append("<table class='table table-hover'>");
                    html.append("<thead><tr><th>Fecha</th><th>Medicamento</th><th>Dosis</th><th>Frecuencia</th><th>Consulta</th><th>Veterinario</th><th>Estado</th><th>Acciones</th></tr></thead>");
                    html.append("<tbody>");

                    for (Map<String, Object> r : recetas) {
                        String estadoClass = r.get("estado").equals("ACTIVO") ? "badge-success" : "badge-anulada";

                        html.append("<tr>");
                        html.append("<td>").append(sdfHora.format(r.get("fechaRegistro"))).append("</td>");
                        html.append("<td>").append(escape(String.valueOf(r.get("medicamento")))).append("</td>");
                        html.append("<td>").append(escape(String.valueOf(r.get("dosis")))).append("</td>");
                        html.append("<td>").append(escape(String.valueOf(r.get("frecuencia")))).append("</td>");
                        html.append("<td>").append(sdfHora.format(r.get("fechaConsulta"))).append("<br><small class='text-muted'>").append(escape(String.valueOf(r.get("motivoConsulta")))).append("</small></td>");
                        html.append("<td>").append(escape(String.valueOf(r.get("veterinario")))).append("</td>");
                        html.append("<td><span class='badge ").append(estadoClass).append("'>").append(r.get("estado")).append("</span></td>");
                        html.append("<td>");

                        if (r.get("estado").equals("ACTIVO")) {
                            html.append("<button class='btn btn-sm btn-warning me-1' onclick='abrirModalEditar(")
                                    .append(r.get("idReceta")).append(")'><i class='fas fa-edit'></i></button>");
                            html.append("<button class='btn btn-sm btn-danger' onclick='abrirModalAnular(")
                                    .append(r.get("idReceta")).append(")'><i class='fas fa-ban'></i></button>");
                        } else {
                            html.append("<span class='text-muted'>Anulada</span>");
                        }

                        html.append("</td>");
                        html.append("</tr>");
                    }

                    html.append("</tbody></table>");
                    html.append("</div>");
                }

                System.out.println("📄 [listarRecetas] HTML generado, longitud: " + html.length());
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
                    options.append(" (").append(c.get("totalRecetas")).append(" recetas)");
                    options.append("</option>");
                }

                out.print(options.toString());
                break;
            }

            case "obtenerDetalles": {
                int idReceta = Integer.parseInt(request.getParameter("idReceta"));
                cx.conexion conexionDB = new cx.conexion();
                Connection conn = null;
                PreparedStatement ps = null;
                ResultSet rs = null;

                try {
                    conn = conexionDB.conectar();

                    if (conn == null) {
                        out.print("ERROR|No se pudo conectar a la base de datos");
                        return;
                    }

                    String sql = "SELECT MEDICAMENTO, DOSIS, FRECUENCIA, DURACION, INDICACIONES, OBSERVACIONES "
                            + "FROM recetas WHERE ID_RECETA = ?";
                    ps = conn.prepareStatement(sql);
                    ps.setInt(1, idReceta);
                    rs = ps.executeQuery();

                    if (rs.next()) {
                        // Obtener valores manejando NULLs correctamente
                        String medicamento = rs.getString("MEDICAMENTO");
                        String dosis = rs.getString("DOSIS");
                        String frecuencia = rs.getString("FRECUENCIA");
                        String duracion = rs.getString("DURACION");
                        String indicaciones = rs.getString("INDICACIONES");
                        String observaciones = rs.getString("OBSERVACIONES");

                        // Construir JSON limpiamente
                        StringBuilder json = new StringBuilder();
                        json.append("{");
                        json.append("\"medicamento\":\"").append(escape(medicamento != null ? medicamento : "")).append("\",");
                        json.append("\"dosis\":\"").append(escape(dosis != null ? dosis : "")).append("\",");
                        json.append("\"frecuencia\":\"").append(escape(frecuencia != null ? frecuencia : "")).append("\",");
                        json.append("\"duracion\":\"").append(escape(duracion != null ? duracion : "")).append("\",");
                        json.append("\"indicaciones\":\"").append(escape(indicaciones != null ? indicaciones : "")).append("\",");
                        json.append("\"observaciones\":\"").append(escape(observaciones != null ? observaciones : "")).append("\"");
                        json.append("}");

                        out.print(json.toString());
                    } else {
                        out.print("ERROR|Receta no encontrada");
                    }
                } catch (SQLException e) {
                    System.err.println("❌ Error SQL en obtenerDetalles: " + e.getMessage());
                    e.printStackTrace();
                    out.print("ERROR|Error al consultar: " + e.getMessage());
                } finally {
                    try {
                        if (rs != null) {
                            rs.close();
                        }
                        if (ps != null) {
                            ps.close();
                        }
                        if (conn != null) {
                            conn.close();
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
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

        if (idUsuario == null) {
            out.print("ERROR|Sesión no válida");
            return;
        }

        if (idVeterinario == null) {
            idVeterinario = obtenerIdVeterinarioPorUsuario(idUsuario);
            if (idVeterinario == null) {
                out.print("ERROR|No se pudo identificar al veterinario");
                return;
            }
        }

        RecetaDAO dao = new RecetaDAO();

        switch (accion) {
            case "registrarReceta": {
                try {
                    int idConsulta = Integer.parseInt(request.getParameter("idConsulta"));
                    int idMascota = Integer.parseInt(request.getParameter("idMascota"));
                    String medicamento = request.getParameter("medicamento");
                    String dosis = request.getParameter("dosis");
                    String frecuencia = request.getParameter("frecuencia");
                    String duracion = request.getParameter("duracion");
                    String indicaciones = request.getParameter("indicaciones");
                    String observaciones = request.getParameter("observaciones");

                    if (medicamento == null || medicamento.trim().isEmpty()) {
                        out.print("ERROR|El medicamento no puede estar vacío");
                        return;
                    }

                    if (dosis == null || dosis.trim().isEmpty()) {
                        out.print("ERROR|La dosis no puede estar vacía");
                        return;
                    }

                    if (frecuencia == null || frecuencia.trim().isEmpty()) {
                        out.print("ERROR|La frecuencia no puede estar vacía");
                        return;
                    }

                    boolean exito = dao.registrarReceta(idConsulta, idMascota, idVeterinario,
                            medicamento, dosis, frecuencia, duracion, indicaciones, observaciones);

                    if (exito) {
                        out.print("OK|Receta registrada correctamente");
                    } else {
                        out.print("ERROR|No se pudo registrar la receta");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    out.print("ERROR|Error al registrar: " + e.getMessage());
                }
                break;
            }

            case "editarReceta": {
                try {
                    int idReceta = Integer.parseInt(request.getParameter("idReceta"));
                    String medicamento = request.getParameter("medicamento");
                    String dosis = request.getParameter("dosis");
                    String frecuencia = request.getParameter("frecuencia");
                    String duracion = request.getParameter("duracion");
                    String indicaciones = request.getParameter("indicaciones");
                    String observaciones = request.getParameter("observaciones");

                    if (medicamento == null || medicamento.trim().isEmpty()) {
                        out.print("ERROR|El medicamento no puede estar vacío");
                        return;
                    }

                    boolean exito = dao.editarReceta(idReceta, medicamento, dosis, frecuencia,
                            duracion, indicaciones, observaciones);

                    if (exito) {
                        out.print("OK|Receta actualizada correctamente");
                    } else {
                        out.print("ERROR|No se pudo actualizar la receta");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    out.print("ERROR|Error al actualizar: " + e.getMessage());
                }
                break;
            }

            case "anularReceta": {
                try {
                    int idReceta = Integer.parseInt(request.getParameter("idReceta"));
                    String motivo = request.getParameter("motivo");

                    if (motivo == null || motivo.trim().isEmpty()) {
                        out.print("ERROR|Debe ingresar el motivo de anulación");
                        return;
                    }

                    boolean exito = dao.anularReceta(idReceta, motivo, idUsuario);

                    if (exito) {
                        out.print("OK|Receta anulada correctamente");
                    } else {
                        out.print("ERROR|No se pudo anular la receta");
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

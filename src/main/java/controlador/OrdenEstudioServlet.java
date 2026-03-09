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
import cx.OrdenEstudioDAO;

@WebServlet("/OrdenEstudioServlet")
public class OrdenEstudioServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        String action = request.getParameter("action");

        if (action == null) {
            out.print("ERROR|Acción no especificada");
            return;
        }

        OrdenEstudioDAO dao = new OrdenEstudioDAO();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");

        switch (action) {
            case "buscarMascotas": {
                String texto = request.getParameter("texto");
                if (texto == null || texto.trim().isEmpty()) {
                    out.print("<div class='alert alert-info'>Ingrese un criterio de búsqueda</div>");
                    return;
                }

                List<Map<String, Object>> mascotas = dao.buscarMascotasConConsultas(texto);

                if (mascotas.isEmpty()) {
                    out.print("<div class='alert alert-info'>No se encontraron mascotas con consultas finalizadas</div>");
                } else {
                    out.print("<div class='table-responsive'>");
                    out.print("<table class='table table-hover'>");
                    out.print("<thead><tr>");
                    out.print("<th>Mascota</th><th>Especie</th><th>Raza</th><th>Cliente</th><th>Teléfono</th><th>Consultas</th><th>Acción</th>");
                    out.print("</tr></thead><tbody>");

                    for (Map<String, Object> m : mascotas) {
                        out.print("<tr>");
                        out.print("<td>" + m.get("nombreMascota") + "</td>");
                        out.print("<td>" + (m.get("especie") != null ? m.get("especie") : "N/D") + "</td>");
                        out.print("<td>" + (m.get("raza") != null ? m.get("raza") : "N/D") + "</td>");
                        out.print("<td>" + m.get("nombreCliente") + "</td>");
                        out.print("<td>" + m.get("telefono") + "</td>");
                        out.print("<td><span class='badge bg-info'>" + m.get("totalConsultas") + "</span></td>");
                        out.print("<td>");
                        out.print("<button class='btn btn-sm btn-primary-custom' onclick='verOrdenes(" + m.get("idMascota") + ")'>");
                        out.print("<i class='fas fa-eye'></i> Ver Órdenes");
                        out.print("</button>");
                        out.print("</td>");
                        out.print("</tr>");
                    }

                    out.print("</tbody></table></div>");
                }
                break;
            }

            case "obtenerConsultas": {
                try {
                    int idMascota = Integer.parseInt(request.getParameter("idMascota"));
                    List<Map<String, Object>> consultas = dao.obtenerConsultasFinalizadas(idMascota);

                    if (consultas.isEmpty()) {
                        out.print("<option value=''>No hay consultas finalizadas</option>");
                    } else {
                        out.print("<option value=''>Seleccione una consulta</option>");
                        for (Map<String, Object> c : consultas) {
                            String fecha = sdf.format(c.get("fechaInicio"));
                            String ordenes = " (" + c.get("totalOrdenes") + " órdenes)";
                            out.print("<option value='" + c.get("idConsulta") + "'>");
                            out.print(fecha + " - " + c.get("motivo") + ordenes);
                            out.print("</option>");
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    out.print("<option value=''>Error al cargar consultas</option>");
                }
                break;
            }

            case "listarOrdenes": {
                try {
                    int idMascota = Integer.parseInt(request.getParameter("idMascota"));
                    List<Map<String, Object>> ordenes = dao.listarOrdenes(idMascota);

                    if (ordenes.isEmpty()) {
                        out.print("<div class='alert alert-info'>No hay órdenes de estudio registradas</div>");
                    } else {
                        out.print("<div class='table-responsive'>");
                        out.print("<table class='table table-hover'>");
                        out.print("<thead><tr>");
                        out.print("<th>Fecha</th><th>Tipo</th><th>Motivo</th><th>Veterinario</th><th>Estado</th><th>Acciones</th>");
                        out.print("</tr></thead><tbody>");

                        for (Map<String, Object> o : ordenes) {
                            String estado = (String) o.get("estado");
                            String badgeClass = estado.equals("EMITIDA") ? "bg-success" : "bg-danger";

                            out.print("<tr>");
                            out.print("<td>" + sdf.format(o.get("fechaOrden")) + "</td>");
                            out.print("<td>" + o.get("tipoEstudio") + "</td>");
                            out.print("<td>" + (o.get("motivo") != null ? o.get("motivo") : "-") + "</td>");
                            out.print("<td>" + o.get("veterinario") + "</td>");
                            out.print("<td><span class='badge " + badgeClass + "'>" + estado + "</span></td>");
                            out.print("<td>");

                            if (estado.equals("EMITIDA")) {
                                out.print("<button class='btn btn-sm btn-warning me-1' onclick='abrirModalEditar("
                                        + o.get("idOrden") + ", \"" + o.get("tipoEstudio") + "\", \""
                                        + escape((String) o.get("motivo")) + "\", \""
                                        + escape((String) o.get("observaciones")) + "\")'>");
                                out.print("<i class='fas fa-edit'></i>");
                                out.print("</button>");

                                out.print("<button class='btn btn-sm btn-danger' onclick='abrirModalAnular(" + o.get("idOrden") + ")'>");
                                out.print("<i class='fas fa-ban'></i>");
                                out.print("</button>");
                            }

                            out.print("</td>");
                            out.print("</tr>");
                        }

                        out.print("</tbody></table></div>");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    out.print("<div class='alert alert-danger'>Error: " + e.getMessage() + "</div>");
                }
                break;
            }

            case "obtenerTipos": {
                List<Map<String, Object>> tipos = dao.obtenerTiposEstudios();
                if (tipos.isEmpty()) {
                    out.print("<option value=''>No hay tipos disponibles</option>");
                } else {
                    out.print("<option value=''>Seleccione un tipo</option>");
                    for (Map<String, Object> t : tipos) {
                        out.print("<option value='" + t.get("idTipo") + "'>" + t.get("nombre") + "</option>");
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
        String action = request.getParameter("action");

        if (action == null) {
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

        OrdenEstudioDAO dao = new OrdenEstudioDAO();

        switch (action) {
            case "registrar": {
                try {
                    int idConsulta = Integer.parseInt(request.getParameter("idConsulta"));
                    int idMascota = Integer.parseInt(request.getParameter("idMascota"));
                    int idTipoEstudio = Integer.parseInt(request.getParameter("idTipoEstudio"));
                    String motivo = request.getParameter("motivo");
                    String observaciones = request.getParameter("observaciones");

                    if (motivo == null || motivo.trim().isEmpty()) {
                        out.print("ERROR|El motivo no puede estar vacío");
                        return;
                    }

                    boolean exito = dao.registrarOrden(idConsulta, idMascota, idVeterinario,
                            idTipoEstudio, motivo, observaciones);

                    if (exito) {
                        out.print("SUCCESS|Orden registrada correctamente");
                    } else {
                        out.print("ERROR|No se pudo registrar la orden");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    out.print("ERROR|Error al registrar: " + e.getMessage());
                }
                break;
            }

            case "actualizar": {
                try {
                    int idOrden = Integer.parseInt(request.getParameter("idOrden"));
                    int idTipoEstudio = Integer.parseInt(request.getParameter("idTipoEstudio"));
                    String motivo = request.getParameter("motivo");
                    String observaciones = request.getParameter("observaciones");

                    if (motivo == null || motivo.trim().isEmpty()) {
                        out.print("ERROR|El motivo no puede estar vacío");
                        return;
                    }

                    boolean exito = dao.editarOrden(idOrden, idTipoEstudio, motivo, observaciones);

                    if (exito) {
                        out.print("SUCCESS|Orden actualizada correctamente");
                    } else {
                        out.print("ERROR|No se pudo actualizar la orden");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    out.print("ERROR|Error al actualizar: " + e.getMessage());
                }
                break;
            }

            case "anular": {
                try {
                    int idOrden = Integer.parseInt(request.getParameter("idOrden"));
                    String motivoAnulacion = request.getParameter("motivoAnulacion");

                    if (motivoAnulacion == null || motivoAnulacion.trim().isEmpty()) {
                        out.print("ERROR|Debe especificar el motivo de anulación");
                        return;
                    }

                    boolean exito = dao.anularOrden(idOrden, idUsuario, motivoAnulacion);

                    if (exito) {
                        out.print("SUCCESS|Orden anulada correctamente");
                    } else {
                        out.print("ERROR|No se pudo anular la orden");
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

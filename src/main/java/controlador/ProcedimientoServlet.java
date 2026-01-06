package controlador;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import cx.ProcedimientoDAO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@WebServlet("/ProcedimientoServlet")
public class ProcedimientoServlet extends HttpServlet {

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

        ProcedimientoDAO dao = new ProcedimientoDAO();
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
                    tabla.append("<button class='btn btn-sm btn-primary' onclick='verProcedimientos(")
                            .append(m.get("idMascota")).append(")'>Ver Procedimientos</button>");
                    tabla.append("</td>");
                    tabla.append("</tr>");
                }

                tabla.append("</tbody></table>");
                out.print(tabla.toString());
                break;
            }

            case "listarProcedimientos": {
                String idMascotaParam = request.getParameter("idMascota");
                System.out.println("📋 [listarProcedimientos] Parámetro recibido: " + idMascotaParam);

                int idMascota = Integer.parseInt(idMascotaParam);
                System.out.println("✅ [listarProcedimientos] ID Mascota parseado: " + idMascota);

                List<Map<String, Object>> procedimientos = dao.listarProcedimientos(idMascota);
                System.out.println("📊 [listarProcedimientos] Procedimientos encontrados: " + procedimientos.size());

                StringBuilder html = new StringBuilder();
                html.append("<div class='mb-3'>");
                html.append("<button class='btn btn-success' onclick='abrirModalRegistrar()'>");
                html.append("<i class='fas fa-plus'></i> Registrar Procedimiento");
                html.append("</button>");
                html.append("</div>");

                if (procedimientos.isEmpty()) {
                    html.append("<div class='alert alert-info'>No hay procedimientos registrados para esta mascota</div>");
                } else {
                    html.append("<div class='table-responsive'>");
                    html.append("<table class='table table-hover'>");
                    html.append("<thead><tr><th>Fecha</th><th>Tipo</th><th>Descripción</th><th>Consulta</th><th>Veterinario</th><th>Estado</th><th>Acciones</th></tr></thead>");
                    html.append("<tbody>");

                    for (Map<String, Object> p : procedimientos) {
                        String estadoClass = p.get("estado").equals("ACTIVO") ? "badge-success" : "badge-anulada";

                        html.append("<tr>");
                        html.append("<td>").append(sdfHora.format(p.get("fechaRegistro"))).append("</td>");
                        html.append("<td>").append(escape(String.valueOf(p.get("tipoProcedimiento")))).append("</td>");
                        html.append("<td>").append(escape(String.valueOf(p.get("descripcion")))).append("</td>");
                        html.append("<td>").append(sdfHora.format(p.get("fechaConsulta"))).append("<br><small class='text-muted'>").append(escape(String.valueOf(p.get("motivoConsulta")))).append("</small></td>");
                        html.append("<td>").append(escape(String.valueOf(p.get("veterinario")))).append("</td>");
                        html.append("<td><span class='badge ").append(estadoClass).append("'>").append(p.get("estado")).append("</span></td>");
                        html.append("<td>");

                        if (p.get("estado").equals("ACTIVO")) {
                            html.append("<button class='btn btn-sm btn-info me-1' onclick='abrirModalVerInsumos(")
                                    .append(p.get("idProcedimiento")).append(")' title='Ver Insumos'><i class='fas fa-box'></i></button>");
                            html.append("<button class='btn btn-sm btn-warning me-1' onclick='abrirModalEditar(")
                                    .append(p.get("idProcedimiento")).append(")'><i class='fas fa-edit'></i></button>");
                            html.append("<button class='btn btn-sm btn-danger' onclick='abrirModalAnular(")
                                    .append(p.get("idProcedimiento")).append(")'><i class='fas fa-ban'></i></button>");
                        } else {
                            html.append("<span class='text-muted'>Anulado</span>");
                        }

                        html.append("</td>");
                        html.append("</tr>");
                    }

                    html.append("</tbody></table>");
                    html.append("</div>");
                }

                System.out.println("📄 [listarProcedimientos] HTML generado, longitud: " + html.length());
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
                    options.append(" (").append(c.get("totalProcedimientos")).append(" procedimientos)");
                    options.append("</option>");
                }

                out.print(options.toString());
                break;
            }

            case "listarInsumosDisponibles": {
                List<Map<String, Object>> insumos = dao.listarInsumosDisponibles();

                if (insumos.isEmpty()) {
                    out.print("<option value=''>No hay insumos disponibles</option>");
                    return;
                }

                StringBuilder options = new StringBuilder();
                options.append("<option value=''>Seleccione un insumo</option>");

                for (Map<String, Object> i : insumos) {
                    options.append("<option value='").append(i.get("idInsumo")).append("' data-stock='").append(i.get("stock")).append("'>");
                    options.append(escape(String.valueOf(i.get("nombre"))));
                    options.append(" (Stock: ").append(i.get("stock")).append(" ").append(escape(String.valueOf(i.get("unidad")))).append(")");
                    options.append("</option>");
                }

                out.print(options.toString());
                break;
            }

            case "obtenerDetalles": {
                int idProcedimiento = Integer.parseInt(request.getParameter("idProcedimiento"));
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
                    String sql = "SELECT TIPO_PROCEDIMIENTO, DESCRIPCION, OBSERVACIONES, ID_CONSULTA, ID_MASCOTA "
                            + "FROM procedimientos WHERE ID_PROCEDIMIENTO = ?";
                    ps = conn.prepareStatement(sql);
                    ps.setInt(1, idProcedimiento);
                    rs = ps.executeQuery();
                    if (rs.next()) {
                        String tipoProcedimiento = rs.getString("TIPO_PROCEDIMIENTO");
                        String descripcion = rs.getString("DESCRIPCION");
                        String observaciones = rs.getString("OBSERVACIONES");
                        int idConsulta = rs.getInt("ID_CONSULTA");
                        int idMascota = rs.getInt("ID_MASCOTA");

                        // Devolver formato: tipo|||descripcion|||observaciones|||idConsulta|||idMascota
                        String respuesta = (tipoProcedimiento != null ? tipoProcedimiento : "") + "|||"
                                + (descripcion != null ? descripcion : "") + "|||"
                                + (observaciones != null ? observaciones : "") + "|||"
                                + idConsulta + "|||"
                                + idMascota;
                        out.print(respuesta);
                    } else {
                        out.print("ERROR|Procedimiento no encontrado");
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
            case "obtenerInsumos": {
                int idProcedimiento = Integer.parseInt(request.getParameter("idProcedimiento"));

                List<Map<String, Object>> insumos = dao.obtenerInsumosPorProcedimiento(idProcedimiento);

                if (insumos.isEmpty()) {
                    out.print("<div class='alert alert-info'><i class='fas fa-info-circle'></i> Este procedimiento no utilizó insumos</div>");
                    return;
                }

                StringBuilder html = new StringBuilder();
                html.append("<div class='table-responsive'>");
                html.append("<table class='table table-hover'>");
                html.append("<thead><tr><th>Insumo</th><th>Cantidad Utilizada</th><th>Unidad</th><th>Stock Actual</th></tr></thead>");
                html.append("<tbody>");

                for (Map<String, Object> insumo : insumos) {
                    html.append("<tr>");
                    html.append("<td><strong>").append(escape(String.valueOf(insumo.get("nombreInsumo")))).append("</strong></td>");
                    html.append("<td><span class='badge bg-primary'>").append(insumo.get("cantidad")).append("</span></td>");
                    html.append("<td>").append(escape(String.valueOf(insumo.get("unidad")))).append("</td>");
                    html.append("<td>");

                    int stockActual = (int) insumo.get("stockActual");
                    String stockClass = stockActual > 10 ? "text-success" : (stockActual > 0 ? "text-warning" : "text-danger");

                    html.append("<span class='").append(stockClass).append("'><i class='fas fa-box'></i> ").append(stockActual).append("</span>");
                    html.append("</td>");
                    html.append("</tr>");
                }

                html.append("</tbody></table>");
                html.append("</div>");

                html.append("<div class='alert alert-info mt-3'>");
                html.append("<i class='fas fa-info-circle'></i> ");
                html.append("El <strong>Stock Actual</strong> es el que hay disponible en este momento (después del descuento).");
                html.append("</div>");

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

        ProcedimientoDAO dao = new ProcedimientoDAO();

        switch (accion) {
            case "registrarProcedimiento": {
                try {
                    int idConsulta = Integer.parseInt(request.getParameter("idConsulta"));
                    int idMascota = Integer.parseInt(request.getParameter("idMascota"));
                    String tipoProcedimiento = request.getParameter("tipoProcedimiento");
                    String descripcion = request.getParameter("descripcion");
                    String observaciones = request.getParameter("observaciones");

                    if (tipoProcedimiento == null || tipoProcedimiento.trim().isEmpty()) {
                        out.print("ERROR|El tipo de procedimiento no puede estar vacío");
                        return;
                    }

                    if (descripcion == null || descripcion.trim().isEmpty()) {
                        out.print("ERROR|La descripción no puede estar vacía");
                        return;
                    }

                    // Procesar insumos (OPCIONAL - puede ser null)
                    String[] idsInsumos = request.getParameterValues("idInsumo[]");
                    String[] cantidades = request.getParameterValues("cantidad[]");

                    System.out.println("🔍 [Servlet] idsInsumos recibidos: " + (idsInsumos != null ? idsInsumos.length : "null"));
                    System.out.println("🔍 [Servlet] cantidades recibidas: " + (cantidades != null ? cantidades.length : "null"));

                    List<Map<String, Integer>> insumos = new ArrayList<>();

                    if (idsInsumos != null && cantidades != null && idsInsumos.length == cantidades.length) {
                        System.out.println("📦 [Servlet] Parseando " + idsInsumos.length + " insumos...");
                        for (int i = 0; i < idsInsumos.length; i++) {
                            Map<String, Integer> insumo = new HashMap<>();
                            insumo.put("idInsumo", Integer.parseInt(idsInsumos[i]));
                            insumo.put("cantidad", Integer.parseInt(cantidades[i]));
                            insumos.add(insumo);
                            System.out.println("   ✅ Insumo " + (i + 1) + ": ID=" + idsInsumos[i] + ", Cantidad=" + cantidades[i]);
                        }
                    } else {
                        System.out.println("⚠️ [Servlet] No se enviaron insumos (procedimiento sin insumos)");
                    }

                    System.out.println("📊 [Servlet] Total de insumos a enviar al DAO: " + insumos.size());

                    boolean exito = dao.registrarProcedimiento(idConsulta, idMascota, idVeterinario,
                            tipoProcedimiento, descripcion, observaciones, insumos);

                    if (exito) {
                        out.print("OK|Procedimiento registrado correctamente");
                    } else {
                        out.print("ERROR|No se pudo registrar el procedimiento");
                    }
                } catch (Exception e) {
                    System.err.println("❌ [Servlet] Error al registrar procedimiento: " + e.getMessage());
                    e.printStackTrace();
                    out.print("ERROR|Error al registrar: " + e.getMessage());
                }
                break;
            }
            case "editarProcedimiento": {
                try {
                    int idProcedimiento = Integer.parseInt(request.getParameter("idProcedimiento"));
                    String tipoProcedimiento = request.getParameter("tipoProcedimiento");
                    String descripcion = request.getParameter("descripcion");
                    String observaciones = request.getParameter("observaciones");

                    if (tipoProcedimiento == null || tipoProcedimiento.trim().isEmpty()) {
                        out.print("ERROR|El tipo de procedimiento no puede estar vacío");
                        return;
                    }

                    if (descripcion == null || descripcion.trim().isEmpty()) {
                        out.print("ERROR|La descripción no puede estar vacía");
                        return;
                    }

                    boolean exito = dao.editarProcedimiento(idProcedimiento, tipoProcedimiento,
                            descripcion, observaciones);

                    if (exito) {
                        out.print("OK|Procedimiento actualizado correctamente");
                    } else {
                        out.print("ERROR|No se pudo actualizar el procedimiento");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    out.print("ERROR|Error al actualizar: " + e.getMessage());
                }
                break;
            }

            case "anularProcedimiento": {
                try {
                    int idProcedimiento = Integer.parseInt(request.getParameter("idProcedimiento"));
                    String motivo = request.getParameter("motivo");

                    if (motivo == null || motivo.trim().isEmpty()) {
                        out.print("ERROR|Debe ingresar el motivo de anulación");
                        return;
                    }

                    boolean exito = dao.anularProcedimiento(idProcedimiento, motivo, idUsuario);

                    if (exito) {
                        out.print("OK|Procedimiento anulado correctamente (stock revertido)");
                    } else {
                        out.print("ERROR|No se pudo anular el procedimiento");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    out.print("ERROR|Error al anular: " + e.getMessage());
                }
                break;
            }

            case "corregirProcedimiento": {
                try {
                    int idProcedimientoOriginal = Integer.parseInt(request.getParameter("idProcedimientoOriginal"));
                    int idConsulta = Integer.parseInt(request.getParameter("idConsulta"));
                    int idMascota = Integer.parseInt(request.getParameter("idMascota"));
                    String tipo = request.getParameter("tipoProcedimiento");
                    String descripcion = request.getParameter("descripcion");
                    String observaciones = request.getParameter("observaciones");

                    System.out.println("🔄 [Servlet] Corrigiendo procedimiento ID: " + idProcedimientoOriginal);

                    // Procesar insumos
                    String[] idsInsumosStr = request.getParameterValues("idInsumo[]");
                    String[] cantidadesStr = request.getParameterValues("cantidad[]");

                    List<Integer> idsInsumos = new ArrayList<>();
                    List<Integer> cantidades = new ArrayList<>();

                    if (idsInsumosStr != null && cantidadesStr != null) {
                        System.out.println("📦 [Servlet] Procesando " + idsInsumosStr.length + " insumos...");
                        for (int i = 0; i < idsInsumosStr.length; i++) {
                            idsInsumos.add(Integer.parseInt(idsInsumosStr[i]));
                            cantidades.add(Integer.parseInt(cantidadesStr[i]));
                            System.out.println("   ✅ Insumo " + (i + 1) + ": ID=" + idsInsumosStr[i] + ", Cantidad=" + cantidadesStr[i]);
                        }
                    } else {
                        System.out.println("⚠️ [Servlet] No se enviaron insumos");
                    }

                    out.print(dao.corregirProcedimiento(idProcedimientoOriginal, idConsulta, idMascota,
                            tipo, descripcion, observaciones, idsInsumos, cantidades));

                } catch (NumberFormatException e) {
                    out.print("ERROR|Datos inválidos: " + e.getMessage());
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
            Connection conexion = conn.conectar();
            String sql = "SELECT ID_VETERINARIO FROM veterinarios WHERE ID_USUARIO = ?";
            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.setInt(1, idUsuario);
            ResultSet rs = ps.executeQuery();

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

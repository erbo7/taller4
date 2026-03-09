// controlador/ConsultaServlet.java - VERSIÓN CORREGIDA
package controlador;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.sql.*;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import cx.ConsultaDAO;
import cx.conexion;
import modelo.Consulta;

@WebServlet("/ConsultaServlet")
public class ConsultaServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/plain;charset=UTF-8");
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

        ConsultaDAO dao = new ConsultaDAO();

        switch (accion) {
            case "iniciarConsulta": // ← CORREGIDO: era "iniciar", ahora "iniciarConsulta"
                String motivo = request.getParameter("motivo");
                String pesoStr = request.getParameter("peso");
                if (motivo == null || motivo.trim().isEmpty() || pesoStr == null || pesoStr.trim().isEmpty()) {
                    out.print("ERROR|Los campos Motivo y Peso son obligatorios.");
                    return;
                }
                try {
                    Consulta c = new Consulta();
                    c.setIdCita(Integer.parseInt(request.getParameter("idCita")));
                    c.setIdVeterinario(Integer.parseInt(request.getParameter("idVeterinario"))); // ← CORREGIDO
                    c.setIdMascota(Integer.parseInt(request.getParameter("idMascota")));
                    c.setMotivo(motivo.trim());
                    c.setPeso(new BigDecimal(pesoStr.trim()));

                    String tempStr = request.getParameter("temperatura");
                    c.setTemperatura((tempStr == null || tempStr.isEmpty()) ? null : new BigDecimal(tempStr.trim()));

                    String fcStr = request.getParameter("frecuenciaCardiaca");
                    c.setFrecuenciaCardiaca((fcStr == null || fcStr.isEmpty()) ? null : Integer.valueOf(fcStr.trim()));

                    String frStr = request.getParameter("frecuenciaRespiratoria");
                    c.setFrecuenciaRespiratoria((frStr == null || frStr.isEmpty()) ? null : Integer.valueOf(frStr.trim()));

                    c.setMucosas(request.getParameter("mucosas"));
                    c.setCapReflejo(request.getParameter("capReflejo"));
                    c.setCondicionCorporal(request.getParameter("condicionCorporal"));
                    c.setObservaciones(request.getParameter("observaciones"));

                    out.print(dao.iniciarConsulta(c));
                } catch (Exception e) {
                    out.print("ERROR|Datos inválidos: " + e.getMessage());
                }
                break;

            case "editarConsulta": // ← CORREGIDO: era "editar", ahora "editarConsulta"
                motivo = request.getParameter("motivo");
                pesoStr = request.getParameter("peso");
                if (motivo == null || motivo.trim().isEmpty() || pesoStr == null || pesoStr.trim().isEmpty()) {
                    out.print("ERROR|Los campos Motivo y Peso son obligatorios.");
                    return;
                }
                try {
                    Consulta c = new Consulta();
                    c.setIdConsulta(Integer.parseInt(request.getParameter("idConsulta")));
                    c.setMotivo(motivo.trim());
                    c.setPeso(new BigDecimal(pesoStr.trim()));

                    String tempStr = request.getParameter("temperatura");
                    c.setTemperatura((tempStr == null || tempStr.isEmpty()) ? null : new BigDecimal(tempStr.trim()));

                    String fcStr = request.getParameter("frecuenciaCardiaca");
                    c.setFrecuenciaCardiaca((fcStr == null || fcStr.isEmpty()) ? null : Integer.valueOf(fcStr.trim()));

                    String frStr = request.getParameter("frecuenciaRespiratoria");
                    c.setFrecuenciaRespiratoria((frStr == null || frStr.isEmpty()) ? null : Integer.valueOf(frStr.trim()));

                    c.setMucosas(request.getParameter("mucosas"));
                    c.setCapReflejo(request.getParameter("capReflejo"));
                    c.setCondicionCorporal(request.getParameter("condicionCorporal"));
                    c.setObservaciones(request.getParameter("observaciones"));

                    out.print(dao.editarConsulta(c));
                } catch (Exception e) {
                    out.print("ERROR|Datos inválidos: " + e.getMessage());
                }
                break;

            case "anularConsulta": // ← CORREGIDO: era "anular", ahora "anularConsulta"
                int idConsulta = Integer.parseInt(request.getParameter("idConsulta"));
                String motivoAnul = request.getParameter("motivoAnulacion"); // ← CORREGIDO
                if (motivoAnul == null || motivoAnul.trim().isEmpty()) {
                    out.print("ERROR|El motivo de anulación es obligatorio.");
                    return;
                }
                out.print(dao.anularConsulta(idConsulta, motivoAnul));
                break;

            case "finalizarConsulta": {
                int idConsultaFin = Integer.parseInt(request.getParameter("idConsulta"));
                out.print(dao.finalizarConsulta(idConsultaFin));
                break;
            }

            default:
                out.print("ERROR|Acción no soportada: " + accion);
        }
    }

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

        ConsultaDAO dao = new ConsultaDAO();

        switch (accion) {
            case "listarCitasHoy": {
                StringBuilder htmlCitas = new StringBuilder(); // ← RENOMBRADO para evitar conflicto
                for (ConsultaDAO.CitaResumen c : dao.obtenerCitasHoy(idUsuario)) {
                    htmlCitas.append("<div class='patient-item'>");
                    htmlCitas.append("<div class='patient-time'>").append(c.getHoraInicio()).append("</div>");
                    htmlCitas.append("<div class='patient-info'>");
                    htmlCitas.append("<h6>").append(escape(c.getNombreMascota())).append("</h6>");
                    htmlCitas.append("<p>Dueño: ").append(escape(c.getNombreCliente())).append("</p>");
                    htmlCitas.append("</div>");
                    htmlCitas.append("<button class='btn-outline-custom' onclick=\"abrirIniciarConsulta(")
                            .append(c.getIdCita()).append(")\">Iniciar Consulta</button>");
                    htmlCitas.append("</div>");
                }
                if (htmlCitas.length() == 0) {
                    htmlCitas.append("<div class='alert alert-info'>No tienes citas para hoy.</div>");
                }
                out.print(htmlCitas.toString());
                break;
            }

            case "listarEnCurso": {
                StringBuilder htmlEnCurso = new StringBuilder();
                java.util.List<Consulta> consultasEnCurso = dao.obtenerConsultasEnCurso(idUsuario);

                if (consultasEnCurso.isEmpty()) {
                    htmlEnCurso.append("<div class='alert alert-info text-center'>");
                    htmlEnCurso.append("<i class='fas fa-info-circle'></i> No tienes consultas en curso en este momento.");
                    htmlEnCurso.append("</div>");
                } else {
                    for (Consulta c : consultasEnCurso) {
                        // Extraer nombre mascota y cliente de observaciones (formato: "Mascota - Cliente")
                        String[] partes = c.getObservaciones().split(" - ");
                        String nombreMascota = partes.length > 0 ? partes[0] : "Desconocida";
                        String nombreCliente = partes.length > 1 ? partes[1] : "Desconocido";

                        htmlEnCurso.append("<div class='consulta-en-curso-item'>");
                        htmlEnCurso.append("<div class='consulta-info-principal'>");
                        htmlEnCurso.append("<h6><i class='fas fa-paw'></i> ").append(escape(nombreMascota)).append("</h6>");
                        htmlEnCurso.append("<div class='detalles'>");
                        htmlEnCurso.append("<span class='detalle-item'><i class='fas fa-user'></i> ").append(escape(nombreCliente)).append("</span>");
                        htmlEnCurso.append("<span class='detalle-item'><i class='fas fa-notes-medical'></i> ").append(escape(c.getMotivo())).append("</span>");
                        if (c.getPeso() != null) {
                            htmlEnCurso.append("<span class='detalle-item'><i class='fas fa-weight'></i> ").append(c.getPeso()).append(" kg</span>");
                        }
                        if (c.getTemperatura() != null) {
                            htmlEnCurso.append("<span class='detalle-item'><i class='fas fa-thermometer-half'></i> ").append(c.getTemperatura()).append(" °C</span>");
                        }
                        htmlEnCurso.append("</div>");
                        htmlEnCurso.append("</div>");

                        htmlEnCurso.append("<div class='consulta-acciones'>");
                        htmlEnCurso.append("<button class='btn-accion btn-continuar' onclick='abrirEditarConsulta(").append(c.getIdConsulta()).append(")'>");
                        htmlEnCurso.append("<i class='fas fa-edit'></i> Continuar</button>");
                        htmlEnCurso.append("</div>");
                    }
                }
                out.print(htmlEnCurso.toString());
                break;
            }

            // EN ConsultaServlet.java - REEMPLAZAR EL case "iniciarForm" COMPLETO
            case "iniciarForm":
                int idCita = Integer.parseInt(request.getParameter("idCita"));

                // Query CORREGIDA según tu estructura real de BD
                String sqlCita = """
        SELECT c.ID_CITA, c.ID_MASCOTA, c.ID_VETERINARIO,
               m.NOMBRE AS mascota, 
               m.EDAD,
               m.SEXO,
               r.NOMBRE AS raza,
               e.NOMBRE AS especie,
               cl.NOMBRE AS cliente, 
               cl.TELEFONO
        FROM citas c
        JOIN mascotas m ON c.ID_MASCOTA = m.ID_MASCOTA
        LEFT JOIN razas r ON m.ID_RAZA = r.ID_RAZA
        LEFT JOIN especies e ON r.ID_ESPECIE = e.ID_ESPECIE
        JOIN clientes cl ON m.ID_CLIENTE = cl.ID_CLIENTE
        WHERE c.ID_CITA = ?
        """;

                try (Connection conn = new conexion().conectar(); PreparedStatement ps = conn.prepareStatement(sqlCita)) {
                    ps.setInt(1, idCita);
                    ResultSet rs = ps.executeQuery();

                    if (rs.next()) {
                        StringBuilder htmlForm = new StringBuilder();
                        htmlForm.append("<form id='formConsulta' onsubmit='event.preventDefault()'>");
                        htmlForm.append("<input type='hidden' name='accion' value='iniciarConsulta'>");
                        htmlForm.append("<input type='hidden' name='idCita' value='").append(idCita).append("'>");
                        htmlForm.append("<input type='hidden' name='idMascota' value='").append(rs.getInt("ID_MASCOTA")).append("'>");
                        htmlForm.append("<input type='hidden' name='idVeterinario' value='").append(rs.getInt("ID_VETERINARIO")).append("'>");

                        // Obtener los valores (con null check)
                        String nombreMascota = rs.getString("mascota");
                        String especie = rs.getString("especie") != null ? rs.getString("especie") : "No especificada";
                        String raza = rs.getString("raza") != null ? rs.getString("raza") : "No especificada";
                        String cliente = rs.getString("cliente");
                        String sexo = rs.getString("SEXO") != null ? rs.getString("SEXO") : "N/D";
                        Integer edad = rs.getInt("EDAD");
                        String edadTexto = edad > 0 ? edad + " años" : "N/D";

                        // === ENCABEZADO CON INFO DE LA MASCOTA ===
                        htmlForm.append("<div class='alert alert-info mb-3'>");
                        htmlForm.append("<strong>🐾 Mascota:</strong> ").append(nombreMascota);
                        htmlForm.append(" (").append(sexo).append(", ").append(edadTexto).append(")");
                        htmlForm.append(" | <strong>Dueño:</strong> ").append(cliente);
                        htmlForm.append("<br><strong>Especie:</strong> ").append(especie);
                        htmlForm.append(" | <strong>Raza:</strong> ").append(raza);
                        htmlForm.append("</div>");

                        // === SIN PESTAÑAS - SOLO FORMULARIO DIRECTO ===
                        htmlForm.append("<div class='row mb-3'>");
                        htmlForm.append("<div class='col-md-12'>");
                        htmlForm.append("<label class='form-label'><i class='fas fa-notes-medical'></i> Motivo de la Visita *</label>");
                        htmlForm.append("<textarea name='motivo' class='form-control' rows='3' required></textarea>");
                        htmlForm.append("</div></div>");

                        htmlForm.append("<div class='row mb-3'>");
                        htmlForm.append("<div class='col-md-4'>");
                        htmlForm.append("<label class='form-label'><i class='fas fa-weight'></i> Peso (kg) *</label>");
                        htmlForm.append("<input type='number' name='peso' class='form-control' step='0.01' required>");
                        htmlForm.append("</div>");
                        htmlForm.append("<div class='col-md-4'>");
                        htmlForm.append("<label class='form-label'><i class='fas fa-thermometer-half'></i> Temperatura (°C)</label>");
                        htmlForm.append("<input type='number' name='temperatura' class='form-control' step='0.1'>");
                        htmlForm.append("</div>");
                        htmlForm.append("<div class='col-md-4'>");
                        htmlForm.append("<label class='form-label'><i class='fas fa-heartbeat'></i> Frec. Cardíaca (lpm)</label>");
                        htmlForm.append("<input type='number' name='frecuenciaCardiaca' class='form-control'>");
                        htmlForm.append("</div></div>");

                        htmlForm.append("<div class='row mb-3'>");
                        htmlForm.append("<div class='col-md-4'>");
                        htmlForm.append("<label class='form-label'><i class='fas fa-lungs'></i> Frec. Respiratoria (rpm)</label>");
                        htmlForm.append("<input type='number' name='frecuenciaRespiratoria' class='form-control'>");
                        htmlForm.append("</div>");
                        htmlForm.append("<div class='col-md-4'>");
                        htmlForm.append("<label class='form-label'>Estado de Mucosas</label>");
                        htmlForm.append("<select name='mucosas' class='form-select'>");
                        htmlForm.append("<option value=''>-- Seleccionar --</option>");
                        htmlForm.append("<option value='Rosadas'>Rosadas</option>");
                        htmlForm.append("<option value='Pálidas'>Pálidas</option>");
                        htmlForm.append("<option value='Ictéricas'>Ictéricas</option>");
                        htmlForm.append("<option value='Cianóticas'>Cianóticas</option>");
                        htmlForm.append("</select></div>");
                        htmlForm.append("<div class='col-md-4'>");
                        htmlForm.append("<label class='form-label'>CAP (Reflejo)</label>");
                        htmlForm.append("<input type='text' name='capReflejo' class='form-control'>");
                        htmlForm.append("</div></div>");

                        htmlForm.append("<div class='row mb-3'>");
                        htmlForm.append("<div class='col-md-6'>");
                        htmlForm.append("<label class='form-label'>Condición Corporal</label>");
                        htmlForm.append("<select name='condicionCorporal' class='form-select'>");
                        htmlForm.append("<option value=''>-- Seleccionar --</option>");
                        htmlForm.append("<option value='Caquéctico'>1 - Caquéctico</option>");
                        htmlForm.append("<option value='Delgado'>2 - Delgado</option>");
                        htmlForm.append("<option value='Ideal'>3 - Ideal</option>");
                        htmlForm.append("<option value='Sobrepeso'>4 - Sobrepeso</option>");
                        htmlForm.append("<option value='Obeso'>5 - Obeso</option>");
                        htmlForm.append("</select></div></div>");

                        htmlForm.append("<div class='row mb-3'>");
                        htmlForm.append("<div class='col-md-12'>");
                        htmlForm.append("<label class='form-label'>Observaciones Generales</label>");
                        htmlForm.append("<textarea name='observaciones' class='form-control' rows='3'></textarea>");
                        htmlForm.append("</div></div>");

                        // === BOTONES ===
                        htmlForm.append("<div class='mt-4 d-flex justify-content-end gap-2'>");
                        htmlForm.append("<button type='button' class='btn btn-secondary' data-bs-dismiss='modal'>Cancelar</button>");
                        htmlForm.append("<button type='button' class='btn btn-primary' onclick='enviarFormulario()'>");
                        htmlForm.append("<i class='fas fa-check'></i> Iniciar Consulta</button>");
                        htmlForm.append("</div>");

                        htmlForm.append("</form>");

                        response.getWriter().write(htmlForm.toString());
                    } else {
                        response.getWriter().write("ERROR: No se encontró la cita");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    response.getWriter().write("ERROR: " + e.getMessage());
                }
                return;

            case "editarForm": {
                int idConsulta = Integer.parseInt(request.getParameter("idConsulta"));
                Consulta cons = dao.obtenerConsultaPorId(idConsulta);
                if (cons == null) {
                    out.print("<div class='alert alert-danger'>Consulta no encontrada</div>");
                    return;
                }
                out.print(buildFormularioEditar(cons));
                break;
            }

            case "anularForm": {
                int idConsulta = Integer.parseInt(request.getParameter("idConsulta"));
                out.print(buildFormularioAnular(idConsulta));
                break;
            }

            case "buscarHistorial": {
                String texto = request.getParameter("texto");
                if (texto == null || texto.trim().isEmpty()) {
                    out.print("<div class='alert alert-warning'>Ingresa un texto de búsqueda</div>");
                    return;
                }

                String sqlBuscar = """
                   SELECT c.ID_CONSULTA, c.ESTADO, c.FECHA_CONSULTA, c.ID_MASCOTA,
                           m.NOMBRE as mascota, cl.NOMBRE as cliente
                    FROM consultas c
                    JOIN mascotas m ON c.ID_MASCOTA = m.ID_MASCOTA
                    JOIN clientes cl ON m.ID_CLIENTE = cl.ID_CLIENTE
                    WHERE (m.NOMBRE LIKE ? OR cl.NOMBRE LIKE ?)
                      AND c.ESTADO IN ('FINALIZADA', 'EN_CURSO', 'ANULADA')
                    ORDER BY c.FECHA_CONSULTA DESC
                    LIMIT 20
                    """;

                try (Connection conn = new conexion().conectar(); PreparedStatement ps = conn.prepareStatement(sqlBuscar)) {

                    ps.setString(1, "%" + texto + "%");
                    ps.setString(2, "%" + texto + "%");
                    ResultSet rs = ps.executeQuery();

                    StringBuilder tabla = new StringBuilder();
                    tabla.append("<table class='table table-hover table-consultas'>");
                    tabla.append("<thead><tr><th>Fecha</th><th>Mascota</th><th>Cliente</th><th>Estado</th><th>Acciones</th></tr></thead>");
                    tabla.append("<tbody>");

                    boolean hayResultados = false;
                    while (rs.next()) {
                        hayResultados = true;
                        int idCons = rs.getInt("ID_CONSULTA");
                        String estado = rs.getString("ESTADO");
                        String badgeClass = estado.equals("FINALIZADA") ? "badge-finalizada"
                                : estado.equals("EN_CURSO") ? "badge-en-curso"
                                : "badge-anulada";

                        tabla.append("<tr>");
                        tabla.append("<td>").append(rs.getString("FECHA_CONSULTA")).append("</td>");
                        tabla.append("<td>").append(escape(rs.getString("mascota"))).append("</td>");
                        tabla.append("<td>").append(escape(rs.getString("cliente"))).append("</td>");
                        tabla.append("<td><span class='badge-estado ").append(badgeClass).append("'>").append(estado).append("</span></td>");
                        tabla.append("<td>");

                        if (estado.equals("FINALIZADA") || estado.equals("EN_CURSO")) {
                            // Botón Ver Ficha
                            tabla.append("<button class='btn btn-sm btn-info me-2' onclick='abrirFichaMascota(")
                                    .append(rs.getInt("ID_MASCOTA")).append(")' title='Ver Ficha Médica'>")
                                    .append("<i class='fas fa-file-medical'></i></button>");
                            tabla.append("<button class='btn btn-sm btn-outline-custom me-2' onclick='abrirEditarConsulta(")
                                    .append(idCons).append(")'>Editar</button>");
                            tabla.append("<button class='btn btn-sm btn-danger' onclick='abrirAnularConsulta(")
                                    .append(idCons).append(")'>Anular</button>");
                        } else {
                            tabla.append("<span class='text-muted'>Sin acciones</span>");
                        }

                        tabla.append("</td></tr>");
                    }

                    tabla.append("</tbody></table>");

                    if (!hayResultados) {
                        out.print("<div class='alert alert-info'>No se encontraron consultas para: " + escape(texto) + "</div>");
                    } else {
                        out.print(tabla.toString());
                    }

                } catch (SQLException e) {
                    e.printStackTrace();
                    out.print("<div class='alert alert-danger'>Error al buscar: " + e.getMessage() + "</div>");
                }
                break;
            }

            default:
                out.print("<div class='alert alert-danger'>Acción no soportada</div>");
        }
    }

    private String buildFormularioEditar(Consulta c) {
        StringBuilder html = new StringBuilder();

        html.append("<form id='formConsulta'>");
        html.append("<input type='hidden' name='accion' value='editarConsulta'>");
        html.append("<input type='hidden' name='idConsulta' value='").append(c.getIdConsulta()).append("'>");
        html.append("<input type='hidden' id='idMascotaEditar' value='").append(c.getIdMascota()).append("'>");
        html.append("<input type='hidden' id='idConsultaGlobal' value='").append(c.getIdConsulta()).append("'>");

        // === PESTAÑAS ===
        html.append("<ul class='nav nav-tabs mb-3' role='tablist'>");
        html.append("<li class='nav-item'><a class='nav-link active' data-bs-toggle='tab' href='#tabDatosConsulta'>📋 Datos</a></li>");
        html.append("<li class='nav-item'><a class='nav-link' data-bs-toggle='tab' href='#tabDiagnosticos'>🩺 Diagnósticos</a></li>");
        html.append("<li class='nav-item'><a class='nav-link' data-bs-toggle='tab' href='#tabRecetas'>💊 Recetas</a></li>");
        html.append("<li class='nav-item'><a class='nav-link' data-bs-toggle='tab' href='#tabProcedimientos'>💉 Procedimientos</a></li>");
        html.append("<li class='nav-item'><a class='nav-link' data-bs-toggle='tab' href='#tabEstudios'>🔬 Estudios</a></li>");
        html.append("<li class='nav-item'><a class='nav-link' data-bs-toggle='tab' href='#tabAnalisis'>🧪 Análisis</a></li>");
        html.append("<li class='nav-item'><a class='nav-link' data-bs-toggle='tab' href='#tabTratamientos'>🩹 Tratamientos</a></li>");

        html.append("</ul>");

        html.append("<div class='tab-content'>");

        // === TAB 1: DATOS ===
        html.append("<div class='tab-pane fade show active' id='tabDatosConsulta'>");
        html.append("<div class='mb-3'><label class='form-label'>Motivo de la visita *</label>");
        html.append("<textarea name='motivo' class='form-control' rows='3' required>").append(escape(c.getMotivo())).append("</textarea></div>");

        html.append("<div class='row mb-3'>");
        html.append("<div class='col-md-4'><label class='form-label'>Peso (kg) *</label>");
        html.append("<input type='number' step='0.01' name='peso' class='form-control' value='").append(c.getPeso() == null ? "" : c.getPeso()).append("' required></div>");
        html.append("<div class='col-md-4'><label class='form-label'>Temperatura (°C)</label>");
        html.append("<input type='number' step='0.1' name='temperatura' class='form-control' value='").append(c.getTemperatura() == null ? "" : c.getTemperatura()).append("'></div>");
        html.append("<div class='col-md-4'><label class='form-label'>Frec. Cardíaca (lpm)</label>");
        html.append("<input type='number' name='frecuenciaCardiaca' class='form-control' value='").append(c.getFrecuenciaCardiaca() == null ? "" : c.getFrecuenciaCardiaca()).append("'></div>");
        html.append("</div>");

        html.append("<div class='row mb-3'>");
        html.append("<div class='col-md-4'><label class='form-label'>Frec. Respiratoria (rpm)</label>");
        html.append("<input type='number' name='frecuenciaRespiratoria' class='form-control' value='").append(c.getFrecuenciaRespiratoria() == null ? "" : c.getFrecuenciaRespiratoria()).append("'></div>");
        html.append("<div class='col-md-4'><label class='form-label'>Mucosas</label>");
        html.append("<input type='text' name='mucosas' class='form-control' value='").append(escape(c.getMucosas())).append("'></div>");
        html.append("<div class='col-md-4'><label class='form-label'>Cap. Reflejo</label>");
        html.append("<input type='text' name='capReflejo' class='form-control' value='").append(escape(c.getCapReflejo())).append("'></div>");
        html.append("</div>");

        html.append("<div class='mb-3'><label class='form-label'>Condición Corporal</label>");
        html.append("<input type='text' name='condicionCorporal' class='form-control' value='").append(escape(c.getCondicionCorporal())).append("'></div>");

        html.append("<div class='mb-3'><label class='form-label'>Observaciones</label>");
        html.append("<textarea name='observaciones' class='form-control' rows='3'>").append(escape(c.getObservaciones())).append("</textarea></div>");
        html.append("</div>"); // Fin tab datos

        // === TAB 2: DIAGNÓSTICOS ===
        html.append("<div class='tab-pane fade' id='tabDiagnosticos'>");
        html.append("<div class='card mb-3' style='border: 2px solid #10b981;'>");
        html.append("<div class='card-header' style='background: linear-gradient(135deg, #10b981 0%, #059669 100%); color: white;'>");
        html.append("<h6 class='mb-0'><i class='fas fa-plus-circle'></i> Registrar Diagnóstico</h6>");
        html.append("</div>");
        html.append("<div class='card-body'>");

        html.append("<div class='mb-3'>");
        html.append("<label class='form-label fw-bold'>Diagnóstico: <span class='text-danger'>*</span></label>");
        html.append("<textarea class='form-control' id='txtDiagnosticoNuevo' rows='3' ");
        html.append("placeholder='Ej: Gastroenteritis aguda, Dermatitis alérgica, etc.'></textarea>");
        html.append("</div>");

        html.append("<div class='row mb-3'>");
        html.append("<div class='col-md-6'>");
        html.append("<label class='form-label fw-bold'>Tipo: <span class='text-danger'>*</span></label>");
        html.append("<select class='form-select' id='selectTipoNuevo'>");
        html.append("<option value='TENTATIVO'>Tentativo</option>");
        html.append("<option value='DEFINITIVO'>Definitivo</option>");
        html.append("</select>");
        html.append("</div></div>");

        html.append("<div class='mb-3'>");
        html.append("<label class='form-label fw-bold'>Hallazgos:</label>");
        html.append("<textarea class='form-control' id='txtHallazgosNuevo' rows='2'></textarea>");
        html.append("</div>");

        html.append("<div class='mb-3'>");
        html.append("<label class='form-label fw-bold'>Síntomas:</label>");
        html.append("<textarea class='form-control' id='txtSintomasNuevo' rows='2'></textarea>");
        html.append("</div>");

        html.append("<div class='mb-3'>");
        html.append("<label class='form-label fw-bold'>Observaciones:</label>");
        html.append("<textarea class='form-control' id='txtObservacionesNuevo' rows='2'></textarea>");
        html.append("</div>");

        html.append("<div class='alert alert-info mt-3'>");
        html.append("<i class='fas fa-info-circle'></i> Los datos se guardarán al <strong>finalizar la consulta</strong>");
        html.append("</div>");

        html.append("<div id='mensajeDiagnostico' class='mt-3'></div>");

        html.append("</div></div>"); // Fin card
        html.append("</div>"); // Fin tab diagnósticos

        // --- TAB 3: RECETAS ---
        html.append("<div class='tab-pane fade' id='tabRecetas'>");
        html.append("<div class='card mb-3' style='border: 2px solid #8b5cf6;'>");
        html.append("<div class='card-header' style='background: linear-gradient(135deg, #8b5cf6 0%, #7c3aed 100%); color: white;'>");
        html.append("<h6 class='mb-0'><i class='fas fa-prescription'></i> Registrar Nueva Receta</h6>");
        html.append("</div>");
        html.append("<div class='card-body'>");

        html.append("<div class='mb-3'>");
        html.append("<label class='form-label fw-bold'>Medicamento: <span class='text-danger'>*</span></label>");
        html.append("<input type='text' class='form-control' id='txtMedicamentoNuevo' ");
        html.append("placeholder='Ej: Amoxicilina, Paracetamol, etc.'>");
        html.append("</div>");

        html.append("<div class='row mb-3'>");
        html.append("<div class='col-md-6'>");
        html.append("<label class='form-label fw-bold'>Dosis: <span class='text-danger'>*</span></label>");
        html.append("<input type='text' class='form-control' id='txtDosisNuevo' ");
        html.append("placeholder='Ej: 500 mg, 1 comprimido'>");
        html.append("</div>");
        html.append("<div class='col-md-6'>");
        html.append("<label class='form-label fw-bold'>Frecuencia: <span class='text-danger'>*</span></label>");
        html.append("<input type='text' class='form-control' id='txtFrecuenciaNuevo' ");
        html.append("placeholder='Ej: Cada 8 horas, 2 veces al día'>");
        html.append("</div>");
        html.append("</div>");

        html.append("<div class='mb-3'>");
        html.append("<label class='form-label fw-bold'>Duración:</label>");
        html.append("<input type='text' class='form-control' id='txtDuracionNuevo' ");
        html.append("placeholder='Ej: 7 días, 2 semanas'>");
        html.append("</div>");

        html.append("<div class='mb-3'>");
        html.append("<label class='form-label fw-bold'>Indicaciones:</label>");
        html.append("<textarea class='form-control' id='txtIndicacionesNuevo' rows='2' ");
        html.append("placeholder='Instrucciones generales...'></textarea>");
        html.append("</div>");

        html.append("<div class='mb-3'>");
        html.append("<label class='form-label fw-bold'>Observaciones:</label>");
        html.append("<textarea class='form-control' id='txtObservacionesRecetaNuevo' rows='2' ");
        html.append("placeholder='Observaciones adicionales...'></textarea>");
        html.append("</div>");

        html.append("<div class='alert alert-info mt-3'>");
        html.append("<i class='fas fa-info-circle'></i> Completa los datos. Se guardarán al hacer clic en <strong>\"Finalizar Consulta\"</strong>");
        html.append("</div>");

        html.append("</div></div>"); // Fin card
        html.append("</div>"); // Fin tab recetas

        // --- TAB 4: PROCEDIMIENTOS ---
        html.append("<div class='tab-pane fade' id='tabProcedimientos'>");
        html.append("<div class='card mb-3' style='border: 2px solid #f59e0b;'>");
        html.append("<div class='card-header' style='background: linear-gradient(135deg, #f59e0b 0%, #d97706 100%); color: white;'>");
        html.append("<h6 class='mb-0'><i class='fas fa-syringe'></i> Registrar Procedimiento</h6>");
        html.append("</div>");
        html.append("<div class='card-body'>");

        html.append("<div class='mb-3'>");
        html.append("<label class='form-label fw-bold'>Tipo de Procedimiento: <span class='text-danger'>*</span></label>");
        html.append("<select class='form-select' id='selectTipoProcedimientoNuevo'>");
        html.append("<option value=''>Seleccione un tipo</option>");
        html.append("<option value='Vacunación'>Vacunación</option>");
        html.append("<option value='Desparasitación'>Desparasitación</option>");
        html.append("<option value='Curación'>Curación</option>");
        html.append("<option value='Limpieza dental'>Limpieza dental</option>");
        html.append("<option value='Cirugía menor'>Cirugía menor</option>");
        html.append("<option value='Esterilización'>Esterilización</option>");
        html.append("<option value='Extracción'>Extracción</option>");
        html.append("<option value='Sutura'>Sutura</option>");
        html.append("<option value='Vendaje'>Vendaje</option>");
        html.append("<option value='Aplicación de medicamento inyectable'>Aplicación de medicamento inyectable</option>");
        html.append("<option value='Toma de muestras'>Toma de muestras</option>");
        html.append("<option value='Otro'>Otro</option>");
        html.append("</select>");
        html.append("</div>");

        html.append("<div class='mb-3'>");
        html.append("<label class='form-label fw-bold'>Descripción: <span class='text-danger'>*</span></label>");
        html.append("<textarea class='form-control' id='txtDescripcionProcedimientoNuevo' rows='3' ");
        html.append("placeholder='Ej: Aplicación de vacuna antirrábica, sutura de 5 puntos en extremidad anterior, etc.'></textarea>");
        html.append("</div>");

        html.append("<div class='mb-3'>");
        html.append("<label class='form-label fw-bold'>Observaciones:</label>");
        html.append("<textarea class='form-control' id='txtObservacionesProcedimientoNuevo' rows='2' ");
        html.append("placeholder='Observaciones adicionales del procedimiento...'></textarea>");
        html.append("</div>");

// SECCIÓN DE INSUMOS
        html.append("<hr>");
        html.append("<h6 class='mb-3'><i class='fas fa-box'></i> Insumos Utilizados</h6>");

        html.append("<div id='contenedorInsumos'>");
        html.append("<div class='alert alert-info'>");
        html.append("<i class='fas fa-info-circle'></i> Los insumos se cargarán al abrir esta pestaña");
        html.append("</div>");
        html.append("</div>");

        html.append("<button type='button' class='btn btn-sm btn-outline-primary mt-2' onclick='agregarInsumoTemporal()'>");
        html.append("<i class='fas fa-plus'></i> Agregar Insumo");
        html.append("</button>");

        html.append("<div class='alert alert-warning mt-3'>");
        html.append("<i class='fas fa-exclamation-triangle'></i> El stock de insumos se descontará al <strong>finalizar la consulta</strong>");
        html.append("</div>");

        html.append("<div class='alert alert-info mt-3'>");
        html.append("<i class='fas fa-info-circle'></i> Completa los datos. Se guardarán al hacer clic en <strong>\"Finalizar Consulta\"</strong>");
        html.append("</div>");

        html.append("</div></div>"); // Fin card
        html.append("</div>"); // Fin tab procedimientos

        // --- TAB 5: ESTUDIOS ---
        html.append("<div class='tab-pane fade' id='tabEstudios'>");
        html.append("<div class='card mb-3' style='border: 2px solid #3b82f6;'>");
        html.append("<div class='card-header' style='background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%); color: white;'>");
        html.append("<h6 class='mb-0'><i class='fas fa-microscope'></i> Registrar Orden de Estudio</h6>");
        html.append("</div>");
        html.append("<div class='card-body'>");

        html.append("<div class='mb-3'>");
        html.append("<label class='form-label fw-bold'>Tipo de Estudio: <span class='text-danger'>*</span></label>");
        html.append("<select class='form-select' id='selectTipoEstudioNuevo'>");
        html.append("<option value=''>Seleccione un tipo de estudio</option>");
        html.append("</select>");
        html.append("</div>");

        html.append("<div class='mb-3'>");
        html.append("<label class='form-label fw-bold'>Descripción: <span class='text-danger'>*</span></label>");
        html.append("<textarea class='form-control' id='txtDescripcionEstudioNuevo' rows='3' ");
        html.append("placeholder='Ej: Radiografía de tórax para descartar neumonía...'></textarea>");
        html.append("</div>");

        html.append("<div class='mb-3'>");
        html.append("<label class='form-label fw-bold'>Observaciones:</label>");
        html.append("<textarea class='form-control' id='txtObservacionesEstudioNuevo' rows='2' ");
        html.append("placeholder='Observaciones adicionales...'></textarea>");
        html.append("</div>");

        html.append("<div class='alert alert-info mt-3'>");
        html.append("<i class='fas fa-info-circle'></i> Los datos se guardarán al <strong>finalizar la consulta</strong>");
        html.append("</div>");

        html.append("</div></div>"); // Fin card
        html.append("</div>"); // Fin tab estudios

// --- TAB 6: ANÁLISIS ---
        html.append("<div class='tab-pane fade' id='tabAnalisis'>");
        html.append("<div class='card mb-3' style='border: 2px solid #ec4899;'>");
        html.append("<div class='card-header' style='background: linear-gradient(135deg, #ec4899 0%, #db2777 100%); color: white;'>");
        html.append("<h6 class='mb-0'><i class='fas fa-flask'></i> Registrar Orden de Análisis</h6>");
        html.append("</div>");
        html.append("<div class='card-body'>");

        html.append("<div class='mb-3'>");
        html.append("<label class='form-label fw-bold'>Tipo de Análisis: <span class='text-danger'>*</span></label>");
        html.append("<select class='form-select' id='selectTipoAnalisisNuevo'>");
        html.append("<option value=''>Seleccione un tipo de análisis</option>");
        html.append("</select>");
        html.append("</div>");

        html.append("<div class='mb-3'>");
        html.append("<label class='form-label fw-bold'>Descripción: <span class='text-danger'>*</span></label>");
        html.append("<textarea class='form-control' id='txtDescripcionAnalisisNuevo' rows='3' ");
        html.append("placeholder='Ej: Hemograma completo para evaluar anemia...'></textarea>");
        html.append("</div>");

        html.append("<div class='mb-3'>");
        html.append("<label class='form-label fw-bold'>Observaciones:</label>");
        html.append("<textarea class='form-control' id='txtObservacionesAnalisisNuevo' rows='2' ");
        html.append("placeholder='Observaciones adicionales...'></textarea>");
        html.append("</div>");

        html.append("<div class='alert alert-info mt-3'>");
        html.append("<i class='fas fa-info-circle'></i> Los datos se guardarán al <strong>finalizar la consulta</strong>");
        html.append("</div>");

        html.append("</div></div>"); // Fin card
        html.append("</div>"); // Fin tab análisis

        // --- TAB 7: TRATAMIENTOS ---
        html.append("<div class='tab-pane fade' id='tabTratamientos'>");
        html.append("<div class='card mb-3' style='border: 2px solid #14b8a6;'>");
        html.append("<div class='card-header' style='background: linear-gradient(135deg, #14b8a6 0%, #0d9488 100%); color: white;'>");
        html.append("<h6 class='mb-0'><i class='fas fa-notes-medical'></i> Registrar Tratamiento</h6>");
        html.append("</div>");
        html.append("<div class='card-body'>");

        html.append("<div class='row mb-3'>");
        html.append("<div class='col-md-4'>");
        html.append("<label class='form-label fw-bold'>Fecha Inicio:</label>");
        html.append("<input type='date' class='form-control' id='txtFechaInicioTratamientoNuevo'>");
        html.append("</div>");
        html.append("<div class='col-md-4'>");
        html.append("<label class='form-label fw-bold'>Fecha Fin Estimada:</label>");
        html.append("<input type='date' class='form-control' id='txtFechaFinTratamientoNuevo'>");
        html.append("</div>");
        html.append("<div class='col-md-4'>");
        html.append("<label class='form-label fw-bold'>Fecha Control:</label>");
        html.append("<input type='date' class='form-control' id='txtFechaControlTratamientoNuevo'>");
        html.append("</div>");
        html.append("</div>");

        html.append("<div class='mb-3'>");
        html.append("<label class='form-label fw-bold'>Plan Terapéutico: <span class='text-danger'>*</span></label>");
        html.append("<textarea class='form-control' id='txtPlanTerapeuticoNuevo' rows='4' ");
        html.append("placeholder='Describe el plan de tratamiento (medicamentos, terapias, cuidados especiales, etc.)'></textarea>");
        html.append("</div>");

        html.append("<div class='mb-3'>");
        html.append("<label class='form-label fw-bold'>Evolución Esperada:</label>");
        html.append("<textarea class='form-control' id='txtEvolucionTratamientoNuevo' rows='3' ");
        html.append("placeholder='Describe la evolución esperada del paciente (mejoras esperadas, signos de recuperación, etc.)'></textarea>");
        html.append("</div>");

        html.append("<div class='mb-3'>");
        html.append("<label class='form-label fw-bold'>Observaciones:</label>");
        html.append("<textarea class='form-control' id='txtObservacionesTratamientoNuevo' rows='2' ");
        html.append("placeholder='Observaciones adicionales, recomendaciones especiales, contraindicaciones, etc.'></textarea>");
        html.append("</div>");

        html.append("<div class='alert alert-info mt-3'>");
        html.append("<i class='fas fa-info-circle'></i> Los datos se guardarán al <strong>finalizar la consulta</strong>");
        html.append("</div>");

        html.append("</div></div>"); // Fin card
        html.append("</div>"); // Fin tab tratamientos

        html.append("</div>"); // Fin tab-content

        // === BOTONES ===
        html.append("<div class='mt-4 d-flex justify-content-end gap-2'>");
        html.append("<button type='button' class='btn btn-secondary' data-bs-dismiss='modal'>Cerrar sin Guardar</button>");
        html.append("<button type='button' class='btn btn-primary' onclick='guardarDatosConsulta()'>");
        html.append("<i class='fas fa-save'></i> Guardar Cambios</button>");
        html.append("<button type='button' class='btn btn-success' onclick='finalizarConsultaCompleta()'>");
        html.append("<i class='fas fa-check-circle'></i> Finalizar Consulta</button>");
        html.append("</div>");

        return html.toString();
    }

    private String buildFormularioAnular(int idConsulta) {
        return "<h5>Anular Consulta</h5>"
                + "<form id='formConsulta'>"
                + "<input type='hidden' name='accion' value='anularConsulta'>"
                + "<input type='hidden' name='idConsulta' value='" + idConsulta + "'>"
                + "<div class='mb-3'><label>Motivo de anulación *</label>"
                + "<textarea name='motivoAnulacion' class='form-control' required rows='3'></textarea>"
                + "<small class='text-muted'>Este motivo quedará registrado en el historial.</small></div>"
                + "<button type='button' class='btn btn-danger' onclick='enviarFormulario()'>Confirmar Anulación</button>"
                + "<button type='button' class='btn btn-secondary' data-bs-dismiss='modal'>Cancelar</button>"
                + "</form>";
    }

    // Agregar este método helper al final de la clase (antes del último })
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
        return s == null ? "" : s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;");
    }
}

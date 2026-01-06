package controlador;

import cx.AgendaDAO;
import cx.VeterinarioDAO;
import cx.TipoTurnoDAO;
import modelo.Agenda;
import modelo.Veterinario;
import modelo.TipoTurno;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet(name = "AgendaServlet", urlPatterns = {"/AgendaServlet"})
@MultipartConfig
public class AgendaServlet extends HttpServlet {

    private AgendaDAO agendaDAO = new AgendaDAO();
    private VeterinarioDAO veterinarioDAO = new VeterinarioDAO();
    private TipoTurnoDAO tipoTurnoDAO = new TipoTurnoDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // ✅ FORZAR UTF-8
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/plain;charset=UTF-8");

        String accion = request.getParameter("accion");

        // ✅ DEBUG
        System.out.println("🔥 ACCION RECIBIDA: [" + accion + "]");

        if (accion == null || accion.trim().isEmpty()) {
            response.getWriter().write("ERROR|Acción no especificada");
            return;
        }

        // ✅ LIMPIAR espacios
        accion = accion.trim();

        try {
            switch (accion) {
                case "crear":
                    crearAgenda(request, response);
                    break;
                case "actualizar":
                    actualizarAgenda(request, response);
                    break;
                case "anular":
                    anularAgenda(request, response);
                    break;
                default:
                    response.getWriter().write("ERROR|Acción inválida: " + accion);
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().write("ERROR|" + e.getMessage());
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        String accion = request.getParameter("accion");

        if (accion == null) {
            response.getWriter().write("<tr><td colspan='6'>Acción no especificada</td></tr>");
            return;
        }

        try {
            switch (accion) {
                case "listar":
                    listarAgendas(request, response);
                    break;
                case "porVeterinario":
                    agendaPorVeterinario(request, response);
                    break;
                case "obtener":
                    obtenerAgenda(request, response);
                    break;
                case "listarVeterinarios":
                    listarVeterinarios(request, response);
                    break;
                case "listarTiposTurno":
                    listarTiposTurno(request, response);
                    break;
                case "verificarFeriado":
                    verificarFeriado(request, response);
                    break;
                case "verificarConflicto":
                    verificarConflictoHorario(request, response);
                    break;
                default:
                    response.getWriter().write("<tr><td colspan='6'>Acción inválida</td></tr>");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().write("<tr><td colspan='6'>Error: " + e.getMessage() + "</td></tr>");
        }
    }

    // ===================== CREAR AGENDA =====================
    private void crearAgenda(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        try {
            System.out.println("🔥🔥🔥 ENTRANDO A crearAgenda() 🔥🔥🔥");

            // Obtener parámetros
            int idVeterinario = Integer.parseInt(request.getParameter("idVeterinario"));
            String fechaStr = request.getParameter("fecha");
            String horaInicioStr = request.getParameter("horaInicio");
            String horaFinStr = request.getParameter("horaFin");
            int idTipoTurno = Integer.parseInt(request.getParameter("idTipoTurno"));
            String observaciones = request.getParameter("observaciones");

            // Obtener usuario de la sesión
            HttpSession session = request.getSession(false);
            int creadoPor = (session != null && session.getAttribute("idUsuario") != null)
                    ? (int) session.getAttribute("idUsuario") : 1;

            System.out.println("🔥 Parámetros recibidos:");
            System.out.println("  idVeterinario: " + idVeterinario);
            System.out.println("  fecha: " + fechaStr);
            System.out.println("  horaInicio: " + horaInicioStr);
            System.out.println("  horaFin: " + horaFinStr);
            System.out.println("  idTipoTurno: " + idTipoTurno);
            System.out.println("  observaciones: " + observaciones);
            System.out.println("  creadoPor: " + creadoPor);

            // Convertir fechas y horas
            LocalDate fecha = LocalDate.parse(fechaStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            LocalTime horaInicio = LocalTime.parse(horaInicioStr);
            LocalTime horaFin = LocalTime.parse(horaFinStr);

            // Validar que hora inicio < hora fin
            if (horaInicio.compareTo(horaFin) >= 0) {
                System.out.println("❌ Error: Hora inicio >= Hora fin");
                response.getWriter().write("ERROR|La hora de inicio debe ser menor que la hora de fin");
                return;
            }

            // Crear objeto Agenda
            Agenda agenda = new Agenda(
                    idVeterinario,
                    fecha,
                    horaInicio,
                    horaFin,
                    idTipoTurno,
                    observaciones,
                    "ACTIVO",
                    creadoPor
            );

            System.out.println("🔥 Agenda creada en memoria");

            // Llamar al DAO
            String resultado = agendaDAO.crear(agenda);
            System.out.println("🔥 Resultado del DAO: " + resultado);

            response.getWriter().write(resultado);

        } catch (NumberFormatException e) {
            System.err.println("❌ NumberFormatException: " + e.getMessage());
            e.printStackTrace();
            response.getWriter().write("ERROR|Formato de número inválido: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("❌ Exception en crearAgenda: " + e.getMessage());
            e.printStackTrace();
            response.getWriter().write("ERROR|" + e.getMessage());
        }
    }

    // ===================== ACTUALIZAR AGENDA =====================
    // ===================== ACTUALIZAR AGENDA =====================
    private void actualizarAgenda(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        try {
            // Obtener parámetros
            int idAgenda = Integer.parseInt(request.getParameter("idAgenda"));
            int idVeterinario = Integer.parseInt(request.getParameter("idVeterinario"));
            String fechaStr = request.getParameter("fecha");
            String horaInicioStr = request.getParameter("horaInicio");
            String horaFinStr = request.getParameter("horaFin");
            int idTipoTurno = Integer.parseInt(request.getParameter("idTipoTurno"));
            String observaciones = request.getParameter("observaciones");

            // Obtener usuario de la sesión
            HttpSession session = request.getSession(false);
            int modificadoPor = (session != null && session.getAttribute("idUsuario") != null)
                    ? (int) session.getAttribute("idUsuario") : 1;

            // Convertir fechas y horas
            LocalDate fecha = LocalDate.parse(fechaStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            LocalTime horaInicio = LocalTime.parse(horaInicioStr);
            LocalTime horaFin = LocalTime.parse(horaFinStr);

            // Validar que hora inicio < hora fin
            if (horaInicio.compareTo(horaFin) >= 0) {
                response.getWriter().write("ERROR|La hora de inicio debe ser menor que la hora de fin");
                return;
            }

            // Crear objeto Agenda
            Agenda agenda = new Agenda();
            agenda.setIdAgenda(idAgenda);
            agenda.setIdVeterinario(idVeterinario);
            agenda.setFecha(fecha);
            agenda.setHoraInicio(horaInicio);
            agenda.setHoraFin(horaFin);
            agenda.setIdTipoTurno(idTipoTurno);
            agenda.setObservaciones(observaciones);

            // Llamar al DAO
            String resultado = agendaDAO.actualizar(agenda, modificadoPor);
            System.out.println("🔥 Resultado del DAO (actualizar): " + resultado);

            // ✅ SIEMPRE responder algo al cliente
            if (resultado == null || resultado.trim().isEmpty()) {
                response.getWriter().write("OK|Agenda actualizada exitosamente");
            } else {
                response.getWriter().write(resultado);
            }

        } catch (NumberFormatException e) {
            System.err.println("❌ NumberFormatException en actualizar: " + e.getMessage());
            e.printStackTrace();
            response.getWriter().write("ERROR|Formato de número inválido: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("❌ Exception en actualizarAgenda: " + e.getMessage());
            e.printStackTrace();
            response.getWriter().write("ERROR|" + e.getMessage());
        }
    }

    // ===================== ANULAR AGENDA =====================
    private void anularAgenda(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        try {
            int idAgenda = Integer.parseInt(request.getParameter("idAgenda"));
            String motivo = request.getParameter("motivo");

            // Obtener usuario de la sesión
            HttpSession session = request.getSession(false);
            int anuladoPor = (session != null && session.getAttribute("idUsuario") != null)
                    ? (int) session.getAttribute("idUsuario") : 1;

            String resultado = agendaDAO.anular(idAgenda, motivo, anuladoPor);
            response.getWriter().write(resultado);

        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().write("ERROR|" + e.getMessage());
        }
    }

    // ===================== LISTAR AGENDAS =====================
    private void listarAgendas(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        List<Agenda> agendas = agendaDAO.obtenerTodas();
        response.getWriter().write(buildAgendaHTML(agendas));
    }

    private void agendaPorVeterinario(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        int idVeterinario = Integer.parseInt(request.getParameter("idVeterinario"));
        LocalDate fecha = LocalDate.parse(request.getParameter("fecha"));

        List<Agenda> agendas = agendaDAO.obtenerPorVeterinarioYFecha(idVeterinario, fecha);
        response.getWriter().write(buildAgendaHTML(agendas));
    }

    // ===================== OBTENER UNA AGENDA =====================
    private void obtenerAgenda(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        int idAgenda = Integer.parseInt(request.getParameter("idAgenda"));
        Agenda agenda = agendaDAO.obtenerPorId(idAgenda);

        if (agenda == null) {
            response.getWriter().write("ERROR|No encontrada");
            return;
        }

        // Formato: OK|idAgenda;idVeterinario;fecha;horaInicio;horaFin;idTipoTurno;observaciones
        String respuesta = "OK|"
                + agenda.getIdAgenda() + ";"
                + agenda.getIdVeterinario() + ";"
                + agenda.getFecha() + ";"
                + agenda.getHoraInicio() + ";"
                + agenda.getHoraFin() + ";"
                + agenda.getIdTipoTurno() + ";"
                + (agenda.getObservaciones() == null ? "" : agenda.getObservaciones());

        response.getWriter().write(respuesta);
    }

    // ===================== LISTAR VETERINARIOS =====================
    private void listarVeterinarios(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        List<Veterinario> veterinarios = veterinarioDAO.obtenerTodos();
        StringBuilder html = new StringBuilder();

        for (Veterinario vet : veterinarios) {
            html.append("<option value='").append(vet.getIdVeterinario()).append("'>")
                    .append("Dr/a. ").append(vet.getNombre())
                    .append("</option>");
        }

        response.getWriter().write(html.toString());
    }

    // ===================== LISTAR TIPOS DE TURNO =====================
    private void listarTiposTurno(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        List<TipoTurno> tiposTurno = tipoTurnoDAO.obtenerTodosActivos();
        StringBuilder html = new StringBuilder();

        html.append("<option value=''>Seleccione tipo de turno</option>");
        for (TipoTurno tipo : tiposTurno) {
            html.append("<option value='").append(tipo.getIdTipoTurno()).append("'>")
                    .append(tipo.getNombre()).append(" (").append(tipo.getDuracionMinutos()).append(" min)")
                    .append("</option>");
        }

        response.getWriter().write(html.toString());
    }

    // ===================== VERIFICAR FERIADO =====================
    private void verificarFeriado(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        try {
            String fechaStr = request.getParameter("fecha");
            LocalDate fecha = LocalDate.parse(fechaStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));

            boolean esFeriado = agendaDAO.esFeriado(fecha);

            if (esFeriado) {
                response.getWriter().write("ERROR|No se puede registrar agenda en fechas feriadas");
            } else {
                response.getWriter().write("OK|Fecha disponible");
            }
        } catch (Exception e) {
            response.getWriter().write("ERROR|" + e.getMessage());
        }
    }

    // ===================== VERIFICAR CONFLICTO HORARIO =====================
    private void verificarConflictoHorario(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        try {
            int idVeterinario = Integer.parseInt(request.getParameter("idVeterinario"));
            String fechaStr = request.getParameter("fecha");
            String horaInicioStr = request.getParameter("horaInicio");
            String horaFinStr = request.getParameter("horaFin");
            String idAgendaStr = request.getParameter("idAgenda");

            LocalDate fecha = LocalDate.parse(fechaStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            LocalTime horaInicio = LocalTime.parse(horaInicioStr);
            LocalTime horaFin = LocalTime.parse(horaFinStr);

            Integer excluirIdAgenda = null;
            if (idAgendaStr != null && !idAgendaStr.isEmpty()) {
                excluirIdAgenda = Integer.parseInt(idAgendaStr);
            }

            boolean existeConflicto = agendaDAO.existeConflictoHorario(
                    idVeterinario, fecha, horaInicio, horaFin, excluirIdAgenda
            );

            if (existeConflicto) {
                response.getWriter().write("ERROR|Conflicto de horarios: ya existe agenda en ese rango");
            } else {
                response.getWriter().write("OK|Horario disponible");
            }
        } catch (Exception e) {
            response.getWriter().write("ERROR|" + e.getMessage());
        }
    }

    // ===================== CONSTRUIR HTML DE AGENDAS =====================
    // ===================== CONSTRUIR HTML DE AGENDAS =====================
    private String buildAgendaHTML(List<Agenda> agendas) {
        StringBuilder html = new StringBuilder();

        if (agendas.isEmpty()) {
            html.append("<tr><td colspan='6' class='text-center'>")
                    .append("<div class='empty-state'>")
                    .append("<i class='fas fa-calendar-times'></i>")
                    .append("<h4>No hay horarios registrados</h4>")
                    .append("</div></td></tr>");
            return html.toString();
        }

        for (Agenda agenda : agendas) {
            // Determinar clase CSS según tipo de turno
            String turnoClass = "badge-consulta";
            String nombreTipoTurno = agenda.getNombreTipoTurno();

            if (nombreTipoTurno != null) {
                if (nombreTipoTurno.toLowerCase().contains("cirug")) {
                    turnoClass = "badge-cirugia";
                } else if (nombreTipoTurno.toLowerCase().contains("urgen")) {
                    turnoClass = "badge-urgencia";
                } else if (nombreTipoTurno.toLowerCase().contains("vacun")) {
                    turnoClass = "badge-vacunacion";
                }
            }

            html.append("<tr>")
                    .append("<td>").append(agenda.getNombreVeterinario() != null ? agenda.getNombreVeterinario() : "N/A").append("</td>")
                    .append("<td>").append(agenda.getFecha()).append("</td>")
                    .append("<td>").append(agenda.getHoraInicio()).append(" - ").append(agenda.getHoraFin()).append("</td>")
                    .append("<td><span class='badge badge-turno ").append(turnoClass).append("'>")
                    .append(nombreTipoTurno != null ? nombreTipoTurno : "N/A").append("</span></td>")
                    .append("<td>").append(agenda.getObservaciones() != null ? agenda.getObservaciones() : "-").append("</td>")
                    .append("<td>")
                    .append("<button class='btn btn-info btn-tabla' onclick='editarAgenda(").append(agenda.getIdAgenda()).append(")' title='Editar'>")
                    .append("<i class='fas fa-edit'></i></button>")
                    .append("<button class='btn btn-danger btn-tabla ml-1' onclick='abrirAnular(").append(agenda.getIdAgenda()).append(")' title='Anular'>")
                    .append("<i class='fas fa-ban'></i></button>")
                    .append("</td>")
                    .append("</tr>");
        }
        return html.toString();
    }

}

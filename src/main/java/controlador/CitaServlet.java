package controlador;

import cx.AgendaDAO;
import cx.AgendaSlotDAO;
import cx.CitaDAO;
import cx.ClienteDAO;
import cx.EspecieDAO;
import cx.MascotaDAO;
import cx.RazaDAO;
import cx.TipoDocumentoDAO;
import cx.VeterinarioDAO;
import modelo.Agenda;
import modelo.Cita;
import modelo.Cliente;
import modelo.Mascota;
import modelo.Veterinario;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import modelo.AgendaSlot;
import modelo.Especie;
import modelo.Raza;
import modelo.TipoDocumento;

@WebServlet(name = "CitaServlet", urlPatterns = {"/CitaServlet"})
@MultipartConfig
public class CitaServlet extends HttpServlet {

    private CitaDAO citaDAO = new CitaDAO();
    private AgendaDAO agendaDAO = new AgendaDAO();
    private VeterinarioDAO veterinarioDAO = new VeterinarioDAO();
    private ClienteDAO clienteDAO = new ClienteDAO();
    private MascotaDAO mascotaDAO = new MascotaDAO();
    private AgendaSlotDAO agendaSlotDAO = new AgendaSlotDAO();
    private EspecieDAO especieDAO = new EspecieDAO();
    private RazaDAO razaDAO = new RazaDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/plain;charset=UTF-8");

        String accion = request.getParameter("accion");

        System.out.println("🔥 ACCION CITA RECIBIDA: [" + accion + "]");

        if (accion == null || accion.trim().isEmpty()) {
            response.getWriter().write("ERROR|Acción no especificada");
            return;
        }

        accion = accion.trim();

        try {
            switch (accion) {
                case "crear":
                    crearCita(request, response);
                    break;
                case "confirmar":
                    confirmarCita(request, response);
                    break;
                case "reprogramar":
                    reprogramarCita(request, response);
                    break;
                case "anular":
                    anularCita(request, response);
                    break;
                case "crearCliente":
                    crearCliente(request, response);
                    break;
                case "crearMascota":
                    crearMascota(request, response);
                    break;
                case "completar":
                    completarCita(request, response);
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
            response.getWriter().write("<tr><td colspan='9'>Acción no especificada</td></tr>");
            return;
        }

        try {
            switch (accion) {
                case "listar":
                    listarCitas(request, response);
                    break;
                case "obtener":
                    obtenerCita(request, response);
                    break;
                case "listarClientes":
                    listarClientes(request, response);
                    break;
                case "listarMascotas":
                    listarMascotasPorCliente(request, response);
                    break;
                case "listarAgendasDisponibles":
                    listarAgendasDisponibles(request, response);
                    break;
                case "listarVeterinarios":
                    listarVeterinarios(request, response);
                    break;
                case "verificarDisponibilidad":
                    verificarDisponibilidad(request, response);
                    break;
                case "estadisticas":
                    obtenerEstadisticas(request, response);
                    break;
                case "listarEspecies":
                    listarEspecies(response);
                    break;
                case "listarRazasPorEspecie":
                    listarRazasPorEspecie(request, response);
                    break;
                case "listarTiposDocumento":
                    listarTiposDocumento(response);
                    break;
                case "listarFiltrado":
                    listarCitasFiltradas(request, response);
                    break;

                default:
                    response.getWriter().write("<tr><td colspan='9'>Acción inválida</td></tr>");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().write("<tr><td colspan='9'>Error: " + e.getMessage() + "</td></tr>");
        }
    }

    // ===================== CREAR CITA =====================
    private void crearCita(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            System.out.println("🔥🔥🔥 ENTRANDO A crearCita() 🔥🔥🔥");

            int idSlot = Integer.parseInt(request.getParameter("idSlot"));
            int idCliente = Integer.parseInt(request.getParameter("idCliente"));
            int idMascota = Integer.parseInt(request.getParameter("idMascota"));
            String motivo = request.getParameter("motivo");
            String observaciones = request.getParameter("observaciones");

            HttpSession session = request.getSession(false);
            int creadoPor = (session != null && session.getAttribute("idUsuario") != null)
                    ? (int) session.getAttribute("idUsuario") : 2;

            System.out.println("🔥 Parámetros recibidos:");
            System.out.println("  idSlot: " + idSlot);
            System.out.println("  idCliente: " + idCliente);
            System.out.println("  idMascota: " + idMascota);
            System.out.println("  motivo: " + motivo);
            System.out.println("  observaciones: " + observaciones);
            System.out.println("  creadoPor: " + creadoPor);

            // ✅ Verificar que el SLOT exista y esté disponible
            if (!agendaSlotDAO.slotDisponible(idSlot)) {
                response.getWriter().write("ERROR|El horario seleccionado ya no está disponible");
                return;
            }

            // ✅ Obtener el slot para obtener el veterinario
            AgendaSlot slot = agendaSlotDAO.obtenerPorId(idSlot);
            if (slot == null) {
                response.getWriter().write("ERROR|Horario no encontrado");
                return;
            }

            Cita cita = new Cita();
            cita.setIdSlot(idSlot);
            cita.setIdCliente(idCliente);
            cita.setIdMascota(idMascota);
            cita.setIdVeterinario(slot.getIdVeterinario()); // ← del slot, no de agenda
            cita.setMotivo(motivo);
            cita.setObservaciones(observaciones);
            cita.setEstado("RESERVADA");
            cita.setCreadoPor(creadoPor);

            String resultado = citaDAO.crear(cita);
            System.out.println("🔥 Resultado del DAO: " + resultado);

            // ✅ Si la cita se creó, reservar el slot
            if (resultado.startsWith("OK")) {
                // Extraer el ID de la cita del resultado: "OK|mensaje|idCita"
                String[] partes = resultado.split("\\|");
                if (partes.length >= 3) {
                    int idCita = Integer.parseInt(partes[2]);
                    if (!agendaSlotDAO.reservarSlot(idSlot, idCita)) {
                        // Si falla reservar el slot, deberías anular la cita (opcional, por ahora solo advertencia)
                        System.out.println("⚠️ Advertencia: cita creada pero slot no reservado");
                    }
                }
            }

            response.getWriter().write(resultado);

        } catch (NumberFormatException e) {
            System.err.println("❌ NumberFormatException: " + e.getMessage());
            response.getWriter().write("ERROR|Formato de número inválido: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("❌ Exception en crearCita: " + e.getMessage());
            e.printStackTrace();
            response.getWriter().write("ERROR|" + e.getMessage());
        }
    }

    // ===================== CONFIRMAR CITA =====================
    private void confirmarCita(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        try {
            int idCita = Integer.parseInt(request.getParameter("idCita"));

            HttpSession session = request.getSession(false);
            int modificadoPor = (session != null && session.getAttribute("idUsuario") != null)
                    ? (int) session.getAttribute("idUsuario") : 2;

            String resultado = citaDAO.confirmar(idCita, modificadoPor);
            response.getWriter().write(resultado);

        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().write("ERROR|" + e.getMessage());
        }
    }

    // ===================== REPROGRAMAR CITA =====================
    private void reprogramarCita(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        try {
            int idCita = Integer.parseInt(request.getParameter("idCita"));
            int nuevoSlot = Integer.parseInt(request.getParameter("nuevoSlot"));
            String motivo = request.getParameter("motivo");
            String observaciones = request.getParameter("observaciones");

            HttpSession session = request.getSession(false);
            int modificadoPor = (session != null && session.getAttribute("idUsuario") != null)
                    ? (int) session.getAttribute("idUsuario") : 2;

            // ✅ Obtener el veterinario directamente del slot
            AgendaSlot slot = agendaSlotDAO.obtenerPorId(nuevoSlot);
            if (slot == null) {
                response.getWriter().write("ERROR|Horario no encontrado");
                return;
            }

            String resultado = citaDAO.reprogramar(idCita, nuevoSlot, slot.getIdVeterinario(), motivo, observaciones, modificadoPor);
            response.getWriter().write(resultado);

        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().write("ERROR|" + e.getMessage());
        }
    }

    // ===================== ANULAR CITA =====================
    private void anularCita(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        try {
            int idCita = Integer.parseInt(request.getParameter("idCita"));
            String motivo = request.getParameter("motivo");
            System.out.println("🔍 ANULANDO CITA ID: " + idCita + ", motivo: " + motivo); // ← AGREGA ESTO

            HttpSession session = request.getSession(false);
            int anuladoPor = (session != null && session.getAttribute("idUsuario") != null)
                    ? (int) session.getAttribute("idUsuario") : 2;

            String resultado = citaDAO.anular(idCita, motivo, anuladoPor);
            System.out.println("✅ RESULTADO ANULACIÓN: " + resultado); // ← Y ESTO

            response.getWriter().write(resultado);

        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().write("ERROR|" + e.getMessage());
        }
    }

    // ===================== COMPLETAR CITA =====================
    private void completarCita(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        try {
            int idCita = Integer.parseInt(request.getParameter("idCita"));
            String diagnostico = request.getParameter("diagnostico");
            String tratamiento = request.getParameter("tratamiento");

            HttpSession session = request.getSession(false);
            int completadoPor = (session != null && session.getAttribute("idUsuario") != null)
                    ? (int) session.getAttribute("idUsuario") : 2;

            String resultado = citaDAO.completar(idCita, diagnostico, tratamiento, completadoPor);
            response.getWriter().write(resultado);

        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().write("ERROR|" + e.getMessage());
        }
    }

    // ===================== LISTAR CITAS =====================
    private void listarCitas(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        try {
            List<Cita> citas;

            // Obtener parámetros (pueden ser null si no vienen)
            String idVeterinarioStr = request.getParameter("idVeterinario");
            String fechaStr = request.getParameter("fecha");
            String estado = request.getParameter("estado");

            // VERIFICACIÓN: Si NO hay filtros, usar método existente
            boolean hayFiltros = (idVeterinarioStr != null && !idVeterinarioStr.isEmpty())
                    || (fechaStr != null && !fechaStr.isEmpty())
                    || (estado != null && !estado.isEmpty());

            if (!hayFiltros) {
                // ✅ MANTENER CÓDIGO ORIGINAL (lo que ya funciona)
                citas = citaDAO.obtenerTodasConAgenda();
            } else {
                // ✅ NUEVA LÓGICA PARA FILTROS
                System.out.println("🔍 Aplicando filtros...");

                Integer idVeterinario = null;
                LocalDate fecha = null;

                if (idVeterinarioStr != null && !idVeterinarioStr.isEmpty()) {
                    try {
                        idVeterinario = Integer.parseInt(idVeterinarioStr);
                    } catch (NumberFormatException e) {
                        // Ignorar si no es número válido
                    }
                }

                if (fechaStr != null && !fechaStr.isEmpty()) {
                    try {
                        fecha = LocalDate.parse(fechaStr);
                    } catch (Exception e) {
                        // Ignorar si no es fecha válida
                    }
                }

                // Llamar al método filtrado (crearemos uno opcional)
                citas = filtrarCitasEnServlet(idVeterinario, fecha, estado);
            }

            // ✅ ESTO SE MANTIENE IGUAL
            response.getWriter().write(buildCitaHTML(citas));

        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().write("<tr><td colspan='9' class='text-center text-danger py-4'>"
                    + "<i class='fas fa-exclamation-triangle'></i> Error al cargar citas</td></tr>");
        }
    }

    // ===================== MÉTODO AUXILIAR PARA FILTRAR =====================
    private List<Cita> filtrarCitasEnServlet(Integer idVeterinario, LocalDate fecha, String estado) {
        try {
            // Primero intentar usar método específico si existe
            if (citaDAO.getClass().getMethod("filtrarCitas", Integer.class, LocalDate.class, String.class) != null) {
                return citaDAO.filtrarCitas(idVeterinario, fecha, estado);
            }
        } catch (NoSuchMethodException e) {
            // Si el método no existe, filtrar manualmente
            System.out.println("⚠️ Método filtrarCitas no encontrado, filtrando manualmente...");
        }

        // ✅ FALLBACK: Filtrar manualmente desde todas las citas (sin tocar DAO)
        List<Cita> todasCitas = citaDAO.obtenerTodasConAgenda();
        List<Cita> citasFiltradas = new ArrayList<>();

        for (Cita cita : todasCitas) {
            boolean cumpleFiltro = true;

            // Filtrar por veterinario
            if (idVeterinario != null) {
                if (cita.getIdVeterinario() != idVeterinario) {
                    cumpleFiltro = false;
                }
            }

            // Filtrar por fecha (necesitas parsear la fecha de la cita)
            if (fecha != null && cita.getFechaCita() != null) {
                try {
                    LocalDate fechaCita = LocalDate.parse(cita.getFechaCita());
                    if (!fechaCita.equals(fecha)) {
                        cumpleFiltro = false;
                    }
                } catch (Exception e) {
                    // Si no se puede parsear, no filtrar por fecha
                }
            }

            // Filtrar por estado
            if (estado != null && !estado.isEmpty()) {
                if (!estado.equals(cita.getEstado())) {
                    cumpleFiltro = false;
                }
            }

            if (cumpleFiltro) {
                citasFiltradas.add(cita);
            }
        }

        return citasFiltradas;
    }

    // ===================== OBTENER UNA CITA =====================
    private void obtenerCita(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        int idCita = Integer.parseInt(request.getParameter("idCita"));
        Cita cita = citaDAO.obtenerPorIdConDetalles(idCita);

        if (cita == null) {
            response.getWriter().write("ERROR|Cita no encontrada");
            return;
        }

        String respuesta = "OK|"
                + cita.getIdCita() + ";"
                + cita.getIdSlot() + ";" // ← CORREGIDO: era getIdAgenda()
                + cita.getIdCliente() + ";"
                + cita.getIdMascota() + ";"
                + cita.getIdVeterinario() + ";"
                + (cita.getMotivo() != null ? cita.getMotivo() : "") + ";"
                + (cita.getObservaciones() != null ? cita.getObservaciones() : "") + ";"
                + cita.getEstado();

        response.getWriter().write(respuesta);
    }

    // ===================== CREAR CLIENTE =====================
    private void crearCliente(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        try {
            String nombre = request.getParameter("nombre");
            String telefono = request.getParameter("telefono");
            String email = request.getParameter("email");
            String direccion = request.getParameter("direccion");
            String tipoDocumento = request.getParameter("tipo_documento"); // ID del tipo documento
            String nroDocumento = request.getParameter("nro_documento");

            Cliente cliente = new Cliente(0, nombre, telefono, email, direccion, tipoDocumento, nroDocumento);
            boolean resultado = clienteDAO.crear(cliente);

            if (resultado) {
                response.getWriter().write("OK|Cliente creado exitosamente");
            } else {
                response.getWriter().write("ERROR|Error al crear cliente");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().write("ERROR|" + e.getMessage());
        }
    }

    // ===================== CREAR MASCOTA =====================
    private void crearMascota(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        try {
            System.out.println("🔥🔥🔥 ENTRANDO A crearMascota() 🔥🔥🔥");

            int idCliente = Integer.parseInt(request.getParameter("idCliente"));
            int idRaza = Integer.parseInt(request.getParameter("idRaza")); // ← VERIFICA ESTE NOMBRE
            String nombre = request.getParameter("nombre");
            String sexo = request.getParameter("sexo");

            String edadStr = request.getParameter("edad");
            Integer edad = (edadStr != null && !edadStr.trim().isEmpty())
                    ? Integer.parseInt(edadStr)
                    : null;

            System.out.println("📋 Parámetros recibidos:");
            System.out.println("  idCliente: " + idCliente);
            System.out.println("  idRaza: " + idRaza);
            System.out.println("  nombre: " + nombre);
            System.out.println("  edad: " + edad);
            System.out.println("  sexo: " + sexo);

            // Verificar si idRaza es 0 o vacío
            if (idRaza == 0) {
                System.err.println("❌ ERROR: idRaza es 0 o no llegó correctamente");
                response.getWriter().write("ERROR|Debe seleccionar una raza válida");
                return;
            }

            Mascota mascota = new Mascota();
            mascota.setIdCliente(idCliente);
            mascota.setIdRaza(idRaza); // ← Asegúrate de usar setIdRaza
            mascota.setNombre(nombre);
            mascota.setEdad(edad);
            mascota.setSexo(sexo);

            boolean resultado = mascotaDAO.crear(mascota);

            System.out.println("✅ Resultado del DAO: " + resultado);

            response.getWriter().write(
                    resultado ? "OK|Mascota creada exitosamente"
                            : "ERROR|Error al crear mascota"
            );

        } catch (NumberFormatException e) {
            System.err.println("❌ NumberFormatException: " + e.getMessage());
            e.printStackTrace();
            response.getWriter().write("ERROR|Formato de número inválido: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("❌ Exception en crearMascota: " + e.getMessage());
            e.printStackTrace();
            response.getWriter().write("ERROR|" + e.getMessage());
        }
    }

    // ===================== LISTAR CLIENTES =====================
    private void listarClientes(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        List<Cliente> clientes = clienteDAO.listar();
        StringBuilder html = new StringBuilder();

        for (Cliente c : clientes) {
            String label = c.getNombre();
            if (c.getNro_documento() != null && !c.getNro_documento().trim().isEmpty()) {
                label += " - " + c.getNro_documento();
            }
            if (c.getTelefono() != null && !c.getTelefono().trim().isEmpty()) {
                label += " (" + c.getTelefono() + ")";
            }
            html.append("<option value='").append(c.getIdCliente()).append("'>")
                    .append(label)
                    .append("</option>");
        }

        response.getWriter().write(html.toString());
    }

    // ===================== LISTAR MASCOTAS POR CLIENTE =====================
    private void listarMascotasPorCliente(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        int idCliente = Integer.parseInt(request.getParameter("idCliente"));
        List<Mascota> mascotas = mascotaDAO.porCliente(idCliente);

        StringBuilder html = new StringBuilder();
        html.append("<option value=''>Seleccione mascota</option>");

        for (Mascota m : mascotas) {
            String label = m.getNombre();
            if (m.getEspecie() != null && !m.getEspecie().isEmpty()) {
                label += " - " + m.getEspecie();
            }
            if (m.getEdad() != null) {
                label += " (" + m.getEdad() + " años)";
            }

            html.append("<option value='").append(m.getIdMascota()).append("'>")
                    .append(label)
                    .append("</option>");
        }

        response.getWriter().write(html.toString());
    }

    // ===================== LISTAR AGENDAS DISPONIBLES =====================
    private void listarAgendasDisponibles(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        try {
            int idVeterinario = Integer.parseInt(request.getParameter("idVeterinario"));
            String fechaStr = request.getParameter("fecha");
            LocalDate fecha = LocalDate.parse(fechaStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));

            // ✅ Usamos AgendaSlotDAO para obtener los slots concretos
            List<AgendaSlot> slots = agendaSlotDAO.obtenerSlotsDisponiblesParaCita(idVeterinario, fecha);

            StringBuilder html = new StringBuilder();
            html.append("<option value=''>Seleccione horario disponible</option>");

            DateTimeFormatter formatterHora = DateTimeFormatter.ofPattern("HH:mm");

            for (AgendaSlot slot : slots) {
                String horaInicio = slot.getFechaHoraInicio().format(formatterHora);
                String horaFin = slot.getFechaHoraFin().format(formatterHora);
                String label = horaInicio + " - " + horaFin;

                html.append("<option value='").append(slot.getIdSlot()).append("'>")
                        .append(label)
                        .append("</option>");
            }

            response.getWriter().write(html.toString());

        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().write("<option value=''>Error al cargar horarios</option>");
        }
    }

    // ===================== LISTAR VETERINARIOS =====================
    private void listarVeterinarios(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        List<Veterinario> veterinarios = veterinarioDAO.obtenerTodos();
        StringBuilder html = new StringBuilder();

        html.append("<option value=''>Seleccione veterinario</option>");
        for (Veterinario vet : veterinarios) {
            html.append("<option value='").append(vet.getIdVeterinario()).append("'>")
                    .append("Dr/a. ").append(vet.getNombre())
                    .append("</option>");
        }

        response.getWriter().write(html.toString());
    }

    // ===================== VERIFICAR DISPONIBILIDAD =====================
    private void verificarDisponibilidad(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        try {
            int idSlot = Integer.parseInt(request.getParameter("idSlot")); // ← CORREGIDO

            Agenda agenda = agendaDAO.obtenerPorId(idSlot);
            if (agenda == null || !"ACTIVO".equals(agenda.getEstado())) {
                response.getWriter().write("ERROR|La agenda no está disponible");
                return;
            }

            int citasReservadas = citaDAO.contarCitasPorSlot(idSlot); // ← CORREGIDO

            // Suponiendo que cada slot permite 1 cita
            if (citasReservadas >= 1) {
                response.getWriter().write("ERROR|Este horario ya está ocupado");
            } else {
                response.getWriter().write("OK|Horario disponible");
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().write("ERROR|" + e.getMessage());
        }
    }

    // ===================== OBTENER ESTADÍSTICAS =====================
    private void obtenerEstadisticas(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        try {
            String fechaStr = request.getParameter("fecha");
            LocalDate fecha = fechaStr != null ? LocalDate.parse(fechaStr) : LocalDate.now();

            int totalCitas = citaDAO.contarCitasPorFecha(fecha);
            int citasConfirmadas = citaDAO.contarCitasPorFechaYEstado(fecha, "CONFIRMADA");
            int citasCompletadas = citaDAO.contarCitasPorFechaYEstado(fecha, "COMPLETADA");

            String respuesta = "OK|" + totalCitas + ";" + citasConfirmadas + ";" + citasCompletadas;
            response.getWriter().write(respuesta);

        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().write("ERROR|" + e.getMessage());
        }
    }

    // ===================== CONSTRUIR HTML DE CITAS =====================
    private String buildCitaHTML(List<Cita> citas) {
        StringBuilder html = new StringBuilder();

        if (citas.isEmpty()) {
            html.append("<tr><td colspan='9' class='text-center'>")
                    .append("<div class='empty-state'>")
                    .append("<i class='fas fa-calendar-times'></i>")
                    .append("<h4>No hay citas registradas</h4>")
                    .append("<p>Comience reservando una nueva cita</p>")
                    .append("</div></td></tr>");
            return html.toString();
        }

        for (Cita cita : citas) {
            String estadoClass = "";
            String estadoText = cita.getEstado();

            switch (cita.getEstado()) {
                case "RESERVADA":
                    estadoClass = "badge-pendiente";
                    break;
                case "CONFIRMADA":
                    estadoClass = "badge-confirmada";
                    break;
                case "COMPLETADA":
                    estadoClass = "badge-completada";
                    break;
                case "CANCELADA":
                    estadoClass = "badge-cancelada";
                    break;
                default:
                    estadoClass = "badge-pendiente";
            }

            html.append("<tr>")
                    //.append("<td>").append(cita.getIdCita()).append("</td>")
                    .append("<td>").append(cita.getNombreVeterinario() != null ? cita.getNombreVeterinario() : "N/A").append("</td>")
                    .append("<td>").append(cita.getNombreCliente() != null ? cita.getNombreCliente() : "N/A").append("</td>")
                    .append("<td>").append(cita.getNombreMascota() != null ? cita.getNombreMascota() : "N/A").append("</td>")
                    .append("<td>").append(cita.getFechaCita() != null ? cita.getFechaCita() : "N/A").append("</td>")
                    .append("<td>").append(cita.getHoraCita() != null ? cita.getHoraCita() : "N/A").append("</td>")
                    .append("<td>").append(cita.getMotivo() != null ? cita.getMotivo() : "-").append("</td>")
                    .append("<td><span class='badge badge-status ").append(estadoClass).append("'>")
                    .append(estadoText).append("</span></td>")
                    .append("<td>");

            if ("RESERVADA".equals(cita.getEstado())) {
                html.append("<button class='btn btn-success btn-tabla' onclick='confirmarCita(").append(cita.getIdCita()).append(")' title='Confirmar'>")
                        .append("<i class='fas fa-check'></i></button>");
            }

            if (!"COMPLETADA".equals(cita.getEstado()) && !"CANCELADA".equals(cita.getEstado())) {
                html.append("<button class='btn btn-warning btn-tabla' onclick='abrirReprogramar(").append(cita.getIdCita()).append(")' title='Reprogramar'>")
                        .append("<i class='fas fa-calendar-alt'></i></button>");
            }

            html.append("<button class='btn btn-danger btn-tabla' onclick='abrirAnularCita(").append(cita.getIdCita()).append(")' title='Anular'>")
                    .append("<i class='fas fa-ban'></i></button>")
                    .append("<button class='btn btn-info btn-tabla' onclick='verDetallesCita(").append(cita.getIdCita()).append(")' title='Detalles'>")
                    .append("<i class='fas fa-eye'></i></button>")
                    .append("</td>")
                    .append("</tr>");
        }
        return html.toString();
    }

    private void listarEspecies(HttpServletResponse response) throws IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {
            List<Especie> especies = especieDAO.obtenerTodas();
            for (Especie e : especies) {
                out.append("<option value='").append(String.valueOf(e.getIdEspecie())).append("'>")
                        .append(e.getNombre()).append("</option>");
            }
        } catch (Exception e) {
            e.printStackTrace();
            out.append("<option value=''>Error</option>");
        }
    }

    private void listarRazasPorEspecie(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {
            int idEspecie = Integer.parseInt(request.getParameter("idEspecie"));
            List<Raza> razas = razaDAO.obtenerPorEspecie(idEspecie);
            for (Raza r : razas) {
                out.append("<option value='").append(String.valueOf(r.getIdRaza())).append("'>")
                        .append(r.getNombre()).append("</option>");
            }
        } catch (Exception e) {
            e.printStackTrace();
            out.append("<option value=''>Error</option>");
        }
    }

    // ===================== NUEVO: LISTAR TIPOS DOCUMENTO =====================
    private void listarTiposDocumento(HttpServletResponse response) throws IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {
            TipoDocumentoDAO tipoDocDAO = new TipoDocumentoDAO();
            List<TipoDocumento> tipos = tipoDocDAO.obtenerTodos();

            out.append("<option value=''>Seleccione tipo documento</option>");
            for (TipoDocumento td : tipos) {
                out.append("<option value='")
                        .append(String.valueOf(td.getIdTipoDocumento()))
                        .append("'>")
                        .append(td.getNombre())
                        .append("</option>");
            }
        } catch (Exception e) {
            e.printStackTrace();
            out.append("<option value=''>Error al cargar tipos</option>");
        }
    }

    private void listarCitasFiltradas(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        try {
            System.out.println("🔍 INICIANDO FILTRADO DE CITAS");

            // Obtener parámetros
            String idVeterinarioStr = request.getParameter("idVeterinario");
            String fechaStr = request.getParameter("fecha");
            String estado = request.getParameter("estado");

            System.out.println("📋 Parámetros recibidos:");
            System.out.println("  Veterinario: " + idVeterinarioStr);
            System.out.println("  Fecha: " + fechaStr);
            System.out.println("  Estado: " + estado);

            // Obtener TODAS las citas (usando el método que ya funciona)
            List<Cita> todasCitas = citaDAO.obtenerTodasConAgenda();
            System.out.println("📊 Total de citas encontradas: " + todasCitas.size());

            // Filtrar manualmente
            List<Cita> citasFiltradas = new ArrayList<>();

            for (Cita cita : todasCitas) {
                boolean cumple = true;

                // Filtrar por veterinario
                if (idVeterinarioStr != null && !idVeterinarioStr.isEmpty()) {
                    try {
                        int idVetFiltro = Integer.parseInt(idVeterinarioStr);
                        if (cita.getIdVeterinario() != idVetFiltro) {
                            cumple = false;
                        }
                    } catch (NumberFormatException e) {
                        // Ignorar si no es número válido
                    }
                }

                // Filtrar por fecha
                if (fechaStr != null && !fechaStr.isEmpty() && cita.getFechaCita() != null) {
                    if (!fechaStr.equals(cita.getFechaCita())) {
                        cumple = false;
                    }
                }

                // Filtrar por estado
                if (estado != null && !estado.isEmpty() && cita.getEstado() != null) {
                    if (!estado.equals(cita.getEstado())) {
                        cumple = false;
                    }
                }

                if (cumple) {
                    citasFiltradas.add(cita);
                }
            }

            System.out.println("✅ Citas después de filtrar: " + citasFiltradas.size());

            // Usar el mismo método de construcción HTML
            response.getWriter().write(buildCitaHTML(citasFiltradas));

        } catch (Exception e) {
            System.err.println("❌ ERROR en listarCitasFiltradas: " + e.getMessage());
            e.printStackTrace();
            response.getWriter().write("<tr><td colspan='9' class='text-center text-danger py-4'>"
                    + "<i class='fas fa-exclamation-triangle'></i> Error al filtrar citas</td></tr>");
        }
    }
}

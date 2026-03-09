package controlador;

import cx.RecepcionistaDAO;
import modelo.Cita;
import modelo.RecepcionistaStats;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;

@WebServlet("/RecepcionistaServlet")
public class RecepcionistaServlet extends HttpServlet {

    private RecepcionistaDAO recepcionistaDAO;

    @Override
    public void init() throws ServletException {
        recepcionistaDAO = new RecepcionistaDAO();
        System.out.println("=== DEBUG RecepcionistaServlet.init() - DAO inicializado ===");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        System.out.println("\n=== DEBUG RecepcionistaServlet.doGet() INICIANDO ===");
        System.out.println("URL: " + request.getRequestURL());
        System.out.println("Query: " + request.getQueryString());

        /* =========================
           VALIDAR SESIÓN Y ROL
           ========================= */
        HttpSession session = request.getSession(false);
        System.out.println("Session ID: " + (session != null ? session.getId() : "null"));

        if (session == null) {
            System.out.println("DEBUG: Sesión nula, redirigiendo a login");
            response.sendRedirect("login.jsp");
            return;
        }

        String usuario = (String) session.getAttribute("usuario");
        Integer rol = (Integer) session.getAttribute("rol");

        System.out.println("Usuario en sesión: " + usuario);
        System.out.println("Rol en sesión: " + rol);

        if (usuario == null || rol == null) {
            System.out.println("DEBUG: Usuario o rol nulo, redirigiendo a login");
            response.sendRedirect("login.jsp");
            return;
        }

        if (rol != 2) {
            System.out.println("DEBUG: Rol no es 2 (es " + rol + "), redirigiendo a accesoDenegado");
            response.sendRedirect("accesoDenegado.jsp");
            return;
        }

        System.out.println("DEBUG: Usuario válido - " + usuario + ", Rol: " + rol);

        /* =========================
           DATOS DASHBOARD
           ========================= */
        System.out.println("\n=== DEBUG: Obteniendo datos del dashboard ===");

        try {
            // 1. Obtener estadísticas completas (NUEVO)
            System.out.println("1. Obteniendo estadísticas completas...");
            RecepcionistaStats stats = recepcionistaDAO.obtenerEstadisticasCompletas();

            // 2. Contador simple de citas de hoy (para compatibilidad)
            System.out.println("2. Contando citas de hoy...");
            int totalCitasHoy = recepcionistaDAO.contarCitasHoy();

            System.out.println("3. Contando recordatorios pendientes...");
            int totalRecordatorios = recepcionistaDAO.contarRecordatoriosPendientes();

            System.out.println("4. Contando clientes atendidos hoy...");
            int clientesAtendidos = recepcionistaDAO.contarClientesAtendidosHoy();

            // 5. Listando citas de hoy
            System.out.println("5. Listando citas de hoy...");
            List<Cita> citasHoy = recepcionistaDAO.listarCitasHoy();

            // 6. Listando próximas citas de la semana (NUEVO)
            System.out.println("6. Listando próximas citas de la semana...");
            List<Cita> proximasCitas = recepcionistaDAO.listarProximasCitasSemana();

            // 7. Listando citas por confirmar (NUEVO)
            System.out.println("7. Listando citas por confirmar...");
            List<Cita> citasPorConfirmar = recepcionistaDAO.listarCitasPorConfirmar();

            System.out.println("\n=== DEBUG DATOS OBTENIDOS ===");
            System.out.println("  - stats: " + stats);
            System.out.println("  - totalCitasHoy: " + totalCitasHoy);
            System.out.println("  - totalRecordatorios: " + totalRecordatorios);
            System.out.println("  - clientesAtendidos: " + clientesAtendidos);
            System.out.println("  - citasHoy.size(): " + citasHoy.size());
            System.out.println("  - proximasCitas.size(): " + proximasCitas.size());
            System.out.println("  - citasPorConfirmar.size(): " + citasPorConfirmar.size());

            /* =========================
               PASAR A LA VISTA
               ========================= */
            System.out.println("\n=== DEBUG: Pasando datos a la vista ===");

            // Estadísticas completas (NUEVO)
            request.setAttribute("stats", stats);

            // Datos individuales (para compatibilidad con código anterior)
            request.setAttribute("totalCitasHoy", totalCitasHoy);
            request.setAttribute("totalRecordatorios", totalRecordatorios);
            request.setAttribute("clientesAtendidos", clientesAtendidos);

            // Listas de citas
            request.setAttribute("citasHoy", citasHoy);
            request.setAttribute("proximasCitas", proximasCitas);
            request.setAttribute("citasPorConfirmar", citasPorConfirmar);

            System.out.println("Atributos seteados en request:");
            System.out.println("  stats: " + (request.getAttribute("stats") != null ? "NO NULL" : "NULL"));
            System.out.println("  totalCitasHoy: " + request.getAttribute("totalCitasHoy"));
            System.out.println("  totalRecordatorios: " + request.getAttribute("totalRecordatorios"));
            System.out.println("  clientesAtendidos: " + request.getAttribute("clientesAtendidos"));
            System.out.println("  citasHoy: " + (request.getAttribute("citasHoy") != null ? "NO NULL" : "NULL"));
            System.out.println("  proximasCitas: " + (request.getAttribute("proximasCitas") != null ? "NO NULL" : "NULL"));
            System.out.println("  citasPorConfirmar: " + (request.getAttribute("citasPorConfirmar") != null ? "NO NULL" : "NULL"));

        } catch (Exception e) {
            System.err.println("ERROR GRAVE en servlet: " + e.getMessage());
            e.printStackTrace();
            // Poner valores por defecto
            request.setAttribute("stats", new RecepcionistaStats());
            request.setAttribute("totalCitasHoy", 0);
            request.setAttribute("totalRecordatorios", 0);
            request.setAttribute("clientesAtendidos", 0);
            request.setAttribute("citasHoy", java.util.Collections.emptyList());
            request.setAttribute("proximasCitas", java.util.Collections.emptyList());
            request.setAttribute("citasPorConfirmar", java.util.Collections.emptyList());
        }

        System.out.println("\n=== DEBUG: Haciendo forward al JSP ===");

        /* =========================
           FORWARD AL DASHBOARD
           ========================= */
        try {
            request.getRequestDispatcher("dashboardRecepcionista.jsp").forward(request, response);
            System.out.println("=== DEBUG: Forward completado con éxito ===");
        } catch (Exception e) {
            System.err.println("ERROR en forward: " + e.getMessage());
            e.printStackTrace();
            response.sendRedirect("error.jsp");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}

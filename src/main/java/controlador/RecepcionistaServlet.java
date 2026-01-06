package controlador;

import cx.RecepcionistaDAO;
import modelo.Cita;

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
            System.out.println("1. Contando citas de hoy...");
            int totalCitasHoy = recepcionistaDAO.contarCitasHoy();
            
            System.out.println("2. Contando recordatorios pendientes...");
            int totalRecordatorios = recepcionistaDAO.contarRecordatoriosPendientes();
            
            System.out.println("3. Contando clientes atendidos hoy...");
            int clientesAtendidos = recepcionistaDAO.contarClientesAtendidosHoy();
            
            System.out.println("4. Listando citas de hoy...");
            List<Cita> citasHoy = recepcionistaDAO.listarCitasHoy();

            System.out.println("\n=== DEBUG DATOS OBTENIDOS ===");
            System.out.println("  - totalCitasHoy: " + totalCitasHoy);
            System.out.println("  - totalRecordatorios: " + totalRecordatorios);
            System.out.println("  - clientesAtendidos: " + clientesAtendidos);
            System.out.println("  - citasHoy.size(): " + citasHoy.size());

            /* =========================
               PASAR A LA VISTA
               ========================= */
            System.out.println("\n=== DEBUG: Pasando datos a la vista ===");
            request.setAttribute("totalCitasHoy", totalCitasHoy);
            request.setAttribute("totalRecordatorios", totalRecordatorios);
            request.setAttribute("clientesAtendidos", clientesAtendidos);
            request.setAttribute("citasHoy", citasHoy);
            
            System.out.println("Atributos seteados en request:");
            System.out.println("  totalCitasHoy: " + request.getAttribute("totalCitasHoy"));
            System.out.println("  totalRecordatorios: " + request.getAttribute("totalRecordatorios"));
            System.out.println("  clientesAtendidos: " + request.getAttribute("clientesAtendidos"));
            System.out.println("  citasHoy: " + (request.getAttribute("citasHoy") != null ? "NO NULL" : "NULL"));

        } catch (Exception e) {
            System.err.println("ERROR GRAVE en servlet: " + e.getMessage());
            e.printStackTrace();
            // Poner valores por defecto
            request.setAttribute("totalCitasHoy", 0);
            request.setAttribute("totalRecordatorios", 0);
            request.setAttribute("clientesAtendidos", 0);
            request.setAttribute("citasHoy", java.util.Collections.emptyList());
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
}
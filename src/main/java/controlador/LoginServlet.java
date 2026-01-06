package controlador;

import cx.UsuarioDAO;
import modelo.Usuario;
import java.io.*;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet(name = "LoginServlet", urlPatterns = {"/LoginServlet"})
public class LoginServlet extends HttpServlet {
    
    private static final int ROL_ADMIN = 1;
    private static final int ROL_RECEPCIONISTA = 2;
    private static final int ROL_VETERINARIO = 3;
    private static final int ROL_GERENTE = 4;
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("text/html;charset=UTF-8");
        
        String usuario = request.getParameter("usuario");
        String contrasena = request.getParameter("contrasena");
        
        if (usuario == null || usuario.isEmpty() || contrasena == null || contrasena.isEmpty()) {
            request.setAttribute("error", "Usuario y contraseña son requeridos");
            request.getRequestDispatcher("login.jsp").forward(request, response);
            return;
        }
        
        UsuarioDAO usuarioDAO = new UsuarioDAO();
        Usuario usuarioAutenticado = usuarioDAO.validarLogin(usuario, contrasena);
        
        if (usuarioAutenticado != null) {
            // ✅ Login exitoso - Crear sesión
            HttpSession sesion = request.getSession();
            sesion.setAttribute("usuario", usuarioAutenticado.getUsuario());
            sesion.setAttribute("idUsuario", usuarioAutenticado.getIdUsuario());
            sesion.setAttribute("nombre", usuarioAutenticado.getNombre());
            sesion.setAttribute("rol", usuarioAutenticado.getIdRol());
            
            // 🔥 DEBUG - ESTAS SON LAS LÍNEAS NUEVAS
            System.out.println("🔥 DATOS GUARDADOS EN SESIÓN:");
            System.out.println("   - idUsuario: " + usuarioAutenticado.getIdUsuario());
            System.out.println("   - nombre: " + usuarioAutenticado.getNombre());
            System.out.println("   - usuario: " + usuarioAutenticado.getUsuario());
            System.out.println("   - rol: " + usuarioAutenticado.getIdRol());
            
            sesion.setAttribute("loginTime", System.currentTimeMillis());
            sesion.setMaxInactiveInterval(30 * 60);
            
            System.out.println("✅ Login exitoso para: " + usuarioAutenticado.getNombre() + 
                             " (Rol: " + usuarioAutenticado.getIdRol() + ")");
            
            String dashboardURL = getDashboardByRole(usuarioAutenticado.getIdRol());
            response.sendRedirect(dashboardURL);
            
        } else {
            System.out.println("❌ Intento de login fallido para usuario: " + usuario);
            request.setAttribute("error", "Usuario o contraseña incorrectos");
            request.getRequestDispatcher("login.jsp").forward(request, response);
        }
    }
    
    private String getDashboardByRole(int idRol) {
        switch (idRol) {
            case ROL_ADMIN:
                return "dashboardAdmin.jsp";
            case ROL_RECEPCIONISTA:
                return "dashboardRecepcionista.jsp";
            case ROL_VETERINARIO:
                return "dashboardVeterinario.jsp";
            case ROL_GERENTE:
                return "dashboardGerente.jsp";
            default:
                System.err.println("⚠️ Rol no reconocido: " + idRol);
                return "dashboardRecepcionista.jsp";
        }
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendRedirect("login.jsp");
    }
}
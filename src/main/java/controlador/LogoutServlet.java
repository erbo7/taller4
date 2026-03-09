package controlador;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet(name = "LogoutServlet", urlPatterns = {"/LogoutServlet"})
public class LogoutServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Invalidar la sesión
        HttpSession session = request.getSession(false);
        if (session != null) {
            String usuario = (String) session.getAttribute("usuario");
            session.invalidate();
            System.out.println("🚪 Logout para usuario: " + usuario);
        }

        // Eliminar cookies de "Recuérdame"
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("usuarioRecordado".equals(cookie.getName())
                        || "tokenRecordado".equals(cookie.getName())) {
                    cookie.setMaxAge(0);
                    cookie.setPath(request.getContextPath() + "/");
                    response.addCookie(cookie);
                    System.out.println("🍪 Cookie eliminada: " + cookie.getName());
                }
            }
        }

        // Redirigir al login
        response.sendRedirect("login.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}

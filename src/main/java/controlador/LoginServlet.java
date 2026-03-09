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

    // Configuración de bloqueo
    private static final int MAX_INTENTOS = 5;
    private static final long TIEMPO_BLOQUEO_MS = 30000; // 30 segundos

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");

        String usuario = request.getParameter("usuario");
        String contrasena = request.getParameter("contrasena");
        String remember = request.getParameter("remember");

        // Validación básica
        if (usuario == null || usuario.isEmpty() || contrasena == null || contrasena.isEmpty()) {
            request.setAttribute("error", "Usuario y contraseña son requeridos");
            request.getRequestDispatcher("login.jsp").forward(request, response);
            return;
        }

        UsuarioDAO usuarioDAO = new UsuarioDAO();

        // ============================================
        // VERIFICAR SI EL USUARIO ESTÁ BLOQUEADO
        // ============================================
        if (usuarioDAO.existeUsuario(usuario)) {
            Long tiempoBloqueo = usuarioDAO.obtenerTiempoBloqueo(usuario);

            if (tiempoBloqueo != null) {
                long tiempoTranscurrido = System.currentTimeMillis() - tiempoBloqueo;

                if (tiempoTranscurrido < TIEMPO_BLOQUEO_MS) {
                    // Usuario AÚN está bloqueado
                    long segundosRestantes = (TIEMPO_BLOQUEO_MS - tiempoTranscurrido) / 1000;
                    System.out.println("⏱️ Usuario bloqueado: " + usuario + " - Tiempo restante: " + segundosRestantes + "s");

                    request.setAttribute("error", "Usuario bloqueado. Intenta de nuevo en " + segundosRestantes + " segundos.");
                    request.getRequestDispatcher("login.jsp").forward(request, response);
                    return;
                } else {
                    // El tiempo de bloqueo ya expiró - resetear
                    usuarioDAO.resetearBloqueo(usuario);
                    System.out.println("✅ Bloqueo expirado para: " + usuario);
                }
            }
        }

        // ============================================
        // INTENTAR LOGIN
        // ============================================
        Usuario usuarioAutenticado = usuarioDAO.validarLogin(usuario, contrasena);

        if (usuarioAutenticado != null) {
            // ✅ LOGIN EXITOSO

            // Resetear intentos fallidos y bloqueo
            usuarioDAO.resetearBloqueo(usuario);

            // Crear sesión
            HttpSession sesion = request.getSession();
            sesion.setAttribute("usuario", usuarioAutenticado.getUsuario());
            sesion.setAttribute("idUsuario", usuarioAutenticado.getIdUsuario());
            sesion.setAttribute("nombre", usuarioAutenticado.getNombre());
            sesion.setAttribute("rol", usuarioAutenticado.getIdRol());
            sesion.setAttribute("loginTime", System.currentTimeMillis());
            sesion.setMaxInactiveInterval(30 * 60);

            // 🔥 DEBUG - Logs originales
            System.out.println("🔥 DATOS GUARDADOS EN SESIÓN:");
            System.out.println("   - idUsuario: " + usuarioAutenticado.getIdUsuario());
            System.out.println("   - nombre: " + usuarioAutenticado.getNombre());
            System.out.println("   - usuario: " + usuarioAutenticado.getUsuario());
            System.out.println("   - rol: " + usuarioAutenticado.getIdRol());

            // ============================================
            // FUNCIONALIDAD "RECUÉRDAME"
            // ============================================
            if (remember != null) {
                // Cookie con el nombre de usuario (dura 7 días)
                Cookie cookieUsuario = new Cookie("usuarioRecordado", usuario);
                cookieUsuario.setMaxAge(7 * 24 * 60 * 60); // 7 días
                cookieUsuario.setPath(request.getContextPath() + "/");
                response.addCookie(cookieUsuario);

                // Cookie con token de seguridad
                String token = generarToken(usuario);
                Cookie cookieToken = new Cookie("tokenRecordado", token);
                cookieToken.setMaxAge(7 * 24 * 60 * 60); // 7 días
                cookieToken.setPath(request.getContextPath() + "/");
                response.addCookie(cookieToken);

                System.out.println("🍪 Cookies de 'Recuérdame' creadas para: " + usuario);
            }

            System.out.println("✅ Login exitoso para: " + usuarioAutenticado.getNombre()
                    + " (Rol: " + usuarioAutenticado.getIdRol() + ")");

            String dashboardURL = getDashboardByRole(usuarioAutenticado.getIdRol());
            response.sendRedirect(dashboardURL);

        } else {
            // ❌ LOGIN FALLIDO

            if (usuarioDAO.existeUsuario(usuario)) {
                // El usuario existe pero la contraseña es incorrecta
                usuarioDAO.incrementarIntentosFallidos(usuario);
                int intentos = usuarioDAO.obtenerIntentosFallidos(usuario);

                System.out.println("❌ Intento fallido #" + intentos + " para usuario: " + usuario);

                if (intentos >= MAX_INTENTOS) {
                    // BLOQUEAR al usuario
                    usuarioDAO.bloquearUsuario(usuario);
                    System.out.println("🔒 Usuario bloqueado: " + usuario);

                    request.setAttribute("error",
                            "Demasiados intentos fallidos. Usuario bloqueado por "
                            + (TIEMPO_BLOQUEO_MS / 1000) + " segundos.");
                } else {
                    // Mostrar intentos restantes
                    int intentosRestantes = MAX_INTENTOS - intentos;
                    request.setAttribute("error",
                            "Usuario o contraseña incorrectos. Intentos restantes: " + intentosRestantes);
                }
            } else {
                // El usuario no existe
                System.out.println("❌ Intento de login con usuario inexistente: " + usuario);
                request.setAttribute("error", "Usuario o contraseña incorrectos");
            }

            request.getRequestDispatcher("login.jsp").forward(request, response);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // ============================================
        // VERIFICAR SI HAY COOKIES DE "RECUÉRDAME"
        // ============================================
        Cookie[] cookies = request.getCookies();

        if (cookies != null) {
            String usuarioRecordado = null;
            String tokenRecordado = null;

            // Buscar las cookies
            for (Cookie cookie : cookies) {
                if ("usuarioRecordado".equals(cookie.getName())) {
                    usuarioRecordado = cookie.getValue();
                }
                if ("tokenRecordado".equals(cookie.getName())) {
                    tokenRecordado = cookie.getValue();
                }
            }

            // Si ambas cookies existen, hacer auto-login
            if (usuarioRecordado != null && tokenRecordado != null) {
                if (verificarToken(usuarioRecordado, tokenRecordado)) {
                    UsuarioDAO usuarioDAO = new UsuarioDAO();

                    // Obtener el usuario de la BD
                    Usuario usuario = usuarioDAO.obtenerPorUsuario(usuarioRecordado);

                    if (usuario != null && usuario.isActivo()) {
                        // Auto-login exitoso
                        HttpSession sesion = request.getSession();
                        sesion.setAttribute("usuario", usuario.getUsuario());
                        sesion.setAttribute("idUsuario", usuario.getIdUsuario());
                        sesion.setAttribute("nombre", usuario.getNombre());
                        sesion.setAttribute("rol", usuario.getIdRol());
                        sesion.setAttribute("loginTime", System.currentTimeMillis());
                        sesion.setMaxInactiveInterval(30 * 60);

                        System.out.println("🍪 Auto-login exitoso para: " + usuario.getNombre());

                        String dashboardURL = getDashboardByRole(usuario.getIdRol());
                        response.sendRedirect(dashboardURL);
                        return;
                    } else {
                        // Usuario inactivo o no existe - eliminar cookies
                        eliminarCookiesRecuerdame(request, response);
                    }
                }
            }
        }

        // Si no hay cookies válidas, mostrar página de login
        request.getRequestDispatcher("login.jsp").forward(request, response);
    }

    // ============================================
    // MÉTODOS AUXILIARES
    // ============================================
    /**
     * Genera un token simple para la cookie de "Recuérdame"
     */
    private String generarToken(String usuario) {
        // Token simple: usuario + timestamp
        // En producción deberías usar algo más seguro (JWT, UUID + BD, etc.)
        return usuario + "_" + System.currentTimeMillis();
    }

    /**
     * Verifica que el token sea válido
     */
    private boolean verificarToken(String usuario, String token) {
        // Verificación simple: el token debe empezar con el usuario
        // En producción deberías validar contra una base de datos o usar JWT
        return token != null && token.startsWith(usuario + "_");
    }

    /**
     * Elimina las cookies de "Recuérdame"
     */
    private void eliminarCookiesRecuerdame(HttpServletRequest request, HttpServletResponse response) {
        Cookie cookieUsuario = new Cookie("usuarioRecordado", "");
        cookieUsuario.setMaxAge(0);
        cookieUsuario.setPath(request.getContextPath() + "/");
        response.addCookie(cookieUsuario);

        Cookie cookieToken = new Cookie("tokenRecordado", "");
        cookieToken.setMaxAge(0);
        cookieToken.setPath(request.getContextPath() + "/");
        response.addCookie(cookieToken);
    }

    /**
     * Obtiene la URL del dashboard según el rol del usuario
     */
    private String getDashboardByRole(int idRol) {
        switch (idRol) {
            case ROL_ADMIN:
                return "dashboardAdmin.jsp";
            case ROL_RECEPCIONISTA:
                return "RecepcionistaServlet";
            case ROL_VETERINARIO:
                return "dashboardVeterinario.jsp";
            case ROL_GERENTE:
                return "dashboardGerente.jsp";
            default:
                System.err.println("⚠️ Rol no reconocido: " + idRol);
                return "dashboardRecepcionista.jsp";
        }
    }
}

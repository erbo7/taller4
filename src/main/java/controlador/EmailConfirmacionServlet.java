package controlador;

import cx.RecordatorioDAO;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/email-confirmar")
public class EmailConfirmacionServlet extends HttpServlet {

    private RecordatorioDAO dao;

    @Override
    public void init() throws ServletException {
        dao = new RecordatorioDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");

        try {
            String idRecordatorioStr = request.getParameter("id");
            String accion = request.getParameter("accion");

            if (idRecordatorioStr == null || accion == null) {
                mostrarError(response, "Parámetros inválidos");
                return;
            }

            int idRecordatorio = Integer.parseInt(idRecordatorioStr);
            String respuesta = "";
            String mensaje = "";
            String icono = "";
            String color = "";

            if ("confirmar".equals(accion)) {
                respuesta = "CONFIRMADA";
                mensaje = "¡Tu asistencia ha sido confirmada!";
                icono = "✅";
                color = "#28a745";
            } else if ("cancelar".equals(accion)) {
                respuesta = "CANCELADA";
                mensaje = "Tu cita ha sido cancelada";
                icono = "❌";
                color = "#dc3545";
            } else {
                mostrarError(response, "Acción no válida");
                return;
            }

            // Registrar la respuesta en la BD
            boolean exito = dao.registrarRespuestaCliente(idRecordatorio, respuesta);

            if (exito) {
                mostrarExito(response, mensaje, icono, color);
            } else {
                mostrarError(response, "No se pudo procesar tu respuesta. El recordatorio puede haber sido procesado previamente.");
            }

        } catch (Exception e) {
            System.err.println("Error en EmailConfirmacionServlet: " + e.getMessage());
            e.printStackTrace();
            mostrarError(response, "Error al procesar tu solicitud");
        }
    }

    private void mostrarExito(HttpServletResponse response, String mensaje, String icono, String color)
            throws IOException {

        response.getWriter().write(
                "<!DOCTYPE html>"
                + "<html lang='es'>"
                + "<head>"
                + "    <meta charset='UTF-8'>"
                + "    <meta name='viewport' content='width=device-width, initial-scale=1.0'>"
                + "    <title>DiazPet - Confirmación</title>"
                + "    <style>"
                + "        * { margin: 0; padding: 0; box-sizing: border-box; }"
                + "        body {"
                + "            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;"
                + "            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);"
                + "            min-height: 100vh;"
                + "            display: flex;"
                + "            justify-content: center;"
                + "            align-items: center;"
                + "            padding: 20px;"
                + "        }"
                + "        .card {"
                + "            background: white;"
                + "            border-radius: 20px;"
                + "            padding: 50px;"
                + "            text-align: center;"
                + "            box-shadow: 0 20px 60px rgba(0,0,0,0.3);"
                + "            max-width: 500px;"
                + "            width: 100%;"
                + "            animation: slideIn 0.5s ease;"
                + "        }"
                + "        @keyframes slideIn {"
                + "            from { opacity: 0; transform: translateY(30px); }"
                + "            to { opacity: 1; transform: translateY(0); }"
                + "        }"
                + "        .icon {"
                + "            font-size: 80px;"
                + "            margin-bottom: 20px;"
                + "            animation: bounce 1s ease;"
                + "        }"
                + "        @keyframes bounce {"
                + "            0%, 100% { transform: translateY(0); }"
                + "            50% { transform: translateY(-20px); }"
                + "        }"
                + "        h1 {"
                + "            color: " + color + ";"
                + "            font-size: 32px;"
                + "            margin-bottom: 15px;"
                + "        }"
                + "        p {"
                + "            color: #666;"
                + "            font-size: 18px;"
                + "            line-height: 1.6;"
                + "        }"
                + "        .logo {"
                + "            margin-top: 30px;"
                + "            color: #999;"
                + "            font-size: 14px;"
                + "        }"
                + "    </style>"
                + "</head>"
                + "<body>"
                + "    <div class='card'>"
                + "        <div class='icon'>" + icono + "</div>"
                + "        <h1>" + mensaje + "</h1>"
                + "        <p>Gracias por confirmar.</p>"
                + "        <p>Te esperamos en DiazPet.</p>"
                + "        <div class='logo'>"
                + "            <p>🐾 DiazPet - Cuidando a tu mejor amigo</p>"
                + "        </div>"
                + "    </div>"
                + "</body>"
                + "</html>"
        );
    }

    private void mostrarError(HttpServletResponse response, String mensaje)
            throws IOException {

        response.getWriter().write(
                "<!DOCTYPE html>"
                + "<html lang='es'>"
                + "<head>"
                + "    <meta charset='UTF-8'>"
                + "    <meta name='viewport' content='width=device-width, initial-scale=1.0'>"
                + "    <title>DiazPet - Error</title>"
                + "    <style>"
                + "        * { margin: 0; padding: 0; box-sizing: border-box; }"
                + "        body {"
                + "            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;"
                + "            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);"
                + "            min-height: 100vh;"
                + "            display: flex;"
                + "            justify-content: center;"
                + "            align-items: center;"
                + "            padding: 20px;"
                + "        }"
                + "        .card {"
                + "            background: white;"
                + "            border-radius: 20px;"
                + "            padding: 50px;"
                + "            text-align: center;"
                + "            box-shadow: 0 20px 60px rgba(0,0,0,0.3);"
                + "            max-width: 500px;"
                + "        }"
                + "        .icon { font-size: 80px; margin-bottom: 20px; }"
                + "        h1 { color: #dc3545; font-size: 32px; margin-bottom: 15px; }"
                + "        p { color: #666; font-size: 18px; }"
                + "    </style>"
                + "</head>"
                + "<body>"
                + "    <div class='card'>"
                + "        <div class='icon'>⚠️</div>"
                + "        <h1>Error</h1>"
                + "        <p>" + mensaje + "</p>"
                + "    </div>"
                + "</body>"
                + "</html>"
        );
    }
}

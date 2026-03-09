package controlador;

import cx.RecordatorioDAO;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/TwilioWebhook")
public class TwilioWebhookServlet extends HttpServlet {

    private RecordatorioDAO dao;

    @Override
    public void init() throws ServletException {
        dao = new RecordatorioDAO();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/xml;charset=UTF-8");

        try {
            // Obtener parámetros de Twilio
            String from = request.getParameter("From");
            String body = request.getParameter("Body");

            System.out.println("📩 Mensaje recibido de WhatsApp:");
            System.out.println("   From: " + from);
            System.out.println("   Body: " + body);

            // Limpiar el número
            if (from != null && from.startsWith("whatsapp:")) {
                from = from.substring(10);
            }

            String respuestaTexto = null;
            String mensajeRespuesta = "";

            if (body != null) {
                body = body.trim();

                if ("1".equals(body)) {
                    respuestaTexto = "CONFIRMADA";
                    mensajeRespuesta = "✅ ¡Gracias! Tu asistencia ha sido confirmada.";
                } else if ("2".equals(body)) {
                    respuestaTexto = "CANCELADA";
                    mensajeRespuesta = "❌ Tu cita ha sido cancelada. Puedes reagendar cuando desees.";
                } else {
                    mensajeRespuesta = "⚠️ Respuesta no válida. Por favor responde:\n1 - Confirmar\n2 - Cancelar";
                }

                if (respuestaTexto != null) {
                    boolean actualizado = dao.registrarRespuestaPorTelefono(from, respuestaTexto);

                    if (actualizado) {
                        System.out.println("✅ Respuesta registrada: " + respuestaTexto);
                    } else {
                        System.out.println("⚠️ No se encontró recordatorio para: " + from);
                        mensajeRespuesta = "⚠️ No encontramos un recordatorio pendiente.";
                    }
                }

                String twiml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                        + "<Response>"
                        + "<Message>" + mensajeRespuesta + "</Message>"
                        + "</Response>";

                response.getWriter().write(twiml);
            }

        } catch (Exception e) {
            System.err.println("❌ Error: " + e.getMessage());
            e.printStackTrace();

            String twiml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                    + "<Response>"
                    + "<Message>⚠️ Error al procesar tu respuesta.</Message>"
                    + "</Response>";

            response.getWriter().write(twiml);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.getWriter().write("Webhook activo ✅");
    }
}

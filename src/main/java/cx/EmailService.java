package cx;

import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;

public class EmailService {

    private static final String EMAIL_FROM = "toniestrella01@gmail.com";
    private static final String EMAIL_PASSWORD = "wxcgihokidjnbwem"; // ← SIN ESPACIOS

    public boolean enviarEmail(String destinatario, String asunto, String mensaje) {

        if (destinatario == null || destinatario.trim().isEmpty()) {
            System.err.println("❌ Destinatario vacío");
            return false;
        }

        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(EMAIL_FROM, EMAIL_PASSWORD);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(EMAIL_FROM));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinatario));
            message.setSubject(asunto);

            String contenidoHTML
                    = "<html>"
                    + "<head><style>"
                    + "body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }"
                    + ".container { max-width: 600px; margin: 0 auto; padding: 20px; background: #f9f9f9; border-radius: 10px; }"
                    + ".header { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; padding: 20px; text-align: center; border-radius: 10px 10px 0 0; }"
                    + ".content { background: white; padding: 30px; border-radius: 0 0 10px 10px; }"
                    + ".footer { text-align: center; margin-top: 20px; color: #999; font-size: 12px; }"
                    + "</style></head>"
                    + "<body>"
                    + "<div class='container'>"
                    + "<div class='header'>"
                    + "<h1>🐾 DiazPet - Recordatorio de Cita</h1>"
                    + "</div>"
                    + "<div class='content'>"
                    + "<p>" + mensaje.replace("\n", "<br>") + "</p>"
                    + "</div>"
                    + "<div class='footer'>"
                    + "<p>Este es un mensaje automático. Por favor no responder.</p>"
                    + "<p>DiazPet © 2025</p>"
                    + "</div>"
                    + "</div>"
                    + "</body>"
                    + "</html>";

            message.setContent(contenidoHTML, "text/html; charset=utf-8");

            Transport.send(message);

            System.out.println("✅ Email enviado correctamente a: " + destinatario);
            return true;

        } catch (MessagingException e) {
            System.err.println("❌ Error al enviar email: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public boolean enviarRecordatorio(String destinatario, String cliente,
            String mascota, String veterinario,
            String fecha, String hora, String motivo,
            int idRecordatorio) {  // ← AGREGAMOS ID

        String asunto = "🐾 Recordatorio de Cita - " + mascota;

        // Construir las URLs de confirmación
        String urlBase = "http://localhost:8080/proyectoVeterinaria/email-confirmar";
        String urlConfirmar = urlBase + "?id=" + idRecordatorio + "&accion=confirmar";
        String urlCancelar = urlBase + "?id=" + idRecordatorio + "&accion=cancelar";

        String contenidoHTML
                = "<html>"
                + "<head><style>"
                + "body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; margin: 0; padding: 0; }"
                + ".container { max-width: 600px; margin: 0 auto; background: #f9f9f9; }"
                + ".header { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; padding: 30px; text-align: center; }"
                + ".header h1 { margin: 0; font-size: 28px; }"
                + ".content { background: white; padding: 40px 30px; }"
                + ".info-box { background: #f8f9fa; border-left: 4px solid #667eea; padding: 20px; margin: 20px 0; border-radius: 5px; }"
                + ".info-row { margin: 10px 0; font-size: 16px; }"
                + ".info-row strong { color: #667eea; }"
                + ".buttons { text-align: center; margin: 30px 0; }"
                + ".btn { display: inline-block; padding: 15px 40px; margin: 10px; text-decoration: none; "
                + "       border-radius: 50px; font-weight: bold; font-size: 16px; transition: all 0.3s; }"
                + ".btn-confirmar { background: #28a745; color: white; }"
                + ".btn-confirmar:hover { background: #218838; transform: scale(1.05); }"
                + ".btn-cancelar { background: #dc3545; color: white; }"
                + ".btn-cancelar:hover { background: #c82333; transform: scale(1.05); }"
                + ".footer { text-align: center; padding: 20px; color: #999; font-size: 12px; }"
                + "</style></head>"
                + "<body>"
                + "<div class='container'>"
                + "  <div class='header'>"
                + "    <h1>🐾 DiazPet</h1>"
                + "    <p>Recordatorio de Cita</p>"
                + "  </div>"
                + "  <div class='content'>"
                + "    <p style='font-size: 18px;'>Hola <strong>" + cliente + "</strong>,</p>"
                + "    <p>Te recordamos que tienes una cita programada para <strong>" + mascota + "</strong>:</p>"
                + "    <div class='info-box'>"
                + "      <div class='info-row'><strong>📅 Fecha:</strong> " + fecha + "</div>"
                + "      <div class='info-row'><strong>🕐 Hora:</strong> " + hora + "</div>"
                + "      <div class='info-row'><strong>👨‍⚕️ Veterinario:</strong> Dr. " + veterinario + "</div>"
                + "      <div class='info-row'><strong>📋 Motivo:</strong> " + motivo + "</div>"
                + "    </div>"
                + "    <p style='text-align: center; font-size: 16px; margin: 30px 0 20px 0;'>"
                + "      <strong>Por favor confirma tu asistencia:</strong>"
                + "    </p>"
                + "    <div class='buttons'>"
                + "      <a href='" + urlConfirmar + "' class='btn btn-confirmar'>✅ CONFIRMAR ASISTENCIA</a>"
                + "      <br>"
                + "      <a href='" + urlCancelar + "' class='btn btn-cancelar'>❌ CANCELAR CITA</a>"
                + "    </div>"
                + "    <p style='text-align: center; color: #666; margin-top: 30px;'>¡Te esperamos! 💚</p>"
                + "  </div>"
                + "  <div class='footer'>"
                + "    <p>Este es un mensaje automático.</p>"
                + "    <p>🐾 DiazPet - Cuidando a tu mejor amigo © 2025</p>"
                + "  </div>"
                + "</div>"
                + "</body>"
                + "</html>";

        return enviarEmailHTML(destinatario, asunto, contenidoHTML);
    }

// Método auxiliar para enviar HTML directamente
    private boolean enviarEmailHTML(String destinatario, String asunto, String contenidoHTML) {

        if (destinatario == null || destinatario.trim().isEmpty()) {
            System.err.println("❌ Destinatario vacío");
            return false;
        }

        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(EMAIL_FROM, EMAIL_PASSWORD);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(EMAIL_FROM));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinatario));
            message.setSubject(asunto);
            message.setContent(contenidoHTML, "text/html; charset=utf-8");

            Transport.send(message);

            System.out.println("✅ Email enviado correctamente a: " + destinatario);
            return true;

        } catch (MessagingException e) {
            System.err.println("❌ Error al enviar email: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}

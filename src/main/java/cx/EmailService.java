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
            
            String contenidoHTML = 
                "<html>" +
                "<head><style>" +
                "body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }" +
                ".container { max-width: 600px; margin: 0 auto; padding: 20px; background: #f9f9f9; border-radius: 10px; }" +
                ".header { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; padding: 20px; text-align: center; border-radius: 10px 10px 0 0; }" +
                ".content { background: white; padding: 30px; border-radius: 0 0 10px 10px; }" +
                ".footer { text-align: center; margin-top: 20px; color: #999; font-size: 12px; }" +
                "</style></head>" +
                "<body>" +
                "<div class='container'>" +
                "<div class='header'>" +
                "<h1>🐾 DiazPet - Recordatorio de Cita</h1>" +
                "</div>" +
                "<div class='content'>" +
                "<p>" + mensaje.replace("\n", "<br>") + "</p>" +
                "</div>" +
                "<div class='footer'>" +
                "<p>Este es un mensaje automático. Por favor no responder.</p>" +
                "<p>DiazPet © 2025</p>" +
                "</div>" +
                "</div>" +
                "</body>" +
                "</html>";
            
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
                                     String fecha, String hora, String motivo) {
        
        String asunto = "🐾 Recordatorio de Cita - " + mascota;
        
        String mensaje = 
            "Hola " + cliente + ",\n\n" +
            "Este es un recordatorio de la cita programada:\n\n" +
            "📅 Fecha: " + fecha + "\n" +
            "🕐 Hora: " + hora + "\n" +
            "🐕 Mascota: " + mascota + "\n" +
            "👨‍⚕️ Veterinario: " + veterinario + "\n" +
            "📋 Motivo: " + motivo + "\n\n" +
            "Por favor confirmar su asistencia respondiendo el correo.\n\n" +
            "Gracias,\n" +
            "Equipo DiazPet";
        
        return enviarEmail(destinatario, asunto, mensaje);
    }
}
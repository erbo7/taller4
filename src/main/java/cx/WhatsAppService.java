package cx;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

public class WhatsAppService {

    // ======================================================
    // ⚠️ PON TUS CREDENCIALES REALES AQUÍ ⚠️
    // ======================================================
    private static final String ACCOUNT_SID = "ACxxxxxxxxxxxxxxxx";  // ← Reemplaza esto
    private static final String AUTH_TOKEN = "ACxxxxxxxxxxxxxxxx";    // ← Reemplaza esto
    private static final String WHATSAPP_NUMBER = "ACxxxxxxxxxxxxxxxx";     // ← Número del Sandbox
    // ======================================================

    private static boolean initialized = false;

    private static void initialize() {
        if (initialized) {
            return;
        }

        try {
            // Validar que las credenciales no sean las de ejemplo
            if (ACCOUNT_SID.equals("TU_ACCOUNT_SID_AQUI")
                    || AUTH_TOKEN.equals("TU_AUTH_TOKEN_AQUI")) {
                System.err.println("❌ ERROR: Debes reemplazar las credenciales en WhatsAppService.java");
                System.err.println("   Líneas 10-12: Pon tu Account SID y Auth Token reales");
                return;
            }

            Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
            initialized = true;
            System.out.println("✅ WhatsApp Service inicializado correctamente");
            System.out.println("   Account SID: " + ACCOUNT_SID.substring(0, 10) + "...");

        } catch (Exception e) {
            System.err.println("❌ Error al inicializar WhatsApp Service: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static boolean enviarRecordatorio(
            String telefonoDestino,
            String nombreCliente,
            String nombreMascota,
            String nombreVeterinario,
            String fechaCita,
            String horaCita,
            String motivo) {

        initialize();

        if (!initialized) {
            System.err.println("❌ WhatsApp Service no inicializado");
            System.err.println("   Verifica que pusiste tus credenciales en WhatsAppService.java líneas 10-12");
            return false;
        }

        try {
            String numeroFormateado = formatearNumero(telefonoDestino);
            if (numeroFormateado == null) {
                System.err.println("❌ Número de teléfono inválido: " + telefonoDestino);
                return false;
            }

            String mensaje = construirMensaje(
                    nombreCliente,
                    nombreMascota,
                    nombreVeterinario,
                    fechaCita,
                    horaCita,
                    motivo
            );

            System.out.println("📤 Enviando WhatsApp:");
            System.out.println("   De: " + WHATSAPP_NUMBER);
            System.out.println("   Para: " + numeroFormateado);
            System.out.println("   Mensaje: " + mensaje.substring(0, Math.min(50, mensaje.length())) + "...");

            Message message = Message.creator(
                    new PhoneNumber("whatsapp:" + numeroFormateado),
                    new PhoneNumber("whatsapp:" + WHATSAPP_NUMBER),
                    mensaje
            ).create();

            System.out.println("✅ WhatsApp enviado exitosamente!");
            System.out.println("   SID: " + message.getSid());
            System.out.println("   Estado: " + message.getStatus());

            return true;

        } catch (Exception e) {
            System.err.println("❌ Error al enviar WhatsApp: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private static String construirMensaje(
            String nombreCliente,
            String nombreMascota,
            String nombreVeterinario,
            String fechaCita,
            String horaCita,
            String motivo) {

        StringBuilder msg = new StringBuilder();
        msg.append("🐾 *DiazPet - Recordatorio de Cita* 🐾\n\n");
        msg.append("Hola *").append(nombreCliente).append("*,\n\n");
        msg.append("Te recordamos que ").append(nombreMascota);
        msg.append(" tiene una cita programada:\n\n");
        msg.append("📅 *Fecha:* ").append(fechaCita).append("\n");
        msg.append("🕐 *Hora:* ").append(horaCita).append("\n");
        msg.append("👨‍⚕️ *Veterinario:* Dr. ").append(nombreVeterinario).append("\n");

        if (motivo != null && !motivo.trim().isEmpty()) {
            msg.append("📋 *Motivo:* ").append(motivo).append("\n");
        }

        msg.append("\n━━━━━━━━━━━━━━━━━━━━\n");
        msg.append("⚠️ *Por favor confirma tu asistencia:*\n\n");
        msg.append("Responde con:\n");
        msg.append("✅ *1* - Confirmar asistencia\n");
        msg.append("❌ *2* - Cancelar cita\n");
        msg.append("━━━━━━━━━━━━━━━━━━━━\n");
        msg.append("\n💚 ¡Te esperamos!\n");
        msg.append("_DiazPet - Cuidando a tu mejor amigo_");

        return msg.toString();
    }

    private static String formatearNumero(String numero) {
        if (numero == null || numero.trim().isEmpty()) {
            return null;
        }

        numero = numero.replaceAll("[^0-9+]", "");

        if (numero.startsWith("+")) {
            return numero;
        }

        if (numero.startsWith("0")) {
            numero = numero.substring(1);
        }

        if (!numero.startsWith("595")) {
            numero = "+595" + numero;
        } else {
            numero = "+" + numero;
        }

        if (numero.length() >= 12 && numero.length() <= 15) {
            return numero;
        }

        return null;
    }
}

package controlador;

import cx.DocumentoDAO;
import cx.TipoDocumentoVeterinarioDAO;
import cx.ClienteDAO;
import cx.MascotaDAO;
import modelo.Documento;
import modelo.TipoDocumentoVeterinario;
import modelo.Cliente;
import modelo.Mascota;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.servlet.*;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

@WebServlet(name = "DocumentoServlet", urlPatterns = {"/DocumentoServlet"})
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024 * 2,
        maxFileSize = 1024 * 1024 * 20,
        maxRequestSize = 1024 * 1024 * 50
)
public class DocumentoServlet extends HttpServlet {

    private DocumentoDAO documentoDAO = new DocumentoDAO();
    private TipoDocumentoVeterinarioDAO tipoDocDAO = new TipoDocumentoVeterinarioDAO();
    private ClienteDAO clienteDAO = new ClienteDAO();
    private MascotaDAO mascotaDAO = new MascotaDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String accion = request.getParameter("accion");
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        if (accion == null) {
            out.print("ERROR: Sin acción");
            return;
        }

        try {
            switch (accion) {
                case "listarClientes":
                    listarClientes(out);
                    break;
                case "listarMascotasPorCliente":
                    listarMascotasPorCliente(request, out);
                    break;
                case "listarTiposDocumento":
                    listarTiposDocumento(out);
                    break;
                case "listarDocumentosPorMascota":
                    listarDocumentosPorMascota(request, out);
                    break;
                case "obtenerDocumento":
                    obtenerDocumento(request, out);
                    break;
                case "descargarDocumento":
                    descargarDocumento(request, response);
                    break;
                default:
                    out.print("ERROR: Acción no válida");
            }
        } catch (Exception e) {
            e.printStackTrace();
            out.print("ERROR: " + e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        String accion = request.getParameter("accion");

        if (accion == null) {
            out.print("ERROR: Sin acción");
            return;
        }

        try {
            switch (accion) {
                case "registrar":
                    registrarDocumento(request, out);
                    break;
                case "actualizar":
                    actualizarDocumento(request, out);
                    break;
                case "eliminar":
                    eliminarDocumento(request, out);
                    break;
                default:
                    out.print("ERROR: Acción no válida");
            }
        } catch (Exception e) {
            e.printStackTrace();
            out.print("ERROR: " + e.getMessage());
        }
    }

    // ===================== MÉTODOS GET =====================
    private void listarClientes(PrintWriter out) {
        try {
            List<Cliente> clientes = clienteDAO.obtenerTodos();
            out.print("<option value=''>Seleccione cliente</option>");
            for (Cliente c : clientes) {
                out.print("<option value='" + c.getIdCliente() + "'>"
                        + (c.getNombre() != null ? c.getNombre() : "") + "</option>");
            }
        } catch (Exception e) {
            out.print("<option value=''>Error al cargar clientes</option>");
        }
    }

    private void listarMascotasPorCliente(HttpServletRequest request, PrintWriter out) {
        try {
            String idStr = request.getParameter("idCliente");

            if (idStr == null || idStr.trim().isEmpty()) {
                out.print("<option value=''>Seleccione cliente primero</option>");
                return;
            }

            int idCliente = Integer.parseInt(idStr.trim());
            List<Mascota> mascotas = mascotaDAO.porCliente(idCliente);

            if (mascotas == null || mascotas.isEmpty()) {
                out.print("<option value=''>No hay mascotas registradas</option>");
                return;
            }

            out.print("<option value=''>Seleccione mascota</option>");
            for (Mascota m : mascotas) {
                String nombre = m.getNombre() != null ? m.getNombre() : "";
                String edad = m.getEdad() != null ? " (" + m.getEdad() + " años)" : "";
                out.print("<option value='" + m.getIdMascota() + "'>" + nombre + edad + "</option>");
            }
        } catch (NumberFormatException e) {
            out.print("<option value=''>ID cliente inválido</option>");
        } catch (Exception e) {
            e.printStackTrace();
            out.print("<option value=''>Error al cargar mascotas</option>");
        }
    }

    private void listarTiposDocumento(PrintWriter out) {
        try {
            List<TipoDocumentoVeterinario> tipos = tipoDocDAO.obtenerTodos();
            out.print("<option value=''>Seleccione tipo</option>");
            for (TipoDocumentoVeterinario t : tipos) {
                String obligatorio = t.isEsObligatorio() ? " (Obligatorio)" : "";
                out.print("<option value='" + t.getIdTipoDocVet() + "'>"
                        + (t.getNombre() != null ? t.getNombre() : "") + obligatorio + "</option>");
            }
        } catch (Exception e) {
            out.print("<option value=''>Error al cargar tipos</option>");
        }
    }

    private void listarDocumentosPorMascota(HttpServletRequest request, PrintWriter out) {
        try {
            String idStr = request.getParameter("idMascota");
            if (idStr == null || idStr.trim().isEmpty()) {
                out.print("<tr><td colspan='6' class='text-center'>Error: ID mascota requerido</td></tr>");
                return;
            }

            int idMascota = Integer.parseInt(idStr.trim());
            List<Documento> documentos = documentoDAO.obtenerPorMascota(idMascota);

            if (documentos == null || documentos.isEmpty()) {
                out.print("<tr><td colspan='6' class='text-center'>No hay documentos registrados</td></tr>");
                return;
            }

            for (Documento doc : documentos) {
                String formatoBadge = "badge-" + doc.getFormato().toLowerCase();
                String obligatorio = doc.isEsObligatorio()
                        ? "<span class='badge badge-obligatorio ms-1'><i class='fas fa-lock'></i> Obligatorio</span>" : "";

                String fechaFormateada = formatFecha(doc.getFechaDocumento().toString());
                String observaciones = doc.getObservaciones();
                String obsHtml = (observaciones != null && !observaciones.trim().isEmpty())
                        ? escapeHtml(observaciones) : "<em class='text-muted'>Sin observaciones</em>";

                out.print("<tr>");
                out.print("<td>" + escapeHtml(doc.getNombreTipoDocumento()) + " " + obligatorio + "</td>");
                out.print("<td>" + fechaFormateada + "</td>");
                out.print("<td>");
                out.print("<span class='badge " + formatoBadge + "'>" + doc.getFormato() + "</span><br>");
                out.print("<small class='text-muted'>" + escapeHtml(doc.getNombreArchivo()) + "</small>");
                out.print("</td>");
                out.print("<td>" + doc.getTamanoKb() + " KB</td>");
                out.print("<td><small>" + obsHtml + "</small></td>");
                out.print("<td>");
                out.print("<button class='btn btn-sm btn-info btn-tabla' onclick='descargarDocumento(" + doc.getIdDocumento() + ")' title='Descargar'>");
                out.print("<i class='fas fa-download'></i></button> ");
                out.print("<button class='btn btn-sm btn-warning btn-tabla' onclick='abrirModalEditar(" + doc.getIdDocumento() + ")' title='Editar'>");
                out.print("<i class='fas fa-edit'></i></button> ");
                out.print("<button class='btn btn-sm btn-danger btn-tabla' onclick='abrirModalEliminar(" + doc.getIdDocumento() + "," + doc.isEsObligatorio() + ")' title='Eliminar'>");
                out.print("<i class='fas fa-trash'></i></button>");
                out.print("</td>");
                out.print("</tr>");
            }
        } catch (NumberFormatException e) {
            out.print("<tr><td colspan='6' class='text-center text-danger'>Error: ID mascota inválido</td></tr>");
        } catch (Exception e) {
            e.printStackTrace();
            out.print("<tr><td colspan='6' class='text-center text-danger'>Error al cargar documentos</td></tr>");
        }
    }

    private void obtenerDocumento(HttpServletRequest request, PrintWriter out) {
        try {
            String idStr = request.getParameter("idDocumento");
            if (idStr == null || idStr.trim().isEmpty()) {
                out.print("ERROR: ID documento requerido");
                return;
            }

            int idDocumento = Integer.parseInt(idStr.trim());
            Documento doc = documentoDAO.obtenerPorId(idDocumento);

            if (doc == null) {
                out.print("ERROR: Documento no encontrado");
                return;
            }

            out.print("<div id='docData'>");
            out.print("<input type='hidden' id='doc_idDocumento' value='" + doc.getIdDocumento() + "'>");
            out.print("<input type='hidden' id='doc_idTipoDocVet' value='" + doc.getIdTipoDocVet() + "'>");
            out.print("<input type='hidden' id='doc_fechaDocumento' value='" + doc.getFechaDocumento() + "'>");
            out.print("<input type='hidden' id='doc_observaciones' value='"
                    + (doc.getObservaciones() != null ? escapeHtml(doc.getObservaciones()) : "") + "'>");
            out.print("</div>");
        } catch (NumberFormatException e) {
            out.print("ERROR: ID documento inválido");
        } catch (Exception e) {
            e.printStackTrace();
            out.print("ERROR: " + e.getMessage());
        }
    }

    // ===================== MÉTODOS POST =====================
    private void registrarDocumento(HttpServletRequest request, PrintWriter out)
            throws IOException, ServletException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("idUsuario") == null) {
            out.print("ERROR: Sesión expirada");
            return;
        }

        int idUsuario = (int) session.getAttribute("idUsuario");

        try {
            // Validar parámetros
            if (request.getParameter("idMascota") == null
                    || request.getParameter("idTipoDocVet") == null
                    || request.getParameter("fechaDocumento") == null) {
                out.print("ERROR: Faltan parámetros obligatorios");
                return;
            }

            int idMascota = Integer.parseInt(request.getParameter("idMascota"));
            int idTipoDocVet = Integer.parseInt(request.getParameter("idTipoDocVet"));
            String fechaDocStr = request.getParameter("fechaDocumento");
            String observaciones = request.getParameter("observaciones");

            // idFicha - si no viene, usar 0
            String idFichaStr = request.getParameter("idFicha");
            Integer idFicha = null;
            if (idFichaStr != null && !idFichaStr.trim().isEmpty()) {
                idFicha = Integer.parseInt(idFichaStr);
            }

            // Validar archivo
            Part filePart = request.getPart("archivo");
            if (filePart == null || filePart.getSize() == 0) {
                out.print("ERROR: Debe seleccionar un archivo");
                return;
            }

            String nombreArchivo = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
            String extension = obtenerExtension(nombreArchivo);

            if (extension.isEmpty()) {
                out.print("ERROR: Archivo sin extensión");
                return;
            }

            // Validar formato
            if (!documentoDAO.validarFormato(extension)) {
                out.print("ERROR: Formato no permitido");
                return;
            }

            // Guardar archivo
            String nombreUnico = generarNombreUnico(idMascota, extension);
            String ruta = guardarArchivo(filePart, nombreUnico);

            // Parsear fecha
            LocalDate fechaDocumento = LocalDate.parse(fechaDocStr);

            /// En el método registrarDocumento(), al crear el documento:
            Documento documento = new Documento(
                    idMascota,
                    idFicha,
                    idTipoDocVet,
                    nombreUnico,
                    nombreUnico,
                    extension.toUpperCase(),
                    filePart.getSize(),
                    fechaDocumento,
                    observaciones,
                    null // idConsulta
            );
// 👇 Agrega esta línea:
            documento.setEstado("ACTIVO");

            // Guardar en BD
            boolean resultado = documentoDAO.registrarDocumento(documento, idUsuario);

            if (resultado) {
                out.print("SUCCESS: Documento registrado exitosamente");
            } else {
                eliminarArchivoFisico(ruta);
                out.print("ERROR: No se pudo guardar en la base de datos");
            }
        } catch (NumberFormatException e) {
            out.print("ERROR: Parámetros numéricos inválidos");
        } catch (Exception e) {
            e.printStackTrace();
            out.print("ERROR: " + e.getMessage());
        }
    }

    private void actualizarDocumento(HttpServletRequest request, PrintWriter out) {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("idUsuario") == null) {
            out.print("ERROR: Sesión expirada");
            return;
        }

        int idUsuario = (int) session.getAttribute("idUsuario");

        try {
            if (request.getParameter("idDocumento") == null
                    || request.getParameter("idTipoDocVet") == null
                    || request.getParameter("fechaDocumento") == null) {
                out.print("ERROR: Faltan parámetros");
                return;
            }

            int idDocumento = Integer.parseInt(request.getParameter("idDocumento"));
            int idTipoDocVet = Integer.parseInt(request.getParameter("idTipoDocVet"));
            String fechaDocStr = request.getParameter("fechaDocumento");
            String observaciones = request.getParameter("observaciones");

            LocalDate fechaDocumento = LocalDate.parse(fechaDocStr);

            boolean resultado = documentoDAO.actualizarDocumento(
                    idDocumento, idTipoDocVet, fechaDocumento, observaciones, idUsuario
            );

            if (resultado) {
                out.print("SUCCESS: Documento actualizado exitosamente");
            } else {
                out.print("ERROR: No se pudo actualizar el documento");
            }
        } catch (NumberFormatException e) {
            out.print("ERROR: Parámetros numéricos inválidos");
        } catch (Exception e) {
            e.printStackTrace();
            out.print("ERROR: " + e.getMessage());
        }
    }

    private void eliminarDocumento(HttpServletRequest request, PrintWriter out) {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("idUsuario") == null) {
            out.print("ERROR: Sesión expirada");
            return;
        }

        int idUsuario = (int) session.getAttribute("idUsuario");

        try {
            if (request.getParameter("idDocumento") == null) {
                out.print("ERROR: Faltan parámetros");
                return;
            }

            int idDocumento = Integer.parseInt(request.getParameter("idDocumento"));
            String motivo = request.getParameter("motivo");

            if (motivo == null || motivo.trim().isEmpty()) {
                motivo = "Sin motivo especificado";
            }

            boolean resultado = documentoDAO.eliminarDocumento(
                    idDocumento, motivo, idUsuario
            );

            if (resultado) {
                out.print("SUCCESS: Documento eliminado exitosamente");
            } else {
                out.print("ERROR: No se pudo eliminar el documento");
            }
        } catch (NumberFormatException e) {
            out.print("ERROR: ID documento inválido");
        } catch (Exception e) {
            e.printStackTrace();
            out.print("ERROR: " + e.getMessage());
        }
    }

    // ===================== MÉTODOS AUXILIARES =====================
    private String guardarArchivo(Part filePart, String nombreArchivo) throws IOException {
        // Ruta permanente FUERA de webapps
        String rutaBase = "C:/archivos_veterinaria/documentos_veterinarios";
        File uploadDir = new File(rutaBase);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }
        String rutaCompleta = rutaBase + File.separator + nombreArchivo;
        try (InputStream input = filePart.getInputStream()) {
            Files.copy(input, Paths.get(rutaCompleta), StandardCopyOption.REPLACE_EXISTING);
        }
        // 👇 DEVOLVÉ SOLO EL NOMBRE DEL ARCHIVO, NO LA RUTA COMPLETA
        return nombreArchivo;
    }

    private String generarNombreUnico(int idMascota, String extension) {
        long timestamp = System.currentTimeMillis();
        return "DOC_" + idMascota + "_" + timestamp + "." + extension.toLowerCase();
    }

    private String obtenerExtension(String nombreArchivo) {
        int lastDot = nombreArchivo.lastIndexOf('.');
        if (lastDot > 0) {
            return nombreArchivo.substring(lastDot + 1).toUpperCase();
        }
        return "";
    }

    private void eliminarArchivoFisico(String ruta) {
        try {
            Files.deleteIfExists(Paths.get(ruta));
        } catch (IOException e) {
            // Ignorar
        }
    }

    private String formatFecha(String fecha) {
        try {
            LocalDate date = LocalDate.parse(fecha);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            return date.format(formatter);
        } catch (Exception e) {
            return fecha;
        }
    }

    private String escapeHtml(String text) {
        if (text == null) {
            return "";
        }
        return text.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }

    private void descargarDocumento(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        // Primero, limpia cualquier writer previo
        response.reset();

        try {
            String idStr = request.getParameter("idDocumento");
            if (idStr == null || idStr.trim().isEmpty()) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID requerido");
                return;
            }

            int idDocumento = Integer.parseInt(idStr.trim());
            Documento documento = documentoDAO.obtenerPorId(idDocumento);

            if (documento == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Documento no encontrado");
                return;
            }

            // Ruta completa del archivo
            String nombreArchivo = documento.getRutaArchivo();
            String rutaCompleta = "C:/archivos_veterinaria/documentos_veterinarios/" + nombreArchivo;
            File archivo = new File(rutaCompleta);

            if (!archivo.exists()) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Archivo físico no encontrado: " + rutaCompleta);
                return;
            }

            // Obtener extensión
            String extension = "";
            int lastDot = nombreArchivo.lastIndexOf('.');
            if (lastDot > 0) {
                extension = nombreArchivo.substring(lastDot + 1).toLowerCase();
            }

            // Determinar MIME type
            String mimeType;
            switch (extension) {
                case "pdf":
                    mimeType = "application/pdf";
                    break;
                case "jpg":
                case "jpeg":
                    mimeType = "image/jpeg";
                    break;
                case "png":
                    mimeType = "image/png";
                    break;
                case "docx":
                    mimeType = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
                    break;
                case "xlsx":
                    mimeType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
                    break;
                default:
                    mimeType = "application/octet-stream";
            }

            // Configurar headers según tipo
            if (extension.equals("pdf") || extension.equals("jpg")
                    || extension.equals("jpeg") || extension.equals("png")) {
                // Mostrar en navegador (inline)
                response.setHeader("Content-Disposition", "inline; filename=\"" + nombreArchivo + "\"");
            } else {
                // Forzar descarga (attachment)
                response.setHeader("Content-Disposition", "attachment; filename=\"" + nombreArchivo + "\"");
            }

            response.setContentType(mimeType);
            response.setHeader("Content-Length", String.valueOf(archivo.length()));
            response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
            response.setHeader("Pragma", "no-cache");
            response.setHeader("Expires", "0");

            // Enviar archivo
            try (FileInputStream fis = new FileInputStream(archivo); OutputStream os = response.getOutputStream()) {

                byte[] buffer = new byte[8192]; // Buffer de 8KB
                int bytesRead;

                while ((bytesRead = fis.read(buffer)) != -1) {
                    os.write(buffer, 0, bytesRead);
                }

                os.flush();
            }

        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID inválido");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error interno: " + e.getMessage());
        }
    }
}

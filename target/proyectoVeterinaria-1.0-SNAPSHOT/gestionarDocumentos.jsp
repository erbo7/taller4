<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String usuario = (String) session.getAttribute("usuario");
    String nombre = (String) session.getAttribute("nombre");
    Integer idRol = (Integer) session.getAttribute("rol");
    Integer idUsuario = (Integer) session.getAttribute("idUsuario");
    if (usuario == null || nombre == null || idRol == null) {
        response.sendRedirect("login.jsp");
        return;
    }
%>
<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Gestionar Documentos - DiazPet</title>
        <link href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap/5.3.0/css/bootstrap.min.css" rel="stylesheet">
        <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
        <style>
            :root {
                --primary-color: #667eea;
                --secondary-color: #764ba2;
                --sidebar-width: 250px;
                --sidebar-collapsed: 70px;
            }
            body {
                font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
                background-color: #f5f6fa;
                overflow-x: hidden;
            }
            .sidebar {
                position: fixed;
                top: 0;
                left: 0;
                height: 100vh;
                width: var(--sidebar-width);
                background: linear-gradient(135deg, var(--primary-color) 0%, var(--secondary-color) 100%);
                transition: all 0.3s ease;
                z-index: 1000;
                box-shadow: 2px 0 10px rgba(0,0,0,0.1);
            }
            .sidebar.collapsed {
                width: var(--sidebar-collapsed);
            }
            .sidebar-header {
                padding: 20px;
                color: white;
                border-bottom: 1px solid rgba(255,255,255,0.1);
                display: flex;
                align-items: center;
                justify-content: space-between;
            }
            .sidebar-header h3 {
                margin: 0;
                font-size: 24px;
                font-weight: bold;
            }
            .sidebar.collapsed .sidebar-header h3,
            .sidebar.collapsed .sidebar-header small,
            .sidebar.collapsed .menu-text,
            .sidebar.collapsed .user-info span {
                display: none;
            }
            .toggle-btn {
                background: rgba(255,255,255,0.2);
                border: none;
                color: white;
                padding: 8px 12px;
                border-radius: 5px;
                cursor: pointer;
                transition: all 0.3s;
            }
            .toggle-btn:hover {
                background: rgba(255,255,255,0.3);
            }
            .user-section {
                padding: 20px;
                color: white;
                border-bottom: 1px solid rgba(255,255,255,0.1);
            }
            .user-info {
                display: flex;
                align-items: center;
                gap: 15px;
            }
            .user-avatar {
                width: 45px;
                height: 45px;
                background: white;
                border-radius: 50%;
                display: flex;
                align-items: center;
                justify-content: center;
                font-weight: bold;
                color: var(--primary-color);
                font-size: 18px;
                flex-shrink: 0;
            }
            .sidebar-menu {
                padding: 20px 0;
                overflow-y: auto;
                height: calc(100vh - 240px);
            }
            .menu-item {
                display: flex;
                align-items: center;
                padding: 12px 20px;
                color: rgba(255,255,255,0.8);
                text-decoration: none;
                transition: all 0.3s;
                cursor: pointer;
            }
            .menu-item:hover {
                background: rgba(255,255,255,0.1);
                color: white;
            }
            .menu-item.active {
                background: rgba(255,255,255,0.2);
                color: white;
                border-left: 4px solid white;
            }
            .menu-item i {
                width: 30px;
                font-size: 18px;
                text-align: center;
            }
            .menu-text {
                margin-left: 15px;
                font-weight: 500;
            }
            .logout-section {
                position: absolute;
                bottom: 0;
                width: 100%;
                border-top: 1px solid rgba(255,255,255,0.1);
            }
            .main-content {
                margin-left: var(--sidebar-width);
                transition: all 0.3s ease;
                min-height: 100vh;
            }
            .sidebar.collapsed ~ .main-content {
                margin-left: var(--sidebar-collapsed);
            }
            .top-bar {
                background: white;
                padding: 20px 30px;
                box-shadow: 0 2px 10px rgba(0,0,0,0.05);
                margin-bottom: 30px;
            }
            .top-bar h2 {
                margin: 0;
                color: #2c3e50;
                font-size: 28px;
                font-weight: bold;
            }
            .content-area {
                padding: 0 30px 30px;
            }
            .card-custom {
                background: white;
                border-radius: 10px;
                box-shadow: 0 2px 10px rgba(0,0,0,0.05);
                margin-bottom: 30px;
            }
            .card-custom-header {
                background: linear-gradient(135deg, var(--primary-color) 0%, var(--secondary-color) 100%);
                color: #fff;
                padding: 10px 25px;
                display: flex;
                justify-content: space-between;
                align-items: center;
                border-radius: 6px 6px 0 0;
            }
            .card-custom-header h5,
            .card-custom-header h5 i {
                color: #fff !important;
                margin: 0;
                font-weight: 600;
            }
            .card-custom-body {
                padding: 25px;
            }
            .form-label {
                font-weight: 600;
                color: #2c3e50;
                margin-bottom: 8px;
            }
            .form-control, .form-select {
                border: 2px solid #e0e0e0;
                border-radius: 8px;
                padding: 10px 15px;
                transition: all 0.3s;
            }
            .form-control:focus, .form-select:focus {
                border-color: var(--primary-color);
                box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
            }
            .btn-primary-custom {
                background: linear-gradient(135deg, var(--primary-color) 0%, var(--secondary-color) 100%);
                border: none;
                color: white;
                padding: 10px 25px;
                border-radius: 8px;
                font-weight: 600;
                transition: all 0.3s;
            }
            .btn-primary-custom:hover {
                transform: translateY(-2px);
                box-shadow: 0 5px 15px rgba(102, 126, 234, 0.4);
                color: white;
            }
            .table-responsive {
                overflow-x: auto;
            }
            .table {
                margin-bottom: 0;
            }
            .table thead tr {
                background: linear-gradient(135deg, var(--primary-color) 0%, var(--secondary-color) 100%);
                color: white;
            }
            .table thead th {
                border: none;
                padding: 15px;
                font-weight: 600;
                white-space: nowrap;
            }
            .table tbody tr {
                transition: all 0.3s;
            }
            .table tbody tr:hover {
                background-color: #f8f9fa;
                transform: scale(1.01);
            }
            .table tbody td {
                vertical-align: middle;
                padding: 12px 15px;
                border-bottom: 1px solid #e9ecef;
            }
            .badge-formato {
                padding: 6px 14px;
                border-radius: 20px;
                font-size: 11px;
                font-weight: 600;
            }
            .badge-pdf {
                background-color: #fee2e2;
                color: #dc2626;
            }
            .badge-jpg, .badge-jpeg, .badge-png {
                background-color: #dbeafe;
                color: #1e40af;
            }
            .badge-docx {
                background-color: #dbeafe;
                color: #2563eb;
            }
            .badge-xlsx {
                background-color: #d1fae5;
                color: #059669;
            }
            .badge-obligatorio {
                background-color: #fef3c7;
                color: #92400e;
            }
            .btn-tabla {
                padding: 6px 12px;
                font-size: 13px;
                margin: 0 3px;
                border-radius: 6px;
            }
            .modal-header {
                background: linear-gradient(135deg, var(--primary-color) 0%, var(--secondary-color) 100%);
                color: white;
                border-radius: 10px 10px 0 0;
            }
            .modal-content {
                border-radius: 10px;
                border: none;
                box-shadow: 0 10px 40px rgba(0,0,0,0.2);
            }
            .empty-state {
                text-align: center;
                padding: 60px 20px;
            }
            .empty-state i {
                font-size: 64px;
                color: #cbd5e1;
                margin-bottom: 20px;
            }
            .empty-state h4 {
                color: #64748b;
                margin-bottom: 10px;
            }
            .empty-state p {
                color: #94a3b8;
            }
            .info-mascota {
                background: linear-gradient(135deg, #f0f9ff 0%, #e0f2fe 100%);
                border-left: 4px solid var(--primary-color);
                padding: 15px;
                border-radius: 8px;
                margin-bottom: 20px;
            }
            .info-mascota h6 {
                color: var(--primary-color);
                font-weight: bold;
                margin-bottom: 10px;
            }
            .file-upload-area {
                border: 2px dashed #cbd5e1;
                border-radius: 8px;
                padding: 30px;
                text-align: center;
                transition: all 0.3s;
                cursor: pointer;
            }
            .file-upload-area:hover {
                border-color: var(--primary-color);
                background-color: #f8fafc;
            }
            .file-upload-area.dragover {
                border-color: var(--primary-color);
                background-color: #eff6ff;
            }
        </style>
    </head>
    <body>
        <!-- Sidebar -->
        <div class="sidebar" id="sidebar">
            <div class="sidebar-header">
                <div>
                    <h3>🐾 DiazPet</h3>
                    <small style="color: rgba(255,255,255,0.8);">Gestión de Documentos</small>
                </div>
                <button class="toggle-btn" onclick="toggleSidebar()">
                    <i class="fas fa-bars"></i>
                </button>
            </div>
            <div class="user-section">
                <div class="user-info">
                    <div class="user-avatar"><%= nombre.substring(0, 1).toUpperCase()%></div>
                    <span>
                        <strong><%= nombre%></strong><br>
                        <small style="opacity: 0.8;"><%= usuario%></small>
                    </span>
                </div>
            </div>
            <div class="sidebar-menu">
                <a href="RecepcionistaServlet" class="menu-item">
                    <i class="fas fa-home"></i>
                    <span class="menu-text">Inicio</span>
                </a>
                <a href="gestionarAgenda.jsp" class="menu-item">
                    <i class="fas fa-calendar-alt"></i>
                    <span class="menu-text">Agenda</span>
                </a>
                <a href="gestionarCitas.jsp" class="menu-item">
                    <i class="fas fa-clock"></i>
                    <span class="menu-text">Citas</span>
                </a>  
                <a href="gestionarRecordatorios.jsp" class="menu-item">
                    <i class="fas fa-bell"></i>
                    <span class="menu-text">Recordatorios</span>
                </a>
                <a href="gestionarDocumentos.jsp" class="menu-item active">
                    <i class="fas fa-folder-open"></i>
                    <span class="menu-text">Documentos</span>
                </a>
            </div>
            <div class="logout-section">
                <a href="login.jsp" class="menu-item" onclick="return confirm('¿Seguro que deseas cerrar sesión?')">
                    <i class="fas fa-sign-out-alt"></i>
                    <span class="menu-text">Cerrar Sesión</span>
                </a>
            </div>
        </div>
        <!-- Main Content -->
        <div class="main-content">
            <div class="top-bar">
                <div class="d-flex justify-content-between align-items-center">
                    <div>
                        <h2><i class="fas fa-folder-open"></i> Gestión de Documentos</h2>
                        <p class="text-muted mb-0">
                            <i class="far fa-calendar"></i>
                            <span id="currentDate"></span>
                        </p>
                    </div>
                </div>
            </div>
            <div class="content-area">
                <!-- Buscador Cliente/Mascota -->
                <div class="card-custom">
                    <div class="card-custom-header">
                        <h5><i class="fas fa-search"></i> Buscar Mascota</h5>
                    </div>
                    <div class="card-custom-body">
                        <div class="row g-3 align-items-end">
                            <div class="col-md-5">
                                <label class="form-label">
                                    <i class="fas fa-user"></i> Cliente
                                </label>
                                <select class="form-select" id="idCliente" onchange="cargarMascotas()">
                                    <option value="">Seleccione cliente</option>
                                </select>
                            </div>
                            <div class="col-md-5">
                                <label class="form-label">
                                    <i class="fas fa-paw"></i> Mascota
                                </label>
                                <select class="form-select" id="idMascota" disabled>
                                    <option value="">Primero seleccione cliente</option>
                                </select>
                            </div>
                            <div class="col-md-2">
                                <button class="btn-primary-custom w-100" onclick="buscarDocumentos()">
                                    <i class="fas fa-search"></i> Buscar
                                </button>
                            </div>
                        </div>
                        <!-- Info de mascota seleccionada -->
                        <div id="infoMascota" style="display: none;" class="info-mascota mt-3">
                            <h6><i class="fas fa-info-circle"></i> Información de la Mascota</h6>
                            <div class="row">
                                <div class="col-md-6">
                                    <strong><i class="fas fa-user"></i> Cliente:</strong> <span id="infoCliente"></span>
                                </div>
                                <div class="col-md-6">
                                    <strong><i class="fas fa-paw"></i> Mascota:</strong> <span id="infoNombreMascota"></span>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <!-- Tabla de Documentos -->
                <div class="card-custom">
                    <div class="card-custom-header">
                        <h5><i class="fas fa-file-medical"></i> Documentos de la Ficha Clínica</h5>
                        <button class="btn btn-light btn-sm" onclick="abrirModalRegistrar()" id="btnRegistrar" disabled>
                            <i class="fas fa-plus"></i> Registrar Documento
                        </button>
                    </div>
                    <div class="card-custom-body">
                        <div class="table-responsive">
                            <table class="table table-hover" id="tablaDocumentos">
                                <thead>
                                    <tr>
                                        <th><i class="fas fa-file"></i> Tipo</th>
                                        <th><i class="fas fa-calendar"></i> Fecha</th>
                                        <th><i class="fas fa-file-alt"></i> Archivo</th>
                                        <th><i class="fas fa-database"></i> Tamaño</th>
                                        <th><i class="fas fa-comment"></i> Observaciones</th>
                                        <th><i class="fas fa-cog"></i> Acciones</th>
                                    </tr>
                                </thead>
                                <tbody id="listaDocumentos">
                                    <tr>
                                        <td colspan="6">
                                            <div class="empty-state">
                                                <i class="fas fa-folder-open"></i>
                                                <h4>Seleccione una mascota</h4>
                                                <p>Use los filtros superiores para buscar documentos</p>
                                            </div>
                                        </td>
                                    </tr>
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <!-- Modal Registrar Documento -->
        <div class="modal fade" id="modalDocumento" tabindex="-1">
            <div class="modal-dialog modal-lg">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title">
                            <i class="fas fa-file-upload"></i> Registrar Documento
                        </h5>
                        <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
                    </div>
                    <div class="modal-body">
                        <form id="formDocumento" enctype="multipart/form-data">
                            <input type="hidden" name="accion" value="registrar">
                            <input type="hidden" name="idMascota" id="formIdMascota">
                            <input type="hidden" name="idFicha" id="formIdFicha">
                            <div class="mb-3">
                                <label class="form-label">
                                    <i class="fas fa-upload"></i> Archivo *
                                </label>
                                <div class="file-upload-area" id="fileUploadArea">
                                    <i class="fas fa-cloud-upload-alt fa-3x text-muted mb-3"></i>
                                    <p class="mb-2">Arrastra un archivo aquí o haz clic para seleccionar</p>
                                    <small class="text-muted">Formatos: PDF, JPG, PNG, DOCX, XLSX - Máx. 20MB</small>
                                    <input type="file" class="form-control mt-3" name="archivo" id="archivo" 
                                           accept=".pdf,.jpg,.jpeg,.png,.docx,.xlsx" required style="display: none;">
                                </div>
                                <div id="selectedFileName" class="mt-2" style="display: none;">
                                    <span class="badge bg-primary">
                                        <i class="fas fa-file"></i> <span id="fileName"></span>
                                    </span>
                                    <button type="button" class="btn btn-sm btn-outline-danger ms-2" onclick="clearFile()">
                                        <i class="fas fa-times"></i>
                                    </button>
                                </div>
                            </div>
                            <div class="row">
                                <div class="col-md-6 mb-3">
                                    <label class="form-label">
                                        <i class="fas fa-tags"></i> Tipo de Documento *
                                    </label>
                                    <select class="form-select" name="idTipoDocVet" id="idTipoDocVet" required>
                                        <option value="">Cargando tipos...</option>
                                    </select>
                                </div>
                                <div class="col-md-6 mb-3">
                                    <label class="form-label">
                                        <i class="fas fa-calendar-alt"></i> Fecha del Documento *
                                    </label>
                                    <input type="date" class="form-control" name="fechaDocumento" 
                                           id="fechaDocumento" required>
                                </div>
                            </div>
                            <div class="mb-3">
                                <label class="form-label">
                                    <i class="fas fa-sticky-note"></i> Observaciones
                                </label>
                                <textarea class="form-control" name="observaciones" id="observaciones" 
                                          rows="3" placeholder="Información adicional sobre el documento"></textarea>
                            </div>
                        </form>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">
                            <i class="fas fa-times"></i> Cancelar
                        </button>
                        <button type="button" class="btn-primary-custom" onclick="guardarDocumento()">
                            <i class="fas fa-save"></i> Guardar Documento
                        </button>
                    </div>
                </div>
            </div>
        </div>
        <!-- Modal Editar Documento -->
        <div class="modal fade" id="modalEditarDocumento" tabindex="-1">
            <div class="modal-dialog">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title">
                            <i class="fas fa-edit"></i> Editar Documento
                        </h5>
                        <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
                    </div>
                    <div class="modal-body">
                        <input type="hidden" id="editIdDocumento">
                        <div class="mb-3">
                            <label class="form-label">Tipo de Documento *</label>
                            <select class="form-select" id="editIdTipoDocVet" required>
                                <option value="">Cargando...</option>
                            </select>
                        </div>
                        <div class="mb-3">
                            <label class="form-label">Fecha del Documento *</label>
                            <input type="date" class="form-control" id="editFechaDocumento" required>
                        </div>
                        <div class="mb-3">
                            <label class="form-label">Observaciones</label>
                            <textarea class="form-control" id="editObservaciones" rows="3"></textarea>
                        </div>
                        <div class="alert alert-info">
                            <i class="fas fa-info-circle"></i>
                            <small>No se puede cambiar el archivo. Para reemplazarlo, elimine este documento y cargue uno nuevo.</small>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancelar</button>
                        <button type="button" class="btn-primary-custom" onclick="actualizarDocumento()">
                            <i class="fas fa-save"></i> Actualizar
                        </button>
                    </div>
                </div>
            </div>
        </div>
        <!-- Modal Eliminar Documento -->
        <div class="modal fade" id="modalEliminarDocumento" tabindex="-1">
            <div class="modal-dialog">
                <div class="modal-content">
                    <div class="modal-header bg-danger text-white">
                        <h5 class="modal-title">
                            <i class="fas fa-trash-alt"></i> Eliminar Documento
                        </h5>
                        <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
                    </div>
                    <div class="modal-body">
                        <input type="hidden" id="deleteIdDocumento">
                        <div class="alert alert-warning">
                            <i class="fas fa-exclamation-triangle"></i>
                            <strong>Advertencia:</strong> Esta acción no se puede deshacer.
                        </div>
                        <p>¿Está seguro de eliminar este documento?</p>
                        <div class="mb-3">
                            <label class="form-label">Motivo de eliminación</label>
                            <textarea class="form-control" id="motivoEliminacion" rows="3" 
                                      placeholder="Opcional: indique el motivo"></textarea>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancelar</button>
                        <button type="button" class="btn btn-danger" onclick="confirmarEliminacion()">
                            <i class="fas fa-trash"></i> Confirmar Eliminación
                        </button>
                    </div>
                </div>
            </div>
        </div>
        <script src="https://cdnjs.cloudflare.com/ajax/libs/bootstrap/5.3.0/js/bootstrap.bundle.min.js"></script>
        <script>
                            // ===================== VARIABLES GLOBALES =====================
                            const idUsuario = <%= idUsuario != null ? idUsuario : 0%>;
                            let mascotaSeleccionada = null;
                            let clientesCargados = false;

                            // ===================== INICIALIZACIÓN =====================
                            document.addEventListener('DOMContentLoaded', function () {
                                setCurrentDate();
                                cargarClientes();
                                cargarTiposDocumento();
                                setupFileUpload();
                                document.getElementById('fechaDocumento').value = new Date().toISOString().split('T')[0];
                            });

                            // ===================== FUNCIONES GENERALES =====================
                            function toggleSidebar() {
                                document.getElementById('sidebar').classList.toggle('collapsed');
                            }

                            function setCurrentDate() {
                                const options = {weekday: 'long', year: 'numeric', month: 'long', day: 'numeric'};
                                const date = new Date().toLocaleDateString('es-ES', options);
                                document.getElementById('currentDate').textContent = date.charAt(0).toUpperCase() + date.slice(1);
                            }

                            function setupFileUpload() {
                                const fileArea = document.getElementById('fileUploadArea');
                                const fileInput = document.getElementById('archivo');
                                fileArea.onclick = function () {
                                    fileInput.click();
                                };
                                fileInput.onchange = function () {
                                    if (this.files.length > 0) {
                                        const file = this.files[0];
                                        document.getElementById('fileName').textContent = file.name;
                                        document.getElementById('selectedFileName').style.display = 'block';
                                        fileArea.style.display = 'none';
                                    }
                                };
                            }

                            function clearFile() {
                                document.getElementById('archivo').value = '';
                                document.getElementById('selectedFileName').style.display = 'none';
                                document.getElementById('fileUploadArea').style.display = 'block';
                            }

                            // ===================== CARGAR DATOS =====================
                            function cargarClientes() {
                                const select = document.getElementById('idCliente');
                                select.innerHTML = '<option value="">Cargando clientes...</option>';
                                fetch('DocumentoServlet?accion=listarClientes')
                                        .then(response => response.text())
                                        .then(html => {
                                            select.innerHTML = html;
                                        })
                                        .catch(err => {
                                            select.innerHTML = '<option value="">Error al cargar clientes</option>';
                                        });
                            }

                            function cargarMascotas() {
                                const clienteId = document.getElementById('idCliente').value;
                                const mascotaSelect = document.getElementById('idMascota');
                                if (!clienteId) {
                                    mascotaSelect.innerHTML = '<option value="">Primero seleccione cliente</option>';
                                    mascotaSelect.disabled = true;
                                    limpiarBusqueda();
                                    return;
                                }
                                mascotaSelect.innerHTML = '<option value="">Cargando...</option>';
                                mascotaSelect.disabled = false;

                                const url = 'DocumentoServlet?accion=listarMascotasPorCliente&idCliente=' + encodeURIComponent(clienteId);
                                fetch(url)
                                        .then(response => response.text())
                                        .then(html => {
                                            mascotaSelect.innerHTML = html;
                                        })
                                        .catch(err => {
                                            mascotaSelect.innerHTML = '<option value="">Error al cargar mascotas</option>';
                                        });
                            }

                            function cargarTiposDocumento() {
                                fetch('DocumentoServlet?accion=listarTiposDocumento')
                                        .then(r => r.text())
                                        .then(html => {
                                            document.getElementById('idTipoDocVet').innerHTML = '<option value="">Seleccione tipo</option>' + html;
                                            document.getElementById('editIdTipoDocVet').innerHTML = '<option value="">Seleccione tipo</option>' + html;
                                        })
                                        .catch(err => {
                                            document.getElementById('idTipoDocVet').innerHTML = '<option value="">Error al cargar tipos</option>';
                                        });
                            }

                            // ===================== BUSCAR DOCUMENTOS =====================
                            function buscarDocumentos() {
                                const mascotaId = document.getElementById('idMascota').value;
                                const mascotaSelect = document.getElementById('idMascota');
                                const clienteSelect = document.getElementById('idCliente');
                                if (!mascotaId) {
                                    alert('⚠️ Seleccione una mascota');
                                    return;
                                }
                                mascotaSeleccionada = {
                                    id: mascotaId,
                                    nombre: mascotaSelect.options[mascotaSelect.selectedIndex].text,
                                    cliente: clienteSelect.options[clienteSelect.selectedIndex].text,
                                    idFicha: 0
                                };
                                document.getElementById('infoCliente').textContent = mascotaSeleccionada.cliente;
                                document.getElementById('infoNombreMascota').textContent = mascotaSeleccionada.nombre;
                                document.getElementById('infoMascota').style.display = 'block';
                                document.getElementById('btnRegistrar').disabled = false;

                                document.getElementById('listaDocumentos').innerHTML = `
                    <tr>
                        <td colspan="6" class="text-center py-4">
                            <div class="spinner-border text-primary" role="status"></div>
                            <p class="mt-3 text-muted">Cargando documentos...</p>
                        </td>
                    </tr>`;

                                fetch('DocumentoServlet?accion=listarDocumentosPorMascota&idMascota=' + mascotaId)
                                        .then(r => r.text())
                                        .then(html => {
                                            document.getElementById('listaDocumentos').innerHTML = html;
                                        })
                                        .catch(err => {
                                            document.getElementById('listaDocumentos').innerHTML = `
                            <tr>
                                <td colspan="6" class="text-center text-danger py-4">
                                    <i class="fas fa-exclamation-triangle"></i> Error al cargar documentos
                                </td>
                            </tr>`;
                                        });
                            }

                            function limpiarBusqueda() {
                                document.getElementById('infoMascota').style.display = 'none';
                                document.getElementById('btnRegistrar').disabled = true;
                                document.getElementById('listaDocumentos').innerHTML = `
                    <tr>
                        <td colspan="6">
                            <div class="empty-state">
                                <i class="fas fa-folder-open"></i>
                                <h4>Seleccione una mascota</h4>
                                <p>Use los filtros superiores para buscar documentos</p>
                            </div>
                        </td>
                    </tr>`;
                                mascotaSeleccionada = null;
                            }

                            // ===================== REGISTRAR DOCUMENTO =====================
                            function abrirModalRegistrar() {
                                if (!mascotaSeleccionada) {
                                    alert('⚠️ Seleccione una mascota primero');
                                    return;
                                }
                                document.getElementById('formIdMascota').value = mascotaSeleccionada.id;
                                document.getElementById('formIdFicha').value = ''; // ← SIEMPRE VACÍO (por ahora)
                                document.getElementById('formDocumento').reset();
                                clearFile();
                                document.getElementById('fechaDocumento').value = new Date().toISOString().split('T')[0];
                                new bootstrap.Modal(document.getElementById('modalDocumento')).show();
                            }

                            function guardarDocumento() {
                                const form = document.getElementById('formDocumento');
                                if (!form.checkValidity()) {
                                    form.reportValidity();
                                    return;
                                }
                                const archivo = document.getElementById('archivo').files[0];
                                if (!archivo) {
                                    alert('⚠️ Seleccione un archivo');
                                    return;
                                }
                                const btn = event.target;
                                const originalText = btn.innerHTML;
                                btn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Guardando...';
                                btn.disabled = true;

                                const formData = new FormData(form);
                                fetch('DocumentoServlet', {method: 'POST', body: formData})
                                        .then(r => r.text())
                                        .then(response => {
                                            if (response.startsWith('SUCCESS:')) {
                                                alert('✅ ' + response.substring(8));
                                                bootstrap.Modal.getInstance(document.getElementById('modalDocumento')).hide();
                                                buscarDocumentos();
                                            } else if (response.startsWith('ERROR:')) {
                                                alert('❌ ' + response.substring(6));
                                            } else {
                                                alert('❌ Respuesta inesperada del servidor');
                                            }
                                        })
                                        .catch(err => {
                                            alert('❌ Error al guardar documento');
                                        })
                                        .finally(() => {
                                            btn.innerHTML = originalText;
                                            btn.disabled = false;
                                        });
                            }

                            // ===================== EDITAR DOCUMENTO =====================
                            function abrirModalEditar(idDocumento) {
                                fetch('DocumentoServlet?accion=obtenerDocumento&idDocumento=' + idDocumento)
                                        .then(r => r.text())
                                        .then(response => {
                                            if (response.startsWith('ERROR:')) {
                                                alert('❌ ' + response.substring(6));
                                                return;
                                            }
                                            const tempDiv = document.createElement('div');
                                            tempDiv.innerHTML = response;
                                            const docId = tempDiv.querySelector('#doc_idDocumento') ? tempDiv.querySelector('#doc_idDocumento').value : '';
                                            const tipoDocVet = tempDiv.querySelector('#doc_idTipoDocVet') ? tempDiv.querySelector('#doc_idTipoDocVet').value : '';
                                            const fechaDoc = tempDiv.querySelector('#doc_fechaDocumento') ? tempDiv.querySelector('#doc_fechaDocumento').value : '';
                                            const observaciones = tempDiv.querySelector('#doc_observaciones') ? tempDiv.querySelector('#doc_observaciones').value : '';

                                            document.getElementById('editIdDocumento').value = docId;
                                            document.getElementById('editIdTipoDocVet').value = tipoDocVet;
                                            if (fechaDoc) {
                                                const fecha = new Date(fechaDoc);
                                                if (!isNaN(fecha.getTime())) {
                                                    document.getElementById('editFechaDocumento').value = fecha.toISOString().split('T')[0];
                                                }
                                            }
                                            document.getElementById('editObservaciones').value = observaciones;
                                            new bootstrap.Modal(document.getElementById('modalEditarDocumento')).show();
                                        })
                                        .catch(err => {
                                            alert('❌ Error al cargar documento');
                                        });
                            }

                            function actualizarDocumento() {
                                const idDocumento = document.getElementById('editIdDocumento').value;
                                const idTipoDocVet = document.getElementById('editIdTipoDocVet').value;
                                const fechaDocumento = document.getElementById('editFechaDocumento').value;
                                const observaciones = document.getElementById('editObservaciones').value;

                                if (!idTipoDocVet || !fechaDocumento) {
                                    alert('⚠️ Complete los campos obligatorios');
                                    return;
                                }

                                const btn = event.target;
                                const originalText = btn.innerHTML;
                                btn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Actualizando...';
                                btn.disabled = true;

                                const formData = new FormData();
                                formData.append('accion', 'actualizar');
                                formData.append('idDocumento', idDocumento);
                                formData.append('idTipoDocVet', idTipoDocVet);
                                formData.append('fechaDocumento', fechaDocumento);
                                formData.append('observaciones', observaciones);

                                fetch('DocumentoServlet', {method: 'POST', body: formData})
                                        .then(r => r.text())
                                        .then(response => {
                                            if (response.startsWith('SUCCESS:')) {
                                                alert('✅ ' + response.substring(8));
                                                bootstrap.Modal.getInstance(document.getElementById('modalEditarDocumento')).hide();
                                                buscarDocumentos();
                                            } else if (response.startsWith('ERROR:')) {
                                                alert('❌ ' + response.substring(6));
                                            } else {
                                                alert('❌ Respuesta inesperada del servidor');
                                            }
                                        })
                                        .catch(err => {
                                            alert('❌ Error al actualizar documento');
                                        })
                                        .finally(() => {
                                            btn.innerHTML = originalText;
                                            btn.disabled = false;
                                        });
                            }

                            // ===================== ELIMINAR DOCUMENTO =====================
                            function abrirModalEliminar(idDocumento, esObligatorio) {
                                if (esObligatorio) {
                                    alert('⚠️ Este documento es obligatorio y no puede ser eliminado');
                                    return;
                                }
                                document.getElementById('deleteIdDocumento').value = idDocumento;
                                document.getElementById('motivoEliminacion').value = '';
                                new bootstrap.Modal(document.getElementById('modalEliminarDocumento')).show();
                            }

                            function confirmarEliminacion() {
                                const idDocumento = document.getElementById('deleteIdDocumento').value;
                                const motivo = document.getElementById('motivoEliminacion').value.trim() || 'Sin motivo especificado';
                                const btn = event.target;
                                const originalText = btn.innerHTML;
                                btn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Eliminando...';
                                btn.disabled = true;

                                const formData = new FormData();
                                formData.append('accion', 'eliminar');
                                formData.append('idDocumento', idDocumento);
                                formData.append('motivo', motivo);

                                fetch('DocumentoServlet', {method: 'POST', body: formData})
                                        .then(r => r.text())
                                        .then(response => {
                                            if (response.startsWith('SUCCESS:')) {
                                                alert('✅ ' + response.substring(8));
                                                bootstrap.Modal.getInstance(document.getElementById('modalEliminarDocumento')).hide();
                                                buscarDocumentos();
                                            } else if (response.startsWith('ERROR:')) {
                                                alert('❌ ' + response.substring(6));
                                            } else {
                                                alert('❌ Respuesta inesperada del servidor');
                                            }
                                        })
                                        .catch(err => {
                                            alert('❌ Error al eliminar documento');
                                        })
                                        .finally(() => {
                                            btn.innerHTML = originalText;
                                            btn.disabled = false;
                                        });
                            }

                            // ===================== DESCARGAR DOCUMENTO =====================
                            function descargarDocumento(idDocumento) {
                                const url = 'DocumentoServlet?accion=descargarDocumento&idDocumento=' + idDocumento;
                                window.open(url, '_blank');
                            }
        </script>
    </body>
</html>
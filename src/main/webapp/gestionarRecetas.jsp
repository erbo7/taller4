<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String usuario = (String) session.getAttribute("usuario");
    String nombre = (String) session.getAttribute("nombre");
    Integer idRol = (Integer) session.getAttribute("rol");
    Integer idUsuario = (Integer) session.getAttribute("idUsuario");

    if (usuario == null || nombre == null || idRol == null || idRol != 3) {
        response.sendRedirect("login.jsp");
        return;
    }
%>

<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Recetas - DiazPet</title>
        <link href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap/5.3.0/css/bootstrap.min.css" rel="stylesheet">
        <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
        <style>
            :root {
                --primary-color: #10b981;
                --secondary-color: #059669;
                --sidebar-width: 250px;
                --sidebar-collapsed: 70px;
            }
            body {
                font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
                background-color: #f5f6fa;
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
            .top-bar .date {
                color: #999;
                font-size: 14px;
                margin-top: 5px;
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
                padding: 20px 25px;
                border-bottom: none;
                display: flex;
                align-items: center;
                justify-content: space-between;
                background: linear-gradient(135deg, var(--primary-color) 0%, var(--secondary-color) 100%);
                border-radius: 10px 10px 0 0;
                transition: all 0.3s ease;
            }
            .card-custom-header:hover {
                box-shadow: 0 4px 12px rgba(16, 185, 129, 0.3);
                transform: translateY(-2px);
            }
            .card-custom-header h5 {
                margin: 0;
                font-size: 18px;
                font-weight: bold;
                color: white;
            }
            .card-custom-header i {
                color: white;
            }
            .card-custom-body {
                padding: 25px;
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
                box-shadow: 0 5px 15px rgba(16, 185, 129, 0.4);
            }
            .badge-success {
                background: #d1fae5;
                color: #10b981;
                padding: 4px 8px;
                border-radius: 12px;
                font-size: 11px;
            }
            .badge-anulada {
                background: #fee2e2;
                color: #dc2626;
                padding: 4px 8px;
                border-radius: 12px;
                font-size: 11px;
            }
            .modal-header {
                background: linear-gradient(135deg, var(--primary-color) 0%, var(--secondary-color) 100%);
                color: white;
            }
            .modal-header .btn-close {
                filter: brightness(0) invert(1);
            }
        </style>
    </head>
    <body>
        <!-- Sidebar -->
        <div class="sidebar" id="sidebar">
            <div class="sidebar-header">
                <div>
                    <h3>🩺 DiazPet</h3>
                    <small style="color: rgba(255,255,255,0.8);">Veterinario</small>
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
                <a href="dashboardVeterinario.jsp" class="menu-item">
                    <i class="fas fa-home"></i>
                    <span class="menu-text">Inicio</span>
                </a>

                <a href="gestionarConsultas.jsp" class="menu-item">
                    <i class="fas fa-stethoscope"></i>
                    <span class="menu-text">Consultas</span>
                </a>

                <a href="gestionarFichaMedica.jsp" class="menu-item">
                    <i class="fas fa-file-medical"></i>
                    <span class="menu-text">Fichas Médicas</span>
                </a>

                <a href="gestionarDiagnosticos.jsp" class="menu-item">
                    <i class="fas fa-diagnoses"></i>
                    <span class="menu-text">Diagnósticos</span>
                </a>

                <a href="gestionarRecetas.jsp" class="menu-item" >
                    <i class="fas fa-prescription"></i>
                    <span class="menu-text">Recetas e Indicaciones</span>
                </a>

                <a href="gestionarProcedimientos.jsp" class="menu-item">
                    <i class="fas fa-syringe"></i>
                    <span class="menu-text">Procedimientos e Insumos</span>
                </a>
                <a href="gestionarOrdenesEstudios.jsp" class="menu-item">
                    <i class="fas fa-microscope"></i>
                    <span class="menu-text">Estudios</span>
                </a>

                <a href="gestionarOrdenesAnalisis.jsp" class="menu-item">
                    <i class="fas fa-dna"></i>
                    <span class="menu-text">Analisis</span>
                </a>

                <a href="gestionarTratamientos.jsp" class="menu-item">
                    <i class="fas fa-pills"></i>
                    <span class="menu-text">Tratamientos y Evolución</span>
                </a> 

                <a href="gestionarConstancias.jsp" class="menu-item">
                    <i class="fas fa-file-signature"></i>
                    <span class="menu-text">Constancias</span>
                </a>

            </div>

            <div class="logout-section">
                <a href="login.jsp" class="menu-item" onclick="return confirm('¿Seguro que deseas cerrar sesión?')">
                    <i class="fas fa-sign-out-alt"></i>
                    <span class="menu-text">Cerrar Sesión</span>
                </a>
            </div>
        </div>

        <!-- Contenido principal -->
        <div class="main-content">
            <div class="top-bar">
                <div>
                    <h2><i class="fas fa-prescription"></i> Gestionar Recetas</h2>
                    <p class="date mb-0">
                        <i class="far fa-calendar"></i>
                        <span id="currentDate"></span>
                    </p>
                </div>
            </div>

            <div class="content-area">
                <!-- SECCIÓN: Buscar Mascota -->
                <div class="card-custom">
                    <div class="card-custom-header">
                        <h5><i class="fas fa-search"></i> Buscar Mascota</h5>
                    </div>
                    <div class="card-custom-body">
                        <div class="row mb-4">
                            <div class="col-md-8">
                                <label class="form-label">Buscar mascota por nombre o dueño:</label>
                                <input type="text" class="form-control" id="buscarMascota" 
                                       placeholder="Escribe el nombre de la mascota o cliente..."
                                       onkeypress="if (event.key === 'Enter')
                                                   buscarMascotas()">
                            </div>
                            <div class="col-md-4">
                                <label class="form-label">&nbsp;</label>
                                <button class="btn btn-primary-custom w-100" onclick="buscarMascotas()">
                                    <i class="fas fa-search"></i> Buscar
                                </button>
                            </div>
                        </div>
                        <div id="contenedorResultados">
                            <div class="alert alert-info">
                                <i class="fas fa-info-circle"></i> Busca una mascota para gestionar sus recetas
                            </div>
                        </div>
                    </div>
                </div>

                <!-- SECCIÓN: Lista de Recetas (oculta por defecto) -->
                <div class="card-custom" id="contenedorRecetas" style="display: none;">
                    <div class="card-custom-header">
                        <h5><i class="fas fa-list"></i> Recetas de la Mascota</h5>
                        <button class="btn btn-light btn-sm" onclick="cerrarRecetas()">
                            <i class="fas fa-times"></i> Cerrar
                        </button>
                    </div>
                    <div class="card-custom-body">
                        <div id="listaRecetas"></div>
                    </div>
                </div>
            </div>
        </div>

        <!-- Modal Registrar Receta -->
        <div class="modal fade" id="modalRegistrar" tabindex="-1">
            <div class="modal-dialog modal-lg">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title"><i class="fas fa-plus"></i> Registrar Receta</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                    </div>
                    <div class="modal-body">
                        <div id="mensajeRegistrar"></div>

                        <div class="mb-3">
                            <label class="form-label fw-bold">Consulta: <span class="text-danger">*</span></label>
                            <select class="form-select" id="selectConsultaRegistrar" required>
                                <option value="">Cargando consultas...</option>
                            </select>
                            <small class="text-muted">Seleccione la consulta asociada a esta receta</small>
                        </div>

                        <div class="mb-3">
                            <label class="form-label fw-bold">Medicamento: <span class="text-danger">*</span></label>
                            <input type="text" class="form-control" id="txtMedicamentoRegistrar" 
                                   placeholder="Ej: Amoxicilina, Paracetamol, etc." required>
                        </div>

                        <div class="row mb-3">
                            <div class="col-md-6">
                                <label class="form-label fw-bold">Dosis: <span class="text-danger">*</span></label>
                                <input type="text" class="form-control" id="txtDosisRegistrar" 
                                       placeholder="Ej: 500 mg, 1 comprimido" required>
                            </div>
                            <div class="col-md-6">
                                <label class="form-label fw-bold">Frecuencia: <span class="text-danger">*</span></label>
                                <input type="text" class="form-control" id="txtFrecuenciaRegistrar" 
                                       placeholder="Ej: Cada 8 horas, 2 veces al día" required>
                            </div>
                        </div>

                        <div class="mb-3">
                            <label class="form-label fw-bold">Duración:</label>
                            <input type="text" class="form-control" id="txtDuracionRegistrar" 
                                   placeholder="Ej: 7 días, 2 semanas">
                        </div>

                        <div class="mb-3">
                            <label class="form-label fw-bold">Indicaciones:</label>
                            <textarea class="form-control" id="txtIndicacionesRegistrar" rows="3" 
                                      placeholder="Instrucciones generales para el cliente..."></textarea>
                        </div>

                        <div class="mb-3">
                            <label class="form-label fw-bold">Observaciones:</label>
                            <textarea class="form-control" id="txtObservacionesRegistrar" rows="3" 
                                      placeholder="Observaciones adicionales..."></textarea>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancelar</button>
                        <button type="button" class="btn btn-primary-custom" onclick="guardarRegistro()">
                            <i class="fas fa-save"></i> Guardar Receta
                        </button>
                    </div>
                </div>
            </div>
        </div>

        <!-- Modal Editar Receta -->
        <div class="modal fade" id="modalEditar" tabindex="-1">
            <div class="modal-dialog modal-lg">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title"><i class="fas fa-edit"></i> Editar Receta</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                    </div>
                    <div class="modal-body">
                        <div id="mensajeEditar"></div>
                        <input type="hidden" id="idRecetaEditar">

                        <div class="mb-3">
                            <label class="form-label fw-bold">Medicamento: <span class="text-danger">*</span></label>
                            <input type="text" class="form-control" id="txtMedicamentoEditar" required>
                        </div>

                        <div class="row mb-3">
                            <div class="col-md-6">
                                <label class="form-label fw-bold">Dosis: <span class="text-danger">*</span></label>
                                <input type="text" class="form-control" id="txtDosisEditar" required>
                            </div>
                            <div class="col-md-6">
                                <label class="form-label fw-bold">Frecuencia: <span class="text-danger">*</span></label>
                                <input type="text" class="form-control" id="txtFrecuenciaEditar" required>
                            </div>
                        </div>

                        <div class="mb-3">
                            <label class="form-label fw-bold">Duración:</label>
                            <input type="text" class="form-control" id="txtDuracionEditar">
                        </div>

                        <div class="mb-3">
                            <label class="form-label fw-bold">Indicaciones:</label>
                            <textarea class="form-control" id="txtIndicacionesEditar" rows="3"></textarea>
                        </div>

                        <div class="mb-3">
                            <label class="form-label fw-bold">Observaciones:</label>
                            <textarea class="form-control" id="txtObservacionesEditar" rows="3"></textarea>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancelar</button>
                        <button type="button" class="btn btn-primary-custom" onclick="guardarEdicion()">
                            <i class="fas fa-save"></i> Guardar Cambios
                        </button>
                    </div>
                </div>
            </div>
        </div>

        <!-- Modal Anular Receta -->
        <div class="modal fade" id="modalAnular" tabindex="-1">
            <div class="modal-dialog">
                <div class="modal-content">
                    <div class="modal-header bg-danger text-white">
                        <h5 class="modal-title"><i class="fas fa-ban"></i> Anular Receta</h5>
                        <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
                    </div>
                    <div class="modal-body">
                        <div id="mensajeAnular"></div>
                        <input type="hidden" id="idRecetaAnular">

                        <div class="alert alert-warning">
                            <i class="fas fa-exclamation-triangle"></i>
                            <strong>Atención:</strong> Esta acción marcará la receta como anulada. Solo debe realizarse en casos de error o datos incorrectos.
                        </div>

                        <div class="mb-3">
                            <label class="form-label fw-bold">Motivo de Anulación: <span class="text-danger">*</span></label>
                            <textarea class="form-control" id="txtMotivoAnulacion" rows="4" 
                                      placeholder="Ingrese el motivo detallado de por qué se anula esta receta..." 
                                      required></textarea>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancelar</button>
                        <button type="button" class="btn btn-danger" onclick="confirmarAnulacion()">
                            <i class="fas fa-ban"></i> Confirmar Anulación
                        </button>
                    </div>
                </div>
            </div>
        </div>

        <script src="https://cdnjs.cloudflare.com/ajax/libs/bootstrap/5.3.0/js/bootstrap.bundle.min.js"></script>
        <script>
                            let modalRegistrar = null;
                            let modalEditar = null;
                            let modalAnular = null;
                            let idMascotaActual = null;

                            document.addEventListener('DOMContentLoaded', function () {
                                setCurrentDate();
                                modalRegistrar = new bootstrap.Modal(document.getElementById('modalRegistrar'));
                                modalEditar = new bootstrap.Modal(document.getElementById('modalEditar'));
                                modalAnular = new bootstrap.Modal(document.getElementById('modalAnular'));
                            });

                            function toggleSidebar() {
                                document.getElementById('sidebar').classList.toggle('collapsed');
                            }

                            function setCurrentDate() {
                                const options = {weekday: 'long', year: 'numeric', month: 'long', day: 'numeric'};
                                const date = new Date().toLocaleDateString('es-ES', options);
                                document.getElementById('currentDate').textContent = date.charAt(0).toUpperCase() + date.slice(1);
                            }

                            function buscarMascotas() {
                                const texto = document.getElementById('buscarMascota').value.trim();
                                if (!texto) {
                                    alert('Ingresa un nombre de mascota o cliente');
                                    return;
                                }

                                document.getElementById('contenedorResultados').innerHTML =
                                        '<div class="text-center py-4"><div class="spinner-border text-primary"></div></div>';

                                fetch('RecetaServlet?accion=buscarMascotas&texto=' + encodeURIComponent(texto))
                                        .then(r => r.text())
                                        .then(html => document.getElementById('contenedorResultados').innerHTML = html)
                                        .catch(error => {
                                            document.getElementById('contenedorResultados').innerHTML =
                                                    '<div class="alert alert-danger">Error: ' + error.message + '</div>';
                                        });
                            }

                            function verRecetas(idMascota) {
                                console.log('💊 verRecetas llamado con ID:', idMascota);
                                idMascotaActual = idMascota;

                                document.getElementById('listaRecetas').innerHTML =
                                        '<div class="text-center py-4"><div class="spinner-border text-primary"></div><p class="mt-3">Cargando recetas...</p></div>';

                                document.getElementById('contenedorRecetas').style.display = 'block';
                                document.getElementById('contenedorRecetas').scrollIntoView({behavior: 'smooth', block: 'start'});

                                fetch('RecetaServlet?accion=listarRecetas&idMascota=' + idMascota)
                                        .then(r => r.text())
                                        .then(html => {
                                            document.getElementById('listaRecetas').innerHTML = html;
                                        })
                                        .catch(error => {
                                            document.getElementById('listaRecetas').innerHTML =
                                                    '<div class="alert alert-danger">Error: ' + error.message + '</div>';
                                        });
                            }

                            function cerrarRecetas() {
                                document.getElementById('contenedorRecetas').style.display = 'none';
                                document.getElementById('listaRecetas').innerHTML = '';
                                idMascotaActual = null;
                                window.scrollTo({top: 0, behavior: 'smooth'});
                            }

                            function abrirModalRegistrar() {
                                if (!idMascotaActual) {
                                    alert('Error: No se identificó la mascota');
                                    return;
                                }

                                document.getElementById('mensajeRegistrar').innerHTML = '';
                                document.getElementById('txtMedicamentoRegistrar').value = '';
                                document.getElementById('txtDosisRegistrar').value = '';
                                document.getElementById('txtFrecuenciaRegistrar').value = '';
                                document.getElementById('txtDuracionRegistrar').value = '';
                                document.getElementById('txtIndicacionesRegistrar').value = '';
                                document.getElementById('txtObservacionesRegistrar').value = '';

                                document.getElementById('selectConsultaRegistrar').innerHTML = '<option value="">Cargando consultas...</option>';

                                fetch('RecetaServlet?accion=obtenerConsultasFinalizadas&idMascota=' + idMascotaActual)
                                        .then(r => r.text())
                                        .then(html => {
                                            document.getElementById('selectConsultaRegistrar').innerHTML = html;
                                            modalRegistrar.show();
                                        })
                                        .catch(error => {
                                            alert('Error al cargar consultas: ' + error.message);
                                        });
                            }

                            function guardarRegistro() {
                                const idConsulta = document.getElementById('selectConsultaRegistrar').value;
                                const medicamento = document.getElementById('txtMedicamentoRegistrar').value.trim();
                                const dosis = document.getElementById('txtDosisRegistrar').value.trim();
                                const frecuencia = document.getElementById('txtFrecuenciaRegistrar').value.trim();
                                const duracion = document.getElementById('txtDuracionRegistrar').value.trim();
                                const indicaciones = document.getElementById('txtIndicacionesRegistrar').value.trim();
                                const observaciones = document.getElementById('txtObservacionesRegistrar').value.trim();

                                if (!idConsulta) {
                                    document.getElementById('mensajeRegistrar').innerHTML =
                                            '<div class="alert alert-warning">Debe seleccionar una consulta</div>';
                                    return;
                                }

                                if (!medicamento) {
                                    document.getElementById('mensajeRegistrar').innerHTML =
                                            '<div class="alert alert-warning">El medicamento no puede estar vacío</div>';
                                    return;
                                }

                                if (!dosis) {
                                    document.getElementById('mensajeRegistrar').innerHTML =
                                            '<div class="alert alert-warning">La dosis no puede estar vacía</div>';
                                    return;
                                }

                                if (!frecuencia) {
                                    document.getElementById('mensajeRegistrar').innerHTML =
                                            '<div class="alert alert-warning">La frecuencia no puede estar vacía</div>';
                                    return;
                                }

                                const btnGuardar = document.querySelector('#modalRegistrar .btn-primary-custom');
                                btnGuardar.disabled = true;
                                btnGuardar.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Guardando...';

                                const params = new URLSearchParams();
                                params.append('accion', 'registrarReceta');
                                params.append('idConsulta', idConsulta);
                                params.append('idMascota', idMascotaActual);
                                params.append('medicamento', medicamento);
                                params.append('dosis', dosis);
                                params.append('frecuencia', frecuencia);
                                params.append('duracion', duracion);
                                params.append('indicaciones', indicaciones);
                                params.append('observaciones', observaciones);

                                fetch('RecetaServlet', {
                                    method: 'POST',
                                    headers: {'Content-Type': 'application/x-www-form-urlencoded'},
                                    body: params.toString()
                                })
                                        .then(r => r.text())
                                        .then(respuesta => {
                                            const partes = respuesta.split('|');

                                            if (partes[0] === 'OK') {
                                                document.getElementById('mensajeRegistrar').innerHTML =
                                                        '<div class="alert alert-success">' + partes[1] + '</div>';

                                                setTimeout(() => {
                                                    modalRegistrar.hide();
                                                    verRecetas(idMascotaActual);
                                                }, 1500);
                                            } else {
                                                document.getElementById('mensajeRegistrar').innerHTML =
                                                        '<div class="alert alert-danger">' + partes[1] + '</div>';
                                                btnGuardar.disabled = false;
                                                btnGuardar.innerHTML = '<i class="fas fa-save"></i> Guardar Receta';
                                            }
                                        })
                                        .catch(error => {
                                            document.getElementById('mensajeRegistrar').innerHTML =
                                                    '<div class="alert alert-danger">Error: ' + error.message + '</div>';
                                            btnGuardar.disabled = false;
                                            btnGuardar.innerHTML = '<i class="fas fa-save"></i> Guardar Receta';
                                        });
                            }

                            function abrirModalEditar(idReceta) {
                                document.getElementById('mensajeEditar').innerHTML = '';
                                document.getElementById('idRecetaEditar').value = idReceta;

                                fetch('RecetaServlet?accion=obtenerDetalles&idReceta=' + idReceta)
                                        .then(r => r.text())
                                        .then(respuesta => {
                                            if (respuesta.startsWith('ERROR')) {
                                                alert(respuesta.split('|')[1]);
                                                return;
                                            }

                                            const detalles = JSON.parse(respuesta);
                                            document.getElementById('txtMedicamentoEditar').value = detalles.medicamento;
                                            document.getElementById('txtDosisEditar').value = detalles.dosis;
                                            document.getElementById('txtFrecuenciaEditar').value = detalles.frecuencia;
                                            document.getElementById('txtDuracionEditar').value = detalles.duracion || '';
                                            document.getElementById('txtIndicacionesEditar').value = detalles.indicaciones || '';
                                            document.getElementById('txtObservacionesEditar').value = detalles.observaciones || '';

                                            modalEditar.show();
                                        })
                                        .catch(error => {
                                            alert('Error al cargar datos: ' + error.message);
                                        });
                            }

                            function guardarEdicion() {
                                const idReceta = document.getElementById('idRecetaEditar').value;
                                const medicamento = document.getElementById('txtMedicamentoEditar').value.trim();
                                const dosis = document.getElementById('txtDosisEditar').value.trim();
                                const frecuencia = document.getElementById('txtFrecuenciaEditar').value.trim();
                                const duracion = document.getElementById('txtDuracionEditar').value.trim();
                                const indicaciones = document.getElementById('txtIndicacionesEditar').value.trim();
                                const observaciones = document.getElementById('txtObservacionesEditar').value.trim();

                                if (!medicamento) {
                                    document.getElementById('mensajeEditar').innerHTML =
                                            '<div class="alert alert-warning">El medicamento no puede estar vacío</div>';
                                    return;
                                }

                                if (!dosis) {
                                    document.getElementById('mensajeEditar').innerHTML =
                                            '<div class="alert alert-warning">La dosis no puede estar vacía</div>';
                                    return;
                                }

                                if (!frecuencia) {
                                    document.getElementById('mensajeEditar').innerHTML =
                                            '<div class="alert alert-warning">La frecuencia no puede estar vacía</div>';
                                    return;
                                }

                                const btnGuardar = document.querySelector('#modalEditar .btn-primary-custom');
                                btnGuardar.disabled = true;
                                btnGuardar.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Guardando...';

                                const params = new URLSearchParams();
                                params.append('accion', 'editarReceta');
                                params.append('idReceta', idReceta);
                                params.append('medicamento', medicamento);
                                params.append('dosis', dosis);
                                params.append('frecuencia', frecuencia);
                                params.append('duracion', duracion);
                                params.append('indicaciones', indicaciones);
                                params.append('observaciones', observaciones);

                                fetch('RecetaServlet', {
                                    method: 'POST',
                                    headers: {'Content-Type': 'application/x-www-form-urlencoded'},
                                    body: params.toString()
                                })
                                        .then(r => r.text())
                                        .then(respuesta => {
                                            const partes = respuesta.split('|');

                                            if (partes[0] === 'OK') {
                                                document.getElementById('mensajeEditar').innerHTML =
                                                        '<div class="alert alert-success">' + partes[1] + '</div>';

                                                setTimeout(() => {
                                                    modalEditar.hide();
                                                    verRecetas(idMascotaActual);
                                                }, 1500);
                                            } else {
                                                document.getElementById('mensajeEditar').innerHTML =
                                                        '<div class="alert alert-danger">' + partes[1] + '</div>';
                                                btnGuardar.disabled = false;
                                                btnGuardar.innerHTML = '<i class="fas fa-save"></i> Guardar Cambios';
                                            }
                                        })
                                        .catch(error => {
                                            document.getElementById('mensajeEditar').innerHTML =
                                                    '<div class="alert alert-danger">Error: ' + error.message + '</div>';
                                            btnGuardar.disabled = false;
                                            btnGuardar.innerHTML = '<i class="fas fa-save"></i> Guardar Cambios';
                                        });
                            }

                            function abrirModalAnular(idReceta) {
                                document.getElementById('mensajeAnular').innerHTML = '';
                                document.getElementById('idRecetaAnular').value = idReceta;
                                document.getElementById('txtMotivoAnulacion').value = '';

                                modalAnular.show();
                            }

                            function confirmarAnulacion() {
                                const idReceta = document.getElementById('idRecetaAnular').value;
                                const motivo = document.getElementById('txtMotivoAnulacion').value.trim();

                                if (!motivo) {
                                    document.getElementById('mensajeAnular').innerHTML =
                                            '<div class="alert alert-warning">Debe ingresar el motivo de anulación</div>';
                                    return;
                                }

                                if (!confirm('¿Está seguro que desea anular esta receta?\n\nEsta acción no se puede deshacer.')) {
                                    return;
                                }

                                const btnAnular = document.querySelector('#modalAnular .btn-danger');
                                btnAnular.disabled = true;
                                btnAnular.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Anulando...';

                                const params = new URLSearchParams();
                                params.append('accion', 'anularReceta');
                                params.append('idReceta', idReceta);
                                params.append('motivo', motivo);

                                fetch('RecetaServlet', {
                                    method: 'POST',
                                    headers: {'Content-Type': 'application/x-www-form-urlencoded'},
                                    body: params.toString()
                                })
                                        .then(r => r.text())
                                        .then(respuesta => {
                                            const partes = respuesta.split('|');

                                            if (partes[0] === 'OK') {
                                                document.getElementById('mensajeAnular').innerHTML =
                                                        '<div class="alert alert-success">' + partes[1] + '</div>';

                                                setTimeout(() => {
                                                    modalAnular.hide();
                                                    verRecetas(idMascotaActual);
                                                }, 1500);
                                            } else {
                                                document.getElementById('mensajeAnular').innerHTML =
                                                        '<div class="alert alert-danger">' + partes[1] + '</div>';
                                                btnAnular.disabled = false;
                                                btnAnular.innerHTML = '<i class="fas fa-ban"></i> Confirmar Anulación';
                                            }
                                        })
                                        .catch(error => {
                                            document.getElementById('mensajeAnular').innerHTML =
                                                    '<div class="alert alert-danger">Error: ' + error.message + '</div>';
                                            btnAnular.disabled = false;
                                            btnAnular.innerHTML = '<i class="fas fa-ban"></i> Confirmar Anulación';
                                        });
                            }
        </script>
    </body>
</html>
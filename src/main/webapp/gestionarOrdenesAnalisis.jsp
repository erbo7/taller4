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
        <title>Órdenes de Análisis - DiazPet</title>
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
                    <h2><i class="fas fa-flask"></i> Gestión de Órdenes de Análisis</h2>
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
                                <input type="text" class="form-control" id="txtBuscar" 
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
                                <i class="fas fa-info-circle"></i> Busca una mascota para gestionar sus órdenes de análisis
                            </div>
                        </div>
                    </div>
                </div>

                <!-- SECCIÓN: Lista de Órdenes (oculta por defecto) -->
                <div class="card-custom" id="contenedorOrdenes" style="display: none;">
                    <div class="card-custom-header">
                        <h5><i class="fas fa-file-medical"></i> Órdenes de Análisis de la Mascota</h5>
                        <button class="btn btn-light btn-sm" onclick="cerrarOrdenes()">
                            <i class="fas fa-times"></i> Cerrar
                        </button>
                    </div>
                    <div class="card-custom-body">
                        <div id="tablaOrdenes"></div>
                    </div>
                </div>
            </div>
        </div>

        <!-- Modal Registrar Orden -->
        <div class="modal fade" id="modalRegistrar" tabindex="-1">
            <div class="modal-dialog modal-lg">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title"><i class="fas fa-plus"></i> Registrar Orden de Análisis</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                    </div>
                    <div class="modal-body">
                        <div id="mensajeRegistrar"></div>
                        <input type="hidden" id="idMascotaRegistrar">

                        <div class="mb-3">
                            <label class="form-label fw-bold">Consulta: <span class="text-danger">*</span></label>
                            <select id="selectConsulta" class="form-select" required>
                                <option value="">Cargando...</option>
                            </select>
                        </div>

                        <div class="mb-3">
                            <label class="form-label fw-bold">Tipo de Análisis: <span class="text-danger">*</span></label>
                            <select id="selectTipoAnalisis" class="form-select" required>
                                <option value="">Seleccione un tipo</option>
                            </select>
                        </div>

                        <div class="mb-3">
                            <label class="form-label fw-bold">Motivo / Indicaciones: <span class="text-danger">*</span></label>
                            <textarea id="txtMotivo" class="form-control" rows="3" 
                                      placeholder="Describa el motivo del análisis..." required></textarea>
                        </div>

                        <div class="mb-3">
                            <label class="form-label fw-bold">Observaciones:</label>
                            <textarea id="txtObservaciones" class="form-control" rows="2" 
                                      placeholder="Observaciones adicionales (opcional)"></textarea>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancelar</button>
                        <button type="button" class="btn btn-primary-custom" onclick="guardarOrden()">
                            <i class="fas fa-save"></i> Guardar Orden
                        </button>
                    </div>
                </div>
            </div>
        </div>

        <!-- Modal Editar Orden -->
        <div class="modal fade" id="modalEditar" tabindex="-1">
            <div class="modal-dialog modal-lg">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title"><i class="fas fa-edit"></i> Editar Orden de Análisis</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                    </div>
                    <div class="modal-body">
                        <div id="mensajeEditar"></div>
                        <input type="hidden" id="idOrdenEditar">

                        <div class="mb-3">
                            <label class="form-label fw-bold">Tipo de Análisis: <span class="text-danger">*</span></label>
                            <select id="selectTipoAnalisisEditar" class="form-select" required>
                                <option value="">Seleccione un tipo</option>
                            </select>
                        </div>

                        <div class="mb-3">
                            <label class="form-label fw-bold">Motivo / Indicaciones: <span class="text-danger">*</span></label>
                            <textarea id="txtMotivoEditar" class="form-control" rows="3" required></textarea>
                        </div>

                        <div class="mb-3">
                            <label class="form-label fw-bold">Observaciones:</label>
                            <textarea id="txtObservacionesEditar" class="form-control" rows="2"></textarea>
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

        <!-- Modal Anular Orden -->
        <div class="modal fade" id="modalAnular" tabindex="-1">
            <div class="modal-dialog">
                <div class="modal-content">
                    <div class="modal-header bg-danger text-white">
                        <h5 class="modal-title"><i class="fas fa-ban"></i> Anular Orden</h5>
                        <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
                    </div>
                    <div class="modal-body">
                        <div id="mensajeAnular"></div>
                        <input type="hidden" id="idOrdenAnular">

                        <div class="alert alert-warning">
                            <i class="fas fa-exclamation-triangle"></i>
                            <strong>Atención:</strong> Esta acción no se puede deshacer
                        </div>

                        <div class="mb-3">
                            <label class="form-label fw-bold">Motivo de Anulación: <span class="text-danger">*</span></label>
                            <textarea id="txtMotivoAnulacion" class="form-control" rows="3" 
                                      placeholder="Explique el motivo de la anulación..." required></textarea>
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
                            let idMascotaActual = null;
                            let modalRegistrar, modalEditar, modalAnular;

                            document.addEventListener('DOMContentLoaded', function () {
                                setCurrentDate();
                                modalRegistrar = new bootstrap.Modal(document.getElementById('modalRegistrar'));
                                modalEditar = new bootstrap.Modal(document.getElementById('modalEditar'));
                                modalAnular = new bootstrap.Modal(document.getElementById('modalAnular'));

                                document.getElementById('txtBuscar').addEventListener('keypress', function (e) {
                                    if (e.key === 'Enter') {
                                        buscarMascotas();
                                    }
                                });
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
                                const texto = document.getElementById('txtBuscar').value.trim();

                                if (texto.length < 2) {
                                    alert('Ingrese al menos 2 caracteres para buscar');
                                    return;
                                }

                                document.getElementById('contenedorResultados').innerHTML =
                                        '<div class="text-center py-4"><div class="spinner-border text-primary"></div></div>';

                                fetch('OrdenAnalisisServlet?action=buscarMascotas&texto=' + encodeURIComponent(texto))
                                        .then(r => r.text())
                                        .then(html => document.getElementById('contenedorResultados').innerHTML = html)
                                        .catch(error => {
                                            document.getElementById('contenedorResultados').innerHTML =
                                                    '<div class="alert alert-danger">Error: ' + error.message + '</div>';
                                        });
                            }

                            function verOrdenes(idMascota) {
                                idMascotaActual = idMascota;

                                document.getElementById('tablaOrdenes').innerHTML =
                                        '<div class="text-center py-4"><div class="spinner-border text-primary"></div><p class="mt-3">Cargando órdenes...</p></div>';

                                document.getElementById('contenedorOrdenes').style.display = 'block';
                                document.getElementById('contenedorOrdenes').scrollIntoView({behavior: 'smooth', block: 'start'});

                                fetch('OrdenAnalisisServlet?action=listarOrdenes&idMascota=' + idMascota)
                                        .then(r => r.text())
                                        .then(html => {
                                            document.getElementById('tablaOrdenes').innerHTML = html;
                                        })
                                        .catch(error => {
                                            document.getElementById('tablaOrdenes').innerHTML =
                                                    '<div class="alert alert-danger">Error: ' + error.message + '</div>';
                                        });
                            }

                            function cerrarOrdenes() {
                                document.getElementById('contenedorOrdenes').style.display = 'none';
                                document.getElementById('tablaOrdenes').innerHTML = '';
                                idMascotaActual = null;
                                window.scrollTo({top: 0, behavior: 'smooth'});
                            }

                            function abrirModalRegistrar() {
                                if (!idMascotaActual) {
                                    alert('Primero seleccione una mascota');
                                    return;
                                }

                                document.getElementById('mensajeRegistrar').innerHTML = '';
                                document.getElementById('idMascotaRegistrar').value = idMascotaActual;
                                document.getElementById('txtMotivo').value = '';
                                document.getElementById('txtObservaciones').value = '';

                                fetch('OrdenAnalisisServlet?action=obtenerConsultas&idMascota=' + idMascotaActual)
                                        .then(r => r.text())
                                        .then(html => {
                                            document.getElementById('selectConsulta').innerHTML = html;
                                        });

                                fetch('OrdenAnalisisServlet?action=obtenerTipos')
                                        .then(r => r.text())
                                        .then(html => {
                                            document.getElementById('selectTipoAnalisis').innerHTML = html;
                                        });

                                modalRegistrar.show();
                            }

                            function guardarOrden() {
                                const idMascota = document.getElementById('idMascotaRegistrar').value;
                                const idConsulta = document.getElementById('selectConsulta').value;
                                const idTipoAnalisis = document.getElementById('selectTipoAnalisis').value;
                                const motivo = document.getElementById('txtMotivo').value.trim();
                                const observaciones = document.getElementById('txtObservaciones').value.trim();

                                if (!idConsulta) {
                                    document.getElementById('mensajeRegistrar').innerHTML =
                                            '<div class="alert alert-warning">Debe seleccionar una consulta</div>';
                                    return;
                                }

                                if (!idTipoAnalisis) {
                                    document.getElementById('mensajeRegistrar').innerHTML =
                                            '<div class="alert alert-warning">Debe seleccionar un tipo de análisis</div>';
                                    return;
                                }

                                if (!motivo) {
                                    document.getElementById('mensajeRegistrar').innerHTML =
                                            '<div class="alert alert-warning">El motivo no puede estar vacío</div>';
                                    return;
                                }

                                const btnGuardar = document.querySelector('#modalRegistrar .btn-primary-custom');
                                btnGuardar.disabled = true;
                                btnGuardar.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Guardando...';

                                const params = new URLSearchParams();
                                params.append('action', 'registrar');
                                params.append('idMascota', idMascota);
                                params.append('idConsulta', idConsulta);
                                params.append('idTipoAnalisis', idTipoAnalisis);
                                params.append('motivo', motivo);
                                params.append('observaciones', observaciones);

                                fetch('OrdenAnalisisServlet', {
                                    method: 'POST',
                                    headers: {'Content-Type': 'application/x-www-form-urlencoded'},
                                    body: params.toString()
                                })
                                        .then(r => r.text())
                                        .then(respuesta => {
                                            const partes = respuesta.split('|');

                                            if (partes[0] === 'SUCCESS') {
                                                document.getElementById('mensajeRegistrar').innerHTML =
                                                        '<div class="alert alert-success">' + partes[1] + '</div>';

                                                setTimeout(function () {
                                                    modalRegistrar.hide();
                                                    verOrdenes(idMascotaActual);
                                                }, 1500);
                                            } else {
                                                document.getElementById('mensajeRegistrar').innerHTML =
                                                        '<div class="alert alert-danger">' + partes[1] + '</div>';
                                                btnGuardar.disabled = false;
                                                btnGuardar.innerHTML = '<i class="fas fa-save"></i> Guardar Orden';
                                            }
                                        })
                                        .catch(error => {
                                            document.getElementById('mensajeRegistrar').innerHTML =
                                                    '<div class="alert alert-danger">Error: ' + error.message + '</div>';
                                            btnGuardar.disabled = false;
                                            btnGuardar.innerHTML = '<i class="fas fa-save"></i> Guardar Orden';
                                        });
                            }

                            function abrirModalEditar(idOrden, tipoAnalisis, motivo, observaciones) {
                                document.getElementById('mensajeEditar').innerHTML = '';
                                document.getElementById('idOrdenEditar').value = idOrden;

                                fetch('OrdenAnalisisServlet?action=obtenerTipos')
                                        .then(r => r.text())
                                        .then(html => {
                                            document.getElementById('selectTipoAnalisisEditar').innerHTML = html;

                                            const options = document.querySelectorAll('#selectTipoAnalisisEditar option');
                                            for (let opt of options) {
                                                if (opt.text === tipoAnalisis) {
                                                    opt.selected = true;
                                                    break;
                                                }
                                            }

                                            document.getElementById('txtMotivoEditar').value = motivo || '';
                                            document.getElementById('txtObservacionesEditar').value = observaciones || '';
                                            modalEditar.show();
                                        })
                                        .catch(error => {
                                            alert('Error al cargar datos: ' + error.message);
                                        });
                            }

                            function guardarEdicion() {
                                const idOrden = document.getElementById('idOrdenEditar').value;
                                const idTipoAnalisis = document.getElementById('selectTipoAnalisisEditar').value;
                                const motivo = document.getElementById('txtMotivoEditar').value.trim();
                                const observaciones = document.getElementById('txtObservacionesEditar').value.trim();

                                if (!idTipoAnalisis) {
                                    document.getElementById('mensajeEditar').innerHTML =
                                            '<div class="alert alert-warning">Debe seleccionar un tipo de análisis</div>';
                                    return;
                                }

                                if (!motivo) {
                                    document.getElementById('mensajeEditar').innerHTML =
                                            '<div class="alert alert-warning">El motivo no puede estar vacío</div>';
                                    return;
                                }

                                const btnGuardar = document.querySelector('#modalEditar .btn-primary-custom');
                                btnGuardar.disabled = true;
                                btnGuardar.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Guardando...';

                                const params = new URLSearchParams();
                                params.append('action', 'actualizar');
                                params.append('idOrden', idOrden);
                                params.append('idTipoAnalisis', idTipoAnalisis);
                                params.append('motivo', motivo);
                                params.append('observaciones', observaciones);

                                fetch('OrdenAnalisisServlet', {
                                    method: 'POST',
                                    headers: {'Content-Type': 'application/x-www-form-urlencoded'},
                                    body: params.toString()
                                })
                                        .then(r => r.text())
                                        .then(respuesta => {
                                            const partes = respuesta.split('|');

                                            if (partes[0] === 'SUCCESS') {
                                                document.getElementById('mensajeEditar').innerHTML =
                                                        '<div class="alert alert-success">' + partes[1] + '</div>';

                                                setTimeout(function () {
                                                    modalEditar.hide();
                                                    verOrdenes(idMascotaActual);
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

                            function abrirModalAnular(idOrden) {
                                document.getElementById('mensajeAnular').innerHTML = '';
                                document.getElementById('idOrdenAnular').value = idOrden;
                                document.getElementById('txtMotivoAnulacion').value = '';

                                modalAnular.show();
                            }

                            function confirmarAnulacion() {
                                const idOrden = document.getElementById('idOrdenAnular').value;
                                const motivo = document.getElementById('txtMotivoAnulacion').value.trim();

                                if (!motivo) {
                                    document.getElementById('mensajeAnular').innerHTML =
                                            '<div class="alert alert-warning">Debe ingresar el motivo de anulación</div>';
                                    return;
                                }

                                if (!confirm('¿Está seguro que desea anular esta orden?\n\nEsta acción no se puede deshacer.')) {
                                    return;
                                }

                                const btnAnular = document.querySelector('#modalAnular .btn-danger');
                                btnAnular.disabled = true;
                                btnAnular.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Anulando...';

                                const params = new URLSearchParams();
                                params.append('action', 'anular');
                                params.append('idOrden', idOrden);
                                params.append('motivoAnulacion', motivo);

                                fetch('OrdenAnalisisServlet', {
                                    method: 'POST',
                                    headers: {'Content-Type': 'application/x-www-form-urlencoded'},
                                    body: params.toString()
                                })
                                        .then(r => r.text())
                                        .then(respuesta => {
                                            const partes = respuesta.split('|');

                                            if (partes[0] === 'SUCCESS') {
                                                document.getElementById('mensajeAnular').innerHTML =
                                                        '<div class="alert alert-success">' + partes[1] + '</div>';

                                                setTimeout(function () {
                                                    modalAnular.hide();
                                                    verOrdenes(idMascotaActual);
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

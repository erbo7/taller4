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
        <title>Gestionar Constancias - DiazPet</title>
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
                    <h2><i class="fas fa-file-contract"></i> Gestionar Constancias Veterinarias</h2>
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
                                <i class="fas fa-info-circle"></i> Busca una mascota para gestionar sus constancias
                            </div>
                        </div>
                    </div>
                </div>

                <!-- SECCIÓN: Constancias de la Mascota (oculta por defecto) -->
                <div class="card-custom" id="contenedorConstancias" style="display: none;">
                    <div class="card-custom-header">
                        <h5><i class="fas fa-file-contract"></i> Constancias Emitidas</h5>
                        <div>
                            <button class="btn btn-light btn-sm" onclick="abrirModalRegistrar()">
                                <i class="fas fa-plus"></i> Nueva Constancia
                            </button>
                            <button class="btn btn-light btn-sm" onclick="cerrarConstancias()">
                                <i class="fas fa-times"></i> Cerrar
                            </button>
                        </div>
                    </div>
                    <div class="card-custom-body">
                        <div id="listaConstancias"></div>
                    </div>
                </div>
            </div>
        </div>

        <!-- Modal Registrar Constancia -->
        <div class="modal fade" id="modalRegistrar" tabindex="-1">
            <div class="modal-dialog modal-lg">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title"><i class="fas fa-plus"></i> Emitir Nueva Constancia</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                    </div>
                    <div class="modal-body">
                        <div id="mensajeRegistro"></div>

                        <div class="mb-3">
                            <label class="form-label fw-bold">Consulta Asociada (Opcional):</label>
                            <select class="form-select" id="selectConsultaRegistro">
                                <option value="">Cargando consultas...</option>
                            </select>
                            <small class="text-muted">Puedes dejar en blanco si la constancia no está vinculada a una consulta específica</small>
                        </div>

                        <div class="mb-3">
                            <label class="form-label fw-bold">Tipo de Constancia: <span class="text-danger">*</span></label>
                            <select class="form-select" id="selectTipoConstancia" required>
                                <option value="">Cargando tipos...</option>
                            </select>
                        </div>

                        <div class="mb-3">
                            <label class="form-label fw-bold">Motivo: <span class="text-danger">*</span></label>
                            <input type="text" class="form-control" id="txtMotivo" 
                                   placeholder="Ej: Para viaje al exterior, Para adopción, etc." required>
                        </div>

                        <div class="mb-3">
                            <label class="form-label fw-bold">Descripción / Certificación: <span class="text-danger">*</span></label>
                            <textarea class="form-control" id="txtDescripcion" rows="5" 
                                      placeholder="Describe lo que certifica este documento (estado de salud, tratamientos aplicados, vacunas al día, etc.)" required></textarea>
                            <small class="text-muted">Este texto aparecerá en el cuerpo de la constancia oficial</small>
                        </div>

                        <div class="mb-3">
                            <label class="form-label fw-bold">Observaciones Adicionales:</label>
                            <textarea class="form-control" id="txtObservaciones" rows="2" 
                                      placeholder="Observaciones internas, notas adicionales, etc."></textarea>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancelar</button>
                        <button type="button" class="btn btn-primary-custom" onclick="guardarConstancia()">
                            <i class="fas fa-file-signature"></i> Emitir Constancia
                        </button>
                    </div>
                </div>
            </div>
        </div>

        <!-- Modal Editar Constancia -->
        <div class="modal fade" id="modalEditar" tabindex="-1">
            <div class="modal-dialog modal-lg">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title"><i class="fas fa-edit"></i> Editar Constancia</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                    </div>
                    <div class="modal-body">
                        <div id="mensajeEditar"></div>
                        <input type="hidden" id="idConstanciaEditar">

                        <div class="mb-3">
                            <label class="form-label fw-bold">Tipo de Constancia: <span class="text-danger">*</span></label>
                            <select class="form-select" id="selectTipoConstanciaEditar" required>
                                <option value="">Cargando tipos...</option>
                            </select>
                        </div>

                        <div class="mb-3">
                            <label class="form-label fw-bold">Motivo: <span class="text-danger">*</span></label>
                            <input type="text" class="form-control" id="txtMotivoEditar" required>
                        </div>

                        <div class="mb-3">
                            <label class="form-label fw-bold">Descripción / Certificación: <span class="text-danger">*</span></label>
                            <textarea class="form-control" id="txtDescripcionEditar" rows="5" required></textarea>
                        </div>

                        <div class="mb-3">
                            <label class="form-label fw-bold">Observaciones Adicionales:</label>
                            <textarea class="form-control" id="txtObservacionesEditar" rows="2"></textarea>
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

        <!-- Modal Anular Constancia -->
        <div class="modal fade" id="modalAnular" tabindex="-1">
            <div class="modal-dialog">
                <div class="modal-content">
                    <div class="modal-header bg-danger text-white">
                        <h5 class="modal-title"><i class="fas fa-ban"></i> Anular Constancia</h5>
                        <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
                    </div>
                    <div class="modal-body">
                        <div id="mensajeAnular"></div>
                        <input type="hidden" id="idConstanciaAnular">
                        <div class="alert alert-warning">
                            <i class="fas fa-exclamation-triangle"></i>
                            <strong>Atención:</strong> Esta acción anulará la constancia. Solo debe realizarse en casos justificados.
                        </div>
                        <div class="mb-3">
                            <label class="form-label fw-bold">Motivo de Anulación: <span class="text-danger">*</span></label>
                            <textarea class="form-control" id="txtMotivoAnulacion" rows="3" 
                                      placeholder="Ingrese el motivo por el cual se anula esta constancia (Ej: Error en datos, Emitida por error, Información incorrecta, etc.)" 
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

        <!-- Modal Ver Detalle -->
        <div class="modal fade" id="modalDetalle" tabindex="-1">
            <div class="modal-dialog modal-lg">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title"><i class="fas fa-eye"></i> Vista Previa de Constancia</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                    </div>
                    <div class="modal-body" id="contenidoDetalle" style="min-height: 400px;">
                        <!-- Aquí se cargará el detalle -->
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cerrar</button>
                        <button type="button" class="btn btn-primary-custom" onclick="imprimirConstancia()">
                            <i class="fas fa-print"></i> Imprimir
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
                            let modalDetalle = null;
                            let idMascotaActual = null;

                            document.addEventListener('DOMContentLoaded', function () {
                                setCurrentDate();
                                modalRegistrar = new bootstrap.Modal(document.getElementById('modalRegistrar'));
                                modalEditar = new bootstrap.Modal(document.getElementById('modalEditar'));
                                modalAnular = new bootstrap.Modal(document.getElementById('modalAnular'));
                                modalDetalle = new bootstrap.Modal(document.getElementById('modalDetalle'));
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

                                fetch('ConstanciaServlet?accion=buscarMascotas&texto=' + encodeURIComponent(texto))
                                        .then(r => r.text())
                                        .then(html => document.getElementById('contenedorResultados').innerHTML = html);
                            }

                            function verConstancias(idMascota) {
                                idMascotaActual = idMascota;

                                document.getElementById('listaConstancias').innerHTML =
                                        '<div class="text-center py-4"><div class="spinner-border text-primary"></div></div>';

                                document.getElementById('contenedorConstancias').style.display = 'block';

                                document.getElementById('contenedorConstancias').scrollIntoView({behavior: 'smooth', block: 'start'});

                                fetch('ConstanciaServlet?accion=verConstancias&idMascota=' + idMascota)
                                        .then(r => r.text())
                                        .then(html => document.getElementById('listaConstancias').innerHTML = html);
                            }

                            function cerrarConstancias() {
                                document.getElementById('contenedorConstancias').style.display = 'none';
                                document.getElementById('listaConstancias').innerHTML = '';
                                idMascotaActual = null;
                            }

                            function abrirModalRegistrar() {
                                if (!idMascotaActual) {
                                    alert('Selecciona una mascota primero');
                                    return;
                                }

                                document.getElementById('mensajeRegistro').innerHTML = '';
                                document.getElementById('txtMotivo').value = '';
                                document.getElementById('txtDescripcion').value = '';
                                document.getElementById('txtObservaciones').value = '';

                                fetch('ConstanciaServlet?accion=cargarConsultas&idMascota=' + idMascotaActual)
                                        .then(r => r.text())
                                        .then(html => {
                                            document.getElementById('selectConsultaRegistro').innerHTML = html;
                                        });

                                fetch('ConstanciaServlet?accion=cargarTiposConstancia')
                                        .then(r => r.text())
                                        .then(html => {
                                            document.getElementById('selectTipoConstancia').innerHTML = html;
                                            modalRegistrar.show();
                                        });
                            }

                            function guardarConstancia() {
                                const idConsulta = document.getElementById('selectConsultaRegistro').value;
                                const idTipoConstancia = document.getElementById('selectTipoConstancia').value;
                                const motivo = document.getElementById('txtMotivo').value.trim();
                                const descripcion = document.getElementById('txtDescripcion').value.trim();
                                const observaciones = document.getElementById('txtObservaciones').value.trim();

                                if (!idTipoConstancia) {
                                    document.getElementById('mensajeRegistro').innerHTML =
                                            '<div class="alert alert-warning">Debe seleccionar un tipo de constancia</div>';
                                    return;
                                }

                                if (!motivo) {
                                    document.getElementById('mensajeRegistro').innerHTML =
                                            '<div class="alert alert-warning">El motivo es obligatorio</div>';
                                    return;
                                }

                                if (!descripcion) {
                                    document.getElementById('mensajeRegistro').innerHTML =
                                            '<div class="alert alert-warning">La descripción es obligatoria</div>';
                                    return;
                                }

                                const btnGuardar = document.querySelector('#modalRegistrar .btn-primary-custom');
                                btnGuardar.disabled = true;
                                btnGuardar.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Guardando...';

                                const params = new URLSearchParams();
                                params.append('accion', 'registrar');
                                params.append('idConsulta', idConsulta);
                                params.append('idMascota', idMascotaActual);
                                params.append('idTipoConstancia', idTipoConstancia);
                                params.append('motivo', motivo);
                                params.append('descripcion', descripcion);
                                params.append('observaciones', observaciones);

                                fetch('ConstanciaServlet', {
                                    method: 'POST',
                                    headers: {'Content-Type': 'application/x-www-form-urlencoded'},
                                    body: params.toString()
                                })
                                        .then(r => r.text())
                                        .then(respuesta => {
                                            const partes = respuesta.split('|');

                                            if (partes[0] === 'OK') {
                                                document.getElementById('mensajeRegistro').innerHTML =
                                                        '<div class="alert alert-success">' + partes[1] + '</div>';

                                                setTimeout(() => {
                                                    modalRegistrar.hide();
                                                    verConstancias(idMascotaActual);
                                                }, 1500);
                                            } else {
                                                document.getElementById('mensajeRegistro').innerHTML =
                                                        '<div class="alert alert-danger">' + partes[1] + '</div>';
                                                btnGuardar.disabled = false;
                                                btnGuardar.innerHTML = '<i class="fas fa-file-signature"></i> Emitir Constancia';
                                            }
                                        });
                            }

                            function abrirModalEditar(idConstancia) {
                                document.getElementById('mensajeEditar').innerHTML = '';
                                document.getElementById('idConstanciaEditar').value = idConstancia;

                                fetch('ConstanciaServlet?accion=cargarTiposConstancia')
                                        .then(r => r.text())
                                        .then(html => {
                                            document.getElementById('selectTipoConstanciaEditar').innerHTML = html;

                                            return fetch('ConstanciaServlet?accion=obtenerDetalles&idConstancia=' + idConstancia);
                                        })
                                        .then(r => r.text())
                                        .then(respuesta => {
                                            const datos = respuesta.split('|');
                                            document.getElementById('selectTipoConstanciaEditar').value = datos[0] || '';
                                            document.getElementById('txtMotivoEditar').value = datos[1] || '';
                                            document.getElementById('txtDescripcionEditar').value = datos[2] || '';
                                            document.getElementById('txtObservacionesEditar').value = datos[3] || '';
                                            modalEditar.show();
                                        });
                            }

                            function guardarEdicion() {
                                const idConstancia = document.getElementById('idConstanciaEditar').value;
                                const idTipoConstancia = document.getElementById('selectTipoConstanciaEditar').value;
                                const motivo = document.getElementById('txtMotivoEditar').value.trim();
                                const descripcion = document.getElementById('txtDescripcionEditar').value.trim();
                                const observaciones = document.getElementById('txtObservacionesEditar').value.trim();

                                if (!idTipoConstancia) {
                                    document.getElementById('mensajeEditar').innerHTML =
                                            '<div class="alert alert-warning">Debe seleccionar un tipo de constancia</div>';
                                    return;
                                }

                                if (!motivo) {
                                    document.getElementById('mensajeEditar').innerHTML =
                                            '<div class="alert alert-warning">El motivo es obligatorio</div>';
                                    return;
                                }

                                if (!descripcion) {
                                    document.getElementById('mensajeEditar').innerHTML =
                                            '<div class="alert alert-warning">La descripción es obligatoria</div>';
                                    return;
                                }

                                const btnGuardar = document.querySelector('#modalEditar .btn-primary-custom');
                                btnGuardar.disabled = true;
                                btnGuardar.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Guardando...';

                                const params = new URLSearchParams();
                                params.append('accion', 'actualizar');
                                params.append('idConstancia', idConstancia);
                                params.append('idTipoConstancia', idTipoConstancia);
                                params.append('motivo', motivo);
                                params.append('descripcion', descripcion);
                                params.append('observaciones', observaciones);

                                fetch('ConstanciaServlet', {
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
                                                    verConstancias(idMascotaActual);
                                                }, 1500);
                                            } else {
                                                document.getElementById('mensajeEditar').innerHTML =
                                                        '<div class="alert alert-danger">' + partes[1] + '</div>';
                                                btnGuardar.disabled = false;
                                                btnGuardar.innerHTML = '<i class="fas fa-save"></i> Guardar Cambios';
                                            }
                                        });
                            }

                            function abrirModalAnular(idConstancia) {
                                document.getElementById('mensajeAnular').innerHTML = '';
                                document.getElementById('idConstanciaAnular').value = idConstancia;
                                document.getElementById('txtMotivoAnulacion').value = '';
                                modalAnular.show();
                            }

                            function confirmarAnulacion() {
                                const idConstancia = document.getElementById('idConstanciaAnular').value;
                                const motivo = document.getElementById('txtMotivoAnulacion').value.trim();

                                if (!motivo) {
                                    document.getElementById('mensajeAnular').innerHTML =
                                            '<div class="alert alert-warning">Debe ingresar el motivo de anulación</div>';
                                    return;
                                }

                                const btnAnular = document.querySelector('#modalAnular .btn-danger');
                                btnAnular.disabled = true;
                                btnAnular.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Anulando...';

                                const params = new URLSearchParams();
                                params.append('accion', 'anular');
                                params.append('idConstancia', idConstancia);
                                params.append('motivo', motivo);

                                fetch('ConstanciaServlet', {
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
                                                    verConstancias(idMascotaActual);
                                                }, 1500);
                                            } else {
                                                document.getElementById('mensajeAnular').innerHTML =
                                                        '<div class="alert alert-danger">' + partes[1] + '</div>';
                                                btnAnular.disabled = false;
                                                btnAnular.innerHTML = '<i class="fas fa-ban"></i> Confirmar Anulación';
                                            }
                                        });
                            }

                            function verDetalleConstancia(idConstancia) {
                                document.getElementById('contenidoDetalle').innerHTML =
                                        '<div class="text-center py-5"><div class="spinner-border text-primary"></div><p class="mt-3">Cargando vista previa...</p></div>';

                                modalDetalle.show();

                                // Primero obtenemos los datos de la constancia
                                fetch('ConstanciaServlet?accion=obtenerDetalles&idConstancia=' + idConstancia)
                                        .then(r => r.text())
                                        .then(respuesta => {
                                            const datos = respuesta.split('|');
                                            const idTipoConstancia = datos[0];
                                            const motivo = datos[1];
                                            const descripcion = datos[2];
                                            const observaciones = datos[3];

                                            // Ahora obtenemos el nombre del tipo
                                            fetch('ConstanciaServlet?accion=cargarTiposConstancia')
                                                    .then(r => r.text())
                                                    .then(htmlTipos => {
                                                        // Parseamos el HTML para extraer el nombre del tipo
                                                        const tempDiv = document.createElement('div');
                                                        tempDiv.innerHTML = '<select>' + htmlTipos + '</select>';
                                                        const options = tempDiv.querySelectorAll('option');

                                                        let nombreTipo = 'Constancia General';
                                                        options.forEach(opt => {
                                                            if (opt.value == idTipoConstancia) {
                                                                nombreTipo = opt.textContent;
                                                            }
                                                        });

                                                        // Generamos la constancia mejorada
                                                        const fecha = new Date().toLocaleDateString('es-ES', {
                                                            day: '2-digit',
                                                            month: 'long',
                                                            year: 'numeric'
                                                        });

                                                        let html = '<div style="max-width: 800px; margin: 0 auto; border: 3px solid #10b981; padding: 40px; border-radius: 15px; background: #ffffff; box-shadow: 0 4px 20px rgba(0,0,0,0.1);">';

                                                        // ENCABEZADO
                                                        html += '<div style="text-align: center; margin-bottom: 40px; border-bottom: 3px solid #10b981; padding-bottom: 20px;">';
                                                        html += '<div style="display: inline-block; padding: 15px 30px; background: linear-gradient(135deg, #10b981 0%, #059669 100%); border-radius: 10px; margin-bottom: 15px;">';
                                                        html += '<h2 style="color: white; margin: 0; font-size: 28px; letter-spacing: 2px;">🩺 CONSTANCIA VETERINARIA</h2>';
                                                        html += '</div>';
                                                        html += '<h4 style="color: #059669; margin: 10px 0 5px 0; font-weight: bold;">DiazPet - Clínica Veterinaria</h4>';
                                                        html += '<p style="margin: 0; color: #666; font-size: 14px;">Reg. Prof. N° 12345 - Mat. N° ABCD-6789</p>';
                                                        html += '<p style="margin: 5px 0 0 0; color: #666; font-size: 14px;">📍 Dirección de la Clínica | ☎️ (021) 123-4567</p>';
                                                        html += '</div>';

                                                        // NÚMERO Y FECHA
                                                        html += '<div style="text-align: right; margin-bottom: 30px; padding: 10px; background: #f8f9fa; border-left: 4px solid #10b981; border-radius: 5px;">';
                                                        html += '<p style="margin: 0; font-size: 14px;"><strong>Constancia N°:</strong> ' + String(idConstancia).padStart(6, '0') + '</p>';
                                                        html += '<p style="margin: 5px 0 0 0; font-size: 14px;"><strong>Fecha de Emisión:</strong> ' + fecha + '</p>';
                                                        html += '</div>';

                                                        // TIPO Y MOTIVO
                                                        html += '<div style="margin: 30px 0; padding: 25px; background: linear-gradient(135deg, #f0fdf4 0%, #dcfce7 100%); border-radius: 10px; border: 2px solid #10b981;">';
                                                        html += '<p style="margin: 0 0 15px 0; font-size: 18px; color: #059669; font-weight: bold;">📋 ' + nombreTipo + '</p>';
                                                        html += '<p style="margin: 0; font-size: 15px;"><strong style="color: #059669;">Motivo:</strong> ' + motivo + '</p>';
                                                        html += '</div>';

                                                        // CUERPO DE LA CONSTANCIA
                                                        html += '<div style="margin: 30px 0; padding: 30px; background: white; border: 2px dashed #10b981; border-radius: 10px; min-height: 200px;">';
                                                        html += '<p style="text-align: center; margin: 0 0 20px 0; font-size: 16px; font-weight: bold; color: #059669; text-transform: uppercase;">Por medio de la presente se certifica:</p>';
                                                        html += '<div style="text-align: justify; line-height: 2; font-size: 15px; color: #2c3e50;">';
                                                        html += '<p style="text-indent: 40px; margin: 0;">' + descripcion.replace(/\n/g, '</p><p style="text-indent: 40px; margin: 15px 0;">') + '</p>';
                                                        html += '</div>';
                                                        html += '</div>';

                                                        // OBSERVACIONES (si existen)
                                                        if (observaciones && observaciones !== 'null' && observaciones.trim() !== '') {
                                                            html += '<div style="margin: 25px 0; padding: 20px; background: #fff3cd; border-left: 5px solid #ffc107; border-radius: 5px;">';
                                                            html += '<p style="margin: 0 0 10px 0; font-weight: bold; color: #856404;"><i class="fas fa-exclamation-circle"></i> Observaciones Adicionales:</p>';
                                                            html += '<p style="margin: 0; color: #856404; font-size: 14px;">' + observaciones + '</p>';
                                                            html += '</div>';
                                                        }

                                                        // PIE DE PÁGINA
                                                        html += '<div style="margin-top: 50px; padding-top: 30px; border-top: 2px solid #e5e7eb;">';
                                                        html += '<p style="margin: 0 0 10px 0; font-size: 14px; color: #666; text-align: center;">Se extiende la presente constancia a solicitud del interesado para los fines que estime convenientes.</p>';
                                                        html += '</div>';

                                                        // FIRMA
                                                        html += '<div style="margin-top: 60px; text-align: center;">';
                                                        html += '<div style="display: inline-block; text-align: center; min-width: 300px;">';
                                                        html += '<div style="border-bottom: 2px solid #2c3e50; margin-bottom: 10px; padding-bottom: 40px;"></div>';
                                                        html += '<p style="margin: 5px 0; font-weight: bold; font-size: 16px; color: #2c3e50;">Dr./Dra. <%= nombre%></p>';
                                                        html += '<p style="margin: 5px 0; color: #666; font-size: 14px;">Médico Veterinario</p>';
                                                        html += '<p style="margin: 5px 0; color: #666; font-size: 14px;">Mat. Prof. N° XXXX</p>';
                                                        html += '<div style="margin-top: 15px; padding: 10px; border: 2px dashed #059669; border-radius: 5px; background: #f0fdf4;">';
                                                        html += '<p style="margin: 0; font-size: 12px; color: #059669; font-weight: bold;">🔒 FIRMA Y SELLO</p>';
                                                        html += '</div>';
                                                        html += '</div>';
                                                        html += '</div>';

                                                        // FOOTER
                                                        html += '<div style="margin-top: 40px; padding-top: 20px; border-top: 2px solid #10b981; text-align: center;">';
                                                        html += '<p style="margin: 0; font-size: 11px; color: #999;">Este documento tiene validez legal y es verificable bajo el código N° ' + String(idConstancia).padStart(6, '0') + '</p>';
                                                        html += '<p style="margin: 5px 0 0 0; font-size: 11px; color: #999;">Para consultas: info@diazpet.com | www.diazpet.com</p>';
                                                        html += '</div>';

                                                        html += '</div>';

                                                        document.getElementById('contenidoDetalle').innerHTML = html;
                                                    });
                                        })
                                        .catch(error => {
                                            document.getElementById('contenidoDetalle').innerHTML =
                                                    '<div class="alert alert-danger">Error al cargar la vista previa</div>';
                                        });
                            }

                            function imprimirConstancia() {
                                const contenido = document.getElementById('contenidoDetalle').innerHTML;
                                const ventana = window.open('', '_blank');
                                ventana.document.write('<html><head><title>Constancia Veterinaria</title>');
                                ventana.document.write('<style>body{font-family: Arial, sans-serif; padding: 20px;}</style>');
                                ventana.document.write('</head><body>');
                                ventana.document.write(contenido);
                                ventana.document.write('</body></html>');
                                ventana.document.close();

                                setTimeout(function () {
                                    ventana.print();
                                }, 500);
                            }
        </script>
    </body>
</html>
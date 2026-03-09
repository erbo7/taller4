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
        <title>Gestionar Tratamientos - DiazPet</title>
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
                    <h2><i class="fas fa-notes-medical"></i> Gestionar Tratamientos y Evolución</h2>
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
                                <i class="fas fa-info-circle"></i> Busca una mascota para gestionar sus tratamientos
                            </div>
                        </div>
                    </div>
                </div>

                <!-- SECCIÓN: Tratamientos de la Mascota (oculta por defecto) -->
                <div class="card-custom" id="contenedorTratamientos" style="display: none;">
                    <div class="card-custom-header">
                        <h5><i class="fas fa-notes-medical"></i> Tratamientos Registrados</h5>
                        <div>
                            <button class="btn btn-light btn-sm" onclick="abrirModalRegistrar()">
                                <i class="fas fa-plus"></i> Nuevo Tratamiento
                            </button>
                            <button class="btn btn-light btn-sm" onclick="cerrarTratamientos()">
                                <i class="fas fa-times"></i> Cerrar
                            </button>
                        </div>
                    </div>
                    <div class="card-custom-body">
                        <div id="listaTratamientos"></div>
                    </div>
                </div>
            </div>
        </div>

        <!-- Modal Registrar Tratamiento -->
        <div class="modal fade" id="modalRegistrar" tabindex="-1">
            <div class="modal-dialog modal-lg">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title"><i class="fas fa-plus"></i> Registrar Nuevo Tratamiento</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                    </div>
                    <div class="modal-body">
                        <div id="mensajeRegistro"></div>

                        <div class="mb-3">
                            <label class="form-label fw-bold">Consulta Asociada: <span class="text-danger">*</span></label>
                            <select class="form-select" id="selectConsultaRegistro" required>
                                <option value="">Cargando consultas...</option>
                            </select>
                            <small class="text-muted">Selecciona la consulta a la que corresponde este tratamiento</small>
                        </div>

                        <div class="row mb-3">
                            <div class="col-md-4">
                                <label class="form-label fw-bold">Fecha Inicio:</label>
                                <input type="date" class="form-control" id="txtFechaInicio">
                            </div>
                            <div class="col-md-4">
                                <label class="form-label fw-bold">Fecha Fin Estimada:</label>
                                <input type="date" class="form-control" id="txtFechaFin">
                            </div>
                            <div class="col-md-4">
                                <label class="form-label fw-bold">Fecha Control:</label>
                                <input type="date" class="form-control" id="txtFechaControl">
                            </div>
                        </div>

                        <div class="mb-3">
                            <label class="form-label fw-bold">Plan Terapéutico: <span class="text-danger">*</span></label>
                            <textarea class="form-control" id="txtPlanTerapeutico" rows="4" 
                                      placeholder="Describe el plan de tratamiento (medicamentos, terapias, cuidados especiales, etc.)" required></textarea>
                        </div>

                        <div class="mb-3">
                            <label class="form-label fw-bold">Evolución Esperada:</label>
                            <textarea class="form-control" id="txtEvolucion" rows="3" 
                                      placeholder="Describe la evolución esperada del paciente (mejoras esperadas, signos de recuperación, etc.)"></textarea>
                        </div>

                        <div class="mb-3">
                            <label class="form-label fw-bold">Observaciones:</label>
                            <textarea class="form-control" id="txtObservaciones" rows="2" 
                                      placeholder="Observaciones adicionales, recomendaciones especiales, contraindicaciones, etc."></textarea>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancelar</button>
                        <button type="button" class="btn btn-primary-custom" onclick="guardarTratamiento()">
                            <i class="fas fa-save"></i> Guardar Tratamiento
                        </button>
                    </div>
                </div>
            </div>
        </div>

        <!-- Modal Editar Tratamiento -->
        <div class="modal fade" id="modalEditar" tabindex="-1">
            <div class="modal-dialog modal-lg">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title"><i class="fas fa-edit"></i> Editar Tratamiento</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                    </div>
                    <div class="modal-body">
                        <div id="mensajeEditar"></div>
                        <input type="hidden" id="idTratamientoEditar">

                        <div class="row mb-3">
                            <div class="col-md-4">
                                <label class="form-label fw-bold">Fecha Inicio:</label>
                                <input type="date" class="form-control" id="txtFechaInicioEditar">
                            </div>
                            <div class="col-md-4">
                                <label class="form-label fw-bold">Fecha Fin Estimada:</label>
                                <input type="date" class="form-control" id="txtFechaFinEditar">
                            </div>
                            <div class="col-md-4">
                                <label class="form-label fw-bold">Fecha Control:</label>
                                <input type="date" class="form-control" id="txtFechaControlEditar">
                            </div>
                        </div>

                        <div class="mb-3">
                            <label class="form-label fw-bold">Plan Terapéutico: <span class="text-danger">*</span></label>
                            <textarea class="form-control" id="txtPlanTerapeuticoEditar" rows="4" required></textarea>
                        </div>

                        <div class="mb-3">
                            <label class="form-label fw-bold">Evolución del Paciente:</label>
                            <textarea class="form-control" id="txtEvolucionEditar" rows="3" 
                                      placeholder="Actualiza la evolución del paciente (mejoras observadas, complicaciones, respuesta al tratamiento, etc.)"></textarea>
                        </div>

                        <div class="mb-3">
                            <label class="form-label fw-bold">Observaciones:</label>
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

        <!-- Modal Anular Tratamiento -->
        <div class="modal fade" id="modalAnular" tabindex="-1">
            <div class="modal-dialog">
                <div class="modal-content">
                    <div class="modal-header bg-danger text-white">
                        <h5 class="modal-title"><i class="fas fa-ban"></i> Anular Tratamiento</h5>
                        <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
                    </div>
                    <div class="modal-body">
                        <div id="mensajeAnular"></div>
                        <input type="hidden" id="idTratamientoAnular">
                        <div class="alert alert-warning">
                            <i class="fas fa-exclamation-triangle"></i>
                            <strong>Atención:</strong> Esta acción anulará el tratamiento. Solo debe realizarse en casos justificados.
                        </div>
                        <div class="mb-3">
                            <label class="form-label fw-bold">Motivo de Anulación: <span class="text-danger">*</span></label>
                            <textarea class="form-control" id="txtMotivoAnulacion" rows="3" 
                                      placeholder="Ingrese el motivo por el cual se anula este tratamiento (Ej: Tratamiento completado, Cambio de plan terapéutico, Error de registro, etc.)" 
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

                                fetch('TratamientoServlet?accion=buscarMascotas&texto=' + encodeURIComponent(texto))
                                        .then(r => r.text())
                                        .then(html => document.getElementById('contenedorResultados').innerHTML = html);
                            }

                            function verTratamientos(idMascota) {
                                idMascotaActual = idMascota;

                                document.getElementById('listaTratamientos').innerHTML =
                                        '<div class="text-center py-4"><div class="spinner-border text-primary"></div></div>';

                                document.getElementById('contenedorTratamientos').style.display = 'block';

                                document.getElementById('contenedorTratamientos').scrollIntoView({behavior: 'smooth', block: 'start'});

                                fetch('TratamientoServlet?accion=verTratamientos&idMascota=' + idMascota)
                                        .then(r => r.text())
                                        .then(html => document.getElementById('listaTratamientos').innerHTML = html);
                            }

                            function cerrarTratamientos() {
                                document.getElementById('contenedorTratamientos').style.display = 'none';
                                document.getElementById('listaTratamientos').innerHTML = '';
                                idMascotaActual = null;
                            }

                            function abrirModalRegistrar() {
                                if (!idMascotaActual) {
                                    alert('Selecciona una mascota primero');
                                    return;
                                }

                                document.getElementById('mensajeRegistro').innerHTML = '';
                                document.getElementById('txtFechaInicio').value = '';
                                document.getElementById('txtFechaFin').value = '';
                                document.getElementById('txtPlanTerapeutico').value = '';
                                document.getElementById('txtEvolucion').value = '';
                                document.getElementById('txtFechaControl').value = '';
                                document.getElementById('txtObservaciones').value = '';

                                fetch('TratamientoServlet?accion=cargarConsultas&idMascota=' + idMascotaActual)
                                        .then(r => r.text())
                                        .then(html => {
                                            document.getElementById('selectConsultaRegistro').innerHTML = html;
                                            modalRegistrar.show();
                                        });
                            }

                            function guardarTratamiento() {
                                const idConsulta = document.getElementById('selectConsultaRegistro').value;
                                const fechaInicio = document.getElementById('txtFechaInicio').value;
                                const fechaFin = document.getElementById('txtFechaFin').value;
                                const planTerapeutico = document.getElementById('txtPlanTerapeutico').value.trim();
                                const evolucion = document.getElementById('txtEvolucion').value.trim();
                                const fechaControl = document.getElementById('txtFechaControl').value;
                                const observaciones = document.getElementById('txtObservaciones').value.trim();

                                if (!idConsulta) {
                                    document.getElementById('mensajeRegistro').innerHTML =
                                            '<div class="alert alert-warning">Debe seleccionar una consulta</div>';
                                    return;
                                }

                                if (!planTerapeutico) {
                                    document.getElementById('mensajeRegistro').innerHTML =
                                            '<div class="alert alert-warning">El plan terapéutico es obligatorio</div>';
                                    return;
                                }

                                const btnGuardar = document.querySelector('#modalRegistrar .btn-primary-custom');
                                btnGuardar.disabled = true;
                                btnGuardar.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Guardando...';

                                const params = new URLSearchParams();
                                params.append('accion', 'registrar');
                                params.append('idConsulta', idConsulta);
                                params.append('idMascota', idMascotaActual);
                                params.append('fechaInicio', fechaInicio);
                                params.append('fechaFin', fechaFin);
                                params.append('planTerapeutico', planTerapeutico);
                                params.append('evolucion', evolucion);
                                params.append('fechaControl', fechaControl);
                                params.append('observaciones', observaciones);

                                fetch('TratamientoServlet', {
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
                                                    verTratamientos(idMascotaActual);
                                                }, 1500);
                                            } else {
                                                document.getElementById('mensajeRegistro').innerHTML =
                                                        '<div class="alert alert-danger">' + partes[1] + '</div>';
                                                btnGuardar.disabled = false;
                                                btnGuardar.innerHTML = '<i class="fas fa-save"></i> Guardar Tratamiento';
                                            }
                                        });
                            }

                            function abrirModalEditar(idTratamiento) {
                                document.getElementById('mensajeEditar').innerHTML = '';
                                document.getElementById('idTratamientoEditar').value = idTratamiento;

                                fetch('TratamientoServlet?accion=obtenerDetalles&idTratamiento=' + idTratamiento)
                                        .then(r => r.text())
                                        .then(respuesta => {
                                            const datos = respuesta.split('|');
                                            document.getElementById('txtFechaInicioEditar').value = datos[0] || '';
                                            document.getElementById('txtFechaFinEditar').value = datos[1] || '';
                                            document.getElementById('txtPlanTerapeuticoEditar').value = datos[2] || '';
                                            document.getElementById('txtEvolucionEditar').value = datos[3] || '';
                                            document.getElementById('txtFechaControlEditar').value = datos[4] || '';
                                            document.getElementById('txtObservacionesEditar').value = datos[5] || '';
                                            modalEditar.show();
                                        });
                            }

                            function guardarEdicion() {
                                const idTratamiento = document.getElementById('idTratamientoEditar').value;
                                const fechaInicio = document.getElementById('txtFechaInicioEditar').value;
                                const fechaFin = document.getElementById('txtFechaFinEditar').value;
                                const planTerapeutico = document.getElementById('txtPlanTerapeuticoEditar').value.trim();
                                const evolucion = document.getElementById('txtEvolucionEditar').value.trim();
                                const fechaControl = document.getElementById('txtFechaControlEditar').value;
                                const observaciones = document.getElementById('txtObservacionesEditar').value.trim();

                                if (!planTerapeutico) {
                                    document.getElementById('mensajeEditar').innerHTML =
                                            '<div class="alert alert-warning">El plan terapéutico es obligatorio</div>';
                                    return;
                                }

                                const btnGuardar = document.querySelector('#modalEditar .btn-primary-custom');
                                btnGuardar.disabled = true;
                                btnGuardar.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Guardando...';

                                const params = new URLSearchParams();
                                params.append('accion', 'actualizar');
                                params.append('idTratamiento', idTratamiento);
                                params.append('fechaInicio', fechaInicio);
                                params.append('fechaFin', fechaFin);
                                params.append('planTerapeutico', planTerapeutico);
                                params.append('evolucion', evolucion);
                                params.append('fechaControl', fechaControl);
                                params.append('observaciones', observaciones);

                                fetch('TratamientoServlet', {
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
                                                    verTratamientos(idMascotaActual);
                                                }, 1500);
                                            } else {
                                                document.getElementById('mensajeEditar').innerHTML =
                                                        '<div class="alert alert-danger">' + partes[1] + '</div>';
                                                btnGuardar.disabled = false;
                                                btnGuardar.innerHTML = '<i class="fas fa-save"></i> Guardar Cambios';
                                            }
                                        });
                            }

                            function abrirModalAnular(idTratamiento) {
                                document.getElementById('mensajeAnular').innerHTML = '';
                                document.getElementById('idTratamientoAnular').value = idTratamiento;
                                document.getElementById('txtMotivoAnulacion').value = '';
                                modalAnular.show();
                            }

                            function confirmarAnulacion() {
                                const idTratamiento = document.getElementById('idTratamientoAnular').value;
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
                                params.append('idTratamiento', idTratamiento);
                                params.append('motivo', motivo);

                                fetch('TratamientoServlet', {
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
                                                    verTratamientos(idMascotaActual);
                                                }, 1500);
                                            } else {
                                                document.getElementById('mensajeAnular').innerHTML =
                                                        '<div class="alert alert-danger">' + partes[1] + '</div>';
                                                btnAnular.disabled = false;
                                                btnAnular.innerHTML = '<i class="fas fa-ban"></i> Confirmar Anulación';
                                            }
                                        });
                            }
        </script>
    </body>
</html>
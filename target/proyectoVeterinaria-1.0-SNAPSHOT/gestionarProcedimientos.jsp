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
        <title>Procedimientos - DiazPet</title>
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
            .insumo-item {
                background: #f8f9fa;
                border: 1px solid #dee2e6;
                border-radius: 8px;
                padding: 12px;
                margin-bottom: 10px;
                display: flex;
                justify-content: space-between;
                align-items: center;
            }
            .insumo-info {
                flex: 1;
            }
            .insumo-info strong {
                color: #2c3e50;
                display: block;
            }
            .insumo-info small {
                color: #6c757d;
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
                    <h2><i class="fas fa-syringe"></i> Gestionar Procedimientos</h2>
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
                                <i class="fas fa-info-circle"></i> Busca una mascota para gestionar sus procedimientos
                            </div>
                        </div>
                    </div>
                </div>

                <!-- SECCIÓN: Lista de Procedimientos (oculta por defecto) -->
                <div class="card-custom" id="contenedorProcedimientos" style="display: none;">
                    <div class="card-custom-header">
                        <h5><i class="fas fa-list"></i> Procedimientos de la Mascota</h5>
                        <button class="btn btn-light btn-sm" onclick="cerrarProcedimientos()">
                            <i class="fas fa-times"></i> Cerrar
                        </button>
                    </div>
                    <div class="card-custom-body">
                        <div id="listaProcedimientos"></div>
                    </div>
                </div>
            </div>
        </div>

        <!-- Modal Registrar Procedimiento -->
        <div class="modal fade" id="modalRegistrar" tabindex="-1">
            <div class="modal-dialog modal-lg">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title"><i class="fas fa-plus"></i> Registrar Procedimiento</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                    </div>
                    <div class="modal-body">
                        <div id="mensajeRegistrar"></div>

                        <div class="mb-3">
                            <label class="form-label fw-bold">Consulta: <span class="text-danger">*</span></label>
                            <select class="form-select" id="selectConsultaRegistrar" required>
                                <option value="">Cargando consultas...</option>
                            </select>
                            <small class="text-muted">Seleccione la consulta asociada a este procedimiento</small>
                        </div>

                        <div class="mb-3">
                            <label class="form-label fw-bold">Tipo de Procedimiento: <span class="text-danger">*</span></label>
                            <select class="form-select" id="selectTipoRegistrar" required>
                                <option value="">Seleccione un tipo</option>
                                <option value="Vacunación">Vacunación</option>
                                <option value="Curación">Curación</option>
                                <option value="Cirugía">Cirugía</option>
                                <option value="Extracción">Extracción</option>
                                <option value="Limpieza dental">Limpieza dental</option>
                                <option value="Castración/Esterilización">Castración/Esterilización</option>
                                <option value="Desparasitación">Desparasitación</option>
                                <option value="Otro">Otro</option>
                            </select>
                        </div>

                        <div class="mb-3">
                            <label class="form-label fw-bold">Descripción: <span class="text-danger">*</span></label>
                            <textarea class="form-control" id="txtDescripcionRegistrar" rows="3" 
                                      placeholder="Describa el procedimiento realizado..." required></textarea>
                        </div>

                        <div class="mb-3">
                            <label class="form-label fw-bold">Insumos Utilizados:</label>
                            <div id="contenedorInsumosRegistrar" class="mb-3">
                                <!-- Los insumos se agregarán aquí dinámicamente -->
                            </div>
                            <button type="button" class="btn btn-sm btn-outline-primary" onclick="agregarInsumoRegistrar()">
                                <i class="fas fa-plus"></i> Agregar Insumo
                            </button>
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
                            <i class="fas fa-save"></i> Guardar Procedimiento
                        </button>
                    </div>
                </div>
            </div>
        </div>

        <!-- Modal Editar Procedimiento -->
        <div class="modal fade" id="modalEditar" tabindex="-1">
            <div class="modal-dialog modal-lg">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title"><i class="fas fa-edit"></i> Editar Procedimiento</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                    </div>
                    <div class="modal-body">
                        <div id="mensajeEditar"></div>
                        <input type="hidden" id="idProcedimientoEditar">

                        <div class="mb-3">
                            <label class="form-label fw-bold">Tipo de Procedimiento: <span class="text-danger">*</span></label>
                            <select class="form-select" id="selectTipoEditar" required>
                                <option value="">Seleccione un tipo</option>
                                <option value="Vacunación">Vacunación</option>
                                <option value="Curación">Curación</option>
                                <option value="Cirugía">Cirugía</option>
                                <option value="Extracción">Extracción</option>
                                <option value="Limpieza dental">Limpieza dental</option>
                                <option value="Castración/Esterilización">Castración/Esterilización</option>
                                <option value="Desparasitación">Desparasitación</option>
                                <option value="Otro">Otro</option>
                            </select>
                        </div>

                        <div class="mb-3">
                            <label class="form-label fw-bold">Descripción: <span class="text-danger">*</span></label>
                            <textarea class="form-control" id="txtDescripcionEditar" rows="3" required></textarea>
                        </div>

                        <div class="mb-3">
                            <label class="form-label fw-bold">Observaciones:</label>
                            <textarea class="form-control" id="txtObservacionesEditar" rows="3"></textarea>
                        </div>

                        <div class="alert alert-warning">
                            <i class="fas fa-info-circle"></i> Los insumos no pueden editarse. Si necesita modificarlos, anule este procedimiento y cree uno nuevo.
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

        <!-- Modal Anular Procedimiento -->
        <div class="modal fade" id="modalAnular" tabindex="-1">
            <div class="modal-dialog">
                <div class="modal-content">
                    <div class="modal-header bg-danger text-white">
                        <h5 class="modal-title"><i class="fas fa-ban"></i> Anular Procedimiento</h5>
                        <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
                    </div>
                    <div class="modal-body">
                        <div id="mensajeAnular"></div>
                        <input type="hidden" id="idProcedimientoAnular">

                        <div class="alert alert-warning">
                            <i class="fas fa-exclamation-triangle"></i>
                            <strong>Atención:</strong> Esta acción marcará el procedimiento como anulado y <strong>devolverá el stock de los insumos utilizados</strong>. Solo debe realizarse en casos de error o datos incorrectos.
                        </div>

                        <div class="mb-3">
                            <label class="form-label fw-bold">Motivo de Anulación: <span class="text-danger">*</span></label>
                            <textarea class="form-control" id="txtMotivoAnulacion" rows="4" 
                                      placeholder="Ingrese el motivo detallado de por qué se anula este procedimiento..." 
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
                            let modalVerInsumos = null;
                            let modalCorregir = null; // ⬅️ AGREGAR
                            let idMascotaActual = null;
                            let idProcedimientoActualCorregir = null; // ⬅️ AGREGAR

                            document.addEventListener('DOMContentLoaded', function () {
                                setCurrentDate();
                                modalRegistrar = new bootstrap.Modal(document.getElementById('modalRegistrar'));
                                modalEditar = new bootstrap.Modal(document.getElementById('modalEditar'));
                                modalAnular = new bootstrap.Modal(document.getElementById('modalAnular'));
                                modalVerInsumos = new bootstrap.Modal(document.getElementById('modalVerInsumos'));
                                modalCorregir = new bootstrap.Modal(document.getElementById('modalCorregir')); // ⬅️ AGREGAR
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

                                fetch('ProcedimientoServlet?accion=buscarMascotas&texto=' + encodeURIComponent(texto))
                                        .then(r => r.text())
                                        .then(html => document.getElementById('contenedorResultados').innerHTML = html)
                                        .catch(error => {
                                            document.getElementById('contenedorResultados').innerHTML =
                                                    '<div class="alert alert-danger">Error: ' + error.message + '</div>';
                                        });
                            }

                            function verProcedimientos(idMascota) {
                                console.log('💉 verProcedimientos llamado con ID:', idMascota);
                                idMascotaActual = idMascota;

                                document.getElementById('listaProcedimientos').innerHTML =
                                        '<div class="text-center py-4"><div class="spinner-border text-primary"></div><p class="mt-3">Cargando procedimientos...</p></div>';

                                document.getElementById('contenedorProcedimientos').style.display = 'block';
                                document.getElementById('contenedorProcedimientos').scrollIntoView({behavior: 'smooth', block: 'start'});

                                fetch('ProcedimientoServlet?accion=listarProcedimientos&idMascota=' + idMascota)
                                        .then(r => r.text())
                                        .then(html => {
                                            document.getElementById('listaProcedimientos').innerHTML = html;
                                        })
                                        .catch(error => {
                                            document.getElementById('listaProcedimientos').innerHTML =
                                                    '<div class="alert alert-danger">Error: ' + error.message + '</div>';
                                        });
                            }

                            function cerrarProcedimientos() {
                                document.getElementById('contenedorProcedimientos').style.display = 'none';
                                document.getElementById('listaProcedimientos').innerHTML = '';
                                idMascotaActual = null;
                                window.scrollTo({top: 0, behavior: 'smooth'});
                            }

                            function abrirModalRegistrar() {
                                if (!idMascotaActual) {
                                    alert('Error: No se identificó la mascota');
                                    return;
                                }

                                document.getElementById('mensajeRegistrar').innerHTML = '';
                                document.getElementById('selectTipoRegistrar').value = '';
                                document.getElementById('txtDescripcionRegistrar').value = '';
                                document.getElementById('txtObservacionesRegistrar').value = '';
                                document.getElementById('contenedorInsumosRegistrar').innerHTML = '';
                                insumosTemporales = [];

                                document.getElementById('selectConsultaRegistrar').innerHTML = '<option value="">Cargando consultas...</option>';

                                // Cargar consultas
                                fetch('ProcedimientoServlet?accion=obtenerConsultasFinalizadas&idMascota=' + idMascotaActual)
                                        .then(r => r.text())
                                        .then(html => {
                                            document.getElementById('selectConsultaRegistrar').innerHTML = html;
                                        })
                                        .catch(error => {
                                            alert('Error al cargar consultas: ' + error.message);
                                        });

                                // Cargar insumos disponibles
                                fetch('ProcedimientoServlet?accion=listarInsumosDisponibles')
                                        .then(r => r.text())
                                        .then(html => {
                                            var tempDiv = document.createElement('div');
                                            tempDiv.innerHTML = '<select>' + html + '</select>';
                                            var options = tempDiv.querySelectorAll('option');
                                            insumosDisponibles = [];

                                            for (var i = 0; i < options.length; i++) {
                                                var opt = options[i];
                                                if (opt.value !== '') {
                                                    insumosDisponibles.push({
                                                        id: opt.value,
                                                        nombre: opt.textContent,
                                                        stock: opt.getAttribute('data-stock'),
                                                        unidad: opt.getAttribute('data-unidad')
                                                    });
                                                }
                                            }

                                            console.log('✅ Insumos disponibles cargados:', insumosDisponibles.length);
                                            modalRegistrar.show();
                                        })
                                        .catch(error => {
                                            alert('Error al cargar insumos: ' + error.message);
                                        });
                            }

                            function agregarInsumoRegistrar() {
                                if (insumosDisponibles.length === 0) {
                                    alert('No hay insumos disponibles en stock');
                                    return;
                                }

                                var idUnico = 'insumo_' + Date.now();

                                var html = '<div class="insumo-item" id="' + idUnico + '">';
                                html += '<div class="insumo-info">';
                                html += '<div class="row">';
                                html += '<div class="col-md-7">';
                                html += '<label class="form-label">Insumo:</label>';
                                html += '<select class="form-select form-select-sm insumo-select">';
                                html += '<option value="">Seleccione un insumo</option>';

                                for (var i = 0; i < insumosDisponibles.length; i++) {
                                    var ins = insumosDisponibles[i];
                                    html += '<option value="' + ins.id + '" data-stock="' + ins.stock + '" data-unidad="' + ins.unidad + '">';
                                    html += ins.nombre + ' (Stock: ' + ins.stock + ' ' + ins.unidad + ')';
                                    html += '</option>';
                                }

                                html += '</select>';
                                html += '</div>';
                                html += '<div class="col-md-4">';
                                html += '<label class="form-label">Cantidad:</label>';
                                html += '<input type="number" class="form-control form-control-sm insumo-cantidad" min="1" value="1">';
                                html += '</div>';
                                html += '<div class="col-md-1 d-flex align-items-end">';
                                html += '<button type="button" class="btn btn-sm btn-danger" onclick="eliminarInsumo(\'' + idUnico + '\')">';
                                html += '<i class="fas fa-times"></i>';
                                html += '</button>';
                                html += '</div>';
                                html += '</div>';
                                html += '</div>';
                                html += '</div>';

                                document.getElementById('contenedorInsumosRegistrar').insertAdjacentHTML('beforeend', html);
                            }

                            function eliminarInsumo(idUnico) {
                                document.getElementById(idUnico).remove();
                            }

                            function guardarRegistro() {
                                var idConsulta = document.getElementById('selectConsultaRegistrar').value;
                                var tipo = document.getElementById('selectTipoRegistrar').value;
                                var descripcion = document.getElementById('txtDescripcionRegistrar').value.trim();
                                var observaciones = document.getElementById('txtObservacionesRegistrar').value.trim();

                                if (!idConsulta) {
                                    document.getElementById('mensajeRegistrar').innerHTML =
                                            '<div class="alert alert-warning">Debe seleccionar una consulta</div>';
                                    return;
                                }

                                if (!tipo) {
                                    document.getElementById('mensajeRegistrar').innerHTML =
                                            '<div class="alert alert-warning">Debe seleccionar un tipo de procedimiento</div>';
                                    return;
                                }

                                if (!descripcion) {
                                    document.getElementById('mensajeRegistrar').innerHTML =
                                            '<div class="alert alert-warning">La descripción no puede estar vacía</div>';
                                    return;
                                }

                                // Recopilar insumos
                                var insumosItems = document.querySelectorAll('#contenedorInsumosRegistrar .insumo-item');
                                console.log('📦 [JSP] Total de items de insumos encontrados:', insumosItems.length);

                                var btnGuardar = document.querySelector('#modalRegistrar .btn-primary-custom');
                                btnGuardar.disabled = true;
                                btnGuardar.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Guardando...';

                                var params = new URLSearchParams();
                                params.append('accion', 'registrarProcedimiento');
                                params.append('idConsulta', idConsulta);
                                params.append('idMascota', idMascotaActual);
                                params.append('tipoProcedimiento', tipo);
                                params.append('descripcion', descripcion);
                                params.append('observaciones', observaciones);

                                // Procesar cada insumo
                                var contadorInsumos = 0;
                                for (var i = 0; i < insumosItems.length; i++) {
                                    var item = insumosItems[i];
                                    var select = item.querySelector('.insumo-select');
                                    var cantidadInput = item.querySelector('.insumo-cantidad');

                                    console.log('🔍 [JSP] Procesando item ' + i + ':');
                                    console.log('   - Select encontrado:', select !== null);
                                    console.log('   - Select value:', select ? select.value : 'N/A');
                                    console.log('   - Cantidad encontrada:', cantidadInput !== null);
                                    console.log('   - Cantidad value:', cantidadInput ? cantidadInput.value : 'N/A');

                                    if (select && select.value && cantidadInput && cantidadInput.value) {
                                        var option = select.options[select.selectedIndex];
                                        var stock = parseInt(option.getAttribute('data-stock'));
                                        var cantidad = parseInt(cantidadInput.value);

                                        console.log('   - Stock disponible:', stock);
                                        console.log('   - Cantidad solicitada:', cantidad);

                                        if (cantidad > stock) {
                                            var nombreInsumo = option.text.split('(')[0].trim();
                                            document.getElementById('mensajeRegistrar').innerHTML =
                                                    '<div class="alert alert-warning">La cantidad de "' + nombreInsumo + '" excede el stock disponible (' + stock + ')</div>';
                                            btnGuardar.disabled = false;
                                            btnGuardar.innerHTML = '<i class="fas fa-save"></i> Guardar Procedimiento';
                                            return;
                                        }

                                        params.append('idInsumo[]', select.value);
                                        params.append('cantidad[]', cantidadInput.value);
                                        contadorInsumos++;
                                        console.log('✅ [JSP] Insumo agregado - ID:', select.value, 'Cantidad:', cantidadInput.value);
                                    } else {
                                        console.log('⚠️ [JSP] Item ' + i + ' fue IGNORADO (select o cantidad vacíos)');
                                    }
                                }

                                console.log('📊 [JSP] Total de insumos agregados a params:', contadorInsumos);
                                console.log('📋 [JSP] Parámetros completos a enviar:', params.toString());

                                fetch('ProcedimientoServlet', {
                                    method: 'POST',
                                    headers: {'Content-Type': 'application/x-www-form-urlencoded'},
                                    body: params.toString()
                                })
                                        .then(r => r.text())
                                        .then(respuesta => {
                                            console.log('📥 [JSP] Respuesta del servidor:', respuesta);
                                            var partes = respuesta.split('|');

                                            if (partes[0] === 'OK') {
                                                document.getElementById('mensajeRegistrar').innerHTML =
                                                        '<div class="alert alert-success">' + partes[1] + '</div>';

                                                setTimeout(function () {
                                                    modalRegistrar.hide();
                                                    verProcedimientos(idMascotaActual);
                                                }, 1500);
                                            } else {
                                                document.getElementById('mensajeRegistrar').innerHTML =
                                                        '<div class="alert alert-danger">' + partes[1] + '</div>';
                                                btnGuardar.disabled = false;
                                                btnGuardar.innerHTML = '<i class="fas fa-save"></i> Guardar Procedimiento';
                                            }
                                        })
                                        .catch(error => {
                                            console.error('❌ [JSP] Error en fetch:', error);
                                            document.getElementById('mensajeRegistrar').innerHTML =
                                                    '<div class="alert alert-danger">Error: ' + error.message + '</div>';
                                            btnGuardar.disabled = false;
                                            btnGuardar.innerHTML = '<i class="fas fa-save"></i> Guardar Procedimiento';
                                        });
                            }
                            function abrirModalEditar(idProcedimiento) {
                                document.getElementById('mensajeEditar').innerHTML = '';
                                document.getElementById('idProcedimientoEditar').value = idProcedimiento;

                                fetch('ProcedimientoServlet?accion=obtenerDetalles&idProcedimiento=' + idProcedimiento)
                                        .then(r => r.text())
                                        .then(respuesta => {
                                            if (respuesta.startsWith('ERROR')) {
                                                alert(respuesta.split('|')[1]);
                                                return;
                                            }

                                            // Parsear respuesta manualmente (formato: tipo|descripcion|observaciones)
                                            var partes = respuesta.split('|||');
                                            if (partes.length >= 2) {
                                                document.getElementById('selectTipoEditar').value = partes[0];
                                                document.getElementById('txtDescripcionEditar').value = partes[1];
                                                document.getElementById('txtObservacionesEditar').value = partes[2] || '';
                                                modalEditar.show();
                                            } else {
                                                alert('Error al cargar datos del procedimiento');
                                            }
                                        })
                                        .catch(error => {
                                            alert('Error al cargar datos: ' + error.message);
                                        });
                            }

                            function guardarEdicion() {
                                var idProcedimiento = document.getElementById('idProcedimientoEditar').value;
                                var tipo = document.getElementById('selectTipoEditar').value;
                                var descripcion = document.getElementById('txtDescripcionEditar').value.trim();
                                var observaciones = document.getElementById('txtObservacionesEditar').value.trim();

                                if (!tipo) {
                                    document.getElementById('mensajeEditar').innerHTML =
                                            '<div class="alert alert-warning">Debe seleccionar un tipo de procedimiento</div>';
                                    return;
                                }

                                if (!descripcion) {
                                    document.getElementById('mensajeEditar').innerHTML =
                                            '<div class="alert alert-warning">La descripción no puede estar vacía</div>';
                                    return;
                                }

                                var btnGuardar = document.querySelector('#modalEditar .btn-primary-custom');
                                btnGuardar.disabled = true;
                                btnGuardar.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Guardando...';

                                var params = new URLSearchParams();
                                params.append('accion', 'editarProcedimiento');
                                params.append('idProcedimiento', idProcedimiento);
                                params.append('tipoProcedimiento', tipo);
                                params.append('descripcion', descripcion);
                                params.append('observaciones', observaciones);

                                fetch('ProcedimientoServlet', {
                                    method: 'POST',
                                    headers: {'Content-Type': 'application/x-www-form-urlencoded'},
                                    body: params.toString()
                                })
                                        .then(r => r.text())
                                        .then(respuesta => {
                                            var partes = respuesta.split('|');

                                            if (partes[0] === 'OK') {
                                                document.getElementById('mensajeEditar').innerHTML =
                                                        '<div class="alert alert-success">' + partes[1] + '</div>';

                                                setTimeout(function () {
                                                    modalEditar.hide();
                                                    verProcedimientos(idMascotaActual);
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

                            function abrirModalAnular(idProcedimiento) {
                                document.getElementById('mensajeAnular').innerHTML = '';
                                document.getElementById('idProcedimientoAnular').value = idProcedimiento;
                                document.getElementById('txtMotivoAnulacion').value = '';

                                modalAnular.show();
                            }

                            function confirmarAnulacion() {
                                var idProcedimiento = document.getElementById('idProcedimientoAnular').value;
                                var motivo = document.getElementById('txtMotivoAnulacion').value.trim();

                                if (!motivo) {
                                    document.getElementById('mensajeAnular').innerHTML =
                                            '<div class="alert alert-warning">Debe ingresar el motivo de anulación</div>';
                                    return;
                                }

                                if (!confirm('¿Está seguro que desea anular este procedimiento?\n\nSe devolverá el stock de los insumos utilizados.\n\nEsta acción no se puede deshacer.')) {
                                    return;
                                }

                                var btnAnular = document.querySelector('#modalAnular .btn-danger');
                                btnAnular.disabled = true;
                                btnAnular.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Anulando...';

                                var params = new URLSearchParams();
                                params.append('accion', 'anularProcedimiento');
                                params.append('idProcedimiento', idProcedimiento);
                                params.append('motivo', motivo);

                                fetch('ProcedimientoServlet', {
                                    method: 'POST',
                                    headers: {'Content-Type': 'application/x-www-form-urlencoded'},
                                    body: params.toString()
                                })
                                        .then(r => r.text())
                                        .then(respuesta => {
                                            var partes = respuesta.split('|');

                                            if (partes[0] === 'OK') {
                                                document.getElementById('mensajeAnular').innerHTML =
                                                        '<div class="alert alert-success">' + partes[1] + '</div>';

                                                setTimeout(function () {
                                                    modalAnular.hide();
                                                    verProcedimientos(idMascotaActual);
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

                            function abrirModalVerInsumos(idProcedimiento) {
                                idProcedimientoActualCorregir = idProcedimiento; // ⬅️ GUARDAR ID PARA CORRECCIÓN

                                document.getElementById('contenedorInsumosVer').innerHTML =
                                        '<div class="text-center py-4"><div class="spinner-border text-primary"></div><p class="mt-3">Cargando insumos...</p></div>';

                                modalVerInsumos.show();

                                fetch('ProcedimientoServlet?accion=obtenerInsumos&idProcedimiento=' + idProcedimiento)
                                        .then(r => r.text())
                                        .then(html => {
                                            document.getElementById('contenedorInsumosVer').innerHTML = html;
                                        })
                                        .catch(error => {
                                            document.getElementById('contenedorInsumosVer').innerHTML =
                                                    '<div class="alert alert-danger">Error: ' + error.message + '</div>';
                                        });
                            }

                            // ========== CORRECCIÓN DE PROCEDIMIENTOS ==========

                            function abrirModalCorregir() {
                                if (!idProcedimientoActualCorregir) {
                                    alert('Error: No se identificó el procedimiento a corregir');
                                    return;
                                }

                                document.getElementById('mensajeCorregir').innerHTML = '';
                                document.getElementById('contenedorInsumosCorregir').innerHTML = '';

                                // Cerrar modal de ver insumos
                                modalVerInsumos.hide();

                                // Obtener datos del procedimiento original
                                fetch('ProcedimientoServlet?accion=obtenerDetalles&idProcedimiento=' + idProcedimientoActualCorregir)
                                        .then(r => r.text())
                                        .then(respuesta => {
                                            if (respuesta.startsWith('ERROR')) {
                                                alert(respuesta.split('|')[1]);
                                                return;
                                            }

                                            // Parsear respuesta: tipo|||descripcion|||observaciones|||idConsulta|||idMascota
                                            var partes = respuesta.split('|||');
                                            if (partes.length >= 5) {
                                                document.getElementById('idProcedimientoCorregir').value = idProcedimientoActualCorregir;
                                                document.getElementById('idConsultaCorregir').value = partes[3];
                                                document.getElementById('idMascotaCorregir').value = partes[4];
                                                document.getElementById('selectTipoCorregir').value = partes[0];
                                                document.getElementById('txtDescripcionCorregir').value = partes[1];
                                                document.getElementById('txtObservacionesCorregir').value = partes[2] || '';

                                                // Cargar insumos actuales del procedimiento
                                                cargarInsumosParaCorregir(idProcedimientoActualCorregir);

                                                modalCorregir.show();
                                            } else {
                                                alert('Error al cargar datos del procedimiento');
                                            }
                                        })
                                        .catch(error => {
                                            alert('Error: ' + error.message);
                                        });
                            }

                            function cargarInsumosParaCorregir(idProcedimiento) {
                                fetch('ProcedimientoServlet?accion=obtenerInsumos&idProcedimiento=' + idProcedimiento)
                                        .then(r => r.text())
                                        .then(html => {
                                            // Extraer los insumos del HTML de la tabla
                                            var tempDiv = document.createElement('div');
                                            tempDiv.innerHTML = html;

                                            var filas = tempDiv.querySelectorAll('tbody tr');
                                            console.log('📦 Insumos encontrados en el procedimiento:', filas.length);

                                            if (filas.length === 0) {
                                                document.getElementById('contenedorInsumosCorregir').innerHTML =
                                                        '<div class="alert alert-info">El procedimiento original no tenía insumos. Puede agregar nuevos.</div>';
                                                return;
                                            }

                                            // Cargar insumos disponibles primero
                                            fetch('ProcedimientoServlet?accion=listarInsumosDisponibles')
                                                    .then(r => r.text())
                                                    .then(htmlInsumos => {
                                                        var tempDiv2 = document.createElement('div');
                                                        tempDiv2.innerHTML = '<select>' + htmlInsumos + '</select>';
                                                        var options = tempDiv2.querySelectorAll('option');
                                                        insumosDisponibles = [];

                                                        for (var i = 0; i < options.length; i++) {
                                                            var opt = options[i];
                                                            if (opt.value !== '') {
                                                                insumosDisponibles.push({
                                                                    id: opt.value,
                                                                    nombre: opt.textContent.split('(')[0].trim(),
                                                                    stock: opt.getAttribute('data-stock'),
                                                                    unidad: opt.getAttribute('data-unidad')
                                                                });
                                                            }
                                                        }

                                                        // Renderizar cada insumo existente como editable
                                                        filas.forEach(function (fila) {
                                                            var celdas = fila.querySelectorAll('td');
                                                            if (celdas.length >= 2) {
                                                                var nombreInsumo = celdas[0].textContent.trim();
                                                                var cantidadBadge = celdas[1].querySelector('.badge');
                                                                var cantidad = cantidadBadge ? parseInt(cantidadBadge.textContent) : 1;

                                                                // Buscar el ID del insumo por nombre
                                                                var insumoEncontrado = insumosDisponibles.find(ins =>
                                                                    nombreInsumo.toLowerCase().includes(ins.nombre.toLowerCase())
                                                                );

                                                                if (insumoEncontrado) {
                                                                    agregarInsumoCorregirConDatos(insumoEncontrado.id, cantidad);
                                                                }
                                                            }
                                                        });

                                                        console.log('✅ Insumos cargados para corrección');
                                                    })
                                                    .catch(error => {
                                                        console.error('Error al cargar insumos disponibles:', error);
                                                    });
                                        })
                                        .catch(error => {
                                            console.error('Error al cargar insumos del procedimiento:', error);
                                        });
                            }

                            function agregarInsumoCorregirConDatos(idInsumo, cantidad) {
                                if (insumosDisponibles.length === 0) {
                                    alert('No hay insumos disponibles');
                                    return;
                                }

                                var idUnico = 'insumo_corr_' + Date.now();

                                var html = '<div class="insumo-item" id="' + idUnico + '">';
                                html += '<div class="insumo-info">';
                                html += '<div class="row">';
                                html += '<div class="col-md-7">';
                                html += '<label class="form-label">Insumo:</label>';
                                html += '<select class="form-select form-select-sm insumo-select-corr">';
                                html += '<option value="">Seleccione un insumo</option>';

                                for (var i = 0; i < insumosDisponibles.length; i++) {
                                    var ins = insumosDisponibles[i];
                                    var selected = (ins.id == idInsumo) ? 'selected' : '';
                                    html += '<option value="' + ins.id + '" data-stock="' + ins.stock + '" data-unidad="' + ins.unidad + '" ' + selected + '>';
                                    html += ins.nombre + ' (Stock: ' + ins.stock + ' ' + ins.unidad + ')';
                                    html += '</option>';
                                }

                                html += '</select>';
                                html += '</div>';
                                html += '<div class="col-md-4">';
                                html += '<label class="form-label">Cantidad:</label>';
                                html += '<input type="number" class="form-control form-control-sm insumo-cantidad-corr" min="1" value="' + cantidad + '">';
                                html += '</div>';
                                html += '<div class="col-md-1 d-flex align-items-end">';
                                html += '<button type="button" class="btn btn-sm btn-danger" onclick="eliminarInsumoCorr(\'' + idUnico + '\')">';
                                html += '<i class="fas fa-times"></i>';
                                html += '</button>';
                                html += '</div>';
                                html += '</div>';
                                html += '</div>';
                                html += '</div>';

                                document.getElementById('contenedorInsumosCorregir').insertAdjacentHTML('beforeend', html);
                            }

                            function agregarInsumoCorregir() {
                                agregarInsumoCorregirConDatos('', 1);
                            }

                            function eliminarInsumoCorr(idUnico) {
                                document.getElementById(idUnico).remove();
                            }

                            function guardarCorreccion() {
                                var idProcedimientoOriginal = document.getElementById('idProcedimientoCorregir').value;
                                var idConsulta = document.getElementById('idConsultaCorregir').value;
                                var idMascota = document.getElementById('idMascotaCorregir').value;
                                var tipo = document.getElementById('selectTipoCorregir').value;
                                var descripcion = document.getElementById('txtDescripcionCorregir').value.trim();
                                var observaciones = document.getElementById('txtObservacionesCorregir').value.trim();

                                if (!tipo) {
                                    document.getElementById('mensajeCorregir').innerHTML =
                                            '<div class="alert alert-warning">Debe seleccionar un tipo de procedimiento</div>';
                                    return;
                                }

                                if (!descripcion) {
                                    document.getElementById('mensajeCorregir').innerHTML =
                                            '<div class="alert alert-warning">La descripción no puede estar vacía</div>';
                                    return;
                                }

                                if (!confirm('¿Está seguro de corregir este procedimiento?\n\n' +
                                        '• El procedimiento original será ANULADO\n' +
                                        '• Se creará un NUEVO procedimiento con los datos corregidos\n' +
                                        '• El stock se ajustará automáticamente\n\n' +
                                        'Esta acción no se puede deshacer.')) {
                                    return;
                                }

                                // Recopilar insumos
                                var insumosItems = document.querySelectorAll('#contenedorInsumosCorregir .insumo-item');
                                console.log('📦 [Corrección] Total de items de insumos:', insumosItems.length);

                                var btnGuardar = document.querySelector('#modalCorregir .btn-warning');
                                btnGuardar.disabled = true;
                                btnGuardar.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Guardando...';

                                var params = new URLSearchParams();
                                params.append('accion', 'corregirProcedimiento');
                                params.append('idProcedimientoOriginal', idProcedimientoOriginal);
                                params.append('idConsulta', idConsulta);
                                params.append('idMascota', idMascota);
                                params.append('tipoProcedimiento', tipo);
                                params.append('descripcion', descripcion);
                                params.append('observaciones', observaciones);

                                var contadorInsumos = 0;
                                for (var i = 0; i < insumosItems.length; i++) {
                                    var item = insumosItems[i];
                                    var select = item.querySelector('.insumo-select-corr');
                                    var cantidadInput = item.querySelector('.insumo-cantidad-corr');

                                    if (select && select.value && cantidadInput && cantidadInput.value) {
                                        var option = select.options[select.selectedIndex];
                                        var stock = parseInt(option.getAttribute('data-stock'));
                                        var cantidad = parseInt(cantidadInput.value);

                                        if (cantidad > stock) {
                                            var nombreInsumo = option.text.split('(')[0].trim();
                                            document.getElementById('mensajeCorregir').innerHTML =
                                                    '<div class="alert alert-warning">La cantidad de "' + nombreInsumo + '" excede el stock disponible (' + stock + ')</div>';
                                            btnGuardar.disabled = false;
                                            btnGuardar.innerHTML = '<i class="fas fa-save"></i> Guardar Corrección';
                                            return;
                                        }

                                        params.append('idInsumo[]', select.value);
                                        params.append('cantidad[]', cantidadInput.value);
                                        contadorInsumos++;
                                        console.log('✅ [Corrección] Insumo agregado - ID:', select.value, 'Cantidad:', cantidadInput.value);
                                    }
                                }

                                console.log('📊 [Corrección] Total de insumos a enviar:', contadorInsumos);
                                console.log('📋 [Corrección] Parámetros:', params.toString());

                                fetch('ProcedimientoServlet', {
                                    method: 'POST',
                                    headers: {'Content-Type': 'application/x-www-form-urlencoded'},
                                    body: params.toString()
                                })
                                        .then(r => r.text())
                                        .then(respuesta => {
                                            console.log('📥 [Corrección] Respuesta del servidor:', respuesta);
                                            var partes = respuesta.split('|');

                                            if (partes[0] === 'OK') {
                                                document.getElementById('mensajeCorregir').innerHTML =
                                                        '<div class="alert alert-success">' + partes[1] + '</div>';

                                                setTimeout(function () {
                                                    modalCorregir.hide();
                                                    verProcedimientos(idMascotaActual);
                                                }, 2000);
                                            } else {
                                                document.getElementById('mensajeCorregir').innerHTML =
                                                        '<div class="alert alert-danger">' + partes[1] + '</div>';
                                                btnGuardar.disabled = false;
                                                btnGuardar.innerHTML = '<i class="fas fa-save"></i> Guardar Corrección';
                                            }
                                        })
                                        .catch(error => {
                                            console.error('❌ [Corrección] Error en fetch:', error);
                                            document.getElementById('mensajeCorregir').innerHTML =
                                                    '<div class="alert alert-danger">Error: ' + error.message + '</div>';
                                            btnGuardar.disabled = false;
                                            btnGuardar.innerHTML = '<i class="fas fa-save"></i> Guardar Corrección';
                                        });
                            }



        </script>

        <!-- Modal Ver Insumos -->
        <div class="modal fade" id="modalVerInsumos" tabindex="-1">
            <div class="modal-dialog modal-lg">
                <div class="modal-content">
                    <div class="modal-header" style="background: linear-gradient(135deg, #f59e0b 0%, #d97706 100%); color: white;">
                        <h5 class="modal-title"><i class="fas fa-box"></i> Insumos Utilizados</h5>
                        <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
                    </div>
                    <div class="modal-body">
                        <div id="contenedorInsumosVer">
                            <div class="text-center py-4">
                                <div class="spinner-border text-primary"></div>
                                <p class="mt-3">Cargando insumos...</p>
                            </div>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-warning" onclick="abrirModalCorregir()">
                            <i class="fas fa-edit"></i> Corregir Insumos
                        </button>
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cerrar</button>
                    </div>
                </div>
            </div>
        </div>  

        <!-- Modal Corregir Procedimiento -->
        <div class="modal fade" id="modalCorregir" tabindex="-1">
            <div class="modal-dialog modal-xl">
                <div class="modal-content">
                    <div class="modal-header" style="background: linear-gradient(135deg, #f59e0b 0%, #d97706 100%); color: white;">
                        <h5 class="modal-title"><i class="fas fa-edit"></i> Corregir Procedimiento</h5>
                        <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
                    </div>
                    <div class="modal-body">
                        <div id="mensajeCorregir"></div>

                        <input type="hidden" id="idProcedimientoCorregir">
                        <input type="hidden" id="idConsultaCorregir">
                        <input type="hidden" id="idMascotaCorregir">

                        <div class="alert alert-info">
                            <i class="fas fa-info-circle"></i>
                            <strong>¿Qué hace esta corrección?</strong>
                            <ul class="mb-0 mt-2">
                                <li>El procedimiento original se marcará como <strong>ANULADO</strong> y su stock se devolverá</li>
                                <li>Se creará un <strong>nuevo procedimiento</strong> con los datos corregidos</li>
                                <li>Ambos quedarán vinculados para mantener el historial</li>
                            </ul>
                        </div>

                        <div class="mb-3">
                            <label class="form-label fw-bold">Tipo de Procedimiento: <span class="text-danger">*</span></label>
                            <select class="form-select" id="selectTipoCorregir" required>
                                <option value="">Seleccione un tipo</option>
                                <option value="Vacunación">Vacunación</option>
                                <option value="Curación">Curación</option>
                                <option value="Cirugía">Cirugía</option>
                                <option value="Extracción">Extracción</option>
                                <option value="Limpieza dental">Limpieza dental</option>
                                <option value="Castración/Esterilización">Castración/Esterilización</option>
                                <option value="Desparasitación">Desparasitación</option>
                                <option value="Otro">Otro</option>
                            </select>
                        </div>

                        <div class="mb-3">
                            <label class="form-label fw-bold">Descripción: <span class="text-danger">*</span></label>
                            <textarea class="form-control" id="txtDescripcionCorregir" rows="3" required></textarea>
                        </div>

                        <div class="mb-3">
                            <label class="form-label fw-bold">Insumos Corregidos:</label>
                            <div id="contenedorInsumosCorregir" class="mb-3">
                                <!-- Los insumos se cargarán aquí -->
                            </div>
                            <button type="button" class="btn btn-sm btn-outline-primary" onclick="agregarInsumoCorregir()">
                                <i class="fas fa-plus"></i> Agregar Insumo
                            </button>
                        </div>

                        <div class="mb-3">
                            <label class="form-label fw-bold">Observaciones:</label>
                            <textarea class="form-control" id="txtObservacionesCorregir" rows="3"></textarea>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancelar</button>
                        <button type="button" class="btn btn-warning" onclick="guardarCorreccion()">
                            <i class="fas fa-save"></i> Guardar Corrección
                        </button>
                    </div>
                </div>
            </div>
        </div>

    </body>
</html>

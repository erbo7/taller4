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
        <title>Fichas Médicas - DiazPet</title>
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
            .ficha-section {
                background: #f8f9fa;
                padding: 20px;
                border-radius: 8px;
                margin-bottom: 20px;
                border-left: 4px solid var(--primary-color);
            }
            .section-title {
                color: var(--primary-color);
                font-weight: bold;
                margin-bottom: 15px;
                padding-bottom: 10px;
                border-bottom: 2px solid #e0e0e0;
            }
            .badge-finalizada {
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
            .badge-en-curso {
                background: #dbeafe;
                color: #3b82f6;
                padding: 4px 8px;
                border-radius: 12px;
                font-size: 11px;
            }
            .action-buttons {
                display: flex;
                gap: 10px;
                justify-content: flex-end;
                margin-top: 20px;
                padding-top: 20px;
                border-top: 2px solid #e0e0e0;
            }
            .action-buttons-header {
                display: flex;
                gap: 10px;
            }
            #fichaContent {
                max-height: 70vh;
                overflow-y: auto;
            }

            /* Estilos para los botones de acción de la ficha */
            .ficha-action-buttons {
                display: flex;
                gap: 10px;
                margin-bottom: 20px;
                padding: 15px;
                background: #f8f9fa;
                border-radius: 8px;
                justify-content: flex-end;
            }

            .btn-edit-ficha {
                background: #3b82f6;
                color: white;
                border: none;
                padding: 10px 20px;
                border-radius: 8px;
                font-weight: 600;
                transition: all 0.3s;
            }

            .btn-edit-ficha:hover {
                background: #2563eb;
                transform: translateY(-2px);
                box-shadow: 0 5px 15px rgba(59, 130, 246, 0.4);
            }

            .btn-anular-ficha {
                background: #ef4444;
                color: white;
                border: none;
                padding: 10px 20px;
                border-radius: 8px;
                font-weight: 600;
                transition: all 0.3s;
            }

            .btn-anular-ficha:hover {
                background: #dc2626;
                transform: translateY(-2px);
                box-shadow: 0 5px 15px rgba(239, 68, 68, 0.4);
            }

            /* Estilos para modales */
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
                    <h2><i class="fas fa-file-medical"></i> Fichas Médicas</h2>
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
                        <h5><i class="fas fa-search"></i> Buscar Ficha Médica</h5>
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
                                <i class="fas fa-info-circle"></i> Busca una mascota para ver su ficha médica completa
                            </div>
                        </div>
                    </div>
                </div>

                <!-- SECCIÓN: Ficha Médica (oculta por defecto) -->
                <div class="card-custom" id="contenedorFicha" style="display: none;">
                    <div class="card-custom-header">
                        <h5><i class="fas fa-file-medical-alt"></i> Ficha Médica Completa</h5>
                        <div class="action-buttons-header">
                            <button class="btn btn-light btn-sm" onclick="imprimirFicha()">
                                <i class="fas fa-print"></i> Imprimir
                            </button>
                            <button class="btn btn-light btn-sm" onclick="enviarEmail()">
                                <i class="fas fa-envelope"></i> Enviar Email
                            </button>
                            <button class="btn btn-light btn-sm" onclick="cerrarFicha()">
                                <i class="fas fa-times"></i> Cerrar
                            </button>
                        </div>
                    </div>
                    <div class="card-custom-body">
                        <!-- Botones de acción de la ficha -->
                        <div class="ficha-action-buttons">
                            <button class="btn-edit-ficha" onclick="abrirModalEditar()">
                                <i class="fas fa-edit"></i> Editar Observaciones
                            </button>
                            <button class="btn-anular-ficha" onclick="abrirModalAnular()">
                                <i class="fas fa-ban"></i> Anular Ficha
                            </button>
                        </div>
                        <div id="fichaContent"></div>
                    </div>
                </div>
            </div>
        </div>

        <!-- Modal Editar Ficha -->
        <div class="modal fade" id="modalEditarFicha" tabindex="-1">
            <div class="modal-dialog modal-lg">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title"><i class="fas fa-edit"></i> Editar Observaciones de la Ficha</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                    </div>
                    <div class="modal-body">
                        <div id="mensajeEditar"></div>
                        <div class="mb-3">
                            <label class="form-label fw-bold">Observaciones Generales:</label>
                            <textarea class="form-control" id="txtObservaciones" rows="6" 
                                      placeholder="Ingrese observaciones generales sobre el estado de salud de la mascota, recomendaciones, notas importantes, etc."></textarea>
                            <small class="text-muted">
                                <i class="fas fa-info-circle"></i> Estas observaciones se agregarán al historial de la ficha médica
                            </small>
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

        <!-- Modal Anular Ficha -->
        <div class="modal fade" id="modalAnularFicha" tabindex="-1">
            <div class="modal-dialog">
                <div class="modal-content">
                    <div class="modal-header bg-danger text-white">
                        <h5 class="modal-title"><i class="fas fa-ban"></i> Anular Ficha Médica</h5>
                        <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
                    </div>
                    <div class="modal-body">
                        <div id="mensajeAnular"></div>
                        <div class="alert alert-warning">
                            <i class="fas fa-exclamation-triangle"></i>
                            <strong>Atención:</strong> Esta acción es irreversible y debe tener una justificación válida (ficha generada por error, duplicada, datos completamente incorrectos, etc.).
                        </div>
                        <div class="mb-3">
                            <label class="form-label fw-bold">Motivo de Anulación: <span class="text-danger">*</span></label>
                            <textarea class="form-control" id="txtMotivoAnulacion" rows="4" 
                                      placeholder="Ingrese el motivo detallado de por qué se anula esta ficha (Ej: Ficha generada por error, Datos incorrectos, Duplicada, etc.)" 
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
                            let modalEditarFicha = null;
                            let modalAnularFicha = null;
                            let idFichaActual = null;
                            let idMascotaActual = null;

                            document.addEventListener('DOMContentLoaded', function () {
                                setCurrentDate();
                                modalEditarFicha = new bootstrap.Modal(document.getElementById('modalEditarFicha'));
                                modalAnularFicha = new bootstrap.Modal(document.getElementById('modalAnularFicha'));
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

                                fetch('FichaMedicaServlet?accion=buscarMascotas&texto=' + encodeURIComponent(texto))
                                        .then(r => r.text())
                                        .then(html => document.getElementById('contenedorResultados').innerHTML = html);
                            }

                            function verFicha(idMascota) {
                                idMascotaActual = idMascota;

                                document.getElementById('fichaContent').innerHTML =
                                        '<div class="text-center py-5"><div class="spinner-border text-primary"></div><p class="mt-3">Cargando ficha médica...</p></div>';

                                document.getElementById('contenedorFicha').style.display = 'block';

                                // Scroll suave hacia la ficha
                                document.getElementById('contenedorFicha').scrollIntoView({behavior: 'smooth', block: 'start'});

                                fetch('FichaMedicaServlet?accion=verFicha&idMascota=' + idMascota)
                                        .then(r => r.text())
                                        .then(html => {
                                            document.getElementById('fichaContent').innerHTML = html;

                                            // Extraer idFicha del contenido
                                            const hiddenIdFicha = document.getElementById('hiddenIdFicha');
                                            if (hiddenIdFicha) {
                                                idFichaActual = parseInt(hiddenIdFicha.value);
                                            }

                                            // Guardar ID para funciones de imprimir/email
                                            document.getElementById('contenedorFicha').setAttribute('data-id-mascota', idMascota);
                                        })
                                        .catch(error => {
                                            document.getElementById('fichaContent').innerHTML =
                                                    '<div class="alert alert-danger">Error al cargar la ficha: ' + error.message + '</div>';
                                        });
                            }

                            function cerrarFicha() {
                                document.getElementById('contenedorFicha').style.display = 'none';
                                document.getElementById('fichaContent').innerHTML = '';
                                idFichaActual = null;
                                idMascotaActual = null;
                                // Scroll hacia arriba
                                window.scrollTo({top: 0, behavior: 'smooth'});
                            }

                            function abrirModalEditar() {
                                if (!idFichaActual) {
                                    alert('No se ha cargado ninguna ficha médica');
                                    return;
                                }

                                // Limpiar mensajes previos
                                document.getElementById('mensajeEditar').innerHTML = '';

                                // Cargar observaciones actuales
                                fetch('FichaMedicaServlet?accion=obtenerDetallesFicha&idFicha=' + idFichaActual)
                                        .then(r => r.text())
                                        .then(obs => {
                                            document.getElementById('txtObservaciones').value = obs;
                                            modalEditarFicha.show();
                                        })
                                        .catch(error => {
                                            alert('Error al cargar observaciones: ' + error.message);
                                        });
                            }

                            function guardarEdicion() {
                                const observaciones = document.getElementById('txtObservaciones').value.trim();

                                if (!observaciones) {
                                    document.getElementById('mensajeEditar').innerHTML =
                                            '<div class="alert alert-warning">Las observaciones no pueden estar vacías</div>';
                                    return;
                                }

                                if (!idFichaActual) {
                                    document.getElementById('mensajeEditar').innerHTML =
                                            '<div class="alert alert-danger">Error: No se identificó la ficha a editar</div>';
                                    return;
                                }

                                const btnGuardar = document.querySelector('#modalEditarFicha .btn-primary-custom');
                                btnGuardar.disabled = true;
                                btnGuardar.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Guardando...';

                                // ✅ USAR URLSearchParams en lugar de FormData
                                const params = new URLSearchParams();
                                params.append('accion', 'editarFicha');
                                params.append('idFicha', idFichaActual);
                                params.append('observaciones', observaciones);

                                fetch('FichaMedicaServlet', {
                                    method: 'POST',
                                    headers: {
                                        'Content-Type': 'application/x-www-form-urlencoded'
                                    },
                                    body: params.toString()
                                })
                                        .then(r => r.text())
                                        .then(respuesta => {
                                            const partes = respuesta.split('|');

                                            if (partes[0] === 'OK') {
                                                document.getElementById('mensajeEditar').innerHTML =
                                                        '<div class="alert alert-success">' + partes[1] + '</div>';

                                                setTimeout(() => {
                                                    modalEditarFicha.hide();
                                                    verFicha(idMascotaActual);
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

                            function abrirModalAnular() {
                                if (!idFichaActual) {
                                    alert('No se ha cargado ninguna ficha médica');
                                    return;
                                }

                                // Limpiar campos
                                document.getElementById('mensajeAnular').innerHTML = '';
                                document.getElementById('txtMotivoAnulacion').value = '';

                                // Mostrar modal directamente (SIN verificación de tiempo)
                                modalAnularFicha.show();
                            }

                            function confirmarAnulacion() {
                                const motivo = document.getElementById('txtMotivoAnulacion').value.trim();

                                if (!motivo) {
                                    document.getElementById('mensajeAnular').innerHTML =
                                            '<div class="alert alert-warning">Debe ingresar el motivo de anulación</div>';
                                    return;
                                }

                                if (!confirm('¿Está COMPLETAMENTE SEGURO que desea anular esta ficha médica?\n\nEsta acción es IRREVERSIBLE y solo debe realizarse en casos excepcionales como:\n- Ficha generada por error\n- Ficha duplicada\n- Datos completamente incorrectos')) {
                                    return;
                                }

                                const btnAnular = document.querySelector('#modalAnularFicha .btn-danger');
                                btnAnular.disabled = true;
                                btnAnular.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Anulando...';

                                // ✅ USAR URLSearchParams en lugar de FormData
                                const params = new URLSearchParams();
                                params.append('accion', 'anularFicha');
                                params.append('idFicha', idFichaActual);
                                params.append('motivo', motivo);

                                fetch('FichaMedicaServlet', {
                                    method: 'POST',
                                    headers: {
                                        'Content-Type': 'application/x-www-form-urlencoded'
                                    },
                                    body: params.toString()
                                })
                                        .then(r => r.text())
                                        .then(respuesta => {
                                            const partes = respuesta.split('|');

                                            if (partes[0] === 'OK') {
                                                document.getElementById('mensajeAnular').innerHTML =
                                                        '<div class="alert alert-success">' + partes[1] + '</div>';

                                                setTimeout(() => {
                                                    modalAnularFicha.hide();
                                                    cerrarFicha();
                                                    alert('Ficha anulada correctamente');
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

                            function imprimirFicha() {
                                const idMascota = document.getElementById('contenedorFicha').getAttribute('data-id-mascota');
                                if (!idMascota) {
                                    alert('No hay ficha cargada');
                                    return;
                                }

                                // Abrir ventana de impresión
                                const contenido = document.getElementById('fichaContent').innerHTML;
                                const ventana = window.open('', '_blank');
                                ventana.document.write('<html><head><title>Ficha Médica</title>');
                                ventana.document.write('<link href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap/5.3.0/css/bootstrap.min.css" rel="stylesheet">');
                                ventana.document.write('<style>');
                                ventana.document.write('.ficha-section { page-break-inside: avoid; margin-bottom: 20px; padding: 15px; border: 1px solid #ddd; }');
                                ventana.document.write('.section-title { color: #10b981; font-weight: bold; border-bottom: 2px solid #10b981; padding-bottom: 10px; }');
                                ventana.document.write('</style>');
                                ventana.document.write('</head><body>');
                                ventana.document.write('<div class="container mt-4">');
                                ventana.document.write('<h2 class="text-center mb-4">FICHA MÉDICA - DiazPet</h2>');
                                ventana.document.write(contenido);
                                ventana.document.write('</div>');
                                ventana.document.write('</body></html>');
                                ventana.document.close();

                                setTimeout(function () {
                                    ventana.print();
                                }, 500);
                            }

                            function enviarEmail() {
                                const idMascota = document.getElementById('contenedorFicha').getAttribute('data-id-mascota');
                                if (!idMascota) {
                                    alert('No hay ficha cargada');
                                    return;
                                }

                                // TODO: Implementar envío por email
                                alert('Función de envío por email en desarrollo');
                            }

                            // Autoload si viene desde otra página
                            const urlParams = new URLSearchParams(window.location.search);
                            const autoload = urlParams.get('autoload');
                            if (autoload) {
                                setTimeout(function () {
                                    verFicha(parseInt(autoload));
                                }, 500);
                            }
        </script>
    </body>
</html>
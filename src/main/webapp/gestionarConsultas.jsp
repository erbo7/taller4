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
        <title>Consultas Veterinarias - DiazPet</title>
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
            .btn-outline-custom {
                border: 2px solid #e0e0e0;
                background: white;
                color: #2c3e50;
                padding: 8px 20px;
                border-radius: 8px;
                font-weight: 600;
                transition: all 0.3s;
            }
            .btn-outline-custom:hover {
                border-color: var(--primary-color);
                color: var(--primary-color);
            }
            .patient-item {
                display: flex;
                align-items: center;
                justify-content: space-between;
                padding: 15px;
                background: #f8f9fa;
                border-radius: 8px;
                margin-bottom: 12px;
            }
            .patient-item:hover {
                background: #e9ecef;
            }
            .patient-time {
                background: var(--primary-color);
                color: white;
                padding: 10px 15px;
                border-radius: 8px;
                font-weight: bold;
                min-width: 80px;
                text-align: center;
            }
            .patient-info {
                flex: 1;
                margin-left: 20px;
            }
            .patient-info h6 {
                margin: 0 0 5px;
                font-weight: bold;
                color: #2c3e50;
            }
            .patient-info p {
                margin: 0;
                font-size: 13px;
                color: #999;
            }
            .consulta-en-curso-item {
                display: flex;
                align-items: center;
                justify-content: space-between;
                padding: 20px;
                background: linear-gradient(135deg, #dbeafe 0%, #bfdbfe 100%);
                border-left: 5px solid #3b82f6;
                border-radius: 8px;
                margin-bottom: 15px;
                box-shadow: 0 2px 8px rgba(59, 130, 246, 0.2);
            }
            .consulta-en-curso-item:hover {
                box-shadow: 0 4px 12px rgba(59, 130, 246, 0.3);
                transform: translateY(-2px);
                transition: all 0.3s;
            }
            .consulta-info-principal {
                flex: 1;
            }
            .consulta-info-principal h6 {
                margin: 0 0 8px;
                font-weight: bold;
                color: #1e40af;
                font-size: 16px;
            }
            .consulta-info-principal .detalles {
                display: flex;
                gap: 20px;
                flex-wrap: wrap;
            }
            .consulta-info-principal .detalle-item {
                display: flex;
                align-items: center;
                gap: 8px;
                font-size: 14px;
                color: #1e40af;
            }
            .consulta-info-principal .detalle-item i {
                color: #3b82f6;
            }
            .consulta-acciones {
                display: flex;
                gap: 8px;
                flex-shrink: 0;
            }
            .btn-accion {
                padding: 8px 15px;
                border-radius: 6px;
                font-size: 13px;
                font-weight: 600;
                border: none;
                cursor: pointer;
                transition: all 0.3s;
                display: flex;
                align-items: center;
                gap: 5px;
            }
            .btn-continuar {
                background: #3b82f6;
                color: white;
            }
            .btn-continuar:hover {
                background: #2563eb;
                transform: translateY(-2px);
                box-shadow: 0 4px 8px rgba(59, 130, 246, 0.3);
            }
            .btn-finalizar {
                background: #10b981;
                color: white;
            }
            .btn-finalizar:hover {
                background: #059669;
                transform: translateY(-2px);
                box-shadow: 0 4px 8px rgba(16, 185, 129, 0.3);
            }
            .btn-anular {
                background: #ef4444;
                color: white;
            }
            .btn-anular:hover {
                background: #dc2626;
                transform: translateY(-2px);
                box-shadow: 0 4px 8px rgba(239, 68, 68, 0.3);
            }
            .table-consultas tbody tr {
                cursor: pointer;
            }
            .table-consultas tbody tr:hover {
                background: #f8f9fa;
            }
            .badge-estado {
                padding: 6px 12px;
                border-radius: 20px;
                font-size: 12px;
                font-weight: 600;
            }
            .badge-finalizada {
                background: #d1fae5;
                color: #10b981;
            }
            .badge-anulada {
                background: #fee2e2;
                color: #dc2626;
            }
            .badge-en-curso {
                background: #dbeafe;
                color: #3b82f6;
            }
            .section-divider {
                border-top: 2px solid #e5e7eb;
                margin: 40px 0 30px 0;
                position: relative;
            }
            .section-divider-text {
                position: absolute;
                top: -12px;
                left: 50%;
                transform: translateX(-50%);
                background: #f5f6fa;
                padding: 0 15px;
                color: #6b7280;
                font-weight: 600;
                font-size: 14px;
            }




            .btn-info {
                background-color: #17a2b8;
                color: white;
                border: none;
            }
            .btn-info:hover {
                background-color: #138496;
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
                    <h2><i class="fas fa-stethoscope"></i> Gestión de Consultas</h2>
                    <p class="date mb-0">
                        <i class="far fa-calendar"></i>
                        <span id="currentDate"></span>
                    </p>
                </div>
            </div>

            <div class="content-area">
                <!-- SECCIÓN 1: Citas del día -->
                <div class="card-custom">
                    <div class="card-custom-header">
                        <h5><i class="fas fa-calendar-check"></i> Mis Citas Programadas para Hoy</h5>
                    </div>
                    <div class="card-custom-body" id="contenedorCitasHoy">
                        <div class="text-center py-4">
                            <div class="spinner-border text-primary" role="status">
                                <span class="visually-hidden">Cargando...</span>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- Divisor visual -->
                <div class="section-divider">
                    <span class="section-divider-text">
                        <i class="fas fa-clipboard-check"></i> EN ATENCIÓN
                    </span>
                </div>

                <!-- SECCIÓN 2: CONSULTAS EN CURSO (NUEVA) -->
                <div class="card-custom">
                    <div class="card-custom-header" style="background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);">
                        <h5><i class="fas fa-spinner fa-pulse"></i> Consultas en Curso</h5>
                        <button class="btn btn-light btn-sm" onclick="cargarConsultasEnCurso()">
                            <i class="fas fa-sync-alt"></i> Actualizar
                        </button>
                    </div>
                    <div class="card-custom-body" id="contenedorConsultasEnCurso">
                        <div class="text-center py-4">
                            <div class="spinner-border text-primary" role="status">
                                <span class="visually-hidden">Cargando...</span>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- Divisor visual -->
                <div class="section-divider">
                    <span class="section-divider-text">
                        <i class="fas fa-history"></i> HISTORIAL Y BÚSQUEDA
                    </span>
                </div>

                <!-- SECCIÓN 3: Historial de consultas -->
                <div class="card-custom">
                    <div class="card-custom-header">
                        <h5><i class="fas fa-search"></i> Buscar Consultas por Mascota</h5>
                    </div>
                    <div class="card-custom-body">
                        <div class="row mb-4">
                            <div class="col-md-8">
                                <label class="form-label">Buscar mascota por nombre o dueño:</label>
                                <input type="text" class="form-control" id="buscarMascota" 
                                       placeholder="Escribe el nombre de la mascota o cliente..."
                                       onkeypress="if (event.key === 'Enter')
                                                   buscarHistorial()">
                            </div>
                            <div class="col-md-4">
                                <label class="form-label">&nbsp;</label>
                                <button class="btn btn-primary-custom w-100" onclick="buscarHistorial()">
                                    <i class="fas fa-search"></i> Buscar Consultas
                                </button>
                            </div>
                        </div>
                        <div id="contenedorHistorial">
                            <div class="alert alert-info">
                                <i class="fas fa-info-circle"></i> Busca una mascota para ver su historial de consultas
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- Modal para formularios -->
        <div class="modal fade" id="modalConsulta" tabindex="-1">
            <div class="modal-dialog modal-xl">
                <div class="modal-content">
                    <div class="modal-header" style="background: linear-gradient(135deg, #10b981 0%, #059669 100%); border: none;">
                        <h5 class="modal-title" id="tituloModal" style="color: white; font-weight: 600; font-size: 20px;">
                            <i class="fas fa-clipboard-check"></i> Ficha de Atención Veterinaria
                        </h5>
                        <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
                    </div>
                    <div class="modal-body" id="contenidoModal"></div>
                </div>
            </div>
        </div>

        <script src="https://cdnjs.cloudflare.com/ajax/libs/bootstrap/5.3.0/js/bootstrap.bundle.min.js"></script>
        <script>
                                    function toggleSidebar() {
                                        document.getElementById('sidebar').classList.toggle('collapsed');
                                    }

                                    function setCurrentDate() {
                                        const options = {weekday: 'long', year: 'numeric', month: 'long', day: 'numeric'};
                                        const date = new Date().toLocaleDateString('es-ES', options);
                                        document.getElementById('currentDate').textContent = date.charAt(0).toUpperCase() + date.slice(1);
                                    }

                                    function cargarCitasHoy() {
                                        fetch('ConsultaServlet?accion=listarCitasHoy')
                                                .then(r => r.text())
                                                .then(html => document.getElementById('contenedorCitasHoy').innerHTML = html);
                                    }

                                    function cargarConsultasEnCurso() {
                                        fetch('ConsultaServlet?accion=listarEnCurso')
                                                .then(r => r.text())
                                                .then(html => document.getElementById('contenedorConsultasEnCurso').innerHTML = html);
                                    }

                                    function abrirIniciarConsulta(idCita) {
                                        fetch('ConsultaServlet?accion=iniciarForm&idCita=' + idCita)
                                                .then(r => r.text())
                                                .then(html => {
                                                    document.getElementById('tituloModal').textContent = 'Iniciar Consulta';
                                                    document.getElementById('contenidoModal').innerHTML = html;
                                                    new bootstrap.Modal(document.getElementById('modalConsulta')).show();
                                                });
                                    }

                                    function buscarHistorial() {
                                        const texto = document.getElementById('buscarMascota').value.trim();
                                        if (!texto) {
                                            alert('Ingresa un nombre de mascota o cliente');
                                            return;
                                        }
                                        document.getElementById('contenedorHistorial').innerHTML =
                                                '<div class="text-center py-4"><div class="spinner-border text-primary"></div></div>';

                                        fetch('ConsultaServlet?accion=buscarHistorial&texto=' + encodeURIComponent(texto))
                                                .then(r => r.text())
                                                .then(html => document.getElementById('contenedorHistorial').innerHTML = html);
                                    }

                                    function abrirEditarConsulta(idConsulta) {
                                        fetch('ConsultaServlet?accion=editarForm&idConsulta=' + idConsulta)
                                                .then(r => r.text())
                                                .then(html => {
                                                    document.getElementById('tituloModal').innerHTML = '<i class="fas fa-clipboard-check"></i> Formulario de Atención Veterinaria';
                                                    document.getElementById('contenidoModal').innerHTML = html;
                                                    const modal = new bootstrap.Modal(document.getElementById('modalConsulta'));
                                                    modal.show();

                                                    // Configurar eventos de pestañas
                                                    setTimeout(() => {
                                                        // Tab Diagnósticos
                                                        const tabDiag = document.querySelector('a[href="#tabDiagnosticos"]');
                                                        if (tabDiag) {
                                                            tabDiag.addEventListener('shown.bs.tab', function () {
                                                                cargarDiagnosticoTemporal(idConsulta);
                                                            });

                                                            if (tabDiag.classList.contains('active')) {
                                                                cargarDiagnosticoTemporal(idConsulta);
                                                            }
                                                        }

                                                        // Tab Recetas
                                                        const tabRecetas = document.querySelector('a[href="#tabRecetas"]');
                                                        if (tabRecetas) {
                                                            tabRecetas.addEventListener('shown.bs.tab', function () {
                                                                cargarRecetaTemporal(idConsulta);
                                                            });

                                                            if (tabRecetas.classList.contains('active')) {
                                                                cargarRecetaTemporal(idConsulta);
                                                            }
                                                        }

                                                        // Tab Procedimientos
                                                        const tabProcedimientos = document.querySelector('a[href="#tabProcedimientos"]');
                                                        if (tabProcedimientos) {
                                                            tabProcedimientos.addEventListener('shown.bs.tab', function () {
                                                                cargarInsumosDisponibles();
                                                                cargarProcedimientoTemporal(idConsulta);
                                                            });

                                                            if (tabProcedimientos.classList.contains('active')) {
                                                                cargarInsumosDisponibles();
                                                                cargarProcedimientoTemporal(idConsulta);
                                                            }
                                                        }

                                                        // Tab Estudios
                                                        const tabEstudios = document.querySelector('a[href="#tabEstudios"]');
                                                        if (tabEstudios) {
                                                            tabEstudios.addEventListener('shown.bs.tab', function () {
                                                                cargarTiposEstudio();
                                                                cargarEstudioTemporal(idConsulta);
                                                            });

                                                            if (tabEstudios.classList.contains('active')) {
                                                                cargarTiposEstudio();
                                                                cargarEstudioTemporal(idConsulta);
                                                            }
                                                        }

                                                        // Tab Análisis
                                                        const tabAnalisis = document.querySelector('a[href="#tabAnalisis"]');
                                                        if (tabAnalisis) {
                                                            tabAnalisis.addEventListener('shown.bs.tab', function () {
                                                                cargarTiposAnalisis();
                                                                cargarAnalisisTemporal(idConsulta);
                                                            });

                                                            if (tabAnalisis.classList.contains('active')) {
                                                                cargarTiposAnalisis();
                                                                cargarAnalisisTemporal(idConsulta);
                                                            }
                                                        }

                                                        // Tab Tratamientos
                                                        const tabTratamientos = document.querySelector('a[href="#tabTratamientos"]');
                                                        if (tabTratamientos) {
                                                            tabTratamientos.addEventListener('shown.bs.tab', function () {
                                                                cargarTratamientoTemporal(idConsulta);
                                                            });

                                                            if (tabTratamientos.classList.contains('active')) {
                                                                cargarTratamientoTemporal(idConsulta);
                                                            }
                                                        }


                                                    }, 300);
                                                });
                                    }

                                    function abrirAnularConsulta(idConsulta) {
                                        fetch('ConsultaServlet?accion=anularForm&idConsulta=' + idConsulta)
                                                .then(r => r.text())
                                                .then(html => {
                                                    document.getElementById('tituloModal').textContent = 'Anular Consulta';
                                                    document.getElementById('contenidoModal').innerHTML = html;
                                                    new bootstrap.Modal(document.getElementById('modalConsulta')).show();
                                                });
                                    }

                                    function abrirFichaMascota(idMascota) {
                                        window.open('gestionarFichaMedica.jsp?autoload=' + idMascota, '_blank');
                                    }

                                    function enviarFormulario() {
                                        const form = document.getElementById('formConsulta');
                                        if (!form.checkValidity()) {
                                            form.reportValidity();
                                            return;
                                        }

                                        const formData = new FormData(form);

                                        fetch('ConsultaServlet', {
                                            method: 'POST',
                                            body: new URLSearchParams(formData)
                                        })
                                                .then(r => r.text())
                                                .then(msg => {
                                                    if (msg.startsWith("OK|")) {
                                                        alert("✅ " + msg.split("|")[1]);
                                                        bootstrap.Modal.getInstance(document.getElementById('modalConsulta')).hide();
                                                        cargarCitasHoy();
                                                        cargarConsultasEnCurso();
                                                    } else {
                                                        alert("❌ " + msg.substring(6));
                                                    }
                                                });
                                    }

                                    function guardarDatosConsulta() {
                                        const form = document.getElementById('formConsulta');
                                        if (!form.checkValidity()) {
                                            form.reportValidity();
                                            return;
                                        }

                                        const idConsulta = document.getElementById('idConsultaGlobal').value;

                                        // 1️⃣ Guardar DIAGNÓSTICO
                                        const diagnostico = document.getElementById('txtDiagnosticoNuevo')?.value.trim();
                                        if (diagnostico) {
                                            const datosDiagnostico = {
                                                diagnostico: diagnostico,
                                                tipoDiagnostico: document.getElementById('selectTipoNuevo').value,
                                                hallazgos: document.getElementById('txtHallazgosNuevo').value.trim(),
                                                sintomas: document.getElementById('txtSintomasNuevo').value.trim(),
                                                observaciones: document.getElementById('txtObservacionesNuevo').value.trim()
                                            };
                                            sessionStorage.setItem('diagnostico_temp_' + idConsulta, JSON.stringify(datosDiagnostico));
                                            console.log('✅ Diagnóstico guardado temporalmente');
                                        }

                                        // 2️⃣ Guardar RECETA
                                        const medicamento = document.getElementById('txtMedicamentoNuevo')?.value.trim();
                                        if (medicamento) {
                                            const datosReceta = {
                                                medicamento: medicamento,
                                                dosis: document.getElementById('txtDosisNuevo').value.trim(),
                                                frecuencia: document.getElementById('txtFrecuenciaNuevo').value.trim(),
                                                duracion: document.getElementById('txtDuracionNuevo').value.trim(),
                                                indicaciones: document.getElementById('txtIndicacionesNuevo').value.trim(),
                                                observaciones: document.getElementById('txtObservacionesRecetaNuevo').value.trim()
                                            };
                                            sessionStorage.setItem('receta_temp_' + idConsulta, JSON.stringify(datosReceta));
                                            console.log('✅ Receta guardada temporalmente');
                                        }

                                        // 3️⃣ Guardar PROCEDIMIENTO con INSUMOS
                                        const tipoProcedimiento = document.getElementById('selectTipoProcedimientoNuevo')?.value.trim();
                                        if (tipoProcedimiento) {
                                            const datosProcedimiento = {
                                                tipoProcedimiento: tipoProcedimiento,
                                                descripcion: document.getElementById('txtDescripcionProcedimientoNuevo').value.trim(),
                                                observaciones: document.getElementById('txtObservacionesProcedimientoNuevo').value.trim(),
                                                insumos: insumosTemporales.filter(i => i.idInsumo)
                                            };
                                            sessionStorage.setItem('procedimiento_temp_' + idConsulta, JSON.stringify(datosProcedimiento));
                                            console.log('✅ Procedimiento guardado temporalmente con', datosProcedimiento.insumos.length, 'insumos');
                                        }

                                        // 4️⃣ Guardar ESTUDIO
                                        const tipoEstudio = document.getElementById('selectTipoEstudioNuevo');
                                        const valorTipoEstudio = tipoEstudio ? tipoEstudio.value.trim() : '';
                                        if (valorTipoEstudio) {
                                            const datosEstudio = {
                                                idTipoEstudio: valorTipoEstudio,
                                                descripcion: document.getElementById('txtDescripcionEstudioNuevo').value.trim(),
                                                observaciones: document.getElementById('txtObservacionesEstudioNuevo').value.trim()
                                            };
                                            sessionStorage.setItem('estudio_temp_' + idConsulta, JSON.stringify(datosEstudio));
                                            console.log('✅ Estudio guardado temporalmente');
                                        }

                                        // 5️⃣ Guardar ANÁLISIS
                                        const tipoAnalisis = document.getElementById('selectTipoAnalisisNuevo');
                                        const valorTipoAnalisis = tipoAnalisis ? tipoAnalisis.value.trim() : '';
                                        if (valorTipoAnalisis) {
                                            const datosAnalisis = {
                                                idTipoAnalisis: valorTipoAnalisis,
                                                descripcion: document.getElementById('txtDescripcionAnalisisNuevo').value.trim(),
                                                observaciones: document.getElementById('txtObservacionesAnalisisNuevo').value.trim()
                                            };
                                            sessionStorage.setItem('analisis_temp_' + idConsulta, JSON.stringify(datosAnalisis));
                                            console.log('✅ Análisis guardado temporalmente');
                                        }

                                        // 6️⃣ Guardar TRATAMIENTO
                                        const planTerapeutico = document.getElementById('txtPlanTerapeuticoNuevo');
                                        const valorPlanTerapeutico = planTerapeutico ? planTerapeutico.value.trim() : '';
                                        if (valorPlanTerapeutico) {
                                            const datosTratamiento = {
                                                fechaInicio: document.getElementById('txtFechaInicioTratamientoNuevo').value,
                                                fechaFin: document.getElementById('txtFechaFinTratamientoNuevo').value,
                                                fechaControl: document.getElementById('txtFechaControlTratamientoNuevo').value,
                                                planTerapeutico: valorPlanTerapeutico,
                                                evolucion: document.getElementById('txtEvolucionTratamientoNuevo').value.trim(),
                                                observaciones: document.getElementById('txtObservacionesTratamientoNuevo').value.trim()
                                            };
                                            sessionStorage.setItem('tratamiento_temp_' + idConsulta, JSON.stringify(datosTratamiento));
                                            console.log('✅ Tratamiento guardado temporalmente');
                                        }

// 7️⃣ Guardar datos de consulta (este era el 6️⃣)

                                        // 6️⃣ Guardar datos de consulta
                                        const formData = new FormData(form);

                                        fetch('ConsultaServlet', {
                                            method: 'POST',
                                            body: new URLSearchParams(formData)
                                        })
                                                .then(r => r.text())
                                                .then(msg => {
                                                    if (msg.startsWith("OK|")) {
                                                        alert("✅ " + msg.split("|")[1] + "\n\n(Los datos del diagnóstico, receta, procedimiento, estudios y análisis se guardarán al finalizar)");
                                                    } else {
                                                        alert("❌ " + msg.substring(6));
                                                    }
                                                });
                                    }

                                    function finalizarConsultaCompleta() {
                                        if (!confirm('¿Finalizar esta consulta?\n\nSe guardarán todos los datos y no podrás modificarlos después.')) {
                                            return;
                                        }

                                        const idConsulta = document.getElementById('idConsultaGlobal').value;
                                        const idMascota = document.getElementById('idMascotaEditar').value;

                                        console.log('🚀 [Finalizar] Iniciando finalización - ID Consulta:', idConsulta);

                                        // 1️⃣ Guardar cambios de consulta
                                        const formConsulta = new FormData(document.getElementById('formConsulta'));

                                        fetch('ConsultaServlet', {
                                            method: 'POST',
                                            body: new URLSearchParams(formConsulta)
                                        })
                                                .then(r => r.text())
                                                .then(msg => {
                                                    console.log('📝 [Finalizar] Consulta guardada:', msg);
                                                    if (!msg.startsWith("OK|")) {
                                                        throw new Error('Error al guardar datos de consulta');
                                                    }

                                                    // 2️⃣ Guardar DIAGNÓSTICO si existe
                                                    const diagnostico = document.getElementById('txtDiagnosticoNuevo')?.value.trim();

                                                    if (diagnostico) {
                                                        const paramsDiag = new URLSearchParams();
                                                        paramsDiag.append('accion', 'registrarDiagnostico');
                                                        paramsDiag.append('idConsulta', idConsulta);
                                                        paramsDiag.append('idMascota', idMascota);
                                                        paramsDiag.append('diagnostico', diagnostico);
                                                        paramsDiag.append('tipoDiagnostico', document.getElementById('selectTipoNuevo').value);
                                                        paramsDiag.append('hallazgos', document.getElementById('txtHallazgosNuevo').value.trim());
                                                        paramsDiag.append('sintomas', document.getElementById('txtSintomasNuevo').value.trim());
                                                        paramsDiag.append('observaciones', document.getElementById('txtObservacionesNuevo').value.trim());

                                                        return fetch('DiagnosticoServlet', {
                                                            method: 'POST',
                                                            headers: {'Content-Type': 'application/x-www-form-urlencoded'},
                                                            body: paramsDiag.toString()
                                                        }).then(r => r.text());
                                                    }

                                                    return 'OK|Sin diagnóstico';
                                                })
                                                .then(respuestaDiag => {
                                                    console.log('📋 Diagnóstico guardado:', respuestaDiag);

                                                    // 3️⃣ Guardar RECETA si existe
                                                    const medicamento = document.getElementById('txtMedicamentoNuevo')?.value.trim();

                                                    if (medicamento) {
                                                        const dosis = document.getElementById('txtDosisNuevo').value.trim();
                                                        const frecuencia = document.getElementById('txtFrecuenciaNuevo').value.trim();

                                                        if (!dosis || !frecuencia) {
                                                            throw new Error('Dosis y Frecuencia son obligatorios si registras un medicamento');
                                                        }

                                                        const paramsReceta = new URLSearchParams();
                                                        paramsReceta.append('accion', 'registrarReceta');
                                                        paramsReceta.append('idConsulta', idConsulta);
                                                        paramsReceta.append('idMascota', idMascota);
                                                        paramsReceta.append('medicamento', medicamento);
                                                        paramsReceta.append('dosis', dosis);
                                                        paramsReceta.append('frecuencia', frecuencia);
                                                        paramsReceta.append('duracion', document.getElementById('txtDuracionNuevo').value.trim());
                                                        paramsReceta.append('indicaciones', document.getElementById('txtIndicacionesNuevo').value.trim());
                                                        paramsReceta.append('observaciones', document.getElementById('txtObservacionesRecetaNuevo').value.trim());

                                                        return fetch('RecetaServlet', {
                                                            method: 'POST',
                                                            headers: {'Content-Type': 'application/x-www-form-urlencoded'},
                                                            body: paramsReceta.toString()
                                                        }).then(r => r.text());
                                                    }

                                                    return 'OK|Sin receta';
                                                })
                                                .then(respuestaReceta => {
                                                    console.log('💊 Receta guardada:', respuestaReceta);

                                                    // 4️⃣ Guardar PROCEDIMIENTO CON INSUMOS
                                                    const procedimientoTempStr = sessionStorage.getItem('procedimiento_temp_' + idConsulta);
                                                    console.log('🔍 [Finalizar] Verificando procedimiento en sessionStorage:', procedimientoTempStr ? 'ENCONTRADO' : 'NO ENCONTRADO');

                                                    if (procedimientoTempStr) {
                                                        try {
                                                            const procTemp = JSON.parse(procedimientoTempStr);
                                                            console.log('💉 [Finalizar] Procedimiento temporal parseado:', procTemp);
                                                            console.log('📦 [Finalizar] Cantidad de insumos en procTemp:', procTemp.insumos ? procTemp.insumos.length : 0);

                                                            const paramsProcedimiento = new URLSearchParams();
                                                            paramsProcedimiento.append('accion', 'registrarProcedimiento');
                                                            paramsProcedimiento.append('idConsulta', idConsulta);
                                                            paramsProcedimiento.append('idMascota', idMascota);
                                                            paramsProcedimiento.append('tipoProcedimiento', procTemp.tipoProcedimiento);
                                                            paramsProcedimiento.append('descripcion', procTemp.descripcion);
                                                            paramsProcedimiento.append('observaciones', procTemp.observaciones || '');

                                                            if (procTemp.insumos && procTemp.insumos.length > 0) {
                                                                console.log('📦 [Finalizar] Procesando ' + procTemp.insumos.length + ' insumos...');

                                                                for (let i = 0; i < procTemp.insumos.length; i++) {
                                                                    const insumo = procTemp.insumos[i];
                                                                    console.log('   🔍 Insumo ' + (i + 1) + ':', insumo);

                                                                    paramsProcedimiento.append('idInsumo[]', insumo.idInsumo);
                                                                    paramsProcedimiento.append('cantidad[]', insumo.cantidad);

                                                                    console.log('   ✅ Agregado - ID:', insumo.idInsumo, 'Cantidad:', insumo.cantidad);
                                                                }
                                                            } else {
                                                                console.log('⚠️ [Finalizar] No hay insumos en procTemp.insumos');
                                                            }

                                                            console.log('📋 [Finalizar] Parámetros completos:', paramsProcedimiento.toString());

                                                            return fetch('ProcedimientoServlet', {
                                                                method: 'POST',
                                                                headers: {'Content-Type': 'application/x-www-form-urlencoded'},
                                                                body: paramsProcedimiento.toString()
                                                            }).then(r => r.text());

                                                        } catch (error) {
                                                            console.error('❌ [Finalizar] Error al parsear procedimiento:', error);
                                                            throw error;
                                                        }
                                                    } else {
                                                        console.log('ℹ️ [Finalizar] No hay procedimiento en sessionStorage');
                                                    }

                                                    return 'OK|Sin procedimiento';
                                                })
                                                .then(respuestaProcedimiento => {
                                                    console.log('💉 Procedimiento guardado:', respuestaProcedimiento);

                                                    // 5️⃣ Guardar ESTUDIO si existe
                                                    const estudioTempStr = sessionStorage.getItem('estudio_temp_' + idConsulta);
                                                    console.log('🔍 [Finalizar] Verificando estudio:', estudioTempStr ? 'ENCONTRADO' : 'NO ENCONTRADO');

                                                    if (estudioTempStr) {
                                                        try {
                                                            const estudioTemp = JSON.parse(estudioTempStr);
                                                            console.log('🔬 [Finalizar] Estudio temporal parseado:', estudioTemp);

                                                            const paramsEstudio = new URLSearchParams();
                                                            paramsEstudio.append('action', 'registrar');
                                                            paramsEstudio.append('idConsulta', idConsulta);
                                                            paramsEstudio.append('idMascota', idMascota);
                                                            paramsEstudio.append('idTipoEstudio', estudioTemp.idTipoEstudio);
                                                            paramsEstudio.append('motivo', estudioTemp.descripcion);
                                                            paramsEstudio.append('observaciones', estudioTemp.observaciones || '');

                                                            return fetch('OrdenEstudioServlet', {
                                                                method: 'POST',
                                                                headers: {'Content-Type': 'application/x-www-form-urlencoded'},
                                                                body: paramsEstudio.toString()
                                                            }).then(r => r.text());

                                                        } catch (error) {
                                                            console.error('❌ [Finalizar] Error al parsear estudio:', error);
                                                            throw error;
                                                        }
                                                    }

                                                    return 'OK|Sin estudio';
                                                })
                                                .then(respuestaEstudio => {
                                                    console.log('🔬 Estudio guardado:', respuestaEstudio);

                                                    // 6️⃣ Guardar ANÁLISIS si existe
                                                    const analisisTempStr = sessionStorage.getItem('analisis_temp_' + idConsulta);
                                                    console.log('🔍 [Finalizar] Verificando análisis:', analisisTempStr ? 'ENCONTRADO' : 'NO ENCONTRADO');

                                                    if (analisisTempStr) {
                                                        try {
                                                            const analisisTemp = JSON.parse(analisisTempStr);
                                                            console.log('🧪 [Finalizar] Análisis temporal parseado:', analisisTemp);

                                                            const paramsAnalisis = new URLSearchParams();
                                                            paramsAnalisis.append('action', 'registrar');
                                                            paramsAnalisis.append('idConsulta', idConsulta);
                                                            paramsAnalisis.append('idMascota', idMascota);
                                                            paramsAnalisis.append('idTipoAnalisis', analisisTemp.idTipoAnalisis);
                                                            paramsAnalisis.append('motivo', analisisTemp.descripcion);
                                                            paramsAnalisis.append('observaciones', analisisTemp.observaciones || '');

                                                            return fetch('OrdenAnalisisServlet', {
                                                                method: 'POST',
                                                                headers: {'Content-Type': 'application/x-www-form-urlencoded'},
                                                                body: paramsAnalisis.toString()
                                                            }).then(r => r.text());

                                                        } catch (error) {
                                                            console.error('❌ [Finalizar] Error al parsear análisis:', error);
                                                            throw error;
                                                        }
                                                    }

                                                    return 'OK|Sin análisis';
                                                })
                                                .then(respuestaAnalisis => {
                                                    console.log('🧪 Análisis guardado:', respuestaAnalisis);

                                                    // 7️⃣ Guardar TRATAMIENTO si existe
                                                    const tratamientoTempStr = sessionStorage.getItem('tratamiento_temp_' + idConsulta);
                                                    console.log('🔍 [Finalizar] Verificando tratamiento:', tratamientoTempStr ? 'ENCONTRADO' : 'NO ENCONTRADO');

                                                    if (tratamientoTempStr) {
                                                        try {
                                                            const tratamientoTemp = JSON.parse(tratamientoTempStr);
                                                            console.log('🩹 [Finalizar] Tratamiento temporal parseado:', tratamientoTemp);

                                                            const paramsTratamiento = new URLSearchParams();
                                                            paramsTratamiento.append('accion', 'registrar');
                                                            paramsTratamiento.append('idConsulta', idConsulta);
                                                            paramsTratamiento.append('idMascota', idMascota);
                                                            paramsTratamiento.append('fechaInicio', tratamientoTemp.fechaInicio || '');
                                                            paramsTratamiento.append('fechaFin', tratamientoTemp.fechaFin || '');
                                                            paramsTratamiento.append('planTerapeutico', tratamientoTemp.planTerapeutico);
                                                            paramsTratamiento.append('evolucion', tratamientoTemp.evolucion || '');
                                                            paramsTratamiento.append('fechaControl', tratamientoTemp.fechaControl || '');
                                                            paramsTratamiento.append('observaciones', tratamientoTemp.observaciones || '');

                                                            return fetch('TratamientoServlet', {
                                                                method: 'POST',
                                                                headers: {'Content-Type': 'application/x-www-form-urlencoded'},
                                                                body: paramsTratamiento.toString()
                                                            }).then(r => r.text());

                                                        } catch (error) {
                                                            console.error('❌ [Finalizar] Error al parsear tratamiento:', error);
                                                            throw error;
                                                        }
                                                    }

                                                    return 'OK|Sin tratamiento';
                                                })
                                                .then(respuestaTratamiento => {
                                                    console.log('🩹 Tratamiento guardado:', respuestaTratamiento);

                                                    // 7️⃣ Cambiar estado a FINALIZADA
                                                    const paramsFinalizacion = new URLSearchParams();
                                                    paramsFinalizacion.append('accion', 'finalizarConsulta');
                                                    paramsFinalizacion.append('idConsulta', idConsulta);

                                                    return fetch('ConsultaServlet', {
                                                        method: 'POST',
                                                        body: paramsFinalizacion
                                                    }).then(r => r.text());
                                                })
                                                .then(msgFinal => {
                                                    console.log('✅ [Finalizar] Resultado final:', msgFinal);

                                                    if (msgFinal.startsWith("OK|")) {
                                                        alert("✅ Consulta finalizada exitosamente");

                                                        // Limpiar sessionStorage
                                                        sessionStorage.removeItem('diagnostico_temp_' + idConsulta);
                                                        sessionStorage.removeItem('receta_temp_' + idConsulta);
                                                        sessionStorage.removeItem('procedimiento_temp_' + idConsulta);
                                                        sessionStorage.removeItem('estudio_temp_' + idConsulta);
                                                        sessionStorage.removeItem('analisis_temp_' + idConsulta);
                                                        sessionStorage.removeItem('tratamiento_temp_' + idConsulta);

                                                        // Cerrar modal
                                                        const modalElement = document.getElementById('modalConsulta');
                                                        const modalInstance = bootstrap.Modal.getInstance(modalElement);
                                                        if (modalInstance) {
                                                            modalInstance.hide();
                                                        }

                                                        // Recargar
                                                        cargarConsultasEnCurso();
                                                    } else {
                                                        alert("❌ " + msgFinal.substring(6));
                                                    }
                                                })
                                                .catch(error => {
                                                    console.error('❌ [Finalizar] Error general:', error);
                                                    alert('❌ Error al finalizar: ' + error.message);
                                                });
                                    }

                                    function cargarDiagnosticoTemporal(idConsulta) {
                                        const datosGuardados = sessionStorage.getItem('diagnostico_temp_' + idConsulta);

                                        if (datosGuardados) {
                                            try {
                                                const datos = JSON.parse(datosGuardados);

                                                if (document.getElementById('txtDiagnosticoNuevo')) {
                                                    document.getElementById('txtDiagnosticoNuevo').value = datos.diagnostico || '';
                                                    document.getElementById('selectTipoNuevo').value = datos.tipoDiagnostico || 'TENTATIVO';
                                                    document.getElementById('txtHallazgosNuevo').value = datos.hallazgos || '';
                                                    document.getElementById('txtSintomasNuevo').value = datos.sintomas || '';
                                                    document.getElementById('txtObservacionesNuevo').value = datos.observaciones || '';

                                                    console.log('✅ Datos del diagnóstico restaurados desde memoria temporal');
                                                }
                                            } catch (e) {
                                                console.error('Error al cargar datos temporales:', e);
                                            }
                                        }
                                    }

                                    function cargarRecetaTemporal(idConsulta) {
                                        const datosGuardados = sessionStorage.getItem('receta_temp_' + idConsulta);

                                        if (datosGuardados) {
                                            try {
                                                const datos = JSON.parse(datosGuardados);

                                                if (document.getElementById('txtMedicamentoNuevo')) {
                                                    document.getElementById('txtMedicamentoNuevo').value = datos.medicamento || '';
                                                    document.getElementById('txtDosisNuevo').value = datos.dosis || '';
                                                    document.getElementById('txtFrecuenciaNuevo').value = datos.frecuencia || '';
                                                    document.getElementById('txtDuracionNuevo').value = datos.duracion || '';
                                                    document.getElementById('txtIndicacionesNuevo').value = datos.indicaciones || '';
                                                    document.getElementById('txtObservacionesRecetaNuevo').value = datos.observaciones || '';

                                                    console.log('✅ Datos de la receta restaurados desde memoria temporal');
                                                }
                                            } catch (e) {
                                                console.error('Error al cargar datos temporales de receta:', e);
                                            }
                                        }
                                    }

                                    let insumosTemporales = [];
                                    let insumosDisponibles = [];

                                    function cargarInsumosDisponibles() {
                                        fetch('ProcedimientoServlet?accion=listarInsumosDisponibles')
                                                .then(r => r.text())
                                                .then(html => {
                                                    const tempDiv = document.createElement('div');
                                                    tempDiv.innerHTML = '<select>' + html + '</select>';
                                                    const options = tempDiv.querySelectorAll('option');

                                                    insumosDisponibles = [];
                                                    options.forEach(opt => {
                                                        if (opt.value) {
                                                            insumosDisponibles.push({
                                                                id: opt.value,
                                                                nombre: opt.textContent,
                                                                stock: opt.dataset.stock || 0
                                                            });
                                                        }
                                                    });

                                                    console.log('✅ Insumos disponibles cargados:', insumosDisponibles.length);
                                                    renderizarInsumos();
                                                })
                                                .catch(error => {
                                                    console.error('❌ Error al cargar insumos:', error);
                                                });
                                    }

                                    function cargarTiposEstudio() {
                                        fetch('OrdenEstudioServlet?action=obtenerTipos')
                                                .then(function (r) {
                                                    return r.text();
                                                })
                                                .then(function (html) {
                                                    document.getElementById('selectTipoEstudioNuevo').innerHTML = html;
                                                })
                                                .catch(function (error) {
                                                    console.error('Error al cargar tipos de estudio:', error);
                                                });
                                    }

                                    function cargarTiposAnalisis() {
                                        fetch('OrdenAnalisisServlet?action=obtenerTipos')
                                                .then(function (r) {
                                                    return r.text();
                                                })
                                                .then(function (html) {
                                                    document.getElementById('selectTipoAnalisisNuevo').innerHTML = html;
                                                })
                                                .catch(function (error) {
                                                    console.error('Error al cargar tipos de análisis:', error);
                                                });
                                    }

                                    function cargarEstudioTemporal(idConsulta) {
                                        var datosGuardados = sessionStorage.getItem('estudio_temp_' + idConsulta);

                                        if (datosGuardados) {
                                            try {
                                                var datos = JSON.parse(datosGuardados);

                                                if (document.getElementById('selectTipoEstudioNuevo')) {
                                                    document.getElementById('selectTipoEstudioNuevo').value = datos.idTipoEstudio || '';
                                                    document.getElementById('txtDescripcionEstudioNuevo').value = datos.descripcion || '';
                                                    document.getElementById('txtObservacionesEstudioNuevo').value = datos.observaciones || '';

                                                    console.log('Datos del estudio restaurados');
                                                }
                                            } catch (e) {
                                                console.error('Error al cargar datos temporales de estudio:', e);
                                            }
                                        }
                                    }

                                    function cargarAnalisisTemporal(idConsulta) {
                                        var datosGuardados = sessionStorage.getItem('analisis_temp_' + idConsulta);

                                        if (datosGuardados) {
                                            try {
                                                var datos = JSON.parse(datosGuardados);

                                                if (document.getElementById('selectTipoAnalisisNuevo')) {
                                                    document.getElementById('selectTipoAnalisisNuevo').value = datos.idTipoAnalisis || '';
                                                    document.getElementById('txtDescripcionAnalisisNuevo').value = datos.descripcion || '';
                                                    document.getElementById('txtObservacionesAnalisisNuevo').value = datos.observaciones || '';

                                                    console.log('Datos del análisis restaurados');
                                                }
                                            } catch (e) {
                                                console.error('Error al cargar datos temporales de análisis:', e);
                                            }
                                        }
                                    }

                                    function cargarTratamientoTemporal(idConsulta) {
                                        var datosGuardados = sessionStorage.getItem('tratamiento_temp_' + idConsulta);

                                        if (datosGuardados) {
                                            try {
                                                var datos = JSON.parse(datosGuardados);

                                                if (document.getElementById('txtFechaInicioTratamientoNuevo')) {
                                                    document.getElementById('txtFechaInicioTratamientoNuevo').value = datos.fechaInicio || '';
                                                    document.getElementById('txtFechaFinTratamientoNuevo').value = datos.fechaFin || '';
                                                    document.getElementById('txtFechaControlTratamientoNuevo').value = datos.fechaControl || '';
                                                    document.getElementById('txtPlanTerapeuticoNuevo').value = datos.planTerapeutico || '';
                                                    document.getElementById('txtEvolucionTratamientoNuevo').value = datos.evolucion || '';
                                                    document.getElementById('txtObservacionesTratamientoNuevo').value = datos.observaciones || '';

                                                    console.log('✅ Datos del tratamiento restaurados');
                                                }
                                            } catch (e) {
                                                console.error('Error al cargar datos temporales de tratamiento:', e);
                                            }
                                        }
                                    }



                                    function agregarInsumoTemporal() {
                                        if (insumosDisponibles.length === 0) {
                                            alert('No hay insumos disponibles');
                                            return;
                                        }

                                        insumosTemporales.push({
                                            idInsumo: '',
                                            cantidad: 1
                                        });

                                        renderizarInsumos();
                                    }

                                    function eliminarInsumoTemporal(index) {
                                        insumosTemporales.splice(index, 1);
                                        renderizarInsumos();
                                    }

                                    function renderizarInsumos() {
                                        const contenedor = document.getElementById('contenedorInsumos');

                                        if (!contenedor)
                                            return;

                                        if (insumosTemporales.length === 0) {
                                            contenedor.innerHTML = '<div class="alert alert-info"><i class="fas fa-info-circle"></i> No hay insumos agregados</div>';
                                            return;
                                        }

                                        let html = '<div class="list-group">';

                                        insumosTemporales.forEach((insumo, index) => {
                                            html += '<div class="list-group-item">';
                                            html += '<div class="row align-items-center">';

                                            html += '<div class="col-md-7">';
                                            html += '<label class="form-label mb-1">Insumo:</label>';
                                            html += '<select class="form-select form-select-sm" onchange="actualizarInsumo(' + index + ', this.value, null)">';
                                            html += '<option value="">Seleccione un insumo</option>';

                                            insumosDisponibles.forEach(ins => {
                                                const selected = insumo.idInsumo == ins.id ? 'selected' : '';
                                                html += '<option value="' + ins.id + '" ' + selected + '>' + ins.nombre + '</option>';
                                            });

                                            html += '</select>';
                                            html += '</div>';

                                            html += '<div class="col-md-3">';
                                            html += '<label class="form-label mb-1">Cantidad:</label>';
                                            html += '<input type="number" class="form-control form-control-sm" min="1" value="' + insumo.cantidad + '" ';
                                            html += 'onchange="actualizarInsumo(' + index + ', null, this.value)">';
                                            html += '</div>';

                                            html += '<div class="col-md-2">';
                                            html += '<label class="form-label mb-1">&nbsp;</label>';
                                            html += '<button type="button" class="btn btn-sm btn-danger w-100" onclick="eliminarInsumoTemporal(' + index + ')">';
                                            html += '<i class="fas fa-trash"></i></button>';
                                            html += '</div>';

                                            html += '</div>';
                                            html += '</div>';
                                        });

                                        html += '</div>';

                                        contenedor.innerHTML = html;
                                    }

                                    function actualizarInsumo(index, idInsumo, cantidad) {
                                        if (idInsumo !== null) {
                                            insumosTemporales[index].idInsumo = idInsumo;
                                        }
                                        if (cantidad !== null) {
                                            insumosTemporales[index].cantidad = parseInt(cantidad);
                                        }
                                    }

                                    function cargarProcedimientoTemporal(idConsulta) {
                                        const datosGuardados = sessionStorage.getItem('procedimiento_temp_' + idConsulta);

                                        if (datosGuardados) {
                                            try {
                                                const datos = JSON.parse(datosGuardados);

                                                if (document.getElementById('selectTipoProcedimientoNuevo')) {
                                                    document.getElementById('selectTipoProcedimientoNuevo').value = datos.tipoProcedimiento || '';
                                                    document.getElementById('txtDescripcionProcedimientoNuevo').value = datos.descripcion || '';
                                                    document.getElementById('txtObservacionesProcedimientoNuevo').value = datos.observaciones || '';

                                                    insumosTemporales = datos.insumos || [];
                                                    renderizarInsumos();

                                                    console.log('✅ Datos del procedimiento restaurados desde memoria temporal');
                                                }
                                            } catch (e) {
                                                console.error('Error al cargar datos temporales de procedimiento:', e);
                                            }
                                        }
                                    }

                                    document.addEventListener('DOMContentLoaded', function () {
                                        setCurrentDate();
                                        cargarCitasHoy();
                                        cargarConsultasEnCurso();
                                    });
        </script>
    </body>
</html>
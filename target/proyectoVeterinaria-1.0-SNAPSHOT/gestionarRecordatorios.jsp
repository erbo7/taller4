<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String usuario = (String) session.getAttribute("usuario");
    String nombre = (String) session.getAttribute("nombre");
    Integer idRol = (Integer) session.getAttribute("rol");
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
        <title>Gestionar Recordatorios - DiazPet</title>
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
                padding: 15px 25px;
                display: flex;
                justify-content: space-between;
                align-items: center;
                border-radius: 10px 10px 0 0;
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
            .stats-grid {
                display: grid;
                grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
                gap: 20px;
                margin-bottom: 30px;
            }
            .stat-card {
                background: white;
                padding: 25px;
                border-radius: 10px;
                box-shadow: 0 2px 10px rgba(0,0,0,0.05);
                transition: all 0.3s;
                border-left: 4px solid;
            }
            .stat-card:hover {
                transform: translateY(-5px);
                box-shadow: 0 5px 20px rgba(0,0,0,0.1);
            }
            .stat-card.blue {
                border-left-color: #667eea;
            }
            .stat-card.green {
                border-left-color: #10b981;
            }
            .stat-card.red {
                border-left-color: #dc3545;
            }
            .stat-card.orange {
                border-left-color: #ff8c42;
            }

            .stat-card-icon {
                width: 50px;
                height: 50px;
                border-radius: 10px;
                display: flex;
                align-items: center;
                justify-content: center;
                font-size: 24px;
                margin-bottom: 15px;
            }
            .stat-card.blue .stat-card-icon {
                background: #e0e7ff;
                color: #667eea;
            }
            .stat-card.green .stat-card-icon {
                background: #d1fae5;
                color: #10b981;
            }
            .stat-card.red .stat-card-icon {
                background: #fee2e2;
                color: #dc3545;
            }
            .stat-card.orange .stat-card-icon {
                background: #fff4e6;
                color: #ff8c42;
            }

            .stat-card h6 {
                color: #999;
                font-size: 14px;
                font-weight: 600;
                margin: 0;
            }
            .stat-card h2 {
                color: #2c3e50;
                font-size: 32px;
                font-weight: bold;
                margin: 10px 0 0;
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
            .badge-estado {
                padding: 6px 14px;
                border-radius: 20px;
                font-size: 12px;
                font-weight: 600;
            }
            .badge-enviado {
                background-color: #d1fae5;
                color: #10b981;
            }
            .badge-pendiente {
                background-color: #fff3cd;
                color: #856404;
            }
            .badge-fallado {
                background-color: #fee2e2;
                color: #dc3545;
            }
            .badge-reintento {
                background-color: #dbeafe;
                color: #3b82f6;
            }
            .canal-badge {
                display: inline-flex;
                align-items: center;
                gap: 5px;
                padding: 4px 12px;
                border-radius: 15px;
                font-size: 12px;
                font-weight: 600;
            }
            .canal-whatsapp {
                background-color: #d1fae5;
                color: #059669;
            }
            .canal-sms {
                background-color: #dbeafe;
                color: #0284c7;
            }
            .canal-email {
                background-color: #fee2e2;
                color: #dc2626;
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
            .empty-state {
                text-align: center;
                padding: 60px 20px;
            }
            .empty-state i {
                font-size: 64px;
                color: #cbd5e1;
                margin-bottom: 20px;
            }
            .nav-tabs {
                border-bottom: 2px solid #e0e0e0;
                margin-bottom: 20px;
            }
            .nav-tabs .nav-link {
                color: #666;
                border: none;
                padding: 12px 24px;
                font-weight: 500;
                background: transparent;
            }
            .nav-tabs .nav-link:hover {
                color: var(--primary-color);
            }
            .nav-tabs .nav-link.active {
                color: var(--primary-color);
                border-bottom: 3px solid var(--primary-color);
                background-color: transparent;
            }
        </style>
    </head>
    <body>
        <!-- Sidebar -->
        <div class="sidebar" id="sidebar">
            <div class="sidebar-header">
                <div>
                    <h3>🐾 DiazPet</h3>
                    <small style="color: rgba(255,255,255,0.8);">Recordatorios</small>
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
                <a href="dashboardRecepcionista.jsp" class="menu-item">
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
                <a href="gestionarRecordatorios.jsp" class="menu-item active">
                    <i class="fas fa-bell"></i>
                    <span class="menu-text">Recordatorios</span>
                </a>
                <a href="gestionarDocumentos.jsp" class="menu-item">
                    <i class="fas fa-file-alt"></i>
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
                        <h2><i class="fas fa-bell"></i> Gestión de Recordatorios</h2>
                        <p class="text-muted mb-0">
                            <i class="far fa-calendar"></i>
                            <span id="currentDate"></span>
                        </p>
                    </div>
                </div>
            </div>

            <div class="content-area">
                <!-- Tabs de navegación -->
                <ul class="nav nav-tabs" role="tablist">
                    <li class="nav-item">
                        <a class="nav-link active" href="#" onclick="cargarSupervision()">
                            <i class="fas fa-list"></i> Supervisión
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="#" onclick="cargarPendientesHoy()">
                            <i class="fas fa-clock"></i> Pendientes Hoy
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="#" onclick="cargarEstadisticas()">
                            <i class="fas fa-chart-bar"></i> Estadísticas
                        </a>
                    </li>
                    <li class="nav-item">
                        <a class="nav-link" href="#" onclick="cargarConfiguracion()">
                            <i class="fas fa-cog"></i> Configuración
                        </a>
                    </li>
                </ul>

                <!-- Contenedor dinámico -->
                <div id="contenidoDinamico">
                    <!-- Aquí se carga el contenido según la pestaña -->
                    <div class="text-center py-5">
                        <div class="spinner-border text-primary" role="status">
                            <span class="visually-hidden">Cargando...</span>
                        </div>
                        <p class="mt-3 text-muted">Cargando recordatorios...</p>
                    </div>
                </div>
            </div>
        </div>

        <!-- Modal para ver mensaje -->
        <div class="modal fade" id="mensajeModal" tabindex="-1">
            <div class="modal-dialog">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title">Mensaje del Recordatorio</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                    </div>
                    <div class="modal-body">
                        <p id="mensajeContenido" class="text-break"></p>
                    </div>
                </div>
            </div>
        </div>

        <script src="https://cdnjs.cloudflare.com/ajax/libs/bootstrap/5.3.0/js/bootstrap.bundle.min.js"></script>
        <script>
                            // ===================== INICIALIZACIÓN =====================
                            document.addEventListener('DOMContentLoaded', function () {
                                setCurrentDate();
                                cargarSupervision();
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

                            function mostrarMensaje(tipo, texto) {
                                // Remover mensajes anteriores
                                var mensajesAntiguos = document.querySelectorAll('.alert-mensaje');
                                mensajesAntiguos.forEach(function (el) {
                                    el.remove();
                                });

                                // Crear nuevo mensaje
                                var alertDiv = document.createElement('div');
                                alertDiv.className = 'alert alert-' + tipo + ' alert-dismissible fade show alert-mensaje';
                                alertDiv.innerHTML =
                                        '<i class="fas ' + (tipo === 'success' ? 'fa-check-circle' : 'fa-exclamation-circle') + '"></i> ' +
                                        texto +
                                        '<button type="button" class="btn-close" data-bs-dismiss="alert"></button>';

                                // Insertar después de los tabs
                                var tabs = document.querySelector('.nav-tabs');
                                if (tabs && tabs.parentNode) {
                                    tabs.parentNode.insertBefore(alertDiv, tabs.nextSibling);
                                }
                            }

                            function verMensaje(mensaje) {
                                document.getElementById('mensajeContenido').textContent = mensaje;
                                new bootstrap.Modal(document.getElementById('mensajeModal')).show();
                            }

                            // ===================== CARGAR CONTENIDO =====================
                            function cargarSupervision() {
                                // Actualizar tabs activos
                                document.querySelectorAll('.nav-link').forEach(function (tab) {
                                    tab.classList.remove('active');
                                });
                                document.querySelector('.nav-link[onclick="cargarSupervision()"]').classList.add('active');

                                // Mostrar loading
                                document.getElementById('contenidoDinamico').innerHTML =
                                        '<div class="text-center py-5">' +
                                        '<div class="spinner-border text-primary" role="status">' +
                                        '<span class="visually-hidden">Cargando...</span>' +
                                        '</div>' +
                                        '<p class="mt-3 text-muted">Cargando supervisión...</p>' +
                                        '</div>';

                                // Primero cargar estadísticas
                                fetch('RecordatoriosServlet?accion=obtenerEstadisticas')
                                        .then(function (r) {
                                            return r.text();
                                        })
                                        .then(function (htmlStats) {
                                            // Luego cargar recordatorios
                                            fetch('RecordatoriosServlet?accion=listar')
                                                    .then(function (r) {
                                                        return r.text();
                                                    })
                                                    .then(function (htmlTabla) {
                                                        // Construir HTML completo
                                                        var html =
                                                                '<div class="stats-grid" id="estadisticasHoy">' + htmlStats + '</div>' +
                                                                '<div class="card-custom">' +
                                                                '<div class="card-custom-body">' +
                                                                '<div class="row">' +
                                                                '<div class="col-md-4 mb-2">' +
                                                                '<button class="btn-primary-custom w-100" onclick="generarRecordatorios()">' +
                                                                '<i class="fas fa-plus-circle"></i> Generar Recordatorios' +
                                                                '</button>' +
                                                                '</div>' +
                                                                '<div class="col-md-4 mb-2">' +
                                                                '<button class="btn btn-success w-100" onclick="procesarEnvios()">' +
                                                                '<i class="fas fa-paper-plane"></i> Procesar Envíos' +
                                                                '</button>' +
                                                                '</div>' +
                                                                '<div class="col-md-4 mb-2">' +
                                                                '<button class="btn btn-warning w-100" onclick="reintentarFallados()">' +
                                                                '<i class="fas fa-redo"></i> Reintentar Fallados' +
                                                                '</button>' +
                                                                '</div>' +
                                                                '</div>' +
                                                                '</div>' +
                                                                '</div>' +
                                                                '<div class="card-custom">' +
                                                                '<div class="card-custom-header">' +
                                                                '<h5><i class="fas fa-filter"></i> Filtros de Búsqueda</h5>' +
                                                                '</div>' +
                                                                '<div class="card-custom-body">' +
                                                                '<div class="row g-3 align-items-end">' +
                                                                '<div class="col-md-3">' +
                                                                '<label class="form-label">Estado</label>' +
                                                                '<select class="form-select" id="estadoFiltro">' +
                                                                '<option value="">Todos</option>' +
                                                                '<option value="PENDIENTE">Pendiente</option>' +
                                                                '<option value="ENVIADO">Enviado</option>' +
                                                                '<option value="FALLADO">Fallado</option>' +
                                                                '<option value="REINTENTO">Reintento</option>' +
                                                                '</select>' +
                                                                '</div>' +
                                                                '<div class="col-md-3">' +
                                                                '<label class="form-label">Canal</label>' +
                                                                '<select class="form-select" id="canalFiltro">' +
                                                                '<option value="">Todos</option>' +
                                                                '<option value="WHATSAPP">WhatsApp</option>' +
                                                                '<option value="SMS">SMS</option>' +
                                                                '<option value="EMAIL">Email</option>' +
                                                                '</select>' +
                                                                '</div>' +
                                                                '<div class="col-md-3">' +
                                                                '<label class="form-label">Fecha</label>' +
                                                                '<input type="date" class="form-control" id="fechaFiltro">' +
                                                                '</div>' +
                                                                '<div class="col-md-3">' +
                                                                '<button type="button" class="btn-primary-custom w-100" onclick="aplicarFiltros()">' +
                                                                '<i class="fas fa-search"></i> Buscar' +
                                                                '</button>' +
                                                                '</div>' +
                                                                '</div>' +
                                                                '</div>' +
                                                                '</div>' +
                                                                '<div class="card-custom">' +
                                                                '<div class="card-custom-header">' +
                                                                '<h5><i class="fas fa-table"></i> Lista de Recordatorios</h5>' +
                                                                '</div>' +
                                                                '<div class="card-custom-body">' +
                                                                '<div class="table-responsive">' +
                                                                '<table class="table table-hover">' +
                                                                '<thead>' +
                                                                '<tr>' +
                                                                '<th>ID</th>' +
                                                                '<th>Cita</th>' +
                                                                '<th>Cliente</th>' +
                                                                '<th>Mascota</th>' +
                                                                '<th>Veterinario</th>' +
                                                                '<th>Fecha Cita</th>' +
                                                                '<th>Canal</th>' +
                                                                '<th>Estado</th>' +
                                                                '<th>Intentos</th>' +
                                                                '<th>Fecha Envío</th>' +
                                                                '</tr>' +
                                                                '</thead>' +
                                                                '<tbody id="tablaRecordatorios">' + htmlTabla + '</tbody>' +
                                                                '</table>' +
                                                                '</div>' +
                                                                '</div>' +
                                                                '</div>';

                                                        document.getElementById('contenidoDinamico').innerHTML = html;
                                                    })
                                                    .catch(function (err) {
                                                        console.error('Error:', err);
                                                        document.getElementById('contenidoDinamico').innerHTML =
                                                                '<div class="alert alert-danger">Error al cargar recordatorios</div>';
                                                    });
                                        })
                                        .catch(function (err) {
                                            console.error('Error:', err);
                                        });
                            }

                            function aplicarFiltros() {
                                var estado = document.getElementById('estadoFiltro').value;
                                var canal = document.getElementById('canalFiltro').value;
                                var fecha = document.getElementById('fechaFiltro').value;

                                var url = 'RecordatoriosServlet?accion=filtrar';
                                if (estado)
                                    url += '&estado=' + encodeURIComponent(estado);
                                if (canal)
                                    url += '&canal=' + encodeURIComponent(canal);
                                if (fecha)
                                    url += '&fecha=' + encodeURIComponent(fecha);

                                fetch(url)
                                        .then(function (r) {
                                            return r.text();
                                        })
                                        .then(function (html) {
                                            document.getElementById('tablaRecordatorios').innerHTML = html;
                                        })
                                        .catch(function (err) {
                                            console.error('Error:', err);
                                            mostrarMensaje('danger', 'Error al aplicar filtros');
                                        });
                            }

                            function cargarPendientesHoy() {
                                // Actualizar tabs activos
                                document.querySelectorAll('.nav-link').forEach(function (tab) {
                                    tab.classList.remove('active');
                                });
                                document.querySelector('.nav-link[onclick="cargarPendientesHoy()"]').classList.add('active');

                                // Mostrar loading
                                document.getElementById('contenidoDinamico').innerHTML =
                                        '<div class="text-center py-5">' +
                                        '<div class="spinner-border text-primary" role="status">' +
                                        '<span class="visually-hidden">Cargando...</span>' +
                                        '</div>' +
                                        '<p class="mt-3 text-muted">Cargando pendientes...</p>' +
                                        '</div>';

                                fetch('RecordatoriosServlet?accion=pendientesHoy')
                                        .then(function (r) {
                                            return r.text();
                                        })
                                        .then(function (html) {
                                            var contenido =
                                                    '<div class="card-custom">' +
                                                    '<div class="card-custom-header">' +
                                                    '<h5><i class="fas fa-clock"></i> Recordatorios Pendientes de Hoy</h5>' +
                                                    '</div>' +
                                                    '<div class="card-custom-body">' +
                                                    '<div class="table-responsive">' +
                                                    '<table class="table table-hover">' +
                                                    '<thead>' +
                                                    '<tr>' +
                                                    '<th>Cliente</th>' +
                                                    '<th>Contacto</th>' +
                                                    '<th>Mascota</th>' +
                                                    '<th>Veterinario</th>' +
                                                    '<th>Fecha/Hora Cita</th>' +
                                                    '<th>Canal</th>' +
                                                    '<th>Estado</th>' +
                                                    '<th>Mensaje</th>' +
                                                    '</tr>' +
                                                    '</thead>' +
                                                    '<tbody>' + html + '</tbody>' +
                                                    '</table>' +
                                                    '</div>' +
                                                    '</div>' +
                                                    '</div>';

                                            document.getElementById('contenidoDinamico').innerHTML = contenido;
                                        })
                                        .catch(function (err) {
                                            console.error('Error:', err);
                                            document.getElementById('contenidoDinamico').innerHTML =
                                                    '<div class="alert alert-danger">Error al cargar pendientes</div>';
                                        });
                            }

                            function cargarEstadisticas() {
                                // Actualizar tabs activos
                                document.querySelectorAll('.nav-link').forEach(function (tab) {
                                    tab.classList.remove('active');
                                });
                                document.querySelector('.nav-link[onclick="cargarEstadisticas()"]').classList.add('active');

                                // Mostrar loading
                                document.getElementById('contenidoDinamico').innerHTML =
                                        '<div class="text-center py-5">' +
                                        '<div class="spinner-border text-primary" role="status">' +
                                        '<span class="visually-hidden">Cargando...</span>' +
                                        '</div>' +
                                        '<p class="mt-3 text-muted">Cargando estadísticas...</p>' +
                                        '</div>';

                                // Cargar estadísticas de hoy
                                fetch('RecordatoriosServlet?accion=obtenerEstadisticas')
                                        .then(function (r) {
                                            return r.text();
                                        })
                                        .then(function (htmlHoy) {
                                            // Cargar estadísticas semanales
                                            fetch('RecordatoriosServlet?accion=obtenerEstadisticasSemanales')
                                                    .then(function (r) {
                                                        return r.text();
                                                    })
                                                    .then(function (htmlSemanales) {
                                                        var contenido =
                                                                '<div class="card-custom mb-4">' +
                                                                '<div class="card-custom-header">' +
                                                                '<h5><i class="fas fa-calendar-day"></i> Estadísticas de Hoy</h5>' +
                                                                '</div>' +
                                                                '<div class="card-custom-body">' + htmlHoy + '</div>' +
                                                                '</div>' +
                                                                '<div class="card-custom">' +
                                                                '<div class="card-custom-header">' +
                                                                '<h5><i class="fas fa-chart-line"></i> Últimos 7 Días</h5>' +
                                                                '</div>' +
                                                                '<div class="card-custom-body">' +
                                                                '<div class="table-responsive">' +
                                                                '<table class="table">' +
                                                                '<thead>' +
                                                                '<tr>' +
                                                                '<th>Fecha</th>' +
                                                                '<th>Total</th>' +
                                                                '<th>Enviados</th>' +
                                                                '<th>Fallados</th>' +
                                                                '<th>Pendientes</th>' +
                                                                '<th>Promedio Intentos</th>' +
                                                                '</tr>' +
                                                                '</thead>' +
                                                                '<tbody>' + htmlSemanales + '</tbody>' +
                                                                '</table>' +
                                                                '</div>' +
                                                                '</div>' +
                                                                '</div>';

                                                        document.getElementById('contenidoDinamico').innerHTML = contenido;
                                                    })
                                                    .catch(function (err) {
                                                        console.error('Error:', err);
                                                    });
                                        })
                                        .catch(function (err) {
                                            console.error('Error:', err);
                                            document.getElementById('contenidoDinamico').innerHTML =
                                                    '<div class="alert alert-danger">Error al cargar estadísticas</div>';
                                        });
                            }

                            function cargarConfiguracion() {
                                // Actualizar tabs activos
                                document.querySelectorAll('.nav-link').forEach(function (tab) {
                                    tab.classList.remove('active');
                                });
                                document.querySelector('.nav-link[onclick="cargarConfiguracion()"]').classList.add('active');

                                // Mostrar loading
                                document.getElementById('contenidoDinamico').innerHTML =
                                        '<div class="text-center py-5">' +
                                        '<div class="spinner-border text-primary" role="status">' +
                                        '<span class="visually-hidden">Cargando...</span>' +
                                        '</div>' +
                                        '<p class="mt-3 text-muted">Cargando configuración...</p>' +
                                        '</div>';

                                fetch('RecordatoriosServlet?accion=obtenerConfiguracion')
                                        .then(function (r) {
                                            return r.text();
                                        })
                                        .then(function (html) {
                                            var contenido =
                                                    '<div class="card-custom">' +
                                                    '<div class="card-custom-header">' +
                                                    '<h5><i class="fas fa-cog"></i> Configuración del Sistema de Recordatorios</h5>' +
                                                    '</div>' +
                                                    '<div class="card-custom-body">' + html + '</div>' +
                                                    '</div>';

                                            document.getElementById('contenidoDinamico').innerHTML = contenido;
                                        })
                                        .catch(function (err) {
                                            console.error('Error:', err);
                                            document.getElementById('contenidoDinamico').innerHTML =
                                                    '<div class="alert alert-danger">Error al cargar configuración</div>';
                                        });
                            }

                            // ===================== ACCIONES =====================
                            function generarRecordatorios() {
                                if (!confirm('¿Desea generar los recordatorios para las citas próximas?'))
                                    return;

                                fetch('RecordatoriosServlet?accion=generar')
                                        .then(function (r) {
                                            return r.text();
                                        })
                                        .then(function (respuesta) {
                                            var partes = respuesta.split('|');
                                            var tipo = partes[0] === 'OK' ? 'success' : 'danger';
                                            var mensaje = partes.length > 1 ? partes[1] : respuesta;

                                            mostrarMensaje(tipo, mensaje);
                                            if (partes[0] === 'OK') {
                                                // Recargar la vista actual
                                                cargarSupervision();
                                            }
                                        })
                                        .catch(function (err) {
                                            console.error('Error:', err);
                                            mostrarMensaje('danger', 'Error al generar recordatorios');
                                        });
                            }

                            function procesarEnvios() {
                                if (!confirm('¿Desea procesar y enviar los recordatorios pendientes?'))
                                    return;

                                fetch('RecordatoriosServlet?accion=procesar')
                                        .then(function (r) {
                                            return r.text();
                                        })
                                        .then(function (respuesta) {
                                            var partes = respuesta.split('|');
                                            var tipo = partes[0] === 'OK' ? 'success' : 'danger';
                                            var mensaje = partes.length > 1 ? partes[1] : respuesta;

                                            mostrarMensaje(tipo, mensaje);
                                            if (partes[0] === 'OK') {
                                                cargarSupervision();
                                            }
                                        })
                                        .catch(function (err) {
                                            console.error('Error:', err);
                                            mostrarMensaje('danger', 'Error al procesar envíos');
                                        });
                            }

                            function reintentarFallados() {
                                if (!confirm('¿Desea reintentar el envío de los recordatorios fallados?'))
                                    return;

                                fetch('RecordatoriosServlet?accion=reintentar')
                                        .then(function (r) {
                                            return r.text();
                                        })
                                        .then(function (respuesta) {
                                            var partes = respuesta.split('|');
                                            var tipo = partes[0] === 'OK' ? 'success' : 'danger';
                                            var mensaje = partes.length > 1 ? partes[1] : respuesta;

                                            mostrarMensaje(tipo, mensaje);
                                            if (partes[0] === 'OK') {
                                                cargarSupervision();
                                            }
                                        })
                                        .catch(function (err) {
                                            console.error('Error:', err);
                                            mostrarMensaje('danger', 'Error al reintentar envíos');
                                        });
                            }

                            function guardarConfiguracion() {
                                var form = document.getElementById('formConfig');
                                if (!form) {
                                    mostrarMensaje('danger', 'Formulario no encontrado');
                                    return;
                                }

                                // Obtener valores directamente
                                var horasAntes = form.horasAntes.value;
                                var horaEnvio = form.horaEnvio.value;
                                var canalPrincipal = form.canalPrincipal.value;
                                var plantilla = form.plantillaMensaje.value;
                                var maxReintentos = form.maxReintentos.value;
                                var habilitado = form.habilitado.checked ? 'on' : 'off';

                                if (!horasAntes || !horaEnvio || !plantilla || !canalPrincipal) {
                                    mostrarMensaje('warning', 'Complete todos los campos obligatorios');
                                    return;
                                }

                                // Construir URL con parámetros
                                var url = 'RecordatoriosServlet?accion=actualizarConfig' +
                                        '&horasAntes=' + encodeURIComponent(horasAntes) +
                                        '&horaEnvio=' + encodeURIComponent(horaEnvio) +
                                        '&canalPrincipal=' + encodeURIComponent(canalPrincipal) +
                                        '&plantillaMensaje=' + encodeURIComponent(plantilla) +
                                        '&maxReintentos=' + encodeURIComponent(maxReintentos) +
                                        '&habilitado=' + encodeURIComponent(habilitado);

                                console.log('URL:', url);

                                fetch(url, {method: 'POST'})
                                        .then(function (r) {
                                            return r.text();
                                        })
                                        .then(function (respuesta) {
                                            console.log('Respuesta:', respuesta);

                                            var partes = respuesta.split('|');
                                            var tipo = partes[0] === 'OK' ? 'success' : 'danger';
                                            var mensaje = partes.length > 1 ? partes[1] : respuesta;

                                            mostrarMensaje(tipo, mensaje);
                                            if (partes[0] === 'OK') {
                                                setTimeout(function () {
                                                    cargarConfiguracion();
                                                }, 1000);
                                            }
                                        })
                                        .catch(function (err) {
                                            console.error('Error:', err);
                                            mostrarMensaje('danger', 'Error: ' + err.message);
                                        });
                            }
        </script>
    </body>
</html>
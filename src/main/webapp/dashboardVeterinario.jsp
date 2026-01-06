<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String usuario = (String) session.getAttribute("usuario");
    String nombre = (String) session.getAttribute("nombre");
    Integer idRol = (Integer) session.getAttribute("rol");

    if (usuario == null || nombre == null || idRol == null) {
        response.sendRedirect("login.jsp");
        return;
    }

    // SOLO VETERINARIO (rol = 3)
    if (idRol != 3) {
        response.sendRedirect("accesoDenegado.jsp");
        return;
    }
%>

<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Dashboard Veterinario - DiazPet</title>
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
                position: relative;
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

            .badge-notify {
                position: absolute;
                right: 20px;
                background: #ff4757;
                color: white;
                padding: 2px 8px;
                border-radius: 12px;
                font-size: 11px;
                font-weight: bold;
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

            .stat-card.green {
                border-left-color: #10b981;
            }
            .stat-card.blue {
                border-left-color: #3b82f6;
            }
            .stat-card.orange {
                border-left-color: #ff8c42;
            }
            .stat-card.purple {
                border-left-color: #a78bfa;
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

            .stat-card.green .stat-card-icon {
                background: #d1fae5;
                color: #10b981;
            }
            .stat-card.blue .stat-card-icon {
                background: #dbeafe;
                color: #3b82f6;
            }
            .stat-card.orange .stat-card-icon {
                background: #fff4e6;
                color: #ff8c42;
            }
            .stat-card.purple .stat-card-icon {
                background: #ede9fe;
                color: #a78bfa;
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

            .card-custom {
                background: white;
                border-radius: 10px;
                box-shadow: 0 2px 10px rgba(0,0,0,0.05);
                margin-bottom: 30px;
            }

            .card-custom-header {
                padding: 20px 25px;
                border-bottom: 1px solid #f0f0f0;
                display: flex;
                align-items: center;
                justify-content: space-between;
            }

            .card-custom-header h5 {
                margin: 0;
                font-size: 18px;
                font-weight: bold;
                color: #2c3e50;
            }

            .card-custom-body {
                padding: 25px;
            }

            .patient-item {
                display: flex;
                align-items: center;
                justify-content: space-between;
                padding: 15px;
                background: #f8f9fa;
                border-radius: 8px;
                margin-bottom: 12px;
                transition: all 0.3s;
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

            .patient-status {
                padding: 6px 15px;
                border-radius: 20px;
                font-size: 12px;
                font-weight: 600;
            }

            .status-waiting {
                background: #fff4e6;
                color: #ff8c42;
            }
            .status-inprogress {
                background: #dbeafe;
                color: #3b82f6;
            }
            .status-completed {
                background: #d1fae5;
                color: #10b981;
            }

            .quick-actions {
                display: grid;
                grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
                gap: 15px;
            }

            .quick-action-btn {
                padding: 20px;
                background: white;
                border: 2px solid #f0f0f0;
                border-radius: 10px;
                text-align: center;
                cursor: pointer;
                transition: all 0.3s;
                text-decoration: none;
                color: inherit;
                display: block;
            }

            .quick-action-btn:hover {
                border-color: var(--primary-color);
                transform: translateY(-5px);
                box-shadow: 0 5px 20px rgba(0,0,0,0.1);
            }

            .quick-action-btn i {
                font-size: 32px;
                margin-bottom: 10px;
                display: block;
            }

            .quick-action-btn.green i {
                color: #10b981;
            }
            .quick-action-btn.blue i {
                color: #3b82f6;
            }
            .quick-action-btn.orange i {
                color: #ff8c42;
            }
            .quick-action-btn.purple i {
                color: #a78bfa;
            }

            .quick-action-btn span {
                font-weight: 600;
                color: #2c3e50;
                font-size: 14px;
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
                <a href="#" class="menu-item active" onclick="showModule('inicio')">
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

        <div class="main-content">
            <div class="top-bar">
                <div class="d-flex justify-content-between align-items-center">
                    <div>
                        <h2 id="pageTitle">Panel Veterinario</h2>
                        <p class="date mb-0">
                            <i class="far fa-calendar"></i>
                            <span id="currentDate"></span>
                        </p>
                    </div>
                </div>
            </div>

            <div class="content-area">
                <div id="module-inicio">
                    <!-- ESTADÍSTICAS -->
                    <div class="stats-grid">
                        <div class="stat-card green">
                            <div class="stat-card-icon">
                                <i class="fas fa-calendar-check"></i>
                            </div>
                            <h6>MIS CITAS HOY</h6>
                            <h2 id="statCitasHoy">0</h2>
                        </div>

                        <div class="stat-card blue">
                            <div class="stat-card-icon">
                                <i class="fas fa-stethoscope"></i>
                            </div>
                            <h6>CONSULTAS EN CURSO</h6>
                            <h2 id="statConsultasEnCurso">0</h2>
                        </div>

                        <div class="stat-card orange">
                            <div class="stat-card-icon">
                                <i class="fas fa-flask"></i>
                            </div>
                            <h6>ESTUDIOS PENDIENTES</h6>
                            <h2 id="statEstudiosPendientes">0</h2>
                        </div>

                        <div class="stat-card purple">
                            <div class="stat-card-icon">
                                <i class="fas fa-paw"></i>
                            </div>
                            <h6>PACIENTES ATENDIDOS (MES)</h6>
                            <h2 id="statPacientesAtendidos">0</h2>
                        </div>
                    </div>

                    <!-- GRÁFICO + TOP RAZAS -->
                    <div class="row mb-4">
                        <div class="col-md-8">
                            <div class="card-custom">
                                <div class="card-custom-header">
                                    <h5><i class="fas fa-chart-line"></i> Consultas de la Semana</h5>
                                </div>
                                <div class="card-custom-body">
                                    <canvas id="graficoConsultas" style="max-height: 300px;"></canvas>
                                </div>
                            </div>
                        </div>

                        <div class="col-md-4">
                            <div class="card-custom">
                                <div class="card-custom-header">
                                    <h5><i class="fas fa-trophy"></i> Top Razas del Mes</h5>
                                </div>
                                <div class="card-custom-body" id="contenedorTopRazas" style="max-height: 300px; overflow-y: auto;">
                                    <div class="text-center py-4">
                                        <div class="spinner-border text-primary"></div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>

                    <!-- AGENDA + ÚLTIMAS CONSULTAS -->
                    <div class="row">
                        <div class="col-md-8">
                            <div class="card-custom">
                                <div class="card-custom-header">
                                    <h5><i class="fas fa-clock"></i> Agenda de Hoy</h5>
                                    <button class="btn-primary-custom" onclick="window.location.href = 'gestionarConsultas.jsp'">
                                        <i class="fas fa-plus"></i> Ir a Consultas
                                    </button>
                                </div>
                                <div class="card-custom-body" id="contenedorAgenda">
                                    <div class="text-center py-4">
                                        <div class="spinner-border text-primary"></div>
                                        <p class="mt-2">Cargando agenda...</p>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <div class="col-md-4">
                            <div class="card-custom">
                                <div class="card-custom-header">
                                    <h5><i class="fas fa-history"></i> Últimas Consultas</h5>
                                </div>
                                <div class="card-custom-body" id="contenedorUltimasConsultas" style="max-height: 400px; overflow-y: auto;">
                                    <div class="text-center py-4">
                                        <div class="spinner-border text-primary"></div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                <div id="module-citas" style="display: none;">
                    <div class="card-custom">
                        <div class="card-custom-body text-center py-5">
                            <i class="fas fa-calendar-check" style="font-size: 64px; color: #10b981; margin-bottom: 20px;"></i>
                            <h3>Mis Citas</h3>
                            <p class="text-muted">Gestiona tu agenda de citas programadas.</p>
                            <button class="btn-primary-custom mt-3">Ver Agenda Completa</button>
                        </div>
                    </div>
                </div>

                <div id="module-historial" style="display: none;">
                    <div class="card-custom">
                        <div class="card-custom-body text-center py-5">
                            <i class="fas fa-clipboard-list" style="font-size: 64px; color: #a78bfa; margin-bottom: 20px;"></i>
                            <h3>Historial Clínico</h3>
                            <p class="text-muted">Accede al historial médico completo de pacientes.</p>
                            <button class="btn-primary-custom mt-3">Buscar Paciente</button>
                        </div>
                    </div>
                </div>

                <div id="module-diagnosticos" style="display: none;">
                    <div class="card-custom">
                        <div class="card-custom-body text-center py-5">
                            <i class="fas fa-diagnoses" style="font-size: 64px; color: #ff8c42; margin-bottom: 20px;"></i>
                            <h3>Diagnósticos</h3>
                            <p class="text-muted">Registra diagnósticos médicos y hallazgos.</p>
                            <button class="btn-primary-custom mt-3">Nuevo Diagnóstico</button>
                        </div>
                    </div>
                </div>

                <div id="module-recetas" style="display: none;">
                    <div class="card-custom">
                        <div class="card-custom-body text-center py-5">
                            <i class="fas fa-prescription" style="font-size: 64px; color: #10b981; margin-bottom: 20px;"></i>
                            <h3>Recetas Médicas</h3>
                            <p class="text-muted">Genera y administra recetas para tratamientos.</p>
                            <button class="btn-primary-custom mt-3">Nueva Receta</button>
                        </div>
                    </div>
                </div>

                <div id="module-estudios" style="display: none;">
                    <div class="card-custom">
                        <div class="card-custom-body text-center py-5">
                            <i class="fas fa-flask" style="font-size: 64px; color: #3b82f6; margin-bottom: 20px;"></i>
                            <h3>Estudios y Análisis</h3>
                            <p class="text-muted">Solicita y revisa órdenes de estudios médicos.</p>
                            <button class="btn-primary-custom mt-3">Solicitar Estudio</button>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <script src="https://cdnjs.cloudflare.com/ajax/libs/bootstrap/5.3.0/js/bootstrap.bundle.min.js"></script>
        <script src="https://cdn.jsdelivr.net/npm/chart.js@4.4.0/dist/chart.umd.min.js"></script>
        <script>
                                        var graficoConsultas = null;

                                        function toggleSidebar() {
                                            document.getElementById('sidebar').classList.toggle('collapsed');
                                        }

                                        function setCurrentDate() {
                                            var options = {weekday: 'long', year: 'numeric', month: 'long', day: 'numeric'};
                                            var date = new Date().toLocaleDateString('es-ES', options);
                                            document.getElementById('currentDate').textContent = date.charAt(0).toUpperCase() + date.slice(1);
                                        }

                                        function cargarEstadisticas() {
                                            fetch('DashboardVeteServlet?accion=obtenerEstadisticas')
                                                    .then(function (r) {
                                                        return r.text();
                                                    })
                                                    .then(function (respuesta) {
                                                        var datos = respuesta.split('|');
                                                        animarNumero('statCitasHoy', parseInt(datos[0]));
                                                        animarNumero('statConsultasEnCurso', parseInt(datos[1]));
                                                        animarNumero('statEstudiosPendientes', parseInt(datos[2]));
                                                        animarNumero('statPacientesAtendidos', parseInt(datos[3]));
                                                    })
                                                    .catch(function (error) {
                                                        console.error('Error al cargar estadísticas:', error);
                                                    });
                                        }

                                        function cargarAgendaHoy() {
                                            var contenedor = document.getElementById('contenedorAgenda');
                                            contenedor.innerHTML = '<div class="text-center py-4"><div class="spinner-border text-primary"></div><p class="mt-2">Cargando agenda...</p></div>';

                                            fetch('DashboardVeteServlet?accion=obtenerAgendaHoy')
                                                    .then(function (r) {
                                                        return r.text();
                                                    })
                                                    .then(function (html) {
                                                        contenedor.innerHTML = html;
                                                    })
                                                    .catch(function (error) {
                                                        console.error('Error al cargar agenda:', error);
                                                        contenedor.innerHTML = '<div class="alert alert-danger">Error al cargar la agenda</div>';
                                                    });
                                        }

                                        function cargarGraficoConsultas() {
                                            fetch('DashboardVeteServlet?accion=obtenerDatosGrafico')
                                                    .then(function (r) {
                                                        return r.text();
                                                    })
                                                    .then(function (respuesta) {
                                                        var partes = respuesta.split('|');
                                                        var labels = partes[0] ? partes[0].split(',') : [];
                                                        var values = partes[1] ? partes[1].split(',').map(function (v) {
                                                            return parseInt(v);
                                                        }) : [];

                                                        var ctx = document.getElementById('graficoConsultas').getContext('2d');

                                                        if (graficoConsultas) {
                                                            graficoConsultas.destroy();
                                                        }

                                                        graficoConsultas = new Chart(ctx, {
                                                            type: 'line',
                                                            data: {
                                                                labels: labels,
                                                                datasets: [{
                                                                        label: 'Consultas Finalizadas',
                                                                        data: values,
                                                                        borderColor: '#10b981',
                                                                        backgroundColor: 'rgba(16, 185, 129, 0.1)',
                                                                        tension: 0.4,
                                                                        fill: true,
                                                                        pointRadius: 5,
                                                                        pointBackgroundColor: '#10b981'
                                                                    }]
                                                            },
                                                            options: {
                                                                responsive: true,
                                                                maintainAspectRatio: true,
                                                                plugins: {
                                                                    legend: {
                                                                        display: false
                                                                    }
                                                                },
                                                                scales: {
                                                                    y: {
                                                                        beginAtZero: true,
                                                                        ticks: {
                                                                            stepSize: 1
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        });
                                                    })
                                                    .catch(function (error) {
                                                        console.error('Error al cargar gráfico:', error);
                                                    });
                                        }

                                        function cargarTopRazas() {
                                            var contenedor = document.getElementById('contenedorTopRazas');
                                            contenedor.innerHTML = '<div class="text-center py-4"><div class="spinner-border text-primary"></div></div>';

                                            fetch('DashboardVeteServlet?accion=obtenerTopRazas')
                                                    .then(function (r) {
                                                        return r.text();
                                                    })
                                                    .then(function (html) {
                                                        contenedor.innerHTML = html;
                                                    })
                                                    .catch(function (error) {
                                                        console.error('Error al cargar top razas:', error);
                                                    });
                                        }

                                        function cargarUltimasConsultas() {
                                            var contenedor = document.getElementById('contenedorUltimasConsultas');
                                            contenedor.innerHTML = '<div class="text-center py-4"><div class="spinner-border text-primary"></div></div>';

                                            fetch('DashboardVeteServlet?accion=obtenerUltimasConsultas')
                                                    .then(function (r) {
                                                        return r.text();
                                                    })
                                                    .then(function (html) {
                                                        contenedor.innerHTML = html;
                                                    })
                                                    .catch(function (error) {
                                                        console.error('Error al cargar últimas consultas:', error);
                                                    });
                                        }

                                        function animarNumero(elementId, valorFinal) {
                                            var elemento = document.getElementById(elementId);
                                            var duracion = 1000;
                                            var pasos = 30;
                                            var incremento = valorFinal / pasos;
                                            var valorActual = 0;
                                            var intervalo = duracion / pasos;

                                            var timer = setInterval(function () {
                                                valorActual += incremento;
                                                if (valorActual >= valorFinal) {
                                                    valorActual = valorFinal;
                                                    clearInterval(timer);
                                                }
                                                elemento.textContent = Math.round(valorActual);
                                            }, intervalo);
                                        }

                                        document.addEventListener('DOMContentLoaded', function () {
                                            setCurrentDate();
                                            cargarEstadisticas();
                                            cargarAgendaHoy();
                                            cargarGraficoConsultas();
                                            cargarTopRazas();
                                            cargarUltimasConsultas();

                                            setInterval(function () {
                                                cargarEstadisticas();
                                                cargarAgendaHoy();
                                                cargarGraficoConsultas();
                                                cargarTopRazas();
                                                cargarUltimasConsultas();
                                            }, 300000);
                                        });
        </script>
    </body>
</html>
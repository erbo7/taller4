<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String usuario = (String) session.getAttribute("usuario");
    String nombre = (String) session.getAttribute("nombre");
    Integer idRol = (Integer) session.getAttribute("rol");

    if (usuario == null || nombre == null || idRol == null) {
        response.sendRedirect("login.jsp");
        return;
    }

    // SOLO RECEPCIONISTA (rol = 2)
    if (idRol != 2) {
        response.sendRedirect("accesoDenegado.jsp");
        return;
    }
%>


<!DOCTYPE html>
<html lang="es">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Dashboard Recepcionista - DiazPet</title>
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

            /* Sidebar */
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

            /* Main Content */
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

            .search-box {
                position: relative;
                width: 350px;
            }

            .search-box input {
                width: 100%;
                padding: 10px 15px 10px 45px;
                border: 2px solid #e0e0e0;
                border-radius: 25px;
                transition: all 0.3s;
            }

            .search-box input:focus {
                border-color: var(--primary-color);
                outline: none;
                box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
            }

            .search-box i {
                position: absolute;
                left: 18px;
                top: 50%;
                transform: translateY(-50%);
                color: #999;
            }

            .content-area {
                padding: 0 30px 30px;
            }

            /* Stats Cards */
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
            .stat-card.orange {
                border-left-color: #ff8c42;
            }
            .stat-card.green {
                border-left-color: #10b981;
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
            .stat-card.orange .stat-card-icon {
                background: #fff4e6;
                color: #ff8c42;
            }
            .stat-card.green .stat-card-icon {
                background: #d1fae5;
                color: #10b981;
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

            /* Card */
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

            /* Appointment Item */
            .appointment-item {
                display: flex;
                align-items: center;
                justify-content: space-between;
                padding: 15px;
                background: #f8f9fa;
                border-radius: 8px;
                margin-bottom: 12px;
                transition: all 0.3s;
            }

            .appointment-item:hover {
                background: #e9ecef;
            }

            .appointment-time {
                background: var(--primary-color);
                color: white;
                padding: 10px 15px;
                border-radius: 8px;
                font-weight: bold;
                min-width: 80px;
                text-align: center;
            }

            .appointment-info {
                flex: 1;
                margin-left: 20px;
            }

            .appointment-info h6 {
                margin: 0 0 5px;
                font-weight: bold;
                color: #2c3e50;
            }

            .appointment-info p {
                margin: 0;
                font-size: 13px;
                color: #999;
            }

            .appointment-status {
                padding: 6px 15px;
                border-radius: 20px;
                font-size: 12px;
                font-weight: 600;
            }

            .status-confirmed {
                background: #d1fae5;
                color: #10b981;
            }
            .status-reserved {
                background: #dbeafe;
                color: #3b82f6;
            }

            /* Quick Actions */
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

            .quick-action-btn.purple i {
                color: #a78bfa;
            }
            .quick-action-btn.green i {
                color: #10b981;
            }
            .quick-action-btn.orange i {
                color: #ff8c42;
            }
            .quick-action-btn.pink i {
                color: #ec4899;
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
                box-shadow: 0 5px 15px rgba(102, 126, 234, 0.4);
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
        <!-- Sidebar -->
        <div class="sidebar" id="sidebar">
            <div class="sidebar-header">
                <div>
                    <h3>🐾 DiazPet</h3>
                    <small style="color: rgba(255,255,255,0.8);">Recepcionista</small>
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
            <!-- Top Bar -->
            <div class="top-bar">
                <div class="d-flex justify-content-between align-items-center">
                    <div>
                        <h2 id="pageTitle">Inicio</h2>
                        <p class="date mb-0">
                            <i class="far fa-calendar"></i>
                            <span id="currentDate"></span>
                        </p>
                    </div>
                    <div class="search-box">
                        <i class="fas fa-search"></i>
                        <input type="text" placeholder="Buscar cliente, mascota...">
                    </div>
                </div>
            </div>

            <!-- Content Area -->
            <div class="content-area">
                <!-- Módulo Inicio -->
                <div id="module-inicio">
                    <!-- Stats Cards -->
                    <div class="stats-grid">
                        <div class="stat-card blue">
                            <div class="stat-card-icon">
                                <i class="fas fa-calendar-check"></i>
                            </div>
                            <h6>CITAS DE HOY</h6>
                            <h2>8</h2>
                        </div>

                        <div class="stat-card orange">
                            <div class="stat-card-icon">
                                <i class="fas fa-bell"></i>
                            </div>
                            <h6>RECORDATORIOS</h6>
                            <h2>5</h2>
                        </div>

                        <div class="stat-card green">
                            <div class="stat-card-icon">
                                <i class="fas fa-user-check"></i>
                            </div>
                            <h6>CLIENTES ATENDIDOS</h6>
                            <h2>12</h2>
                        </div>
                    </div>

                    <!-- Citas del Día -->
                    <div class="card-custom">
                        <div class="card-custom-header">
                            <h5><i class="fas fa-calendar-day"></i> Citas de Hoy</h5>
                            <button class="btn-primary-custom">
                                <i class="fas fa-plus"></i> Nueva Cita
                            </button>
                        </div>
                        <div class="card-custom-body">
                            <div class="appointment-item">
                                <div class="appointment-time">09:00</div>
                                <div class="appointment-info">
                                    <h6>María García</h6>
                                    <p>Mascota: Max • Dr. Rodríguez</p>
                                </div>
                                <span class="appointment-status status-confirmed">Confirmada</span>
                                <button class="btn-outline-custom ms-3">Ver Detalles</button>
                            </div>

                            <div class="appointment-item">
                                <div class="appointment-time">10:30</div>
                                <div class="appointment-info">
                                    <h6>Juan Pérez</h6>
                                    <p>Mascota: Luna • Dra. López</p>
                                </div>
                                <span class="appointment-status status-reserved">Reservada</span>
                                <button class="btn-outline-custom ms-3">Ver Detalles</button>
                            </div>

                            <div class="appointment-item">
                                <div class="appointment-time">14:00</div>
                                <div class="appointment-info">
                                    <h6>Ana Torres</h6>
                                    <p>Mascota: Rocky • Dr. Rodríguez</p>
                                </div>
                                <span class="appointment-status status-confirmed">Confirmada</span>
                                <button class="btn-outline-custom ms-3">Ver Detalles</button>
                            </div>

                            <div class="appointment-item">
                                <div class="appointment-time">16:00</div>
                                <div class="appointment-info">
                                    <h6>Carlos Ruiz</h6>
                                    <p>Mascota: Bella • Dra. López</p>
                                </div>
                                <span class="appointment-status status-reserved">Reservada</span>
                                <button class="btn-outline-custom ms-3">Ver Detalles</button>
                            </div>
                        </div>
                    </div>

                    <!-- Quick Actions -->
                    <div class="card-custom">
                        <div class="card-custom-header">
                            <h5><i class="fas fa-bolt"></i> Accesos Rápidos</h5>
                        </div>
                        <div class="card-custom-body">
                            <div class="quick-actions">
                                <a href="gestionarAgenda.jsp" class="quick-action-btn purple">
                                    <i class="fas fa-calendar-alt"></i>
                                    <span>Gestionar Agenda</span>
                                </a>
                                <a href="#" class="quick-action-btn green" onclick="showModule('citas'); return false;">
                                    <i class="fas fa-clock"></i>
                                    <span>Reservar Cita</span>
                                </a>
                                <a href="#" class="quick-action-btn orange" onclick="showModule('recordatorios'); return false;">
                                    <i class="fas fa-bell"></i>
                                    <span>Recordatorios</span>
                                </a>
                                <a href="#" class="quick-action-btn pink" onclick="showModule('clientes'); return false;">
                                    <i class="fas fa-users"></i>
                                    <span>Nuevo Cliente</span>
                                </a>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- Otros módulos (ocultos por defecto) -->
                <div id="module-agenda" style="display: none;">
                    <div class="card-custom">
                        <div class="card-custom-body text-center py-5">
                            <i class="fas fa-calendar-alt" style="font-size: 64px; color: #a78bfa; margin-bottom: 20px;"></i>
                            <h3>Módulo de Agenda</h3>
                            <p class="text-muted">Este módulo está en desarrollo. Aquí podrás gestionar la disponibilidad de los veterinarios.</p>
                            <button class="btn-primary-custom mt-3">Comenzar a usar</button>
                        </div>
                    </div>
                </div>

                <div id="module-citas" style="display: none;">
                    <div class="card-custom">
                        <div class="card-custom-body text-center py-5">
                            <i class="fas fa-clock" style="font-size: 64px; color: #10b981; margin-bottom: 20px;"></i>
                            <h3>Módulo de Citas</h3>
                            <p class="text-muted">Gestiona reservas, confirmaciones, reprogramaciones y anulaciones de citas.</p>
                            <button class="btn-primary-custom mt-3">Comenzar a usar</button>
                        </div>
                    </div>
                </div>

                <div id="module-recordatorios" style="display: none;">
                    <div class="card-custom">
                        <div class="card-custom-body text-center py-5">
                            <i class="fas fa-bell" style="font-size: 64px; color: #ff8c42; margin-bottom: 20px;"></i>
                            <h3>Módulo de Recordatorios</h3>
                            <p class="text-muted">Configura y supervisa los recordatorios automáticos a clientes.</p>
                            <button class="btn-primary-custom mt-3">Comenzar a usar</button>
                        </div>
                    </div>
                </div>

                <div id="module-documentos" style="display: none;">
                    <div class="card-custom">
                        <div class="card-custom-body text-center py-5">
                            <i class="fas fa-file-alt" style="font-size: 64px; color: #667eea; margin-bottom: 20px;"></i>
                            <h3>Módulo de Documentos</h3>
                            <p class="text-muted">Adjunta y gestiona documentos en las fichas clínicas.</p>
                            <button class="btn-primary-custom mt-3">Comenzar a usar</button>
                        </div>
                    </div>
                </div>

                <div id="module-clientes" style="display: none;">
                    <div class="card-custom">
                        <div class="card-custom-body text-center py-5">
                            <i class="fas fa-users" style="font-size: 64px; color: #ec4899; margin-bottom: 20px;"></i>
                            <h3>Módulo de Clientes</h3>
                            <p class="text-muted">Registra y administra la información de clientes y mascotas.</p>
                            <button class="btn-primary-custom mt-3">Comenzar a usar</button>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <script src="https://cdnjs.cloudflare.com/ajax/libs/bootstrap/5.3.0/js/bootstrap.bundle.min.js"></script>
        <script>
                                    // Toggle Sidebar
                                    function toggleSidebar() {
                                        document.getElementById('sidebar').classList.toggle('collapsed');
                                    }

                                    // Show Module
                                    function showModule(moduleName) {
                                        // Ocultar todos los módulos
                                        const modules = ['inicio', 'agenda', 'citas', 'recordatorios', 'documentos', 'clientes'];
                                        modules.forEach(mod => {
                                            document.getElementById('module-' + mod).style.display = 'none';
                                        });

                                        // Mostrar módulo seleccionado
                                        document.getElementById('module-' + moduleName).style.display = 'block';

                                        // Actualizar título
                                        const titles = {
                                            'inicio': 'Inicio',
                                            'agenda': 'Gestionar Agenda',
                                            'citas': 'Gestionar Citas',
                                            'recordatorios': 'Recordatorios',
                                            'documentos': 'Documentos',
                                            'clientes': 'Clientes'
                                        };
                                        document.getElementById('pageTitle').textContent = titles[moduleName];

                                        // Actualizar menú activo
                                        document.querySelectorAll('.menu-item').forEach(item => {
                                            item.classList.remove('active');
                                        });
                                        event.target.closest('.menu-item').classList.add('active');
                                    }

                                    // Set current date
                                    function setCurrentDate() {
                                        const options = {weekday: 'long', year: 'numeric', month: 'long', day: 'numeric'};
                                        const date = new Date().toLocaleDateString('es-ES', options);
                                        document.getElementById('currentDate').textContent = date.charAt(0).toUpperCase() + date.slice(1);
                                    }

                                    setCurrentDate();
        </script>
    </body>
</html>
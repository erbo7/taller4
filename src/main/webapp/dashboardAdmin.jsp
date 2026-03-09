<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String usuario = (String) session.getAttribute("usuario");
    String nombre = (String) session.getAttribute("nombre");
    Integer idRol = (Integer) session.getAttribute("rol");

    if (usuario == null || nombre == null || idRol == null) {
        response.sendRedirect("login.jsp");
        return;
    }

    // SOLO ADMIN (rol = 1)
    if (idRol != 1) {
        response.sendRedirect("accesoDenegado.jsp");
        return;
    }
%>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Dashboard Admin - DiazPet</title>
    <link href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap/5.3.0/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
    <style>
        :root {
            --primary-color: #dc2626;
            --secondary-color: #991b1b;
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
        
        .stat-card.red { border-left-color: #dc2626; }
        .stat-card.blue { border-left-color: #3b82f6; }
        .stat-card.green { border-left-color: #10b981; }
        .stat-card.purple { border-left-color: #a78bfa; }
        
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
        
        .stat-card.red .stat-card-icon { background: #fee2e2; color: #dc2626; }
        .stat-card.blue .stat-card-icon { background: #dbeafe; color: #3b82f6; }
        .stat-card.green .stat-card-icon { background: #d1fae5; color: #10b981; }
        .stat-card.purple .stat-card-icon { background: #ede9fe; color: #a78bfa; }
        
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
        
        .quick-action-btn.red i { color: #dc2626; }
        .quick-action-btn.blue i { color: #3b82f6; }
        .quick-action-btn.green i { color: #10b981; }
        .quick-action-btn.purple i { color: #a78bfa; }
        
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
            box-shadow: 0 5px 15px rgba(220, 38, 38, 0.4);
        }
        
        .activity-item {
            display: flex;
            align-items: start;
            padding: 15px;
            background: #f8f9fa;
            border-radius: 8px;
            margin-bottom: 12px;
        }
        
        .activity-icon {
            width: 40px;
            height: 40px;
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            margin-right: 15px;
            flex-shrink: 0;
        }
        
        .activity-icon.red { background: #fee2e2; color: #dc2626; }
        .activity-icon.blue { background: #dbeafe; color: #3b82f6; }
        .activity-icon.green { background: #d1fae5; color: #10b981; }
        
        .activity-info h6 {
            margin: 0 0 5px;
            font-weight: bold;
            color: #2c3e50;
            font-size: 14px;
        }
        
        .activity-info p {
            margin: 0;
            font-size: 12px;
            color: #999;
        }
    </style>
</head>
<body>
    <div class="sidebar" id="sidebar">
        <div class="sidebar-header">
            <div>
                <h3>🔒 DiazPet</h3>
                <small style="color: rgba(255,255,255,0.8);">Administrador</small>
            </div>
            <button class="toggle-btn" onclick="toggleSidebar()">
                <i class="fas fa-bars"></i>
            </button>
        </div>
        
        <div class="user-section">
            <div class="user-info">
                <div class="user-avatar"><%= nombre.substring(0,1).toUpperCase() %></div>
                <span>
                    <strong><%= nombre %></strong><br>
                    <small style="opacity: 0.8;"><%= usuario %></small>
                </span>
            </div>
        </div>
        
        <div class="sidebar-menu">
            <a href="#" class="menu-item active" onclick="showModule('inicio')">
                <i class="fas fa-home"></i>
                <span class="menu-text">Inicio</span>
            </a>
            <a href="#" class="menu-item" onclick="showModule('usuarios')">
                <i class="fas fa-users-cog"></i>
                <span class="menu-text">Usuarios</span>
            </a>
            <a href="#" class="menu-item" onclick="showModule('roles')">
                <i class="fas fa-shield-alt"></i>
                <span class="menu-text">Roles</span>
            </a>
            <a href="#" class="menu-item" onclick="showModule('auditoria')">
                <i class="fas fa-history"></i>
                <span class="menu-text">Auditoría</span>
            </a>
            <a href="#" class="menu-item" onclick="showModule('configuracion')">
                <i class="fas fa-cog"></i>
                <span class="menu-text">Configuración</span>
            </a>
            <a href="#" class="menu-item" onclick="showModule('reportes')">
                <i class="fas fa-chart-bar"></i>
                <span class="menu-text">Reportes</span>
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
                    <h2 id="pageTitle">Panel de Administración</h2>
                    <p class="date mb-0">
                        <i class="far fa-calendar"></i>
                        <span id="currentDate"></span>
                    </p>
                </div>
            </div>
        </div>
        
        <div class="content-area">
            <div id="module-inicio">
                <div class="stats-grid">
                    <div class="stat-card red">
                        <div class="stat-card-icon">
                            <i class="fas fa-users"></i>
                        </div>
                        <h6>USUARIOS ACTIVOS</h6>
                        <h2>24</h2>
                    </div>
                    
                    <div class="stat-card blue">
                        <div class="stat-card-icon">
                            <i class="fas fa-calendar-check"></i>
                        </div>
                        <h6>CITAS HOY</h6>
                        <h2>18</h2>
                    </div>
                    
                    <div class="stat-card green">
                        <div class="stat-card-icon">
                            <i class="fas fa-dollar-sign"></i>
                        </div>
                        <h6>INGRESOS HOY</h6>
                        <h2>$2,450</h2>
                    </div>
                    
                    <div class="stat-card purple">
                        <div class="stat-card-icon">
                            <i class="fas fa-paw"></i>
                        </div>
                        <h6>MASCOTAS REGISTRADAS</h6>
                        <h2>342</h2>
                    </div>
                </div>
                
                <div class="row">
                    <div class="col-md-8">
                        <div class="card-custom">
                            <div class="card-custom-header">
                                <h5><i class="fas fa-history"></i> Actividad Reciente del Sistema</h5>
                            </div>
                            <div class="card-custom-body">
                                <div class="activity-item">
                                    <div class="activity-icon green">
                                        <i class="fas fa-user-plus"></i>
                                    </div>
                                    <div class="activity-info">
                                        <h6>Nuevo usuario registrado</h6>
                                        <p>Usuario "recepcionista2" creado por Admin • Hace 5 minutos</p>
                                    </div>
                                </div>
                                
                                <div class="activity-item">
                                    <div class="activity-icon blue">
                                        <i class="fas fa-edit"></i>
                                    </div>
                                    <div class="activity-info">
                                        <h6>Configuración actualizada</h6>
                                        <p>Horario de atención modificado • Hace 1 hora</p>
                                    </div>
                                </div>
                                
                                <div class="activity-item">
                                    <div class="activity-icon red">
                                        <i class="fas fa-shield-alt"></i>
                                    </div>
                                    <div class="activity-info">
                                        <h6>Intento de acceso fallido</h6>
                                        <p>Usuario desconocido intentó acceder • Hace 2 horas</p>
                                    </div>
                                </div>
                                
                                <div class="activity-item">
                                    <div class="activity-icon green">
                                        <i class="fas fa-database"></i>
                                    </div>
                                    <div class="activity-info">
                                        <h6>Respaldo completado</h6>
                                        <p>Base de datos respaldada exitosamente • Hace 3 horas</p>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    
                    <div class="col-md-4">
                        <div class="card-custom">
                            <div class="card-custom-header">
                                <h5><i class="fas fa-bolt"></i> Accesos Rápidos</h5>
                            </div>
                            <div class="card-custom-body">
                                <div class="quick-actions">
                                    <a href="#" class="quick-action-btn red" onclick="showModule('usuarios'); return false;">
                                        <i class="fas fa-users-cog"></i>
                                        <span>Usuarios</span>
                                    </a>
                                    <a href="#" class="quick-action-btn blue" onclick="showModule('roles'); return false;">
                                        <i class="fas fa-shield-alt"></i>
                                        <span>Roles</span>
                                    </a>
                                    <a href="#" class="quick-action-btn green" onclick="showModule('configuracion'); return false;">
                                        <i class="fas fa-cog"></i>
                                        <span>Config</span>
                                    </a>
                                    <a href="#" class="quick-action-btn purple" onclick="showModule('reportes'); return false;">
                                        <i class="fas fa-chart-bar"></i>
                                        <span>Reportes</span>
                                    </a>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            
            <div id="module-usuarios" style="display: none;">
                <div class="card-custom">
                    <div class="card-custom-body text-center py-5">
                        <i class="fas fa-users-cog" style="font-size: 64px; color: #dc2626; margin-bottom: 20px;"></i>
                        <h3>Gestión de Usuarios</h3>
                        <p class="text-muted">Administra usuarios del sistema, permisos y accesos.</p>
                        <button class="btn-primary-custom mt-3">Comenzar</button>
                    </div>
                </div>
            </div>
            
            <div id="module-roles" style="display: none;">
                <div class="card-custom">
                    <div class="card-custom-body text-center py-5">
                        <i class="fas fa-shield-alt" style="font-size: 64px; color: #3b82f6; margin-bottom: 20px;"></i>
                        <h3>Gestión de Roles</h3>
                        <p class="text-muted">Define y administra roles y permisos del sistema.</p>
                        <button class="btn-primary-custom mt-3">Comenzar</button>
                    </div>
                </div>
            </div>
            
            <div id="module-auditoria" style="display: none;">
                <div class="card-custom">
                    <div class="card-custom-body text-center py-5">
                        <i class="fas fa-history" style="font-size: 64px; color: #a78bfa; margin-bottom: 20px;"></i>
                        <h3>Auditoría del Sistema</h3>
                        <p class="text-muted">Revisa el historial de acciones y cambios en el sistema.</p>
                        <button class="btn-primary-custom mt-3">Ver Historial</button>
                    </div>
                </div>
            </div>
            
            <div id="module-configuracion" style="display: none;">
                <div class="card-custom">
                    <div class="card-custom-body text-center py-5">
                        <i class="fas fa-cog" style="font-size: 64px; color: #10b981; margin-bottom: 20px;"></i>
                        <h3>Configuración General</h3>
                        <p class="text-muted">Administra configuraciones globales del sistema.</p>
                        <button class="btn-primary-custom mt-3">Configurar</button>
                    </div>
                </div>
            </div>
            
            <div id="module-reportes" style="display: none;">
                <div class="card-custom">
                    <div class="card-custom-body text-center py-5">
                        <i class="fas fa-chart-bar" style="font-size: 64px; color: #ff8c42; margin-bottom: 20px;"></i>
                        <h3>Reportes y Estadísticas</h3>
                        <p class="text-muted">Genera reportes completos del sistema.</p>
                        <button class="btn-primary-custom mt-3">Ver Reportes</button>
                    </div>
                </div>
            </div>
        </div>
    </div>
    
    <script src="https://cdnjs.cloudflare.com/ajax/libs/bootstrap/5.3.0/js/bootstrap.bundle.min.js"></script>
    <script>
        function toggleSidebar() {
            document.getElementById('sidebar').classList.toggle('collapsed');
        }
        
        function showModule(moduleName) {
            const modules = ['inicio', 'usuarios', 'roles', 'auditoria', 'configuracion', 'reportes'];
            modules.forEach(mod => {
                document.getElementById('module-' + mod).style.display = 'none';
            });
            
            document.getElementById('module-' + moduleName).style.display = 'block';
            
            const titles = {
                'inicio': 'Panel de Administración',
                'usuarios': 'Gestión de Usuarios',
                'roles': 'Gestión de Roles',
                'auditoria': 'Auditoría del Sistema',
                'configuracion': 'Configuración General',
                'reportes': 'Reportes y Estadísticas'
            };
            document.getElementById('pageTitle').textContent = titles[moduleName];
            
            document.querySelectorAll('.menu-item').forEach(item => {
                item.classList.remove('active');
            });
            event.target.closest('.menu-item').classList.add('active');
        }
        
        function setCurrentDate() {
            const options = { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric' };
            const date = new Date().toLocaleDateString('es-ES', options);
            document.getElementById('currentDate').textContent = date.charAt(0).toUpperCase() + date.slice(1);
        }
        
        setCurrentDate();
    </script>
</body>
</html>
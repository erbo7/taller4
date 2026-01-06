<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String usuario = (String) session.getAttribute("usuario");
    String nombre = (String) session.getAttribute("nombre");
    Integer idRol = (Integer) session.getAttribute("rol");

    if (usuario == null || nombre == null || idRol == null) {
        response.sendRedirect("login.jsp");
        return;
    }

    // SOLO GERENTE (rol = 4)
    if (idRol != 4) {
        response.sendRedirect("accesoDenegado.jsp");
        return;
    }
%>

<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Dashboard Gerente - DiazPet</title>
    <link href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap/5.3.0/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
    <style>
        :root {
            --primary-color: #f59e0b;
            --secondary-color: #d97706;
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
        
        .stat-card.orange { border-left-color: #f59e0b; }
        .stat-card.green { border-left-color: #10b981; }
        .stat-card.red { border-left-color: #ef4444; }
        .stat-card.blue { border-left-color: #3b82f6; }
        
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
        
        .stat-card.orange .stat-card-icon { background: #fff4e6; color: #f59e0b; }
        .stat-card.green .stat-card-icon { background: #d1fae5; color: #10b981; }
        .stat-card.red .stat-card-icon { background: #fee2e2; color: #ef4444; }
        .stat-card.blue .stat-card-icon { background: #dbeafe; color: #3b82f6; }
        
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
        
        .stat-card .trend {
            font-size: 12px;
            margin-top: 8px;
        }
        
        .trend.up { color: #10b981; }
        .trend.down { color: #ef4444; }
        
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
        
        .order-item {
            display: flex;
            align-items: center;
            justify-content: space-between;
            padding: 15px;
            background: #f8f9fa;
            border-radius: 8px;
            margin-bottom: 12px;
        }
        
        .order-info h6 {
            margin: 0 0 5px;
            font-weight: bold;
            color: #2c3e50;
            font-size: 14px;
        }
        
        .order-info p {
            margin: 0;
            font-size: 12px;
            color: #999;
        }
        
        .order-status {
            padding: 6px 15px;
            border-radius: 20px;
            font-size: 12px;
            font-weight: 600;
        }
        
        .status-pending { background: #fff4e6; color: #f59e0b; }
        .status-progress { background: #dbeafe; color: #3b82f6; }
        .status-completed { background: #d1fae5; color: #10b981; }
        
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
        
        .quick-action-btn.orange i { color: #f59e0b; }
        .quick-action-btn.green i { color: #10b981; }
        .quick-action-btn.blue i { color: #3b82f6; }
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
            box-shadow: 0 5px 15px rgba(245, 158, 11, 0.4);
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
                <h3>📊 DiazPet</h3>
                <small style="color: rgba(255,255,255,0.8);">Gerente</small>
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
            <a href="#" class="menu-item" onclick="showModule('finanzas')">
                <i class="fas fa-dollar-sign"></i>
                <span class="menu-text">Finanzas</span>
            </a>
            <a href="#" class="menu-item" onclick="showModule('ordenes')">
                <i class="fas fa-clipboard-list"></i>
                <span class="menu-text">Órdenes Servicio</span>
                <span class="badge-notify">8</span>
            </a>
            <a href="#" class="menu-item" onclick="showModule('inventario')">
                <i class="fas fa-boxes"></i>
                <span class="menu-text">Inventario</span>
            </a>
            <a href="#" class="menu-item" onclick="showModule('promociones')">
                <i class="fas fa-tags"></i>
                <span class="menu-text">Promociones</span>
            </a>
            <a href="#" class="menu-item" onclick="showModule('reportes')">
                <i class="fas fa-chart-bar"></i>
                <span class="menu-text">Reportes</span>
            </a>
            <a href="#" class="menu-item" onclick="showModule('presupuestos')">
                <i class="fas fa-file-invoice-dollar"></i>
                <span class="menu-text">Presupuestos</span>
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
                    <h2 id="pageTitle">Panel de Gerencia</h2>
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
                    <div class="stat-card orange">
                        <div class="stat-card-icon">
                            <i class="fas fa-dollar-sign"></i>
                        </div>
                        <h6>INGRESOS DEL MES</h6>
                        <h2>$45,230</h2>
                        <div class="trend up">
                            <i class="fas fa-arrow-up"></i> +12.5% vs mes anterior
                        </div>
                    </div>
                    
                    <div class="stat-card green">
                        <div class="stat-card-icon">
                            <i class="fas fa-check-circle"></i>
                        </div>
                        <h6>SERVICIOS COMPLETADOS</h6>
                        <h2>124</h2>
                        <div class="trend up">
                            <i class="fas fa-arrow-up"></i> +8.3% vs mes anterior
                        </div>
                    </div>
                    
                    <div class="stat-card red">
                        <div class="stat-card-icon">
                            <i class="fas fa-exclamation-triangle"></i>
                        </div>
                        <h6>PENDIENTES DE COBRO</h6>
                        <h2>$8,450</h2>
                        <div class="trend down">
                            <i class="fas fa-arrow-down"></i> -5.2% vs mes anterior
                        </div>
                    </div>
                    
                    <div class="stat-card blue">
                        <div class="stat-card-icon">
                            <i class="fas fa-boxes"></i>
                        </div>
                        <h6>PRODUCTOS EN STOCK</h6>
                        <h2>342</h2>
                        <div class="trend up">
                            <i class="fas fa-arrow-up"></i> Stock saludable
                        </div>
                    </div>
                </div>
                
                <div class="row">
                    <div class="col-md-8">
                        <div class="card-custom">
                            <div class="card-custom-header">
                                <h5><i class="fas fa-clipboard-list"></i> Órdenes de Servicio Pendientes</h5>
                                <button class="btn-primary-custom">
                                    <i class="fas fa-plus"></i> Nueva Orden
                                </button>
                            </div>
                            <div class="card-custom-body">
                                <div class="order-item">
                                    <div class="order-info">
                                        <h6>Orden #1234 - Baño y Peluquería</h6>
                                        <p>Cliente: María García • Mascota: Max • Asignado a: Juan Pérez</p>
                                    </div>
                                    <span class="order-status status-pending">Pendiente</span>
                                    <button class="btn-outline-custom ms-3">Ver Detalles</button>
                                </div>
                                
                                <div class="order-item">
                                    <div class="order-info">
                                        <h6>Orden #1235 - Servicio Delivery</h6>
                                        <p>Cliente: Ana Torres • Mascota: Luna • En ruta</p>
                                    </div>
                                    <span class="order-status status-progress">En Proceso</span>
                                    <button class="btn-outline-custom ms-3">Ver Detalles</button>
                                </div>
                                
                                <div class="order-item">
                                    <div class="order-info">
                                        <h6>Orden #1236 - Hospitalización</h6>
                                        <p>Cliente: Carlos Ruiz • Mascota: Rocky • 2 días</p>
                                    </div>
                                    <span class="order-status status-progress">En Proceso</span>
                                    <button class="btn-outline-custom ms-3">Ver Detalles</button>
                                </div>
                                
                                <div class="order-item">
                                    <div class="order-info">
                                        <h6>Orden #1237 - Vacunación Múltiple</h6>
                                        <p>Cliente: Pedro Sánchez • Mascota: Bella • Completado hoy</p>
                                    </div>
                                    <span class="order-status status-completed">Completado</span>
                                    <button class="btn-outline-custom ms-3">Ver Detalles</button>
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
                                    <a href="#" class="quick-action-btn orange" onclick="showModule('finanzas'); return false;">
                                        <i class="fas fa-dollar-sign"></i>
                                        <span>Finanzas</span>
                                    </a>
                                    <a href="#" class="quick-action-btn green" onclick="showModule('ordenes'); return false;">
                                        <i class="fas fa-clipboard-list"></i>
                                        <span>Órdenes</span>
                                    </a>
                                    <a href="#" class="quick-action-btn blue" onclick="showModule('inventario'); return false;">
                                        <i class="fas fa-boxes"></i>
                                        <span>Inventario</span>
                                    </a>
                                    <a href="#" class="quick-action-btn purple" onclick="showModule('reportes'); return false;">
                                        <i class="fas fa-chart-bar"></i>
                                        <span>Reportes</span>
                                    </a>
                                </div>
                            </div>
                        </div>
                        
                        <div class="card-custom">
                            <div class="card-custom-header">
                                <h5><i class="fas fa-exclamation-circle"></i> Alertas</h5>
                            </div>
                            <div class="card-custom-body">
                                <div class="alert alert-warning mb-2">
                                    <i class="fas fa-box"></i> 5 productos con stock bajo
                                </div>
                                <div class="alert alert-info mb-0">
                                    <i class="fas fa-coins"></i> 8 facturas por vencer esta semana
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            
            <div id="module-finanzas" style="display: none;">
                <div class="card-custom">
                    <div class="card-custom-body text-center py-5">
                        <i class="fas fa-dollar-sign" style="font-size: 64px; color: #f59e0b; margin-bottom: 20px;"></i>
                        <h3>Módulo Financiero</h3>
                        <p class="text-muted">Gestiona ingresos, egresos, facturas y pagos.</p>
                        <button class="btn-primary-custom mt-3">Ver Finanzas</button>
                    </div>
                </div>
            </div>
            
            <div id="module-ordenes" style="display: none;">
                <div class="card-custom">
                    <div class="card-custom-body text-center py-5">
                        <i class="fas fa-clipboard-list" style="font-size: 64px; color: #3b82f6; margin-bottom: 20px;"></i>
                        <h3>Órdenes de Servicio</h3>
                        <p class="text-muted">Administra y supervisa órdenes de servicio.</p>
                        <button class="btn-primary-custom mt-3">Ver Órdenes</button>
                    </div>
                </div>
            </div>
            
            <div id="module-inventario" style="display: none;">
                <div class="card-custom">
                    <div class="card-custom-body text-center py-5">
                        <i class="fas fa-boxes" style="font-size: 64px; color: #10b981; margin-bottom: 20px;"></i>
                        <h3>Control de Inventario</h3>
                        <p class="text-muted">Gestiona productos, insumos y movimientos de stock.</p>
                        <button class="btn-primary-custom mt-3">Ver Inventario</button>
                    </div>
                </div>
            </div>
            
            <div id="module-promociones" style="display: none;">
                <div class="card-custom">
                    <div class="card-custom-body text-center py-5">
                        <i class="fas fa-tags" style="font-size: 64px; color: #ec4899; margin-bottom: 20px;"></i>
                        <h3>Promociones y Descuentos</h3>
                        <p class="text-muted">Crea y administra promociones para clientes.</p>
                        <button class="btn-primary-custom mt-3">Gestionar Promociones</button>
                    </div>
                </div>
            </div>
            
            <div id="module-reportes" style="display: none;">
                <div class="card-custom">
                    <div class="card-custom-body text-center py-5">
                        <i class="fas fa-chart-bar" style="font-size: 64px; color: #a78bfa; margin-bottom: 20px;"></i>
                        <h3>Reportes y Estadísticas</h3>
                        <p class="text-muted">Genera reportes detallados de ventas, servicios y finanzas.</p>
                        <button class="btn-primary-custom mt-3">Ver Reportes</button>
                    </div>
                </div>
            </div>
            
            <div id="module-presupuestos" style="display: none;">
                <div class="card-custom">
                    <div class="card-custom-body text-center py-5">
                        <i class="fas fa-file-invoice-dollar" style="font-size: 64px; color: #ff8c42; margin-bottom: 20px;"></i>
                        <h3>Presupuestos</h3>
                        <p class="text-muted">Gestiona cotizaciones y presupuestos para clientes.</p>
                        <button class="btn-primary-custom mt-3">Ver Presupuestos</button>
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
            const modules = ['inicio', 'finanzas', 'ordenes', 'inventario', 'promociones', 'reportes', 'presupuestos'];
            modules.forEach(mod => {
                document.getElementById('module-' + mod).style.display = 'none';
            });
            
            document.getElementById('module-' + moduleName).style.display = 'block';
            
            const titles = {
                'inicio': 'Panel de Gerencia',
                'finanzas': 'Módulo Financiero',
                'ordenes': 'Órdenes de Servicio',
                'inventario': 'Control de Inventario',
                'promociones': 'Promociones y Descuentos',
                'reportes': 'Reportes y Estadísticas',
                'presupuestos': 'Presupuestos'
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
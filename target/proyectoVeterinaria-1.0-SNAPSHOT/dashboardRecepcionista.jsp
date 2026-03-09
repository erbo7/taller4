<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="modelo.Cita" %>
<%@ page import="modelo.RecepcionistaStats" %>
<%@ page import="java.util.List" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.text.ParseException" %>
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

    // Obtener datos del request
    RecepcionistaStats stats = (RecepcionistaStats) request.getAttribute("stats");
    Integer totalCitasHoy = (Integer) request.getAttribute("totalCitasHoy");
    Integer totalRecordatorios = (Integer) request.getAttribute("totalRecordatorios");
    Integer clientesAtendidos = (Integer) request.getAttribute("clientesAtendidos");
    List<Cita> citasHoy = (List<Cita>) request.getAttribute("citasHoy");
    List<Cita> proximasCitas = (List<Cita>) request.getAttribute("proximasCitas");
    List<Cita> citasPorConfirmar = (List<Cita>) request.getAttribute("citasPorConfirmar");

    // Si no hay datos, inicializar con valores por defecto
    if (stats == null) {
        stats = new RecepcionistaStats();
    }
    if (totalCitasHoy == null) {
        totalCitasHoy = 0;
    }
    if (totalRecordatorios == null) {
        totalRecordatorios = 0;
    }
    if (clientesAtendidos == null) {
        clientesAtendidos = 0;
    }
    if (citasHoy == null) {
        citasHoy = new java.util.ArrayList<>();
    }
    if (proximasCitas == null) {
        proximasCitas = new java.util.ArrayList<>();
    }
    if (citasPorConfirmar == null) {
        citasPorConfirmar = new java.util.ArrayList<>();
    }

    // Formateo de fechas
    SimpleDateFormat sdfEntrada = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat sdfSalida = new SimpleDateFormat("EEEE dd 'de' MMMM", new java.util.Locale("es", "ES"));
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

            .stat-card small {
                color: #999;
                font-size: 12px;
            }

            /* Card */
            .card-custom {
                background: white;
                border-radius: 10px;
                box-shadow: 0 2px 10px rgba(0,0,0,0.05);
                margin-bottom: 30px;
            }

            .card-custom-header {
                padding: 15px 25px;
                /* Usamos el mismo degradado que tu sidebar */
                background: linear-gradient(135deg, var(--primary-color) 0%, var(--secondary-color) 100%);
                border-bottom: none;
                display: flex;
                align-items: center;
                justify-content: space-between;
                /* Redondeamos las esquinas superiores para que encajen con la tarjeta */
                border-radius: 10px 10px 0 0;
                color: white;
            }

            /* Cambiamos el color del texto e iconos a blanco para que resalten sobre el azul/morado */
            .card-custom-header h5 {
                margin: 0;
                font-size: 18px;
                font-weight: bold;
                color: white !important;
            }

            .card-custom-header h5 i {
                color: rgba(255,255,255,0.8);
                margin-right: 10px;
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

            .status-CONFIRMADA {
                background: #d1fae5;
                color: #10b981;
            }
            .status-RESERVADA {
                background: #dbeafe;
                color: #3b82f6;
            }
            .status-EN_PROCESO {
                background: #fef3c7;
                color: #f59e0b;
            }
            .status-COMPLETADA {
                background: #d1fae5;
                color: #059669;
            }
            .status-ANULADA {
                background: #fee2e2;
                color: #dc2626;
            }

            .btn-primary-custom {
                background: linear-gradient(135deg, var(--primary-color) 0%, var(--secondary-color) 100%);
                border: none;
                color: white;
                padding: 10px 25px;
                border-radius: 8px;
                font-weight: 600;
                transition: all 0.3s;
                text-decoration: none;
                display: inline-block;
            }

            .btn-primary-custom:hover {
                transform: translateY(-2px);
                box-shadow: 0 5px 15px rgba(102, 126, 234, 0.4);
                color: white;
            }

            .btn-outline-custom {
                border: 2px solid #e0e0e0;
                background: white;
                color: #2c3e50;
                padding: 8px 20px;
                border-radius: 8px;
                font-weight: 600;
                transition: all 0.3s;
                text-decoration: none;
                display: inline-block;
            }

            .btn-outline-custom:hover {
                border-color: var(--primary-color);
                color: var(--primary-color);
            }

            .empty-state {
                text-align: center;
                padding: 40px 20px;
                color: #999;
            }

            .empty-state i {
                font-size: 48px;
                margin-bottom: 15px;
                opacity: 0.5;
            }

            .empty-state p {
                margin: 0;
                font-size: 16px;
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
                <a href="RecepcionistaServlet" class="menu-item active">
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
                <a href="LogoutServlet" class="menu-item" onclick="return confirm('¿Seguro que deseas cerrar sesión?')">
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
                        <h2>Inicio</h2>
                        <p class="date mb-0">
                            <i class="far fa-calendar"></i>
                            <span id="currentDate"></span>
                        </p>
                    </div>
                    <button class="btn-primary-custom" onclick="location.reload()">
                        <i class="fas fa-sync-alt"></i> Actualizar
                    </button>
                </div>
            </div>

            <!-- Content Area -->
            <div class="content-area">
                <!-- Stats Cards -->
                <div class="stats-grid">
                    <div class="stat-card blue">
                        <div class="stat-card-icon">
                            <i class="fas fa-calendar-check"></i>
                        </div>
                        <h6>CITAS DE HOY</h6>
                        <h2><%= totalCitasHoy%></h2>
                        <small><%= citasHoy.size()%> activas</small>
                    </div>

                    <div class="stat-card orange">
                        <div class="stat-card-icon">
                            <i class="fas fa-bell"></i>
                        </div>
                        <h6>RECORDATORIOS</h6>
                        <h2><%= totalRecordatorios%></h2>
                        <small>pendientes de envío</small>
                    </div>

                    <div class="stat-card green">
                        <div class="stat-card-icon">
                            <i class="fas fa-user-check"></i>
                        </div>
                        <h6>CLIENTES ATENDIDOS HOY</h6>  <!-- ⭐ CAMBIO -->
                        <h2><%= clientesAtendidos%></h2>
                        <small><%= stats.getClientesAtendidosMes()%> este mes</small>
                    </div>

                    <div class="stat-card purple">
                        <div class="stat-card-icon">
                            <i class="fas fa-clock"></i>
                        </div>
                        <h6>CITAS DE LA SEMANA</h6>
                        <h2><%= stats.getCitasSemana()%></h2>
                        <small><%= stats.getCitasPendientes()%> por confirmar</small>
                    </div>
                </div>

                <!-- Citas del Día -->
                <div class="card-custom">
                    <div class="card-custom-header">
                        <h5><i class="fas fa-calendar-day"></i> Citas de Hoy</h5>
                        <a href="gestionarCitas.jsp" class="btn-outline-custom">
                            <i class="fas fa-plus"></i> Nueva Cita
                        </a>
                    </div>
                    <div class="card-custom-body">
                        <% if (citasHoy.isEmpty()) { %>
                        <div class="empty-state">
                            <i class="fas fa-calendar-times"></i>
                            <p>No hay citas programadas para hoy</p>
                        </div>
                        <% } else { %>
                        <% for (Cita cita : citasHoy) {
                                String horaFormateada = cita.getHoraCita();
                                if (horaFormateada != null && horaFormateada.length() >= 5) {
                                    horaFormateada = horaFormateada.substring(0, 5);
                                }
                        %>
                        <div class="appointment-item">
                            <div class="appointment-time"><%= horaFormateada%></div>
                            <div class="appointment-info">
                                <h6><%= cita.getNombreCliente()%></h6>
                                <p>
                                    <i class="fas fa-paw"></i> <%= cita.getNombreMascota()%> • 
                                    <i class="fas fa-user-md"></i> <%= cita.getNombreVeterinario()%>
                                    <% if (cita.getMotivo() != null && !cita.getMotivo().isEmpty()) {%>
                                    • <%= cita.getMotivo()%>
                                    <% }%>
                                </p>
                            </div>
                            <span class="appointment-status status-<%= cita.getEstado()%>">
                                <%= "FINALIZADA".equals(cita.getEstado()) ? "ATENDIDA" : cita.getEstado()%>
                            </span>
                            <a href="gestionarCitas.jsp?id=<%= cita.getIdCita()%>" class="btn-primary-custom ms-3">
                                Ver Detalles
                            </a>
                        </div>
                        <% } %>
                        <% } %>
                    </div>
                </div>

                <div class="row">
                    <!-- Próximas Citas -->
                    <div class="col-md-6">
                        <div class="card-custom">
                            <div class="card-custom-header">
                                <h5><i class="fas fa-calendar-week"></i> Próximas Citas</h5>
                            </div>
                            <div class="card-custom-body">
                                <% if (proximasCitas.isEmpty()) { %>
                                <div class="empty-state">
                                    <i class="fas fa-calendar-check"></i>
                                    <p>No hay citas próximas esta semana</p>
                                </div>
                                <% } else { %>
                                <% for (Cita cita : proximasCitas) {
                                        String fechaFormateada = "";
                                        try {
                                            java.util.Date fecha = sdfEntrada.parse(cita.getFechaCita());
                                            fechaFormateada = sdfSalida.format(fecha);
                                            fechaFormateada = fechaFormateada.substring(0, 1).toUpperCase() + fechaFormateada.substring(1);
                                        } catch (ParseException e) {
                                            fechaFormateada = cita.getFechaCita();
                                        }

                                        String horaFormateada = cita.getHoraCita();
                                        if (horaFormateada != null && horaFormateada.length() >= 5) {
                                            horaFormateada = horaFormateada.substring(0, 5);
                                        }
                                %>
                                <div class="appointment-item">
                                    <div style="min-width: 100px;">
                                        <small style="color: #999;"><%= fechaFormateada%></small>
                                        <div style="font-weight: bold; color: #2c3e50;"><%= horaFormateada%></div>
                                    </div>
                                    <div class="appointment-info">
                                        <h6><%= cita.getNombreCliente()%></h6>
                                        <p>
                                            <i class="fas fa-paw"></i> <%= cita.getNombreMascota()%> • 
                                            <%= cita.getNombreVeterinario()%>
                                        </p>
                                    </div>
                                    <span class="appointment-status status-<%= cita.getEstado()%>">
                                        <%= "FINALIZADA".equals(cita.getEstado()) ? "ATENDIDA" : cita.getEstado()%>
                                    </span>
                                </div>
                                <% } %>
                                <% } %>
                            </div>
                        </div>
                    </div>

                    <!-- Citas Por Confirmar -->
                    <div class="col-md-6">
                        <div class="card-custom">
                            <div class="card-custom-header">
                                <h5><i class="fas fa-exclamation-circle"></i> Por Confirmar</h5>
                            </div>
                            <div class="card-custom-body">
                                <% if (citasPorConfirmar.isEmpty()) { %>
                                <div class="empty-state">
                                    <i class="fas fa-check-circle"></i>
                                    <p>Todas las citas están confirmadas</p>
                                </div>
                                <% } else { %>
                                <% for (Cita cita : citasPorConfirmar) {
                                        String fechaFormateada = "";
                                        try {
                                            java.util.Date fecha = sdfEntrada.parse(cita.getFechaCita());
                                            fechaFormateada = sdfSalida.format(fecha);
                                            fechaFormateada = fechaFormateada.substring(0, 1).toUpperCase() + fechaFormateada.substring(1);
                                        } catch (ParseException e) {
                                            fechaFormateada = cita.getFechaCita();
                                        }

                                        String horaFormateada = cita.getHoraCita();
                                        if (horaFormateada != null && horaFormateada.length() >= 5) {
                                            horaFormateada = horaFormateada.substring(0, 5);
                                        }
                                %>
                                <div class="appointment-item">
                                    <div style="min-width: 100px;">
                                        <small style="color: #999;"><%= fechaFormateada%></small>
                                        <div style="font-weight: bold; color: #2c3e50;"><%= horaFormateada%></div>
                                    </div>
                                    <div class="appointment-info">
                                        <h6><%= cita.getNombreCliente()%></h6>
                                        <p>
                                            <i class="fas fa-paw"></i> <%= cita.getNombreMascota()%>
                                            <% if (cita.getNombreVeterinario() != null && !cita.getNombreVeterinario().isEmpty()) {%>
                                            • <i class="fas fa-user-md"></i> <%= cita.getNombreVeterinario()%>
                                            <% }%>
                                        </p>
                                    </div>
                                    <span class="appointment-status status-<%= cita.getEstado()%>">
                                        <%= cita.getEstado()%>
                                    </span>
                                    <a href="gestionarCitas.jsp?id=<%= cita.getIdCita()%>" class="btn-outline-custom ms-3">
                                        Ver Detalles
                                    </a>
                                </div>
                                <% } %>
                                <% }%>
                            </div>
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

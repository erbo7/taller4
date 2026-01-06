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
        <title>Gestionar Citas - DiazPet</title>
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
            /* ✅ ENCABEZADO DE CARD CON FONDO DEGRADADO EN BLANCO (como en agenda) */
            .card-custom-header {
                background: linear-gradient(135deg, var(--primary-color) 0%, var(--secondary-color) 100%);
                color: #fff;
                padding: 10px 25px;
                display: flex;
                justify-content: space-between;
                align-items: center;
                border-radius: 6px 6px 0 0;
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
            .badge-status {
                padding: 6px 14px;
                border-radius: 20px;
                font-size: 12px;
                font-weight: 600;
            }
            .badge-pendiente {
                background-color: #fff3cd;
                color: #856404;
            }
            .badge-confirmada {
                background-color: #d1fae5;
                color: #10b981;
            }
            .badge-completada {
                background-color: #cfe2ff;
                color: #084298;
            }
            .badge-cancelada {
                background-color: #fee2e2;
                color: #dc3545;
            }
            .btn-tabla {
                padding: 6px 12px;
                font-size: 13px;
                margin: 0 3px;
                border-radius: 6px;
            }
            .modal-header {
                background: linear-gradient(135deg, var(--primary-color) 0%, var(--secondary-color) 100%);
                color: white;
                border-radius: 10px 10px 0 0;
            }
            .modal-content {
                border-radius: 10px;
                border: none;
                box-shadow: 0 10px 40px rgba(0,0,0,0.2);
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
            .empty-state h4 {
                color: #64748b;
                margin-bottom: 10px;
            }
            .empty-state p {
                color: #94a3b8;
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
                    <small style="color: rgba(255,255,255,0.8);">Gestión de Citas</small>
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
                <a href="gestionarCitas.jsp" class="menu-item active">
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
            <div class="top-bar">
                <div class="d-flex justify-content-between align-items-center">
                    <div>
                        <h2><i class="fas fa-calendar-check"></i> Gestión de Citas</h2>
                        <p class="text-muted mb-0">
                            <i class="far fa-calendar"></i>
                            <span id="currentDate"></span>
                        </p>
                    </div>
                    <button class="btn-primary-custom" data-bs-toggle="modal" data-bs-target="#modalCita">
                        <i class="fas fa-plus"></i> Reservar Nueva Cita
                    </button>
                </div>
            </div>
            <div class="content-area">
                <!-- Filtros -->
                <div class="card-custom">
                    <div class="card-custom-header">
                        <h5><i class="fas fa-filter"></i> Filtros de Búsqueda</h5>
                    </div>
                    <div class="card-custom-body">
                        <div class="row g-3 align-items-end">
                            <div class="col-md-4">
                                <label class="form-label">
                                    <i class="fas fa-user-md"></i> Veterinario
                                </label>
                                <select class="form-select" id="filtroVeterinario">
                                    <option value="">Todos los veterinarios</option>
                                </select>
                            </div>
                            <div class="col-md-3">
                                <label class="form-label">
                                    <i class="fas fa-calendar"></i> Fecha específica
                                </label>
                                <input type="date" class="form-control" id="filtroFecha">
                            </div>
                            <div class="col-md-3">
                                <label class="form-label">
                                    <i class="fas fa-info-circle"></i> Estado
                                </label>
                                <select class="form-select" id="filtroEstado">
                                    <option value="">Todos los estados</option>
                                    <option value="RESERVADA">Reservada</option>
                                    <option value="CONFIRMADA">Confirmada</option>
                                    <option value="COMPLETADA">Completada</option>
                                    <option value="CANCELADA">Cancelada</option>
                                </select>
                            </div>
                            <div class="col-md-2">
                                <button class="btn-primary-custom w-100" onclick="buscarCitasConFiltros()">
                                    <i class="fas fa-search"></i> Buscar
                                </button>
                            </div>
                            <div class="col-md-2">
                                <button class="btn-outline-custom w-100" onclick="limpiarFiltrosCitas()">
                                    <i class="fas fa-redo"></i> Limpiar Filtros
                                </button>
                            </div>
                        </div>
                    </div>
                </div>
                <!-- Tabla de Citas -->
                <div class="card-custom">
                    <div class="card-custom-header">
                        <h5><i class="fas fa-list"></i> Citas Registradas</h5>
                    </div>
                    <div class="card-custom-body">
                        <div class="table-responsive">
                            <table class="table table-hover" id="tablaCitas">
                                <thead>
                                    <tr>
                                        <!--  <th><i class="fas fa-hashtag"></i> ID</th> -->
                                        <th><i class="fas fa-user-md"></i> Veterinario</th>
                                        <th><i class="fas fa-user"></i> Cliente</th>
                                        <th><i class="fas fa-paw"></i> Mascota</th>
                                        <th><i class="fas fa-calendar"></i> Fecha</th>
                                        <th><i class="fas fa-clock"></i> Hora</th>
                                        <th><i class="fas fa-clipboard"></i> Motivo</th>
                                        <th><i class="fas fa-info-circle"></i> Estado</th>
                                        <th><i class="fas fa-cog"></i> Acciones</th>
                                    </tr>
                                </thead>
                                <tbody id="listaCitas">
                                    <tr>
                                        <td colspan="9" class="text-center py-5">
                                            <div class="spinner-border text-primary" role="status">
                                                <span class="visually-hidden">Cargando...</span>
                                            </div>
                                            <p class="mt-3 text-muted">Cargando citas...</p>
                                        </td>
                                    </tr>
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- Modal Reservar Cita -->
        <div class="modal fade" id="modalCita" tabindex="-1">
            <div class="modal-dialog modal-lg">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title">
                            <i class="fas fa-calendar-plus"></i> Reservar Nueva Cita
                        </h5>
                        <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
                    </div>
                    <div class="modal-body">
                        <form id="formCita" onsubmit="return false;">
                            <input type="hidden" id="accionForm" name="accion" value="crear">
                            <input type="hidden" name="creadoPor" id="creadoPor" value="<%= session.getAttribute("idUsuario") != null ? session.getAttribute("idUsuario") : "2"%>">

                            <div class="row">
                                <div class="col-md-6 mb-3">
                                    <label class="form-label">
                                        <i class="fas fa-user-md"></i> Veterinario *
                                    </label>
                                    <select class="form-select" id="idVeterinario" name="idVeterinario" required onchange="cargarAgendasDisponibles()">
                                        <option value="">Seleccione un veterinario</option>
                                    </select>
                                </div>
                                <div class="col-md-6 mb-3">
                                    <label class="form-label">
                                        <i class="fas fa-calendar-alt"></i> Fecha
                                    </label>
                                    <input type="date" class="form-control" id="fechaAgenda" onchange="cargarAgendasDisponibles()">
                                </div>
                            </div>

                            <div class="row">
                                <div class="col-md-6 mb-3">
                                    <label class="form-label">
                                        <i class="fas fa-clock"></i> Horario Disponible *
                                    </label>
                                    <select class="form-select" id="idSlot" name="idSlot" required disabled>
                                        <option value="">Primero seleccione veterinario</option>
                                    </select>
                                </div>

                                <!-- Cliente con botón + -->
                                <div class="col-md-6 mb-3">
                                    <label class="form-label">
                                        <i class="fas fa-user"></i> Cliente *
                                    </label>
                                    <div class="input-group">
                                        <select class="form-select" id="idCliente" name="idCliente" required onchange="cargarMascotas()">
                                            <option value="">Seleccione cliente</option>
                                        </select>
                                        <button class="btn btn-outline-secondary" type="button" onclick="abrirModalClienteNuevo()">
                                            <i class="fas fa-plus"></i>
                                        </button>
                                    </div>
                                </div>

                                <!-- Mascota con botón + -->
                                <div class="col-md-6 mb-3">
                                    <label class="form-label">
                                        <i class="fas fa-paw"></i> Mascota *
                                    </label>
                                    <div class="input-group">
                                        <select class="form-select" id="idMascota" name="idMascota" required>
                                            <option value="">Seleccione mascota</option>
                                        </select>
                                        <button class="btn btn-outline-secondary" type="button" onclick="abrirModalMascotaNueva()">
                                            <i class="fas fa-plus"></i>
                                        </button>
                                    </div>
                                </div>

                            </div>



                            <div class="mb-3">
                                <label class="form-label">
                                    <i class="fas fa-clipboard"></i> Motivo de la Cita *
                                </label>
                                <textarea class="form-control" id="motivo" name="motivo" rows="2" required></textarea>
                            </div>
                            <div class="mb-3">
                                <label class="form-label">
                                    <i class="fas fa-sticky-note"></i> Observaciones
                                </label>
                                <textarea class="form-control" id="observaciones" name="observaciones" rows="2"></textarea>
                            </div>
                        </form>
                    </div>
                    <div class="modal-footer">
                        <button class="btn btn-secondary" data-bs-dismiss="modal">
                            <i class="fas fa-times"></i> Cancelar
                        </button>
                        <button type="button" class="btn-primary-custom" onclick="reservarCita()">
                            <i class="fas fa-save"></i> Reservar Cita
                        </button>
                    </div>
                </div>
            </div>
        </div>

        <!-- Modal Anular Cita -->
        <div class="modal fade" id="modalAnularCita" tabindex="-1">
            <div class="modal-dialog">
                <div class="modal-content">
                    <div class="modal-header bg-danger text-white">
                        <h5 class="modal-title">
                            <i class="fas fa-ban"></i> Anular Cita
                        </h5>
                        <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
                    </div>
                    <div class="modal-body">
                        <input type="hidden" id="idCitaAnular">
                        <div class="alert alert-warning">
                            <i class="fas fa-exclamation-triangle"></i>
                            <strong>Advertencia:</strong> Esta acción no se puede deshacer.
                        </div>
                        <div class="mb-3">
                            <label class="form-label">
                                <i class="fas fa-comment"></i> Motivo de anulación *
                            </label>
                            <textarea class="form-control" id="motivoAnulacion" rows="4" required></textarea>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancelar</button>
                        <button type="button" class="btn btn-danger" onclick="anularCita()">Confirmar Anulación</button>
                    </div>
                </div>
            </div>
        </div>

        <!-- Modal Reprogramar Cita -->
        <div class="modal fade" id="modalReprogramar" tabindex="-1">
            <div class="modal-dialog modal-lg">
                <div class="modal-content">
                    <div class="modal-header bg-warning text-white">
                        <h5 class="modal-title">
                            <i class="fas fa-calendar-alt"></i> Reprogramar Cita
                        </h5>
                        <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
                    </div>
                    <div class="modal-body">
                        <input type="hidden" id="idCitaReprogramar">
                        <div class="row">
                            <div class="col-md-6 mb-3">
                                <label class="form-label">Nuevo Veterinario *</label>
                                <select class="form-select" id="nuevoVeterinario" onchange="cargarNuevasAgendas()">
                                    <option value="">Seleccione veterinario</option>
                                </select>
                            </div>
                            <div class="col-md-6 mb-3">
                                <label class="form-label">Nueva Fecha</label>
                                <input type="date" class="form-control" id="nuevaFecha" onchange="cargarNuevasAgendas()">
                            </div>
                        </div>
                        <div class="mb-3">
                            <label class="form-label">Nueva Agenda Disponible *</label>
                            <select class="form-select" id="nuevoSlot" name="nuevoSlot" required>
                                <option value="">Seleccione horario</option>
                            </select>
                        </div>
                        <div class="mb-3">
                            <label class="form-label">Motivo de reprogramación *</label>
                            <textarea class="form-control" id="motivoReprogramacion" rows="3" required></textarea>
                        </div>
                        <div class="mb-3">
                            <label class="form-label">Observaciones</label>
                            <textarea class="form-control" id="observacionesReprogramacion" rows="2"></textarea>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancelar</button>
                        <button type="button" class="btn btn-warning" onclick="reprogramarCita()">Reprogramar</button>
                    </div>
                </div>
            </div>
        </div>

        <!-- Modal Cliente Nuevo -->
        <div class="modal fade" id="modalClienteNuevo" tabindex="-1">
            <div class="modal-dialog">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title"><i class="fas fa-user-plus"></i> Nuevo Cliente</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                    </div>
                    <div class="modal-body">
                        <div class="mb-3">
                            <label class="form-label">Nombre *</label>
                            <input type="text" class="form-control" id="nuevoClienteNombre" required>
                        </div>

                        <!-- CAMBIO: Tipo de Documento dinámico -->
                        <div class="row">
                            <div class="col-md-6 mb-3">
                                <label class="form-label">Tipo de Documento *</label>
                                <select class="form-select" id="nuevoClienteTipoDoc" required 
                                        onchange="habilitarDocumento()">
                                    <option value="">Cargando tipos...</option>
                                </select>
                            </div>
                            <div class="col-md-6 mb-3">
                                <label class="form-label">Número de Documento *</label>
                                <input type="text" class="form-control" id="nuevoClienteDocumento" 
                                       required disabled
                                       placeholder="Seleccione tipo primero">
                            </div>
                        </div>

                        <div class="mb-3">
                            <label class="form-label">Teléfono</label>
                            <input type="text" class="form-control" id="nuevoClienteTelefono">
                        </div>
                        <div class="mb-3">
                            <label class="form-label">Email</label>
                            <input type="email" class="form-control" id="nuevoClienteEmail">
                        </div>
                        <div class="mb-3">
                            <label class="form-label">Dirección</label>
                            <textarea class="form-control" id="nuevoClienteDireccion" rows="2"></textarea>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancelar</button>
                        <button type="button" class="btn-primary-custom" onclick="guardarClienteNuevo()">Guardar Cliente</button>
                    </div>
                </div>
            </div>
        </div>
        <!-- Modal Mascota Nueva -->
        <div class="modal fade" id="modalMascotaNueva" tabindex="-1">
            <div class="modal-dialog">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title"><i class="fas fa-paw"></i> Nueva Mascota</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                    </div>
                    <div class="modal-body">
                        <div class="mb-3">
                            <label class="form-label">Cliente *</label>
                            <select class="form-select" id="mascotaIdCliente" required>
                                <option value="">Seleccione cliente</option>
                            </select>
                        </div>
                        <div class="mb-3">
                            <label class="form-label">Nombre *</label>
                            <input type="text" class="form-control" id="nuevaMascotaNombre" required>
                        </div>
                        <div class="mb-3">
                            <label class="form-label">Especie *</label>
                            <select class="form-select" id="nuevaMascotaEspecie" required onchange="cargarRazasPorEspecie()">
                                <option value="">Seleccione especie</option>
                            </select>
                        </div>
                        <div class="mb-3">
                            <label class="form-label">Raza *</label>
                            <select class="form-select" id="nuevaMascotaRaza" required>
                                <option value="">Seleccione especie primero</option>
                            </select>
                        </div>
                        <div class="mb-3">
                            <label class="form-label">Edad (años)</label>
                            <input type="number" class="form-control" id="nuevaMascotaEdad" min="0">
                        </div>
                        <div class="mb-3">
                            <label class="form-label">Sexo</label>
                            <select class="form-select" id="nuevaMascotaSexo">
                                <option value="">Seleccione</option>
                                <option value="Macho">Macho</option>
                                <option value="Hembra">Hembra</option>
                            </select>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancelar</button>
                        <button type="button" class="btn-primary-custom" onclick="guardarMascotaNueva()">Guardar Mascota</button>
                    </div>
                </div>
            </div>
        </div>

        <script src="https://cdnjs.cloudflare.com/ajax/libs/bootstrap/5.3.0/js/bootstrap.bundle.min.js"></script>
        <script>
                            // ===================== INICIALIZACIÓN =====================
                            document.addEventListener('DOMContentLoaded', function () {
                                var hoy = new Date().toISOString().split('T')[0];
                                document.getElementById('filtroFecha').value = hoy;
                                document.getElementById('fechaAgenda').value = hoy;
                                document.getElementById('nuevaFecha').value = hoy;

                                setCurrentDate();
                                cargarVeterinarios();
                                cargarCitas();
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

                            // ===================== CARGAR DATOS =====================
                            function cargarVeterinarios() {
                                fetch('CitaServlet?accion=listarVeterinarios')
                                        .then(function (r) {
                                            return r.text();
                                        })
                                        .then(function (html) {
                                            document.getElementById('idVeterinario').innerHTML = '<option value="">Seleccione un veterinario</option>' + html;
                                            document.getElementById('filtroVeterinario').innerHTML = '<option value="">Todos los veterinarios</option>' + html;
                                            document.getElementById('nuevoVeterinario').innerHTML = '<option value="">Seleccione veterinario</option>' + html;
                                        })
                                        .catch(function (err) {
                                            console.error('Error al cargar veterinarios:', err);
                                        });
                            }

                            function cargarAgendasDisponibles() {
                                var vetId = document.getElementById('idVeterinario').value;
                                var fecha = document.getElementById('fechaAgenda').value;
                                var slotSel = document.getElementById('idSlot'); // ← ANTES era 'idAgenda'
                                if (!vetId) {
                                    slotSel.disabled = true;
                                    slotSel.innerHTML = '<option value="">Seleccione veterinario primero</option>';
                                    return;
                                }
                                if (!fecha) {
                                    fecha = new Date().toISOString().split('T')[0];
                                    document.getElementById('fechaAgenda').value = fecha;
                                }
                                slotSel.disabled = false;
                                slotSel.innerHTML = '<option value="">Cargando horarios...</option>';
                                var url = 'CitaServlet?accion=listarAgendasDisponibles&idVeterinario=' + encodeURIComponent(vetId) + '&fecha=' + encodeURIComponent(fecha);
                                fetch(url)
                                        .then(r => r.text())
                                        .then(html => {
                                            slotSel.innerHTML = html;
                                            if (slotSel.options.length === 1 && slotSel.options[0].value === '') {
                                                slotSel.innerHTML = '<option value="">No hay horarios disponibles</option>';
                                            }
                                        })
                                        .catch(err => {
                                            console.error('Error al cargar horarios:', err);
                                            slotSel.innerHTML = '<option value="">Error al cargar</option>';
                                        });
                            }

                            function cargarNuevasAgendas() {
                                var vetId = document.getElementById('nuevoVeterinario').value;
                                var fecha = document.getElementById('nuevaFecha').value;
                                var slotSel = document.getElementById('nuevoSlot');
                                if (!vetId) {
                                    slotSel.innerHTML = '<option value="">Seleccione veterinario primero</option>';
                                    return;
                                }
                                if (!fecha) {
                                    fecha = new Date().toISOString().split('T')[0];
                                    document.getElementById('nuevaFecha').value = fecha;
                                }
                                slotSel.innerHTML = '<option value="">Cargando horarios...</option>';
                                var url = 'CitaServlet?accion=listarAgendasDisponibles&idVeterinario=' + encodeURIComponent(vetId) + '&fecha=' + encodeURIComponent(fecha);
                                fetch(url)
                                        .then(r => r.text())
                                        .then(html => slotSel.innerHTML = html)
                                        .catch(err => {
                                            console.error('Error al cargar horarios:', err);
                                            slotSel.innerHTML = '<option value="">Error al cargar</option>';
                                        });
                            }

                            function cargarClientes() {
                                fetch('CitaServlet?accion=listarClientes')
                                        .then(function (r) {
                                            return r.text();
                                        })
                                        .then(function (html) {
                                            document.getElementById('idCliente').innerHTML = '<option value="">Seleccione cliente</option>' + html;
                                        })
                                        .catch(function (err) {
                                            console.error('Error al cargar clientes:', err);
                                            document.getElementById('idCliente').innerHTML = '<option value="">Error al cargar</option>';
                                        });
                            }

                            function cargarMascotas() {
                                var clienteId = document.getElementById('idCliente').value;
                                var mascotaSel = document.getElementById('idMascota');
                                mascotaSel.innerHTML = '<option value="">Seleccione mascota</option>';

                                if (clienteId) {
                                    fetch('CitaServlet?accion=listarMascotas&idCliente=' + encodeURIComponent(clienteId))
                                            .then(function (r) {
                                                return r.text();
                                            })
                                            .then(function (html) {
                                                mascotaSel.innerHTML = '<option value="">Seleccione mascota</option>' + html;
                                            })
                                            .catch(function (err) {
                                                console.error('Error al cargar mascotas:', err);
                                                mascotaSel.innerHTML = '<option value="">Error al cargar</option>';
                                            });
                                }
                            }

                            // ===================== OPERACIONES DE CITAS =====================
                            function cargarCitas() {
                                fetch('CitaServlet?accion=listar')
                                        .then(function (r) {
                                            return r.text();
                                        })
                                        .then(function (html) {
                                            document.getElementById('listaCitas').innerHTML = html;
                                        })
                                        .catch(function (err) {
                                            console.error('Error:', err);
                                            document.getElementById('listaCitas').innerHTML =
                                                    '<tr><td colspan="9" class="text-center text-danger py-4">' +
                                                    '<i class="fas fa-exclamation-triangle"></i> Error al cargar citas' +
                                                    '</td></tr>';
                                        });
                            }

                            function buscarCitasConFiltros() {
                                // Obtener valores de filtros
                                const filtroVeterinario = document.getElementById('filtroVeterinario').value;
                                const filtroFecha = document.getElementById('filtroFecha').value;
                                const filtroEstado = document.getElementById('filtroEstado').value;

                                // Construir URL
                                let url = 'CitaServlet?accion=listarFiltrado';

                                // Agregar parámetros si existen
                                if (filtroVeterinario)
                                    url += '&idVeterinario=' + encodeURIComponent(filtroVeterinario);
                                if (filtroFecha)
                                    url += '&fecha=' + encodeURIComponent(filtroFecha);
                                if (filtroEstado)
                                    url += '&estado=' + encodeURIComponent(filtroEstado);

                                console.log('🔍 Buscando con filtros:', url);

                                // Mostrar loading
                                document.getElementById('listaCitas').innerHTML = `
        <tr>
            <td colspan="9" class="text-center py-5">
                <div class="spinner-border text-primary" role="status">
                    <span class="visually-hidden">Buscando...</span>
                </div>
                <p class="mt-3 text-muted">Aplicando filtros...</p>
            </td>
        </tr>
    `;

                                // Hacer la petición
                                fetch(url)
                                        .then(r => r.text())
                                        .then(html => {
                                            document.getElementById('listaCitas').innerHTML = html;
                                        })
                                        .catch(err => {
                                            console.error('Error en filtros:', err);
                                            document.getElementById('listaCitas').innerHTML =
                                                    '<tr><td colspan="9" class="text-center text-danger py-4">' +
                                                    '<i class="fas fa-exclamation-triangle"></i> Error al aplicar filtros' +
                                                    '</td></tr>';
                                        });
                            }


                            function reservarCita() {
                                var form = document.getElementById('formCita');
                                if (!form.checkValidity()) {
                                    form.reportValidity();
                                    return;
                                }

                                var btn = document.querySelector('#modalCita .btn-primary-custom');
                                var originalText = btn.innerHTML;
                                btn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Procesando...';
                                btn.disabled = true;

                                var fd = new FormData(form);
                                fetch('CitaServlet', {method: 'POST', body: fd})
                                        .then(function (r) {
                                            return r.text();
                                        })
                                        .then(function (txt) {
                                            var parts = txt.split('|');
                                            var status = parts[0];
                                            var msg = parts.length > 1 ? parts[1] : txt;

                                            if (status === 'OK') {
                                                alert('✅ ' + msg);
                                                cargarCitas();
                                                bootstrap.Modal.getInstance(document.getElementById('modalCita')).hide();
                                                form.reset();
                                                form.reset();
                                                document.getElementById('idSlot').innerHTML = '<option value="">Primero seleccione veterinario</option>';
                                                document.getElementById('idSlot').disabled = true;
                                            } else {
                                                alert('❌ ' + msg);
                                            }
                                            btn.innerHTML = originalText;
                                            btn.disabled = false;
                                        })
                                        .catch(function (err) {
                                            console.error('Error:', err);
                                            alert('❌ Error al reservar la cita');
                                            btn.innerHTML = originalText;
                                            btn.disabled = false;
                                        });
                            }

                            function confirmarCita(idCita) {
                                if (!confirm('¿Confirmar esta cita?'))
                                    return;

                                var fd = new FormData();
                                fd.append('accion', 'confirmar');
                                fd.append('idCita', idCita);
                                fd.append('creadoPor', document.getElementById('creadoPor').value);

                                fetch('CitaServlet', {method: 'POST', body: fd})
                                        .then(function (r) {
                                            return r.text();
                                        })
                                        .then(function (txt) {
                                            var parts = txt.split('|');
                                            var status = parts[0];
                                            var msg = parts.length > 1 ? parts[1] : txt;

                                            alert(status === 'OK' ? '✅ ' + msg : '❌ ' + msg);
                                            if (status === 'OK')
                                                cargarCitas();
                                        })
                                        .catch(function (err) {
                                            console.error('Error:', err);
                                            alert('❌ Error al confirmar la cita');
                                        });
                            }

                            function abrirAnularCita(id) {
                                document.getElementById('idCitaAnular').value = id;
                                document.getElementById('motivoAnulacion').value = '';
                                new bootstrap.Modal(document.getElementById('modalAnularCita')).show();
                            }

                            function anularCita() {
                                var motivo = document.getElementById('motivoAnulacion').value.trim();
                                if (!motivo) {
                                    alert('⚠️ Ingrese un motivo');
                                    return;
                                }

                                var fd = new FormData();
                                fd.append('accion', 'anular');
                                fd.append('idCita', document.getElementById('idCitaAnular').value);
                                fd.append('motivo', motivo);
                                fd.append('creadoPor', document.getElementById('creadoPor').value);

                                fetch('CitaServlet', {method: 'POST', body: fd})
                                        .then(function (r) {
                                            return r.text();
                                        })
                                        .then(function (txt) {
                                            var parts = txt.split('|');
                                            var status = parts[0];
                                            var msg = parts.length > 1 ? parts[1] : txt;

                                            alert(status === 'OK' ? '✅ ' + msg : '❌ ' + msg);
                                            if (status === 'OK') {
                                                cargarCitas();
                                                bootstrap.Modal.getInstance(document.getElementById('modalAnularCita')).hide();
                                            }
                                        })
                                        .catch(function (err) {
                                            console.error('Error:', err);
                                            alert('❌ Error al anular la cita');
                                        });
                            }

                            function abrirReprogramar(id) {
                                document.getElementById('idCitaReprogramar').value = id;
                                document.getElementById('nuevoVeterinario').value = '';
                                document.getElementById('nuevaFecha').value = new Date().toISOString().split('T')[0];
                                document.getElementById('nuevoSlot').innerHTML = '<option value="">Seleccione horario</option>'; // ✅ CORREGIDO
                                document.getElementById('motivoReprogramacion').value = '';
                                document.getElementById('observacionesReprogramacion').value = '';
                                new bootstrap.Modal(document.getElementById('modalReprogramar')).show();
                            }

                            function reprogramarCita() {
                                var nuevoSlot = document.getElementById('nuevoSlot').value;
                                var motivo = document.getElementById('motivoReprogramacion').value.trim();

                                console.log("nuevoSlot:", nuevoSlot); // ← AGREGA ESTO
                                console.log("motivo:", motivo);

                                if (!nuevoSlot) {
                                    alert('⚠️ Seleccione un nuevo horario');
                                    return;
                                }
                                if (!motivo) {
                                    alert('⚠️ Ingrese el motivo de reprogramación');
                                    return;
                                }

                                var fd = new FormData();
                                fd.append('accion', 'reprogramar');
                                fd.append('idCita', document.getElementById('idCitaReprogramar').value);
                                fd.append('nuevoSlot', nuevoSlot);
                                fd.append('motivo', motivo);
                                fd.append('observaciones', document.getElementById('observacionesReprogramacion').value);
                                fd.append('creadoPor', document.getElementById('creadoPor').value);

                                fetch('CitaServlet', {method: 'POST', body: fd})
                                        .then(function (r) {
                                            return r.text();
                                        })
                                        .then(function (txt) {
                                            var parts = txt.split('|');
                                            var status = parts[0];
                                            var msg = parts.length > 1 ? parts[1] : txt;

                                            alert(status === 'OK' ? '✅ ' + msg : '❌ ' + msg);
                                            if (status === 'OK') {
                                                cargarCitas();
                                                bootstrap.Modal.getInstance(document.getElementById('modalReprogramar')).hide();
                                            }
                                        })
                                        .catch(function (err) {
                                            console.error('Error:', err);
                                            alert('❌ Error al reprogramar la cita');
                                        });
                            }

                            function verDetallesCita(idCita) {
                                alert('Detalles de la cita ID: ' + idCita + '\nEsta función se implementará próximamente.');
                            }

                            // ===================== EVENT LISTENERS =====================
                            document.getElementById('modalCita').addEventListener('shown.bs.modal', function () {
                                cargarClientes();
                            });

                            document.getElementById('modalCita').addEventListener('hidden.bs.modal', function () {
                                document.getElementById('formCita').reset();
                                document.getElementById('idAgenda').innerHTML = '<option value="">Primero seleccione veterinario</option>';
                                document.getElementById('idAgenda').disabled = true;

                            });


                            function limpiarFiltrosCitas() {
                                // Limpiar filtros
                                document.getElementById('filtroVeterinario').value = '';
                                document.getElementById('filtroFecha').value = '';
                                document.getElementById('filtroEstado').value = '';

                                // Volver a cargar todas las citas (con la función original)
                                cargarCitas();

                                // Mensaje opcional
                                console.log('✅ Filtros limpiados - Mostrando todas las citas');
                            }


                            // ===================== CLIENTE NUEVO =====================
                            function abrirModalClienteNuevo() {
                                // Resetear campos
                                document.getElementById('nuevoClienteNombre').value = '';
                                document.getElementById('nuevoClienteTipoDoc').value = '';
                                document.getElementById('nuevoClienteDocumento').value = '';
                                document.getElementById('nuevoClienteDocumento').disabled = true;
                                document.getElementById('nuevoClienteDocumento').placeholder = 'Seleccione tipo documento primero';
                                document.getElementById('nuevoClienteTelefono').value = '';
                                document.getElementById('nuevoClienteEmail').value = '';
                                document.getElementById('nuevoClienteDireccion').value = '';

                                // Cargar tipos de documento desde BD
                                cargarTiposDocumento();

                                new bootstrap.Modal(document.getElementById('modalClienteNuevo')).show();
                            }

                            function guardarClienteNuevo() {
                                const nombre = document.getElementById('nuevoClienteNombre').value.trim();
                                const tipoDoc = document.getElementById('nuevoClienteTipoDoc').value;
                                const nroDoc = document.getElementById('nuevoClienteDocumento').value.trim();
                                const telefono = document.getElementById('nuevoClienteTelefono').value.trim();
                                const email = document.getElementById('nuevoClienteEmail').value.trim();
                                const direccion = document.getElementById('nuevoClienteDireccion').value.trim();

                                // Validaciones
                                if (!nombre) {
                                    alert('⚠️ Ingrese nombre');
                                    return;
                                }
                                if (!tipoDoc) {
                                    alert('⚠️ Seleccione tipo de documento');
                                    return;
                                }
                                if (!nroDoc) {
                                    alert('⚠️ Ingrese número de documento');
                                    return;
                                }

                                // Enviar datos
                                const fd = new FormData();
                                fd.append('accion', 'crearCliente');
                                fd.append('nombre', nombre);
                                fd.append('tipo_documento', tipoDoc); // Enviamos el ID del tipo
                                fd.append('nro_documento', nroDoc);
                                fd.append('telefono', telefono);
                                fd.append('email', email);
                                fd.append('direccion', direccion);

                                fetch('CitaServlet', {method: 'POST', body: fd})
                                        .then(r => r.text())
                                        .then(txt => {
                                            const [status, msg] = txt.split('|');
                                            if (status === 'OK') {
                                                alert('✅ ' + msg);
                                                // Recargar lista de clientes en los selects
                                                fetch('CitaServlet?accion=listarClientes')
                                                        .then(r => r.text())
                                                        .then(html => {
                                                            document.getElementById('idCliente').innerHTML =
                                                                    '<option value="">Seleccione cliente</option>' + html;
                                                            document.getElementById('mascotaIdCliente').innerHTML =
                                                                    '<option value="">Seleccione cliente</option>' + html;
                                                        });
                                                bootstrap.Modal.getInstance(document.getElementById('modalClienteNuevo')).hide();
                                            } else {
                                                alert('❌ ' + msg);
                                            }
                                        })
                                        .catch(err => {
                                            console.error('Error:', err);
                                            alert('❌ Error al crear cliente');
                                        });
                            }

// ===================== MASCOTA NUEVA =====================
                            function abrirModalMascotaNueva() {
                                // Cargar clientes y especies al abrir el modal
                                fetch('CitaServlet?accion=listarClientes')
                                        .then(r => r.text())
                                        .then(html => {
                                            document.getElementById('mascotaIdCliente').innerHTML = '<option value="">Seleccione cliente</option>' + html;
                                        });

                                cargarEspecies();

                                // Resetear campos
                                document.getElementById('nuevaMascotaNombre').value = '';
                                document.getElementById('nuevaMascotaEspecie').value = '';
                                document.getElementById('nuevaMascotaRaza').innerHTML = '<option value="">Seleccione especie primero</option>';
                                document.getElementById('nuevaMascotaEdad').value = '';
                                document.getElementById('nuevaMascotaSexo').value = '';

                                new bootstrap.Modal(document.getElementById('modalMascotaNueva')).show();
                            }

                            function guardarMascotaNueva() {
                                var clienteId = document.getElementById('mascotaIdCliente').value;
                                var nombre = document.getElementById('nuevaMascotaNombre').value.trim();
                                var razaId = document.getElementById('nuevaMascotaRaza').value; // ← VERIFICA ESTE ID

                                console.log("🔍 Validando datos de mascota:");
                                console.log("  Cliente ID: " + clienteId);
                                console.log("  Nombre: " + nombre);
                                console.log("  Raza ID: " + razaId);

                                if (!clienteId || !nombre || !razaId) {
                                    alert('⚠️ Complete todos los campos obligatorios: Cliente, Nombre y Raza');
                                    return;
                                }

                                // Verificar que razaId no sea el placeholder
                                if (razaId === "" || razaId === "0") {
                                    alert('⚠️ Debe seleccionar una raza válida');
                                    return;
                                }

                                var fd = new FormData();
                                fd.append('accion', 'crearMascota');
                                fd.append('idCliente', clienteId);
                                fd.append('nombre', nombre);
                                fd.append('idRaza', razaId); // ← IMPORTANTE: Este nombre debe coincidir con el servlet

                                var edad = document.getElementById('nuevaMascotaEdad').value;
                                var sexo = document.getElementById('nuevaMascotaSexo').value;

                                if (edad)
                                    fd.append('edad', edad);
                                if (sexo)
                                    fd.append('sexo', sexo);

                                console.log("📤 Enviando datos:", {
                                    accion: 'crearMascota',
                                    idCliente: clienteId,
                                    nombre: nombre,
                                    idRaza: razaId,
                                    edad: edad,
                                    sexo: sexo
                                });

                                // Mostrar loading
                                var btn = document.querySelector('#modalMascotaNueva .btn-primary-custom');
                                var originalText = btn.innerHTML;
                                btn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Guardando...';
                                btn.disabled = true;

                                fetch('CitaServlet', {method: 'POST', body: fd})
                                        .then(r => r.text())
                                        .then(txt => {
                                            console.log("📥 Respuesta del servidor: " + txt);
                                            var [status, msg] = txt.split('|');

                                            if (status === 'OK') {
                                                alert('✅ ' + msg);
                                                // Recargar mascotas si el cliente está seleccionado en el modal principal
                                                if (document.getElementById('idCliente').value == clienteId) {
                                                    cargarMascotas();
                                                }
                                                // Cerrar modal
                                                bootstrap.Modal.getInstance(document.getElementById('modalMascotaNueva')).hide();
                                            } else {
                                                alert('❌ ' + msg);
                                            }
                                            btn.innerHTML = originalText;
                                            btn.disabled = false;
                                        })
                                        .catch(err => {
                                            console.error('❌ Error en fetch:', err);
                                            alert('❌ Error al crear mascota');
                                            btn.innerHTML = originalText;
                                            btn.disabled = false;
                                        });
                            }

                            // ===================== CARGAR ESPECIES Y RAZAS =====================
                            function cargarEspecies() {
                                fetch('CitaServlet?accion=listarEspecies')
                                        .then(r => r.text())
                                        .then(html => {
                                            document.getElementById('nuevaMascotaEspecie').innerHTML =
                                                    '<option value="">Seleccione especie</option>' + html;
                                        })
                                        .catch(err => console.error('Error al cargar especies:', err));
                            }


                            function cargarRazasPorEspecie() {
                                const especieId = document.getElementById('nuevaMascotaEspecie').value;
                                const razaSel = document.getElementById('nuevaMascotaRaza');

                                if (!especieId) {
                                    razaSel.innerHTML = '<option value="">Seleccione especie primero</option>';
                                    return;
                                }

                                razaSel.innerHTML = '<option value="">Cargando razas...</option>';

                                fetch('CitaServlet?accion=listarRazasPorEspecie&idEspecie=' + encodeURIComponent(especieId))
                                        .then(r => r.text())
                                        .then(html => {
                                            razaSel.innerHTML = '<option value="">Seleccione raza</option>' + html;
                                        })
                                        .catch(err => {
                                            console.error('Error al cargar razas:', err);
                                            razaSel.innerHTML = '<option value="">Error al cargar</option>';
                                        });
                            }


                            function cargarTiposDocumento() {
                                fetch('CitaServlet?accion=listarTiposDocumento')
                                        .then(r => r.text())
                                        .then(html => {
                                            document.getElementById('nuevoClienteTipoDoc').innerHTML = html;
                                        })
                                        .catch(err => {
                                            console.error('Error al cargar tipos de documento:', err);
                                            document.getElementById('nuevoClienteTipoDoc').innerHTML =
                                                    '<option value="">Error al cargar tipos</option>';
                                        });
                            }

                            function habilitarDocumento() {
                                const tipoDocSelect = document.getElementById('nuevoClienteTipoDoc');
                                const docInput = document.getElementById('nuevoClienteDocumento');

                                if (tipoDocSelect.value) {
                                    docInput.disabled = false;
                                    docInput.placeholder = 'Ingrese número de documento';
                                    docInput.focus();
                                } else {
                                    docInput.disabled = true;
                                    docInput.value = '';
                                    docInput.placeholder = 'Seleccione tipo documento primero';
                                }
                            }

        </script>
    </body>

</html>
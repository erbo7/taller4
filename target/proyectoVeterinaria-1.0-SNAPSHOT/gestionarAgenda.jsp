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
        <title>Gestionar Agenda - DiazPet</title>
        <link href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap/5.3.0/css/bootstrap.min.css" rel="stylesheet">
        <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" rel="stylesheet">
        <style>
            :root {
                --primary-color: #667eea;
                --secondary-color: #764ba2;
                --sidebar-width: 250px;
                --sidebar-collapsed: 70px;

                /* Colores para tipos de turno */
                --color-consulta: #10b981;
                --color-cirugia: #f59e0b;
                --color-urgencia: #ef4444;
                --color-vacunacion: #3b82f6;
                --color-otro: #8b5cf6;
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

            .filter-section {
                background: #f8f9fa;
                padding: 20px;
                border-radius: 10px;
                margin-bottom: 20px;
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

            .agenda-slot {
                border: 2px solid #e9ecef;
                border-radius: 10px;
                padding: 20px;
                margin-bottom: 15px;
                background: white;
                transition: all 0.3s;
            }

            .agenda-slot:hover {
                box-shadow: 0 5px 15px rgba(0,0,0,0.1);
                transform: translateY(-2px);
            }

            .agenda-slot.disponible {
                border-left: 5px solid #10b981;
                background: linear-gradient(to right, #f0fdf4 0%, white 100%);
            }

            .agenda-slot.ocupado {
                border-left: 5px solid #dc3545;
                background: linear-gradient(to right, #fff5f5 0%, white 100%);
            }

            .agenda-slot.no-disponible {
                border-left: 5px solid #6c757d;
                background: linear-gradient(to right, #f8f9fa 0%, white 100%);
            }

            .agenda-info {
                flex: 1;
            }

            .agenda-info h5 {
                color: #2c3e50;
                font-weight: bold;
                margin-bottom: 12px;
                font-size: 18px;
            }

            .agenda-detail {
                display: flex;
                align-items: center;
                margin-bottom: 8px;
                color: #666;
            }

            .agenda-detail i {
                width: 25px;
                color: var(--primary-color);
            }

            .badge-status {
                padding: 8px 16px;
                border-radius: 20px;
                font-size: 13px;
                font-weight: 600;
            }

            .badge-disponible {
                background: #d1fae5;
                color: #10b981;
            }

            .badge-ocupado {
                background: #fee2e2;
                color: #dc3545;
            }

            .badge-no-disponible {
                background: #e9ecef;
                color: #6c757d;
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

            .btn-actions {
                display: flex;
                gap: 10px;
                flex-wrap: wrap;
            }

            .btn-sm-custom {
                padding: 8px 16px;
                font-size: 13px;
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

            .badge-turno {
                padding: 6px 15px;
                border-radius: 15px;
                font-size: 12px;
                font-weight: 600;
                display: inline-block;
                margin: 2px;
            }

            .badge-consulta {
                background-color: #d1fae5;
                color: #047857;
            }

            .badge-cirugia {
                background-color: #fef3c7;
                color: #92400e;
            }

            .badge-urgencia {
                background-color: #fee2e2;
                color: #991b1b;
            }

            .badge-vacunacion {
                background-color: #dbeafe;
                color: #1e40af;
            }

            .badge-otro {
                background-color: #e9d5ff;
                color: #5b21b6;
            }

            .btn-tabla {
                padding: 6px 12px;
                font-size: 13px;
                margin: 0 3px;
                border-radius: 6px;
            }

            .slots-info {
                font-size: 12px;
                color: #666;
                background: #f8f9fa;
                padding: 5px 10px;
                border-radius: 5px;
                margin-top: 5px;
            }

            .slots-info i {
                color: var(--primary-color);
            }

            .alert-info-custom {
                background: #e0f2fe;
                border-left: 4px solid #0ea5e9;
                border-radius: 8px;
                padding: 15px;
                margin: 15px 0;
            }

            .time-validation {
                font-size: 12px;
                margin-top: 5px;
            }

            .time-validation.error {
                color: #dc3545;
            }

            .time-validation.success {
                color: #198754;
            }


            /* Ajuste para tabla con 6 columnas */
            #tablaAgenda th:nth-child(1) {
                width: 20%;
            } /* Veterinario */
            #tablaAgenda th:nth-child(2) {
                width: 15%;
            } /* Fecha */
            #tablaAgenda th:nth-child(3) {
                width: 20%;
            } /* Horario */
            #tablaAgenda th:nth-child(4) {
                width: 15%;
            } /* Tipo Turno */
            #tablaAgenda th:nth-child(5) {
                width: 20%;
            } /* Observaciones */
            #tablaAgenda th:nth-child(6) {
                width: 10%;
            } /* Acciones */


            .card-custom-header {
                background: linear-gradient(135deg, var(--primary-color) 0%, var(--secondary-color) 100%);
                color: #fff; /* texto en blanco para contraste */
                padding: 10px;
                display: flex;
                justify-content: space-between;
                align-items: center;
                border-radius: 6px 6px 0 0; /* opcional: esquinas superiores redondeadas */
            }

            .card-custom-header h5,
            .card-custom-header h5 i {
                color: #fff !important; /* fuerza el blanco */
                margin: 0;              /* opcional: elimina margen extra */
                font-weight: 600;       /* opcional: más marcado */
            }
        </style>
    </head>
    <body>
        <div class="sidebar" id="sidebar">
            <div class="sidebar-header">
                <div>
                    <h3>🐾 DiazPet</h3>
                    <small style="color: rgba(255,255,255,0.8);">Gestión de Agenda</small>
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

            <a href="RecepcionistaServlet" class="menu-item">
                <i class="fas fa-home"></i>
                <span class="menu-text">Inicio</span>
            </a>
            <a href="gestionarAgenda.jsp" class="menu-item active">
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

    <div class="main-content">
        <div class="top-bar">
            <div class="d-flex justify-content-between align-items-center">
                <div>
                    <h2><i class="fas fa-calendar-alt"></i> Gestión de Agenda Médica</h2>
                    <p class="text-muted mb-0">
                        <i class="far fa-calendar"></i>
                        <span id="currentDate"></span>
                    </p>
                </div>
                <button class="btn-primary-custom" data-bs-toggle="modal" data-bs-target="#modalAgenda" onclick="nuevaAgenda()">
                    <i class="fas fa-plus"></i> Nueva Agenda
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
                    <div class="row g-3">
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
                                <i class="fas fa-calendar"></i> Fecha
                            </label>
                            <input type="date" class="form-control" id="filtroFecha">
                        </div>
                        <div class="col-md-3">
                            <label class="form-label">
                                <i class="fas fa-sun"></i> Tipo de Turno
                            </label>
                            <select class="form-select" id="filtroTurno">
                                <option value="">Todos los tipos</option>
                            </select>
                        </div>
                        <div class="col-md-2 d-flex align-items-end">
                            <button class="btn-primary-custom w-100" onclick="cargarAgenda()">
                                <i class="fas fa-search"></i> Buscar
                            </button>
                        </div>
                    </div>
                    <div class="row mt-3">
                        <div class="col-md-4 d-flex align-items-end">
                            <button class="btn-outline-custom w-100" onclick="limpiarFiltros()">
                                <i class="fas fa-redo"></i> Limpiar Filtros
                            </button>
                        </div>
                    </div>
                </div>
            </div>

            <!-- Lista de Agendas -->
            <div class="card-custom">
                <div class="card-custom-header">
                    <h5><i class="fas fa-list"></i> Agendas Registradas</h5>
                    <button class="btn-outline-custom btn-sm-custom" onclick="exportarAgenda()">
                        <i class="fas fa-download"></i> Exportar
                    </button>
                </div>
                <div class="card-custom-body">
                    <div class="table-responsive">
                        <table class="table table-hover" id="tablaAgenda">
                            <thead>
                                <tr>
                                    <th><i class="fas fa-user-md"></i> Veterinario</th>
                                    <th><i class="fas fa-calendar"></i> Fecha</th>
                                    <th><i class="fas fa-clock"></i> Horario</th>
                                    <th><i class="fas fa-stethoscope"></i> Tipo Turno</th>
                                    <th><i class="fas fa-sticky-note"></i> Observaciones</th>
                                    <th><i class="fas fa-cog"></i> Acciones</th>
                                </tr>
                            </thead>
                            <tbody id="listaAgenda">
                                <tr>
                                    <td colspan="6" class="text-center py-5">
                                        <div class="spinner-border text-primary" role="status">
                                            <span class="visually-hidden">Cargando...</span>
                                        </div>
                                        <p class="mt-3 text-muted">Cargando agendas...</p>
                                    </td>
                                </tr>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- Modal para Agenda -->
    <div class="modal fade" id="modalAgenda" tabindex="-1">
        <div class="modal-dialog modal-lg">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title" id="tituloModal">
                        <i class="fas fa-calendar-plus"></i> Nueva Agenda
                    </h5>
                    <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
                </div>
                <div class="modal-body">
                    <form id="formAgenda" onsubmit="return false;">
                        <input type="hidden" id="idAgenda" name="idAgenda">
                        <input type="hidden" id="accionForm" name="accion" value="crear">


                        <div class="row">
                            <div class="col-md-6 mb-3">
                                <label class="form-label">
                                    <i class="fas fa-user-md"></i> Veterinario *
                                </label>
                                <select class="form-select" id="idVeterinario" name="idVeterinario" required onchange="validarHorario()">
                                    <option value="">Seleccione un veterinario</option>
                                </select>
                            </div>

                            <div class="col-md-6 mb-3">
                                <label class="form-label">
                                    <i class="fas fa-calendar"></i> Fecha *
                                </label>
                                <input type="date" class="form-control" id="fecha" name="fecha" required onchange="validarFeriado(); validarHorario()">
                                <div class="time-validation" id="feriadoValidation"></div>
                            </div>
                        </div>

                        <div class="row">
                            <div class="col-md-4 mb-3">
                                <label class="form-label">
                                    <i class="fas fa-clock"></i> Hora Inicio *
                                </label>
                                <input type="time" 
                                       class="form-control" 
                                       id="horaInicio" 
                                       name="horaInicio" 
                                       required
                                       onchange="validarHorario()"
                                       step="300"> <!-- 5 minutos -->
                                <div class="time-validation" id="horaInicioValidation"></div>
                            </div>

                            <div class="col-md-4 mb-3">
                                <label class="form-label">
                                    <i class="fas fa-clock"></i> Hora Fin *
                                </label>
                                <input type="time" 
                                       class="form-control" 
                                       id="horaFin" 
                                       name="horaFin" 
                                       required
                                       onchange="validarHorario()"
                                       step="300">
                                <div class="time-validation" id="horaFinValidation"></div>
                            </div>

                            <div class="col-md-4 mb-3">
                                <label class="form-label">
                                    <i class="fas fa-stethoscope"></i> Tipo de Turno *
                                </label>
                                <select class="form-select" id="idTipoTurno" name="idTipoTurno" required>
                                    <option value="">Seleccione tipo de turno</option>
                                </select>
                                <small class="text-muted" id="duracionInfo"></small>
                            </div>
                        </div>

                        <div class="mb-3">
                            <label class="form-label">
                                <i class="fas fa-sticky-note"></i> Observaciones
                            </label>
                            <textarea class="form-control" id="observaciones" name="observaciones" rows="3"
                                      placeholder="Notas adicionales sobre este horario..."></textarea>
                        </div>

                        <div class="alert alert-warning" id="conflictoAlert" style="display: none;">
                            <i class="fas fa-exclamation-triangle"></i>
                            <strong>Advertencia:</strong> <span id="conflictoTexto"></span>
                        </div>
                    </form>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">
                        <i class="fas fa-times"></i> Cancelar
                    </button>
                    <button type="button" class="btn-primary-custom" id="btnGuardar" onclick="guardarAgenda(event)">
                        <i class="fas fa-save"></i> Registrar Agenda
                    </button>
                </div>
            </div>
        </div>
    </div>

    <!-- Modal para Anular -->
    <div class="modal fade" id="modalAnular" tabindex="-1">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header bg-danger">
                    <h5 class="modal-title">
                        <i class="fas fa-ban"></i> Anular Agenda
                    </h5>
                    <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
                </div>
                <div class="modal-body">
                    <input type="hidden" id="idAgendaAnular" name="idAgendaAnular">
                    <div class="alert alert-warning">
                        <i class="fas fa-exclamation-triangle"></i>
                        <strong>Advertencia:</strong> Esta acción no se puede deshacer. Se anulara definitivamente la agenda.
                    </div>
                    <div class="mb-3">
                        <label class="form-label">
                            <i class="fas fa-comment"></i> Motivo de anulación *
                        </label>
                        <textarea class="form-control" id="motivoAnular" name="motivoAnular" rows="4" required
                                  placeholder="Explique el motivo por el cual se anula este horario..."></textarea>
                    </div>
                </div>
                <div class="modal-footer">
                    <button class="btn btn-secondary" data-bs-dismiss="modal">
                        <i class="fas fa-times"></i> Cancelar
                    </button>
                    <button class="btn btn-danger" onclick="anularAgenda()">
                        <i class="fas fa-ban"></i> Confirmar Anulación
                    </button>
                </div>
            </div>
        </div>
    </div>

    <!-- Modal para Ver Slots -->
    <div class="modal fade" id="modalSlots" tabindex="-1">
        <div class="modal-dialog modal-lg">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title">
                        <i class="fas fa-list"></i> Slots Generados
                    </h5>
                    <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
                </div>
                <div class="modal-body">
                    <div id="slotsList"></div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">
                        <i class="fas fa-times"></i> Cerrar
                    </button>
                </div>
            </div>
        </div>
    </div>

    <script src="https://cdnjs.cloudflare.com/ajax/libs/bootstrap/5.3.0/js/bootstrap.bundle.min.js"></script>
    <script>
                        // Variables globales
                        let tiposTurnoData = [];
                        let slotsGenerados = 0;

                        // Funciones principales
                        function toggleSidebar() {
                            document.getElementById('sidebar').classList.toggle('collapsed');
                        }

                        function setCurrentDate() {
                            const options = {weekday: 'long', year: 'numeric', month: 'long', day: 'numeric'};
                            const date = new Date().toLocaleDateString('es-ES', options);
                            document.getElementById('currentDate').textContent = date.charAt(0).toUpperCase() + date.slice(1);
                        }



                        function cargarVeterinarios() {
                            fetch('AgendaServlet?accion=listarVeterinarios')
                                    .then(function (r) {
                                        return r.text();
                                    })
                                    .then(function (html) {
                                        document.getElementById('filtroVeterinario').innerHTML =
                                                '<option value="">Todos los veterinarios</option>' + html;
                                        document.getElementById('idVeterinario').innerHTML =
                                                '<option value="">Seleccione un veterinario</option>' + html;
                                    })
                                    .catch(function (err) {
                                        console.error('Error al cargar veterinarios:', err);
                                    });
                        }

                        function cargarTiposTurno() {
                            fetch('AgendaServlet?accion=listarTiposTurno')
                                    .then(function (r) {
                                        return r.text();
                                    })
                                    .then(function (html) {
                                        document.getElementById('filtroTurno').innerHTML =
                                                '<option value="">Todos los tipos</option>' + html;
                                        document.getElementById('idTipoTurno').innerHTML =
                                                '<option value="">Seleccione tipo de turno</option>' + html;

                                    })
                                    .catch(function (err) {
                                        console.error('Error al cargar tipos de turno:', err);
                                    });
                        }

                        function validarFeriado() {
                            var fecha = document.getElementById('fecha').value;
                            var validationDiv = document.getElementById('feriadoValidation');
                            if (!fecha) {
                                validationDiv.innerHTML = '';
                                return;
                            }

                            var url = 'AgendaServlet?accion=verificarFeriado&fecha=' + encodeURIComponent(fecha);
                            fetch(url)
                                    .then(function (r) {
                                        return r.text();
                                    })
                                    .then(function (text) {
                                        var partes = text.split('|');
                                        var status = partes[0];
                                        var msg = partes.slice(1).join('|');
                                        if (status === 'ERROR') {
                                            validationDiv.innerHTML = '<span class="error"><i class="fas fa-exclamation-triangle"></i> ' + msg + '</span>';
                                        } else {
                                            validationDiv.innerHTML = '<span class="success"><i class="fas fa-check-circle"></i> ' + msg + '</span>';
                                        }
                                    })
                                    .catch(function (err) {
                                        validationDiv.innerHTML = '<span class="error">Error al validar fecha</span>';
                                    });
                        }

                        function validarHorario() {
                            var idVeterinario = document.getElementById('idVeterinario').value;
                            var fecha = document.getElementById('fecha').value;
                            var horaInicio = document.getElementById('horaInicio').value;
                            var horaFin = document.getElementById('horaFin').value;
                            var idAgenda = document.getElementById('idAgenda').value;
                            var inicioValidation = document.getElementById('horaInicioValidation');
                            var finValidation = document.getElementById('horaFinValidation');
                            var conflictoAlert = document.getElementById('conflictoAlert');
                            inicioValidation.innerHTML = '';
                            finValidation.innerHTML = '';
                            conflictoAlert.style.display = 'none';

                            // Validar horas básicas
                            if (horaInicio && horaFin) {
                                if (horaInicio >= horaFin) {
                                    inicioValidation.innerHTML = '<span class="error">La hora de inicio debe ser anterior a la hora de fin</span>';
                                    finValidation.innerHTML = '<span class="error">La hora de fin debe ser posterior a la hora de inicio</span>';
                                    return;
                                }
                                var inicio = new Date('1970-01-01T' + horaInicio + ':00');
                                var fin = new Date('1970-01-01T' + horaFin + ':00');
                                var diffMinutos = (fin - inicio) / 60000;
                                if (diffMinutos < 30) {
                                    finValidation.innerHTML = '<span class="error">El intervalo mínimo es de 30 minutos</span>';
                                    return;
                                }
                            }

                            // Validar conflicto si hay todos los datos
                            if (idVeterinario && fecha && horaInicio && horaFin) {
                                // ✅ SIN TEMPLATE LITERALS
                                var url = 'AgendaServlet?accion=verificarConflicto&idVeterinario=' + encodeURIComponent(idVeterinario);
                                url += '&fecha=' + encodeURIComponent(fecha);
                                url += '&horaInicio=' + encodeURIComponent(horaInicio);
                                url += '&horaFin=' + encodeURIComponent(horaFin);
                                if (idAgenda) {
                                    url += '&idAgenda=' + encodeURIComponent(idAgenda);
                                }
                                fetch(url)
                                        .then(function (r) {
                                            return r.text();
                                        })
                                        .then(function (text) {
                                            var partes = text.split('|');
                                            var status = partes[0];
                                            var msg = partes.slice(1).join('|');
                                            if (status === 'ERROR') {
                                                conflictoAlert.style.display = 'block';
                                                document.getElementById('conflictoTexto').textContent = msg;
                                            }
                                        })
                                        .catch(function (err) {
                                            console.error('Error al validar horario:', err);
                                        });
                            }
                        }

                        function cargarAgenda() {
                            var idVeterinario = document.getElementById('filtroVeterinario').value;
                            var fecha = document.getElementById('filtroFecha').value;
                            var url = 'AgendaServlet?accion=listar';
                            if (idVeterinario && fecha) {

                                url = 'AgendaServlet?accion=porVeterinario&idVeterinario=' + encodeURIComponent(idVeterinario) + '&fecha=' + encodeURIComponent(fecha);
                            }
                            fetch(url)
                                    .then(function (r) {
                                        return r.text();
                                    })
                                    .then(function (html) {
                                        document.getElementById('listaAgenda').innerHTML = html;
                                    })
                                    .catch(function (err) {
                                        console.error('Error al cargar agenda:', err);
                                        document.getElementById('listaAgenda').innerHTML =
                                                '<tr><td colspan="6" class="text-center py-5">Error al cargar agenda</td></tr>';
                                    });
                        }

                        function nuevaAgenda() {
                            document.getElementById('tituloModal').innerHTML = '<i class="fas fa-calendar-plus"></i> Nueva Agenda';
                            document.getElementById('accionForm').value = 'crear';
                            document.getElementById('idAgenda').value = '';
                            document.getElementById('formAgenda').reset();
                            document.getElementById('btnGuardar').innerHTML = '<i class="fas fa-save"></i> Registrar Agenda';
                            var hoy = new Date().toISOString().split('T')[0];
                            document.getElementById('fecha').value = hoy;
                            document.getElementById('fecha').setAttribute('min', hoy);
                        }

                        function guardarAgenda(event) {
                            event.preventDefault();
                            console.log("=== guardarAgenda() llamado ===");

                            // ⭐⭐ CORRECCIÓN CRÍTICA: Obtener la acción del formulario
                            var accion = document.getElementById('accionForm').value;
                            console.log("Acción detectada:", accion);

                            // Obtener valores directamente
                            var idVeterinario = document.getElementById('idVeterinario').value;
                            var fecha = document.getElementById('fecha').value;
                            var horaInicio = document.getElementById('horaInicio').value;
                            var horaFin = document.getElementById('horaFin').value;
                            var idTipoTurno = document.getElementById('idTipoTurno').value;
                            var observaciones = document.getElementById('observaciones').value;
                            var idAgenda = document.getElementById('idAgenda').value; // Para editar

                            // Validaciones básicas (igual que antes)
                            if (!idVeterinario || !fecha || !horaInicio || !horaFin || !idTipoTurno) {
                                alert('❌ Complete todos los campos obligatorios');
                                return;
                            }

                            if (horaInicio >= horaFin) {
                                alert('❌ La hora de inicio debe ser anterior a la hora de fin');
                                return;
                            }

                            // Mostrar loading
                            var btn = document.getElementById('btnGuardar');
                            var originalText = btn.innerHTML;
                            btn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Procesando...';
                            btn.disabled = true;

                            // ⭐⭐ CORRECCIÓN: Usar FormData correctamente
                            var formData = new FormData();
                            formData.append('accion', accion); // ⭐⭐ Usar la acción del formulario

                            // Solo agregar idAgenda si estamos editando (para mantener compatibilidad)
                            if (idAgenda) {
                                formData.append('idAgenda', idAgenda);
                            }

                            formData.append('idVeterinario', idVeterinario);
                            formData.append('fecha', fecha);
                            formData.append('horaInicio', horaInicio);
                            formData.append('horaFin', horaFin);
                            formData.append('idTipoTurno', idTipoTurno);
                            formData.append('observaciones', observaciones);

                            console.log("Enviando:", {
                                accion: accion,
                                idAgenda: idAgenda,
                                idVeterinario: idVeterinario,
                                fecha: fecha,
                                horaInicio: horaInicio,
                                horaFin: horaFin,
                                idTipoTurno: idTipoTurno
                            });

                            // Enviar (igual que antes)
                            fetch("AgendaServlet", {method: "POST", body: formData})
                                    .then(function (res) {
                                        return res.text();
                                    })
                                    .then(function (txt) {
                                        console.log("Respuesta del servidor:", txt);
                                        var partes = txt.split('|', 2);
                                        var status = partes[0];
                                        var msg = partes.length > 1 ? partes[1] : 'Operación exitosa';

                                        if (status === "OK") {
                                            alert("✅ " + msg);
                                            cargarAgenda();
                                            bootstrap.Modal.getInstance(document.getElementById("modalAgenda")).hide();
                                        } else {
                                            alert("❌ " + msg);
                                        }
                                        btn.innerHTML = originalText;
                                        btn.disabled = false;
                                    })
                                    .catch(function (err) {
                                        console.error("Error:", err);
                                        alert("❌ Error de conexión: " + err.message);
                                        btn.innerHTML = originalText;
                                        btn.disabled = false;
                                    });
                        }

                        function editarAgenda(idAgenda) {
                            // ✅ SIN TEMPLATE LITERALS
                            var url = 'AgendaServlet?accion=obtener&idAgenda=' + encodeURIComponent(idAgenda);
                            fetch(url)
                                    .then(function (r) {
                                        return r.text();
                                    })
                                    .then(function (text) {
                                        var partes = text.split('|');
                                        var status = partes[0];
                                        if (status !== 'OK') {
                                            alert('❌ Error al cargar el horario');
                                            return;
                                        }
                                        var data = partes.slice(1).join('|');
                                        var campos = data.split(';');
                                        var id = campos[0];
                                        var vet = campos[1];
                                        var fecha = campos[2];
                                        var hIni = campos[3];
                                        var hFin = campos[4];
                                        var idTipoTurno = campos[5];
                                        var obs = campos[6] || '';

                                        document.getElementById('tituloModal').innerHTML = '<i class="fas fa-edit"></i> Editar Agenda';
                                        document.getElementById('accionForm').value = 'actualizar';
                                        document.getElementById('btnGuardar').innerHTML = '<i class="fas fa-save"></i> Actualizar Agenda';
                                        document.getElementById('idAgenda').value = id;
                                        document.getElementById('idVeterinario').value = vet;
                                        document.getElementById('fecha').value = fecha;
                                        document.getElementById('horaInicio').value = hIni;
                                        document.getElementById('horaFin').value = hFin;
                                        document.getElementById('idTipoTurno').value = idTipoTurno;
                                        document.getElementById('observaciones').value = obs;

                                        //mostrarDuracion();
                                        setTimeout(function () {
                                            validarHorario();
                                        }, 100);
                                        new bootstrap.Modal(document.getElementById('modalAgenda')).show();
                                    })
                                    .catch(function (err) {
                                        console.error('Error:', err);
                                        alert('❌ Error al cargar datos del horario');
                                    });
                        }

                        function verSlots(idAgenda) {
                            var slotsDiv = document.getElementById('slotsList');
                            slotsDiv.innerHTML = `
                <div class="alert alert-info">
                    <i class="fas fa-info-circle"></i>
                    Los slots generados automáticamente para esta agenda estarán disponibles 
                    en el módulo de citas para que los clientes puedan reservar turnos.
                </div>
                <div class="text-center py-4">
                    <i class="fas fa-calendar-alt" style="font-size: 48px; color: var(--primary-color);"></i>
                    <h5 class="mt-3">Slots Generados</h5>
                    <p>Los slots se crean automáticamente según el tipo de turno seleccionado.</p>
                    <button class="btn-primary-custom mt-2" onclick="location.href='gestionarCitas.jsp'">
                        <i class="fas fa-external-link-alt"></i> Ir a Citas
                    </button>
                </div>
            `;
                            new bootstrap.Modal(document.getElementById('modalSlots')).show();
                        }

                        function abrirAnular(idAgenda) {
                            document.getElementById("idAgendaAnular").value = idAgenda;
                            document.getElementById("motivoAnular").value = '';
                            new bootstrap.Modal(document.getElementById('modalAnular')).show();
                        }

                        function anularAgenda() {
                            const idAgenda = document.getElementById('idAgendaAnular').value;
                            const motivo = document.getElementById('motivoAnular').value.trim();
                            // ✅ NO usar el campo creadoPor del formulario, usar la sesión (ya lo hace el servlet)
                            if (!motivo) {
                                alert('⚠️ Ingrese un motivo de anulación');
                                return;
                            }
                            const formData = new FormData();
                            formData.append('accion', 'anular');
                            formData.append('idAgenda', idAgenda);
                            formData.append('motivo', motivo);
                            // ✅ El servlet obtiene anuladoPor de la sesión, no del formulario
                            fetch('AgendaServlet', {method: 'POST', body: formData})
                                    .then(r => r.text())
                                    .then(txt => {
                                        const [status, msg] = txt.split('|', 2);
                                        if (status !== 'OK') {
                                            alert('❌ ' + (msg || 'Error al anular'));
                                            return;
                                        }
                                        alert('✅ ' + (msg || 'Agenda anulada exitosamente'));
                                        cargarAgenda();
                                        bootstrap.Modal.getInstance(document.getElementById('modalAnular')).hide();
                                    })
                                    .catch(err => {
                                        console.error('Error:', err);
                                        alert('❌ Error al anular: ' + err.message);
                                    });
                        }

                        function limpiarFiltros() {
                            document.getElementById('filtroVeterinario').value = '';
                            document.getElementById('filtroFecha').value = '';
                            document.getElementById('filtroTurno').value = '';
                            cargarAgenda();
                        }

                        function exportarAgenda() {
                            alert('🚧 Función de exportación en desarrollo');
                        }

                        // Event Listeners
                        document.getElementById('modalAgenda').addEventListener('hidden.bs.modal', function () {
                            document.getElementById('formAgenda').reset();
                            document.getElementById('feriadoValidation').innerHTML = '';
                            document.getElementById('horaInicioValidation').innerHTML = '';
                            document.getElementById('horaFinValidation').innerHTML = '';
                            document.getElementById('conflictoAlert').style.display = 'none';
                            document.getElementById('duracionInfo').textContent = '';
                            // document.getElementById('slotsInfo').innerHTML = '';
                        });


                        // Inicialización
                        document.addEventListener('DOMContentLoaded', function () {
                            setCurrentDate();
                            //configurarFechas();
                            cargarVeterinarios();
                            cargarTiposTurno();
                            cargarAgenda();
                        });
    </script>
</body>
</html>
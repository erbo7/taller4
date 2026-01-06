package cx;

import modelo.AgendaSlot;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AgendaSlotDAO {

    private final conexion cx = new conexion();

    // ✅ Obtener slots disponibles por veterinario y fecha
    public List<AgendaSlot> obtenerSlotsDisponiblesPorVeterinarioYFecha(int idVeterinario, java.time.LocalDate fecha) {
        List<AgendaSlot> slots = new ArrayList<>();
        String sql = "SELECT asl.*, v.NOMBRE AS nombre_veterinario, "
                + "tt.NOMBRE AS tipo_turno, tt.DURACION_MINUTOS "
                + "FROM agenda_slots asl "
                + "INNER JOIN veterinarios v ON asl.ID_VETERINARIO = v.ID_VETERINARIO "
                + "INNER JOIN agenda a ON asl.ID_AGENDA = a.ID_AGENDA "
                + "INNER JOIN tipos_turno tt ON a.ID_TIPO_TURNO = tt.ID_TIPO_TURNO "
                + "WHERE asl.ID_VETERINARIO = ? "
                + "AND DATE(asl.FECHA_HORA_INICIO) = ? "
                + "AND asl.DISPONIBLE = 1 "
                + "AND asl.ID_CITA IS NULL "
                + "AND a.ESTADO = 'ACTIVO' "
                + "ORDER BY asl.FECHA_HORA_INICIO ASC";

        try (Connection conn = cx.conectar(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idVeterinario);
            ps.setDate(2, java.sql.Date.valueOf(fecha));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    slots.add(mapearAgendaSlot(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al obtener slots disponibles: " + e.getMessage());
            e.printStackTrace();
        }
        return slots;
    }

    // ✅ Obtener slots por agenda
    public List<AgendaSlot> obtenerSlotsPorAgenda(int idAgenda) {
        List<AgendaSlot> slots = new ArrayList<>();
        String sql = "SELECT asl.*, v.NOMBRE AS nombre_veterinario "
                + "FROM agenda_slots asl "
                + "INNER JOIN veterinarios v ON asl.ID_VETERINARIO = v.ID_VETERINARIO "
                + "WHERE asl.ID_AGENDA = ? "
                + "ORDER BY asl.FECHA_HORA_INICIO ASC";

        try (Connection conn = cx.conectar(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idAgenda);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    slots.add(mapearAgendaSlot(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al obtener slots por agenda: " + e.getMessage());
        }
        return slots;
    }

    // ✅ Reservar slot para una cita
    public boolean reservarSlot(int idSlot, int idCita) {
        String sql = "UPDATE agenda_slots SET DISPONIBLE = 0, ID_CITA = ? WHERE ID_SLOT = ? AND DISPONIBLE = 1";

        try (Connection conn = cx.conectar(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idCita);
            ps.setInt(2, idSlot);
            int filas = ps.executeUpdate();
            return filas > 0;
        } catch (SQLException e) {
            System.err.println("❌ Error al reservar slot: " + e.getMessage());
            return false;
        }
    }

    // ✅ Liberar slot (cuando se cancela una cita)
    public boolean liberarSlot(int idSlot) {
        String sql = "UPDATE agenda_slots SET DISPONIBLE = 1, ID_CITA = NULL WHERE ID_SLOT = ?";

        try (Connection conn = cx.conectar(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idSlot);
            int filas = ps.executeUpdate();
            return filas > 0;
        } catch (SQLException e) {
            System.err.println("❌ Error al liberar slot: " + e.getMessage());
            return false;
        }
    }

    // ✅ Verificar si un slot está disponible
    public boolean slotDisponible(int idSlot) {
        String sql = "SELECT COUNT(*) FROM agenda_slots WHERE ID_SLOT = ? AND DISPONIBLE = 1 AND ID_CITA IS NULL";

        try (Connection conn = cx.conectar(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idSlot);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al verificar disponibilidad de slot: " + e.getMessage());
        }
        return false;
    }

    // ✅ Método privado para mapear ResultSet a AgendaSlot
    private AgendaSlot mapearAgendaSlot(ResultSet rs) throws SQLException {
        AgendaSlot slot = new AgendaSlot();
        slot.setIdSlot(rs.getInt("ID_SLOT"));
        slot.setIdAgenda(rs.getInt("ID_AGENDA"));
        slot.setIdVeterinario(rs.getInt("ID_VETERINARIO"));
        slot.setFechaHoraInicio(rs.getTimestamp("FECHA_HORA_INICIO").toLocalDateTime());
        slot.setFechaHoraFin(rs.getTimestamp("FECHA_HORA_FIN").toLocalDateTime());
        slot.setDisponible(rs.getBoolean("DISPONIBLE"));
        slot.setIdCita(rs.getObject("ID_CITA", Integer.class));
        slot.setCreadoEn(rs.getTimestamp("CREADO_EN") != null
                ? rs.getTimestamp("CREADO_EN").toLocalDateTime() : null);
        slot.setModificadoEn(rs.getTimestamp("MODIFICADO_EN") != null
                ? rs.getTimestamp("MODIFICADO_EN").toLocalDateTime() : null);

        // Datos adicionales
        if (existeColumna(rs, "nombre_veterinario")) {
            slot.setNombreVeterinario(rs.getString("nombre_veterinario"));
        }
        if (existeColumna(rs, "tipo_turno")) {
            slot.setTipoTurno(rs.getString("tipo_turno"));
        }
        if (existeColumna(rs, "DURACION_MINUTOS")) {
            slot.setDuracionMinutos(rs.getObject("DURACION_MINUTOS", Integer.class));
        }

        return slot;
    }

    // ✅ Método auxiliar para verificar si una columna existe en el ResultSet
    private boolean existeColumna(ResultSet rs, String columna) {
        try {
            rs.findColumn(columna);
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    // Agrega esto a tu AgendaSlotDAO.java existente
    public List<AgendaSlot> obtenerSlotsDisponiblesParaCita(int idVeterinario, java.time.LocalDate fecha) {
        List<AgendaSlot> slots = new ArrayList<>();
        String sql = """
        SELECT 
            s.ID_SLOT,
            s.FECHA_HORA_INICIO,
            s.FECHA_HORA_FIN,
            v.NOMBRE AS nombre_veterinario,
            a.OBSERVACIONES
        FROM agenda_slots s
        JOIN veterinarios v ON s.ID_VETERINARIO = v.ID_VETERINARIO
        JOIN agenda a ON s.ID_AGENDA = a.ID_AGENDA
        WHERE s.ID_VETERINARIO = ?
          AND DATE(s.FECHA_HORA_INICIO) = ?
          AND s.DISPONIBLE = 1
          AND s.ID_CITA IS NULL
          AND a.ESTADO = 'ACTIVO'
          AND v.ESTADO = 'ACTIVO'
          AND s.FECHA_HORA_INICIO > NOW()  -- No slots pasados
        ORDER BY s.FECHA_HORA_INICIO
        """;

        try (Connection conn = cx.conectar(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idVeterinario);
            ps.setDate(2, java.sql.Date.valueOf(fecha));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    AgendaSlot slot = new AgendaSlot();
                    slot.setIdSlot(rs.getInt("ID_SLOT"));
                    slot.setFechaHoraInicio(rs.getTimestamp("FECHA_HORA_INICIO").toLocalDateTime());
                    slot.setFechaHoraFin(rs.getTimestamp("FECHA_HORA_FIN").toLocalDateTime());
                    slot.setNombreVeterinario(rs.getString("nombre_veterinario"));
                    slots.add(slot);
                }
            }
        } catch (SQLException e) {
            System.err.println("❌ Error al obtener slots para cita: " + e.getMessage());
            e.printStackTrace();
        }
        return slots;
    }

    // En AgendaSlotDAO.java
    public AgendaSlot obtenerPorId(int idSlot) {
        String sql = "SELECT * FROM agenda_slots WHERE ID_SLOT = ?";
        try (Connection conn = cx.conectar(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idSlot);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearAgendaSlot(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}

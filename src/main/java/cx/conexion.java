package cx;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class conexion {

    private static final String URL = "jdbc:mysql://localhost:3306/bd_veterinaria";
    private static final String USUARIO = "root";
    private static final String PASSWORD = "17273747576777";
    private static final String DRIVER = "com.mysql.cj.jdbc.Driver";

    static {
        try {
            Class.forName(DRIVER);
        } catch (ClassNotFoundException e) {
            System.err.println("❌ No se pudo cargar el driver JDBC");
            e.printStackTrace();
        }
    }

    // ✅ Retorna una NUEVA conexión cada vez (NO estática, sin shared state)
    public Connection conectar() {
        try {
            return DriverManager.getConnection(URL, USUARIO, PASSWORD);
        } catch (SQLException e) {
            System.err.println("❌ Error al conectar: " + e.getMessage());
            return null;
        }
    }

    // ✅ Alias para compatibilidad (opcional)
    public Connection getConnection() {
        return conectar();
    }
}
package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Plantilla de conexión para SQL Server.
 * No es necesario tener el driver en compile-time; el README explica cómo añadirlo en NetBeans.
 */
public class Conexion {
    // Ejemplo de URL: jdbc:sqlserver://localhost:1433;databaseName=BD_TAREAS;encrypt=false;
    private static final String URL = "jdbc:sqlserver://localhost:1433;databaseName=BD_TAREAS;encrypt=false;";
    private static final String USER = "sa";
    private static final String PASS = "tu_password";

    public static Connection getConnection() throws SQLException, ClassNotFoundException {
        // Cargar driver por nombre (no obliga a depender del jar en compile)
        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        return DriverManager.getConnection(URL, USER, PASS);
    }
}

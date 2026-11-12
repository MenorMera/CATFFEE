package catffee.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionBD {
    // Parámetros de conexión local para MySQL
    private static final String URL = "jdbc:mysql://localhost:3306/catffee_db?useSSL=false&serverTimezone=UTC";
    private static final String USUARIO = "root";
    private static final String PASSWORD = ""; // Cambia esto si tu MySQL local tiene contraseña

    public static Connection obtenerConexion() {
        try {
            // Cargar explícitamente el driver de MySQL
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conexion = DriverManager.getConnection(URL, USUARIO, PASSWORD);
            System.out.println("¡Conexión exitosa a la base de datos MySQL local!");
            return conexion;
        } catch (ClassNotFoundException e) {
            System.err.println("Error: No se encontró el Driver JDBC de MySQL. " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("Error de conexión a la base de datos: " + e.getMessage());
        }
        return null;
    }
}
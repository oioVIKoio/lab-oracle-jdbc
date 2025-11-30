package ProyectBD.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


// Clase para manejar la conexión a la Base de Datos Oracle
public class ConexionDB {
    //  DATOS EXACTOS de conexión Oracle
    private static final String URL = "jdbc:oracle:thin:@localhost:1522/FREEPDB1";
    private static final String USER = "dockCICLO2";
    private static final String PASSWORD = "ORACLE";

    //Obtiene una conexión a la base de datos Oracle
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}

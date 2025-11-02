package lab_oracle_jdbc;

import java.sql.*;

public class CONEXIONORACLE {
    // Datos de conexión
    private static final String URL = "jdbc:oracle:thin:@localhost:1522/FREEPDB1";
    private static final String USER = "dockCICLO2";
    private static final String PASSWORD = "ORACLE";

    // Carga el driver
    static {
        try {
            Class.forName("oracle.jdbc.OracleDriver");
        } catch (ClassNotFoundException e) {
            System.err.println("Driver JDBC de Oracle no encontrado.");
            e.printStackTrace();
        }
    }

    // Metodo para obtener una conexión
    public static Connection obtenerConexion() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}

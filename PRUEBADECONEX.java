package lab_oracle_jdbc;
// App.java
import SEMANA10.CONEXIONORACLE;

import java.sql.*;

public class PRUEBADECONEX {
    public static void main(String[] args) {
        //PEDIMOS UNA CONEXION A LA CLASE DE CONEXION_ORACLE
        try (Connection conn = CONEXIONORACLE.obtenerConexion()) {
            // SI FUE ACEPTADO ENTONCES SE IMPRIMIRA COMO UNA CONEXION EXITOSA
            System.out.println("Conexión exitosa");
            // --- CLIENTES ---
            System.out.println("-".repeat(80));
            System.out.println("--- CLIENTES ---");
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT * FROM cliente")) {
                while (rs.next()) {
                    System.out.printf("ID: %d | Nombre: %-20s | Email: %-20s | DNI: %s%n",
                            rs.getInt("id_cliente"),
                            rs.getString("nombre"),
                            rs.getString("email"),
                            rs.getString("dni")
                    );
                }
            }

            //consultas
            System.out.println("-".repeat(103));
            System.out.println("---PRODUCTOS---");
            try (Statement sta = conn.createStatement();
                 ResultSet rs = sta.executeQuery("SELECT * FROM producto")) {
                while (rs.next()) {
                    System.out.printf("ID: %d | Nombre: %-20s | Precio: %8.2f | Categoría ID: %d%n",
                            rs.getInt("id_producto"),
                            rs.getString("nombre"),
                            rs.getDouble("precio"),
                            rs.getInt("id_categoria")
                    );
                }
            }
            System.out.println("-".repeat(103));
            System.out.println("---PEDIDO---");
            try (Statement sesta= conn.createStatement();
                 ResultSet rs = sesta.executeQuery("SELECT * FROM pedido")) {
                while (rs.next()) {
                    System.out.printf("Pedido #%d | Cliente ID: %d | Producto ID: %d | Cantidad: %d | Fecha: %s%n",
                            rs.getInt("id_pedido"),
                            rs.getInt("id_cliente"),
                            rs.getInt("id_producto"),
                            rs.getInt("cantidad"),
                            rs.getDate("fecha")
                    );
                }
            }
            System.out.println("-".repeat(103));
        } catch (SQLException e) {
            //SI FALLA ALGO ENTONCES SE CAPTURARA DENTRO DEL CATCH
            System.out.println("FALLO DE LA CONEXIÓN");
            e.printStackTrace();
        }
        //AL USAR ESTE ESTILO DE LA EXCEPCION TMB SE CIERRA LA CONEXION AUTOMATICAMENTE
    }
}
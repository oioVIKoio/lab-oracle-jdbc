package ProyectBD.servicio;

import ProyectBD.modelos.Producto;
import ProyectBD.persistencia.ConexionDB;
import ProyectBD.persistencia.ProductoDAO;
import ProyectBD.persistencia.ProductoDAOImpl;
import ProyectBD.persistencia.CategoriaDAO;
import ProyectBD.persistencia.CategoriaDAOImpl;
import java.sql.*;
import java.util.*;

public class ProdCateService {
    private final ProductoDAO productoDAO; // reutilizamos de ProductoDAOImpl
    private final CategoriaDAO categoriaDAO; // reutilizamos de CategoriaDAOImpl

    public ProdCateService() {
        this.productoDAO = new ProductoDAOImpl();
        this.categoriaDAO = new CategoriaDAOImpl();
    }

    //  INSERT con validaciones
    public void insertarProductoConValidacion(Producto producto) {
        // reutilizamoss CategoriaDAO.existePorID
        if (!categoriaDAO.existePorId(producto.getIdCategoria())) {
            throw new IllegalStateException("Error: La categoría no existe");
        }
        // reutilizamoss de CategoriaDAO el metodo existePorNombre

        if (productoDAO.existePorNombre(producto.getNombre())) {
            throw new IllegalStateException("Error: Ya existe un producto con ese nombre");
        }

        productoDAO.insertar(producto);
    }

    // UPDATE con validaciones
    public void actualizarProductoConValidacion(Producto producto) {
        if (!productoDAO.existePorId(producto.getIdProducto())) {
            throw new IllegalStateException("Error: El producto no existe");
        }

        if (!categoriaDAO.existePorId(producto.getIdCategoria())) {
            throw new IllegalStateException("Error: La categoría no existe");
        }

        productoDAO.actualizar(producto);
    }

    // DELETE con validación
    public void eliminarProductoConValidacion(int idProducto) {
        if (!productoDAO.existePorId(idProducto)) {
            throw new IllegalStateException("Error: El producto no existe");
        }

        productoDAO.eliminar(idProducto);
    }

    public void transaccionActualizarProducto(Producto producto) {
        Connection conn = null;
        try {
            conn = ConexionDB.getConnection();
            conn.setAutoCommit(false); // Iniciar transacción

            // Operación 1: Actualizar producto
            String sqlUpdate = "UPDATE producto SET nombre = ?, precio = ? WHERE id_producto = ?";
            try (PreparedStatement ps = conn.prepareStatement(sqlUpdate)) {
                ps.setString(1, producto.getNombre());
                ps.setDouble(2, producto.getPrecio());
                ps.setInt(3, producto.getIdProducto());
                ps.executeUpdate();
            }

            // Operación 2: Insertar registro en tabla pedido (si existe)
            // O hacer un SELECT de verificación
            String sqlVerificar = "SELECT COUNT(*) FROM producto WHERE id_categoria = ?";
            try (PreparedStatement ps = conn.prepareStatement(sqlVerificar)) {
                ps.setInt(1, producto.getIdCategoria());
                ResultSet rs = ps.executeQuery();
                // Solo ejecutamos para demostrar la transacción, no usamos el resultado
            }

            conn.commit(); // Confirmar
            System.out.println("Transaccion exitosa");

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback(); // Revertir
                    System.out.println("Transaccion fallida - Rollback");
                } catch (SQLException ex) {}
            }
            throw new RuntimeException("Error en transaccion: " + e.getMessage(), e);
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {}
            }
        }
    }
    public void probarTransaccionSimple() {
        List<Producto> productos = productoDAO.obtenerTodos();
        if (!productos.isEmpty()) {
            Producto producto = productos.get(0);
            transaccionActualizarProducto(producto);
            System.out.println("Transaccion probada con producto: " + producto.getNombre());
        } else {
            throw new IllegalStateException("No hay productos para probar la transaccion");
        }
    }
    public List<Producto> obtenerTodos() {
        return productoDAO.obtenerTodos();
    }

    public List<Producto> obtenerProductosConCategoria() {
        return productoDAO.obtenerProductosConCategoria();
    }

    public List<Producto> obtenerProductosPrecioMayorPromedio() {
        return productoDAO.obtenerProductosPrecioMayorPromedio();
    }

    public int contarProductos() {
        return productoDAO.contarProductos();
    }

    public double obtenerPrecioPromedio() {
        return productoDAO.obtenerPrecioPromedio();
    }
}

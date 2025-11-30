package ProyectBD.servicio;

import ProyectBD.dto.ProductoDTO;
import ProyectBD.modelos.Categoria;
import ProyectBD.modelos.Producto;
import ProyectBD.persistencia.daos.CategoriaDAO;
import ProyectBD.persistencia.daos.ProductoDAO;
import ProyectBD.persistencia.impl.CategoriaDAOImpl;
import ProyectBD.persistencia.impl.ProductoDAOImpl;
import ProyectBD.util.ConexionDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class ProdCateService {
    private final ProductoDAO productoDAO = new ProductoDAOImpl();
    private final CategoriaDAO categoriaDAO = new CategoriaDAOImpl();

    // ---------- CATEGORÍAS ----------
    public void insertarCategoria(Categoria c) {
        categoriaDAO.insertar(c);
    }

    public List<Categoria> obtenerCategorias() {
        return categoriaDAO.obtenerTodos();
    }

    // ---------- PRODUCTOS ----------
    public void insertarProductoConValidacion(Producto p) {
        if (!categoriaDAO.existePorId(p.getIdCategoria()))
            throw new IllegalStateException("La categoría no existe");
        if (productoDAO.existePorNombre(p.getNombre()))
            throw new IllegalStateException("Producto duplicado");
        productoDAO.insertar(p);
    }

    public List<Producto> obtenerTodos() {
        return productoDAO.obtenerTodos();
    }

    // ---------- CONSULTAS AVANZADAS ----------
    public List<ProductoDTO> obtenerVistaProductosCategoria() {
        return productoDAO.obtenerProductosConCategoria();
    }

    public List<Producto> obtenerProductosPrecioMayorPromedio() {
        return productoDAO.obtenerProductosPrecioMayorPromedio();
    }

    public double obtenerPrecioPromedio() {
        return productoDAO.obtenerPrecioPromedio();
    }

    public int contarProductos() {
        return productoDAO.contarProductos();
    }

    public void aplicarAumentoCategoria(int idCat, double porcentaje) {
        productoDAO.aplicarAumentoCategoria(idCat, porcentaje);
    }

    // ---------- TRANSACCIÓN
    public void transaccionActualizarProducto(Producto p) {
        Connection conn = null;
        try {
            conn = ConexionDB.getConnection();
            conn.setAutoCommit(false);

            String sql = "UPDATE producto SET nombre = ?, precio = ? WHERE id_producto = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, p.getNombre());
                ps.setDouble(2, p.getPrecio());
                ps.setInt(3, p.getIdProducto());
                ps.executeUpdate();
            }
            conn.commit();
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback(); // ← ¡esto es lo clave!
                } catch (SQLException ignored) {}
            }
            throw new RuntimeException("Error en transacción", e);
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException ignored) {}
            }
        }
    }
}
package ProyectBD.persistencia.impl;

import ProyectBD.modelos.Producto;
import ProyectBD.dto.ProductoDTO;
import ProyectBD.util.ConexionDB;
import ProyectBD.persistencia.daos.ProductoDAO;

import java.util.*;
import java.sql.*;

public class ProductoDAOImpl implements ProductoDAO {
    private static final String TABLA = "producto";
    private static final String PK = "id_producto";

    @Override
    public void insertar(Producto p) {
        if (existePorNombre(p.getNombre()))
            throw new IllegalStateException("Producto duplicado");
        String sql = "INSERT INTO " + TABLA + " (nombre, precio, id_categoria) VALUES (?,?,?)";
        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, new String[]{PK})) {
            ps.setString(1, p.getNombre());
            ps.setDouble(2, p.getPrecio());
            ps.setInt(3, p.getIdCategoria());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) p.setIdProducto(rs.getInt(1));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al insertar producto", e);
        }
    }

    @Override
    public void actualizar(Producto producto) {
        if (!existePorId(producto.getIdProducto())) {
            throw new IllegalStateException("Producto no existe ID: " + producto.getIdProducto());
        }
        String sql = "UPDATE " + TABLA + " SET nombre = ?, precio = ?, id_categoria = ? WHERE " + PK + " = ?";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, producto.getNombre());
            ps.setDouble(2, producto.getPrecio());
            ps.setInt(3, producto.getIdCategoria());
            ps.setInt(4, producto.getIdProducto());
            if (ps.executeUpdate() == 0) throw new SQLException("Actualización fallida");
        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar: " + e.getMessage(), e);
        }
    }

    @Override
    public void eliminar(int idProducto) {
        if (!existePorId(idProducto)) {
            throw new IllegalStateException("Producto no existe ID: " + idProducto);
        }
        String sql = "DELETE FROM " + TABLA + " WHERE " + PK + " = ?";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idProducto);
            if (ps.executeUpdate() == 0) throw new SQLException("Eliminación fallida");
        } catch (SQLException e) {
            throw new RuntimeException("Error al eliminar: " + e.getMessage(), e);
        }
    }

    @Override
    public Producto obtenerPorId(int idProducto) {
        String sql = "SELECT * FROM " + TABLA + " WHERE " + PK + " = ?";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idProducto);
            ResultSet rs = ps.executeQuery();
            return rs.next() ? mapear(rs) : null;
        } catch (SQLException e) {
            throw new RuntimeException("Error al obtener por ID: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Producto> obtenerTodos() {
        String sql = "SELECT * FROM " + TABLA + " ORDER BY nombre";
        try (Connection con = ConexionDB.getConnection();
             Statement s = con.createStatement();
             ResultSet rs = s.executeQuery(sql)) {
            List<Producto> lista = new ArrayList<>();
            while (rs.next()) lista.add(mapear(rs));
            return lista;
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar productos", e);
        }
    }

    @Override
    public boolean existePorNombre(String nombre) {
        String sql = "SELECT COUNT(*) FROM " + TABLA + " WHERE UPPER(nombre) = UPPER(?)";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nombre);
            ResultSet rs = ps.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error en validación nombre: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean existePorId(int idProducto) {
        String sql = "SELECT COUNT(*) FROM " + TABLA + " WHERE " + PK + " = ?";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idProducto);
            ResultSet rs = ps.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error en validación ID: " + e.getMessage(), e);
        }
    }

    // CONSULTAS AVANZADAS
    @Override
    public List<Producto> obtenerProductosPrecioMayorPromedio() {
        String sql = "SELECT * FROM " + TABLA + " WHERE precio >= (SELECT AVG(precio) FROM " + TABLA + ")";
        try (Connection conn = ConexionDB.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            List<Producto> productos = new ArrayList<>();
            while (rs.next()) productos.add(mapear(rs));
            return productos;
        } catch (SQLException e) {
            throw new RuntimeException("Error en subconsulta: " + e.getMessage(), e);
        }
    }

    @Override
    public double obtenerPrecioPromedio() {
        String sql = "SELECT AVG(precio) AS promedio FROM " + TABLA;
        try (Connection conn = ConexionDB.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            return rs.next() ? rs.getDouble("promedio") : 0.0;
        } catch (SQLException e) {
            throw new RuntimeException("Error en promedio: " + e.getMessage(), e);
        }
    }

    @Override
    public int contarProductos() {
        String sql = "SELECT COUNT(*) AS total FROM " + TABLA;
        try (Connection conn = ConexionDB.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            return rs.next() ? rs.getInt("total") : 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error en conteo: " + e.getMessage(), e);
        }
    }

    @Override
    public List<ProductoDTO> obtenerProductosConCategoria() {
        String sql = "SELECT p.id_producto, p.nombre, p.precio, c.nombre AS nombre_categoria " +
                "FROM producto p JOIN categoria c ON p.id_categoria = c.id_categoria";
        try (Connection con = ConexionDB.getConnection();
             Statement s = con.createStatement();
             ResultSet rs = s.executeQuery(sql)) {
            List<ProductoDTO> lista = new ArrayList<>();
            while (rs.next()) {
                lista.add(new ProductoDTO(
                        rs.getInt("id_producto"),
                        rs.getString("nombre"),
                        rs.getDouble("precio"),
                        rs.getString("nombre_categoria")
                ));
            }
            return lista;
        } catch (SQLException e) {
            throw new RuntimeException("Error en vista", e);
        }
    }

    @Override
    public void aplicarAumentoCategoria(int idCat, double porcentaje) {
        String sql = "{ CALL sp_aumenta_precio_cat(?, ?) }";
        try (Connection con = ConexionDB.getConnection();
             CallableStatement cs = con.prepareCall(sql)) {
            cs.setInt(1, idCat);
            cs.setDouble(2, porcentaje);
            cs.execute();
        } catch (SQLException e) {
            throw new RuntimeException("Error al aplicar aumento", e);
        }
    }

    /* ---------- metodo reutilizable ---------- */
    private Producto mapear(ResultSet rs) throws SQLException {
        return new Producto(
                rs.getInt("id_producto"),
                rs.getString("nombre"),
                rs.getDouble("precio"),
                rs.getInt("id_categoria")
        );
    }
}
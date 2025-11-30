package ProyectBD.persistencia;
import ProyectBD.modelos.Producto;

import java.util.*;
import java.sql.*;
public class ProductoDAOImpl implements ProductoDAO {
    private static final String TABLA = "producto";
    private static final String PK = "id_producto";

    // CRUD
    @Override
    public void insertar(Producto producto) {
        if (existePorNombre(producto.getNombre())) {
            throw new IllegalStateException("Producto ya existe: " + producto.getNombre());
        }

        String sql = String.format("INSERT INTO %s (nombre, precio, id_categoria) VALUES (?, ?, ?)", TABLA);
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, new String[]{PK})) {

            ps.setString(1, producto.getNombre());
            ps.setDouble(2, producto.getPrecio());
            ps.setInt(3, producto.getIdCategoria());

            int filas = ps.executeUpdate();
            if (filas == 0) throw new SQLException("Inserción fallida");

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) producto.setIdProducto(rs.getInt(1));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al insertar: " + e.getMessage(), e);
        }
    }

    @Override
    public void actualizar(Producto producto) {
        if (!existePorId(producto.getIdProducto())) {
            throw new IllegalStateException("Producto no existe ID: " + producto.getIdProducto());
        }

        String sql = String.format("UPDATE %s SET nombre = ?, precio = ?, id_categoria = ? WHERE %s = ?", TABLA, PK);
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, producto.getNombre());
            ps.setDouble(2, producto.getPrecio());
            ps.setInt(3, producto.getIdCategoria());
            ps.setInt(4, producto.getIdProducto());

            int filas = ps.executeUpdate();
            if (filas == 0) throw new SQLException("Actualización fallida");
        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar: " + e.getMessage(), e);
        }
    }

    @Override
    public void eliminar(int idProducto) {
        if (!existePorId(idProducto)) {
            throw new IllegalStateException("Producto no existe ID: " + idProducto);
        }

        String sql = String.format("DELETE FROM %s WHERE %s = ?", TABLA, PK);
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idProducto);
            int filas = ps.executeUpdate();
            if (filas == 0) throw new SQLException("Eliminación fallida");
        } catch (SQLException e) {
            throw new RuntimeException("Error al eliminar: " + e.getMessage(), e);
        }
    }

    @Override
    public Producto obtenerPorId(int idProducto) {
        String sql = String.format("SELECT * FROM %s WHERE %s = ?", TABLA, PK);
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idProducto);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return mapearProducto(rs);
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException("Error al obtener por ID: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Producto> obtenerTodos() {
        List<Producto> productos = new ArrayList<>();
        String sql = String.format("SELECT * FROM %s", TABLA);

        try (Connection conn = ConexionDB.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                productos.add(mapearProducto(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al obtener todos: " + e.getMessage(), e);
        }
        return productos;
    }

    //  VALIDACIONES
    @Override
    public boolean existePorNombre(String nombre) {
        String sql = String.format("SELECT COUNT(*) FROM %s WHERE UPPER(nombre) = UPPER(?)", TABLA);
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
        String sql = String.format("SELECT COUNT(*) FROM %s WHERE %s = ?", TABLA, PK);
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
    public List<Producto> obtenerProductosConCategoria() {
        List<Producto> productos = new ArrayList<>();
        String sql = "SELECT p.* FROM producto p JOIN categoria c ON p.id_categoria = c.id_categoria";

        try (Connection conn = ConexionDB.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                productos.add(mapearProducto(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error en JOIN: " + e.getMessage(), e);
        }
        return productos;
    }

    @Override
    public List<Producto> obtenerProductosPrecioMayorPromedio() {
        List<Producto> productos = new ArrayList<>();
        String sql = "SELECT * FROM producto WHERE precio >= (SELECT AVG(precio) FROM producto)";

        try (Connection conn = ConexionDB.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                productos.add(mapearProducto(rs));
            }
            // Mensaje informativo
            if (productos.isEmpty()) {
                System.out.println("No hay productos con precio mayor al promedio.");
                System.out.println("Esto puede ocurrir cuando hay pocos productos o todos tienen precios similares.");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error en subconsulta: " + e.getMessage(), e);
        }
        return productos;
    }

    @Override
    public double obtenerPrecioPromedio() {
        String sql = "SELECT AVG(precio) as promedio FROM producto";

        try (Connection conn = ConexionDB.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getDouble("promedio");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error en promedio: " + e.getMessage(), e);
        }
        return 0;
    }

    @Override
    public int contarProductos() {
        String sql = "SELECT COUNT(*) as total FROM producto";

        try (Connection conn = ConexionDB.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error en conteo: " + e.getMessage(), e);
        }
        return 0;
    }

    // METODO REUTILIZABLE
    private Producto mapearProducto(ResultSet rs) throws SQLException {
        return new Producto(
                rs.getInt("id_producto"),
                rs.getString("nombre"),
                rs.getDouble("precio"),
                rs.getInt("id_categoria")
        );
    }
}
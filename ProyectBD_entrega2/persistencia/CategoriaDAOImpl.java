package ProyectBD.persistencia;
import ProyectBD.modelos.Categoria;

import java.util.*;
import java.sql.*;
public class CategoriaDAOImpl implements CategoriaDAO {
    private static final String TABLA = "categoria";
    private static final String PK = "id_categoria";

    @Override
    public void insertar(Categoria categoria) {
        if (existePorNombre(categoria.getNombre())) {
            throw new IllegalStateException("Categoría ya existe: " + categoria.getNombre());
        }

        String sql = String.format("INSERT INTO %s (nombre) VALUES (?)", TABLA);
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, new String[]{PK})) {

            ps.setString(1, categoria.getNombre());
            int filas = ps.executeUpdate();
            if (filas == 0) throw new SQLException("Inserción fallida");

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) categoria.setIdCategoria(rs.getInt(1));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al insertar: " + e.getMessage(), e);
        }
    }

    @Override
    public void actualizar(Categoria categoria) {
        if (!existePorId(categoria.getIdCategoria())) {
            throw new IllegalStateException("Categoría no existe ID: " + categoria.getIdCategoria());
        }

        String sql = String.format("UPDATE %s SET nombre = ? WHERE %s = ?", TABLA, PK);
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, categoria.getNombre());
            ps.setInt(2, categoria.getIdCategoria());

            int filas = ps.executeUpdate();
            if (filas == 0) throw new SQLException("Actualización fallida");
        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar: " + e.getMessage(), e);
        }
    }

    @Override
    public void eliminar(int idCategoria) {
        if (!existePorId(idCategoria)) {
            throw new IllegalStateException("Categoría no existe ID: " + idCategoria);
        }
        if (tieneProductos(idCategoria)) {
            throw new IllegalStateException("No se puede eliminar categoría con productos asociados");
        }
        String sql = String.format("DELETE FROM %s WHERE %s = ?", TABLA, PK);
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idCategoria);
            int filas = ps.executeUpdate();
            if (filas == 0) throw new SQLException("Eliminación fallida");
        } catch (SQLException e) {
            throw new RuntimeException("Error al eliminar: " + e.getMessage(), e);
        }
    }

    @Override
    public Categoria obtenerPorId(int idCategoria) {
        String sql = String.format("SELECT * FROM %s WHERE %s = ?", TABLA, PK);
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idCategoria);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new Categoria(
                        rs.getInt("id_categoria"),
                        rs.getString("nombre")
                );
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException("Error al obtener por ID: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Categoria> obtenerTodos() {
        List<Categoria> categorias = new ArrayList<>();
        String sql = String.format("SELECT * FROM %s", TABLA);

        try (Connection conn = ConexionDB.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                categorias.add(new Categoria(
                        rs.getInt("id_categoria"),
                        rs.getString("nombre")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al obtener todos: " + e.getMessage(), e);
        }
        return categorias;
    }

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

    // Metodo privado auxiliar
    public boolean existePorId(int idCategoria) {
        String sql = String.format("SELECT COUNT(*) FROM %s WHERE %s = ?", TABLA, PK);
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idCategoria);
            ResultSet rs = ps.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error en validación ID: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean tieneProductos(int idCategoria) {
        String sql = "SELECT COUNT(*) FROM producto WHERE id_categoria = ?";
        try (Connection conn = ConexionDB.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idCategoria);
            ResultSet rs = ps.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error validando productos: " + e.getMessage(), e);
        }
    }
}
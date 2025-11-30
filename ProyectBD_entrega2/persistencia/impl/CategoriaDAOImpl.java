package ProyectBD.persistencia.impl;

import ProyectBD.modelos.Categoria;
import ProyectBD.util.ConexionDB;
import ProyectBD.persistencia.daos.CategoriaDAO;

import java.sql.*;
import java.util.*;

public class CategoriaDAOImpl implements CategoriaDAO {
    private static final String TABLA = "categoria";
    private static final String PK   = "id_categoria";

    @Override public void insertar(Categoria c) {
        if (existePorNombre(c.getNombre()))
            throw new IllegalStateException("Categoría ya existe");
        String sql = "INSERT INTO " + TABLA + " (nombre) VALUES (?)";
        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, new String[]{PK})) {
            ps.setString(1, c.getNombre());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) c.setIdCategoria(rs.getInt(1));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al insertar categoría", e);
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

    @Override public List<Categoria> obtenerTodos() {
        String sql = "SELECT * FROM " + TABLA + " ORDER BY nombre";
        try (Connection con = ConexionDB.getConnection();
             Statement s = con.createStatement();
             ResultSet rs = s.executeQuery(sql)) {
            List<Categoria> lista = new ArrayList<>();
            while (rs.next()) lista.add(mapear(rs));
            return lista;
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar categorías", e);
        }
    }

    @Override public boolean existePorNombre(String nombre) {
        return contar("WHERE UPPER(nombre) = UPPER(?)", nombre) > 0;
    }

    @Override public boolean existePorId(int id) {
        return contar("WHERE " + PK + " = ?", id) > 0;
    }
    @Override
    public boolean tieneProductos(int id) {
        String sql = "SELECT COUNT(*) FROM producto WHERE id_categoria = ?";
        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error validando productos", e);
        }
    }

    /* privados */
    private Categoria mapear(ResultSet rs) throws SQLException {
        return new Categoria(rs.getInt(1), rs.getString(2));
    }
    private int contar(String where, Object param) {
        String sql = "SELECT COUNT(*) FROM " + TABLA + " " + where;
        try (Connection con = ConexionDB.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setObject(1, param);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error en validación", e);
        }
    }
}
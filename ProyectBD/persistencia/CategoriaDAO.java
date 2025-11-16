package ProyectBD.persistencia;
import ProyectBD.modelos.Categoria;

import java.util.*;
public interface CategoriaDAO {
    //CRUD
    void insertar(Categoria categoria);
    void actualizar(Categoria categoria);
    void eliminar(int idCategoria);
    Categoria obtenerPorId(int idCategoria);
    boolean existePorId(int idCategoria);  //
    List<Categoria> obtenerTodos();

    // VALIDACIONES ESPECÍFICAS
    boolean existePorNombre(String nombre);
    boolean tieneProductos(int idCategoria); // evitar eliminar categorías con productos
}
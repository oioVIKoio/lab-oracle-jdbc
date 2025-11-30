package ProyectBD.persistencia;
import ProyectBD.modelos.Producto;

import java.util.*;

public interface ProductoDAO {
    // CRUD básico
    void insertar(Producto producto);
    void actualizar(Producto producto);
    void eliminar(int idProducto);
    Producto obtenerPorId(int idProducto);
    List<Producto> obtenerTodos();

    // VALIDACIONES
    boolean existePorNombre(String nombre);
    boolean existePorId(int idProducto);

    // CONSULTAS AVANZADAS
    List<Producto> obtenerProductosConCategoria(); // JOIN - devuelve productos normales
    List<Producto> obtenerProductosPrecioMayorPromedio(); // SUBCONSULTA
    double obtenerPrecioPromedio(); // FUNCIÓN AGREGADA
    int contarProductos(); // FUNCIÓN AGREGADA
}
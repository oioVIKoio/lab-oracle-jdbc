package ProyectBD.persistencia.daos;

import ProyectBD.modelos.Producto;
import ProyectBD.dto.ProductoDTO;
import java.util.List;

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
    List<Producto> obtenerProductosPrecioMayorPromedio();
    double obtenerPrecioPromedio();
    int contarProductos();
    List<ProductoDTO> obtenerProductosConCategoria(); //


    // nuevo
    void aplicarAumentoCategoria(int idCategoria, double porcentaje); //
}
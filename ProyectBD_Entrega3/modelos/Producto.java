package ProyectBD.modelos;
import ProyectBD.util.Validador;
public class Producto {
    private int idProducto;
    private String nombre;
    private double precio;
    private int idCategoria;

    public Producto() {}
    public Producto(String nombre, double precio, int idCategoria) {
        setNombre(nombre); setPrecio(precio); this.idCategoria = idCategoria;
    }
    public Producto(int idProducto, String nombre, double precio, int idCategoria) {
        this(nombre, precio, idCategoria); this.idProducto = idProducto;
    }

    /* getters y setters */

    public int getIdProducto() {
        return idProducto; }
    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto; }
    public String getNombre() {
        return nombre; }

    public void setNombre(String nombre) {
        Validador.nombre("Nombre producto", nombre); this.nombre = nombre.trim(); }

    public double getPrecio() {
        return precio; }
    public void setPrecio(double precio) {
        Validador.positivo("Precio", precio); this.precio = precio; }

    public int getIdCategoria() {
        return idCategoria; }
    public void setIdCategoria(int idCategoria) {
        Validador.positivo("ID categoría", idCategoria); this.idCategoria = idCategoria; }

    @Override public String toString() {
        return String.format("Producto[id=%d, nombre='%s', precio=%.2f, categoria=%d]",
                idProducto, nombre, precio, idCategoria);
    }
}
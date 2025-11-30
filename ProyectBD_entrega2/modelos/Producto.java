package ProyectBD.modelos;

//Mapea con la tabla PRODUCTO de la base de datos.
public class Producto {
    private int idProducto;
    private String nombre;
    private double precio;
    private int idCategoria;
//Constructor vacio para frameworks
    public Producto() {}

    //Constructor para insertar nuevos productos (sin ID).
    public Producto(String nombre, double precio, int idCategoria) {
        setNombre(nombre);
        setPrecio(precio);
        this.idCategoria = idCategoria;
    }

     //Constructor para cargar productos existentes (con ID).
  // idProducto ID generado por la base de datos
    public Producto(int idProducto, String nombre, double precio, int idCategoria) {
        this.idProducto = idProducto;
        setNombre(nombre);
        setPrecio(precio);
        this.idCategoria = idCategoria;
    }

   //Establece el nombre del producto con validacion.
    //IllegalArgumentException si el nombre es nulo o vacio
    public void setNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("Error: El nombre no puede estar vacio");
        }
        this.nombre = nombre.trim();
    }

   //Establece el precio del producto con validacion.
    //precio Precio del producto
   // IllegalArgumentException si el precio es menor o igual a 0

    public void setPrecio(double precio) {
        if (precio <= 0) {
            throw new IllegalArgumentException("Error: El precio debe ser mayor a 0");
        }
        this.precio = precio;
    }

    // Getters
    public int getIdProducto() {
        return idProducto;
    }

    public String getNombre() {
        return nombre;
    }

    public double getPrecio() {
        return precio;
    }

    public int getIdCategoria() {
        return idCategoria;
    }

    // Setters
    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

    public void setIdCategoria(int idCategoria) {
        if(idCategoria <=0) {
            throw new IllegalArgumentException("ID de categoría debe ser mayor a 0...");
        }
        this.idCategoria = idCategoria;
    }

  //Representacion en texto del producto.
    @Override
    public String toString() {
        return String.format("Producto[id=%d, nombre='%s', precio=%.2f, categoria_id=%d]",
                idProducto, nombre, precio, idCategoria);
    }
}
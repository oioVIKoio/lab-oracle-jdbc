package ProyectBD.modelos;

public class Categoria {
    private int idCategoria;
    private String nombre;

    /*
     * Constructor vacio para frameworks
     */
    public Categoria() {}

     // Constructor para insertar nuevas categorias (sin ID).
    public Categoria(String nombre) {
        setNombre(nombre);
    }

    //Constructor para cargar categorias existentes (con ID)
    public Categoria(int idCategoria, String nombre) {
        this.idCategoria = idCategoria;
        setNombre(nombre);
    }

 //Establece el nombre de la categoria con validacion.
    public void setNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("Error: El nombre no puede estar vacio");
        }
        this.nombre = nombre.trim();
    }

    // Getters y setters
    public int getIdCategoria() {
        return idCategoria;
    }

    public String getNombre() {
        return nombre;
    }

    public void setIdCategoria(int idCategoria) {
        this.idCategoria = idCategoria;
    }

   //Representacion en texto de la categoria.

    @Override
    public String toString() {
        return String.format("Categoria[id=%d, nombre='%s']", idCategoria, nombre);
    }
}

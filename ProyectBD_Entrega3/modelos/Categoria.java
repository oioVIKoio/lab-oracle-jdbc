package ProyectBD.modelos;
import ProyectBD.util.Validador;
public class Categoria {
    private int idCategoria;
    private String nombre;

    public Categoria() {}
    public Categoria(String nombre) { setNombre(nombre); }
    public Categoria(int idCategoria, String nombre) {
        this.idCategoria = idCategoria; setNombre(nombre);
    }

    /* getters y setters */
    public int getIdCategoria() {
        return idCategoria; }
    public void setIdCategoria(int idCategoria) {
        this.idCategoria = idCategoria; }
    public String getNombre() {
        return nombre; }
    public void setNombre(String nombre) {
        Validador.nombre("Nombre categoría", nombre);
        this.nombre = nombre.trim();
    }
    @Override public String toString() {
        return String.format("Categoria[id=%d, nombre='%s']", idCategoria, nombre);
    }
}
package ProyectBD.Main;


import ProyectBD.modelos.Categoria;
import ProyectBD.modelos.Producto;
import ProyectBD.servicio.ProdCateService;
import ProyectBD.persistencia.CategoriaDAO;
import ProyectBD.persistencia.CategoriaDAOImpl;
import java.util.Scanner;
import java.util.List;

public class MainDAO {
    private static final Scanner scanner = new Scanner(System.in);
    private static final ProdCateService service = new ProdCateService();
    private static final CategoriaDAO categoriaDAO = new CategoriaDAOImpl();

    public static void main(String[] args) {
        System.out.println("--- Tienda ---");

        try {
            while (true) {
                System.out.println("--- MENU PRINCIPAL ---");
                System.out.println("1. Gestionar Categorias");
                System.out.println("2. Gestionar Productos");
                System.out.println("3. Consultas Avanzadas");
                System.out.println("4. Salir");
                System.out.print("Seleccione opcion: ");

                String input = scanner.nextLine();

                try {
                    int opcion = Integer.parseInt(input);

                    switch (opcion) {
                        case 1 -> gestionarCategorias();
                        case 2 -> gestionarProductos();
                        case 3 -> consultasAvanzadas();
                        case 4 -> {
                            System.out.println("Saliendo del sistema...");
                            return;
                        }
                        default -> System.out.println("Opcion invalida");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Error: Ingrese un numero valido");
                }
            }

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void gestionarCategorias() {
        try {
            System.out.println("--- GESTIONAR CATEGORIAS ---");
            System.out.println("1. Insertar Categoria");
            System.out.println("2. Listar Categorias");
            System.out.print("Seleccione: ");

            int opcion = Integer.parseInt(scanner.nextLine());

            switch (opcion) {
                case 1 -> {
                    System.out.print("Nombre de la categoria: ");
                    String nombre = scanner.nextLine();
                    Categoria categoria = new Categoria(nombre);
                    categoriaDAO.insertar(categoria);
                    System.out.println("Categoria insertada. ID: " + categoria.getIdCategoria());
                }
                case 2 -> {
                    List<Categoria> categorias = categoriaDAO.obtenerTodos();
                    for (Categoria cat : categorias) {
                        System.out.println(cat);
                    }
                }
                default -> System.out.println("Opcion invalida");
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void gestionarProductos() {
        try {
            System.out.println("--- GESTIONAR PRODUCTOS ---");
            System.out.println("1. Insertar Producto");
            System.out.println("2. Listar Productos");
            System.out.print("Seleccione: ");

            int opcion = Integer.parseInt(scanner.nextLine());

            switch (opcion) {
                case 1 -> insertarProducto();
                case 2 -> listarProductos();
                default -> System.out.println("Opcion invalida");
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void insertarProducto() {
        try {
            System.out.println("--- INSERTAR PRODUCTO ---");

            List<Categoria> categorias = categoriaDAO.obtenerTodos();
            System.out.println("Categorias disponibles:");
            for (Categoria cat : categorias) {
                System.out.println("ID: " + cat.getIdCategoria() + " - " + cat.getNombre());
            }

            System.out.print("Nombre del producto: ");
            String nombre = scanner.nextLine();

            System.out.print("Precio: ");
            double precio = Double.parseDouble(scanner.nextLine());

            System.out.print("ID Categoria: ");
            int idCategoria = Integer.parseInt(scanner.nextLine());

            Producto producto = new Producto(nombre, precio, idCategoria);
            service.insertarProductoConValidacion(producto);

            System.out.println("Producto insertado correctamente. ID: " + producto.getIdProducto());

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void listarProductos() {
        try {
            System.out.println("--- LISTA DE PRODUCTOS ---");
            List<Producto> productos = service.obtenerTodos();
            for (Producto p : productos) {
                System.out.println(p);
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void consultasAvanzadas() {
        try {
            System.out.println("--- CONSULTAS AVANZADAS ---");
            System.out.println("1. Productos con categoria (JOIN)");
            System.out.println("2. Productos precio mayor al promedio");
            System.out.println("3. Estadisticas generales");
            System.out.println("4. Probar Transaccion");
            System.out.print("Seleccione: ");

            int subopcion = Integer.parseInt(scanner.nextLine());

            switch (subopcion) {
                case 1 -> {
                    List<Producto> productos = service.obtenerProductosConCategoria();
                    for (Producto p : productos) {
                        System.out.println(p);
                    }
                }
                case 2 -> {
                    List<Producto> productos = service.obtenerProductosPrecioMayorPromedio();
                    for (Producto p : productos) {
                        System.out.println(p);
                    }
                }
                case 3 -> {
                    System.out.println("Total productos: " + service.contarProductos());
                    System.out.println("Precio promedio: " + service.obtenerPrecioPromedio());
                }
                case 4 -> service.probarTransaccionSimple();

                default -> System.out.println("Opcion invalida");
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
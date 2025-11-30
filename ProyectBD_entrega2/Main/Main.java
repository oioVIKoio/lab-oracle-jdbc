package ProyectBD.main;

import ProyectBD.dto.ProductoDTO;
import ProyectBD.modelos.*;
import ProyectBD.servicio.ProdCateService;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static final Scanner sc = new Scanner(System.in);
    private static final ProdCateService servicio = new ProdCateService();

    public static void main(String[] args) {
        System.out.println("--- Tienda - Entrega 3 ---");
        boolean salir = false;
        while (!salir) {
            mostrarOpciones();
            try {
                switch (Integer.parseInt(sc.nextLine())) {
                    case 1 -> gestionarCategorias();
                    case 2 -> gestionarProductos();
                    case 3 -> consultasAvanzadas();
                    case 4 -> salir = true;
                    default -> System.out.println("Opcion invalida");
                }
            } catch (NumberFormatException e) {
                System.out.println("Ingrese un numero valido");
            } catch (Exception ex) {
                System.out.println("Error: " + ex.getMessage());
            }
        }
        System.out.println("Fin del programa");
    }

    private static void mostrarOpciones() {
        System.out.println("""
                1. Gestionar Categorias
                2. Gestionar Productos
                3. Consultas Avanzadas
                4. Salir
                Seleccione:""");
    }

    /* ---------- CATEGORIAS ---------- */
    private static void gestionarCategorias() {
        System.out.println("1. Insertar  2. Listar");
        switch (sc.nextLine()) {
            case "1" -> {
                System.out.print("Nombre: ");
                String nombre = sc.nextLine();
                try {
                    Categoria c = new Categoria(nombre);
                    servicio.insertarCategoria(c);
                    System.out.println("Categoria insertada ID=" + c.getIdCategoria());
                } catch (Exception e) {
                    System.out.println("Error: " + e.getMessage());
                }
            }
            case "2" -> {
                List<Categoria> categorias = servicio.obtenerCategorias();
                if (categorias.isEmpty()) {
                    System.out.println("No hay categorias registradas.");
                } else {
                    categorias.forEach(System.out::println);
                }
            }
            default -> System.out.println("Opcion invalida");
        }
    }

    /* ---------- PRODUCTOS ---------- */
    private static void gestionarProductos() {
        System.out.println("1. Insertar  2. Listar");
        switch (sc.nextLine()) {
            case "1" -> insertarProducto();
            case "2" -> {
                List<Producto> productos = servicio.obtenerTodos();
                if (productos.isEmpty()) {
                    System.out.println("No hay productos registrados.");
                } else {
                    productos.forEach(System.out::println);
                }
            }
            default -> System.out.println("Opcion invalida");
        }
    }

    private static void insertarProducto() {
        List<Categoria> cats = servicio.obtenerCategorias();
        if (cats.isEmpty()) {
            System.out.println("No se puede crear un producto: no existen categorias.");
            return;
        }
        System.out.println("Categorias disponibles:");
        cats.forEach(c -> System.out.println(c.getIdCategoria() + ") " + c.getNombre()));
        System.out.print("Elija categoria: ");
        int idCat;
        try {
            idCat = Integer.parseInt(sc.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("ID de categoria invalido.");
            return;
        }

        System.out.print("Nombre producto: ");
        String nom = sc.nextLine();
        if (nom.isBlank()) {
            System.out.println("El nombre del producto no puede estar vacio.");
            return;
        }

        System.out.print("Precio: ");
        double pre;
        try {
            pre = Double.parseDouble(sc.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Precio invalido.");
            return;
        }

        try {
            Producto p = new Producto(nom, pre, idCat);
            servicio.insertarProductoConValidacion(p);
            System.out.println("Producto insertado ID=" + p.getIdProducto());
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    /* ---------- CONSULTAS AVANZADAS ---------- */
    private static void consultasAvanzadas() {
        System.out.println("""
                1. Vista productos + categoria
                2. Productos con precio > promedio
                3. Estadisticas (count, avg)
                4. Aplicar aumento % a categoria
                5. Probar transaccion
                Seleccione:""");
        switch (sc.nextLine()) {
            case "1" -> {
                List<ProductoDTO> vista = servicio.obtenerVistaProductosCategoria();
                if (vista.isEmpty()) {
                    System.out.println("No hay productos registrados.");
                } else {
                    vista.forEach(System.out::println);
                }
            }
            case "2" -> {
                List<Producto> productos = servicio.obtenerProductosPrecioMayorPromedio();
                if (productos.isEmpty()) {
                    System.out.println("No hay productos con precio mayor al promedio.");
                } else {
                    productos.forEach(System.out::println);
                }
            }
            case "3" -> {
                int total = servicio.contarProductos();
                if (total == 0) {
                    System.out.println("No hay productos para mostrar estadisticas.");
                } else {
                    System.out.println("Total productos: " + total);
                    System.out.println("Precio promedio: " + servicio.obtenerPrecioPromedio());
                }
            }
            case "4" -> aplicarAumento();
            case "5" -> probarTransaccion();
            default -> System.out.println("Opcion invalida");
        }
    }

    private static void aplicarAumento() {
        List<Categoria> cats = servicio.obtenerCategorias();
        if (cats.isEmpty()) {
            System.out.println("No hay categorias para aplicar aumento.");
            return;
        }
        cats.forEach(c -> System.out.println(c.getIdCategoria() + ") " + c.getNombre()));
        System.out.print("Elija categoria: ");
        int idCat;
        try {
            idCat = Integer.parseInt(sc.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("ID de categoria invalido.");
            return;
        }

        System.out.print("Porcentaje a aumentar: ");
        double pct;
        try {
            pct = Double.parseDouble(sc.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Porcentaje invalido.");
            return;
        }

        try {
            servicio.aplicarAumentoCategoria(idCat, pct);
            System.out.println("Aumento aplicado");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void probarTransaccion() {
        List<Producto> lista = servicio.obtenerTodos();
        if (lista.isEmpty()) {
            System.out.println("No hay productos para probar la transaccion.");
            return;
        }
        Producto p = lista.get(0);
        p.setPrecio(p.getPrecio() + 10);
        try {
            servicio.transaccionActualizarProducto(p);
            System.out.println("Transaccion exitosa sobre " + p.getNombre());
        } catch (Exception e) {
            System.out.println("Error en la transaccion: " + e.getMessage());
        }
    }
}
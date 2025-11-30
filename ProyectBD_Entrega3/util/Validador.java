package ProyectBD.util;

public final class Validador {
    private Validador() {
    }
    public static void nombre(String campo, String valor) {
        if (valor == null || valor.isBlank())
            throw new IllegalArgumentException(campo + " no puede estar vacío");
    }

    public static void positivo(String campo, double num) {
        if (num <= 0)
            throw new IllegalArgumentException(campo + " debe ser > 0");
    }
}
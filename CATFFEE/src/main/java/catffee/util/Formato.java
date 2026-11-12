package catffee.util;

import java.text.Normalizer;

/**
 * Utilidades de texto y números compartidas por repositorios y controlador.
 */
public class Formato {

    // 500.0 -> "500"   |   2.5 -> "2.5"
    public static String numero(double n) {
        return n == Math.rint(n) ? String.valueOf((long) n) : String.valueOf(n);
    }

    // Acepta "2,5" o "2.5"
    public static double aNumero(String texto) throws NumberFormatException {
        return Double.parseDouble(texto.trim().replace(',', '.'));
    }

    // "Café" -> "cafe": sirve para buscar sin importar tildes ni mayúsculas
    public static String normalizar(String texto) {
        return Normalizer.normalize(texto, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase();
    }

    // El separador de los archivos es ';', así que no puede venir dentro de un dato
    public static String limpiar(String texto) {
        return texto.replace(";", ",").trim();
    }
}

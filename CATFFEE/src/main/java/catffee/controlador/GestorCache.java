package catffee.controlador;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class GestorCache {
    private static final String RUTA_CACHE = "cache/";

    static {
        File dir = new File(RUTA_CACHE);
        if (!dir.exists()) {
            dir.mkdir();
        }
    }

    // Guardar una línea de datos en un archivo específico (ej: insumos.txt, productos.txt)
    public static void guardarRegistro(String nombreArchivo, String datos) {
        try (FileWriter fw = new FileWriter(RUTA_CACHE + nombreArchivo, true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {
            out.println(datos);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Leer todos los registros de un archivo de caché
    public static List<String> leerRegistros(String nombreArchivo) {
        List<String> lista = new ArrayList<>();
        File archivo = new File(RUTA_CACHE + nombreArchivo);
        if (!archivo.exists()) return lista;

        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                if (!linea.trim().isEmpty()) {
                    lista.add(linea);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lista;
    }
}
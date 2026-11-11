package catffee.controlador;

import javax.swing.JTable;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class GeneradorFactura {

    public static String generarFacturaTxt(String cliente, String empleado, JTable tabla, String total) {
        File carpeta = new File("facturas");
        if (!carpeta.exists() && !carpeta.mkdirs()) {
            return null;
        }

        String marca = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        File archivo = new File(carpeta, "factura_" + marca + ".txt");

        try (PrintWriter w = new PrintWriter(archivo, "UTF-8")) {
            w.println("========== CATFFEE ==========");
            w.println("Fecha:    " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
            w.println("Cliente:  " + cliente);
            w.println("Atendió:  " + empleado);
            w.println("-----------------------------");
            for (int i = 0; i < tabla.getRowCount(); i++) {
                w.println(String.format("%-15s x%-3s %8s %9s",
                        tabla.getValueAt(i, 0), tabla.getValueAt(i, 1),
                        tabla.getValueAt(i, 2), tabla.getValueAt(i, 3)));
            }
            w.println("-----------------------------");
            w.println("TOTAL: " + total);
            w.println("¡Gracias por su compra!");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return archivo.getAbsolutePath();
    }
}
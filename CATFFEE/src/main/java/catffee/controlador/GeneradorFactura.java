package catffee.controlador;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class GeneradorFactura {

    public static String generarFacturaTxt(String cliente, String empleado, JTable tablaCarrito, String totalPagar) {
        // Generar un número de factura único basado en timestamp
        String numFactura = "FAC-" + System.currentTimeMillis() % 100000;
        String fechaHora = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String nombreArchivo = "factura_" + numFactura + ".txt";

        try (PrintWriter writer = new PrintWriter(new FileWriter(nombreArchivo))) {
            writer.println("==================================================");
            writer.println("                 CATFFEE S.A.S.                   ");
            writer.println("             NIT: 901.548.723-1                   ");
            writer.println("      Calle 46 Bis # 14-20, Cali, Colombia        ");
            writer.println("             Tel: +57 (602) 555-0199              ");
            writer.println("==================================================");
            writer.println("Factura Nro: " + numFactura);
            writer.println("Fecha: " + fechaHora);
            writer.println("Atendido por: " + empleado);
            writer.println("Cliente: " + (cliente == null || cliente.trim().isEmpty() ? "PÚBLICO GENERAL (USUARIO NULL)" : cliente));
            writer.println("==================================================");
            writer.println(String.format("%-20s %-8s %-10s %-10s", "PRODUCTO", "CANT.", "PRECIO", "SUBTOTAL"));
            writer.println("--------------------------------------------------");

            DefaultTableModel model = (DefaultTableModel) tablaCarrito.getModel();
            for (int i = 0; i < model.getRowCount(); i++) {
                String prod = model.getValueAt(i, 0).toString();
                String cant = model.getValueAt(i, 1).toString();
                String precio = model.getValueAt(i, 2).toString();
                String subtotal = model.getValueAt(i, 3).toString();

                writer.println(String.format("%-20s %-8s %-10s %-10s", 
                    prod.length() > 20 ? prod.substring(0, 17) + "..." : prod, 
                    cant, 
                    precio, 
                    subtotal));
            }

            writer.println("--------------------------------------------------");
            writer.println("TOTAL A PAGAR: " + totalPagar);
            writer.println("==================================================");
            writer.println("          ¡Gracias por su visita a CATFFEE!        ");
            writer.println("    Conserve este ticket para cualquier reclamo.  ");
            writer.println("==================================================");

            return nombreArchivo;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    static String generarFacturaTxt(String clienteStr, String empleado, DefaultTableModel modeloCarrito, String totalStr) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}
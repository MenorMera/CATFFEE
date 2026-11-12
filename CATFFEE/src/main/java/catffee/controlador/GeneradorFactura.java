package catffee.controlador;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JTable;

public class GeneradorFactura {

    public static String generarFacturaTxt(String cliente, String empleado, JTable tablaCarrito, String totalPagarStr) {
        String numFactura = "FE-" + (System.currentTimeMillis() % 100000);
        String fechaHora = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        
        // CORRECCIÓN: Carpeta independiente para las facturas físicas
        File dir = new File("facturas/");
        if (!dir.exists()) {
            dir.mkdir();
        }

        String nombreArchivo = "facturas/factura_" + numFactura.replace("-", "_") + ".txt";

        double totalPagar = 0.0;
        try {
            totalPagar = Double.parseDouble(totalPagarStr.replace("$", "").trim());
        } catch (Exception e) {
            totalPagar = 0.0;
        }

        double baseImponible = totalPagar / 1.19;
        double valorIva = totalPagar - baseImponible;
        String cufe = "a3f5b7c8d9e1f2a3b4c5d6e7f8a9b0c1d2e3f4a5";

        try (PrintWriter writer = new PrintWriter(new FileWriter(nombreArchivo))) {
            writer.println("================================================================");
            writer.println("                 FACTURA ELECTRÓNICA DE VENTA                   ");
            writer.println("                      CATFFEE S.A.S.                            ");
            writer.println("                 NIT: 901.548.723-1                             ");
            writer.println("        Dirección: Calle 46 Bis # 14-20, Cali, Colombia         ");
            writer.println("             Contacto: gerencia@catffee.com                     ");
            writer.println("================================================================");
            writer.println("Resolución DIAN No. 18764000001234 de 2025/01 al 2027/01");
            writer.println("Prefijo FE desde el número FE-1 hasta FE-50000. Activa.");
            writer.println("----------------------------------------------------------------");
            writer.println("Factura Nro     : "  + numFactura);
            writer.println("Fecha y Hora    : "  + fechaHora);
            writer.println("Medio de Pago   : EFECTIVO / TRANSFERENCIA BANCARIA");
            writer.println("Atendido por    : "  + empleado);
            writer.println("Adquiriente     : "  + (cliente == null || cliente.trim().isEmpty() ? "CONSUMO FINAL (CC: 222222222)" : cliente));
            writer.println("================================================================");
            writer.println(String.format("%-22s | %-5s | %-10s | %-10s", "PRODUCTO", "CANT.", "P. UNIT", "SUBTOTAL"));
            writer.println("----------------------------------------------------------------");

            int rows = tablaCarrito.getRowCount();
            for (int i = 0; i < rows; i++) {
                String prod = tablaCarrito.getValueAt(i, 0).toString();
                String cant = tablaCarrito.getValueAt(i, 1).toString();
                String precio = tablaCarrito.getValueAt(i, 2).toString();
                String subtotal = tablaCarrito.getValueAt(i, 3).toString();

                if (prod.length() > 22) prod = prod.substring(0, 19) + "...";
                
                writer.println(padRight(prod, 22) + " | " + padRight(cant, 5) + " | " + padRight(precio, 10) + " | " + padRight(subtotal, 10));
            }

            writer.println("----------------------------------------------------------------");
            writer.println("Base Gravable IVA (19%) : $" + String.format("%.2f", baseImponible));
            writer.println("Total IVA (19%)         : $" + String.format("%.2f", valorIva));
            writer.println("Impoconsumo (0%)        : $0.00");
            writer.println("----------------------------------------------------------------");
            writer.println("TOTAL A PAGAR           : $" + totalPagar);
            writer.println("================================================================");
            writer.println("ELEMENTOS TECNOLÓGICOS DIAN:");
            writer.println("CUFE: " + cufe);
            writer.println("----------------------------------------------------------------");
            writer.println(" [QR CODE DIAN SIMULADO] ");
            writer.println(" HTTPS://FACTURAELECTRONICA.DIAN.GOV.CO/KIMOCC/");
            writer.println("================================================================");
            writer.println("       Emitido mediante software autorizado - CATFFEE POS       ");
            writer.println("================================================================");

            return nombreArchivo;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String padRight(String s, int n) {
        return String.format("%-" + n + "s", s);
    }
}
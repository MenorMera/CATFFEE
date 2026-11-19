package catffee.controlador;

import catffee.modelo.Cliente;
import catffee.modelo.DetalleVenta;
import catffee.modelo.Ingrediente;
import catffee.modelo.Insumo;
import catffee.modelo.Producto;
import catffee.modelo.Proveedor;
import catffee.modelo.Vendedor;
import catffee.modelo.Venta;
import catffee.repositorio.InsumoRepositorio;
import catffee.repositorio.ProductoRepositorio;
import catffee.repositorio.ProveedorRepositorio;
import catffee.util.Formato;
import catffee.vista.*;
import javax.swing.AbstractButton;
import javax.swing.JOptionPane;
import java.awt.event.ActionListener;
import java.util.List;
import catffee.modelo.Administrador;
import catffee.modelo.Compra;
import catffee.repositorio.CompraRepositorio;

public class ControladorReportes {

    private static VentanaReportes ventanaReportesInstance;

    public static void abrirVentanaReportes() {
        if (ventanaReportesInstance == null || !ventanaReportesInstance.isVisible()) {
            ventanaReportesInstance = new VentanaReportes();
            inicializarVentanaReportes(ventanaReportesInstance);
        }
        ventanaReportesInstance.setVisible(true);
        ventanaReportesInstance.toFront();
        if (ControladorCatffee.ventanaPrincipalInstance != null) ControladorCatffee.ventanaPrincipalInstance.dispose();
    }

    public static void inicializarVentanaReportes(VentanaReportes v) {
        if (v.btnCargar != null) {
            v.btnCargar.addActionListener(e -> {
                v.txtReporte.setText("");
                List<String> ventas = GestorCache.leerRegistros("ventas_cache.txt");
                if (ventas.isEmpty()) {
                    v.txtReporte.setText("No hay registros de ventas en la caché todavía.");
                } else {
                    v.txtReporte.append("=== CONSOLIDADO GENERAL DE VENTAS (CATFFEE) ===\n\n");
                    for (String vent : ventas) {
                        v.txtReporte.append(vent + "\n");
                    }
                }
            });
        }

        if (v.btnCancelar != null) {
            v.btnCancelar.addActionListener(e -> {
                v.dispose();
                ControladorCatffee.abrirVentanaPrincipal(ControladorCatffee.rolActual);
            });
        }
    }

    // --- INSUMOS ---
}

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

public class ControladorProveedores {

    private static final ProveedorRepositorio proveedorRepo = new ProveedorRepositorio();
    private static VentanaProveedores ventanaProveedoresInstance;

    public static void abrirVentanaProveedores() {
        if (ventanaProveedoresInstance == null || !ventanaProveedoresInstance.isVisible()) {
            ventanaProveedoresInstance = new VentanaProveedores();
            inicializarVentanaProveedores(ventanaProveedoresInstance);
        }
        ventanaProveedoresInstance.setVisible(true);
        ventanaProveedoresInstance.toFront();
        if (ControladorCatffee.ventanaPrincipalInstance != null) ControladorCatffee.ventanaPrincipalInstance.dispose();
    }

    public static void inicializarVentanaProveedores(VentanaProveedores v) {
        cargarProveedores(v);

        if (v.btnGuardar != null) {
            v.btnGuardar.addActionListener(e -> {
                String nombre = v.txtNombre.getText().trim();
                String tel = v.txtTelefono.getText().trim();

                if (nombre.isEmpty()) {
                    JOptionPane.showMessageDialog(v, "Ingrese el nombre del proveedor.");
                    return;
                }
                if (!proveedorRepo.guardar(new Proveedor(nombre, tel))) {
                    JOptionPane.showMessageDialog(v, "Ya existe un proveedor con ese nombre.");
                    return;
                }
                cargarProveedores(v);
                JOptionPane.showMessageDialog(v, "¡Proveedor registrado!\n"
                        + "Ya puede elegirlo en la ventana Insumos para asignarle sus insumos.");
                v.txtNombre.setText("");
                v.txtTelefono.setText("");
            });
        }

        if (v.btnCancelar != null) {
            v.btnCancelar.addActionListener(e -> {
                v.dispose();
                ControladorCatffee.abrirVentanaPrincipal(ControladorCatffee.rolActual);
            });
        }
    }

    // La columna "Insumos que ofrece" sale del catálogo del proveedor
    private static void cargarProveedores(VentanaProveedores v) {
        v.modeloProveedores.setRowCount(0);
        for (Proveedor p : proveedorRepo.listar()) {
            StringBuilder insumos = new StringBuilder();
            for (Insumo i : p.getInsumosQueOfrece()) {
                if (insumos.length() > 0) insumos.append(", ");
                insumos.append(i.getDescripcion());
            }
            v.modeloProveedores.addRow(new Object[]{p.getNombre(), p.getContacto(), insumos.toString()});
        }
    }

    // --- REPORTES ---
}

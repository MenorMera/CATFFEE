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

public class ControladorClientes {

    private static VentanaClientes ventanaClientesInstance;

    public static void abrirVentanaClientes() {
        if (ventanaClientesInstance == null || !ventanaClientesInstance.isVisible()) {
            ventanaClientesInstance = new VentanaClientes();
            inicializarVentanaClientes(ventanaClientesInstance);
        }
        ventanaClientesInstance.setVisible(true);
        ventanaClientesInstance.toFront();
        if (ControladorCatffee.ventanaPrincipalInstance != null) ControladorCatffee.ventanaPrincipalInstance.dispose();
    }

    public static void inicializarVentanaClientes(VentanaClientes v) {
        v.modeloClientes.setRowCount(0);
        List<String> lista = GestorCache.leerRegistros("clientes.txt");
        for (String c : lista) {
            String[] p = c.split(";");
            if (p.length >= 3) {
                v.modeloClientes.addRow(new Object[]{p[0], p[1], p[2]});
            }
        }

        if (v.btnGuardar != null) {
            v.btnGuardar.addActionListener(e -> {
                String nombre = v.txtNombre.getText().trim();
                String tel = v.txtTelefono.getText().trim();
                String ced = v.txtCedula.getText().trim();

                if (nombre.isEmpty() || ced.isEmpty()) {
                    JOptionPane.showMessageDialog(v, "Ingrese nombre y cédula del cliente.");
                    return;
                }
                // La cédula identifica al cliente: no puede repetirse
                for (String c : GestorCache.leerRegistros("clientes.txt")) {
                    String[] p = c.split(";");
                    if (p.length >= 3 && p[2].trim().equals(ced)) {
                        JOptionPane.showMessageDialog(v, "Ya existe un cliente con esa cédula.");
                        return;
                    }
                }
                v.modeloClientes.addRow(new Object[]{nombre, tel, ced});
                GestorCache.guardarRegistro("clientes.txt", nombre + ";" + tel + ";" + ced);
                JOptionPane.showMessageDialog(v, "¡Cliente registrado!");
                v.txtNombre.setText("");
                v.txtTelefono.setText("");
                v.txtCedula.setText("");
            });
        }

        if (v.btnCancelar != null) {
            v.btnCancelar.addActionListener(e -> {
                v.dispose();
                ControladorCatffee.abrirVentanaPrincipal(ControladorCatffee.rolActual);
            });
        }
    }

    // --- PROVEEDORES ---
}

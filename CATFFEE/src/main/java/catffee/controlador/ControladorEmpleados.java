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

public class ControladorEmpleados {

    private static VentanaEmpleados ventanaEmpleadosInstance;

    public static void abrirVentanaEmpleados() {
        if (ventanaEmpleadosInstance == null || !ventanaEmpleadosInstance.isVisible()) {
            ventanaEmpleadosInstance = new VentanaEmpleados();
            inicializarVentanaEmpleados(ventanaEmpleadosInstance);
        }
        ventanaEmpleadosInstance.setVisible(true);
        ventanaEmpleadosInstance.toFront();
        if (ControladorCatffee.ventanaPrincipalInstance != null) ControladorCatffee.ventanaPrincipalInstance.dispose();
    }

    public static void inicializarVentanaEmpleados(VentanaEmpleados v) {
        v.modeloEmpleados.setRowCount(0);
        List<String> lista = GestorCache.leerRegistros("empleados.txt");
        for (String emp : lista) {
            String[] p = emp.split(";");
            if (p.length >= 4) {
                v.modeloEmpleados.addRow(new Object[]{p[0], p[1], p[3]});
            }
        }

        if (v.btnGuardar != null && v.btnGuardar.getActionListeners().length == 0) {
            v.btnGuardar.addActionListener(e -> {
                String nombre = v.txtNombre.getText().trim();
                String user = v.txtUsuario.getText().trim();
                String pass = new String(v.txtPassword.getPassword()).trim();
                String rol = (String) v.cmbRol.getSelectedItem();

                if (!nombre.isEmpty() && !user.isEmpty() && !pass.isEmpty()) {
                    v.modeloEmpleados.addRow(new Object[]{nombre, user, rol});
                    GestorCache.guardarRegistro("empleados.txt", nombre + ";" + user + ";" + pass + ";" + rol);
                    JOptionPane.showMessageDialog(v, "¡Empleado guardado exitosamente!");
                    v.txtNombre.setText("");
                    v.txtUsuario.setText("");
                    v.txtPassword.setText("");
                } else {
                    JOptionPane.showMessageDialog(v, "Complete todos los campos del empleado.");
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

    // --- CLIENTES ---
}

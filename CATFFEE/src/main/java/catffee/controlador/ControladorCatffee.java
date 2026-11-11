package catffee.controlador;

import catffee.mockups.*;
import catffee.vista.*;
import javax.swing.JOptionPane;
import javax.swing.JButton;
import java.util.List;

public class ControladorCatffee {

    private static Login loginInstance;
    private static VentanaPrincipal ventanaPrincipalInstance;
    private static VentanaVenta ventanaVentaInstance;
    private static VentanaProductos ventanaProductosInstance;
    private static VentanaInsumos ventanaInsumosInstance;
    private static VentanaCompras ventanaComprasInstance;
    private static VentanaEmpleados ventanaEmpleadosInstance;
    private static VentanaClientes ventanaClientesInstance;
    private static VentanaProveedores ventanaProveedoresInstance;
    private static VentanaReportes ventanaReportesInstance;

    private static String rolActual = "Vendedor";
    public static String tempTelefono = "";
    public static String tempCliente = "";

    public static void abrirLogin() {
        if (loginInstance == null || !loginInstance.isVisible()) {
            loginInstance = new Login();
            inicializarLogin(loginInstance);
        }
        loginInstance.setVisible(true);
        loginInstance.toFront();
    }

    public static void inicializarLogin(Login ventana) {
        if (ventana.btnIngresar != null) {
            ((JButton) ventana.btnIngresar).addActionListener(e -> {
                String usuario = ventana.txtUsuario != null ? ventana.txtUsuario.getText().trim() : "";
                String password = ventana.txtContrasena != null ? new String(ventana.txtContrasena.getPassword()) : "";

                if (usuario.equals("admin") && password.equals("1234")) {
                    rolActual = "Administrador";
                    JOptionPane.showMessageDialog(ventana, "Acceso concedido: Administrador");
                    abrirVentanaPrincipal(rolActual);
                    ventana.dispose();
                } else if (usuario.equals("vendedor") && password.equals("1234")) {
                    rolActual = "Vendedor";
                    JOptionPane.showMessageDialog(ventana, "Acceso concedido: Vendedor");
                    abrirVentanaPrincipal(rolActual);
                    ventana.dispose();
                } else {
                    if (ventana.lblError != null) {
                        ventana.lblError.setText("Usuario o contraseña incorrectos");
                    }
                }
            });
        }

        if (ventana.btnSalir != null) {
            ((JButton) ventana.btnSalir).addActionListener(e -> System.exit(0));
        }
    }

    public static void abrirVentanaPrincipal(String rol) {
        rolActual = rol;
        if (ventanaPrincipalInstance == null || !ventanaPrincipalInstance.isVisible()) {
            ventanaPrincipalInstance = new VentanaPrincipal(rol);
            inicializarVentanaPrincipal(ventanaPrincipalInstance, rol);
        }
        ventanaPrincipalInstance.setVisible(true);
        ventanaPrincipalInstance.toFront();
    }

    public static void inicializarVentanaPrincipal(VentanaPrincipal ventana, String rol) {
        if (ventana.btnNuevaVenta != null) {
            ((JButton) ventana.btnNuevaVenta).addActionListener(e -> abrirVentanaVenta());
        }
        if (ventana.btnProductos != null) {
            ((JButton) ventana.btnProductos).addActionListener(e -> abrirVentanaProductos(rol));
        }
        if (ventana.btnClientes != null) {
            ((JButton) ventana.btnClientes).addActionListener(e -> abrirVentanaClientes());
        }

        if (rol.equals("Administrador")) {
            if (ventana.btnInsumos != null) ((JButton) ventana.btnInsumos).addActionListener(e -> abrirVentanaInsumos());
            if (ventana.btnCompras != null) ((JButton) ventana.btnCompras).addActionListener(e -> abrirVentanaCompras());
            if (ventana.btnProveedores != null) ((JButton) ventana.btnProveedores).addActionListener(e -> abrirVentanaProveedores());
            if (ventana.btnEmpleados != null) ((JButton) ventana.btnEmpleados).addActionListener(e -> abrirVentanaEmpleados());
            if (ventana.btnReportes != null) ((JButton) ventana.btnReportes).addActionListener(e -> abrirVentanaReportes());
        }

        if (ventana.btnCerrarSesion != null) {
            ((JButton) ventana.btnCerrarSesion).addActionListener(e -> {
                ventana.dispose();
                abrirLogin();
            });
        }
    }

    // --- VENTANA VENTA Y CACHÉ ---
    public static void abrirVentanaVenta() {
        if (ventanaVentaInstance == null || !ventanaVentaInstance.isVisible()) {
            ventanaVentaInstance = new VentanaVenta();
            inicializarVentanaVenta(ventanaVentaInstance);
        }
        ventanaVentaInstance.txtTelefonoCliente.setText(tempTelefono);
        ventanaVentaInstance.lblCliente.setText(tempCliente.isEmpty() ? "PÚBLICO GENERAL (USUARIO NULL)" : tempCliente);
        ventanaVentaInstance.setVisible(true);
        ventanaVentaInstance.toFront();
        if (ventanaPrincipalInstance != null) ventanaPrincipalInstance.dispose();
    }

    public static void inicializarVentanaVenta(VentanaVenta ventana) {
        if (ventana.btnSinCliente != null) {
            ((JButton) ventana.btnSinCliente).addActionListener(e -> {
                ventana.txtTelefonoCliente.setText("");
                ventana.lblCliente.setText("PÚBLICO GENERAL (USUARIO NULL)");
                tempCliente = "";
                tempTelefono = "";
            });
        }

        if (ventana.btnBuscarCliente != null) {
            ((JButton) ventana.btnBuscarCliente).addActionListener(e -> {
                String tel = ventana.txtTelefonoCliente.getText().trim();
                if (!tel.isEmpty()) {
                    tempTelefono = tel;
                    tempCliente = "Cliente Tel: " + tel;
                    ventana.lblCliente.setText(tempCliente);
                } else {
                    JOptionPane.showMessageDialog(ventana, "Ingrese un teléfono.");
                }
            });
        }

        if (ventana.btnAgregar != null) {
            ((JButton) ventana.btnAgregar).addActionListener(e -> {
                try {
                    String sel = (String) ventana.cmbProducto.getSelectedItem();
                    String[] p = sel.split(" - ");
                    int precio = Integer.parseInt(p[1]);
                    int cant = Integer.parseInt(ventana.txtCantidad.getText().trim());
                    int sub = precio * cant;
                    ventana.modeloCarrito.addRow(new Object[]{p[0], cant, "$" + precio, "$" + sub});
                    actualizarTotalVenta(ventana);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(ventana, "Cantidad inválida.");
                }
            });
        }

        if (ventana.btnQuitar != null) {
            ((JButton) ventana.btnQuitar).addActionListener(e -> {
                int fila = ventana.tblCarrito.getSelectedRow();
                if (fila >= 0) {
                    ventana.modeloCarrito.removeRow(fila);
                    actualizarTotalVenta(ventana);
                }
            });
        }

        if (ventana.btnConfirmar != null) {
            ((JButton) ventana.btnConfirmar).addActionListener(e -> {
                if (ventana.modeloCarrito.getRowCount() == 0) {
                    JOptionPane.showMessageDialog(ventana, "Carrito vacío.");
                    return;
                }
                String total = ventana.lblTotal.getText().replace("Total: ", "");
                String factura = GeneradorFactura.generarFacturaTxt(ventana.lblCliente.getText(), "Empleado", ventana.modeloCarrito, total);
                
                // Guardar venta en caché de reportes
                GestorCache.guardarRegistro("ventas_cache.txt", java.time.LocalDate.now() + " | " + ventana.lblCliente.getText() + " | " + total);

                JOptionPane.showMessageDialog(ventana, "¡Venta exitosa! Factura generada:\n" + factura);
                ventana.modeloCarrito.setRowCount(0);
                actualizarTotalVenta(ventana);
            });
        }

        if (ventana.btnCancelar != null) {
            ((JButton) ventana.btnCancelar).addActionListener(e -> {
                tempTelefono = ventana.txtTelefonoCliente.getText();
                ventana.dispose();
                abrirVentanaPrincipal(rolActual);
            });
        }
    }

    private static void actualizarTotalVenta(VentanaVenta v) {
        int tot = 0;
        for (int i = 0; i < v.modeloCarrito.getRowCount(); i++) {
            tot += Integer.parseInt(v.modeloCarrito.getValueAt(i, 3).toString().replace("$", "").trim());
        }
        v.lblTotal.setText("Total: $" + tot);
    }

    // --- INSUMOS Y CACHÉ ---
    public static void abrirVentanaInsumos() {
        if (ventanaInsumosInstance == null || !ventanaInsumosInstance.isVisible()) {
            ventanaInsumosInstance = new VentanaInsumos();
            inicializarVentanaInsumos(ventanaInsumosInstance);
        }
        ventanaInsumosInstance.setVisible(true);
        ventanaInsumosInstance.toFront();
        if (ventanaPrincipalInstance != null) ventanaPrincipalInstance.dispose();
    }

    public static void inicializarVentanaInsumos(VentanaInsumos v) {
        // Cargar datos de caché
        List<String> insumos = GestorCache.leerRegistros("insumos.txt");
        for (String ins : insumos) {
            String[] partes = ins.split(";");
            if (partes.length >= 4) {
                v.modeloInsumos.addRow(new Object[]{partes[0], partes[1], partes[2], partes[3]});
            }
        }

        if (v.btnGuardar != null) {
            ((JButton) v.btnGuardar).addActionListener(e -> {
                String desc = v.txtDescripcion.getText().trim();
                String costo = v.txtPrecioCosto.getText().trim();
                String act = v.txtStockActual.getText().trim();
                String min = v.txtStockMinimo.getText().trim();

                if (!desc.isEmpty() && !act.isEmpty()) {
                    String estado = Integer.parseInt(act) <= Integer.parseInt(min) ? "REPONER" : "OK";
                    v.modeloInsumos.addRow(new Object[]{desc, act, min, estado});
                    GestorCache.guardarRegistro("insumos.txt", desc + ";" + act + ";" + min + ";" + estado);
                    JOptionPane.showMessageDialog(v, "¡Insumo guardado en caché!");
                    v.txtDescripcion.setText("");
                    v.txtPrecioCosto.setText("");
                    v.txtStockActual.setText("");
                    v.txtStockMinimo.setText("");
                } else {
                    JOptionPane.showMessageDialog(v, "Complete los campos obligatorios.");
                }
            });
        }

        if (v.btnCancelar != null) {
            ((JButton) v.btnCancelar).addActionListener(e -> {
                v.dispose();
                abrirVentanaPrincipal(rolActual);
            });
        }
    }

    // --- MÉTODOS DE APERTURA PARA NUEVAS VENTANAS ---
    public static void abrirVentanaProductos(String rol) {
        if (ventanaProductosInstance == null || !ventanaProductosInstance.isVisible()) {
            ventanaProductosInstance = new VentanaProductos(rol);
        }
        ventanaProductosInstance.setVisible(true);
        ventanaProductosInstance.toFront();
        if (ventanaPrincipalInstance != null) ventanaPrincipalInstance.dispose();
    }

    public static void abrirVentanaEmpleados() {
        if (ventanaEmpleadosInstance == null || !ventanaEmpleadosInstance.isVisible()) {
            ventanaEmpleadosInstance = new VentanaEmpleados();
        }
        ventanaEmpleadosInstance.setVisible(true);
        ventanaEmpleadosInstance.toFront();
        if (ventanaPrincipalInstance != null) ventanaPrincipalInstance.dispose();
    }

    public static void abrirVentanaProveedores() {
        if (ventanaProveedoresInstance == null || !ventanaProveedoresInstance.isVisible()) {
            ventanaProveedoresInstance = new VentanaProveedores();
        }
        ventanaProveedoresInstance.setVisible(true);
        ventanaProveedoresInstance.toFront();
        if (ventanaPrincipalInstance != null) ventanaPrincipalInstance.dispose();
    }

    public static void abrirVentanaClientes() {
        if (ventanaClientesInstance == null || !ventanaClientesInstance.isVisible()) {
            ventanaClientesInstance = new VentanaClientes();
        }
        ventanaClientesInstance.setVisible(true);
        ventanaClientesInstance.toFront();
        if (ventanaPrincipalInstance != null) ventanaPrincipalInstance.dispose();
    }

    public static void abrirVentanaReportes() {
        if (ventanaReportesInstance == null || !ventanaReportesInstance.isVisible()) {
            ventanaReportesInstance = new VentanaReportes();
        }
        ventanaReportesInstance.setVisible(true);
        ventanaReportesInstance.toFront();
        if (ventanaPrincipalInstance != null) ventanaPrincipalInstance.dispose();
    }

    public static void abrirVentanaCompras() {
        if (ventanaComprasInstance == null || !ventanaComprasInstance.isVisible()) {
            ventanaComprasInstance = new VentanaCompras();
        }
        ventanaComprasInstance.setVisible(true);
        ventanaComprasInstance.toFront();
        if (ventanaPrincipalInstance != null) ventanaPrincipalInstance.dispose();
    }

    public static void inicializarVentanaProductos(VentanaProductos v, String rol) {
        if (v.btnCancelar != null) ((JButton) v.btnCancelar).addActionListener(e -> { v.dispose(); abrirVentanaPrincipal(rolActual); });
        if (rol.equals("Vendedor")) {
            if (v.btnGuardar != null) v.btnGuardar.setEnabled(false);
            if (v.btnActualizar != null) v.btnActualizar.setEnabled(false);
            if (v.btnEliminar != null) v.btnEliminar.setEnabled(false);
        }
    }
    
    public static void inicializarVentanaCompras(VentanaCompras v) {
        if (v.btnCancelar != null) ((JButton) v.btnCancelar).addActionListener(e -> { v.dispose(); abrirVentanaPrincipal(rolActual); });
    }
}
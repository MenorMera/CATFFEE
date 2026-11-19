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

public class ControladorCatffee {

    private static VentanaLogin loginInstance;
    public static VentanaPrincipal ventanaPrincipalInstance;
    public static String rolActual = "Vendedor";
    public static String tempCedula = "";
    public static String tempCliente = "";
    public static String nombreActual = "Empleado";

    public static void abrirLogin() {
        if (loginInstance == null || !loginInstance.isVisible()) {
            loginInstance = new VentanaLogin();
            inicializarLogin(loginInstance);
        }
        loginInstance.setVisible(true);
        loginInstance.toFront();
    }

    public static void inicializarLogin(VentanaLogin ventana) {
        if (ventana.btnIngresar != null && ventana.btnIngresar.getActionListeners().length == 0) {
            ventana.btnIngresar.addActionListener(e -> {
                String usuario = ventana.txtUsuario != null ? ventana.txtUsuario.getText().trim() : "";
                String password = ventana.txtContrasena != null ? new String(ventana.txtContrasena.getPassword()) : "";

                if (usuario.equals("admin") && password.equals("1234")) {
                    rolActual = "Administrador";
                    nombreActual = usuario;
                    JOptionPane.showMessageDialog(ventana, "Acceso concedido: Administrador");
                    abrirVentanaPrincipal(rolActual);
                    ventana.dispose();
                } else if (usuario.equals("vendedor") && password.equals("1234")) {
                    rolActual = "Vendedor";
                    nombreActual = usuario;
                    JOptionPane.showMessageDialog(ventana, "Acceso concedido: Vendedor");
                    abrirVentanaPrincipal(rolActual);
                    ventana.dispose();
                } else {
                    boolean hallado = false;
                    List<String> emps = GestorCache.leerRegistros("empleados.txt");
                    for (String emp : emps) {
                        String[] p = emp.split(";");
                        if (p.length >= 3 && p[1].equals(usuario) && p[2].equals(password)) {
                            rolActual = p.length >= 4 ? p[3] : "Vendedor";
                            nombreActual = p[0];
                            JOptionPane.showMessageDialog(ventana, "Bienvenido " + p[0] + " (" + rolActual + ")");
                            hallado = true;
                            abrirVentanaPrincipal(rolActual);
                            ventana.dispose();
                            break;
                        }
                    }
                    if (!hallado && ventana.lblError != null) {
                        ventana.lblError.setText("Usuario o contraseña incorrectos");
                    }
                }
            });
        }

        if (ventana.btnSalir != null && ventana.btnSalir.getActionListeners().length == 0) {
            ventana.btnSalir.addActionListener(e -> System.exit(0));
        }
    }

    public static void abrirVentanaPrincipal(String rol) {
        if (rol != null && !rol.trim().isEmpty()) {
            rolActual = rol;
        }
        if (ventanaPrincipalInstance != null) {
            ventanaPrincipalInstance.dispose();
            ventanaPrincipalInstance = null;
        }
        ventanaPrincipalInstance = new VentanaPrincipal(rolActual);
        inicializarVentanaPrincipal(ventanaPrincipalInstance, rolActual);
        ventanaPrincipalInstance.setVisible(true);
        ventanaPrincipalInstance.toFront();
    }

    public static void inicializarVentanaPrincipal(VentanaPrincipal ventana, String rol) {
        if (ventana.btnNuevaVenta != null && ventana.btnNuevaVenta.getActionListeners().length == 0) {
            ventana.btnNuevaVenta.addActionListener(e -> ControladorVentas.abrirVentanaVenta());
        }
        if (ventana.btnProductos != null && ventana.btnProductos.getActionListeners().length == 0) {
            ventana.btnProductos.addActionListener(e -> ControladorProductos.abrirVentanaProductos(rolActual));
        }
        if (ventana.btnClientes != null && ventana.btnClientes.getActionListeners().length == 0) {
            ventana.btnClientes.addActionListener(e -> ControladorClientes.abrirVentanaClientes());
        }

        if (rolActual.equals("Administrador")) {
            if (ventana.btnInsumos != null && ventana.btnInsumos.getActionListeners().length == 0)
                ventana.btnInsumos.addActionListener(e -> ControladorInsumos.abrirVentanaInsumos());
            if (ventana.btnCompras != null && ventana.btnCompras.getActionListeners().length == 0)
                ventana.btnCompras.addActionListener(e -> ControladorCompras.abrirVentanaCompras());
            if (ventana.btnProveedores != null && ventana.btnProveedores.getActionListeners().length == 0)
                ventana.btnProveedores.addActionListener(e -> ControladorProveedores.abrirVentanaProveedores());
            if (ventana.btnEmpleados != null && ventana.btnEmpleados.getActionListeners().length == 0)
                ventana.btnEmpleados.addActionListener(e -> ControladorEmpleados.abrirVentanaEmpleados());
            if (ventana.btnReportes != null && ventana.btnReportes.getActionListeners().length == 0)
                ventana.btnReportes.addActionListener(e -> ControladorReportes.abrirVentanaReportes());
        }

        if (ventana.btnCerrarSesion != null && ventana.btnCerrarSesion.getActionListeners().length == 0) {
            ventana.btnCerrarSesion.addActionListener(e -> {
                rolActual = "Vendedor";
                ventana.dispose();
                abrirLogin();
            });
        }
    }

    // --- VENTANA DE VENTA ---
    public static void agregarAccion(Object boton, ActionListener accion) {
        if (boton instanceof AbstractButton abstractButton) {
            abstractButton.addActionListener(accion);
        }
    }

}
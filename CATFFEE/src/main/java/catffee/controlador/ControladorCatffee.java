package catffee.controlador;

import catffee.mockups.Login;
import catffee.vista.*;
import javax.swing.AbstractButton;
import javax.swing.JOptionPane;
import java.awt.event.ActionListener;
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

    // EL ROL PERMANECE FIJO HASTA CERRAR SESIÓN
    public static String rolActual = "Vendedor";
    public static String tempTelefono = "";
    public static String tempCliente = "";

    // --- MÉTODO AUXILIAR ---
    // Agrega un listener a un botón aunque el campo esté declarado como Object.
    private static void agregarAccion(Object boton, ActionListener accion) {
        if (boton instanceof AbstractButton abstractButton) {
            abstractButton.addActionListener(accion);
        }
    }

    public static void abrirLogin() {
        if (loginInstance == null || !loginInstance.isVisible()) {
            loginInstance = new Login();
            inicializarLogin(loginInstance);
        }
        loginInstance.setVisible(true);
        loginInstance.toFront();
    }

    public static void inicializarLogin(Login ventana) {
        if (ventana.btnIngresar != null && ventana.btnIngresar.getActionListeners().length == 0) {
            ventana.btnIngresar.addActionListener(e -> {
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
                    boolean hallado = false;
                    List<String> emps = GestorCache.leerRegistros("empleados.txt");
                    for (String emp : emps) {
                        String[] p = emp.split(";");
                        if (p.length >= 3 && p[1].equals(usuario) && p[2].equals(password)) {
                            rolActual = p.length >= 4 ? p[3] : "Vendedor";
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
            ventana.btnNuevaVenta.addActionListener(e -> abrirVentanaVenta());
        }
        if (ventana.btnProductos != null && ventana.btnProductos.getActionListeners().length == 0) {
            ventana.btnProductos.addActionListener(e -> abrirVentanaProductos(rolActual));
        }
        if (ventana.btnClientes != null && ventana.btnClientes.getActionListeners().length == 0) {
            ventana.btnClientes.addActionListener(e -> abrirVentanaClientes());
        }

        if (rolActual.equals("Administrador")) {
            if (ventana.btnInsumos != null && ventana.btnInsumos.getActionListeners().length == 0)
                ventana.btnInsumos.addActionListener(e -> abrirVentanaInsumos());
            if (ventana.btnCompras != null && ventana.btnCompras.getActionListeners().length == 0)
                ventana.btnCompras.addActionListener(e -> abrirVentanaCompras());
            if (ventana.btnProveedores != null && ventana.btnProveedores.getActionListeners().length == 0)
                ventana.btnProveedores.addActionListener(e -> abrirVentanaProveedores());
            if (ventana.btnEmpleados != null && ventana.btnEmpleados.getActionListeners().length == 0)
                ventana.btnEmpleados.addActionListener(e -> abrirVentanaEmpleados());
            if (ventana.btnReportes != null && ventana.btnReportes.getActionListeners().length == 0)
                ventana.btnReportes.addActionListener(e -> abrirVentanaReportes());
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
        if (ventana.btnSinCliente != null && ventana.btnSinCliente.getActionListeners().length == 0) {
            ventana.btnSinCliente.addActionListener(e -> {
                ventana.txtTelefonoCliente.setText("");
                ventana.lblCliente.setText("PÚBLICO GENERAL (USUARIO NULL)");
                tempCliente = "";
                tempTelefono = "";
            });
        }

        if (ventana.btnBuscarCliente != null && ventana.btnBuscarCliente.getActionListeners().length == 0) {
            ventana.btnBuscarCliente.addActionListener(e -> {
                String tel = ventana.txtTelefonoCliente.getText().trim();
                if (!tel.isEmpty()) {
                    boolean hallado = false;
                    List<String> clientes = GestorCache.leerRegistros("clientes.txt");
                    for (String c : clientes) {
                        String[] p = c.split(";");
                        if (p.length >= 2 && p[1].equals(tel)) {
                            tempTelefono = tel;
                            tempCliente = p[0] + " (Tel: " + tel + ")";
                            ventana.lblCliente.setText(tempCliente);
                            hallado = true;
                            break;
                        }
                    }
                    if (!hallado) {
                        tempTelefono = tel;
                        tempCliente = "Cliente Tel: " + tel;
                        ventana.lblCliente.setText(tempCliente);
                    }
                } else {
                    JOptionPane.showMessageDialog(ventana, "Ingrese un teléfono.");
                }
            });
        }

        if (ventana.btnAgregar != null && ventana.btnAgregar.getActionListeners().length == 0) {
            ventana.btnAgregar.addActionListener(e -> {
                try {
                    String sel = (String) ventana.cmbProducto.getSelectedItem();
                    String[] p = sel.split(" - ");
                    int precio = Integer.parseInt(p[1].trim());
                    int cant = Integer.parseInt(ventana.txtCantidad.getText().trim());
                    int sub = precio * cant;

                    ventana.modeloCarrito.addRow(new Object[]{p[0], cant, "$" + precio, "$" + sub});
                    actualizarTotalVenta(ventana);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(ventana, "Cantidad inválida.");
                }
            });
        }

        if (ventana.btnQuitar != null && ventana.btnQuitar.getActionListeners().length == 0) {
            ventana.btnQuitar.addActionListener(e -> {
                int fila = ventana.tblCarrito.getSelectedRow();
                if (fila >= 0) {
                    ventana.modeloCarrito.removeRow(fila);
                    actualizarTotalVenta(ventana);
                } else {
                    JOptionPane.showMessageDialog(ventana, "Seleccione un producto para quitar.");
                }
            });
        }

        if (ventana.btnConfirmar != null && ventana.btnConfirmar.getActionListeners().length == 0) {
            ventana.btnConfirmar.addActionListener(e -> {
                if (ventana.modeloCarrito.getRowCount() == 0) {
                    JOptionPane.showMessageDialog(ventana, "El carrito está vacío.");
                    return;
                }
                String total = ventana.lblTotal.getText().replace("Total: ", "");
                String factura = GeneradorFactura.generarFacturaTxt(ventana.lblCliente.getText(), "Empleado (" + rolActual + ")", ventana.tblCarrito, total);

                GestorCache.guardarRegistro("ventas_cache.txt", java.time.LocalDate.now() + " | " + ventana.lblCliente.getText() + " | " + total);

                if (factura != null) {
                    JOptionPane.showMessageDialog(ventana, "¡Venta exitosa!\nFactura física guardada en:\n" + factura);
                    ventana.modeloCarrito.setRowCount(0);
                    actualizarTotalVenta(ventana);
                } else {
                    JOptionPane.showMessageDialog(ventana, "Error al generar la factura.");
                }
            });
        }

        if (ventana.btnCancelar != null) {
            ventana.btnCancelar.addActionListener(e -> {
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

    // --- EMPLEADOS ---
    public static void abrirVentanaEmpleados() {
        if (ventanaEmpleadosInstance == null || !ventanaEmpleadosInstance.isVisible()) {
            ventanaEmpleadosInstance = new VentanaEmpleados();
            inicializarVentanaEmpleados(ventanaEmpleadosInstance);
        }
        ventanaEmpleadosInstance.setVisible(true);
        ventanaEmpleadosInstance.toFront();
        if (ventanaPrincipalInstance != null) ventanaPrincipalInstance.dispose();
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
                abrirVentanaPrincipal(rolActual);
            });
        }
    }

    // --- CLIENTES ---
    public static void abrirVentanaClientes() {
        if (ventanaClientesInstance == null || !ventanaClientesInstance.isVisible()) {
            ventanaClientesInstance = new VentanaClientes();
            inicializarVentanaClientes(ventanaClientesInstance);
        }
        ventanaClientesInstance.setVisible(true);
        ventanaClientesInstance.toFront();
        if (ventanaPrincipalInstance != null) ventanaPrincipalInstance.dispose();
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

                if (!nombre.isEmpty() && !tel.isEmpty()) {
                    v.modeloClientes.addRow(new Object[]{nombre, tel, ced});
                    GestorCache.guardarRegistro("clientes.txt", nombre + ";" + tel + ";" + ced);
                    JOptionPane.showMessageDialog(v, "¡Cliente registrado en caché!");
                    v.txtNombre.setText("");
                    v.txtTelefono.setText("");
                    v.txtCedula.setText("");
                } else {
                    JOptionPane.showMessageDialog(v, "Ingrese nombre y teléfono del cliente.");
                }
            });
        }

        if (v.btnCancelar != null) {
            v.btnCancelar.addActionListener(e -> {
                v.dispose();
                abrirVentanaPrincipal(rolActual);
            });
        }
    }

    // --- PROVEEDORES ---
    public static void abrirVentanaProveedores() {
        if (ventanaProveedoresInstance == null || !ventanaProveedoresInstance.isVisible()) {
            ventanaProveedoresInstance = new VentanaProveedores();
            inicializarVentanaProveedores(ventanaProveedoresInstance);
        }
        ventanaProveedoresInstance.setVisible(true);
        ventanaProveedoresInstance.toFront();
        if (ventanaPrincipalInstance != null) ventanaPrincipalInstance.dispose();
    }

    public static void inicializarVentanaProveedores(VentanaProveedores v) {
        v.modeloProveedores.setRowCount(0);
        List<String> lista = GestorCache.leerRegistros("proveedores.txt");
        for (String pStr : lista) {
            String[] p = pStr.split(";");
            if (p.length >= 3) {
                v.modeloProveedores.addRow(new Object[]{p[0], p[1], p[2]});
            }
        }

        if (v.btnGuardar != null) {
            v.btnGuardar.addActionListener(e -> {
                String nombre = v.txtNombre.getText().trim();
                String tel = v.txtTelefono.getText().trim();
                String prod = v.txtProducto.getText().trim();

                if (!nombre.isEmpty()) {
                    v.modeloProveedores.addRow(new Object[]{nombre, tel, prod});
                    GestorCache.guardarRegistro("proveedores.txt", nombre + ";" + tel + ";" + prod);
                    JOptionPane.showMessageDialog(v, "¡Proveedor registrado!");
                    v.txtNombre.setText("");
                    v.txtTelefono.setText("");
                    v.txtProducto.setText("");
                } else {
                    JOptionPane.showMessageDialog(v, "Ingrese el nombre del proveedor.");
                }
            });
        }

        if (v.btnCancelar != null) {
            v.btnCancelar.addActionListener(e -> {
                v.dispose();
                abrirVentanaPrincipal(rolActual);
            });
        }
    }

    // --- REPORTES ---
    public static void abrirVentanaReportes() {
        if (ventanaReportesInstance == null || !ventanaReportesInstance.isVisible()) {
            ventanaReportesInstance = new VentanaReportes();
            inicializarVentanaReportes(ventanaReportesInstance);
        }
        ventanaReportesInstance.setVisible(true);
        ventanaReportesInstance.toFront();
        if (ventanaPrincipalInstance != null) ventanaPrincipalInstance.dispose();
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
                abrirVentanaPrincipal(rolActual);
            });
        }
    }

    // --- INSUMOS ---
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
    cargarInsumos(v, false);

    // Clic en una fila -> llena el formulario
    v.tblInsumos.getSelectionModel().addListSelectionListener(e -> {
        if (e.getValueIsAdjusting()) return;
        int fila = v.tblInsumos.getSelectedRow();
        if (fila >= 0) {
            String desc = v.modeloInsumos.getValueAt(fila, 0).toString();
            v.txtDescripcion.setText(desc);
            v.txtStockActual.setText(v.modeloInsumos.getValueAt(fila, 1).toString());
            v.txtStockMinimo.setText(v.modeloInsumos.getValueAt(fila, 2).toString());
            String[] reg = buscarInsumo(desc);
            v.txtCosto.setText(reg != null && reg.length >= 5 ? reg[4] : "");
        }
    });

    // Filtro "solo los que hay que reponer"
    v.chkSoloReponer.addActionListener(e -> cargarInsumos(v, v.chkSoloReponer.isSelected()));

    // Guardar (nuevo)
    v.btnGuardar.addActionListener(e -> {
        String[] d = leerFormularioInsumo(v);
        if (d == null) return;
        GestorCache.guardarRegistro("insumos.txt", String.join(";", d));
        limpiarFormularioInsumo(v);
        cargarInsumos(v, v.chkSoloReponer.isSelected());
        JOptionPane.showMessageDialog(v, "¡Insumo guardado!");
    });

    // Actualizar (el seleccionado)
    v.btnActualizar.addActionListener(e -> {
        int fila = v.tblInsumos.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(v, "Seleccione un insumo de la tabla.");
            return;
        }
        String original = v.modeloInsumos.getValueAt(fila, 0).toString();
        String[] d = leerFormularioInsumo(v);
        if (d == null) return;

        List<String> nuevas = new java.util.ArrayList<>();
        for (String linea : GestorCache.leerRegistros("insumos.txt")) {
            nuevas.add(linea.split(";")[0].equals(original) ? String.join(";", d) : linea);
        }
        GestorCache.sobrescribirRegistros("insumos.txt", nuevas);
        limpiarFormularioInsumo(v);
        cargarInsumos(v, v.chkSoloReponer.isSelected());
    });

    // Eliminar
    v.btnEliminar.addActionListener(e -> {
        int fila = v.tblInsumos.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(v, "Seleccione un insumo de la tabla.");
            return;
        }
        String desc = v.modeloInsumos.getValueAt(fila, 0).toString();
        int ok = JOptionPane.showConfirmDialog(v, "¿Eliminar \"" + desc + "\"?", "Confirmar",
                JOptionPane.YES_NO_OPTION);
        if (ok != JOptionPane.YES_OPTION) return;

        List<String> nuevas = new java.util.ArrayList<>();
        for (String linea : GestorCache.leerRegistros("insumos.txt")) {
            if (!linea.split(";")[0].equals(desc)) nuevas.add(linea);
        }
        GestorCache.sobrescribirRegistros("insumos.txt", nuevas);
        limpiarFormularioInsumo(v);
        cargarInsumos(v, v.chkSoloReponer.isSelected());
    });

    // Limpiar
    v.btnLimpiar.addActionListener(e -> {
        limpiarFormularioInsumo(v);
        v.tblInsumos.clearSelection();
    });

    agregarAccion(v.btnCancelar, e -> {
        v.dispose();
        abrirVentanaPrincipal(rolActual);
    });
}

// --- Auxiliares de Insumos ---
private static void cargarInsumos(VentanaInsumos v, boolean soloReponer) {
    v.modeloInsumos.setRowCount(0);
    for (String linea : GestorCache.leerRegistros("insumos.txt")) {
        String[] p = linea.split(";");
        if (p.length >= 4 && (!soloReponer || p[3].equals("REPONER"))) {
            v.modeloInsumos.addRow(new Object[]{p[0], p[1], p[2], p[3]});
        }
    }
}

private static String[] buscarInsumo(String desc) {
    for (String linea : GestorCache.leerRegistros("insumos.txt")) {
        String[] p = linea.split(";");
        if (p[0].equals(desc)) return p;
    }
    return null;
}

// Devuelve {desc, stock, minimo, estado, costo} o null si hay error
private static String[] leerFormularioInsumo(VentanaInsumos v) {
    String desc = v.txtDescripcion.getText().trim();
    String act = v.txtStockActual.getText().trim();
    String min = v.txtStockMinimo.getText().trim();
    String costo = v.txtCosto.getText().trim();
    if (desc.isEmpty() || act.isEmpty() || min.isEmpty()) {
        JOptionPane.showMessageDialog(v, "Complete descripción, stock actual y stock mínimo.");
        return null;
    }
    try {
        int a = Integer.parseInt(act);
        int m = Integer.parseInt(min);
        if (!costo.isEmpty()) Integer.parseInt(costo);
        String estado = a <= m ? "REPONER" : "OK";
        return new String[]{desc, act, min, estado, costo.isEmpty() ? "0" : costo};
    } catch (NumberFormatException ex) {
        JOptionPane.showMessageDialog(v, "Stock y costo deben ser números enteros.");
        return null;
    }
}

private static void limpiarFormularioInsumo(VentanaInsumos v) {
    v.txtDescripcion.setText("");
    v.txtStockActual.setText("");
    v.txtStockMinimo.setText("");
    v.txtCosto.setText("");
}
    // --- PRODUCTOS ---
    public static void abrirVentanaProductos(String rol) {
        if (ventanaProductosInstance == null || !ventanaProductosInstance.isVisible()) {
            ventanaProductosInstance = new VentanaProductos(rolActual);
            inicializarVentanaProductos(ventanaProductosInstance, rolActual);
        }
        ventanaProductosInstance.setVisible(true);
        ventanaProductosInstance.toFront();
        if (ventanaPrincipalInstance != null) ventanaPrincipalInstance.dispose();
    }

    public static void inicializarVentanaProductos(VentanaProductos v, String rol) {
        // CORREGIDO: btnCancelar puede estar declarado como Object
        agregarAccion(v.btnCancelar, e -> {
            v.dispose();
            abrirVentanaPrincipal(rolActual);
        });

        if (rolActual.equals("Vendedor")) {
            if (v.btnGuardar != null) v.btnGuardar.setEnabled(false);
            if (v.btnActualizar != null) v.btnActualizar.setEnabled(false);
            if (v.btnEliminar != null) v.btnEliminar.setEnabled(false);
            if (v.btnAgregarIngrediente != null) v.btnAgregarIngrediente.setEnabled(false);
        }
    }

    // --- COMPRAS ---
    public static void abrirVentanaCompras() {
        if (ventanaComprasInstance == null || !ventanaComprasInstance.isVisible()) {
            ventanaComprasInstance = new VentanaCompras();
            inicializarVentanaCompras(ventanaComprasInstance);
        }
        ventanaComprasInstance.setVisible(true);
        ventanaComprasInstance.toFront();
        if (ventanaPrincipalInstance != null) ventanaPrincipalInstance.dispose();
    }

    public static void inicializarVentanaCompras(VentanaCompras v) {
        // CORREGIDO: btnCancelar puede estar declarado como Object
        agregarAccion(v.btnCancelar, e -> {
            v.dispose();
            abrirVentanaPrincipal(rolActual);
        });
    }
}
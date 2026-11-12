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

public class ControladorCatffee {

    private static VentanaLogin loginInstance;
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
    // Repositorios: el controlador no toca los archivos, le pide todo a ellos
    private static final InsumoRepositorio insumoRepo = new InsumoRepositorio();
    private static final ProveedorRepositorio proveedorRepo = new ProveedorRepositorio();
    private static final ProductoRepositorio productoRepo = new ProductoRepositorio();

    public static String rolActual = "Vendedor";
    public static String tempCedula = "";
    public static String tempCliente = "";
    public static String nombreActual = "Empleado";      // quien inició sesión
    private static Cliente clienteSeleccionado = null;   // cliente de la venta en curso (null = público general)

    // --- MÉTODO AUXILIAR ---
    // Agrega un listener a un botón aunque el campo esté declarado como Object.
    private static void agregarAccion(Object boton, ActionListener accion) {
        if (boton instanceof AbstractButton abstractButton) {
            abstractButton.addActionListener(accion);
        }
    }

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
        ventanaVentaInstance.txtCedulaCliente.setText(tempCedula);
        ventanaVentaInstance.lblCliente.setText(tempCliente.isEmpty() ? "PÚBLICO GENERAL (USUARIO NULL)" : tempCliente);
        ventanaVentaInstance.setVisible(true);
        ventanaVentaInstance.toFront();
        if (ventanaPrincipalInstance != null) ventanaPrincipalInstance.dispose();
    }

    public static void inicializarVentanaVenta(VentanaVenta ventana) {
        recargarProductosEnVenta(ventana);
        if (ventana.btnRefrescar != null && ventana.btnRefrescar.getActionListeners().length == 0) {
            ventana.btnRefrescar.addActionListener(e -> recargarProductosEnVenta(ventana));
            // Al volver a esta ventana se actualiza solo la lista de productos
            ventana.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowActivated(java.awt.event.WindowEvent e) {
                    recargarProductosEnVenta(ventana);
                }
            });
        }

        if (ventana.btnSinCliente != null && ventana.btnSinCliente.getActionListeners().length == 0) {
            ventana.btnSinCliente.addActionListener(e -> {
                ventana.txtCedulaCliente.setText("");
                ventana.lblCliente.setText("PÚBLICO GENERAL (USUARIO NULL)");
                tempCliente = "";
                tempCedula = "";
                clienteSeleccionado = null;
            });
        }

        if (ventana.btnBuscarCliente != null && ventana.btnBuscarCliente.getActionListeners().length == 0) {
            ventana.btnBuscarCliente.addActionListener(e -> {
                String ced = ventana.txtCedulaCliente.getText().trim();
                if (ced.isEmpty()) {
                    JOptionPane.showMessageDialog(ventana, "Ingrese la cédula del cliente.");
                    return;
                }
                // clientes.txt guarda: nombre;telefono;cedula  -> la cédula es el identificador
                for (String c : GestorCache.leerRegistros("clientes.txt")) {
                    String[] p = c.split(";");
                    if (p.length >= 3 && p[2].trim().equals(ced)) {
                        tempCedula = ced;
                        tempCliente = p[0] + " (C.C. " + ced + ")";
                        clienteSeleccionado = new Cliente(ced, p[0], "", p[1]);
                        ventana.lblCliente.setText(tempCliente);
                        return;
                    }
                }
                JOptionPane.showMessageDialog(ventana,
                        "No hay ningún cliente registrado con la cédula " + ced + ".\n"
                        + "Regístrelo primero en Clientes o use \"Público General\".");
            });
        }

        if (ventana.btnAgregar != null && ventana.btnAgregar.getActionListeners().length == 0) {
            ventana.btnAgregar.addActionListener(e -> {
                String sel = (String) ventana.cmbProducto.getSelectedItem();
                if (sel == null) {
                    JOptionPane.showMessageDialog(ventana, "No hay productos registrados.");
                    return;
                }
                try {
                    // El combo muestra "Nombre - precio"
                    int corte = sel.lastIndexOf(" - ");
                    String nombre = sel.substring(0, corte);
                    int precio = (int) Double.parseDouble(sel.substring(corte + 3).trim());
                    int cant = Integer.parseInt(ventana.txtCantidad.getText().trim());
                    if (cant <= 0) throw new NumberFormatException();
                    agregarAlCarrito(ventana, nombre, precio, cant);
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
                // Confirmar con el modelo: verifica que alcancen los insumos y los descuenta del stock
                java.util.List<String> nombres = new java.util.ArrayList<>();
                java.util.List<Integer> cantidades = new java.util.ArrayList<>();
                for (int i = 0; i < ventana.modeloCarrito.getRowCount(); i++) {
                    nombres.add(ventana.modeloCarrito.getValueAt(i, 0).toString());
                    cantidades.add(Integer.parseInt(ventana.modeloCarrito.getValueAt(i, 1).toString()));
                }
                ResultadoVenta rv = confirmarVentaConInsumos(nombres, cantidades, clienteSeleccionado);
                if (!rv.ok) {
                    JOptionPane.showMessageDialog(ventana, rv.mensaje, "No se pudo confirmar", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                String total = ventana.lblTotal.getText().replace("Total: ", "");
                String factura = GeneradorFactura.generarFacturaTxt(ventana.lblCliente.getText(), "Empleado (" + rolActual + ")", ventana.tblCarrito, total);

                GestorCache.guardarRegistro("ventas_cache.txt", java.time.LocalDate.now() + " | " + ventana.lblCliente.getText() + " | " + total);

                if (factura != null) {
                    String aviso = "";
                    if (!rv.sinReceta.isEmpty()) {
                        aviso += "\n\nSin receta (no descuentan insumos): " + String.join(", ", rv.sinReceta);
                    }
                    if (!rv.porReponer.isEmpty()) {
                        aviso += "\n\nInsumos por reponer: " + String.join(", ", rv.porReponer);
                    }
                    JOptionPane.showMessageDialog(ventana, "¡Venta exitosa!\nFactura física guardada en:\n" + factura + aviso);
                    ventana.modeloCarrito.setRowCount(0);
                    actualizarTotalVenta(ventana);
                } else {
                    JOptionPane.showMessageDialog(ventana, "Error al generar la factura.");
                }
            });
        }

        if (ventana.btnCancelar != null) {
            ventana.btnCancelar.addActionListener(e -> {
                tempCedula = ventana.txtCedulaCliente.getText();
                ventana.dispose();
                abrirVentanaPrincipal(rolActual);
            });
        }
    }

    // Resultado de confirmar una venta
    public static class ResultadoVenta {
        public boolean ok = false;
        public String mensaje = "";
        public java.util.List<String> sinReceta = new java.util.ArrayList<>();   // productos que no descuentan nada
        public java.util.List<String> porReponer = new java.util.ArrayList<>();  // insumos que quedaron en el mínimo o menos
    }

    /**
     * Confirma una venta usando las clases del modelo (Venta, Producto, Ingrediente, Insumo):
     * 1) arma la Venta con los productos del carrito,
     * 2) Venta.agregarProducto / confirmar verifican que alcancen los insumos y los descuentan,
     * 3) el stock que quedó se guarda en cache/insumos.txt.
     * Si falta algún insumo no se descuenta nada.
     */
    public static ResultadoVenta confirmarVentaConInsumos(java.util.List<String> nombres,
                                                          java.util.List<Integer> cantidades,
                                                          Cliente cliente) {
        ResultadoVenta rv = new ResultadoVenta();
        java.util.ArrayList<Producto> catalogo = productoRepo.listar();   // comparten los mismos objetos Insumo
        Venta venta = new Venta(cliente, new Vendedor(nombreActual, nombreActual, "-"));

        for (int i = 0; i < nombres.size(); i++) {
            Producto prod = null;
            for (Producto p : catalogo) {
                if (p.getNombre().equalsIgnoreCase(nombres.get(i))) prod = p;
            }
            if (prod == null) {
                rv.mensaje = "El producto \"" + nombres.get(i) + "\" ya no existe. Quítelo del carrito.";
                return rv;
            }
            if (!venta.agregarProducto(prod, cantidades.get(i))) {
                rv.mensaje = "No alcanzan los insumos para vender " + cantidades.get(i) + " x " + prod.getNombre() + ".\n\n"
                        + insumosInsuficientes(venta, prod, cantidades.get(i))
                        + "\nReponga stock en la ventana Insumos (botón \"+ Stock\").";
                return rv;
            }
            if (prod.getIngredientes().isEmpty()) rv.sinReceta.add(prod.getNombre());
        }

        if (!venta.confirmar()) {
            rv.mensaje = "No se pudo confirmar la venta.";
            return rv;
        }

        // Guardar el stock que dejó la venta (cada insumo una sola vez)
        java.util.Map<String, Insumo> tocados = new java.util.LinkedHashMap<>();
        for (DetalleVenta d : venta.getDetalles()) {
            for (Ingrediente ing : d.getProducto().getIngredientes()) {
                tocados.put(ing.getInsumo().getDescripcion().toLowerCase(), ing.getInsumo());
            }
        }
        for (Insumo i : tocados.values()) {
            insumoRepo.actualizar(i.getDescripcion(), i);
            if (i.necesitaReposicion()) rv.porReponer.add(i.getDescripcion());
        }
        rv.ok = true;
        return rv;
    }

    // Texto con los insumos que no alcanzan: "Leche: se necesitan 400 y hay 150"
    private static String insumosInsuficientes(Venta venta, Producto nuevo, int cantidadNueva) {
        java.util.Map<String, Double> necesario = new java.util.LinkedHashMap<>();
        java.util.Map<String, Insumo> insumos = new java.util.HashMap<>();
        for (DetalleVenta d : venta.getDetalles()) {
            acumularNecesidad(necesario, insumos, d.getProducto(), d.getCantidad());
        }
        acumularNecesidad(necesario, insumos, nuevo, cantidadNueva);

        StringBuilder sb = new StringBuilder();
        for (java.util.Map.Entry<String, Double> en : necesario.entrySet()) {
            double hay = insumos.get(en.getKey()).getStockActual();
            if (en.getValue() > hay) {
                sb.append("• ").append(insumos.get(en.getKey()).getDescripcion())
                  .append(": se necesitan ").append(Formato.numero(en.getValue()))
                  .append(" y hay ").append(Formato.numero(hay)).append("\n");
            }
        }
        return sb.toString();
    }

    private static void acumularNecesidad(java.util.Map<String, Double> necesario,
                                          java.util.Map<String, Insumo> insumos,
                                          Producto prod, int cantidad) {
        for (Ingrediente ing : prod.getIngredientes()) {
            String clave = ing.getInsumo().getDescripcion().toLowerCase();
            insumos.put(clave, ing.getInsumo());
            necesario.merge(clave, ing.getCantidad() * cantidad, Double::sum);
        }
    }

    // Llena el combo con los productos registrados ("Nombre - precio") y conserva la selección
    private static void recargarProductosEnVenta(VentanaVenta v) {
        productoRepo.sembrarSiHaceFalta();
        Object seleccionado = v.cmbProducto.getSelectedItem();
        v.cmbProducto.removeAllItems();
        for (Producto p : productoRepo.listar()) {
            v.cmbProducto.addItem(p.getNombre() + " - " + Formato.numero(p.getPrecioVenta()));
        }
        if (seleccionado != null) v.cmbProducto.setSelectedItem(seleccionado);
    }

    // Si el producto ya está en el carrito, se suma la cantidad en la misma fila
    private static void agregarAlCarrito(VentanaVenta v, String nombre, int precio, int cant) {
        for (int i = 0; i < v.modeloCarrito.getRowCount(); i++) {
            if (v.modeloCarrito.getValueAt(i, 0).toString().equals(nombre)) {
                int nuevaCant = Integer.parseInt(v.modeloCarrito.getValueAt(i, 1).toString()) + cant;
                v.modeloCarrito.setValueAt(nuevaCant, i, 1);
                v.modeloCarrito.setValueAt("$" + (precio * nuevaCant), i, 3);
                actualizarTotalVenta(v);
                return;
            }
        }
        v.modeloCarrito.addRow(new Object[]{nombre, cant, "$" + precio, "$" + (precio * cant)});
        actualizarTotalVenta(v);
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
                abrirVentanaPrincipal(rolActual);
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

    private static final String SIN_PROVEEDOR = "(sin proveedor)";
    private static String filtroInsumos = "";

    public static void inicializarVentanaInsumos(VentanaInsumos v) {
        filtroInsumos = "";
        recargarProveedoresEnCombo(v);
        cargarInsumos(v);

        // Si se registra un proveedor y se vuelve a esta ventana, el combo se actualiza solo
        v.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowActivated(java.awt.event.WindowEvent e) {
                recargarProveedoresEnCombo(v);
            }
        });

        // Al elegir una fila se llenan los campos para poder editarla
        v.tblInsumos.getSelectionModel().addListSelectionListener(e -> {
            int fila = v.tblInsumos.getSelectedRow();
            if (e.getValueIsAdjusting() || fila < 0) return;
            Insumo ins = insumoRepo.buscarPorDescripcion(v.modeloInsumos.getValueAt(fila, 0).toString());
            if (ins == null) return;
            v.txtDescripcion.setText(ins.getDescripcion());
            v.txtStockActual.setText(Formato.numero(ins.getStockActual()));
            v.txtStockMinimo.setText(Formato.numero(ins.getStockMinimo()));
            v.txtCosto.setText(Formato.numero(ins.getPrecioCosto()));
            java.util.ArrayList<String> provs = proveedorRepo.proveedoresDe(ins.getDescripcion());
            v.cmbProveedor.setSelectedItem(provs.isEmpty() ? SIN_PROVEEDOR : provs.get(0));
        });

        // BUSCAR: filtra por lo escrito en Descripción
        v.btnBuscar.addActionListener(e -> {
            filtroInsumos = v.txtDescripcion.getText().trim();
            cargarInsumos(v);
        });
        v.chkSoloReponer.addActionListener(e -> cargarInsumos(v));

        // GUARDAR
        v.btnGuardar.addActionListener(e -> {
            Insumo nuevo = leerFormularioInsumo(v);
            if (nuevo == null) return;
            if (!insumoRepo.guardar(nuevo)) {
                JOptionPane.showMessageDialog(v, "Ya existe un insumo con esa descripción.");
                return;
            }
            String prov = proveedorSeleccionado(v);
            if (prov != null) proveedorRepo.agregarAlCatalogo(prov, nuevo.getDescripcion());
            limpiarFormularioInsumo(v);
            cargarInsumos(v);
            JOptionPane.showMessageDialog(v, "¡Insumo guardado!");
        });

        // ACTUALIZAR (también asigna el proveedor elegido en el combo)
        v.btnActualizar.addActionListener(e -> {
            int fila = v.tblInsumos.getSelectedRow();
            if (fila < 0) {
                JOptionPane.showMessageDialog(v, "Seleccione un insumo de la tabla.");
                return;
            }
            String original = v.modeloInsumos.getValueAt(fila, 0).toString();
            Insumo nuevo = leerFormularioInsumo(v);
            if (nuevo == null) return;
            if (!insumoRepo.actualizar(original, nuevo)) {
                JOptionPane.showMessageDialog(v, "Ya existe otro insumo con esa descripción.");
                return;
            }
            if (!original.equalsIgnoreCase(nuevo.getDescripcion())) {
                proveedorRepo.renombrarInsumo(original, nuevo.getDescripcion());
                productoRepo.renombrarInsumo(original, nuevo.getDescripcion());
            }
            String prov = proveedorSeleccionado(v);
            if (prov != null) proveedorRepo.agregarAlCatalogo(prov, nuevo.getDescripcion());
            limpiarFormularioInsumo(v);
            cargarInsumos(v);
            JOptionPane.showMessageDialog(v, "¡Insumo actualizado!");
        });

        // ELIMINAR (no deja borrar un insumo que usa alguna receta)
        v.btnEliminar.addActionListener(e -> {
            int fila = v.tblInsumos.getSelectedRow();
            if (fila < 0) {
                JOptionPane.showMessageDialog(v, "Seleccione un insumo de la tabla.");
                return;
            }
            String desc = v.modeloInsumos.getValueAt(fila, 0).toString();
            java.util.ArrayList<String> usan = productoRepo.productosQueUsan(desc);
            if (!usan.isEmpty()) {
                JOptionPane.showMessageDialog(v, "No se puede eliminar \"" + desc
                        + "\": lo usan las recetas de: " + String.join(", ", usan) + ".");
                return;
            }
            int r = JOptionPane.showConfirmDialog(v, "¿Eliminar el insumo \"" + desc + "\"?",
                    "Confirmar", JOptionPane.YES_NO_OPTION);
            if (r != JOptionPane.YES_OPTION) return;
            insumoRepo.eliminar(desc);
            proveedorRepo.quitarInsumo(desc);
            limpiarFormularioInsumo(v);
            cargarInsumos(v);
        });

        // QUITAR PROVEEDOR: saca al proveedor elegido en el combo del insumo seleccionado
        v.btnQuitarProveedor.addActionListener(e -> {
            int fila = v.tblInsumos.getSelectedRow();
            String prov = proveedorSeleccionado(v);
            if (fila < 0 || prov == null) {
                JOptionPane.showMessageDialog(v, "Seleccione un insumo y el proveedor que desea quitarle.");
                return;
            }
            String desc = v.modeloInsumos.getValueAt(fila, 0).toString();
            if (proveedorRepo.quitarDelCatalogo(prov, desc)) {
                cargarInsumos(v);
            } else {
                JOptionPane.showMessageDialog(v, prov + " no ofrecía \"" + desc + "\".");
            }
        });

        // + STOCK / - STOCK: ajuste manual (por una compra, un daño, un conteo...)
        v.btnSumarStock.addActionListener(e -> ajustarStock(v, true));
        v.btnRestarStock.addActionListener(e -> ajustarStock(v, false));

        // LIMPIAR
        v.btnLimpiar.addActionListener(e -> {
            filtroInsumos = "";
            v.chkSoloReponer.setSelected(false);
            limpiarFormularioInsumo(v);
            cargarInsumos(v);
        });

        v.btnCancelar.addActionListener(e -> {
            v.dispose();
            abrirVentanaPrincipal(rolActual);
        });
    }

    // --- Auxiliares de Insumos ---

    // Pide una cantidad y la suma o resta al stock del insumo seleccionado (usa los métodos del modelo)
    private static void ajustarStock(VentanaInsumos v, boolean sumar) {
        int fila = v.tblInsumos.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(v, "Seleccione un insumo de la tabla.");
            return;
        }
        String desc = v.modeloInsumos.getValueAt(fila, 0).toString();
        Insumo ins = insumoRepo.buscarPorDescripcion(desc);
        if (ins == null) return;

        String texto = JOptionPane.showInputDialog(v,
                (sumar ? "¿Cuánto stock desea AGREGAR" : "¿Cuánto stock desea DESCONTAR")
                + " a \"" + desc + "\"?\nStock actual: " + Formato.numero(ins.getStockActual()));
        if (texto == null) return;   // canceló

        try {
            double cantidad = Formato.aNumero(texto);
            boolean ok = sumar ? ins.aumentarStock(cantidad) : ins.descontarStock(cantidad);
            if (!ok) {
                JOptionPane.showMessageDialog(v, sumar
                        ? "La cantidad debe ser mayor que 0."
                        : "La cantidad debe ser mayor que 0 y no puede superar el stock actual ("
                          + Formato.numero(ins.getStockActual()) + ").");
                return;
            }
            insumoRepo.actualizar(desc, ins);
            limpiarFormularioInsumo(v);
            cargarInsumos(v);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(v, "Ingrese un número válido.");
        }
    }

    private static void cargarInsumos(VentanaInsumos v) {
        v.modeloInsumos.setRowCount(0);
        for (Insumo i : insumoRepo.buscar(filtroInsumos, v.chkSoloReponer.isSelected())) {
            String proveedores = String.join(", ", proveedorRepo.proveedoresDe(i.getDescripcion()));
            v.modeloInsumos.addRow(new Object[]{
                i.getDescripcion(),
                Formato.numero(i.getStockActual()),
                Formato.numero(i.getStockMinimo()),
                i.necesitaReposicion() ? "REPONER" : "OK",
                proveedores});
        }
    }

    // Vuelve a llenar el combo con los proveedores registrados y conserva la selección
    private static void recargarProveedoresEnCombo(VentanaInsumos v) {
        Object seleccionado = v.cmbProveedor.getSelectedItem();
        v.cmbProveedor.removeAllItems();
        v.cmbProveedor.addItem(SIN_PROVEEDOR);
        for (String nombre : proveedorRepo.nombres()) {
            v.cmbProveedor.addItem(nombre);
        }
        if (seleccionado != null) v.cmbProveedor.setSelectedItem(seleccionado);
    }

    // null si está en "(sin proveedor)"
    private static String proveedorSeleccionado(VentanaInsumos v) {
        Object sel = v.cmbProveedor.getSelectedItem();
        if (sel == null || sel.toString().equals(SIN_PROVEEDOR)) return null;
        return sel.toString();
    }

    // Valida el formulario y devuelve el Insumo (o null si hay un error, ya avisado)
    private static Insumo leerFormularioInsumo(VentanaInsumos v) {
        String desc = v.txtDescripcion.getText().trim();
        if (desc.isEmpty()) {
            JOptionPane.showMessageDialog(v, "Ingrese la descripción del insumo.");
            return null;
        }
        try {
            double stock = Formato.aNumero(v.txtStockActual.getText());
            double minimo = Formato.aNumero(v.txtStockMinimo.getText());
            String c = v.txtCosto.getText().trim();
            double costo = c.isEmpty() ? 0 : Formato.aNumero(c);
            if (stock < 0 || minimo < 0 || costo < 0) throw new NumberFormatException();
            return new Insumo(desc, stock, minimo, costo);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(v, "Stock, mínimo y costo deben ser números mayores o iguales a 0.");
            return null;
        }
    }

    private static void limpiarFormularioInsumo(VentanaInsumos v) {
        v.txtDescripcion.setText("");
        v.txtStockActual.setText("");
        v.txtStockMinimo.setText("");
        v.txtCosto.setText("");
        v.cmbProveedor.setSelectedItem(SIN_PROVEEDOR);
        v.tblInsumos.clearSelection();
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

    // Receta que se está armando en pantalla (todavía no guardada)
    private static Producto borradorReceta = new Producto("", 0);
    // Nombre del producto que se está editando (null = producto nuevo, aún sin guardar)
    private static String productoEnEdicion = null;

    public static void inicializarVentanaProductos(VentanaProductos v, String rol) {
        productoRepo.sembrarSiHaceFalta();
        borradorReceta = new Producto("", 0);
        productoEnEdicion = null;
        cargarProductos(v, "");
        mostrarReceta(v);

        // BUSCAR: con el botón o presionando Enter dentro del campo Nombre
        v.btnBuscar.addActionListener(e -> {
            soltarProductoEnEdicion(v);
            cargarProductos(v, v.txtNombre.getText().trim());
        });
        v.txtNombre.addActionListener(e -> {
            soltarProductoEnEdicion(v);
            cargarProductos(v, v.txtNombre.getText().trim());
        });

        // ACTUALIZAR LISTA: vuelve a leer todo (por si el administrador agregó productos nuevos)
        v.btnRefrescar.addActionListener(e -> {
            soltarProductoEnEdicion(v);
            v.txtNombre.setText("");
            cargarProductos(v, "");
            recargarInsumosEnCombo(v);
        });

        v.btnLimpiar.addActionListener(e -> {
            limpiarFormularioProducto(v);
            cargarProductos(v, "");
        });

        v.btnCancelar.addActionListener(e -> {
            v.dispose();
            abrirVentanaPrincipal(rolActual);
        });

        // Todo lo siguiente es solo del Administrador
        if (!rolActual.equals("Administrador")) return;

        recargarInsumosEnCombo(v);
        v.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowActivated(java.awt.event.WindowEvent e) {
                recargarInsumosEnCombo(v);   // por si se registraron insumos nuevos
            }
        });

        // Al elegir un producto se cargan sus datos y su receta
        v.tblProductos.getSelectionModel().addListSelectionListener(e -> {
            int fila = v.tblProductos.getSelectedRow();
            if (e.getValueIsAdjusting() || fila < 0) return;
            Producto p = productoRepo.obtener(v.modeloProductos.getValueAt(fila, 0).toString());
            if (p == null) return;
            borradorReceta = p;
            productoEnEdicion = p.getNombre();
            v.txtNombre.setText(p.getNombre());
            v.txtPrecioVenta.setText(Formato.numero(p.getPrecioVenta()));
            mostrarReceta(v);
        });

        // AGREGAR INGREDIENTE: si el insumo ya está en la receta, se suma la cantidad
        v.btnAgregarIngrediente.addActionListener(e -> {
            Object sel = v.cmbInsumo.getSelectedItem();
            if (sel == null) {
                JOptionPane.showMessageDialog(v, "Primero registre insumos en la ventana Insumos.");
                return;
            }
            double cant;
            try {
                cant = Formato.aNumero(v.txtCantidad.getText());
                if (cant <= 0) throw new NumberFormatException();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(v, "La cantidad debe ser un número mayor que 0.");
                return;
            }
            Insumo insumo = insumoRepo.buscarPorDescripcion(sel.toString());
            if (insumo == null) {
                JOptionPane.showMessageDialog(v, "Ese insumo ya no existe. Actualice la lista.");
                recargarInsumosEnCombo(v);
                return;
            }
            borradorReceta.agregarIngrediente(insumo, cant);
            guardarRecetaSiHayProducto(v);
            mostrarReceta(v);
            v.txtCantidad.setText("");
        });

        // QUITAR INGREDIENTE
        v.btnQuitarIngrediente.addActionListener(e -> {
            int fila = v.tblIngredientes.getSelectedRow();
            if (fila < 0) {
                JOptionPane.showMessageDialog(v, "Seleccione un ingrediente de la receta para quitarlo.");
                return;
            }
            borradorReceta.quitarIngrediente(fila);
            guardarRecetaSiHayProducto(v);
            mostrarReceta(v);
        });

        // GUARDAR
        v.btnGuardar.addActionListener(e -> {
            Producto nuevo = leerFormularioProducto(v);
            if (nuevo == null) return;
            if (!productoRepo.guardar(nuevo)) {
                JOptionPane.showMessageDialog(v, "Ya existe un producto con ese nombre.");
                return;
            }
            limpiarFormularioProducto(v);
            cargarProductos(v, "");
            JOptionPane.showMessageDialog(v, "¡Producto guardado!");
        });

        // ACTUALIZAR (nombre, precio y receta del producto seleccionado)
        v.btnActualizar.addActionListener(e -> {
            int fila = v.tblProductos.getSelectedRow();
            if (fila < 0) {
                JOptionPane.showMessageDialog(v, "Seleccione un producto de la tabla.");
                return;
            }
            String original = v.modeloProductos.getValueAt(fila, 0).toString();
            Producto nuevo = leerFormularioProducto(v);
            if (nuevo == null) return;
            if (!productoRepo.actualizar(original, nuevo)) {
                JOptionPane.showMessageDialog(v, "Ya existe otro producto con ese nombre.");
                return;
            }
            limpiarFormularioProducto(v);
            cargarProductos(v, "");
            JOptionPane.showMessageDialog(v, "¡Producto actualizado!");
        });

        // ELIMINAR
        v.btnEliminar.addActionListener(e -> {
            int fila = v.tblProductos.getSelectedRow();
            if (fila < 0) {
                JOptionPane.showMessageDialog(v, "Seleccione un producto de la tabla.");
                return;
            }
            String nombre = v.modeloProductos.getValueAt(fila, 0).toString();
            int r = JOptionPane.showConfirmDialog(v, "¿Eliminar el producto \"" + nombre + "\" y su receta?",
                    "Confirmar", JOptionPane.YES_NO_OPTION);
            if (r != JOptionPane.YES_OPTION) return;
            productoRepo.eliminar(nombre);
            limpiarFormularioProducto(v);
            cargarProductos(v, "");
        });
    }

    // --- Auxiliares de Productos ---

    // Llena la tabla con los productos cuyo nombre contenga el texto. Texto vacío = todos.
    private static void cargarProductos(VentanaProductos v, String texto) {
        v.modeloProductos.setRowCount(0);
        for (Producto p : productoRepo.buscar(texto)) {
            v.modeloProductos.addRow(new Object[]{p.getNombre(), Formato.numero(p.getPrecioVenta())});
        }
    }

    // La tabla de ingredientes es un reflejo de la receta que se está armando
    private static void mostrarReceta(VentanaProductos v) {
        v.lblRecetaHeader.setText(productoEnEdicion != null
                ? "Receta de: " + productoEnEdicion + "   (los cambios se guardan al instante)"
                : "Receta del producto NUEVO   (se guarda cuando presione Guardar)");
        v.modeloIngredientes.setRowCount(0);
        for (Ingrediente ing : borradorReceta.getIngredientes()) {
            v.modeloIngredientes.addRow(new Object[]{
                ing.getInsumo().getDescripcion(), Formato.numero(ing.getCantidad())});
        }
    }

    private static void recargarInsumosEnCombo(VentanaProductos v) {
        Object seleccionado = v.cmbInsumo.getSelectedItem();
        v.cmbInsumo.removeAllItems();
        for (Insumo i : insumoRepo.listar()) {
            v.cmbInsumo.addItem(i.getDescripcion());
        }
        if (seleccionado != null) v.cmbInsumo.setSelectedItem(seleccionado);
    }

    // Valida el formulario y arma el Producto con la receta del borrador (null si hay error, ya avisado)
    private static Producto leerFormularioProducto(VentanaProductos v) {
        String nombre = v.txtNombre.getText().trim();
        if (nombre.isEmpty()) {
            JOptionPane.showMessageDialog(v, "Ingrese el nombre del producto.");
            return null;
        }
        double precio;
        try {
            precio = Formato.aNumero(v.txtPrecioVenta.getText());
            if (precio <= 0 || precio != Math.rint(precio)) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(v, "El precio debe ser un número entero mayor que 0 (pesos).");
            return null;
        }
        Producto p = new Producto(nombre, precio);
        for (Ingrediente ing : borradorReceta.getIngredientes()) {
            p.agregarIngrediente(ing.getInsumo(), ing.getCantidad());
        }
        return p;
    }

    // Si hay un producto elegido de la tabla, su receta se guarda en el archivo en cuanto cambia
    private static void guardarRecetaSiHayProducto(VentanaProductos v) {
        if (productoEnEdicion == null) return;
        if (!productoRepo.actualizar(productoEnEdicion, borradorReceta)) {
            JOptionPane.showMessageDialog(v, "No se pudo guardar la receta: el producto ya no existe.");
        }
    }

    // Deja de editar el producto elegido (por ejemplo al buscar otro)
    private static void soltarProductoEnEdicion(VentanaProductos v) {
        if (productoEnEdicion == null) return;
        productoEnEdicion = null;
        borradorReceta = new Producto("", 0);
        v.tblProductos.clearSelection();
        mostrarReceta(v);
    }

    private static void limpiarFormularioProducto(VentanaProductos v) {
        productoEnEdicion = null;
        borradorReceta = new Producto("", 0);
        v.txtNombre.setText("");
        v.txtPrecioVenta.setText("");
        v.txtCantidad.setText("");
        v.tblProductos.clearSelection();
        mostrarReceta(v);
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
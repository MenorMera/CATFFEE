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

public class ControladorVentas {

    private static final InsumoRepositorio insumoRepo = new InsumoRepositorio();
    private static final ProductoRepositorio productoRepo = new ProductoRepositorio();
    private static VentanaVenta ventanaVentaInstance;
    private static Cliente clienteSeleccionado = null;   // cliente de la venta en curso (null = público general)

    public static void abrirVentanaVenta() {
        if (ventanaVentaInstance == null || !ventanaVentaInstance.isVisible()) {
            ventanaVentaInstance = new VentanaVenta();
            inicializarVentanaVenta(ventanaVentaInstance);
        }
        ventanaVentaInstance.txtCedulaCliente.setText(ControladorCatffee.tempCedula);
        ventanaVentaInstance.lblCliente.setText(ControladorCatffee.tempCliente.isEmpty() ? "PÚBLICO GENERAL (USUARIO NULL)" : ControladorCatffee.tempCliente);
        ventanaVentaInstance.setVisible(true);
        ventanaVentaInstance.toFront();
        if (ControladorCatffee.ventanaPrincipalInstance != null) ControladorCatffee.ventanaPrincipalInstance.dispose();
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
                ControladorCatffee.tempCliente = "";
                ControladorCatffee.tempCedula = "";
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
                        ControladorCatffee.tempCedula = ced;
                        ControladorCatffee.tempCliente = p[0] + " (C.C. " + ced + ")";
                        clienteSeleccionado = new Cliente(ced, p[0], "", p[1]);
                        ventana.lblCliente.setText(ControladorCatffee.tempCliente);
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
                String factura = GeneradorFactura.generarFacturaTxt(ventana.lblCliente.getText(), "Empleado (" + ControladorCatffee.rolActual + ")", ventana.tblCarrito, total);

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
                ControladorCatffee.tempCedula = ventana.txtCedulaCliente.getText();
                ventana.dispose();
                ControladorCatffee.abrirVentanaPrincipal(ControladorCatffee.rolActual);
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
        Venta venta = new Venta(cliente, new Vendedor(ControladorCatffee.nombreActual, ControladorCatffee.nombreActual, "-"));

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
            necesario.merge(clave, ing.getInsumo().aUnidadCompra(ing.getCantidad()) * cantidad, Double::sum);
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
}
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

public class ControladorCompras {

    private static final InsumoRepositorio insumoRepo = new InsumoRepositorio();
    private static final ProveedorRepositorio proveedorRepo = new ProveedorRepositorio();
    private static final CompraRepositorio compraRepo = new CompraRepositorio();
    private static VentanaCompras ventanaComprasInstance;

    public static void abrirVentanaCompras() {
        if (ventanaComprasInstance == null || !ventanaComprasInstance.isVisible()) {
            ventanaComprasInstance = new VentanaCompras();
            inicializarVentanaCompras(ventanaComprasInstance);
        }
        ventanaComprasInstance.setVisible(true);
        ventanaComprasInstance.toFront();
        if (ControladorCatffee.ventanaPrincipalInstance != null) ControladorCatffee.ventanaPrincipalInstance.dispose();
    }

        // Evita que los combos reaccionen solos mientras se están llenando
    private static boolean cargandoCompra = false;

    public static void inicializarVentanaCompras(VentanaCompras v) {
        v.modeloCompra.setRowCount(0);
        v.lblTotal.setText("Total: $0");
        recargarCombosCompra(v);

        // Si se registra un proveedor o un insumo en otra ventana y se vuelve a esta, los combos se actualizan solos
        v.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowActivated(java.awt.event.WindowEvent e) {
                recargarCombosCompra(v);
            }
        });

        // Al cambiar de proveedor, el combo de insumos muestra solo los de su catálogo
        v.cmbProveedor.addActionListener(e -> {
            if (!cargandoCompra) cargarInsumosDelProveedor(v);
        });

        // Al elegir un insumo, el precio sugerido es su costo actual
        v.cmbInsumo.addActionListener(e -> {
            if (!cargandoCompra) sugerirPrecioCompra(v);
        });

        // AGREGAR: pone el insumo en la tabla de la compra (todavía no toca el inventario)
        v.btnAgregar.addActionListener(e -> {
            if (v.cmbProveedor.getSelectedItem() == null) {
                JOptionPane.showMessageDialog(v, "Primero registre un proveedor en la ventana Proveedores.");
                return;
            }
            String desc = (String) v.cmbInsumo.getSelectedItem();
            if (desc == null) {
                JOptionPane.showMessageDialog(v, "Ese proveedor no tiene insumos.\n"
                        + "Asígnele uno en la ventana Insumos (elíjalo en el combo Proveedor al guardar el insumo).");
                return;
            }
            double cantidad, precio;
            try {
                cantidad = Formato.aNumero(v.txtCantidad.getText());
                precio = Formato.aNumero(v.txtPrecio.getText());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(v, "Cantidad y precio deben ser números.");
                return;
            }
            if (!Double.isFinite(cantidad) || !Double.isFinite(precio) || cantidad <= 0 || precio < 0) {
                JOptionPane.showMessageDialog(v, "La cantidad debe ser mayor que 0 y el precio no puede ser negativo.");
                return;
            }
            v.modeloCompra.addRow(new Object[]{desc, Formato.numero(cantidad),
                Formato.numero(precio), Formato.numero(cantidad * precio)});
            v.cmbProveedor.setEnabled(false); // una compra es de un solo proveedor
            v.txtCantidad.setText("");
            actualizarTotalCompra(v);
        });

        // QUITAR: saca la fila seleccionada de la compra
        v.btnQuitar.addActionListener(e -> {
            int fila = v.tblCompra.getSelectedRow();
            if (fila < 0) {
                JOptionPane.showMessageDialog(v, "Seleccione un insumo de la compra para quitarlo.");
                return;
            }
            v.modeloCompra.removeRow(fila);
            if (v.modeloCompra.getRowCount() == 0) v.cmbProveedor.setEnabled(true);
            actualizarTotalCompra(v);
        });

        // CONFIRMAR: aquí la compra se conecta con los insumos (suma al stock) y se guarda
        v.btnConfirmarCompra.addActionListener(e -> {
            if (v.modeloCompra.getRowCount() == 0) {
                JOptionPane.showMessageDialog(v, "La compra está vacía.");
                return;
            }
            String nombreProveedor = (String) v.cmbProveedor.getSelectedItem();
            Proveedor proveedor = null;
            for (Proveedor p : proveedorRepo.listar()) {
                if (p.getNombre().equalsIgnoreCase(nombreProveedor)) proveedor = p;
            }
            if (proveedor == null) {
                JOptionPane.showMessageDialog(v, "El proveedor ya no existe.");
                return;
            }

            Compra compra = new Compra(new Administrador(ControladorCatffee.nombreActual, ControladorCatffee.nombreActual, ""), proveedor);
            // Un solo objeto por insumo: si el mismo insumo está en dos filas, ambas suman al mismo stock
            java.util.LinkedHashMap<String, Insumo> insumos = new java.util.LinkedHashMap<>();
            java.util.HashMap<String, Double> stockAntes = new java.util.HashMap<>();
            for (int i = 0; i < v.modeloCompra.getRowCount(); i++) {
                String desc = v.modeloCompra.getValueAt(i, 0).toString();
                Insumo insumo = insumos.get(desc);
                if (insumo == null) {
                    insumo = insumoRepo.buscarPorDescripcion(desc);
                    if (insumo == null) {
                        JOptionPane.showMessageDialog(v, "El insumo \"" + desc + "\" ya no existe. Quítelo de la compra.");
                        return;
                    }
                    insumos.put(desc, insumo);
                    stockAntes.put(desc, insumo.getStockActual());
                }
                double cantidad = Formato.aNumero(v.modeloCompra.getValueAt(i, 1).toString());
                double precio = Formato.aNumero(v.modeloCompra.getValueAt(i, 2).toString());
                compra.agregarInsumo(insumo, cantidad, precio);
            }

            if (!compra.confirmar()) { // suma la cantidad comprada al stock de cada insumo
                JOptionPane.showMessageDialog(v, "No se pudo confirmar la compra.");
                return;
            }
            for (Insumo insumo : insumos.values()) {
                insumoRepo.actualizar(insumo.getDescripcion(), insumo); // guarda el stock nuevo
            }
            compraRepo.guardar(compra);

            StringBuilder resumen = new StringBuilder("¡Compra registrada! Stock actualizado:\n");
            for (Insumo insumo : insumos.values()) {
                resumen.append("• ").append(insumo.getDescripcion()).append(": ")
                       .append(Formato.numero(stockAntes.get(insumo.getDescripcion())))
                       .append(" → ").append(Formato.numero(insumo.getStockActual())).append("\n");
            }
            resumen.append("Total: $").append(Formato.numero(compra.getTotal()));

            v.modeloCompra.setRowCount(0);
            v.cmbProveedor.setEnabled(true);
            actualizarTotalCompra(v);
            JOptionPane.showMessageDialog(v, resumen.toString());
        });

        ControladorCatffee.agregarAccion(v.btnCancelar, e -> {
            v.dispose();
            ControladorCatffee.abrirVentanaPrincipal(ControladorCatffee.rolActual);
        });
    }

    // --- Auxiliares de Compras ---

    // Llena el combo con los proveedores registrados (conserva el elegido si sigue existiendo)
    private static void recargarCombosCompra(VentanaCompras v) {
        cargandoCompra = true;
        try {
            Object antes = v.cmbProveedor.getSelectedItem();
            v.cmbProveedor.removeAllItems();
            for (String nombre : proveedorRepo.nombres()) {
                v.cmbProveedor.addItem(nombre);
            }
            if (antes != null) v.cmbProveedor.setSelectedItem(antes);
        } finally {
            cargandoCompra = false;
        }
        cargarInsumosDelProveedor(v);
    }

    // El combo de insumos muestra solo los del catálogo del proveedor elegido
    private static void cargarInsumosDelProveedor(VentanaCompras v) {
        String proveedor = (String) v.cmbProveedor.getSelectedItem();
        Object antes = v.cmbInsumo.getSelectedItem();
        cargandoCompra = true;
        try {
            v.cmbInsumo.removeAllItems();
            if (proveedor != null) {
                for (String desc : proveedorRepo.insumosDe(proveedor)) {
                    Insumo insumo = insumoRepo.buscarPorDescripcion(desc);
                    if (insumo != null) v.cmbInsumo.addItem(insumo.getDescripcion());
                }
            }
            if (antes != null) v.cmbInsumo.setSelectedItem(antes);
        } finally {
            cargandoCompra = false;
        }
        Object ahora = v.cmbInsumo.getSelectedItem();
        if (ahora != null && !ahora.equals(antes)) sugerirPrecioCompra(v);
    }

    private static void sugerirPrecioCompra(VentanaCompras v) {
        String desc = (String) v.cmbInsumo.getSelectedItem();
        Insumo insumo = desc == null ? null : insumoRepo.buscarPorDescripcion(desc);
        if (insumo != null) v.txtPrecio.setText(Formato.numero(insumo.getPrecioCosto()));
    }

    private static void actualizarTotalCompra(VentanaCompras v) {
        double total = 0;
        for (int i = 0; i < v.modeloCompra.getRowCount(); i++) {
            total += Formato.aNumero(v.modeloCompra.getValueAt(i, 3).toString());
        }
        v.lblTotal.setText("Total: $" + Formato.numero(total));
    }
}

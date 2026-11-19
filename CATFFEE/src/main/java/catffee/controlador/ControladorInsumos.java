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

public class ControladorInsumos {

    private static final InsumoRepositorio insumoRepo = new InsumoRepositorio();
    private static final ProveedorRepositorio proveedorRepo = new ProveedorRepositorio();
    private static final ProductoRepositorio productoRepo = new ProductoRepositorio();
    private static VentanaInsumos ventanaInsumosInstance;

    public static void abrirVentanaInsumos() {
        if (ventanaInsumosInstance == null || !ventanaInsumosInstance.isVisible()) {
            ventanaInsumosInstance = new VentanaInsumos();
            inicializarVentanaInsumos(ventanaInsumosInstance);
        }
        ventanaInsumosInstance.setVisible(true);
        ventanaInsumosInstance.toFront();
        if (ControladorCatffee.ventanaPrincipalInstance != null) ControladorCatffee.ventanaPrincipalInstance.dispose();
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
            ControladorCatffee.abrirVentanaPrincipal(ControladorCatffee.rolActual);
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
}

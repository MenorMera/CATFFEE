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

public class ControladorProductos {

    private static final InsumoRepositorio insumoRepo = new InsumoRepositorio();
    private static final ProductoRepositorio productoRepo = new ProductoRepositorio();
    private static VentanaProductos ventanaProductosInstance;

    public static void abrirVentanaProductos(String rol) {
        if (ventanaProductosInstance == null || !ventanaProductosInstance.isVisible()) {
            ventanaProductosInstance = new VentanaProductos(ControladorCatffee.rolActual);
            inicializarVentanaProductos(ventanaProductosInstance, ControladorCatffee.rolActual);
        }
        ventanaProductosInstance.setVisible(true);
        ventanaProductosInstance.toFront();
        if (ControladorCatffee.ventanaPrincipalInstance != null) ControladorCatffee.ventanaPrincipalInstance.dispose();
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
            ControladorCatffee.abrirVentanaPrincipal(ControladorCatffee.rolActual);
        });

        // Todo lo siguiente es solo del Administrador
        if (!ControladorCatffee.rolActual.equals("Administrador")) return;

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
}

package catffee.vista;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class VentanaProductos extends JFrame {
    public JTextField txtNombre, txtPrecioVenta, txtCantidad;
    public JComboBox<String> cmbInsumo;
    public JButton btnAgregarIngrediente, btnGuardar, btnActualizar, btnEliminar, btnLimpiar;
    public JTable tblIngredientes, tblProductos;
    public DefaultTableModel modeloIngredientes, modeloProductos;

    public VentanaProductos(String rol) {
        setTitle("CATFFEE · Productos");
        setSize(650, 620);
        setLocationRelativeTo(null);
        setLayout(null);

        JLabel lblHeader = new JLabel("CATFFEE · Productos");
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblHeader.setBounds(25, 15, 300, 25);
        add(lblHeader);

        JLabel lblNom = new JLabel("Nombre:");
        lblNom.setBounds(25, 55, 60, 25);
        add(lblNom);

        txtNombre = new JTextField();
        txtNombre.setBounds(85, 55, 180, 25);
        add(txtNombre);

        JLabel lblPrecio = new JLabel("Precio venta:");
        lblPrecio.setBounds(290, 55, 85, 25);
        add(lblPrecio);

        txtPrecioVenta = new JTextField();
        txtPrecioVenta.setBounds(380, 55, 100, 25);
        add(txtPrecioVenta);

        JLabel lblRecetaHeader = new JLabel("Ingredientes (receta)");
        lblRecetaHeader.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblRecetaHeader.setBounds(25, 95, 200, 20);
        add(lblRecetaHeader);

        JLabel lblIns = new JLabel("Insumo:");
        lblIns.setBounds(25, 125, 55, 25);
        add(lblIns);

        cmbInsumo = new JComboBox<>(new String[]{"Café molido", "Leche", "Azúcar", "Chocolate"});
        cmbInsumo.setBounds(80, 125, 150, 25);
        add(cmbInsumo);

        JLabel lblCant = new JLabel("Cant.:");
        lblCant.setBounds(245, 125, 45, 25);
        add(lblCant);

        txtCantidad = new JTextField();
        txtCantidad.setBounds(285, 125, 50, 25);
        add(txtCantidad);

        btnAgregarIngrediente = new JButton("Agregar ingrediente");
        btnAgregarIngrediente.setBounds(350, 123, 160, 28);
        add(btnAgregarIngrediente);

        // Tabla ingredientes
        modeloIngredientes = new DefaultTableModel(new String[]{"Insumo", "Cantidad"}, 0);
        modeloIngredientes.addRow(new Object[]{"Café molido", 18});
        modeloIngredientes.addRow(new Object[]{"Leche", 200});
        tblIngredientes = new JTable(modeloIngredientes);
        JScrollPane scroll1 = new JScrollPane(tblIngredientes);
        scroll1.setBounds(25, 160, 580, 90);
        add(scroll1);

        // Tabla productos
        modeloProductos = new DefaultTableModel(new String[]{"Nombre", "Precio venta"}, 0);
        modeloProductos.addRow(new Object[]{"Capuchino", 8500});
        modeloProductos.addRow(new Object[]{"Cheesecake", 9000});
        tblProductos = new JTable(modeloProductos);
        JScrollPane scroll2 = new JScrollPane(tblProductos);
        scroll2.setBounds(25, 265, 580, 160);
        add(scroll2);

        // Botones de acción inferior
        btnGuardar = new JButton("Guardar");
        btnGuardar.setBounds(25, 445, 110, 35);
        add(btnGuardar);

        btnActualizar = new JButton("Actualizar");
        btnActualizar.setBounds(145, 445, 110, 35);
        add(btnActualizar);

        btnEliminar = new JButton("Eliminar");
        btnEliminar.setBounds(265, 445, 110, 35);
        add(btnEliminar);

        btnLimpiar = new JButton("Limpiar");
        btnLimpiar.setBounds(385, 445, 110, 35);
        add(btnLimpiar);

        // Restricción: Si entra el Vendedor, solo consulta (se bloquean edición y botones de modificación)
        if (rol.equals("Vendedor")) {
            txtNombre.setEnabled(false);
            txtPrecioVenta.setEnabled(false);
            txtCantidad.setEnabled(false);
            cmbInsumo.setEnabled(false);
            btnAgregarIngrediente.setEnabled(false);
            btnGuardar.setEnabled(false);
            btnActualizar.setEnabled(false);
            btnEliminar.setEnabled(false);
            btnLimpiar.setEnabled(false);
        }
    }
}
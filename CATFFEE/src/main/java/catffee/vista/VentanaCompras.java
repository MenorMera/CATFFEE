package catffee.vista;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class VentanaCompras extends JFrame {
    public JComboBox<String> cmbProveedor, cmbInsumo;
    public JTextField txtCantidad, txtPrecio;
    public JButton btnAgregar, btnConfirmarCompra, btnCancelar;
    public JLabel lblTotal;
    public JTable tblCompra;
    public DefaultTableModel modeloCompra;

    public VentanaCompras() {
        setTitle("CATFFEE · Nueva compra");
        setSize(650, 520);
        setLocationRelativeTo(null);
        setLayout(null);

        JLabel lblHeader = new JLabel("CATFFEE · Nueva compra");
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblHeader.setBounds(25, 15, 300, 25);
        add(lblHeader);

        JLabel lblProv = new JLabel("Proveedor:");
        lblProv.setBounds(25, 60, 75, 25);
        add(lblProv);

        cmbProveedor = new JComboBox<>(new String[]{"Distribuidora Café S.A.S.", "Lácteos del Valle", "Comercializadora Global"});
        cmbProveedor.setBounds(105, 60, 300, 25);
        add(cmbProveedor);

        JLabel lblIns = new JLabel("Insumo:");
        lblIns.setBounds(25, 105, 55, 25);
        add(lblIns);

        cmbInsumo = new JComboBox<>(new String[]{"Café molido", "Leche", "Azúcar"});
        cmbInsumo.setBounds(85, 105, 150, 25);
        add(cmbInsumo);

        JLabel lblCant = new JLabel("Cant.:");
        lblCant.setBounds(250, 105, 45, 25);
        add(lblCant);

        txtCantidad = new JTextField();
        txtCantidad.setBounds(290, 105, 60, 25);
        add(txtCantidad);

        JLabel lblPrecio = new JLabel("Precio:");
        lblPrecio.setBounds(365, 105, 50, 25);
        add(lblPrecio);

        txtPrecio = new JTextField();
        txtPrecio.setBounds(415, 105, 80, 25);
        add(txtPrecio);

        btnAgregar = new JButton("Agregar");
        btnAgregar.setBounds(510, 103, 95, 28);
        add(btnAgregar);

        // Tabla de compra
        modeloCompra = new DefaultTableModel(new String[]{"Insumo", "Cant.", "Precio", "Subtotal"}, 0);
        modeloCompra.addRow(new Object[]{"Leche", 20, 3200, 64000});
        modeloCompra.addRow(new Object[]{"Azúcar", 10, 2800, 28000});

        tblCompra = new JTable(modeloCompra);
        JScrollPane scroll = new JScrollPane(tblCompra);
        scroll.setBounds(25, 150, 580, 170);
        add(scroll);

        lblTotal = new JLabel("Total: $92000");
        lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblTotal.setBounds(475, 335, 150, 30);
        add(lblTotal);

        btnConfirmarCompra = new JButton("Confirmar compra");
        btnConfirmarCompra.setBounds(330, 395, 140, 35);
        add(btnConfirmarCompra);

        btnCancelar = new JButton("Cancelar");
        btnCancelar.setBounds(480, 395, 125, 35);
        add(btnCancelar);

        btnCancelar.addActionListener(e -> dispose());
    }
}
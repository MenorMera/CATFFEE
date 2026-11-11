package catffee.vista;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class VentanaVenta extends JFrame {
    public JTextField txtTelefonoCliente, txtCantidad;
    public JButton btnBuscarCliente, btnSinCliente, btnAgregar, btnQuitar, btnConfirmar, btnCancelar;
    public JLabel lblCliente, lblTotal;
    public JTable tblCarrito;
    public JComboBox<String> cmbProducto;
    public DefaultTableModel modeloCarrito;

    public VentanaVenta() {
        setTitle("CATFFEE · Nueva venta");
        setSize(650, 520);
        setLocationRelativeTo(null);
        setLayout(null);

        JLabel lblHeader = new JLabel("CATFFEE · Nueva venta");
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblHeader.setBounds(25, 15, 300, 25);
        add(lblHeader);

        JLabel lblTel = new JLabel("Cliente:");
        lblTel.setBounds(25, 60, 60, 25);
        add(lblTel);

        txtTelefonoCliente = new JTextField();
        txtTelefonoCliente.setBounds(85, 60, 140, 25);
        add(txtTelefonoCliente);

        btnBuscarCliente = new JButton("Buscar");
        btnBuscarCliente.setBounds(235, 60, 85, 25);
        add(btnBuscarCliente);

        btnSinCliente = new JButton("Sin cliente");
        btnSinCliente.setBounds(330, 60, 110, 25);
        add(btnSinCliente);

        lblCliente = new JLabel("Ana Pérez (Cliente frecuente)");
        lblCliente.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblCliente.setForeground(new Color(0, 100, 0));
        lblCliente.setBounds(85, 90, 300, 20);
        add(lblCliente);

        JLabel lblProd = new JLabel("Producto:");
        lblProd.setBounds(25, 125, 65, 25);
        add(lblProd);

        cmbProducto = new JComboBox<>(new String[]{"Capuchino - $8500", "Cheesecake - $9000", "Latte Macchiato - $7500"});
        cmbProducto.setBounds(95, 125, 180, 25);
        add(cmbProducto);

        JLabel lblCant = new JLabel("Cant.:");
        lblCant.setBounds(285, 125, 45, 25);
        add(lblCant);

        txtCantidad = new JTextField("1");
        txtCantidad.setBounds(330, 125, 50, 25);
        add(txtCantidad);

        btnAgregar = new JButton("Agregar al carrito");
        btnAgregar.setBounds(395, 123, 145, 28);
        add(btnAgregar);

        // Tabla del carrito
        modeloCarrito = new DefaultTableModel(new String[]{"Producto", "Cant.", "Precio", "Subtotal"}, 0);
        modeloCarrito.addRow(new Object[]{"Capuchino", 2, 8500, 17000});
        modeloCarrito.addRow(new Object[]{"Cheesecake", 1, 9000, 9000});
        
        tblCarrito = new JTable(modeloCarrito);
        JScrollPane scroll = new JScrollPane(tblCarrito);
        scroll.setBounds(25, 165, 580, 180);
        add(scroll);

        btnQuitar = new JButton("Quitar");
        btnQuitar.setBounds(25, 355, 80, 30);
        add(btnQuitar);

        lblTotal = new JLabel("Total: $26000");
        lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblTotal.setBounds(470, 355, 150, 30);
        add(lblTotal);

        btnConfirmar = new JButton("Confirmar venta");
        btnConfirmar.setBounds(320, 415, 135, 35);
        add(btnConfirmar);

        btnCancelar = new JButton("Cancelar");
        btnCancelar.setBounds(470, 415, 135, 35);
        add(btnCancelar);

        btnCancelar.addActionListener(e -> dispose());
    }
}
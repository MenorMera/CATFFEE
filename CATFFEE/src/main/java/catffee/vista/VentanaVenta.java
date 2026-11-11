package catffee.vista;

import catffee.controlador.ControladorCatffee;
import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class VentanaVenta extends JFrame {
    public JTextField txtTelefonoCliente, txtCantidad;
    public JButton btnBuscarCliente, btnSinCliente, btnAgregar, btnQuitar, btnConfirmar, btnCancelar;
    public JLabel lblCliente, lblTotal;
    public JTable tblCarrito;
    public JComboBox<String> cmbProducto;
    public DefaultTableModel modeloCarrito;

    public VentanaVenta() {
        setTitle("CATFFEE · Nueva Venta");
        setSize(650, 520);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(null);
        getContentPane().setBackground(new Color(245, 247, 250));

        JLabel lblHeader = new JLabel("CATFFEE · Registro de Venta");
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 16));
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

        btnSinCliente = new JButton("Público General");
        btnSinCliente.setBounds(330, 60, 140, 25);
        add(btnSinCliente);

        lblCliente = new JLabel("PÚBLICO GENERAL (USUARIO NULL)");
        lblCliente.setFont(new Font("Segoe UI", Font.BOLD | Font.ITALIC, 12));
        lblCliente.setForeground(new Color(100, 100, 100));
        lblCliente.setBounds(85, 90, 350, 20);
        add(lblCliente);

        JLabel lblProd = new JLabel("Producto:");
        lblProd.setBounds(25, 125, 65, 25);
        add(lblProd);

        cmbProducto = new JComboBox<>(new String[]{
            "Capuchino - 8500", 
            "Cheesecake - 9000", 
            "Latte Macchiato - 7500",
            "Café Americano - 5000",
            "Tarta de Manzana - 8000"
        });
        cmbProducto.setBounds(95, 125, 180, 25);
        add(cmbProducto);

        JLabel lblCant = new JLabel("Cant.:");
        lblCant.setBounds(285, 125, 45, 25);
        add(lblCant);

        txtCantidad = new JTextField("1");
        txtCantidad.setBounds(330, 125, 50, 25);
        add(txtCantidad);

        btnAgregar = new JButton("Agregar");
        btnAgregar.setBounds(395, 123, 100, 28);
        add(btnAgregar);

        modeloCarrito = new DefaultTableModel(new String[]{"Producto", "Cant.", "Precio", "Subtotal"}, 0);
        tblCarrito = new JTable(modeloCarrito);
        JScrollPane scroll = new JScrollPane(tblCarrito);
        scroll.setBounds(25, 165, 580, 180);
        add(scroll);

        btnQuitar = new JButton("Quitar");
        btnQuitar.setBounds(25, 355, 90, 30);
        add(btnQuitar);

        lblTotal = new JLabel("Total: $0");
        lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTotal.setForeground(new Color(20, 100, 20));
        lblTotal.setBounds(470, 355, 150, 30);
        add(lblTotal);

        btnConfirmar = new JButton("Confirmar Venta");
        btnConfirmar.setBounds(280, 415, 150, 35);
        btnConfirmar.setBackground(new Color(40, 167, 69));
        btnConfirmar.setForeground(Color.WHITE);
        add(btnConfirmar);

        // Botón de retroceso seguro
        btnCancelar = new JButton("← Retroceder");
        btnCancelar.setBounds(440, 415, 165, 35);
        add(btnCancelar);

        ControladorCatffee.inicializarVentanaVenta(this);
    }
}
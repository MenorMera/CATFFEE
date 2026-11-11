package catffee.vista;

import catffee.controlador.ControladorCatffee;
import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class VentanaProveedores extends JFrame {
    public JTextField txtNombre, txtTelefono, txtProducto;
    public JButton btnGuardar, btnCancelar; // Tipado explícito JButton
    public JTable tblProveedores;
    public DefaultTableModel modeloProveedores;

    public VentanaProveedores() {
        setTitle("CATFFEE · Proveedores");
        setSize(580, 440);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(null);

        JLabel lblHeader = new JLabel("CATFFEE · Proveedores");
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblHeader.setBounds(25, 15, 300, 25);
        add(lblHeader);

        JLabel lblNom = new JLabel("Empresa/Nom:");
        lblNom.setBounds(25, 55, 90, 25);
        add(lblNom);

        txtNombre = new JTextField();
        txtNombre.setBounds(115, 55, 160, 25);
        add(txtNombre);

        JLabel lblTel = new JLabel("Teléfono:");
        lblTel.setBounds(290, 55, 60, 25);
        add(lblTel);

        txtTelefono = new JTextField();
        txtTelefono.setBounds(355, 55, 160, 25);
        add(txtTelefono);

        JLabel lblProd = new JLabel("Insumo/Prod:");
        lblProd.setBounds(25, 95, 90, 25);
        add(lblProd);

        txtProducto = new JTextField();
        txtProducto.setBounds(115, 95, 160, 25);
        add(txtProducto);

        modeloProveedores = new DefaultTableModel(new String[]{"Proveedor", "Teléfono", "Suministro"}, 0);
        tblProveedores = new JTable(modeloProveedores);
        JScrollPane sp = new JScrollPane(tblProveedores);
        sp.setBounds(25, 135, 510, 180);
        add(sp);

        btnGuardar = new JButton("Guardar Proveedor");
        btnGuardar.setBounds(25, 335, 160, 35);
        add(btnGuardar);

        btnCancelar = new JButton("← Retroceder");
        btnCancelar.setBounds(395, 335, 140, 35);
        add(btnCancelar);

        ControladorCatffee.inicializarVentanaProveedores(this);
    }
}
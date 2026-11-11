package catffee.vista;

import catffee.controlador.ControladorCatffee;
import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class VentanaClientes extends JFrame {
    public JTextField txtNombre, txtTelefono, txtCedula;
    public JButton btnGuardar, btnCancelar; // Tipado explícito JButton
    public JTable tblClientes;
    public DefaultTableModel modeloClientes;

    public VentanaClientes() {
        setTitle("CATFFEE · Registro de Clientes");
        setSize(580, 440);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(null);

        JLabel lblHeader = new JLabel("CATFFEE · Registro de Clientes");
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblHeader.setBounds(25, 15, 300, 25);
        add(lblHeader);

        JLabel lblNom = new JLabel("Nombre:");
        lblNom.setBounds(25, 55, 60, 25);
        add(lblNom);

        txtNombre = new JTextField();
        txtNombre.setBounds(90, 55, 170, 25);
        add(txtNombre);

        JLabel lblTel = new JLabel("Teléfono:");
        lblTel.setBounds(280, 55, 60, 25);
        add(lblTel);

        txtTelefono = new JTextField();
        txtTelefono.setBounds(345, 55, 170, 25);
        add(txtTelefono);

        JLabel lblCed = new JLabel("Cédula:");
        lblCed.setBounds(25, 95, 60, 25);
        add(lblCed);

        txtCedula = new JTextField();
        txtCedula.setBounds(90, 95, 170, 25);
        add(txtCedula);

        modeloClientes = new DefaultTableModel(new String[]{"Nombre", "Teléfono", "Cédula"}, 0);
        tblClientes = new JTable(modeloClientes);
        JScrollPane sp = new JScrollPane(tblClientes);
        sp.setBounds(25, 135, 510, 180);
        add(sp);

        btnGuardar = new JButton("Guardar Cliente");
        btnGuardar.setBounds(25, 335, 150, 35);
        add(btnGuardar);

        btnCancelar = new JButton("← Retroceder");
        btnCancelar.setBounds(395, 335, 140, 35);
        add(btnCancelar);

        ControladorCatffee.inicializarVentanaClientes(this);
    }
}
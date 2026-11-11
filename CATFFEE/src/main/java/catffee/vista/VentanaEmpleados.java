package catffee.vista;

import catffee.controlador.ControladorCatffee;
import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class VentanaEmpleados extends JFrame {
    public JTextField txtNombre, txtUsuario;
    public JPasswordField txtPassword;
    public JComboBox<String> cmbRol;
    public JButton btnGuardar, btnCancelar;
    public JTable tblEmpleados;
    public DefaultTableModel modeloEmpleados;

    public VentanaEmpleados() {
        setTitle("CATFFEE · Gestión de Empleados");
        setSize(600, 480);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(null);

        JLabel lblHeader = new JLabel("CATFFEE · Registro de Empleados");
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblHeader.setBounds(25, 15, 300, 25);
        add(lblHeader);

        JLabel lblNom = new JLabel("Nombre:");
        lblNom.setBounds(25, 60, 80, 25);
        add(lblNom);

        txtNombre = new JTextField();
        txtNombre.setBounds(100, 60, 180, 25);
        add(txtNombre);

        JLabel lblUser = new JLabel("Usuario:");
        lblUser.setBounds(300, 60, 60, 25);
        add(lblUser);

        txtUsuario = new JTextField();
        txtUsuario.setBounds(370, 60, 180, 25);
        add(txtUsuario);

        JLabel lblPass = new JLabel("Contraseña:");
        lblPass.setBounds(25, 100, 80, 25);
        add(lblPass);

        txtPassword = new JPasswordField();
        txtPassword.setBounds(100, 100, 180, 25);
        add(txtPassword);

        JLabel lblRol = new JLabel("Rol:");
        lblRol.setBounds(300, 100, 60, 25);
        add(lblRol);

        cmbRol = new JComboBox<>(new String[]{"Vendedor", "Administrador"});
        cmbRol.setBounds(370, 100, 180, 25);
        add(cmbRol);

        modeloEmpleados = new DefaultTableModel(new String[]{"Nombre", "Usuario", "Rol"}, 0);
        tblEmpleados = new JTable(modeloEmpleados);
        JScrollPane sp = new JScrollPane(tblEmpleados);
        sp.setBounds(25, 150, 525, 180);
        add(sp);

        btnGuardar = new JButton("Guardar Empleado");
        btnGuardar.setBounds(25, 360, 160, 35);
        add(btnGuardar);

        btnCancelar = new JButton("← Retroceder");
        btnCancelar.setBounds(410, 360, 140, 35);
        add(btnCancelar);

        ControladorCatffee.inicializarVentanaEmpleados(this);
    }
}
package catffee.vista;

import javax.swing.*;
import java.awt.*;

public class VentanaLogin extends JFrame {
    public JTextField txtUsuario;
    public JPasswordField txtContrasena;
    public JButton btnIngresar, btnSalir;
    public JLabel lblError;

    public VentanaLogin() {
        setTitle("CATFFEE · Iniciar sesión");
        setSize(450, 320);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);
        getContentPane().setBackground(new Color(245, 247, 250));

        JLabel lblTitulo = new JLabel("CATFFEE", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitulo.setBounds(100, 25, 230, 30);
        add(lblTitulo);

        JLabel lblUser = new JLabel("Usuario:");
        lblUser.setBounds(50, 80, 80, 25);
        add(lblUser);

        txtUsuario = new JTextField();
        txtUsuario.setBounds(140, 80, 230, 30);
        add(txtUsuario);

        JLabel lblPass = new JLabel("Contraseña:");
        lblPass.setBounds(50, 130, 80, 25);
        add(lblPass);

        txtContrasena = new JPasswordField();
        txtContrasena.setBounds(140, 130, 230, 30);
        add(txtContrasena);

        lblError = new JLabel("", SwingConstants.CENTER);
        lblError.setForeground(new Color(200, 30, 30));
        lblError.setBounds(50, 170, 320, 20);
        add(lblError);

        btnIngresar = new JButton("Ingresar");
        btnIngresar.setBounds(100, 210, 110, 35);
        add(btnIngresar);

        btnSalir = new JButton("Salir");
        btnSalir.setBounds(225, 210, 110, 35);
        add(btnSalir);

        // Lógica de navegación y roles simulada
        btnIngresar.addActionListener(e -> {
            String user = txtUsuario.getText();
            String pass = new String(txtContrasena.getPassword());
            if (user.equals("admin") && pass.equals("1234")) {
                new VentanaPrincipal("Administrador").setVisible(true);
                dispose();
            } else if (user.equals("vendedor") && pass.equals("1234")) {
                new VentanaPrincipal("Vendedor").setVisible(true);
                dispose();
            } else {
                lblError.setText("Usuario o contraseña incorrectos");
            }
        });

        btnSalir.addActionListener(e -> System.exit(0));
    }
}
package catffee.vista;

import javax.swing.*;
import java.awt.*;

public class VentanaPrincipal extends JFrame {
    public JLabel lblBienvenida;
    public JButton btnNuevaVenta, btnProductos, btnClientes, btnInsumos, btnCompras, btnProveedores, btnEmpleados, btnCerrarSesion;

    public VentanaPrincipal(String rol) {
        setTitle("CATFFEE · Menú principal");
        setSize(700, 420);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);
        getContentPane().setBackground(new Color(240, 244, 248));

        JLabel lblHeader = new JLabel("CATFFEE · Menú principal");
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblHeader.setBounds(25, 20, 300, 25);
        add(lblHeader);

        lblBienvenida = new JLabel("Bienvenido, " + (rol.equals("Administrador") ? "Admin" : "Laura") + " (" + rol + ")");
        lblBienvenida.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblBienvenida.setBounds(25, 55, 400, 20);
        add(lblBienvenida);

        // Botones comunes
        btnNuevaVenta = new JButton("Nueva venta");
        btnNuevaVenta.setBounds(25, 100, 140, 45);
        add(btnNuevaVenta);

        btnProductos = new JButton("Productos");
        btnProductos.setBounds(180, 100, 140, 45);
        add(btnProductos);

        btnClientes = new JButton("Clientes");
        btnClientes.setBounds(335, 100, 140, 45);
        add(btnClientes);

        JLabel lblAdminMsg = new JLabel("Solo el administrador (se ocultan al vendedor):");
        lblAdminMsg.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblAdminMsg.setForeground(Color.GRAY);
        lblAdminMsg.setBounds(25, 175, 350, 20);
        add(lblAdminMsg);

        // Botones exclusivos de Administrador
        btnInsumos = new JButton("Insumos");
        btnInsumos.setBounds(25, 205, 110, 40);
        add(btnInsumos);

        btnCompras = new JButton("Compras");
        btnCompras.setBounds(145, 205, 110, 40);
        add(btnCompras);

        btnProveedores = new JButton("Proveedores");
        btnProveedores.setBounds(265, 205, 120, 40);
        add(btnProveedores);

        btnEmpleados = new JButton("Empleados");
        btnEmpleados.setBounds(395, 205, 110, 40);
        add(btnEmpleados);

        btnCerrarSesion = new JButton("Cerrar sesión");
        btnCerrarSesion.setBounds(520, 315, 135, 35);
        add(btnCerrarSesion);

        // Control de restricciones de rol
        if (rol.equals("Vendedor")) {
            btnInsumos.setVisible(false);
            btnCompras.setVisible(false);
            btnProveedores.setVisible(false);
            btnEmpleados.setVisible(false);
            lblAdminMsg.setVisible(false);
        }

        // Eventos de navegación
        btnNuevaVenta.addActionListener(e -> new VentanaVenta().setVisible(true));
        btnProductos.addActionListener(e -> new VentanaProductos(rol).setVisible(true));
        btnInsumos.addActionListener(e -> new VentanaInsumos().setVisible(true));
        btnCompras.addActionListener(e -> new VentanaCompras().setVisible(true));
        
        btnCerrarSesion.addActionListener(e -> {
            new VentanaLogin().setVisible(true);
            dispose();
        });
    }
}
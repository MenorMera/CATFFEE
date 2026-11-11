package catffee.vista;

import catffee.controlador.ControladorCatffee;
import java.awt.*;
import javax.swing.*;

public class VentanaPrincipal extends JFrame {
    public JLabel lblBienvenida;
    public JButton btnNuevaVenta, btnProductos, btnClientes, btnInsumos, btnCompras, btnProveedores, btnEmpleados, btnReportes, btnCerrarSesion;

    public VentanaPrincipal(String rol) {
        setTitle("CATFFEE · Menú Principal (" + rol + ")");
        setSize(700, 420);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);
        getContentPane().setBackground(new Color(240, 244, 248));

        JLabel lblHeader = new JLabel("CATFFEE · Menú Principal");
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblHeader.setBounds(25, 20, 300, 25);
        add(lblHeader);

        lblBienvenida = new JLabel("Bienvenido, Usuario (" + rol + ")");
        lblBienvenida.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblBienvenida.setBounds(25, 55, 400, 20);
        add(lblBienvenida);

        // Botones principales
        btnNuevaVenta = new JButton("Nueva Venta");
        btnNuevaVenta.setBounds(25, 100, 140, 45);
        add(btnNuevaVenta);

        btnProductos = new JButton("Productos");
        btnProductos.setBounds(180, 100, 140, 45);
        add(btnProductos);

        btnClientes = new JButton("Clientes");
        btnClientes.setBounds(335, 100, 140, 45);
        add(btnClientes);

        JLabel lblAdminMsg = new JLabel("Módulos exclusivos de Administrador:");
        lblAdminMsg.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblAdminMsg.setForeground(Color.GRAY);
        lblAdminMsg.setBounds(25, 175, 350, 20);
        add(lblAdminMsg);

        btnInsumos = new JButton("Insumos");
        btnInsumos.setBounds(25, 205, 105, 40);
        add(btnInsumos);

        btnCompras = new JButton("Compras");
        btnCompras.setBounds(135, 205, 105, 40);
        add(btnCompras);

        btnProveedores = new JButton("Proveedores");
        btnProveedores.setBounds(245, 205, 115, 40);
        add(btnProveedores);

        btnEmpleados = new JButton("Empleados");
        btnEmpleados.setBounds(365, 205, 110, 40);
        add(btnEmpleados);

        btnReportes = new JButton("Reportes");
        btnReportes.setBounds(480, 205, 105, 40);
        add(btnReportes);

        btnCerrarSesion = new JButton("Cerrar Sesión");
        btnCerrarSesion.setBounds(520, 315, 135, 35);
        add(btnCerrarSesion);

        // Control visual de restricciones de rol
        if (rol.equals("Vendedor")) {
            btnInsumos.setVisible(false);
            btnCompras.setVisible(false);
            btnProveedores.setVisible(false);
            btnEmpleados.setVisible(false);
            btnReportes.setVisible(false);
            lblAdminMsg.setVisible(false);
        }

        // Vinculación automática con el controlador centralizado
        ControladorCatffee.inicializarVentanaPrincipal(this, rol);
    }
}
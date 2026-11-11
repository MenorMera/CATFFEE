package catffee.vista;
import catffee.controlador.ControladorCatffee;
import java.awt.*;
import javax.swing.*;

public class VentanaClientes extends JFrame {
    public JButton btnCancelar;
    public VentanaClientes() {
        setTitle("CATFFEE · Clientes");
        setSize(500, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(null);
        add(new JLabel("Módulo de Registro de Clientes")).setBounds(25, 20, 300, 25);
        
        btnCancelar = new JButton("← Retroceder");
        btnCancelar.setBounds(320, 310, 140, 30);
        add(btnCancelar);
        btnCancelar.addActionListener(e -> { dispose(); ControladorCatffee.abrirVentanaPrincipal("Administrador"); });
    }
}
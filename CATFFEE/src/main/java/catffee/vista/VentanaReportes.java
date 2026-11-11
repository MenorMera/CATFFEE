package catffee.vista;
import catffee.controlador.ControladorCatffee;
import catffee.controlador.GestorCache;
import java.awt.*;
import java.util.List;
import javax.swing.*;

public class VentanaReportes extends JFrame {
    public JButton btnCancelar, btnCargar;
    public JTextArea txtReporte;

    public VentanaReportes() {
        setTitle("CATFFEE · Reportes de Ventas");
        setSize(600, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(null);

        JLabel lbl = new JLabel("Consolidado de Ventas (Caché Local)");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lbl.setBounds(25, 15, 350, 25);
        add(lbl);

        txtReporte = new JTextArea();
        txtReporte.setEditable(false);
        JScrollPane sp = new JScrollPane(txtReporte);
        sp.setBounds(25, 55, 530, 280);
        add(sp);

        btnCargar = new JButton("Cargar Reporte");
        btnCargar.setBounds(25, 355, 140, 35);
        add(btnCargar);

        btnCancelar = new JButton("← Retroceder");
        btnCancelar.setBounds(415, 355, 140, 35);
        add(btnCancelar);

        btnCargar.addActionListener(e -> {
            txtReporte.setText("");
            List<String> ventas = GestorCache.leerRegistros("ventas_cache.txt");
            if (ventas.isEmpty()) {
                txtReporte.setText("No hay ventas registradas en la caché todavía.");
            } else {
                for (String v : ventas) {
                    txtReporte.append(v + "\n");
                }
            }
        });

        btnCancelar.addActionListener(e -> {
            dispose();
            ControladorCatffee.abrirVentanaPrincipal("Administrador");
        });
    }
}
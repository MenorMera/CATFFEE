package catffee.vista;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class VentanaInsumos extends JFrame {
    public JTextField txtDescripcion, txtPrecioCosto, txtStockActual, txtStockMinimo;
    public JCheckBox chkSoloReponer;
    public JButton btnGuardar, btnActualizar, btnEliminar, btnLimpiar;
    public JTable tblInsumos;
    public DefaultTableModel modeloInsumos;
    public Object btnCancelar;

    public VentanaInsumos() {
        setTitle("CATFFEE · Insumos");
        setSize(650, 530);
        setLocationRelativeTo(null);
        setLayout(null);

        JLabel lblHeader = new JLabel("CATFFEE · Insumos");
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblHeader.setBounds(25, 15, 300, 25);
        add(lblHeader);

        JLabel lblDesc = new JLabel("Descripción:");
        lblDesc.setBounds(25, 60, 80, 25);
        add(lblDesc);

        txtDescripcion = new JTextField();
        txtDescripcion.setBounds(110, 60, 180, 25);
        add(txtDescripcion);

        JLabel lblCosto = new JLabel("Costo:");
        lblCosto.setBounds(315, 60, 50, 25);
        add(lblCosto);

        txtPrecioCosto = new JTextField();
        txtPrecioCosto.setBounds(365, 60, 120, 25);
        add(txtPrecioCosto);

        JLabel lblStockAct = new JLabel("Stock actual:");
        lblStockAct.setBounds(25, 100, 80, 25);
        add(lblStockAct);

        txtStockActual = new JTextField();
        txtStockActual.setBounds(110, 100, 120, 25);
        add(txtStockActual);

        JLabel lblStockMin = new JLabel("Stock mínimo:");
        lblStockMin.setBounds(275, 100, 90, 25);
        add(lblStockMin);

        txtStockMinimo = new JTextField();
        txtStockMinimo.setBounds(365, 100, 120, 25);
        add(txtStockMinimo);

        chkSoloReponer = new JCheckBox("Ver solo los que hay que reponer");
        chkSoloReponer.setBounds(25, 140, 250, 25);
        add(chkSoloReponer);

        // Tabla insumos
        modeloInsumos = new DefaultTableModel(new String[]{"Descripción", "Stock", "Mínimo", "Estado"}, 0);
        modeloInsumos.addRow(new Object[]{"Café molido", 500, 200, "OK"});
        modeloInsumos.addRow(new Object[]{"Leche", 150, 300, "REPONER"});
        modeloInsumos.addRow(new Object[]{"Azúcar", 900, 300, "OK"});

        tblInsumos = new JTable(modeloInsumos);
        JScrollPane scroll = new JScrollPane(tblInsumos);
        scroll.setBounds(25, 180, 580, 180);
        add(scroll);

        // Botones de acción inferior
        btnGuardar = new JButton("Guardar");
        btnGuardar.setBounds(25, 385, 110, 35);
        add(btnGuardar);

        btnActualizar = new JButton("Actualizar");
        btnActualizar.setBounds(145, 385, 110, 35);
        add(btnActualizar);

        btnEliminar = new JButton("Eliminar");
        btnEliminar.setBounds(265, 385, 110, 35);
        add(btnEliminar);

        btnLimpiar = new JButton("Limpiar");
        btnLimpiar.setBounds(385, 385, 110, 35);
        add(btnLimpiar);
        
        JButton btnCancelar = new JButton("← Retroceder");
        btnCancelar.setBounds(500, 435, 120, 35);
        add(btnCancelar);
    }
}
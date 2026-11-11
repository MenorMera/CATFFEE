/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package catffee.mockups;

import catffee.controlador.ControladorCatffee;
import java.awt.Color;
import java.awt.Font;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

/**
 *
 * @author Mera
 */
public class Login extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(Login.class.getName());
public JTextField txtUsuario;
    public JPasswordField txtContrasena;
    public JButton btnIngresar, btnSalir;
    public JLabel lblError;

    public Login() {
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

        // Vinculación automática con el controlador
        ControladorCatffee.inicializarLogin(this);
    }
     
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new Login().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}

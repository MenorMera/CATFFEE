package catffee;

import catffee.vista.VentanaLogin;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        // Ejecuta la interfaz gráfica de forma segura en el hilo de eventos de Swing
        SwingUtilities.invokeLater(() -> {
            new VentanaLogin().setVisible(true);
        });
    }
}
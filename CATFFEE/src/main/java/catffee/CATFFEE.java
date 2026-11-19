package catffee;

import catffee.controlador.ControladorCatffee;
import javax.swing.SwingUtilities;

public class CATFFEE {
    public static void main(String[] args) {
        // Arranca la aplicación ejecutando el Login a través del controlador
        SwingUtilities.invokeLater(() -> {
            ControladorCatffee.abrirLogin();
        });
    }
}
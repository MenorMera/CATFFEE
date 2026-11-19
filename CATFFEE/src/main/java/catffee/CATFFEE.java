package catffee;

import catffee.controlador.ControladorCatffee;
import catffee.vista.Tema;
import javax.swing.SwingUtilities;

public class CATFFEE {
    public static void main(String[] args) {
        // Primero el tema visual (colores y logo), después el login
        Tema.aplicar();
        SwingUtilities.invokeLater(() -> {
            ControladorCatffee.abrirLogin();
        });
    }
}
package catffee.vista;

import com.formdev.flatlaf.FlatLightLaf;
import java.awt.AWTEvent;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;
import java.net.URL;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.BevelBorder;

/**
 * Tema visual de Catffee: fondo de madera, botones color café y logo.
 * Se aplica a todas las ventanas al abrirse.
 */
public final class Tema {

    // Colores de madera y café
    public static final Color MADERA_OSCURA = new Color(92, 58, 33);
    public static final Color NOGAL         = new Color(74, 44, 26);
    public static final Color CAFE          = new Color(111, 78, 55);
    public static final Color CAFE_CLARO    = new Color(150, 105, 70);
    public static final Color BEIGE         = new Color(232, 220, 203);
    public static final Color TEXTO         = new Color(60, 35, 20);

    private static final Border BORDE_BOTON = BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(NOGAL, 1),
            BorderFactory.createBevelBorder(BevelBorder.RAISED, CAFE_CLARO, NOGAL));

    private static Image logo;
    private static Image madera;
    private static Image gato;

    private Tema() { }

    public static void aplicar() {
        FlatLightLaf.setup();

        UIManager.put("Panel.background", BEIGE);
        UIManager.put("Label.foreground", TEXTO);
        UIManager.put("Button.background", CAFE);
        UIManager.put("Button.foreground", Color.WHITE);
        UIManager.put("Button.hoverBackground", CAFE_CLARO);
        UIManager.put("Button.pressedBackground", NOGAL);
        UIManager.put("Button.arc", 14);
        UIManager.put("Component.arc", 10);
        UIManager.put("TextComponent.arc", 10);
        UIManager.put("TextField.background", Color.WHITE);
        UIManager.put("PasswordField.background", Color.WHITE);
        UIManager.put("Table.background", Color.WHITE);
        UIManager.put("Table.selectionBackground", BEIGE);
        UIManager.put("Table.selectionForeground", TEXTO);
        UIManager.put("TableHeader.background", CAFE);
        UIManager.put("TableHeader.foreground", Color.WHITE);

        Toolkit.getDefaultToolkit().addAWTEventListener(e -> {
            if (e.getID() == WindowEvent.WINDOW_OPENED && e.getSource() instanceof JFrame f) {
                estilizar(f);
            }
        }, AWTEvent.WINDOW_EVENT_MASK);
    }

    public static void estilizar(JFrame ventana) {
        ((javax.swing.JComponent) ventana.getContentPane()).setOpaque(false);
        List<Image> iconos = new ArrayList<>();
        for (int t : new int[]{16, 32, 64, 128}) {
            Image i = icono(t);
            if (i != null) iconos.add(i);
        }
        if (!iconos.isEmpty()) ventana.setIconImages(iconos);

        // Fondo de madera detrás de todo el contenido
        JLayeredPane capas = ventana.getLayeredPane();
        JPanel fondo = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Image m = madera();
                int w = getWidth(), h = getHeight();
                if (m == null || m.getWidth(null) <= 0) {
                    g.setColor(BEIGE); g.fillRect(0, 0, w, h); return;
                }
                int iw = m.getWidth(null), ih = m.getHeight(null);
                double escala = Math.max(w / (double) iw, h / (double) ih);
                int dw = (int) Math.round(iw * escala), dh = (int) Math.round(ih * escala);
                g.drawImage(m, (w - dw) / 2, (h - dh) / 2, dw, dh, null);
            }
        };
        fondo.setOpaque(true);
        fondo.setBounds(0, 0, capas.getWidth(), capas.getHeight());
        capas.add(fondo, JLayeredPane.FRAME_CONTENT_LAYER);
        capas.setComponentZOrder(fondo, capas.getComponentCount() - 1);

        ventana.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                fondo.setBounds(0, 0, capas.getWidth(), capas.getHeight());
            }
        });

        estilizarComponentes(ventana.getContentPane());
        ponerLogoEnTitulos(ventana.getContentPane());
        ventana.pack();
    }

    // Botones café con borde 3D; paneles y etiquetas transparentes para ver la madera
    private static void estilizarComponentes(Container contenedor) {
        for (Component c : contenedor.getComponents()) {
            if (c instanceof JButton b) {
                b.setOpaque(true);
                b.setContentAreaFilled(true);
                b.setBackground(CAFE);
                b.setForeground(Color.WHITE);
                b.setBorder(BORDE_BOTON);
                b.setFocusPainted(false);
            } else if (c instanceof JLabel lbl) {
                lbl.setOpaque(false);
                Color fg = lbl.getForeground();
                boolean gris = fg.getRed() == fg.getGreen() && fg.getGreen() == fg.getBlue()
                            && fg.getRed() > 90 && fg.getRed() < 200;
                if (gris) lbl.setForeground(TEXTO);
            } else if (c instanceof JPanel p) {
                p.setBackground(BEIGE);
                p.setOpaque(false);
            }
            if (c instanceof Container hijo) estilizarComponentes(hijo);
        }
    }

    // Reemplaza el texto "CATFFEE" de los títulos por el logo
    private static void ponerLogoEnTitulos(Container contenedor) {
        for (Component c : contenedor.getComponents()) {
            if (c instanceof JLabel lbl && lbl.getText() != null && lbl.getText().startsWith("CATFFEE")) {
                boolean soloNombre = lbl.getText().trim().equals("CATFFEE");
                if (soloNombre) {
                    // Logo completo: se reduce para caber en 230 x 96, sin deformarse
                    Image img = logoEnCaja(230, 96);
                    if (img != null) {
                        lbl.setIcon(new ImageIcon(img));
                        lbl.setText("");
                        lbl.setPreferredSize(new Dimension(230, 96));
                    }
                } else {
                    Image img = logo(40);
                    if (img != null) {
                        lbl.setIcon(new ImageIcon(img));
                        Dimension d = lbl.getPreferredSize();
                        lbl.setPreferredSize(new Dimension(d.width, Math.max(d.height, 46)));
                    }
                }
            }
            if (c instanceof Container hijo) ponerLogoEnTitulos(hijo);
        }
    }

    // Logo completo escalado para caber dentro de maxAncho x maxAlto
    public static Image logoEnCaja(int maxAncho, int maxAlto) {
        Image base = logoBase();
        if (base == null) return null;
        double escala = Math.min(maxAncho / (double) base.getWidth(null), maxAlto / (double) base.getHeight(null));
        return base.getScaledInstance((int) Math.round(base.getWidth(null) * escala),
                                      (int) Math.round(base.getHeight(null) * escala), Image.SCALE_SMOOTH);
    }

    // Solo el gato, cuadrado, para el icono de cada ventana
    public static Image icono(int tamano) {
        if (gato == null) {
            URL url = Tema.class.getResource("/images/gato_icono.png");
            if (url == null) return null;
            gato = new ImageIcon(url).getImage();
        }
        return gato.getScaledInstance(tamano, tamano, Image.SCALE_SMOOTH);
    }

    private static Image logoBase() {
        if (logo == null) {
            URL url = Tema.class.getResource("/images/logo_catffee.png");
            if (url == null) return null;
            logo = new ImageIcon(url).getImage();
        }
        return logo;
    }

    public static Image logo(int altura) {
        if (logo == null) {
            URL url = Tema.class.getResource("/images/logo_catffee.png");
            if (url == null) return null;
            logo = new ImageIcon(url).getImage();
        }
        int ancho = (int) Math.round(logo.getWidth(null) * (altura / (double) logo.getHeight(null)));
        return logo.getScaledInstance(ancho, altura, Image.SCALE_SMOOTH);
    }

    private static Image madera() {
        if (madera == null) {
            URL url = Tema.class.getResource("/images/madera.jpg");
            if (url == null) return null;
            madera = new ImageIcon(url).getImage();
        }
        return madera;
    }
}
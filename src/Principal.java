import db.operaciones.vistatienda;
import javax.swing.*;

public class Principal {
    // Método principal que inicia la aplicación
    public static void main(String[] args) {

        // Ajusta la apariencia de la ventana para que se vea como una aplicación nativa de Windows
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
            // Si no puede aplicar el estilo, continúa con el estilo por defecto de Java
        }

        // Abre la interfaz gráfica de forma segura en un hilo independiente
        SwingUtilities.invokeLater(() -> new vistatienda().setVisible(true));
    }
}
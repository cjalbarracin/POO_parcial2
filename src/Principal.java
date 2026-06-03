import db.operaciones.vistatienda;
import javax.swing.*;

public class Principal {
    // Método principal que inicia la aplicación
    public static void main(String[] args) {
        // Abre la interfaz gráfica de forma segura en un hilo independiente
        SwingUtilities.invokeLater(() -> new vistatienda().setVisible(true));
    }
}
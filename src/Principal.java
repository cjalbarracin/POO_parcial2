import db.operaciones.vistatienda;
import javax.swing.*;

public class Principal {
    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception ignored) {}
        SwingUtilities.invokeLater(() -> new vistatienda().setVisible(true));
    }
}
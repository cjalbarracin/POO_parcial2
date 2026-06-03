import db.operaciones.vistatienda; // Asegúrate de importar tu ventana

public class Principal {
    public static void main(String[] args) {
        // Esto le dice a Java que inicie la interfaz gráfica
        javax.swing.SwingUtilities.invokeLater(() -> {
            vistatienda frame = new vistatienda();
            frame.setVisible(true);
        });
    }
}
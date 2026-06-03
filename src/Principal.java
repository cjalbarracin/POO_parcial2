package db;

import db.operaciones.VentanaTienda;
import javax.swing.SwingUtilities;

public class Principal {
    public static void main(String[] args) {
        // Lanza la interfaz gráfica de la tienda de celulares
        SwingUtilities.invokeLater(() -> {
            new VentanaTienda().setVisible(true);
        });
    }
}
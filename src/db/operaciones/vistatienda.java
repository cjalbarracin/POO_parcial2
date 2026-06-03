package db.operaciones;

import db.DBConnection; // Asegúrate de importar tu clase de conexión
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class vistatienda extends JFrame { // Heredamos de JFrame
    private JPanel panel1;
    private JTextField txtPrecio;
    private JTextField txtAlmacenamiento;
    private JTextField txtCamara;
    private JTextField txtModelo;
    private JTextField txtMarca;
    private JTextField txtRAM;
    private JTextField txtBateria;
    private JButton btnRegistrar;
    private JComboBox cmbBuscarPor;
    private JTextField txtBuscar;
    private JButton btnAplicarFiltro;
    private JTextArea txtAreaResultados;
    private JButton btnConsultarTodo;
    private JButton btnDescargarReporte;

    // CONSTRUCTOR: Aquí es donde inicializamos la ventana
    public vistatienda() {
        setContentPane(panel1);
        setTitle("Sistema de Gestión - Tienda de Celulares");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null); // Centrar la ventana en pantalla

        // Ejemplo: Configurar el botón de registrar
        btnRegistrar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Aquí llamaremos a la función que inserta en la BD
                JOptionPane.showMessageDialog(null, "Botón Registrar presionado");
            }
        });
    }

    // Este método es necesario para arrancar la app
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            vistatienda frame = new vistatienda();
            frame.setVisible(true);
        });
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }
}
package db.operaciones;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
// Importamos tus modelos desde el paquete 'model'
import model.celular;
import model.inventario;

public class vistatienda extends JFrame {
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

    // Instancia del DAO (está en el mismo paquete, así que no requiere import)
    private TiendaDAO dao = new TiendaDAO();

    public vistatienda() {
        setContentPane(panel1);
        setTitle("Sistema de Gestión - Tienda de Celulares");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);

        // Lógica de Registro
        btnRegistrar.addActionListener(e -> {
            try {
                celular c = new celular(txtMarca.getText(), txtModelo.getText(),
                        Integer.parseInt(txtCamara.getText()),
                        Integer.parseInt(txtBateria.getText()));
                inventario i = new inventario(0, Integer.parseInt(txtAlmacenamiento.getText()),
                        Double.parseDouble(txtPrecio.getText()),
                        Integer.parseInt(txtRAM.getText()));

                if (dao.registrarCelularCompleto(c, i)) {
                    JOptionPane.showMessageDialog(this, "¡Celular registrado!");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });

        // Lógica de Consulta
        btnConsultarTodo.addActionListener(e -> {
            txtAreaResultados.setText(dao.obtenerInventarioCompletoTexto());
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new vistatienda().setVisible(true));
    }
}
package db.operaciones;

import javax.swing.*;
import model.celular;
import model.inventario;

public class vistatienda extends JFrame {
    private JPanel panel1;
    private JTextField txtMarca, txtModelo, txtCamara, txtBateria, txtAlmacenamiento, txtPrecio, txtRAM, txtBuscar;
    private JButton btnRegistrar, btnAplicarFiltro, btnConsultarTodo, btnDescargarReporte;
    private JComboBox cmbBuscarPor;
    private JTextArea txtAreaResultados;

    private TiendaDAO dao = new TiendaDAO();

    public vistatienda() {
        setContentPane(panel1);
        setTitle("Sistema de Gestión - Tienda de Celulares");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        btnRegistrar.addActionListener(e -> registrar());

        btnConsultarTodo.addActionListener(e -> {
            txtAreaResultados.setText(dao.obtenerInventarioCompletoTexto());
        });

        btnAplicarFiltro.addActionListener(e -> {
            // Usamos trim() para quitar espacios accidentales y toString() seguro
            String criterio = cmbBuscarPor.getSelectedItem() != null ? cmbBuscarPor.getSelectedItem().toString() : "";
            String busqueda = txtBuscar.getText().trim();

            if (busqueda.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Escribe algo en el campo de búsqueda");
                return;
            }

            if (criterio.equalsIgnoreCase("Marca")) {
                String resultado = dao.filtrarPorMarcaTexto(busqueda);
                txtAreaResultados.setText(resultado);
            } else {
                JOptionPane.showMessageDialog(this, "Criterio no implementado aún");
            }
        });

        pack();
        setLocationRelativeTo(null);
    }

    private void registrar() {
        try {
            celular c = new celular(txtMarca.getText(), txtModelo.getText(), Integer.parseInt(txtCamara.getText()), Integer.parseInt(txtBateria.getText()));
            inventario i = new inventario(0, Integer.parseInt(txtAlmacenamiento.getText()), Double.parseDouble(txtPrecio.getText()), Integer.parseInt(txtRAM.getText()));
            if (dao.registrarCelularCompleto(c, i)) {
                JOptionPane.showMessageDialog(this, "¡Registrado con éxito!");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }
}
package db.operaciones;

import javax.swing.*;
import model.celular;
import model.inventario;
import java.awt.*;
import java.io.*; // Importante para BufferedWriter, FileWriter e IOException

public class vistatienda extends JFrame {
    // Declaración de componentes
    private JPanel panel1;
    private JTextField txtMarca;
    private JTextField txtModelo;
    private JTextField txtCamara;
    private JTextField txtBateria;
    private JTextField txtAlmacenamiento;
    private JTextField txtPrecio;
    private JTextField txtRAM;
    private JButton btnRegistrar;
    private JComboBox cmbBuscarPor;
    private JTextField txtBuscar;
    private JButton btnAplicarFiltro;
    private JTextArea txtAreaResultados;
    private JButton btnConsultarTodo;
    private JButton btnDescargarReporte;

    private TiendaDAO dao = new TiendaDAO();

    public vistatienda() {
        setContentPane(panel1);
        setTitle("Sistema de Gestión - Tienda de Celulares");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Configuración de tamaño y centrado
        this.setSize(800, 600);
        this.setLocationRelativeTo(null);

        // Estética básica
        txtAreaResultados.setFont(new Font("Monospaced", Font.PLAIN, 12));

        // Eventos
        btnRegistrar.addActionListener(e -> registrar());
        btnConsultarTodo.addActionListener(e -> txtAreaResultados.setText(dao.obtenerInventarioCompletoTexto()));

        btnAplicarFiltro.addActionListener(e -> filtrar());

        // Evento para el botón de reporte
        btnDescargarReporte.addActionListener(e -> descargarReporte());
    }

    private void filtrar() {
        String criterio = cmbBuscarPor.getSelectedItem().toString();
        String valor = txtBuscar.getText().trim();

        if (valor.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, ingresa un valor de búsqueda.");
            return;
        }

        try {
            String resultado = "";
            switch (criterio) {
                case "Marca": resultado = dao.filtrarPorMarcaTexto(valor); break;
                case "Precio": resultado = dao.filtrarPorPrecioMax(Double.parseDouble(valor)); break;
                case "Almacenamiento": resultado = dao.filtrarPorAlmacenamiento(Integer.parseInt(valor)); break;
                default: resultado = "Criterio no reconocido.";
            }
            txtAreaResultados.setText(resultado);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Error: Para Precio o Almacenamiento, ingresa solo números.");
        }
    }

    private void registrar() {
        try {
            celular c = new celular(txtMarca.getText(), txtModelo.getText(),
                    Integer.parseInt(txtCamara.getText()),
                    Integer.parseInt(txtBateria.getText()));
            inventario i = new inventario(0, Integer.parseInt(txtAlmacenamiento.getText()),
                    Double.parseDouble(txtPrecio.getText()),
                    Integer.parseInt(txtRAM.getText()));

            if (dao.registrarCelularCompleto(c, i)) {
                JOptionPane.showMessageDialog(this, "¡Celular registrado con éxito!");
            } else {
                JOptionPane.showMessageDialog(this, "Error al guardar en base de datos.");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error en los datos: " + ex.getMessage());
        }
    }

    private void descargarReporte() {
        String contenido = txtAreaResultados.getText();

        if (contenido.isEmpty() || contenido.equals("No se encontraron resultados.")) {
            JOptionPane.showMessageDialog(this, "No hay datos para guardar.");
            return;
        }

        String fileName = "reporte_inventario.txt";

        // Implementación de escritura de archivos basada en el documento [cite: 35-37, 67]
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            writer.write(contenido);
            JOptionPane.showMessageDialog(this, "Reporte guardado exitosamente como: " + fileName);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error al escribir en el archivo: " + e.getMessage());
        }
    }
}
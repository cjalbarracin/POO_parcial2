package db.operaciones;

import javax.swing.*;
import model.celular;
import model.inventario;
import java.awt.*;
import java.io.*;

public class vistatienda extends JFrame {
    // Declaración de los componentes de la interfaz (botones, cajas de texto, etc.)
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

    // Conectamos la interfaz con nuestro "Encargado de Bodega" (DAO)
    private TiendaDAO dao = new TiendaDAO();

    public vistatienda() {
        setContentPane(panel1);
        setTitle("Sistema de Gestión - Tienda de Celulares");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        this.setSize(800, 600);
        this.setLocationRelativeTo(null); // Centra la ventana en pantalla

        txtAreaResultados.setFont(new Font("Monospaced", Font.PLAIN, 12));

        // Asignamos las acciones a cada botón de la interfaz
        btnRegistrar.addActionListener(e -> registrar());
        btnConsultarTodo.addActionListener(e -> txtAreaResultados.setText(dao.obtenerInventarioCompletoTexto()));
        btnAplicarFiltro.addActionListener(e -> filtrar());
        btnDescargarReporte.addActionListener(e -> descargarReporte());
    }

    // Lógica para filtrar datos según lo que el usuario elija en el combo box
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

    // Toma los datos de las cajas de texto y los envía a registrar mediante el DAO
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

    // Abre una ventana para que el usuario elija dónde guardar el reporte de texto
    private void descargarReporte() {
        String contenido = txtAreaResultados.getText();

        if (contenido.isEmpty() || contenido.equals("No se encontraron resultados.")) {
            JOptionPane.showMessageDialog(this, "No hay datos para guardar.");
            return;
        }

        // Selector nativo de archivos para evitar problemas de rutas
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Guardar Reporte en...");
        fileChooser.setSelectedFile(new File("reporte_inventario.txt"));

        int userSelection = fileChooser.showSaveDialog(this);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();

            // Aseguramos que el archivo tenga extensión .txt
            String path = fileToSave.getAbsolutePath();
            if (!path.endsWith(".txt")) {
                path += ".txt";
            }

            // Escribimos el contenido en el archivo seleccionado
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(path))) {
                writer.write(contenido);
                JOptionPane.showMessageDialog(this, "¡Reporte guardado con éxito!");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error al guardar: " + ex.getMessage());
            }
        }
    }
}
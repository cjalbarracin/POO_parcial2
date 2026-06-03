package db.operaciones;

import model.celular;
import model.inventario;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class VentanaTienda extends JFrame {
    private JPanel panelFormulario, panelBotones, panelPrincipal, panelFiltros;
    private JTextField txtMarca, txtModelo, txtCamara, txtBateria, txtAlmacenamiento, txtPrecio, txtRam;

    // Componentes interactivos para el filtrado dinámico
    private JTextField txtCriterioFiltro, txtPrecioMax;
    private JComboBox<String> comboTipoFiltro;
    private JLabel lblCriterio, lblPrecioMax;

    private JButton btnRegistrar, btnListar, btnExportar, btnEjecutarFiltro;
    private JTextArea txtAreaResultados;
    private JScrollPane scrollPane;
    private TiendaDAO dao;

    public VentanaTienda() {
        dao = new TiendaDAO();

        setTitle("Sistema Avanzado de Gestión de Celulares - POO Parcial Final");
        setSize(850, 720);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        panelPrincipal = new JPanel(new BorderLayout(10, 10));
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 1. Formulario superior de inserción
        panelFormulario = new JPanel(new GridLayout(4, 4, 10, 10));
        panelFormulario.setBorder(BorderFactory.createTitledBorder(" Formulario: Adicionar Nuevo Registro "));

        panelFormulario.add(new JLabel(" Marca:"));
        txtMarca = new JTextField(); panelFormulario.add(txtMarca);

        panelFormulario.add(new JLabel(" Modelo:"));
        txtModelo = new JTextField(); panelFormulario.add(txtModelo);

        panelFormulario.add(new JLabel(" Cámara (MP):"));
        txtCamara = new JTextField(); panelFormulario.add(txtCamara);

        panelFormulario.add(new JLabel(" Batería (mAh):"));
        txtBateria = new JTextField(); panelFormulario.add(txtBateria);

        panelFormulario.add(new JLabel(" Almacenamiento (GB):"));
        txtAlmacenamiento = new JTextField(); panelFormulario.add(txtAlmacenamiento);

        panelFormulario.add(new JLabel(" Precio ($):"));
        txtPrecio = new JTextField(); panelFormulario.add(txtPrecio);

        panelFormulario.add(new JLabel(" Memoria RAM (GB):"));
        txtRam = new JTextField(); panelFormulario.add(txtRam);

        btnRegistrar = new JButton("Registrar Celular");
        btnRegistrar.setFont(new Font("Arial", Font.BOLD, 12));
        panelFormulario.add(btnRegistrar);

        // 2. Panel de Consultas y Múltiples Filtros adaptables
        panelFiltros = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 8));
        panelFiltros.setBorder(BorderFactory.createTitledBorder(" Consultar y Filtrar Información (Filtros Avanzados) "));

        panelFiltros.add(new JLabel("Buscar por:"));
        String[] opciones = {"Filtrar por Marca", "Consultar un Modelo", "Filtrar por Precio (Min-Max)", "Filtrar por Almacenamiento (GB)"};
        comboTipoFiltro = new JComboBox<>(opciones);
        panelFiltros.add(comboTipoFiltro);

        lblCriterio = new JLabel("Marca a buscar:");
        panelFiltros.add(lblCriterio);

        txtCriterioFiltro = new JTextField(10);
        panelFiltros.add(txtCriterioFiltro);

        lblPrecioMax = new JLabel("Precio Máx ($):");
        panelFiltros.add(lblPrecioMax);

        txtPrecioMax = new JTextField(8);
        panelFiltros.add(txtPrecioMax);

        // Estado inicial de la GUI: ocultar precio máximo ya que inicia por "Marca"
        lblPrecioMax.setVisible(false);
        txtPrecioMax.setVisible(false);

        btnEjecutarFiltro = new JButton("Aplicar Filtro");
        panelFiltros.add(btnEjecutarFiltro);

        JPanel panelSuperiorCohesion = new JPanel(new BorderLayout(5, 5));
        panelSuperiorCohesion.add(panelFormulario, BorderLayout.NORTH);
        panelSuperiorCohesion.add(panelFiltros, BorderLayout.SOUTH);

        // 3. Monitor de visualización de datos
        txtAreaResultados = new JTextArea();
        txtAreaResultados.setEditable(false);
        txtAreaResultados.setFont(new Font("Monospaced", Font.PLAIN, 12));
        scrollPane = new JScrollPane(txtAreaResultados);

        // 4. Panel inferior de acciones globales
        panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 25, 10));
        btnListar = new JButton("Consultar Todo el Inventario");
        btnExportar = new JButton("Exportar Filtro Actual a TXT");

        btnExportar.setBackground(new Color(34, 139, 34));
        btnExportar.setForeground(Color.WHITE);
        btnExportar.setFont(new Font("Arial", Font.BOLD, 12));

        panelBotones.add(btnListar);
        panelBotones.add(btnExportar);

        panelPrincipal.add(panelSuperiorCohesion, BorderLayout.NORTH);
        panelPrincipal.add(scrollPane, BorderLayout.CENTER);
        panelPrincipal.add(panelBotones, BorderLayout.SOUTH);

        setContentPane(panelPrincipal);
        configurarEventos();
    }

    private void configurarEventos() {

        // Manejador del cambio de selección en el ComboBox (adapta los inputs dinámicamente)
        comboTipoFiltro.addActionListener(e -> {
            int seleccion = comboTipoFiltro.getSelectedIndex();
            txtCriterioFiltro.setText("");
            txtPrecioMax.setText("");

            switch (seleccion) {
                case 0: // Marca
                    lblCriterio.setText("Marca a buscar:");
                    lblPrecioMax.setVisible(false);
                    txtPrecioMax.setVisible(false);
                    break;
                case 1: // Modelo
                    lblCriterio.setText("Modelo exacto:");
                    lblPrecioMax.setVisible(false);
                    txtPrecioMax.setVisible(false);
                    break;
                case 2: // Precio Rango
                    lblCriterio.setText("Precio Mín ($):");
                    lblPrecioMax.setVisible(true);
                    txtPrecioMax.setVisible(true);
                    break;
                case 3: // Almacenamiento
                    lblCriterio.setText("Capacidad (GB):");
                    lblPrecioMax.setVisible(false);
                    txtPrecioMax.setVisible(false);
                    break;
            }
            panelFiltros.revalidate();
            panelFiltros.repaint();
        });

        // --- ACCIÓN 1: INSERTAR REGISTROS TRANSACCIONALES ---
        btnRegistrar.addActionListener(e -> {
            try {
                String marca = txtMarca.getText().trim();
                String modelo = txtModelo.getText().trim();
                int camara = Integer.parseInt(txtCamara.getText().trim());
                int bateria = Integer.parseInt(txtBateria.getText().trim());
                int almc = Integer.parseInt(txtAlmacenamiento.getText().trim());
                double precio = Double.parseDouble(txtPrecio.getText().trim());
                int ram = Integer.parseInt(txtRam.getText().trim());

                if (marca.isEmpty() || modelo.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Debe completar Marca y Modelo.", "Campos vacíos", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                // Corregido: Creación del objeto usando el constructor original (marca, modelo, camara, bateria)
                celular c = new celular(marca, modelo, camara, bateria);
                inventario i = new inventario(0, almc, precio, ram);

                dao.registrarCellularCompleto(c, i);
                JOptionPane.showMessageDialog(this, "¡Dispositivo guardado exitosamente en Neon!");
                limpiarCampos();
                txtAreaResultados.setText(dao.obtenerInventarioParaGUI());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Por favor verifique que los campos numéricos tengan valores válidos.", "Error de formato", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error de persistencia: " + ex.getMessage(), "Error BD", JOptionPane.ERROR_MESSAGE);
            }
        });

        // --- ACCIÓN 2: LISTAR TODO EL INVENTARIO ---
        btnListar.addActionListener(e -> {
            txtAreaResultados.setText("Consultando base de datos remota...");
            txtAreaResultados.setText(dao.obtenerInventarioParaGUI());
        });

        // --- ACCIÓN 3: PROCESAMIENTO DINÁMICO DE FILTROS ---
        btnEjecutarFiltro.addActionListener(e -> {
            int seleccion = comboTipoFiltro.getSelectedIndex();
            String valor = txtCriterioFiltro.getText().trim();

            if (valor.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Por favor ingrese el parámetro requerido en la casilla de texto.", "Dato Faltante", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            txtAreaResultados.setText("Filtrando registros en Neon...\n\n");
            String resultado = "";

            switch (seleccion) {
                case 0: // Filtrar por Marca
                    resultado = dao.obtenerFiltroMarcaParaGUI(valor);
                    break;
                case 1: // Consultar un Registro (Por Modelo)
                    resultado = dao.consultarUnRegistroPorModelo(valor);
                    break;
                case 2: // Filtrar por Rango de Precios
                    try {
                        double min = Double.parseDouble(valor);
                        double max = Double.parseDouble(txtPrecioMax.getText().trim());
                        resultado = dao.obtenerFiltroPrecioParaGUI(min, max);
                    } catch (Exception ex) {
                        resultado = "Error numérico: Ingrese valores válidos en las casillas de Precio Mínimo y Máximo.";
                    }
                    break;
                case 3: // Filtrar por Almacenamiento (GB)
                    try {
                        int gb = Integer.parseInt(valor);
                        resultado = dao.obtenerFiltroAlmacenamientoParaGUI(gb);
                    } catch (NumberFormatException ex) {
                        resultado = "Error: El almacenamiento debe expresarse en números enteros (Ej: 128, 256).";
                    }
                    break;
            }
            txtAreaResultados.setText(resultado);
        });

        // --- ACCIÓN 4: EXPORTACIÓN A ARCHIVOS PLANOS (.TXT) ---
        btnExportar.addActionListener(e -> {
            String textoAExportar = txtAreaResultados.getText();
            if (textoAExportar.trim().isEmpty() || textoAExportar.startsWith("Filtrando") || textoAExportar.startsWith("Consultando")) {
                JOptionPane.showMessageDialog(this, "No hay ningún reporte o resultado válido en pantalla para exportar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String nombreArchivo = "reporte_tienda.txt";
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(nombreArchivo))) {
                writer.write(textoAExportar);
                JOptionPane.showMessageDialog(this, "¡Reporte guardado exitosamente como '" + nombreArchivo + "' en la raíz del proyecto!");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error de escritura de archivo: " + ex.getMessage(), "Error I/O", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private void limpiarCampos() {
        txtMarca.setText(""); txtModelo.setText(""); txtCamara.setText("");
        txtBateria.setText(""); txtAlmacenamiento.setText(""); txtPrecio.setText(""); txtRam.setText("");
    }
}
package db.operaciones;

import db.DBConnection;
import model.celular;
import model.inventario;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TiendaDAO {

    // 1. Registro transaccional completo
    public boolean registrarCelularCompleto(celular c, inventario i) {
        String sqlCelular = "INSERT INTO celular (marca, modelo, camara, bateria) VALUES (?, ?, ?, ?)";
        String sqlInventario = "INSERT INTO inventario (celular_id, almacenamiento, precio, ram) VALUES (?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement ps1 = conn.prepareStatement(sqlCelular, Statement.RETURN_GENERATED_KEYS)) {
                ps1.setString(1, c.getMarca());
                ps1.setString(2, c.getModelo());
                ps1.setInt(3, c.getCamara());
                ps1.setInt(4, c.getBateria());
                ps1.executeUpdate();

                ResultSet rs = ps1.getGeneratedKeys();
                if (rs.next()) {
                    int idGenerado = rs.getInt(1);

                    try (PreparedStatement ps2 = conn.prepareStatement(sqlInventario)) {
                        ps2.setInt(1, idGenerado);
                        ps2.setInt(2, i.getAlmacenamiento());
                        ps2.setDouble(3, i.getPrecio());
                        ps2.setInt(4, i.getRam());
                        ps2.executeUpdate();
                    }
                }
                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                e.printStackTrace();
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // =========================================================================
    // MÉTODOS PARA LA CONSOLA (Evitan los errores en Principal.java)
    // =========================================================================

    public void listarInventarioCompleto() {
        String sql = "SELECT c.marca, c.modelo, i.precio, i.almacenamiento, i.ram " +
                "FROM celular c " +
                "JOIN inventario i ON c.id = i.celular_id";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            System.out.println("\n--- LISTADO COMPLETO ---");
            while (rs.next()) {
                System.out.println("Marca: " + rs.getString("marca") + " | Modelo: " + rs.getString("modelo"));
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public List<celular> filtrarPorMarca(String marca) {
        List<celular> lista = new ArrayList<>();
        String sql = "SELECT * FROM celular WHERE marca ILIKE ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + marca + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(new celular(rs.getString("marca"), rs.getString("modelo"), rs.getInt("camara"), rs.getInt("bateria")));
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return lista;
    }

    public void filtrarPorPrecio(double precioMin, double precioMax) {
        // Método vacío para cumplir con la consola vieja
    }

    public void filtrarPorAlmacenamiento(int gb) {
        // Método vacío para cumplir con la consola vieja
    }

    // =========================================================================
    // MÉTODOS PARA TU NUEVA INTERFAZ GRÁFICA (JTextArea)
    // =========================================================================

    public String obtenerInventarioCompletoTexto() {
        StringBuilder sb = new StringBuilder();
        String sql = "SELECT c.marca, c.modelo, i.precio, i.almacenamiento, i.ram " +
                "FROM celular c " +
                "JOIN inventario i ON c.id = i.celular_id";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            sb.append("=========================================================\n");
            sb.append("                  LISTADO COMPLETO DEL INVENTARIO         \n");
            sb.append("=========================================================\n\n");

            boolean tieneDatos = false;
            while (rs.next()) {
                tieneDatos = true;
                sb.append("• Marca: ").append(rs.getString("marca"))
                        .append("  |  Modelo: ").append(rs.getString("modelo"))
                        .append("  |  Precio: $").append(rs.getDouble("precio"))
                        .append("  |  Almacenamiento: ").append(rs.getInt("almacenamiento")).append("GB")
                        .append("  |  RAM: ").append(rs.getInt("ram")).append("GB\n")
                        .append("----------------------------------------------------------------------------------------\n");
            }
            if (!tieneDatos) return "No hay celulares registrados.";

        } catch (SQLException e) {
            return "Error al conectar: " + e.getMessage();
        }
        return sb.toString();
    }

    public String obtenerFiltradoPorMarcaTexto(String marca) {
        StringBuilder sb = new StringBuilder();
        String sql = "SELECT c.marca, c.modelo, i.precio, i.almacenamiento, i.ram " +
                "FROM celular c " +
                "JOIN inventario i ON c.id = i.celular_id " +
                "WHERE c.marca ILIKE ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, "%" + marca + "%");
            try (ResultSet rs = ps.executeQuery()) {
                sb.append("--- RESULTADOS POR MARCA: ").append(marca.toUpperCase()).append(" ---\n\n");
                boolean conResultados = false;
                while (rs.next()) {
                    conResultados = true;
                    sb.append("• Marca: ").append(rs.getString("marca"))
                            .append("  |  Modelo: ").append(rs.getString("modelo"))
                            .append("  |  Precio: $").append(rs.getDouble("precio"))
                            .append("  |  Almacenamiento: ").append(rs.getInt("almacenamiento")).append("GB")
                            .append("  |  RAM: ").append(rs.getInt("ram")).append("GB\n")
                            .append("----------------------------------------------------------------------------------------\n");
                }
                if (!conResultados) return "No se encontraron celulares con la marca: " + marca;
            }
        } catch (SQLException e) {
            return "Error al buscar: " + e.getMessage();
        }
        return sb.toString();
    }
}
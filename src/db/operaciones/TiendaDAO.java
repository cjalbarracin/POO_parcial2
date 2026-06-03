package db.operaciones;

import db.DBConnection;
import model.celular;
import model.inventario;
import java.sql.*;

public class TiendaDAO {

    // 1. Inserción transaccional atómica en dos tablas
    public void registrarCelularCompleto(celular c, inventario i) throws SQLException {
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

                try (ResultSet rs = ps1.getGeneratedKeys()) {
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
                }
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        }
    }

    // 2. Consulta de todo el inventario
    public String obtenerInventarioParaGUI() {
        StringBuilder sb = new StringBuilder();
        String sql = "SELECT c.marca, c.modelo, i.precio, i.almacenamiento, i.ram " +
                "FROM celular c JOIN inventario i ON c.id = i.celular_id ORDER BY c.marca ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            return procesarResultSet(ps, sb);
        } catch (SQLException e) {
            return "Error al leer inventario: " + e.getMessage();
        }
    }

    // 3. Consulta de un único registro por modelo exacto
    public String consultarUnRegistroPorModelo(String modeloBuscar) {
        StringBuilder sb = new StringBuilder();
        String sql = "SELECT c.marca, c.modelo, i.precio, i.almacenamiento, i.ram " +
                "FROM celular c JOIN inventario i ON c.id = i.celular_id " +
                "WHERE LOWER(c.modelo) = LOWER(?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, modeloBuscar);
            return procesarResultSet(ps, sb);
        } catch (SQLException e) {
            return "Error al buscar modelo: " + e.getMessage();
        }
    }

    // 4. Filtro por Marca (Flexible con LIKE)
    public String obtenerFiltroMarcaParaGUI(String marcaCriterio) {
        StringBuilder sb = new StringBuilder();
        String sql = "SELECT c.marca, c.modelo, i.precio, i.almacenamiento, i.ram " +
                "FROM celular c JOIN inventario i ON c.id = i.celular_id " +
                "WHERE LOWER(c.marca) LIKE LOWER(?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + marcaCriterio + "%");
            return procesarResultSet(ps, sb);
        } catch (SQLException e) {
            return "Error al filtrar por marca: " + e.getMessage();
        }
    }

    // 5. Filtro por Rango de Precios
    public String obtenerFiltroPrecioParaGUI(double min, double max) {
        StringBuilder sb = new StringBuilder();
        String sql = "SELECT c.marca, c.modelo, i.precio, i.almacenamiento, i.ram " +
                "FROM celular c JOIN inventario i ON c.id = i.celular_id " +
                "WHERE i.precio BETWEEN ? AND ? ORDER BY i.precio ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, min);
            ps.setDouble(2, max);
            return procesarResultSet(ps, sb);
        } catch (SQLException e) {
            return "Error en filtro de precios: " + e.getMessage();
        }
    }

    // 6. Filtro por Almacenamiento exacto en GB
    public String obtenerFiltroAlmacenamientoParaGUI(int gb) {
        StringBuilder sb = new StringBuilder();
        String sql = "SELECT c.marca, c.modelo, i.precio, i.almacenamiento, i.ram " +
                "FROM celular c JOIN inventario i ON c.id = i.celular_id " +
                "WHERE i.almacenamiento = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, gb);
            return procesarResultSet(ps, sb);
        } catch (SQLException e) {
            return "Error en filtro de almacenamiento: " + e.getMessage();
        }
    }

    private String procesarResultSet(PreparedStatement ps, StringBuilder sb) throws SQLException {
        try (ResultSet rs = ps.executeQuery()) {
            sb.append(String.format("%-15s | %-15s | %-12s | %-12s | %-8s\n", "MARCA", "MODELO", "PRECIO", "ALMACEN.", "RAM"));
            sb.append("-----------------------------------------------------------------------------\n");
            boolean tieneDatos = false;
            while (rs.next()) {
                tieneDatos = true;
                sb.append(String.format("%-15s | %-15s | $%-11.2f | %-4d GB    | %-2d GB\n",
                        rs.getString("marca"),
                        rs.getString("modelo"),
                        rs.getDouble("precio"),
                        rs.getInt("almacenamiento"),
                        rs.getInt("ram")));
            }
            if (!tieneDatos) {
                sb.append("No se encontraron registros que coincidan con la búsqueda seleccionada.");
            }
        }
        return sb.toString();
    }
}
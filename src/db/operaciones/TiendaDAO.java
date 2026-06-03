package db.operaciones;

import db.DBConnection;
import model.celular;
import model.inventario;
import java.sql.*;

public class TiendaDAO {

    // Método privado para evitar repetir código en todas las consultas
    private String ejecutarConsulta(String sql) {
        StringBuilder sb = new StringBuilder();
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            boolean encontrado = false;
            while (rs.next()) {
                encontrado = true;
                sb.append("Marca: ").append(rs.getString("marca"))
                        .append(" | Modelo: ").append(rs.getString("modelo"))
                        .append(" | Cámara: ").append(rs.getInt("camara")).append("MP")
                        .append(" | Batería: ").append(rs.getInt("bateria")).append("mAh")
                        .append(" | Almacenamiento: ").append(rs.getInt("almacenamiento")).append("GB")
                        .append(" | RAM: ").append(rs.getInt("ram")).append("GB")
                        .append(" | Precio: $").append(rs.getDouble("precio")).append("\n");
            }
            return encontrado ? sb.toString() : "No se encontraron resultados.";
        } catch (SQLException e) {
            return "Error en BD: " + e.getMessage();
        }
    }

    // 1. Registro (con transacción para asegurar que se guarden ambos)
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
                    try (PreparedStatement ps2 = conn.prepareStatement(sqlInventario)) {
                        ps2.setInt(1, rs.getInt(1));
                        ps2.setInt(2, i.getAlmacenamiento());
                        ps2.setDouble(3, i.getPrecio());
                        ps2.setInt(4, i.getRam());
                        ps2.executeUpdate();
                    }
                }
                conn.commit();
                return true;
            } catch (SQLException e) { conn.rollback(); return false; }
        } catch (SQLException e) { return false; }
    }

    // 2. Consulta General
    // Consulta General
    public String obtenerInventarioCompletoTexto() {
        return ejecutarConsulta("SELECT c.marca, c.modelo, c.camara, c.bateria, i.almacenamiento, i.precio, i.ram " +
                "FROM celular c JOIN inventario i ON c.id = i.celular_id");
    }

    // Filtro por Marca
    public String filtrarPorMarcaTexto(String marca) {
        return ejecutarConsulta("SELECT c.marca, c.modelo, c.camara, c.bateria, i.almacenamiento, i.precio, i.ram " +
                "FROM celular c JOIN inventario i ON c.id = i.celular_id " +
                "WHERE c.marca ILIKE '%" + marca + "%'");
    }

    // Filtro por Precio Máximo
    public String filtrarPorPrecioMax(double precio) {
        return ejecutarConsulta("SELECT c.marca, c.modelo, c.camara, c.bateria, i.almacenamiento, i.precio, i.ram " +
                "FROM celular c JOIN inventario i ON c.id = i.celular_id " +
                "WHERE i.precio <= " + precio);
    }

    // Filtro por Almacenamiento
    public String filtrarPorAlmacenamiento(int gb) {
        return ejecutarConsulta("SELECT c.marca, c.modelo, c.camara, c.bateria, i.almacenamiento, i.precio, i.ram " +
                "FROM celular c JOIN inventario i ON c.id = i.celular_id " +
                "WHERE i.almacenamiento = " + gb);
    }
}
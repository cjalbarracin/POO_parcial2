package db.operaciones;

import db.DBConnection;
import model.celular;
import model.inventario;
import java.sql.*;

public class TiendaDAO {

    // Une los resultados de la base de datos en un solo texto legible
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

    // Registra el celular y su inventario. Si uno falla, cancela todo (transacción)
    public boolean registrarCelularCompleto(celular c, inventario i) {
        String sqlCelular = "INSERT INTO celular (marca, modelo, camara, bateria) VALUES (?, ?, ?, ?)";
        String sqlInventario = "INSERT INTO inventario (celular_id, almacenamiento, precio, ram) VALUES (?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false); // Iniciamos modo seguro
            try (PreparedStatement ps1 = conn.prepareStatement(sqlCelular, Statement.RETURN_GENERATED_KEYS)) {
                ps1.setString(1, c.getMarca());
                ps1.setString(2, c.getModelo());
                ps1.setInt(3, c.getCamara());
                ps1.setInt(4, c.getBateria());
                ps1.executeUpdate();

                ResultSet rs = ps1.getGeneratedKeys(); // Obtenemos el ID del celular recién creado
                if (rs.next()) {
                    try (PreparedStatement ps2 = conn.prepareStatement(sqlInventario)) {
                        ps2.setInt(1, rs.getInt(1)); // Usamos el ID del celular
                        ps2.setInt(2, i.getAlmacenamiento());
                        ps2.setDouble(3, i.getPrecio());
                        ps2.setInt(4, i.getRam());
                        ps2.executeUpdate();
                    }
                }
                conn.commit(); // Todo OK, guardamos cambios
                return true;
            } catch (SQLException e) {
                conn.rollback(); // Error: revertimos todo
                return false;
            }
        } catch (SQLException e) { return false; }
    }

    // Trae toda la info de ambas tablas unidas
    public String obtenerInventarioCompletoTexto() {
        return ejecutarConsulta("SELECT c.marca, c.modelo, c.camara, c.bateria, i.almacenamiento, i.precio, i.ram " +
                "FROM celular c JOIN inventario i ON c.id = i.celular_id");
    }

    // Busca por marca (ignora mayúsculas/minúsculas)
    public String filtrarPorMarcaTexto(String marca) {
        return ejecutarConsulta("SELECT c.marca, c.modelo, c.camara, c.bateria, i.almacenamiento, i.precio, i.ram " +
                "FROM celular c JOIN inventario i ON c.id = i.celular_id " +
                "WHERE c.marca ILIKE '%" + marca + "%'");
    }

    // Filtra los productos que cuestan igual o menos a la cifra dada
    public String filtrarPorPrecioMax(double precio) {
        return ejecutarConsulta("SELECT c.marca, c.modelo, c.camara, c.bateria, i.almacenamiento, i.precio, i.ram " +
                "FROM celular c JOIN inventario i ON c.id = i.celular_id " +
                "WHERE i.precio <= " + precio);
    }

    // Filtra por capacidad de almacenamiento exacta
    public String filtrarPorAlmacenamiento(int gb) {
        return ejecutarConsulta("SELECT c.marca, c.modelo, c.camara, c.bateria, i.almacenamiento, i.precio, i.ram " +
                "FROM celular c JOIN inventario i ON c.id = i.celular_id " +
                "WHERE i.almacenamiento = " + gb);
    }
}
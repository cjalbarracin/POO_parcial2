import db.DBConnection;
import java.sql.*;

public class InsertCelular {
    public static void main(String[] args) {
        String add_celular = "INSERT INTO celulares (marca, modelo, camara, bateria) VALUES (?,?,?,?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement query_insert = conn.prepareStatement(add_celular)
        ) {
            query_insert.setString(1, "Apple");
            query_insert.setString(2, "iPhone 15");
            query_insert.setInt(3, 48);
            query_insert.setInt(4, 3349);
            query_insert.executeUpdate();

            System.out.println("Celular adicionado con exito");
        }
        catch (SQLException e)
        {
            System.err.println("Error: " + e.getMessage());
        }
    }
}
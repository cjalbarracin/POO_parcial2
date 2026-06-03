import db.DBConnection;
import java.sql.*;

public class ReadCelulares {

    public static void main(String[] args) {

        String read_query = "SELECT * FROM celulares";

        try( Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(read_query)
        )
        {
            System.out.println("ID | MARCA | MODELO | CREADO");
            while (rs.next())
            {
                int id = rs.getInt("id");
                String marca = rs.getString("marca");
                String modelo = rs.getString("modelo");
                Timestamp creado = rs.getTimestamp("creado");

                System.out.println(id + " | " + marca + " | " + modelo + " | " + creado);
            }
        }
        catch (SQLException e)
        {
            System.err.println(e.getMessage());
        }
    }
}
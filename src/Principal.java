import db.operaciones.TiendaDAO;
import model.celular;
import model.inventario;
import java.util.Scanner;
import java.util.List;

public class Principal {
    public static void main(String[] args) {
        TiendaDAO dao = new TiendaDAO();
        Scanner leer = new Scanner(System.in);
        boolean salir = false;

        while (!salir) {
            System.out.println("\n=== MENÚ TIENDA DE CELULARES ===");
            System.out.println("1. Registrar nuevo celular e inventario");
            System.out.println("2. Consultar todos los celulares");
            System.out.println("3. Filtrar celulares por marca");
            System.out.println("4. Salir");
            System.out.print("Seleccione una opción: ");

            int opcion = leer.nextInt();
            leer.nextLine();

            switch (opcion) {
                case 1:
                    registrarNuevo(dao, leer);
                    break;
                case 2:
                    dao.listarInventarioCompleto();
                    break;
                case 3:
                    System.out.print("Ingrese la marca a buscar: ");
                    String marca = leer.nextLine();
                    List<celular> filtrados = dao.filtrarPorMarca(marca);
                    for (celular c : filtrados) {
                        System.out.println(">> " + c.getMarca() + " " + c.getModelo());
                    }
                    break;
                case 4:
                    salir = true;
                    System.out.println("¡Hasta luego!");
                    break;
                default:
                    System.out.println("Opción no válida.");
            }
        }
        leer.close();
    }

    private static void registrarNuevo(TiendaDAO dao, Scanner leer) {
        System.out.println("\n--- REGISTRO DE CELULAR ---");
        System.out.print("Marca: "); String marca = leer.nextLine();
        System.out.print("Modelo: "); String modelo = leer.nextLine();
        System.out.print("Megapíxeles: "); int camara = leer.nextInt();
        System.out.print("Batería: "); int bateria = leer.nextInt();

        System.out.print("Almacenamiento (GB): "); int almc = leer.nextInt();
        System.out.print("Precio: "); double precio = leer.nextDouble();
        System.out.print("RAM (GB): "); int ram = leer.nextInt();

        celular c = new celular(marca, modelo, camara, bateria);
        inventario i = new inventario(0, almc, precio, ram);

        dao.registrarCelularCompleto(c, i);
    }
}
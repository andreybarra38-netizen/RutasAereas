package cr.ac.una.rutasaereas;

import cr.ac.una.rutasaereas.datos.CargadorDatos;
import cr.ac.una.rutasaereas.datos.ErrorCargaException;
import cr.ac.una.rutasaereas.modelo.Aeropuerto;
import cr.ac.una.rutasaereas.modelo.Grafo;
import cr.ac.una.rutasaereas.modelo.Ruta;
import java.io.File;
import java.util.List;
import java.util.Scanner;

public class RutasAereas {

    public static void main(String[] args) {
        Grafo grafo;
        try {
            grafo = CargadorDatos.cargar(new File("datos/aeropuertos.txt"),
                                         new File("datos/rutas.txt"));
        } catch (ErrorCargaException e) {
            // Si los datos estan mal, se informa y se termina sin "caerse"
            System.out.println("ERROR al cargar los datos: " + e.getMessage());
            return;
        }
        System.out.println("Datos cargados: " + grafo.getCantidadAeropuertos()
                + " aeropuertos y " + grafo.getCantidadRutas() + " rutas.");

        Scanner teclado = new Scanner(System.in);
        while (true) {
            System.out.print("\nCodigo de aeropuerto (o 'salir'): ");
            if (!teclado.hasNextLine()) {
                break;
            }
            String codigo = teclado.nextLine().trim();
            if (codigo.equalsIgnoreCase("salir")) {
                break;
            }
            consultarAeropuerto(grafo, codigo);
        }
    }

    private static void consultarAeropuerto(Grafo grafo, String codigo) {
        Aeropuerto a = grafo.getAeropuerto(codigo);
        if (a == null) {
            System.out.println("No existe ningun aeropuerto con el codigo '" + codigo + "'.");
            return;
        }
        System.out.println(a.getCodigo() + " - " + a.getCiudadLegible() + ", " + a.getPaisLegible());
        System.out.println("Posicion en el mapa: (" + a.getX() + ", " + a.getY() + ")");

        List<Ruta> rutas = grafo.getRutasSalientes(codigo);
        if (rutas.isEmpty()) {
            System.out.println("No salen vuelos de este aeropuerto.");
            return;
        }
        System.out.println("Rutas que salen (" + rutas.size() + "):");
        for (Ruta r : rutas) {
            Aeropuerto destino = grafo.getAeropuerto(r.getDestino());
            System.out.printf("  -> %-4s %-22s %4d min   $%d%n",
                    r.getDestino(), destino.getCiudadLegible(), r.getMinutos(), r.getCosto());
        }
    }
}
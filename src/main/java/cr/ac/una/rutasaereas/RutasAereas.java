package cr.ac.una.rutasaereas;

import cr.ac.una.rutasaereas.algoritmos.BuscadorRutas;
import cr.ac.una.rutasaereas.algoritmos.ResultadoRuta;
import cr.ac.una.rutasaereas.datos.CargadorDatos;
import cr.ac.una.rutasaereas.datos.ErrorCargaException;
import cr.ac.una.rutasaereas.modelo.Aeropuerto;
import cr.ac.una.rutasaereas.modelo.Grafo;
import cr.ac.una.rutasaereas.modelo.Ruta;
import cr.ac.una.rutasaereas.algoritmos.Criterio;
import java.io.File;
import java.util.List;
import java.util.Scanner;

public class RutasAereas {
    
    private static final Scanner teclado = new Scanner(System.in);
    
    public static void main(String[] args) {
        Grafo grafo;
        try {
            grafo = CargadorDatos.cargar(new File("datos/aeropuertos.txt"),
                                         new File("datos/rutas.txt"));
        } catch (ErrorCargaException e) {
            System.out.println("ERROR al cargar los datos: " + e.getMessage());
            return;
        }
        System.out.println("Datos cargados: " + grafo.getCantidadAeropuertos()
                + " aeropuertos y " + grafo.getCantidadRutas() + " rutas.");

        while (true) {
            System.out.println("\n===== MENU =====");
            System.out.println("1. Consultar aeropuerto");
            System.out.println("2. Ruta con menos escalas (BFS)");
            System.out.println("3. Ruta mas rapida (Dijkstra por minutos)");
            System.out.println("4. Ruta mas barata (Dijkstra por costo)");
            System.out.println("0. Salir");
            String opcion = leer("Opcion: ");
            if (opcion == null || opcion.equals("0")) {
                break;
            }
            switch (opcion) {
                case "1": {
                    String c = leer("Codigo: ");
                    consultarAeropuerto(grafo, c);
                    break;
                }
                case "2": {
                    String[] od = pedirOrigenDestino(grafo);
                    if (od != null) {
                        mostrarResultado("Menos escalas", BuscadorRutas.menosEscalas(grafo, od[0], od[1]));
                    }
                    break;
                }
                case "3":
                case "4": {
                    String[] od = pedirOrigenDestino(grafo);
                    if (od != null) {
                        Criterio c = opcion.equals("3") ? Criterio.TIEMPO : Criterio.COSTO;
                        mostrarResultado(c.toString(), BuscadorRutas.dijkstra(grafo, od[0], od[1], c));
                    }
                    break;
                }
            }
        }
    }

    //Muestra un mensaje y lee una linea; devuelve null si se acabo la entrada
    private static String leer(String mensaje) {
        System.out.print(mensaje);
        if (!teclado.hasNextLine()) {
            return null;
        }
        return teclado.nextLine().trim();
    }

    //Pide origen, destino y los valida que existan y que sean
    
    private static String[] pedirOrigenDestino(Grafo grafo) {
        String o = leer("Origen: ");
        String d = leer("Destino: ");
        if (o == null || d == null) {
            return null;
        }
        Aeropuerto ao = grafo.getAeropuerto(o);
        Aeropuerto ad = grafo.getAeropuerto(d);
        if (ao == null) {
            System.out.println("No existe ningun aeropuerto con el codigo '" + o + "'.");
            return null;
        }
        if (ad == null) {
            System.out.println("No existe ningun aeropuerto con el codigo '" + d + "'.");
            return null;
        }
        if (ao == ad) {
            System.out.println("El origen y el destino son el mismo aeropuerto.");
            return null;
        }
        return new String[]{ao.getCodigo(), ad.getCodigo()};
    }

    //Muestra la secuencia, la cantidad de vuelos y los totales de minutos y costo
     
    private static void mostrarResultado(String titulo, ResultadoRuta res) {
        System.out.println("\n--- " + titulo + ": " + res.getOrigen() + " -> " + res.getDestino() + " ---");
        if (!res.existe()) {
            System.out.println("No existe ruta de " + res.getOrigen() + " a " + res.getDestino() + ".");
            return;
        }
        System.out.println("Secuencia: " + res.getSecuencia());
        for (Ruta r : res.getTramos()) {
            System.out.printf("   %s -> %s  %4d min  $%d%n",
                    r.getOrigen(), r.getDestino(), r.getMinutos(), r.getCosto());
        }
        System.out.println("Vuelos:  " + res.getCantidadVuelos());
        System.out.println("Minutos: " + res.getTotalMinutos());
        System.out.println("Costo:   $" + res.getTotalCosto());
    }

    //Muestra los datos de un aeropuerto y todas sus rutas salientes con minutos y costo
    
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
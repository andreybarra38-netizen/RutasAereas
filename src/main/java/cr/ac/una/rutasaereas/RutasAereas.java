package cr.ac.una.rutasaereas;

import cr.ac.una.rutasaereas.algoritmos.Alcanzabilidad;
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
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

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
            System.out.println("\n===== MENU =====  (rutas activas: " + grafo.getCantidadRutas()
                    + ", canceladas: " + grafo.getRutasCanceladas().size() + ")");
            System.out.println("1. Consultar aeropuerto");
            System.out.println("2. Ruta con menos escalas (BFS)");
            System.out.println("3. Ruta mas rapida (Dijkstra por minutos)");
            System.out.println("4. Ruta mas barata (Dijkstra por costo)");
            System.out.println("5. Destinos alcanzables desde un origen (DFS)");
            System.out.println("6. Aeropuertos que no pueden llegar a un destino");
            System.out.println("7. Rutas criticas desde un origen");
            System.out.println("8. Cancelar una ruta");
            System.out.println("9. Restaurar todas las rutas canceladas");
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
                case "5": {
                    String o = pedirAeropuerto(grafo, "Origen: ");
                    if (o != null) {
                        mostrarAlcanzables(grafo, o);
                    }
                    break;
                }
                case "6": {
                    String d = pedirAeropuerto(grafo, "Destino: ");
                    if (d != null) {
                        List<String> no = Alcanzabilidad.noPuedenLlegarA(grafo, d);
                        System.out.println(no.isEmpty()
                                ? "Desde todos los aeropuertos se puede llegar a " + d + "."
                                : "No pueden llegar a " + d + " (" + no.size() + "): " + no);
                    }
                    break;
                }
                case "7": {
                    String o = pedirAeropuerto(grafo, "Origen: ");
                    if (o != null) {
                        Map<Ruta, Set<String>> criticas = Alcanzabilidad.rutasCriticas(grafo, o);
                        if (criticas.isEmpty()) {
                            System.out.println("Ninguna ruta, cancelada sola, deja aeropuertos inalcanzables desde " + o + ".");
                        }
                        for (Map.Entry<Ruta, Set<String>> e : criticas.entrySet()) {
                            System.out.println("  Cancelar " + e.getKey().getOrigen() + " -> "
                                    + e.getKey().getDestino() + " deja sin llegar a: " + e.getValue());
                        }
                    }
                    break;
                }
                case "8": {
                    String o = leer("Origen de la ruta: ");
                    String d = leer("Destino de la ruta: ");
                    if (o != null && d != null) {
                        if (grafo.cancelarRuta(o, d)) {
                            System.out.println("Ruta " + o.toUpperCase() + " -> " + d.toUpperCase() + " cancelada.");
                        } else {
                            System.out.println("No existe una ruta activa de '" + o + "' a '" + d + "'.");
                        }
                    }
                    break;
                }
                case "9": {
                    int n = grafo.restaurarRutas();
                    System.out.println("Se restauraron " + n + " ruta(s).");
                    break;
                }
                default:
                    System.out.println("Opcion no valida.");
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

    //Pide un codigo y verifica que exista; devuelve el codigo en mayusculas o null
    
    private static String pedirAeropuerto(Grafo grafo, String mensaje) {
        String c = leer(mensaje);
        if (c == null) {
            return null;
        }
        Aeropuerto a = grafo.getAeropuerto(c);
        if (a == null) {
            System.out.println("No existe ningun aeropuerto con el codigo '" + c + "'.");
            return null;
        }
        return a.getCodigo();
    }

    //Lista los aeropuertos alcanzables desde el origen y los que no
    
    private static void mostrarAlcanzables(Grafo grafo, String origen) {
        Set<String> alcanzables = Alcanzabilidad.alcanzables(grafo, origen);
        StringBuilder si = new StringBuilder();
        StringBuilder no = new StringBuilder();
        int cuantosSi = 0;
        int cuantosNo = 0;
        for (Aeropuerto a : grafo.getAeropuertos()) {
            if (a.getCodigo().equals(origen)) {
                continue;
            }
            if (alcanzables.contains(a.getCodigo())) {
                si.append(a.getCodigo()).append(' ');
                cuantosSi++;
            } else {
                no.append(a.getCodigo()).append(' ');
                cuantosNo++;
            }
        }
        System.out.println("Alcanzables desde " + origen + " (" + cuantosSi + "): " + si);
        System.out.println("NO alcanzables (" + cuantosNo + "): " + (cuantosNo == 0 ? "ninguno" : no));
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
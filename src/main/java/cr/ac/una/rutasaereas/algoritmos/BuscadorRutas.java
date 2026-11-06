package cr.ac.una.rutasaereas.algoritmos;

import cr.ac.una.rutasaereas.modelo.Grafo;
import cr.ac.una.rutasaereas.modelo.Ruta;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;

public class BuscadorRutas {

    public static ResultadoRuta menosEscalas(Grafo g, String origen, String destino) {
        Queue<String> cola = new ArrayDeque<>();      // FIFO
        Set<String> visitados = new HashSet<>();      // aeropuertos ya descubiertos
        Map<String, Ruta> llegoPor = new HashMap<>(); // codigo -> ultima ruta usada para llegar

        cola.add(origen);
        visitados.add(origen);

        while (!cola.isEmpty()) {
            String actual = cola.poll();
            if (actual.equals(destino)) {
                break; //LLega al destino pero al ser BFS es con la menor cantidad de vuelos
            }
            for (Ruta r : g.getRutasSalientes(actual)) {
                String vecino = r.getDestino();
                if (!visitados.contains(vecino)) {
                    visitados.add(vecino);   // se marca al encolar para no meterlo dos veces
                    llegoPor.put(vecino, r);
                    cola.add(vecino);
                }
            }
        }

        if (!visitados.contains(destino)) {
            return new ResultadoRuta(origen, destino, null); // no hay ruta
        }
        return new ResultadoRuta(origen, destino, reconstruir(llegoPor, origen, destino));
    }
        /*Ruta de menor peso total usando algoritmo de dijkstra. El mismo metodo sirve para la ruta mas rapida 
        y para la mas barata cambiando solo el criterio*/
    
    public static ResultadoRuta dijkstra(Grafo g, String origen, String destino, Criterio criterio) {
        Map<String, Integer> distancia = new HashMap<>(); //La mejor distancia conocida desde el punto de origen 
        Map<String, Ruta> llegoPor = new HashMap<>();
        Set<String> cerrados = new HashSet<>();           // aeropuertos con distancia definitiva

        // Cola de prioridad que saca siempre la entrada con menor distancia 
        PriorityQueue<Entrada> cola = new PriorityQueue<>();

        distancia.put(origen, 0);
        cola.add(new Entrada(origen, 0));

        while (!cola.isEmpty()) {
            Entrada e = cola.poll();
            String actual = e.codigo;
            if (cerrados.contains(actual)) {
                continue; //Entra al viaje y procesa como una menor distancia 
            }
            cerrados.add(actual);
            if (actual.equals(destino)) {
                break; //Ya definio la menor distancia de forma definitiva
            }
            for (Ruta r : g.getRutasSalientes(actual)) {
                String vecino = r.getDestino();
                if (cerrados.contains(vecino)) {
                    continue;
                }
                int nueva = e.distancia + criterio.peso(r); // usa el criterio
                Integer vieja = distancia.get(vecino);      // null = no hay un camino conocido "infinito"
                if (vieja == null || nueva < vieja) {
                    distancia.put(vecino, nueva);
                    llegoPor.put(vecino, r);
                    cola.add(new Entrada(vecino, nueva));
                }
            }
        }

        if (!distancia.containsKey(destino)) {
            return new ResultadoRuta(origen, destino, null);
        }
        return new ResultadoRuta(origen, destino, reconstruir(llegoPor, origen, destino));
    }

    //Arma la lista de vuelos caminando hacia atras desde el destino
    
    private static List<Ruta> reconstruir(Map<String, Ruta> llegoPor, String origen, String destino) {
        List<Ruta> camino = new ArrayList<>();
        String actual = destino;
        while (!actual.equals(origen)) {
            Ruta r = llegoPor.get(actual);
            camino.add(r);
            actual = r.getOrigen();
        }
        Collections.reverse(camino);
        return camino;
    }
     //Cola de prioridad de Dijkstra encuentra un aeropuerto con su distancia y la guarda en base a ello 
    
    private static class Entrada implements Comparable<Entrada> {
        final String codigo;
        final int distancia;

        Entrada(String codigo, int distancia) {
            this.codigo = codigo;
            this.distancia = distancia;
        }

        @Override
        public int compareTo(Entrada otra) {
            return Integer.compare(this.distancia, otra.distancia);
        }
    }
}
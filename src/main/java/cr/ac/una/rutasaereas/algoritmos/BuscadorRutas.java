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
}
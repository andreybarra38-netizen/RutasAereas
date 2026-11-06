package cr.ac.una.rutasaereas.algoritmos;

import cr.ac.una.rutasaereas.modelo.Aeropuerto;
import cr.ac.una.rutasaereas.modelo.Grafo;
import cr.ac.una.rutasaereas.modelo.Ruta;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

// Consultas de alcanzabilidad sobre el grafo 
public class Alcanzabilidad {

    // DFS con PILA: devuelve los aeropuertos a los que se puede llegar desde el origen
    // incluyendo al propio origen Complejidad O(n + m).
    public static Set<String> alcanzables(Grafo g, String origen) {
        Set<String> visitados = new LinkedHashSet<>(); // conserva el orden de visita
        Deque<String> pila = new ArrayDeque<>();       // LIFO

        pila.push(origen);
        while (!pila.isEmpty()) {
            String actual = pila.pop();
            if (visitados.contains(actual)) {
                continue; // ya se habia visitado por otro camino
            }
            visitados.add(actual);
            for (Ruta r : g.getRutasSalientes(actual)) {
                if (!visitados.contains(r.getDestino())) {
                    pila.push(r.getDestino());
                }
            }
        }
        return visitados;
    }

    // Aeropuertos desde los cuales no se puede llegar al destino
    // Hace un DFS desde cada aeropuerto y revisa si aparece el destino. O(n * (n + m)).
    public static List<String> noPuedenLlegarA(Grafo g, String destino) {
        String d = g.getAeropuerto(destino).getCodigo();
        List<String> resultado = new ArrayList<>();
        for (Aeropuerto a : g.getAeropuertos()) {
            if (!a.getCodigo().equals(d) && !alcanzables(g, a.getCodigo()).contains(d)) {
                resultado.add(a.getCodigo());
            }
        }
        return resultado;
    }

    // Rutas criticas desde un origen: las que, si se cancelan, dejan algun aeropuerto inalcanzable.
    // Prueba cada ruta: la cancela, repite el DFS, compara y la restaura. O(m * (n + m)).
    public static Map<Ruta, Set<String>> rutasCriticas(Grafo g, String origen) {
        Set<String> antes = alcanzables(g, origen);

        // Copia de todas las rutas, no se puede recorrer una lista mientras se le quitan elementos
        List<Ruta> todas = new ArrayList<>();
        for (Aeropuerto a : g.getAeropuertos()) {
            todas.addAll(g.getRutasSalientes(a.getCodigo()));
        }

        Map<Ruta, Set<String>> criticas = new LinkedHashMap<>();
        for (Ruta r : todas) {
            g.cancelarRuta(r.getOrigen(), r.getDestino());
            Set<String> despues = alcanzables(g, origen);
            g.restaurarUltima(); // deja el grafo como estaba
            if (despues.size() < antes.size()) {
                Set<String> perdidos = new LinkedHashSet<>(antes);
                perdidos.removeAll(despues);
                criticas.put(r, perdidos);
            }
        }
        return criticas;
    }
}
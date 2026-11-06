package cr.ac.una.rutasaereas.algoritmos;

import cr.ac.una.rutasaereas.modelo.Aeropuerto;
import cr.ac.una.rutasaereas.modelo.Grafo;
import cr.ac.una.rutasaereas.modelo.Ruta;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;


public class Alcanzabilidad {

    /*Aeropuertos a los que se puede llegar desde elpunto de  origen, con cualquier
     cantidad de escalas, usando el DFS,recorrido en profundidad con una pila*/
    
    public static Set<String> alcanzables(Grafo g, String origen) {
        Set<String> visitados = new LinkedHashSet<>(); //conserva el orden de visita
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

    /*Aeropuertos desde los cuales no se puede llegar al destino.Se hace un
     DFS desde cada aeropuerto y se revisa si el destino aparece.*/
    
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
}
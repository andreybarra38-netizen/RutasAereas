package cr.ac.una.rutasaereas.modelo;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Grafo {

    // Vertices: codigo a aeropuerto. LinkedHashMap conserva el orden del archivo
    private final Map<String, Aeropuerto> aeropuertos = new LinkedHashMap<>();

    // Lista de adyacencia: codigo a rutas que salen de ese aeropuerto
    private final Map<String, List<Ruta>> adyacencia = new HashMap<>();

    // Cantidad total de rutas activas/aristas
    private int cantidadRutas = 0;

    // Rutas canceladas como pila, la ultima cancelada queda arriba
    // Cada una guarda la posicion que tenia en su lista para devolverla al mismo lugar
    private final Deque<RutaCancelada> canceladas = new ArrayDeque<>();

    // Agrega un aeropuerto vertice con su lista de rutas vacia y da error si el codigo esta repetido
    public void agregarAeropuerto(Aeropuerto a) {
        String codigo = normalizar(a.getCodigo());
        if (aeropuertos.containsKey(codigo)) {
            throw new IllegalArgumentException("El aeropuerto " + codigo + " esta repetido");
        }
        aeropuertos.put(codigo, a);
        adyacencia.put(codigo, new ArrayList<>());
    }

    // Agrega una ruta, arista dirigida a la lista del origeny da error si el origen o destino no existen
    public void agregarRuta(Ruta r) {
        String origen = normalizar(r.getOrigen());
        String destino = normalizar(r.getDestino());
        if (!existeAeropuerto(origen)) {
            throw new IllegalArgumentException("No existe el aeropuerto de origen " + origen);
        }
        if (!existeAeropuerto(destino)) {
            throw new IllegalArgumentException("No existe el aeropuerto de destino " + destino);
        }
        adyacencia.get(origen).add(r);
        cantidadRutas++;
    }

    // Indica si existe un aeropuerto con ese codigo
    public boolean existeAeropuerto(String codigo) {
        return codigo != null && aeropuertos.containsKey(normalizar(codigo));
    }

    // Busca un aeropuerto por codigo, devuelve null si no existe
    public Aeropuerto getAeropuerto(String codigo) {
        if (codigo == null) {
            return null;
        }
        return aeropuertos.get(normalizar(codigo));
    }

    // Rutas que salen de un aeropuerto, no modificable desde afuera y es vacia si no existe
    public List<Ruta> getRutasSalientes(String codigo) {
        if (codigo == null) {
            return Collections.emptyList();
        }
        List<Ruta> lista = adyacencia.get(normalizar(codigo));
        if (lista == null) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(lista);
    }

    // Todos los aeropuertos en el orden del archivo
    public Collection<Aeropuerto> getAeropuertos() {
        return Collections.unmodifiableCollection(aeropuertos.values());
    }

    // Cantidad de aeropuertos (n)
    public int getCantidadAeropuertos() {
        return aeropuertos.size();
    }

    // Cantidad de rutas activas (m)
    public int getCantidadRutas() {
        return cantidadRutas;
    }

    // Cancela la ruta origen a destino, la quita de la lista de adyacencia (el archivo no se toca).
    // Devuelve false si esa ruta no existe o ya estaba cancelada.
    public boolean cancelarRuta(String origen, String destino) {
        if (origen == null || destino == null) {
            return false;
        }
        List<Ruta> lista = adyacencia.get(normalizar(origen));
        if (lista == null) {
            return false; // el origen no existe
        }
        String d = normalizar(destino);
        for (int i = 0; i < lista.size(); i++) {
            if (normalizar(lista.get(i).getDestino()).equals(d)) {
                Ruta r = lista.remove(i);
                canceladas.push(new RutaCancelada(r, i));
                cantidadRutas--;
                return true;
            }
        }
        return false;
    }

    // Restaura solo la ultima ruta cancelada, la de arriba de la pila, en su posicion original
    public boolean restaurarUltima() {
        if (canceladas.isEmpty()) {
            return false;
        }
        RutaCancelada c = canceladas.pop();
        adyacencia.get(normalizar(c.ruta.getOrigen())).add(c.posicion, c.ruta);
        cantidadRutas++;
        return true;
    }

    // Restaura todas las rutas canceladas, en orden inverso; devuelve cuantas restauro
    public int restaurarRutas() {
        int cuantas = 0;
        while (restaurarUltima()) {
            cuantas++;
        }
        return cuantas;
    }

    // Devuelve las rutas canceladas (para mostrarlas o dibujarlas distinto)
    public List<Ruta> getRutasCanceladas() {
        List<Ruta> lista = new ArrayList<>();
        for (RutaCancelada c : canceladas) {
            lista.add(c.ruta);
        }
        return lista;
    }

    // Pone el codigo en mayusculas y sin espacios ("sjo" y "SJO" son el mismo)
    private static String normalizar(String codigo) {
        return codigo.trim().toUpperCase();
    }

    // Una ruta cancelada junto con la posicion que tenia en su lista
    private static class RutaCancelada {
        final Ruta ruta;
        final int posicion;

        RutaCancelada(Ruta ruta, int posicion) {
            this.ruta = ruta;
            this.posicion = posicion;
        }
    }
}
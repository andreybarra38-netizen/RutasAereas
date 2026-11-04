package cr.ac.una.rutasaereas.modelo;

import java.util.ArrayList; 
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Grafo {

    private final Map<String, Aeropuerto> aeropuertos = new LinkedHashMap<>();
    private final Map<String, List<Ruta>> adyacencia = new HashMap<>();
    private int cantidadRutas = 0;

    public void agregarAeropuerto(Aeropuerto a) {
        String codigo = normalizar(a.getCodigo());
        if (aeropuertos.containsKey(codigo)) {
            throw new IllegalArgumentException("El aeropuerto " + codigo + " esta repetido");
        }
        aeropuertos.put(codigo, a);
        adyacencia.put(codigo, new ArrayList<>());
    }

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

    public boolean existeAeropuerto(String codigo) {
        return codigo != null && aeropuertos.containsKey(normalizar(codigo));
    }

    public Aeropuerto getAeropuerto(String codigo) {
        if (codigo == null) {
            return null;
        }
        return aeropuertos.get(normalizar(codigo));
    }

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

    public Collection<Aeropuerto> getAeropuertos() {
        return Collections.unmodifiableCollection(aeropuertos.values());
    }

    public int getCantidadAeropuertos() {
        return aeropuertos.size();
    }

    public int getCantidadRutas() {
        return cantidadRutas;
    }

    private static String normalizar(String codigo) {
        return codigo.trim().toUpperCase();
    }
}
package cr.ac.una.rutasaereas.algoritmos;

import cr.ac.una.rutasaereas.modelo.Ruta;

/*Esta clase lo que busca es un criterio ya sea tiempo o costo para evitar tener dos algoritmos de Dijkstra
usando como parametro el peso de cada arista*/

public enum Criterio {
    
    TIEMPO("Mas rapida"),
    COSTO("Mas barata");

    private final String nombre;

    Criterio(String nombre) {
        this.nombre = nombre;
    }

    // Devuelve el peso de una ruta segun este criterio.
    public int peso(Ruta r) {
        if (this == TIEMPO) {
            return r.getMinutos();
        }
        return r.getCosto();
    }

    @Override
    public String toString() {
        return nombre;
    }
}
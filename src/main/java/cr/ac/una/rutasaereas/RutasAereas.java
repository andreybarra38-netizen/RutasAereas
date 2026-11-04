package cr.ac.una.rutasaereas;

import cr.ac.una.rutasaereas.modelo.Aeropuerto;
import cr.ac.una.rutasaereas.modelo.Grafo;
import cr.ac.una.rutasaereas.modelo.Ruta;

public class RutasAereas {

    public static void main(String[] args) {

        Grafo g = new Grafo();
        g.agregarAeropuerto(new Aeropuerto("SJO", 464, 447, "San_Jose", "Costa_Rica"));
        g.agregarAeropuerto(new Aeropuerto("MIA", 508, 278, "Miami", "EE.UU."));
        g.agregarAeropuerto(new Aeropuerto("PTY", 518, 456, "Ciudad_de_Panama", "Panama"));

        g.agregarRuta(new Ruta("SJO", "MIA", 165, 190));
        g.agregarRuta(new Ruta("SJO", "PTY", 75, 110));
        g.agregarRuta(new Ruta("MIA", "SJO", 170, 185));

        System.out.println("Aeropuertos: " + g.getCantidadAeropuertos()
                + "  Rutas: " + g.getCantidadRutas());

        System.out.println("\nRutas que salen de SJO:");
        for (Ruta r : g.getRutasSalientes("SJO")) {
            System.out.println("  " + r);
        }

        System.out.println("\nRutas que salen de PTY (ninguna):");
        System.out.println("  " + g.getRutasSalientes("PTY"));

        System.out.println("\nBuscar 'sjo' en minusculas: " + g.getAeropuerto("sjo"));
        System.out.println("Buscar 'XXX' (no existe):   " + g.getAeropuerto("XXX"));

        // Una ruta hacia un aeropuerto que no existe debe dar un error claro
        try {
            g.agregarRuta(new Ruta("SJO", "XXX", 100, 100));
        } catch (IllegalArgumentException e) {
            System.out.println("\nError esperado: " + e.getMessage());
        }
    }
}
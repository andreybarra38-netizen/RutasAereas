package cr.ac.una.rutasaereas;

import cr.ac.una.rutasaereas.modelo.Aeropuerto;
import cr.ac.una.rutasaereas.modelo.Ruta;

public class RutasAereas {

    public static void main(String[] args) {
        
        Aeropuerto sjo = new Aeropuerto("SJO", 464, 447, "San_Jose", "Costa_Rica");
        Ruta r = new Ruta("SJO", "MIA", 165, 190);

        System.out.println(sjo);
        System.out.println(r);
        System.out.println("Ciudad legible: " + sjo.getCiudadLegible());
    }
}

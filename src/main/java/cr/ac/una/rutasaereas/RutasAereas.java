package cr.ac.una.rutasaereas;

import java.io.File;

public class RutasAereas {

    public static void main(String[] args) {
    
        File aeropuertos = new File("datos/aeropuertos.txt");
        File rutas = new File("datos/rutas.txt");

        System.out.println("Carpeta de trabajo: " + new File(".").getAbsolutePath());
        System.out.println("aeropuertos.txt existe? " + aeropuertos.exists());
        System.out.println("rutas.txt existe?       " + rutas.exists());
    }
}

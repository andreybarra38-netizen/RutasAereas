package cr.ac.una.rutasaereas;

import java.io.File;

/**
 * Clase principal del proyecto Rutas Aereas (EIF207, Proyecto II).
 * <p>
 * Por ahora solo verifica que el programa encuentre la carpeta de datos.
 *
 * @author Nombre Integrante 1
 * @author Nombre Integrante 2
 */
public class RutasAereas {

    /**
     * Punto de entrada del programa.
     *
     * @param args no se usan
     */
    public static void main(String[] args) {
        // Ruta RELATIVA: NetBeans ejecuta el programa desde la raiz del proyecto,
        // asi que funciona en cualquier computadora (no depende de C:\Users\...).
        File aeropuertos = new File("datos/aeropuertos.txt");
        File rutas = new File("datos/rutas.txt");

        System.out.println("Carpeta de trabajo: " + new File(".").getAbsolutePath());
        System.out.println("aeropuertos.txt existe? " + aeropuertos.exists());
        System.out.println("rutas.txt existe?       " + rutas.exists());
    }
}

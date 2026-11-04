package cr.ac.una.rutasaereas.datos;

import cr.ac.una.rutasaereas.modelo.Aeropuerto;
import cr.ac.una.rutasaereas.modelo.Grafo;
import cr.ac.una.rutasaereas.modelo.Ruta;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.InputMismatchException;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class CargadorDatos {

    public static Grafo cargar(File archivoAeropuertos, File archivoRutas) throws ErrorCargaException {
        Grafo grafo = new Grafo();
        leerAeropuertos(archivoAeropuertos, grafo);
        leerRutas(archivoRutas, grafo);
        return grafo;
    }

    private static void leerAeropuertos(File archivo, Grafo grafo) throws ErrorCargaException {
        String nombre = archivo.getName();
        int numero = 0; // cuantos aeropuertos se han leido, para los mensajes de error

        try (Scanner sc = new Scanner(archivo, "UTF-8")) {
            int cantidad = leerCantidad(sc, nombre);
            for (numero = 1; numero <= cantidad; numero++) {
                String codigo = sc.next().toUpperCase();
                int x = sc.nextInt();
                int y = sc.nextInt();
                String ciudad = sc.next();
                String pais = sc.next();
                grafo.agregarAeropuerto(new Aeropuerto(codigo, x, y, ciudad, pais));
            }
        } catch (FileNotFoundException e) {
            throw new ErrorCargaException("No se encontro el archivo: " + archivo.getAbsolutePath());
        } catch (InputMismatchException e) {

            throw new ErrorCargaException(nombre + ": el aeropuerto #" + numero
                    + " tiene un dato que deberia ser numero (x o y)");
        } catch (NoSuchElementException e) {
            throw new ErrorCargaException(nombre + ": el archivo termina antes de tiempo;"
                    + " faltan datos en el aeropuerto #" + numero);
        } catch (IllegalArgumentException e) {

            throw new ErrorCargaException(nombre + ": " + e.getMessage());
        }
    }

     private static void leerRutas(File archivo, Grafo grafo) throws ErrorCargaException {
        String nombre = archivo.getName();
        int numero = 0;

        try (Scanner sc = new Scanner(archivo, "UTF-8")) {
            int cantidad = leerCantidad(sc, nombre);
            for (numero = 1; numero <= cantidad; numero++) {
                String origen = sc.next().toUpperCase();
                String destino = sc.next().toUpperCase();
                int minutos = sc.nextInt();
                int costo = sc.nextInt();
                // Dijkstra solo funciona con pesos no negativos
                if (minutos < 0 || costo < 0) {
                    throw new ErrorCargaException(nombre + ": la ruta #" + numero + " (" + origen
                            + " " + destino + ") tiene minutos o costo negativos");
                }
                grafo.agregarRuta(new Ruta(origen, destino, minutos, costo));
            }
        } catch (FileNotFoundException e) {
            throw new ErrorCargaException("No se encontro el archivo: " + archivo.getAbsolutePath());
        } catch (InputMismatchException e) {
            throw new ErrorCargaException(nombre + ": la ruta #" + numero
                    + " tiene un dato que deberia ser numero (minutos o costo)");
        } catch (NoSuchElementException e) {
            throw new ErrorCargaException(nombre + ": el archivo termina antes de tiempo;"
                    + " faltan datos en la ruta #" + numero);
        } catch (IllegalArgumentException e) {
            // Lanzada por el grafo si la ruta usa un aeropuerto que no existe
            throw new ErrorCargaException(nombre + ": ruta #" + numero + ": " + e.getMessage());
        }
    }

       private static int leerCantidad(Scanner sc, String nombre) throws ErrorCargaException {
        if (!sc.hasNext()) {
            throw new ErrorCargaException(nombre + ": el archivo esta vacio");
        }
        String primero = sc.next().replace("\uFEFF", "");
        try {
            return Integer.parseInt(primero);
        } catch (NumberFormatException e) {
            throw new ErrorCargaException(nombre + ": la primera linea debe ser la cantidad"
                    + " de registros, pero dice '" + primero + "'");
        }
    }
}
package cr.ac.una.rutasaereas.algoritmos;

import cr.ac.una.rutasaereas.modelo.Ruta;
import java.util.Collections;
import java.util.List;

public class ResultadoRuta {

    private final List<Ruta> tramos;
    private final String origen;
    private final String destino;
    private final int totalMinutos;
    private final int totalCosto;

    public ResultadoRuta(String origen, String destino, List<Ruta> tramos) {
        this.origen = origen;
        this.destino = destino;
        this.tramos = tramos;
        int minutos = 0;
        int costo = 0;
        if (tramos != null) {
            for (Ruta r : tramos) {
                minutos += r.getMinutos();
                costo += r.getCosto();
            }
        }
        this.totalMinutos = minutos;
        this.totalCosto = costo;
    }

    public boolean existe() {
        return tramos != null;
    }

    public List<Ruta> getTramos() {
        return tramos == null ? Collections.emptyList() : Collections.unmodifiableList(tramos);
    }

    public String getOrigen() {
        return origen;
    }

    public String getDestino() {
        return destino;
    }

    public int getCantidadVuelos() {
        return getTramos().size();
    }

    public int getTotalMinutos() {
        return totalMinutos;
    }

    public int getTotalCosto() {
        return totalCosto;
    }

    public String getSecuencia() {
        if (!existe()) {
            return "(sin ruta)";
        }
        StringBuilder sb = new StringBuilder(origen);
        for (Ruta r : tramos) {
            sb.append(" > ").append(r.getDestino());
        }
        return sb.toString();
    }
}
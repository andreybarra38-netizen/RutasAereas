package cr.ac.una.rutasaereas.modelo;

public class Aeropuerto {
    
    private final String codigo;
    private final int x;
    private final int y;
    private final String ciudad;
    private final String pais;

    public Aeropuerto(String codigo, int x, int y, String ciudad, String pais) {
        this.codigo = codigo;
        this.x = x;
        this.y = y;
        this.ciudad = ciudad;
        this.pais = pais;
    }

    public String getCodigo() { return codigo; }
    public int getX() { return x; }
    public int getY() { return y; }
    public String getCiudad() { return ciudad; }
    public String getPais() { return pais; }
    public String getCiudadLegible() { return ciudad.replace('_', ' '); }
    public String getPaisLegible() { return pais.replace('_', ' '); }
  
  
    @Override
    public String toString() {
        return codigo + " - " + getCiudadLegible() + ", " + getPaisLegible()
                + " (" + x + ", " + y + ")";
    }
}
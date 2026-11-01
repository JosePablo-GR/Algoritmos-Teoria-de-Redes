import Excepciones.*;

public class Arista {

    private String origen;
    private String destino;
    private double peso;
    private boolean dirigida;

    public Arista (String origen, String destino, double peso, boolean dirigida) throws AristaInvalidaException {

        if (origen == null || destino == null) {
            throw new AristaInvalidaException("Origen o destino no pueden ser null");
        }

        if (origen.trim().isEmpty() || destino.trim().isEmpty()) {
            throw new AristaInvalidaException("Origen o Destino no puede ser vacío");
        }

        this.origen = origen;
        this.destino = destino;
        this.peso = peso;
        this.dirigida = dirigida;

    }

    public String getOrigen () {
        return origen;
    }

    public String getDestino () {
        return destino;
    }

    public double getPeso () {
        return peso;
    }

    public boolean esDirigida () {
        return dirigida;
    }

    private String formatoPeso () {
        if (peso == (int) peso) {
            return String.valueOf((int) peso);
        }
        return String.valueOf(peso);
    }

    @Override
    public String toString () {

        if (dirigida) {
            return origen + " -(" + formatoPeso() + ")-> " + destino;
        } else {
            return origen + " -(" + formatoPeso() + ")- " + destino;
        }
        
    }

}
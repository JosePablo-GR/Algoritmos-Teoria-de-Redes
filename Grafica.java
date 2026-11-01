import Excepciones.*;

public abstract class Grafica {

    protected int totalAristas;
    protected int capacidadMaximaAristas;

    public Grafica (int capacidadMaximaAristas) {

        this.totalAristas = 0;
        this.capacidadMaximaAristas = capacidadMaximaAristas;

    }

    public int getTotalAristas () {
        return totalAristas;
    }

    public int getCapacidadMaximaAristas () {
        return capacidadMaximaAristas;
    }

    // Métodos que toda gráfica debe tener
    public abstract void agregarArista (Arista a) throws GraficaLlenaException, AristaDuplicadaException;

    public abstract void mostrarGrafica();

    public abstract Arista[] obtenerAristas();

}
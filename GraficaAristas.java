import Excepciones.*;

public class GraficaAristas extends Grafica {

    private Arista[] aristas;

    public GraficaAristas (int capacidadMaximaAristas) {

        super(capacidadMaximaAristas);

        aristas = new Arista[capacidadMaximaAristas];

    }

    @Override
    public void agregarArista (Arista a) throws GraficaLlenaException, AristaDuplicadaException {

        if (totalAristas >= capacidadMaximaAristas) {
            throw new GraficaLlenaException("No hay espacio para más aristas");
        }

        // Valida duplicados
        for (int i = 0; i < totalAristas; i++) {

            Arista actual = aristas[i];

            // Caso arista *dirigida*: si tienen misma dirección
            if (a.esDirigida() && actual.esDirigida()) {

                if (actual.getOrigen().equals(a.getOrigen()) && actual.getDestino().equals(a.getDestino())) {
                    throw new AristaDuplicadaException("Arista dirigida duplicada");
                }

            }

            // Caso arista *no dirigida*: A-B == B-A
            if (!a.esDirigida() && !actual.esDirigida()) {

                boolean condicion1 = actual.getOrigen().equals(a.getOrigen()) && actual.getDestino().equals(a.getDestino());
                boolean condicion2 = actual.getOrigen().equals(a.getDestino()) && actual.getDestino().equals(a.getOrigen());

                boolean misma = condicion1 || condicion2;

                if (misma) {
                    throw new AristaDuplicadaException("Arista no dirigida duplicada");
                }

            }

        }

        aristas[totalAristas] = a;
        totalAristas++;

    }

    @Override
    public void mostrarGrafica () {

        for (int i = 0; i < totalAristas; i++) {
            System.out.println(aristas[i]);
        }
        
    }

    @Override
    public Arista[] obtenerAristas () {
        return aristas;
    }

}
import Excepciones.*;

public class MetodoFordFulkerson implements BaseAlgoritmos {

    private String fuente;
    private String sumidero;
    private static final int INFINITO = 999999;

    public MetodoFordFulkerson (String fuente, String sumidero) {

        this.fuente = fuente;
        this.sumidero = sumidero;

    }

    @Override
    public void ejecutar (Grafica grafica) throws AlgoritmoNoCompatibleException {

        Arista[] aristas = grafica.obtenerAristas();
        int totalAristas = grafica.getTotalAristas();

        if (totalAristas == 0) {
            throw new AlgoritmoNoCompatibleException("La gráfica no tiene aristas");
        }

        validarRed(aristas, totalAristas);

        String[] vertices = new String[totalAristas * 2];
        int totalVertices = obtenerVertices(aristas, totalAristas, vertices);

        int s = indice(vertices, totalVertices, fuente);
        int t = indice(vertices, totalVertices, sumidero);

        if (s == -1 || t == -1) {
            throw new AlgoritmoNoCompatibleException("La fuente o el sumidero no existen en la gráfica");
        }

        int[][] capacidad = construirMatrizCapacidad(aristas, totalAristas, vertices, totalVertices);
        int[][] flujo = new int[totalVertices][totalVertices];

        int iteracion = 1;
        boolean hayCadenaAumentante = true;

        while (hayCadenaAumentante) {

            System.out.println("\nIteración " + iteracion);

            boolean[] etiquetado = new boolean[totalVertices];
            boolean[] examinado = new boolean[totalVertices];
            int[] anterior = new int[totalVertices];
            int[] valorEtiqueta = new int[totalVertices];
            char[] signo = new char[totalVertices];

            inicializarArreglos(anterior, valorEtiqueta, signo, totalVertices);

            etiquetado[s] = true;
            anterior[s] = s;
            valorEtiqueta[s] = INFINITO;
            signo[s] = 'S';

            etiquetarVertices(capacidad, flujo, vertices, etiquetado, examinado, anterior, valorEtiqueta, signo, totalVertices, t);

            imprimirEtiquetas(vertices, etiquetado, anterior, valorEtiqueta, signo, totalVertices);

            if (!etiquetado[t]) {

                hayCadenaAumentante = false;

                System.out.println("\nYa no se pudo etiquetar al sumidero " + sumidero + ".");
                System.out.println("Entonces el flujo actual es máximo.");
                System.out.println("Valor del flujo máximo: " + calcularValorFlujo(flujo, s, totalVertices));

                imprimirFlujo(aristas, totalAristas, vertices, totalVertices, flujo);
                imprimirCorte(vertices, etiquetado, totalVertices);

            } else {

                int aumento = valorEtiqueta[t];

                System.out.println("\nCadena aumentante:");
                imprimirCadena(vertices, anterior, signo, s, t);

                System.out.println("Aumento permitido: " + aumento);

                actualizarFlujo(flujo, anterior, signo, s, t, aumento);

                imprimirFlujo(aristas, totalAristas, vertices, totalVertices, flujo);

                iteracion++;

            }

        }

    }

    private void validarRed (Arista[] aristas, int totalAristas) throws AlgoritmoNoCompatibleException {

        for (int i = 0; i < totalAristas; i++) {

            if (!aristas[i].esDirigida()) {
                throw new AlgoritmoNoCompatibleException("Ford-Fulkerson necesita arcos dirigidos");
            }

            if (aristas[i].getPeso() < 0) {
                throw new AlgoritmoNoCompatibleException("Las capacidades no pueden ser negativas");
            }

        }

    }

    private int obtenerVertices (Arista[] aristas, int totalAristas, String[] vertices) {

        int totalVertices = 0;

        for (int i = 0; i < totalAristas; i++) {

            String origen = aristas[i].getOrigen();
            String destino = aristas[i].getDestino();

            if (!existe(vertices, totalVertices, origen)) {
                vertices[totalVertices] = origen;
                totalVertices++;
            }

            if (!existe(vertices, totalVertices, destino)) {
                vertices[totalVertices] = destino;
                totalVertices++;
            }

        }

        return totalVertices;

    }

    private int[][] construirMatrizCapacidad (Arista[] aristas, int totalAristas, String[] vertices, int totalVertices) {

        int[][] capacidad = new int[totalVertices][totalVertices];

        for (int i = 0; i < totalAristas; i++) {

            int origen = indice(vertices, totalVertices, aristas[i].getOrigen());
            int destino = indice(vertices, totalVertices, aristas[i].getDestino());

            capacidad[origen][destino] = aristas[i].getPeso();

        }

        return capacidad;

    }

    private void inicializarArreglos (int[] anterior, int[] valorEtiqueta, char[] signo, int totalVertices) {

        for (int i = 0; i < totalVertices; i++) {

            anterior[i] = -1;
            valorEtiqueta[i] = 0;
            signo[i] = ' ';

        }

    }

    private void etiquetarVertices (int[][] capacidad, int[][] flujo, String[] vertices, boolean[] etiquetado, boolean[] examinado, int[] anterior, int[] valorEtiqueta, char[] signo, int totalVertices, int t) {

        while (!etiquetado[t]) {

            int j = buscarVerticeEtiquetadoNoExaminado(etiquetado, examinado, totalVertices);

            if (j == -1) {
                break;
            }

            etiquetarSucesores(capacidad, flujo, vertices, etiquetado, anterior, valorEtiqueta, signo, totalVertices, j);
            etiquetarPredecesores(capacidad, flujo, vertices, etiquetado, anterior, valorEtiqueta, signo, totalVertices, j);

            examinado[j] = true;

        }

    }

    private int buscarVerticeEtiquetadoNoExaminado (boolean[] etiquetado, boolean[] examinado, int totalVertices) {

        for (int i = 0; i < totalVertices; i++) {

            if (etiquetado[i] && !examinado[i]) {
                return i;
            }

        }

        return -1;

    }

    private void etiquetarSucesores (int[][] capacidad, int[][] flujo, String[] vertices, boolean[] etiquetado, int[] anterior, int[] valorEtiqueta, char[] signo, int totalVertices, int j) {

        for (int i = 0; i < totalVertices; i++) {

            if (!etiquetado[i] && capacidad[j][i] > 0 && flujo[j][i] < capacidad[j][i]) {

                etiquetado[i] = true;
                anterior[i] = j;
                signo[i] = '+';
                valorEtiqueta[i] = minimo(valorEtiqueta[j], capacidad[j][i] - flujo[j][i]);

                System.out.println("Se etiqueta " + vertices[i] + " con [+" + vertices[j] + "," + valorEtiqueta[i] + "]");

            }

        }

    }

    private void etiquetarPredecesores (int[][] capacidad, int[][] flujo, String[] vertices, boolean[] etiquetado, int[] anterior, int[] valorEtiqueta, char[] signo, int totalVertices, int j) {

        for (int i = 0; i < totalVertices; i++) {

            if (!etiquetado[i] && capacidad[i][j] > 0 && flujo[i][j] > 0) {

                etiquetado[i] = true;
                anterior[i] = j;
                signo[i] = '-';
                valorEtiqueta[i] = minimo(valorEtiqueta[j], flujo[i][j]);

                System.out.println("Se etiqueta " + vertices[i] + " con [-" + vertices[j] + "," + valorEtiqueta[i] + "]");

            }

        }

    }

    private void actualizarFlujo (int[][] flujo, int[] anterior, char[] signo, int s, int t, int aumento) {

        int actual = t;

        while (actual != s) {

            int previo = anterior[actual];

            if (signo[actual] == '+') {
                flujo[previo][actual] = flujo[previo][actual] + aumento;
            } else {
                flujo[actual][previo] = flujo[actual][previo] - aumento;
            }

            actual = previo;

        }

    }

    private int calcularValorFlujo (int[][] flujo, int s, int totalVertices) {

        int valor = 0;

        for (int i = 0; i < totalVertices; i++) {
            valor = valor + flujo[s][i];
            valor = valor - flujo[i][s];
        }

        return valor;

    }

    private void imprimirEtiquetas (String[] vertices, boolean[] etiquetado, int[] anterior, int[] valorEtiqueta, char[] signo, int totalVertices) {

        System.out.println("\nEtiquetas finales de la iteración:");

        for (int i = 0; i < totalVertices; i++) {

            System.out.print(vertices[i] + ": ");

            if (!etiquetado[i]) {
                System.out.println("sin etiqueta");
            } else if (signo[i] == 'S') {
                System.out.println("[s,∞]");
            } else {
                System.out.println("[" + signo[i] + vertices[anterior[i]] + "," + valorEtiqueta[i] + "]");
            }

        }

    }

    private void imprimirCadena (String[] vertices, int[] anterior, char[] signo, int s, int t) {

        int[] camino = new int[vertices.length];
        char[] signosCamino = new char[vertices.length];
        int total = 0;

        int actual = t;

        while (actual != s) {

            camino[total] = actual;
            signosCamino[total] = signo[actual];
            actual = anterior[actual];
            total++;

        }

        camino[total] = s;

        for (int i = total; i >= 0; i--) {

            System.out.print(vertices[camino[i]]);

            if (i > 0) {

                if (signosCamino[i - 1] == '+') {
                    System.out.print(" -> ");
                } else {
                    System.out.print(" <- ");
                }

            }

        }

        System.out.println();

    }

    private void imprimirFlujo (Arista[] aristas, int totalAristas, String[] vertices, int totalVertices, int[][] flujo) {

        System.out.println("\nFlujo actual:");

        for (int i = 0; i < totalAristas; i++) {

            int origen = indice(vertices, totalVertices, aristas[i].getOrigen());
            int destino = indice(vertices, totalVertices, aristas[i].getDestino());

            System.out.println(aristas[i].getOrigen() + " -> " + aristas[i].getDestino() + " : " + flujo[origen][destino] + " / " + aristas[i].getPeso());

        }

    }

    private void imprimirCorte (String[] vertices, boolean[] etiquetado, int totalVertices) {

        System.out.print("\nN = { ");

        for (int i = 0; i < totalVertices; i++) {

            if (etiquetado[i]) {
                System.out.print(vertices[i] + " ");
            }

        }

        System.out.println("}");

        System.out.print("N barra = { ");

        for (int i = 0; i < totalVertices; i++) {

            if (!etiquetado[i]) {
                System.out.print(vertices[i] + " ");
            }

        }

        System.out.println("}");

    }

    private boolean existe (String[] arreglo, int total, String valor) {

        for (int i = 0; i < total; i++) {

            if (arreglo[i].equals(valor)) {
                return true;
            }

        }

        return false;

    }

    private int indice (String[] arreglo, int total, String valor) {

        for (int i = 0; i < total; i++) {

            if (arreglo[i].equals(valor)) {
                return i;
            }

        }

        return -1;

    }

    private int minimo (int a, int b) {

        if (a < b) {
            return a;
        }

        return b;

    }

}
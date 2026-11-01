import Excepciones.*;

public class MetodoDijkstra implements BaseAlgoritmos {

    private String origen;
    private String destino;
    private boolean usarDestino;

    public MetodoDijkstra(String origen) {
        this.origen = origen;
        this.destino = null;
        this.usarDestino = false;
    }

    public MetodoDijkstra(String origen, String destino) {
        this.origen = origen;
        this.destino = destino;
        this.usarDestino = true;
    }

    @Override
    public void ejecutar(Grafica grafica) throws AlgoritmoNoCompatibleException {

        Arista[] aristas = grafica.obtenerAristas();
        int totalAristas = grafica.getTotalAristas();

        if (totalAristas == 0) {
            throw new AlgoritmoNoCompatibleException("La gráfica no tiene aristas");
        }

        validarPesos(aristas, totalAristas);

        String[] vertices = obtenerVertices(aristas, totalAristas);
        int totalVertices = contarVertices(vertices);

        int indiceOrigen = indiceDe(vertices, totalVertices, origen);

        if (indiceOrigen == -1) {
            throw new AlgoritmoNoCompatibleException("El vértice origen no existe");
        }

        if (usarDestino) {
            int indiceDestino = indiceDe(vertices, totalVertices, destino);

            if (indiceDestino == -1) {
                throw new AlgoritmoNoCompatibleException("El vértice destino no existe");
            }
        }

        double[] distancia = new double[totalVertices];
        boolean[] permanente = new boolean[totalVertices];
        String[] anterior = new String[totalVertices];

        int maxEtiquetas = totalAristas * 2 + totalVertices + 5;
        String[][] etiquetaAnterior = new String[totalVertices][maxEtiquetas];
        double[][] etiquetaCosto = new double[totalVertices][maxEtiquetas];
        int[] totalEtiquetas = new int[totalVertices];
        int[] mejorEtiqueta = new int[totalVertices];

        inicializar(distancia, permanente, anterior, vertices, totalVertices, indiceOrigen, etiquetaAnterior, etiquetaCosto, totalEtiquetas, mejorEtiqueta);

        System.out.println("ALGORITMO DE DIJKSTRA");
        System.out.println("Raíz s = " + origen);

        if (usarDestino) {
            System.out.println("Destino t = " + destino);
        } else {
            System.out.println("Se buscará la arborescencia de rutas más cortas.");
        }

        System.out.println();

        imprimirInicializacion(vertices, permanente, etiquetaAnterior, etiquetaCosto, totalEtiquetas, mejorEtiqueta, totalVertices);

        int p = indiceOrigen;
        int iteracion = 1;

        while (true) {

            System.out.println("Iteración " + iteracion);
            System.out.println("p = " + vertices[p]);
            System.out.println();

            imprimirSucesores(aristas, totalAristas, vertices, totalVertices, p);

            actualizarEtiquetas(aristas, totalAristas, vertices, totalVertices, p, distancia, permanente, anterior, etiquetaAnterior, etiquetaCosto, totalEtiquetas, mejorEtiqueta);

            imprimirEstadoEtiquetas(vertices, permanente, etiquetaAnterior, etiquetaCosto, totalEtiquetas, mejorEtiqueta, totalVertices);

            int xEstrella = obtenerMenorTemporal(distancia, permanente, totalVertices);

            if (xEstrella == -1) {
                System.out.println("No quedan etiquetas temporales alcanzables.");
                break;
            }

            if (distancia[xEstrella] == Double.POSITIVE_INFINITY) {
                System.out.println("d(x*) = ∞");
                System.out.println("No existe arborescencia de raíz " + origen);
                break;
            }

            System.out.println("Escogemos la etiqueta [" + anterior[xEstrella] + ", " + formatoNumero(distancia[xEstrella]) + "] del nodo " + vertices[xEstrella] + " porque es la etiqueta temporal de menor costo.");
            System.out.println("Se marca el nodo " + vertices[xEstrella] + " como permanente.");
            System.out.println();

            permanente[xEstrella] = true;
            p = xEstrella;

            if (usarDestino && vertices[p].equals(destino)) {
                System.out.println("Como p = t = " + destino + ", se termina el algoritmo.");
                System.out.println();
                break;
            }

            if (todosPermanentes(permanente, totalVertices)) {
                System.out.println("Todos los vértices tienen etiqueta permanente.");
                System.out.println();
                break;
            }

            iteracion++;
        }

        if (usarDestino) {
            imprimirRuta(vertices, distancia, anterior, totalVertices, destino);
        } else {
            imprimirArborescencia(vertices, distancia, anterior, totalVertices);
        }
    }

    private void validarPesos(Arista[] aristas, int totalAristas) throws AlgoritmoNoCompatibleException {

        for (int i = 0; i < totalAristas; i++) {
            if (aristas[i].getPeso() < 0) {
                throw new AlgoritmoNoCompatibleException("Dijkstra no acepta pesos negativos");
            }
        }
    }

    private String[] obtenerVertices(Arista[] aristas, int totalAristas) {

        String[] vertices = new String[totalAristas * 2];
        int totalVertices = 0;

        for (int i = 0; i < totalAristas; i++) {
            String origenArista = aristas[i].getOrigen();
            String destinoArista = aristas[i].getDestino();

            if (!existe(vertices, totalVertices, origenArista)) {
                vertices[totalVertices] = origenArista;
                totalVertices++;
            }

            if (!existe(vertices, totalVertices, destinoArista)) {
                vertices[totalVertices] = destinoArista;
                totalVertices++;
            }
        }

        return vertices;
    }

    private int contarVertices(String[] vertices) {

        int contador = 0;

        for (int i = 0; i < vertices.length; i++) {
            if (vertices[i] != null) {
                contador++;
            }
        }

        return contador;
    }

    private void inicializar(double[] distancia, boolean[] permanente, String[] anterior, String[] vertices, int totalVertices, int indiceOrigen, String[][] etiquetaAnterior, double[][] etiquetaCosto, int[] totalEtiquetas, int[] mejorEtiqueta) {

        for (int i = 0; i < totalVertices; i++) {
            distancia[i] = Double.POSITIVE_INFINITY;
            permanente[i] = false;
            anterior[i] = vertices[i];

            etiquetaAnterior[i][0] = vertices[i];
            etiquetaCosto[i][0] = Double.POSITIVE_INFINITY;
            totalEtiquetas[i] = 1;
            mejorEtiqueta[i] = 0;
        }

        distancia[indiceOrigen] = 0;
        permanente[indiceOrigen] = true;
        anterior[indiceOrigen] = vertices[indiceOrigen];

        etiquetaAnterior[indiceOrigen][0] = vertices[indiceOrigen];
        etiquetaCosto[indiceOrigen][0] = 0;
        mejorEtiqueta[indiceOrigen] = 0;
    }

    private void actualizarEtiquetas(Arista[] aristas, int totalAristas, String[] vertices, int totalVertices, int p, double[] distancia, boolean[] permanente, String[] anterior, String[][] etiquetaAnterior, double[][] etiquetaCosto, int[] totalEtiquetas, int[] mejorEtiqueta) {

        boolean huboActualizacion = false;

        System.out.println("Desde " + vertices[p] + " podemos etiquetar a sus sucesores temporales.");
        System.out.println();

        for (int i = 0; i < totalAristas; i++) {
            Arista a = aristas[i];

            int indiceOrigen = indiceDe(vertices, totalVertices, a.getOrigen());
            int indiceDestino = indiceDe(vertices, totalVertices, a.getDestino());

            if (indiceOrigen == p && !permanente[indiceDestino]) {
                actualizarEtiqueta(vertices, p, indiceDestino, a.getPeso(), distancia, anterior, etiquetaAnterior, etiquetaCosto, totalEtiquetas, mejorEtiqueta);
                huboActualizacion = true;
            }

            if (!a.esDirigida() && indiceDestino == p && !permanente[indiceOrigen]) {
                actualizarEtiqueta(vertices, p, indiceOrigen, a.getPeso(), distancia, anterior, etiquetaAnterior, etiquetaCosto, totalEtiquetas, mejorEtiqueta);
                huboActualizacion = true;
            }
        }

        if (!huboActualizacion) {
            System.out.println("No hay etiquetas temporales que actualizar desde " + vertices[p] + ".");
            System.out.println();
        }
    }

    private void actualizarEtiqueta(String[] vertices, int p, int x, double peso, double[] distancia, String[] anterior, String[][] etiquetaAnterior, double[][] etiquetaCosto, int[] totalEtiquetas, int[] mejorEtiqueta) {

        if (distancia[p] == Double.POSITIVE_INFINITY) {
            return;
        }

        double distanciaAnterior = distancia[x];
        double nuevaDistancia = distancia[p] + peso;

        System.out.println("Para el nodo " + vertices[x] + ":");
        System.out.println("Nueva etiqueta propuesta: [" + vertices[p] + ", " + formatoNumero(distancia[p]) + " + " + formatoNumero(peso) + "] = [" + vertices[p] + ", " + formatoNumero(nuevaDistancia) + "]");

        int indiceNuevaEtiqueta = agregarEtiqueta(x, vertices[p], nuevaDistancia, etiquetaAnterior, etiquetaCosto, totalEtiquetas);

        if (nuevaDistancia < distanciaAnterior) {
            distancia[x] = nuevaDistancia;
            anterior[x] = vertices[p];
            mejorEtiqueta[x] = indiceNuevaEtiqueta;

            if (distanciaAnterior == Double.POSITIVE_INFINITY) {
                System.out.println("Como " + formatoNumero(nuevaDistancia) + " < ∞, se agrega la etiqueta [" + vertices[p] + ", " + formatoNumero(nuevaDistancia) + "].");
            } else {
                System.out.println("Como " + formatoNumero(nuevaDistancia) + " < " + formatoNumero(distanciaAnterior) + ", ahora conviene llegar a " + vertices[x] + " desde " + vertices[p] + ".");
                System.out.println("Se agrega la etiqueta [" + vertices[p] + ", " + formatoNumero(nuevaDistancia) + "] y pasa a ser la mejor etiqueta de " + vertices[x] + ".");
            }
        } else {
            System.out.println("Como " + formatoNumero(nuevaDistancia) + " no mejora a " + formatoDistancia(distanciaAnterior) + ", la etiqueta ganadora sigue siendo [" + anterior[x] + ", " + formatoDistancia(distancia[x]) + "].");
        }

        System.out.println();
    }

    private int agregarEtiqueta(int nodo, String anterior, double costo, String[][] etiquetaAnterior, double[][] etiquetaCosto, int[] totalEtiquetas) {

        int posicion = totalEtiquetas[nodo];

        etiquetaAnterior[nodo][posicion] = anterior;
        etiquetaCosto[nodo][posicion] = costo;
        totalEtiquetas[nodo]++;

        return posicion;
    }

    private int obtenerMenorTemporal(double[] distancia, boolean[] permanente, int totalVertices) {

        double menor = Double.POSITIVE_INFINITY;
        int indiceMenor = -1;

        for (int i = 0; i < totalVertices; i++) {
            if (!permanente[i] && distancia[i] < menor) {
                menor = distancia[i];
                indiceMenor = i;
            }
        }

        return indiceMenor;
    }

    private boolean todosPermanentes(boolean[] permanente, int totalVertices) {

        for (int i = 0; i < totalVertices; i++) {
            if (!permanente[i]) {
                return false;
            }
        }

        return true;
    }

    private boolean existe(String[] arr, int total, String valor) {

        for (int i = 0; i < total; i++) {
            if (arr[i].equals(valor)) {
                return true;
            }
        }

        return false;
    }

    private int indiceDe(String[] arr, int total, String valor) {

        for (int i = 0; i < total; i++) {
            if (arr[i].equals(valor)) {
                return i;
            }
        }

        return -1;
    }

    private void imprimirInicializacion(String[] vertices, boolean[] permanente, String[][] etiquetaAnterior, double[][] etiquetaCosto, int[] totalEtiquetas, int[] mejorEtiqueta, int totalVertices) {

        System.out.println("Inicialización de etiquetas:");
        System.out.println("Etiquetamos al origen " + origen + " con [" + origen + ", 0] y lo marcamos como permanente.");
        System.out.println("A todos los demás nodos los etiquetamos inicialmente con [consigo mismo, ∞].");
        System.out.println();

        imprimirEtiquetas(vertices, permanente, etiquetaAnterior, etiquetaCosto, totalEtiquetas, mejorEtiqueta, totalVertices);
        System.out.println();
    }

    private void imprimirSucesores(Arista[] aristas, int totalAristas, String[] vertices, int totalVertices, int p) {

        System.out.print("Γ+(" + vertices[p] + ") = {");

        boolean primero = true;

        for (int i = 0; i < totalAristas; i++) {
            Arista a = aristas[i];

            int indiceOrigen = indiceDe(vertices, totalVertices, a.getOrigen());
            int indiceDestino = indiceDe(vertices, totalVertices, a.getDestino());

            if (indiceOrigen == p) {
                if (!primero) {
                    System.out.print(", ");
                }

                System.out.print(vertices[indiceDestino]);
                primero = false;
            }

            if (!a.esDirigida() && indiceDestino == p) {
                if (!primero) {
                    System.out.print(", ");
                }

                System.out.print(vertices[indiceOrigen]);
                primero = false;
            }
        }

        System.out.println("}");
        System.out.println();
    }

    private void imprimirEstadoEtiquetas(String[] vertices, boolean[] permanente, String[][] etiquetaAnterior, double[][] etiquetaCosto, int[] totalEtiquetas, int[] mejorEtiqueta, int totalVertices) {

        System.out.println("Etiquetas acumuladas:");
        imprimirEtiquetas(vertices, permanente, etiquetaAnterior, etiquetaCosto, totalEtiquetas, mejorEtiqueta, totalVertices);
        System.out.println();
    }

    private void imprimirEtiquetas(String[] vertices, boolean[] permanente, String[][] etiquetaAnterior, double[][] etiquetaCosto, int[] totalEtiquetas, int[] mejorEtiqueta, int totalVertices) {

        for (int i = 0; i < totalVertices; i++) {
            System.out.println("Nodo " + vertices[i] + ":");

            for (int j = 0; j < totalEtiquetas[i]; j++) {
                System.out.print("[" + etiquetaAnterior[i][j] + ", " + formatoDistancia(etiquetaCosto[i][j]) + "]");

                if (permanente[i] && j == mejorEtiqueta[i]) {
                    System.out.print(" *");
                }

                System.out.println();
            }

            System.out.println();
        }
    }

    private void imprimirRuta(String[] vertices, double[] distancia, String[] anterior, int totalVertices, String destino) {

        int indiceDestino = indiceDe(vertices, totalVertices, destino);

        System.out.println("Resultado final:");

        if (indiceDestino == -1 || distancia[indiceDestino] == Double.POSITIVE_INFINITY) {
            System.out.println("No existe ruta de " + origen + " a " + destino);
            return;
        }

        System.out.println("Longitud de la ruta más corta:");
        System.out.println("d(" + destino + ") = " + formatoNumero(distancia[indiceDestino]));
        System.out.println();

        System.out.println("Recuperación de la ruta usando la mejor etiqueta permanente:");

        String[] ruta = new String[totalVertices];
        int contador = 0;

        String actual = destino;

        while (!actual.equals(origen)) {
            ruta[contador] = actual;
            contador++;

            int indiceActual = indiceDe(vertices, totalVertices, actual);

            System.out.println(actual + " viene de " + anterior[indiceActual]);

            actual = anterior[indiceActual];
        }

        ruta[contador] = origen;
        contador++;

        System.out.println();

        System.out.println("Ruta más corta:");

        for (int i = contador - 1; i >= 0; i--) {
            System.out.print(ruta[i]);

            if (i > 0) {
                System.out.print(" -> ");
            }
        }

        System.out.println();
    }

    private void imprimirArborescencia(String[] vertices, double[] distancia, String[] anterior, int totalVertices) {

        System.out.println("Resultado final:");
        System.out.println("Arborescencia de rutas más cortas con raíz " + origen + ":");

        for (int i = 0; i < totalVertices; i++) {
            if (!vertices[i].equals(origen) && distancia[i] != Double.POSITIVE_INFINITY) {
                System.out.println("(" + anterior[i] + ", " + vertices[i] + ")");
            }
        }

        System.out.println();

        System.out.println("Distancias finales:");

        for (int i = 0; i < totalVertices; i++) {
            System.out.println("d(" + vertices[i] + ") = " + formatoDistancia(distancia[i]));
        }
    }

    private String formatoDistancia(double valor) {

        if (valor == Double.POSITIVE_INFINITY) {
            return "∞";
        }

        return formatoNumero(valor);
    }

    private String formatoNumero(double valor) {

        if (valor == (int) valor) {
            return String.valueOf((int) valor);
        }

        return String.valueOf(valor);
    }
}
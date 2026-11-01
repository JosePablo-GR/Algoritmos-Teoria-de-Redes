import Excepciones.*;

public class MetodoKruskal implements BaseAlgoritmos {

    @Override
    public void ejecutar (Grafica grafica) throws AlgoritmoNoCompatibleException {

        Arista[] aristas = grafica.obtenerAristas();
        int n = grafica.getTotalAristas();

        if (n == 0) {
            throw new AlgoritmoNoCompatibleException("La gráfica no tiene aristas");
        }

        //No permitir aristas dirigidas
        for (int i = 0; i < n; i++) {

            if (aristas[i].esDirigida()) {

                throw new AlgoritmoNoCompatibleException(
                    "Kruskal usa aristas no dirigidas"
                );

            }

        }

        System.out.println("ALGORITMO DE KRUSKAL");

        // Paso 1: ordenar A de forma creciente respecto al peso
        for (int i = 0; i < n - 1; i++) {

            for (int j = 0; j < n - i - 1; j++) {

                if (aristas[j].getPeso() > aristas[j + 1].getPeso()) {

                    Arista temp = aristas[j];
                    aristas[j] = aristas[j + 1];
                    aristas[j + 1] = temp;

                }

            }

        }

        // Paso 2: construir el conjunto de vértices X a partir de las aristas de A
        String[] vertices = new String[n * 2];
        int totalVertices = 0;

        for (int i = 0; i < n; i++) {
            String o = aristas[i].getOrigen();
            String d = aristas[i].getDestino();

            if (!existe(vertices, totalVertices, o)) {
                vertices[totalVertices++] = o;
            }
            if (!existe(vertices, totalVertices, d)) {
                vertices[totalVertices++] = d;
            }
        }

        // Paso 3: inicializar componentes para detectar ciclos en A'
        int[] padre = new int[totalVertices];

        for (int i = 0; i < totalVertices; i++) {
            padre[i] = i;
        }

        // Paso 4: si no forma ciclo, agregarlo
        for (int i = 0; i < n; i++) {

            int idxO = indice(vertices, totalVertices, aristas[i].getOrigen());
            int idxD = indice(vertices, totalVertices, aristas[i].getDestino());
 
            int rootO = encontrar(padre, idxO);
            int rootD = encontrar(padre, idxD);

            if (rootO != rootD) {
                System.out.println(aristas[i]);
                unir(padre, rootO, rootD);
            }

        }

    }

    // Verifica si un vértice ya pertenece al conjunto X
    private boolean existe (String[] arr, int tam, String val) {

        for (int i = 0; i < tam; i++) {

            if (arr[i].equals(val)) {

                return true;

            }

        }

        return false;

    }

    // Obtiene la posición de un vértice dentro del conjunto X
    private int indice (String[] arr, int tam, String val) {

        for (int i = 0; i < tam; i++) {

            if (arr[i].equals(val)) {
                return i;
            }

        }

        return -1;

    }

    // a+ b = Dos nodos están conectados?

    // a
    private int encontrar (int[] padre, int i) {

        if (padre[i] == i){
            return i;
        }

        return encontrar(padre, padre[i]);

    } // Encuentra la componente a la que pertenece un vértice

    // b
    private void unir (int[] padre, int a, int b) {
        padre[a] = b;
    } // Une dos componentes cuando se agrega una arista a A'

}
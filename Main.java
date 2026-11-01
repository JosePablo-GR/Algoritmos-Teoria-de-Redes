//import Excepciones.*;

public class Main {

    public static void main (String[] fin_dSemestre) {

        try {

            GraficaAristas g = new GraficaAristas(10);
            GraficaAristas g2 = new GraficaAristas(30);
            GraficaAristas g3 = new GraficaAristas(6);
            GraficaAristas g4 = new GraficaAristas(17);

            // Gráfica no dirigida
            g.agregarArista(new Arista("1","2",1.3,false));
            g.agregarArista(new Arista("1","3",2.1,false));
            g.agregarArista(new Arista("1","4",0.9,false));
            g.agregarArista(new Arista("1","5",0.9,false));
            g.agregarArista(new Arista("2","3",0.9,false));
            g.agregarArista(new Arista("2","4",1.7,false));
            g.agregarArista(new Arista("2","5",1.2,false));
            g.agregarArista(new Arista("3","4",2.0,false));
            g.agregarArista(new Arista("3","5",0.7,false));
            g.agregarArista(new Arista("4","5",0.7,false));

            // Gráfica dirigida
            g2.agregarArista(new Arista("1","3",3,true));
            g2.agregarArista(new Arista("1","2",0,true));
            g2.agregarArista(new Arista("1","4",1,true));
            g2.agregarArista(new Arista("2","3",1,true));
            g2.agregarArista(new Arista("3","2",1,true));
            g2.agregarArista(new Arista("2","4",1,true));
            g2.agregarArista(new Arista("3","5",4,true));
            g2.agregarArista(new Arista("5","3",4,true));
            g2.agregarArista(new Arista("3","6",2,true));
            g2.agregarArista(new Arista("4","6",5,true));
            g2.agregarArista(new Arista("4","7",3,true));
            g2.agregarArista(new Arista("7","4",1,true));
            g2.agregarArista(new Arista("6","7",3,true));
            g2.agregarArista(new Arista("6","8",1,true));
            g2.agregarArista(new Arista("7","8",1,true));
            g2.agregarArista(new Arista("8","9",9,true));
            g2.agregarArista(new Arista("7","9",9,true));

            // Gráfica dirigida
            g3.agregarArista(new Arista("0","1",4,true));
            g3.agregarArista(new Arista("0","2",8,true));
            g3.agregarArista(new Arista("0","3",16,true));
            g3.agregarArista(new Arista("1","2",5,true));
            g3.agregarArista(new Arista("1","3",11,true));
            g3.agregarArista(new Arista("2","3",6,true));

            // Gráfica no dirigida
            g4.agregarArista(new Arista("2","3",0,false));
            g4.agregarArista(new Arista("2","8",1,false));
            g4.agregarArista(new Arista("1","2",2,false));
            g4.agregarArista(new Arista("8","4",2,false));
            g4.agregarArista(new Arista("1","8",3,false));
            g4.agregarArista(new Arista("5","8",3,false));
            g4.agregarArista(new Arista("8","9",3,false));
            g4.agregarArista(new Arista("1","5",4,false));
            g4.agregarArista(new Arista("3","4",4,false));
            g4.agregarArista(new Arista("5","6",5,false));
            g4.agregarArista(new Arista("7","9",5,false));
            g4.agregarArista(new Arista("3","8",6,false));
            g4.agregarArista(new Arista("6","7",7,false));
            g4.agregarArista(new Arista("3","9",7,false));
            g4.agregarArista(new Arista("8","6",8,false));
            g4.agregarArista(new Arista("4","7",9,false));
            g4.agregarArista(new Arista("3","5",10,false));

            // Método Kruskal

            
            System.out.println("Gráfica:");
            g4.mostrarGrafica();
            System.out.println();
            
            BaseAlgoritmos kruskal = new MetodoKruskal();
            kruskal.ejecutar(g4);

            System.out.println();

            // Método Dijkstra
            /*
            System.out.println("Gráfica:");
            g3.mostrarGrafica();
            System.out.println();

            BaseAlgoritmos dijkstra = new MetodoDijkstra("0", "3");
            dijkstra.ejecutar(g3);
            */

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }

}
package datastructures;
// Grafo no dirigido y ponderado con lista de adyacencia.
// Nodos identificados por String (ID de atraccion).
// Incluye Dijkstra para ruta optima y BFS para conectividad.

public class Grafo {

    private static class Arista {
        final String destino;
        final double peso;

        Arista(String destino, double peso) {
            this.destino = destino;
            this.peso = peso;
        }
    }

    // Entrada para la cola de prioridad interna de Dijkstra
    private static class EntradaDijkstra implements Comparable<EntradaDijkstra> {
        final String nodo;
        final double distancia;

        EntradaDijkstra(String nodo, double distancia) {
            this.nodo = nodo;
            this.distancia = distancia;
        }

        @Override
        public int compareTo(EntradaDijkstra otra) {
            return Double.compare(this.distancia, otra.distancia);
        }
    }

    private static class NodoGrafo {
        final String id;
        final ListaEnlazada<Arista> vecinos = new ListaEnlazada<>();

        NodoGrafo(String id) { this.id = id; }
    }

    private final ListaEnlazada<NodoGrafo> nodos = new ListaEnlazada<>();

    public void agregarNodo(String id) {
        if (buscarNodo(id) == null) nodos.agregar(new NodoGrafo(id));
    }

    public void agregarArista(String origenId, String destinoId, double peso) {
        agregarNodo(origenId);
        agregarNodo(destinoId);
        buscarNodo(origenId).vecinos.agregar(new Arista(destinoId, peso));
        buscarNodo(destinoId).vecinos.agregar(new Arista(origenId, peso));
    }

    // Devuelve el camino mas corto (lista de IDs) de origen a destino.
    // Retorna lista vacia si no existe camino.
    public ListaEnlazada<String> caminoMasCorto(String origenId, String destinoId) {
        ArbolBST<String, Double> distancias = new ArbolBST<>();
        ArbolBST<String, String>  anterior  = new ArbolBST<>();
        ColaPrioridad<EntradaDijkstra> pendiente = new ColaPrioridad<>();

        for (NodoGrafo n : nodos) distancias.insertar(n.id, Double.MAX_VALUE);
        distancias.insertar(origenId, 0.0);
        pendiente.encolar(new EntradaDijkstra(origenId, 0.0));

        while (!pendiente.estaVacia()) {
            EntradaDijkstra actual = pendiente.desencolar();
            if (actual.nodo.equals(destinoId)) break;

            double distActual = distancias.buscar(actual.nodo);
            if (actual.distancia > distActual) continue; // entrada obsoleta

            NodoGrafo nodoActual = buscarNodo(actual.nodo);
            if (nodoActual == null) continue;

            for (Arista arista : nodoActual.vecinos) {
                double nuevaDist = distActual + arista.peso;
                Double distVecino = distancias.buscar(arista.destino);
                if (distVecino != null && nuevaDist < distVecino) {
                    distancias.insertar(arista.destino, nuevaDist);
                    anterior.insertar(arista.destino, actual.nodo);
                    pendiente.encolar(new EntradaDijkstra(arista.destino, nuevaDist));
                }
            }
        }

        return reconstruirCamino(anterior, origenId, destinoId);
    }

    // BFS: devuelve todos los nodos alcanzables desde origen
    public ListaEnlazada<String> bfs(String origenId) {
        ListaEnlazada<String> visitados = new ListaEnlazada<>();
        ListaEnlazada<String> cola      = new ListaEnlazada<>();

        cola.agregar(origenId);
        visitados.agregar(origenId);

        while (!cola.estaVacia()) {
            String actualId = cola.obtener(0);
            cola.eliminar(actualId);

            NodoGrafo actual = buscarNodo(actualId);
            if (actual == null) continue;

            for (Arista arista : actual.vecinos) {
                if (!visitados.contiene(arista.destino)) {
                    visitados.agregar(arista.destino);
                    cola.agregar(arista.destino);
                }
            }
        }
        return visitados;
    }

    public int cantidadNodos() { return nodos.tamano(); }

    private NodoGrafo buscarNodo(String id) {
        for (NodoGrafo n : nodos) if (n.id.equals(id)) return n;
        return null;
    }

    private ListaEnlazada<String> reconstruirCamino(
            ArbolBST<String, String> anterior, String origen, String destino) {

        ListaEnlazada<String> camino = new ListaEnlazada<>();
        String actual = destino;

        while (actual != null && !actual.equals(origen)) {
            camino.agregarAlInicio(actual);
            actual = anterior.buscar(actual);
        }

        if (actual == null) return new ListaEnlazada<>();
        camino.agregarAlInicio(origen);
        return camino;
    }
}


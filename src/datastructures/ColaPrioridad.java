package datastructures;

import java.util.NoSuchElementException;

// Min-heap generico: el elemento de MENOR valor compareTo tiene mayor prioridad.
// Uso en el parque: Fast-Pass (prioridad=1) sale antes que General (prioridad=2).
public class ColaPrioridad<T extends Comparable<T>> {

    private static final int CAPACIDAD_INICIAL = 16;

    @SuppressWarnings("unchecked")
    private T[] heap = (T[]) new Comparable[CAPACIDAD_INICIAL];
    private int tamano = 0;

    public void encolar(T elemento) {
        if (tamano == heap.length) crecer();
        heap[tamano] = elemento;
        subirFlotando(tamano);
        tamano++;
    }

    public T desencolar() {
        if (estaVacia()) throw new NoSuchElementException("Cola vacia");
        T raiz = heap[0];
        heap[0] = heap[--tamano];
        heap[tamano] = null;
        bajarHundiendo(0);
        return raiz;
    }

    public T peek() {
        if (estaVacia()) throw new NoSuchElementException("Cola vacia");
        return heap[0];
    }

    public int tamano() { return tamano; }
    public boolean estaVacia() { return tamano == 0; }

    private void subirFlotando(int i) {
        while (i > 0) {
            int padre = (i - 1) / 2;
            if (heap[i].compareTo(heap[padre]) >= 0) break;
            intercambiar(i, padre);
            i = padre;
        }
    }

    private void bajarHundiendo(int i) {
        while (true) {
            int menor = i;
            int izq = 2 * i + 1, der = 2 * i + 2;
            if (izq < tamano && heap[izq].compareTo(heap[menor]) < 0) menor = izq;
            if (der < tamano && heap[der].compareTo(heap[menor]) < 0) menor = der;
            if (menor == i) break;
            intercambiar(i, menor);
            i = menor;
        }
    }

    private void intercambiar(int a, int b) {
        T tmp = heap[a]; heap[a] = heap[b]; heap[b] = tmp;
    }

    @SuppressWarnings("unchecked")
    private void crecer() {
        T[] nuevo = (T[]) new Comparable[heap.length * 2];
        System.arraycopy(heap, 0, nuevo, 0, tamano);
        heap = nuevo;
    }
}


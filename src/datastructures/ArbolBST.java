package datastructures;

import java.util.function.Consumer;

// Arbol Binario de Busqueda generico clave-valor.
// Uso en el parque: ArbolBST<String, Atraccion> para buscar por ID o nombre.
public class ArbolBST<K extends Comparable<K>, V> {

    private static class Nodo<K, V> {
        K clave;
        V valor;
        Nodo<K, V> izquierdo, derecho;

        Nodo(K clave, V valor) {
            this.clave = clave;
            this.valor = valor;
        }
    }

    private Nodo<K, V> raiz;
    private int tamano;

    public void insertar(K clave, V valor) {
        raiz = insertarRec(raiz, clave, valor);
    }

    private Nodo<K, V> insertarRec(Nodo<K, V> nodo, K clave, V valor) {
        if (nodo == null) { tamano++; return new Nodo<>(clave, valor); }
        int cmp = clave.compareTo(nodo.clave);
        if      (cmp < 0) nodo.izquierdo = insertarRec(nodo.izquierdo, clave, valor);
        else if (cmp > 0) nodo.derecho   = insertarRec(nodo.derecho,   clave, valor);
        else              nodo.valor = valor;
        return nodo;
    }

    public V buscar(K clave) {
        Nodo<K, V> nodo = raiz;
        while (nodo != null) {
            int cmp = clave.compareTo(nodo.clave);
            if      (cmp < 0) nodo = nodo.izquierdo;
            else if (cmp > 0) nodo = nodo.derecho;
            else              return nodo.valor;
        }
        return null;
    }

    public void eliminar(K clave) {
        raiz = eliminarRec(raiz, clave);
    }

    private Nodo<K, V> eliminarRec(Nodo<K, V> nodo, K clave) {
        if (nodo == null) return null;
        int cmp = clave.compareTo(nodo.clave);
        if      (cmp < 0) nodo.izquierdo = eliminarRec(nodo.izquierdo, clave);
        else if (cmp > 0) nodo.derecho   = eliminarRec(nodo.derecho,   clave);
        else {
            tamano--;
            if (nodo.izquierdo == null) return nodo.derecho;
            if (nodo.derecho   == null) return nodo.izquierdo;
            // Reemplazar con el sucesor in-orden (minimo del subarbol derecho)
            Nodo<K, V> sucesor = minNodo(nodo.derecho);
            nodo.clave  = sucesor.clave;
            nodo.valor  = sucesor.valor;
            nodo.derecho = eliminarRec(nodo.derecho, sucesor.clave);
            tamano++;
        }
        return nodo;
    }

    private Nodo<K, V> minNodo(Nodo<K, V> nodo) {
        while (nodo.izquierdo != null) nodo = nodo.izquierdo;
        return nodo;
    }

    // Recorrido en orden ascendente por clave
    public void enOrden(Consumer<V> accion) {
        enOrdenRec(raiz, accion);
    }

    private void enOrdenRec(Nodo<K, V> nodo, Consumer<V> accion) {
        if (nodo == null) return;
        enOrdenRec(nodo.izquierdo, accion);
        accion.accept(nodo.valor);
        enOrdenRec(nodo.derecho, accion);
    }

    public boolean contiene(K clave) { return buscar(clave) != null; }
    public int tamano() { return tamano; }
    public boolean estaVacio() { return raiz == null; }
}

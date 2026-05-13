package datastructures;

import java.util.function.Consumer;

// Set basado en tabla hash con encadenamiento (ListaEnlazada por bucket).
// Uso en el parque: SetSimple<String> para guardar IDs de atracciones favoritas.
public class SetSimple<T> {

    private static final int CAPACIDAD = 16;

    @SuppressWarnings("unchecked")
    private final ListaEnlazada<T>[] buckets = new ListaEnlazada[CAPACIDAD];
    private int tamano = 0;

    public SetSimple() {
        for (int i = 0; i < CAPACIDAD; i++) buckets[i] = new ListaEnlazada<>();
    }

    public boolean agregar(T elemento) {
        int indice = indice(elemento);
        if (buckets[indice].contiene(elemento)) return false;
        buckets[indice].agregar(elemento);
        tamano++;
        return true;
    }

    public boolean contiene(T elemento) {
        return buckets[indice(elemento)].contiene(elemento);
    }

    public boolean eliminar(T elemento) {
        int indice = indice(elemento);
        boolean eliminado = buckets[indice].eliminar(elemento);
        if (eliminado) tamano--;
        return eliminado;
    }

    public void forEach(Consumer<T> accion) {
        for (ListaEnlazada<T> bucket : buckets)
            for (T item : bucket) accion.accept(item);
    }

    public int tamano() { return tamano; }
    public boolean estaVacio() { return tamano == 0; }

    private int indice(T elemento) {
        return Math.abs(elemento.hashCode() % CAPACIDAD);
    }
}

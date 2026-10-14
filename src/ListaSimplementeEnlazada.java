import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;

public class ListaSimplementeEnlazada<T> implements Coleccion<T> {
    
    private static class Nodo<T> {
        T valor;
        Nodo<T> siguiente;

        Nodo(T valor) {
            this.valor = valor;
            this.siguiente = null;
        }
    }

    private Nodo<T> cabeza;
    private int tamanio;

    public ListaSimplementeEnlazada() {
        this.cabeza = null;
        this.tamanio = 0;
    }

    @Override
    public void agregar(T elemento) {
        Nodo<T> nuevoNodo = new Nodo<>(elemento);
        if (cabeza == null) {
            cabeza = nuevoNodo;
        } else {
            Nodo<T> actual = cabeza;
            while (actual.siguiente != null) {
                actual = actual.siguiente;
            }
            actual.siguiente = nuevoNodo;
        }
        tamanio++;
    }

    @Override
    public boolean eliminar(T elemento) {
        if (estaVacia()) 
            return false;
        //si el elemento a eliminar es la cabeza
        if (Objects.equals(cabeza.valor, elemento)) {
            cabeza = cabeza.siguiente;
            tamanio--;
            return true;
        }
        //avanzar mientras el siguiente nodo no sea nulo y no sea el elemento a eliminar
        Nodo<T> actual = cabeza;
        while (actual.siguiente != null && !Objects.equals(actual.siguiente.valor, elemento)) {
            actual = actual.siguiente;
        }
        //si encontramos el elemento a eliminar
        if (actual.siguiente != null) {
            actual.siguiente = actual.siguiente.siguiente;
            tamanio--;
            return true;
        }
        return false;
    }

    @Override
    public T obtener(int indice) {
        if (indice < 0 || indice >= tamanio) {
            throw new IndexOutOfBoundsException("Índice fuera de rango");
        }
        Nodo<T> actual = cabeza;
        for (int i = 0; i < indice; i++) {
            actual = actual.siguiente;
        }
        return actual.valor;
    }

    @Override
    public int tamanio() {
        return tamanio;
    }

    @Override
    public boolean estaVacia() {
        return cabeza == null;
    }

    @Override 
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private Nodo<T> actual = cabeza;

            @Override
            public boolean hasNext() {
                return actual != null;
            }

            @Override
            public T next() {
                if (!hasNext()) throw new NoSuchElementException();
                T valor = actual.valor;
                actual = actual.siguiente;
                return valor;

            }
        };
    }
    
}

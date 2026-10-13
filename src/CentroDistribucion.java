import java.util.*;

public class CentroDistribucion {

    private final Map<String, Paquete> mapaPaquetes = new HashMap<>();
    private final List<Paquete> listaOrdenLlegada = new ArrayList<>();
    private final Queue<Paquete> colaLlegada = new ArrayDeque<>();
    private final PriorityQueue<Paquete> colaPrioridad = new PriorityQueue<>((Comparator) new ComparadorPorPrioridad());
    private final Set<String> municipiosSet = new HashSet<>();
    private final Set<String> municipiosOrdenadosSet = new TreeSet<>();
    private final Map<String, List<Paquete>> paquetesPorMunicipio = new HashMap<>();
    private final Map<String, Repartidor> repartidoresMap = new HashMap<>();
    private final ListaSimplementeEnlazada<Paquete> historialEntregas = new ListaSimplementeEnlazada<>();

    public void registrarRepartidor(Repartidor r) {
        if (repartidoresMap.containsKey(r.getIdenficicacion())) {
            throw new IllegalArgumentException("El repartidor con ID " + r.getIdenficicacion() + " ya está registrado.");
        }
        repartidoresMap.put(r.getIdenficicacion(), r);
    }

    public void registrarPaquete(Paquete p) {
        if (mapaPaquetes.containsKey(p.getCodigo())) {
            throw new IllegalArgumentException("El paquete ya existe: " + p.getCodigo());
        }
        mapaPaquetes.put(p.getCodigo(), p);
        listaOrdenLlegada.add(p);
        colaLlegada.add(p);
        colaPrioridad.add(p);

        municipiosSet.add(p.getDestino());
        municipiosOrdenadosSet.add(p.getDestino());

        paquetesPorMunicipio.computeIfAbsent(p.getDestino(), k -> new ArrayList<>()).add(p);
    }

    public Paquete buscarPorCodigo(String codigo) {
        return mapaPaquetes.get(codigo);
    }

    public void registrarEntrega(Paquete p) {
        historialEntregas.agregar(p);
    }

    // --- RECURSIVIDAD ---
    public double pesoTotalMunicipioRecursivo(List<Paquete> paquetes, String municipio, int indice) {
        if (indice >= paquetes.size()) return 0.0;
        Paquete p = paquetes.get(indice);
        double pesoActual = p.getDestino().equalsIgnoreCase(municipio) ? p.getPeso() : 0.0;
        return pesoActual + pesoTotalMunicipioRecursivo(paquetes, municipio, indice + 1);
    }

    public int contarPorPrioridadMinimaRecursivo(List<Paquete> paquetes, int prioridadMin, int indice) {
        if (indice >= paquetes.size()) return 0;
        int cuentaActual = paquetes.get(indice).getPrioridad() >= prioridadMin ? 1 : 0;
        return cuentaActual + contarPorPrioridadMinimaRecursivo(paquetes, prioridadMin, indice + 1);
    }

    // --- DIVIDE Y VENCERÁS ---
    public Paquete paqueteMayorPesoDyV(List<Paquete> paquetes, int inicio, int fin) {
        if (inicio == fin) return paquetes.get(inicio);

        int medio = inicio + (fin - inicio) / 2;
        Paquete izq = paqueteMayorPesoDyV(paquetes, inicio, medio);
        Paquete der = paqueteMayorPesoDyV(paquetes, medio + 1, fin);

        return (izq.getPeso() >= der.getPeso()) ? izq : der;
    }

    // --- BÚSQUEDA BINARIA ---
    public Paquete busquedaBinariaPorCodigo(List<Paquete> paquetesOrdenados, String codigoBuscado) {
        int inicio = 0;
        int fin = paquetesOrdenados.size() - 1;

        while (inicio <= fin) {
            int medio = inicio + (fin - inicio) / 2;
            int comparacion = paquetesOrdenados.get(medio).getCodigo().compareTo(codigoBuscado);

            if (comparacion == 0) return paquetesOrdenados.get(medio);
            if (comparacion < 0) inicio = medio + 1;
            else fin = medio - 1;
        }
        return null;
    }
}

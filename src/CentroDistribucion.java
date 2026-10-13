import java.util.*;

public static class CentroDistribucion {

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
public static void main(String[] args) {
    CentroDistribucion centro= new CentroDistribucion();
    // 1. Registro de Repartidores (Mínimo 6)
    centro.registrarRepartidor(new Repartidor("R01", "Carlos Pérez", "Armenia", true));
    centro.registrarRepartidor(new Repartidor("R02", "Ana Gómez", "Salento", true));
    centro.registrarRepartidor(new Repartidor("R03", "Luis Díaz", "Calarcá", true));
    centro.registrarRepartidor(new Repartidor("R04", "Marta Ríos", "Circasia", false));
    centro.registrarRepartidor(new Repartidor("R05", "Jorge Torres", "Montenegro", true));
    centro.registrarRepartidor(new Repartidor("R06", "Sofia López", "Armenia", true));

    // 2. Registro de Paquetes (Mínimo 30, no consecutivos, 5 destinos)
    String[] municipios = {"Armenia", "Salento", "Calarcá", "Circasia", "Montenegro"};
    Random rng = new Random(42);

    for (int i = 1; i <= 30; i++) {
        String cod = "PQ" + (100 + rng.nextInt(800));
        while (centro.buscarPorCodigo(cod) != null) {
            cod = "PQ" + (100 + rng.nextInt(800));
        }
        String dest = municipios[i % municipios.length];
        double peso = Math.round((1.0 + rng.nextDouble() * 15.0) * 10.0) / 10.0;
        int prio = 1 + rng.nextInt(5);
        int tiempo = 15 + rng.nextInt(60);

        centro.registrarPaquete(new Paquete(cod, dest, peso, prio, tiempo));
    }

    System.out.println("=== QUINDÍOEXPRESS - PRUEBAS DE OPERACIÓN ===");
    System.out.println("Total paquetes registrados: " + centro.listaOrdenLlegada.size());

    // Test Recursividad
    double pesoArmenia = centro.pesoTotalMunicipioRecursivo(centro.listaOrdenLlegada, "Armenia", 0);
    System.out.printf("\n[RECURSIVIDAD] Peso total para Armenia: %.1f kg\n", pesoArmenia);

    int deAltaPrio = centro.contarPorPrioridadMinimaRecursivo(centro.listaOrdenLlegada, 4, 0);
    System.out.println("[RECURSIVIDAD] Paquetes con prioridad >= 4: " + deAltaPrio);
}

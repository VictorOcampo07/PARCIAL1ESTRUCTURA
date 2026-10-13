public class CentroDistribucion {

    private final Map<String, Paquete> mapaPaquetes = new HashMap<>();
    private final List<Paquete> listaOrdenLlegada = new ArrayList<>();
    private final Queue<Paquete> colaLlegada = new ArrayDeque<>();
    private final PriorityQueue<Paquete> colaPrioridad = new PriorityQueue<>(new ComparadorPorPrioridad());
    private final Set<String> municipiosSet = new HashSet<>();
    private final Set<String> municipiosOrdenadosSet = new TreeSet<>(); 
    private final Map<String, List<Paquete>> paquetesPorMunicipio = new HashMap<>();
    private final Map<String, repartidor> repartidoresMap = new HashMap<>();
    private final ListaSimplementeEnlazada<Paquete> historialEntregas = new ListaSimplementeEnlazada<>();

    public void registrarRepartidor(Repartidor r){
        if(repartidoresMap.containsKey(r.getIdenficicacion())){
            throw new IllegalArgumentException("El repartidor con ID " + r.getIdenficicacion() + " ya está registrado.");
        }
        repartidoresMap.put(r.getIdenficicacion(), r);
    }




}

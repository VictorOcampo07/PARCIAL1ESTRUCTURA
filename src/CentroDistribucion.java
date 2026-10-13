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
        if(p != null)
            historialEntregas.agregar(p);
            mapaPaquetes.remove(p.getCodigo());
            listaOrdenLlegada.remove(p);
            colaLlegada.remove(p);
            colaPrioridad.remove(p);

            List<Paquete> listaMun = paquetesPorMunicipio.get(p.getDestino());
            if(listaMun != null) listaMun.remove(p);
         
    }
    public Paquete despacharSiguientePrioritario() {
        Paquete siguiente = colaPrioridad.poll();
        if (siguiente != null) {
            mapaPaquetes.remove(siguiente.getCodigo());
            listaOrdenLlegada.remove(siguiente);
            colaLlegada.remove(siguiente);
            historialEntregas.agregar(siguiente);
            List<Paquete> listaMun = paquetesPorMunicipio.get(siguiente.getDestino());
            if (listaMun != null) listaMun.remove(siguiente);
        }
        return siguiente;
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
    Random rng = new Random(45);

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

    // --- DEMOSTRACIÓN DE REQUERIMIENTOS BÁSICOS DEL PARCIAL ---
    System.out.println("=== QUINDÍOEXPRESS - PRUEBAS DE OPERACIÓN ===");
    System.out.println("Total paquetes registrados: " + centro.listaOrdenLlegada.size());

    // Test Recursividad
    double pesoArmenia = centro.pesoTotalMunicipioRecursivo(centro.listaOrdenLlegada, "Armenia", 0);
    System.out.printf("\n[RECURSIVIDAD] Peso total para Armenia: %.1f kg\n", pesoArmenia);

    int deAltaPrio = centro.contarPorPrioridadMinimaRecursivo(centro.listaOrdenLlegada, 4, 0);
    System.out.println("[RECURSIVIDAD] Paquetes con prioridad >= 4: " + deAltaPrio);

    //Test Divide y Vencerás
    Paquete masPesado = centro.paqueteMayorPesoDyV(centro.listaOrdenLlegada, 0, centro.listaOrdenLlegada.size() - 1);
    System.out.println("\n[DIVIDE Y VENCERÁS] Paquete más pesado: " + masPesado);

    // Test Búsqueda Binaria
    List<Paquete> ordenados = new ArrayList<>(centro.listaOrdenLlegada);
    Collections.sort(ordenados);
    String codBuscar = ordenados.get(5).getCodigo();
    Paquete hallado = centro.busquedaBinariaPorCodigo(ordenados, codBuscar);
    System.out.println("\n[BÚSQUEDA BINARIA] Buscando " + codBuscar + " -> Hallado: " + hallado);

    // Test Historial con Lista Propia e Iterator
    System.out.println("\n[HISTORIAL DE ENTREGAS] Procesando primeras 10 entregas:");
    for (int i = 0; i < 10; i++) {
            Paquete p = centro.colaPrioridad.poll();
            centro.registrarEntrega(p);
        }

        System.out.println("Recorriendo historial con Iterable/Iterator:");
        for (Paquete entregado : centro.historialEntregas) {
            System.out.println(" -> Entregado: " + entregado);
        }

    // --- MENÚ INTERACTIVO COMPLEMENTARIO ---
        Scanner scanner = new Scanner(System.in);
        int opcion = -1;

        while (opcion != 0) {
            System.out.println("\n=================================================");
            System.out.println("          SISTEMA DE GESTIÓN INTERACTIVO        ");
            System.out.println("=================================================");
            System.out.println("1. Registrar un paquete manualmente");
            System.out.println("2. Ver lista de paquetes pendientes (Orden de llegada)");
            System.out.println("3. Ver historial de paquetes entregados (Lista Propia)");
            System.out.println("4. Ver municipios registrados (Ordenados alfabéticamente)");
            System.out.println("5. Ver paquetes por municipio");
            System.out.println("6. Despachar siguiente paquete por prioridad");
            System.out.println("7. Buscar paquete por código");
            System.out.println("8. Volver a ejecutar pruebas de análisis (Recursividad / DyV)");
            System.out.println("0. Salir");
            System.out.print("Seleccione una opción: ");

            try {
                opcion = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Opción no válida. Por favor ingrese un número.");
                continue;
            }

            switch (opcion) {
                case 1:
                    System.out.println("\n--- AGREGAR PAQUETE MANUALMENTE ---");
                    try {
                        System.out.print("Código único (Ej: PQ555): ");
                        String cod = scanner.nextLine();
                        System.out.print("Municipio destino (Armenia, Montenegro, Salento, Circacia, Calarcá): ");
                        String dest = scanner.nextLine();
                        System.out.print("Peso en Kg (> 0): ");
                        double peso = Double.parseDouble(scanner.nextLine());
                        System.out.print("Prioridad (1 a 5): ");
                        int prio = Integer.parseInt(scanner.nextLine());
                        System.out.print("Tiempo estimado en min (> 0): ");
                        int tiempo = Integer.parseInt(scanner.nextLine());

                        Paquete nuevo = new Paquete(cod, dest, peso, prio, tiempo);
                        centro.registrarPaquete(nuevo);
                        System.out.println("Paquete registrado correctamente: " + nuevo);
                    } catch (Exception e) {
                        System.out.println("Error al registrar el paquete: " + e.getMessage());
                    }
                    break;

                case 2:
                    System.out.println("\n--- PAQUETES PENDIENTES (ORDEN DE REGISTRO / LLEGADA) ---");
                    if (centro.listaOrdenLlegada.isEmpty()) {
                        System.out.println("No hay paquetes pendientes.");
                    } else {
                        System.out.println("Total pendientes: " + centro.listaOrdenLlegada.size());
                        for (int i = 0; i < centro.listaOrdenLlegada.size(); i++) {
                            System.out.printf("%2d. %s\n", (i + 1), centro.listaOrdenLlegada.get(i));
                        }
                    }
                    break;

                case 3:
                    System.out.println("\n--- HISTORIAL DE ENTREGAS (LISTA SIMPLEMENTE ENLAZADA PROPIA) ---");
                    if (centro.historialEntregas.estaVacia()) {
                        System.out.println("El historial de entregas está vacío.");
                    } else {
                        System.out.println("Total entregados: " + centro.historialEntregas.tamanio());
                        int cont = 1;
                        for (Paquete p : centro.historialEntregas) {
                            System.out.printf("%2d. %s\n", cont++, p);
                        }
                    }
                    break;

                case 4:
                    System.out.println("\n--- MUNICIPIOS REGISTRADOS (TREE SET - ORDEN ALFABÉTICO) ---");
                    for (String mun : centro.municipiosOrdenadosSet) {
                        System.out.println("-" + mun);
                    }
                    break;

                case 5:
                    System.out.println("\n--- PAQUETES AGRUPADOS POR MUNICIPIO ---");
                    centro.paquetesPorMunicipio.forEach((mun, lista) -> {
                        System.out.println(mun + " (" + lista.size() + " pendientes):");
                        for (Paquete p : lista) {
                            System.out.println("   -> " + p);
                        }
                    });
                    break;

                case 6:
                    System.out.println("\n--- DESPACHAR SIGUIENTE PAQUETE POR PRIORIDAD ---");
                    Paquete despachado = centro.despacharSiguientePrioritario();
                    if (despachado == null) {
                        System.out.println("No hay paquetes pendientes por despachar.");
                    } else {
                        System.out.println("Paquete despachado y enviado al historial:");
                        System.out.println("   " + despachado);
                    }
                    break;

                case 7:
                    System.out.print("\nIngrese el código a buscar: ");
                    String codB = scanner.nextLine();
                    Paquete encontrado = centro.buscarPorCodigo(codB);
                    if (encontrado != null) {
                        System.out.println("Paquete encontrado:");
                        System.out.println("   " + encontrado);
                    } else {
                        System.out.println("Paquete no encontrado o ya fue entregado.");
                    }
                    break;

                case 8:
                    System.out.println("\n--- RE-EJECUCIÓN DE ANÁLISIS ---");
                    System.out.print("Ingrese municipio para peso total recursivo: ");
                    String munTarget = scanner.nextLine();
                    double pTotal = centro.pesoTotalMunicipioRecursivo(centro.listaOrdenLlegada, munTarget, 0);
                    System.out.printf("Peso total hacia %s: %.1f kg\n", munTarget, pTotal);

                    if (!centro.listaOrdenLlegada.isEmpty()) {
                        Paquete maxP = centro.paqueteMayorPesoDyV(centro.listaOrdenLlegada, 0, centro.listaOrdenLlegada.size() - 1);
                        System.out.println("Paquete más pesado (DyV): " + maxP);
                    }
                    break;

                case 0:
                    System.out.println("\n¡Programa finalizado!");
                    break;

                default:
                    System.out.println("⚠️ Opción no reconocida.");
            }
        }
        scanner.close();
    }
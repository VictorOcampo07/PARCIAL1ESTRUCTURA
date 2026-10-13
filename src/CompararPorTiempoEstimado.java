public class CompararPorTiempoEstimado implements Comparador<Paquete> {

    @Override
    public int compare(Paquete p1, Paquete p2) {
        return Integer.compare(p1.getTiempoEstimado(), p2.getTiempoEstimado());
    }
    
}

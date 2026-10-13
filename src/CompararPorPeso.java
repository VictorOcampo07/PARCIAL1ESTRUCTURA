public class CompararPorPeso implements Comparator<Paquete> {

    @Override
    public int compare(Paquete p1, Paquete p2) {
        return Double.compare(p1.getPeso(), p2.getPeso());
    }
    
}

import java.util.Comparator;

public class ComparadorPorPrioridad implements Comparator<Paquete> {

    @Override 
    public int compare(Paquete p1, Paquete p2) {
        int cmpPrioridad = Integer.compare(p2.getPrioridad(), p1.getPrioridad());
        if(cmpPrioridad != 0)
            return cmpPrioridad;
        
        int cmpTiempo = Integer.compare(p1.getTiempoEstimado(), p2.getTiempoEstimado());
        if(cmpTiempo != 0)
            return cmpTiempo;
        
        return p1.getCodigo().compareTo(p2.getCodigo());
    }
    
}

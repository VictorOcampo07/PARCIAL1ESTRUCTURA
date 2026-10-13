public class Repartidor {
    
    private final String idenficicacion;
    private final String nombre;
    private final String zona;
    private boolean disponible;


    public Repartidor(String idenficicacion, String nombre, String zona, boolean disponible) {
        this.idenficicacion = idenficicacion;
        this.nombre = nombre;
        this.zona = zona;
        this.disponible = disponible;
    }

    public String getIdenficicacion() {
        return idenficicacion;
    }
    public String getNombre() {
        return nombre;
    }
    public String getZona(){
        return zona;
    }
    public boolean isDisponible() {
        return disponible;
    }
    public void setDisponible(boolean disponible) {
        this.disponible = disponible;
    }
}

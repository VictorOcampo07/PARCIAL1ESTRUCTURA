import java.util.Objects;

public class Paquete implements Comparable<Paquete>{
    private final String codigo;
    private final String destino;
    private final double peso;
    private final int prioridad;
    private final int tiempoEstimado;

    public Paquete(String codigo, String destino, double peso, int prioridad, int tiempoEstimado) {
        if (codigo == null || codigo.trim().isEmpty()) throw new IllegalArgumentException("Código inválido, no puede ser nulo o vacío");
        if (destino == null || destino.trim().isEmpty()) throw new IllegalArgumentException("Destino inválido, no puede ser nulo o vacío");
        if (peso <= 0) throw new IllegalArgumentException("Peso debe ser > 0");
        if (prioridad < 1 || prioridad > 5) throw new IllegalArgumentException("Prioridad debe estar entre 1 y 5");
        if (tiempoEstimado <= 0) throw new IllegalArgumentException("Tiempo estimado debe ser > 0");

        this.codigo = codigo.toUpperCase().trim();
        this.destino = destino.trim();
        this.peso = peso;
        this.prioridad = prioridad;
        this.tiempoEstimado = tiempoEstimado;
    }

    public String getCodigo() { return codigo; }
    public String getDestino() { return destino; }
    public double getPeso() { return peso; }
    public int getPrioridad() { return prioridad; }
    public int getTiempoEstimado() { return tiempoEstimado; }

    @Override
    public int compareTo(Paquete otro) {
        return this.codigo.compareTo(otro.codigo);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Paquete)) return false;
        Paquete paquete = (Paquete) o;
        return Objects.equals(codigo, paquete.codigo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(codigo);
    }

    @Override
    public String toString() {
       return String.format("[%s | Destino: %-10s | Peso: %4.1fkg | Prio: %d | Tiempo: %2dmin]", 
                codigo, destino, peso, prioridad, tiempoEstimado);
    }
}


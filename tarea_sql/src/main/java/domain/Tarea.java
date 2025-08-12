package domain;

import java.time.LocalDateTime;
import java.util.Objects;

public class Tarea {
    private Long id;
    private String titulo;
    private int prioridad; // 1=Alta,2=Media,3=Baja
    private boolean especial;
    private LocalDateTime fecha; // opcional
    private boolean hecho;
    // auditoría simple
    private LocalDateTime creado;
    private LocalDateTime actualizado;

    public Tarea() {}

    public Tarea(Long id, String titulo, int prioridad, boolean especial, LocalDateTime fecha) {
        this.id = id;
        this.titulo = titulo;
        this.prioridad = prioridad;
        this.especial = especial;
        this.fecha = fecha;
        this.hecho = false;
        this.creado = LocalDateTime.now();
        this.actualizado = LocalDateTime.now();
    }

    // getters y setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public int getPrioridad() { return prioridad; }
    public void setPrioridad(int prioridad) { this.prioridad = prioridad; }

    public boolean isEspecial() { return especial; }
    public void setEspecial(boolean especial) { this.especial = especial; }

    public java.time.LocalDateTime getFecha() { return fecha; }
    public void setFecha(java.time.LocalDateTime fecha) { this.fecha = fecha; }

    public boolean isHecho() { return hecho; }
    public void setHecho(boolean hecho) { this.hecho = hecho; }

    public java.time.LocalDateTime getCreado() { return creado; }
    public void setCreado(java.time.LocalDateTime creado) { this.creado = creado; }

    public java.time.LocalDateTime getActualizado() { return actualizado; }
    public void setActualizado(java.time.LocalDateTime actualizado) { this.actualizado = actualizado; }

    @Override
    public String toString() {
        return String.format("Tarea[id=%d,titulo=%s,prio=%d,hecho=%s]", id, titulo, prioridad, hecho);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Tarea)) return false;
        Tarea tarea = (Tarea) o;
        return Objects.equals(id, tarea.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

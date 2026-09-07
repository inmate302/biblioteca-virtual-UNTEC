package cl.untec.biblioteca_virtual.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Prestamo implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private static final DateTimeFormatter FORMATOFECHA = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    private long id;
    private long usuarioId;
    private long libroId;
    private String tituloLibro;
    private String isbnLibro;
    private LocalDate fechaPrestamo;
    private LocalDate fechaDevolucion;
    private boolean devuelto;

    public Prestamo(){
    }

    public Prestamo(
        long id, 
        long usuarioId, 
        long libroId, 
        String tituloLibro, 
        String isbnLibro,
        LocalDate fechaPrestamo,
        LocalDate fechaDevolucion,
        boolean devuelto) {
    
    this.id = id;
    this.usuarioId = usuarioId;
    this.libroId = libroId;
    this.tituloLibro = tituloLibro;
    this.isbnLibro = isbnLibro;
    this.fechaPrestamo = fechaPrestamo;
    this.fechaDevolucion = fechaDevolucion;
    this.devuelto = devuelto;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public long getLibroId() {
        return libroId;
    }

    public void setLibroId(long libroId) {
        this.libroId = libroId;
    }

    public String getTituloLibro() {
        return tituloLibro;
    }

    public void setTituloLibro(String tituloLibro) {
        this.tituloLibro = tituloLibro;
    }

    public String getIsbnLibro() {
        return isbnLibro;
    }

    public void setIsbnLibro(String isbnLibro) {
        this.isbnLibro = isbnLibro;
    }

    public LocalDate getFechaPrestamo() {
        return fechaPrestamo;
    }

    public void setFechaPrestamo(LocalDate fechaPrestamo) {
        this.fechaPrestamo = fechaPrestamo;
    }

    public LocalDate getFechaDevolucion() {
        return fechaDevolucion;
    }

    public void setFechaDevolucion(LocalDate fechaDevolucion) {
        this.fechaDevolucion = fechaDevolucion;
    }

    public boolean isDevuelto() {
        return devuelto;
    }

    public String fechaPrestamoaTexto(){
        return fechaPrestamo == null ? "" : fechaPrestamo.format(FORMATOFECHA);
    }

    public String fechaDevolucionaTexto(){
        return fechaDevolucion == null ? "Pendiente" : fechaDevolucion.format(FORMATOFECHA);
    }


}

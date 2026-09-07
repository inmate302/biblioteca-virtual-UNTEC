package cl.untec.biblioteca_virtual.model;

import java.io.Serializable;

public class Libro implements Serializable {
    
    private static final long serialVersionUID = 1L;

    private long id;
    private String isbn;
    private String titulo;
    private String autor;
    private int stock;

    public Libro() {
    }

    public Libro (long id, String isbn, String titulo, String autor, int stock) {
        this.id = id;
        this.isbn = isbn;
        this.titulo = titulo;
        this.autor = autor;
        this.stock = stock;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public boolean isDisponible() {
        return stock > 0;
    }







}

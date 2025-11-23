package org.example;

public class Usuario {
    private int id;
    private String nombre;
    private String rol;

    // Constructor
    public Usuario(int id, String nombre, String rol) {
        this.id = id;
        this.nombre = nombre;
        this.rol = rol;
    }

    public int getId() { return id; }

    // No necesitamos getters para este ejemplo de Reflexión,
    // porque leeremos los campos privados directamente.
}
package org.example;

import java.util.List;
import java.util.Map;

public class UserController {

    // 1. Simulamos una Base de Datos en memoria
    private static final List<Usuario> USERS_DB = List.of(
            new Usuario(1, "Joel Rodriguez", "Backend Dev"),
            new Usuario(2, "Aristoteles", "Filosofo"),
            new Usuario(3, "Rene Descartes", "Filosofo")
    );

    // 2. Método nuevo: Recibe parámetros (QueryParams)
    public static String buscarUsuario(Map<String, String> params) {
        // Obtenemos el "id" de la URL (ej: ?id=2)
        String idStr = params.get("id");

        if (idStr == null) {
            return JsonUtil.toJson(USERS_DB); // Si no hay ID, devolvemos todos
        }

        try {
            int id = Integer.parseInt(idStr);
            // Buscamos en la lista usando Streams (Java funcional)
            return USERS_DB.stream()
                    .filter(u -> u.getId() == id) // Necesitarás agregar getters en Usuario.java si no los tienes
                    .findFirst()
                    .map(JsonUtil::toJson)
                    .orElse("{\"error\": \"Usuario no encontrado\"}");
        } catch (NumberFormatException e) {
            return "{\"error\": \"El ID debe ser un numero\"}";
        }
    }

    // Nota: Este método recibe params aunque no los use, para cumplir con la firma de la interfaz Function
    public static String obtenerStatus(Map<String, String> params) {
        return "{\"status\": \"Online\", \"version\": \"1.0.0\"}";
    }
}
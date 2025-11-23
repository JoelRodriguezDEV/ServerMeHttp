package org.example;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class JsonUtil {

    public static String toJson(Object objeto) {
        if (objeto == null) {
            return "null";
        }

        // CASO 1: Si es una LISTA (o cualquier colección iterable)
        // En lugar de usar reflexión, la recorremos y formateamos como Array JSON [...]
        if (objeto instanceof Iterable<?>) {
            List<String> jsonItems = new ArrayList<>();
            for (Object item : (Iterable<?>) objeto) {
                // Llamada recursiva: convertimos cada item de la lista
                jsonItems.add(toJson(item));
            }
            return "[" + String.join(", ", jsonItems) + "]";
        }

        // CASO 2: Si es un OBJETO normal (Usuario, etc.)
        // Aquí sí usamos Reflexión para leer sus campos
        Class<?> clase = objeto.getClass();
        Field[] campos = clase.getDeclaredFields();

        List<String> jsonPairs = new ArrayList<>();

        for (Field campo : campos) {
            campo.setAccessible(true);
            try {
                String nombrePropiedad = campo.getName();
                Object valor = campo.get(objeto);

                // Formateamos el valor
                String valorJson;
                if (valor instanceof String) {
                    valorJson = "\"" + valor + "\"";
                } else {
                    valorJson = String.valueOf(valor);
                }

                jsonPairs.add("\"" + nombrePropiedad + "\": " + valorJson);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        return "{" + String.join(", ", jsonPairs) + "}";
    }
}
package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function; // Usamos Function en lugar de Supplier

public class HttpServer {

    // CAMBIO 1: El mapa ahora guarda funciones que RECIBEN parámetros (Map -> String)
    private static final Map<String, Function<Map<String, String>, String>> rutas = new HashMap<>();

    static {
        rutas.put("/api/usuario", UserController::buscarUsuario);
        rutas.put("/api/status", UserController::obtenerStatus);
        // Lambda que ignora los params ("_")
        rutas.put("/", _ -> "{\"mensaje\": \"Servidor Dinamico Listo\"}");
    }

    public static void main(String[] args) throws IOException {
        int port = 8080;
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Servidor Final en puerto " + port);
            while (true) {
                Socket clientSocket = serverSocket.accept();
                Thread.ofVirtual().start(() -> {
                    try { handleClient(clientSocket); }
                    catch (IOException e) { e.printStackTrace(); }
                });
            }
        }
    }

    private static void handleClient(Socket clientSocket) throws IOException {
        try (clientSocket;
             BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
             OutputStream out = clientSocket.getOutputStream()) {

            String requestLine = in.readLine();
            if (requestLine == null) return;
            System.out.println("Petición: " + requestLine);

            String[] parts = requestLine.split(" ");
            String method = parts[0];
            String fullPath = parts[1]; // Esto puede ser "/api/usuario?id=1"

            // CAMBIO 2: Separar la ruta de los parámetros
            String routePath;
            Map<String, String> queryParams = new HashMap<>();

            if (fullPath.contains("?")) {
                String[] pathAndQuery = fullPath.split("\\?", 2);
                routePath = pathAndQuery[0]; // "/api/usuario"
                String queryString = pathAndQuery[1]; // "id=1&otro=algo"
                parseQueryParams(queryString, queryParams);
            } else {
                routePath = fullPath;
            }

            // CAMBIO 3: Ejecutar pasando los parámetros
            String jsonResponse;
            int statusCode = 200;

            if (method.equals("GET") && rutas.containsKey(routePath)) {
                // .apply() es el método para ejecutar una Function
                jsonResponse = rutas.get(routePath).apply(queryParams);
            } else {
                statusCode = 404;
                jsonResponse = "{\"error\": \"Ruta no encontrada\"}";
            }

            sendResponse(out, statusCode, jsonResponse);
        }
    }

    // Método auxiliar para convertir "id=1&lang=es" en un Mapa
    private static void parseQueryParams(String queryString, Map<String, String> map) {
        String[] pairs = queryString.split("&");
        for (String pair : pairs) {
            String[] keyValue = pair.split("=", 2);
            if (keyValue.length == 2) {
                map.put(keyValue[0], keyValue[1]);
            }
        }
    }

    private static void sendResponse(OutputStream out, int statusCode, String jsonBody) throws IOException {
        String statusText = (statusCode == 200) ? "OK" : "Not Found";
        String httpResponse = """
                HTTP/1.1 %d %s
                Content-Type: application/json
                Content-Length: %d
                
                %s
                """.formatted(statusCode, statusText, jsonBody.length(), jsonBody);
        out.write(httpResponse.getBytes());
        out.flush();
    }
}
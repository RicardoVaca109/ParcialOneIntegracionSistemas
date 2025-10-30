package com.ecologistics.eco_logistics.routes;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

import com.ecologistics.eco_logistics.models.Envio;
import com.ecologistics.eco_logistics.services.EnvioService;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class EnvioRoute extends RouteBuilder {

    private final EnvioService envioService;

    public EnvioRoute(EnvioService envioService) {
        this.envioService = envioService;
    }

    @Override
    public void configure() throws Exception {

        from("file:input?fileName=envio.csv&noop=true")
            .routeId("envioRoute")
            .log("[INFO] Archivo cargado: ${file:name}")
            .split(body().tokenize("\n")).streaming()
            .process(exchange -> {
                String line = exchange.getIn().getBody(String.class);
                if (line == null) {
                    exchange.getIn().setBody(null);
                    return;
                }
                line = line.trim();
                if (line.isEmpty() || line.toLowerCase().startsWith("id") || line.toLowerCase().contains("id_envio")) {
                    exchange.getIn().setBody(null);
                    return;
                }
                String[] data = line.split(",");
                if (data.length >= 4) {
                    Envio envio = new Envio(data[0].trim(), data[1].trim(), data[2].trim(), data[3].trim());
                    exchange.getIn().setBody(envio);
                } else {
                    exchange.getIn().setBody(null);
                }
            })
            .filter(body().isNotNull())
            .aggregate(constant(true), (oldEx, newEx) -> {
                // Obtener el objeto entrante de forma segura
                Object incomingObj = newEx.getIn().getBody();
                Envio incoming = incomingObj instanceof Envio ? (Envio) incomingObj : null;

                // Recuperar lista previa de forma segura
                List<Envio> list;
                if (oldEx == null) {
                    list = new ArrayList<>();
                } else {
                    Object oldBody = oldEx.getIn().getBody();
                    if (oldBody instanceof List) {
                        //noinspection unchecked
                        list = (List<Envio>) oldBody;
                        if (list == null) list = new ArrayList<>();
                    } else if (oldBody instanceof Envio) {
                        list = new ArrayList<>();
                        list.add((Envio) oldBody);
                    } else {
                        // Si el body anterior no es lista ni Envio (ej. File), empezamos una nueva lista
                        list = new ArrayList<>();
                    }
                }

                // Solo añadimos si incoming es Envio
                if (incoming != null) {
                    list.add(incoming);
                }

                // Colocamos la lista como body del exchange devuelto
                newEx.getIn().setBody(list);
                return newEx;
            }).completionTimeout(1000)
            .process(exchange -> {
                List<Envio> lista = exchange.getIn().getBody(List.class);
                envioService.guardarEnvios(lista);

                // --- Guardar JSON en carpeta "output" ---
                try {
                    if (lista == null) lista = new ArrayList<>();
                    ObjectMapper mapper = new ObjectMapper();
                    String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(lista);

                    Path outDir = Path.of("output");
                    Files.createDirectories(outDir);

                    String originalName = exchange.getIn().getHeader("CamelFileName", String.class);
                    String base = (originalName != null && originalName.endsWith(".csv"))
                            ? originalName.substring(0, originalName.length() - 4)
                            : (originalName != null ? originalName : "envio");
                    String outFileName = base + "_" + System.currentTimeMillis() + ".json";
                    Path outFile = outDir.resolve(outFileName);

                    Files.writeString(outFile, json);
                    System.out.println("[INFO] JSON generado en: " + outFile.toString());
                } catch (Exception ex) {
                    System.out.println("[ERROR] No se pudo escribir JSON en carpeta output: " + ex.getMessage());
                }

                System.out.println("[INFO] Archivo procesado. Registros guardados: " + (lista == null ? 0 : lista.size()));
            });
    }
}
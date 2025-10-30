package com.ecologistics.eco_logistics.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

import com.ecologistics.eco_logistics.models.Envio;

@Service
public class EnvioService {

    private final Map<String, Envio> store = new ConcurrentHashMap<>();

    // Guardar lista de envíos de forma segura (ignora nulls y envíos sin id)
    public void guardarEnvios(List<Envio> envios) {
        if (envios == null || envios.isEmpty()) {
            return;
        }
        for (Envio e : envios) {
            if (e == null) {
                System.out.println("[WARN] Envío nulo ignorado.");
                continue;
            }
            String id = e.getId_envio();
            if (id == null || id.isBlank()) {
                System.out.println("[WARN] Envío sin id ignorado: " + e);
                continue;
            }
            store.put(id, e);
        }
    }

    public List<Envio> obtenerTodos() {
        return new ArrayList<>(store.values());
    }

    public Envio obtenerPorId(String id) {
        if (id == null) return null;
        return store.get(id);
    }

    public void agregarEnvio(Envio envio) {
        if (envio == null) return;
        String id = envio.getId_envio();
        if (id == null || id.isBlank()) return;
        store.put(id, envio);
    }
}
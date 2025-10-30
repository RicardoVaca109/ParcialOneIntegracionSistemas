package com.ecologistics.eco_logistics.controllers;

import com.ecologistics.eco_logistics.models.Envio;
import com.ecologistics.eco_logistics.services.EnvioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/envios")
public class EnvioController {

    private final EnvioService envioService;

    public EnvioController(EnvioService envioService) {
        this.envioService = envioService;
    }

    @GetMapping
    public List<Envio> listar() {
        return envioService.obtenerTodos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Envio> obtener(@PathVariable String id) {
        Envio e = envioService.obtenerPorId(id);
        return e != null ? ResponseEntity.ok(e) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<Void> crear(@RequestBody Envio envio) {
        envioService.agregarEnvio(envio);
        return ResponseEntity.status(201).build();
    }
}
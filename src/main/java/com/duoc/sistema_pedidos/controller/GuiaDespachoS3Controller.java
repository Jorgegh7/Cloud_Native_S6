package com.duoc.sistema_pedidos.controller;

import com.duoc.sistema_pedidos.model.GuiaDespacho;
import com.duoc.sistema_pedidos.service.contrato.GuiaDespachoS3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/guias")
@RequiredArgsConstructor
public class GuiaDespachoS3Controller {

    private final GuiaDespachoS3Service guiaDespachoS3Service;

    @GetMapping("/{id}/generar")
    public ResponseEntity<?> generarGuia(@PathVariable Long id) {
        try {
            byte[] guia = guiaDespachoS3Service.generarGuia(id);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=guia_" + id + ".txt")
                    .contentType(MediaType.TEXT_PLAIN)
                    .body(guia);
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    @PostMapping("/{id}/subir")
    public ResponseEntity<?> subirGuia(@PathVariable Long id) {
        try {
            guiaDespachoS3Service.subirGuia(id);
            return ResponseEntity.ok("Guía #" + id + " subida a S3");
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

        @GetMapping("/{id}/descargar")
    public ResponseEntity<?> descargarGuia(@PathVariable Long id) {
        try {
            byte[] guia = guiaDespachoS3Service.descargarGuia(id);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=guia_" + id + ".txt")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(guia);
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    @PutMapping("/{id}/modificar")
    public ResponseEntity<?> modificarGuia(@PathVariable Long id, @RequestBody GuiaDespacho guiaDespacho) {
        try {
            guiaDespachoS3Service.modificarGuia(id, guiaDespacho);
            return ResponseEntity.ok("Guía #" + id + " actualizada y regenerada en S3");
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}/borrar")
    public ResponseEntity<?> borrarGuia(@PathVariable Long id) {
        try {
            guiaDespachoS3Service.borrarGuia(id);
            return ResponseEntity.ok("Guía #" + id + " borrada de S3");
        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }
}
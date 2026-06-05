package com.duoc.sistema_pedidos.service.impl;

import com.duoc.sistema_pedidos.model.GuiaDespacho;
import com.duoc.sistema_pedidos.model.Pedido;
import com.duoc.sistema_pedidos.model.Usuario;
import com.duoc.sistema_pedidos.repository.GuiaDespachoRepository;
import com.duoc.sistema_pedidos.repository.UsuarioRepository;
import com.duoc.sistema_pedidos.service.contrato.GuiaDespachoS3Service;
import com.duoc.sistema_pedidos.service.contrato.GuiaDespachoService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;

@Service
@RequiredArgsConstructor
public class GuiaDespachoS3ServiceImpl implements GuiaDespachoS3Service {

    private final S3Client s3Client;
    private final GuiaDespachoRepository guiaDespachoRepository;
    private final GuiaDespachoService guiaDespachoService;

    @Value("${aws.s3.bucket}")
    private String bucket;

    /**
     * Genera la ruta en S3 organizada por fecha y transportista.
     * Ejemplo: 2026-06-04/MiguelTorres/GUIA-001.txt
     */
    private String generarKey(GuiaDespacho guia) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String fecha = sdf.format(guia.getFechaCreacion());
        String transportista = guia.getTransportista().getNombre().replaceAll(" ", "");
        return fecha + "/" + transportista + "/" + guia.getNumeroGuia() + ".txt";
    }

    @Override
    public byte[] generarGuia(Long guiaId) {
        GuiaDespacho guia = guiaDespachoRepository.findById(guiaId)
                .orElseThrow(() -> new RuntimeException("Guía de despacho no encontrada"));

        Pedido pedido = guia.getPedido();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

        StringBuilder sb = new StringBuilder();
        sb.append("========================================\n");
        sb.append("     GUÍA DE DESPACHO ").append(guia.getNumeroGuia()).append("\n");
        sb.append("========================================\n\n");
        sb.append("Estado: ").append(guia.getEstado()).append("\n");
        sb.append("Fecha: ").append(sdf.format(guia.getFechaCreacion())).append("\n\n");
        sb.append("--- Datos del Pedido ---\n");
        sb.append("Descripción: ").append(pedido.getDescripcion()).append("\n");
        sb.append("Remitente: ").append(pedido.getRemitente().getNombre()).append("\n");
        sb.append("Origen: ").append(pedido.getDireccionOrigen()).append("\n");
        sb.append("Destinatario: ").append(pedido.getDestinatario().getNombre()).append("\n");
        sb.append("Destino: ").append(pedido.getDireccionDestino()).append("\n\n");
        sb.append("--- Transportista ---\n");
        sb.append("Nombre: ").append(guia.getTransportista().getNombre()).append("\n");
        sb.append("========================================\n");

        return sb.toString().getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public void subirGuia(Long guiaId) {
        GuiaDespacho guia = guiaDespachoRepository.findById(guiaId)
                .orElseThrow(() -> new RuntimeException("Guía de despacho no encontrada"));

        byte[] contenido = generarGuia(guiaId);
        String key = generarKey(guia);

        s3Client.putObject(
                PutObjectRequest.builder()
                        .bucket(bucket)
                        .key(key)
                        .contentType("text/plain")
                        .build(),
                RequestBody.fromBytes(contenido)
        );
    }

    @Override
    public byte[] descargarGuia(Long guiaId) {
        GuiaDespacho guia = guiaDespachoRepository.findById(guiaId)
                .orElseThrow(() -> new RuntimeException("Guía de despacho no encontrada"));

        String key = generarKey(guia);
        return s3Client.getObjectAsBytes(
                GetObjectRequest.builder().bucket(bucket).key(key).build()
        ).asByteArray();
    }

    @Override
    public void modificarGuia(Long guiaId, GuiaDespacho guiaDespacho) {
        guiaDespachoService.update(guiaId, guiaDespacho);
        subirGuia(guiaId);
    }

    @Override
    public void borrarGuia(Long guiaId) {
        GuiaDespacho guia = guiaDespachoRepository.findById(guiaId)
                .orElseThrow(() -> new RuntimeException("Guía de despacho no encontrada"));

        String key = generarKey(guia);
        s3Client.deleteObject(
                DeleteObjectRequest.builder().bucket(bucket).key(key).build()
        );
    }
}
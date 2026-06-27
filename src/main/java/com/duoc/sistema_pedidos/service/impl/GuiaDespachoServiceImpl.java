package com.duoc.sistema_pedidos.service.impl;

import com.duoc.sistema_pedidos.model.GuiaDespacho;
import com.duoc.sistema_pedidos.model.Pedido;
import com.duoc.sistema_pedidos.model.Rol;
import com.duoc.sistema_pedidos.model.Usuario;
import com.duoc.sistema_pedidos.repository.GuiaDespachoRepository;
import com.duoc.sistema_pedidos.repository.PedidoRepository;
import com.duoc.sistema_pedidos.repository.UsuarioRepository;
import com.duoc.sistema_pedidos.service.contrato.GuiaDespachoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class GuiaDespachoServiceImpl implements GuiaDespachoService {

    private final GuiaDespachoRepository guiaDespachoRepository;
    private final UsuarioRepository usuarioRepository;
    private final PedidoRepository pedidoRepository;

    @Autowired
    public GuiaDespachoServiceImpl(GuiaDespachoRepository guiaDespachoRepository, UsuarioRepository usuarioRepository, PedidoRepository pedidoRepository) {
        this.guiaDespachoRepository = guiaDespachoRepository;
        this.usuarioRepository = usuarioRepository;
        this.pedidoRepository = pedidoRepository;
    }


    @Override
    public List<GuiaDespacho> findAll() {
        return guiaDespachoRepository.findAll();
    }

    @Override
    public Optional<GuiaDespacho> findById(Long id) {
        if(!guiaDespachoRepository.existsById(id)){
            throw new RuntimeException("Guia de Despacho no encontrado");
        }
        return guiaDespachoRepository.findById(id);
    }

    @Override
    public GuiaDespacho save(GuiaDespacho guiaDespacho) {
        Usuario transportista = usuarioRepository.findById(guiaDespacho.getTransportista().getId())
                .orElseThrow(() -> new RuntimeException("Transportista no encontrado"));

        if (transportista.getRol() != Rol.TRANSPORTISTA) {
            throw new RuntimeException("El usuario no tiene rol TRANSPORTISTA");
        }

        Pedido pedido = pedidoRepository.findById(guiaDespacho.getPedido().getId())
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));

        guiaDespacho.setTransportista(transportista);
        guiaDespacho.setPedido(pedido);
        return guiaDespachoRepository.save(guiaDespacho);
    }

    @Override
    public Optional<GuiaDespacho> update(Long id, GuiaDespacho guiaDespacho) {
        if (!guiaDespachoRepository.existsById(id)) {
            throw new RuntimeException("Guía de despacho no encontrada");
        }

        return guiaDespachoRepository.findById(id).map(g -> {
            Usuario transportista = usuarioRepository.findById(guiaDespacho.getTransportista().getId())
                    .orElseThrow(() -> new RuntimeException("Transportista no encontrado"));

            if (transportista.getRol() != Rol.TRANSPORTISTA) {
                throw new RuntimeException("El usuario no tiene rol TRANSPORTISTA");
            }

            Pedido pedido = pedidoRepository.findById(guiaDespacho.getPedido().getId())
                    .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));

            g.setTransportista(transportista);
            g.setPedido(pedido);
            g.setEstado(guiaDespacho.getEstado());
            return guiaDespachoRepository.save(g);
        });
    }

    @Override
    public Boolean delete(Long id) {
        if(!guiaDespachoRepository.existsById(id)){
            throw new RuntimeException("Guia de Despacho no encontrado");
        }
        guiaDespachoRepository.deleteById(id);
        return true;
    }

    @Override
    public List<GuiaDespacho> findByTransportistaAndFecha(Long transportistaId, Date desde, Date hasta) {
        return guiaDespachoRepository.findByTransportistaIdAndFechaCreacionBetween(transportistaId, desde, hasta);
    }
}

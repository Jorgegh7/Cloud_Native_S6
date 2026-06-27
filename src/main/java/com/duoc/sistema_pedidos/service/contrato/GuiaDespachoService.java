package com.duoc.sistema_pedidos.service.contrato;

import com.duoc.sistema_pedidos.model.GuiaDespacho;
import com.duoc.sistema_pedidos.model.Pedido;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface GuiaDespachoService {

    List<GuiaDespacho> findAll();
    Optional<GuiaDespacho> findById(Long id);
    GuiaDespacho save(GuiaDespacho guiaDespacho);
    Optional<GuiaDespacho> update(Long id, GuiaDespacho guiaDespacho);
    Boolean delete(Long id);
    List<GuiaDespacho> findByTransportistaAndFecha(Long transportistaId, Date desde, Date hasta);

}

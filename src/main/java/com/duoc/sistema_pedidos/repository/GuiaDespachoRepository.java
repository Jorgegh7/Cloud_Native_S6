package com.duoc.sistema_pedidos.repository;

import com.duoc.sistema_pedidos.model.GuiaDespacho;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

public interface GuiaDespachoRepository extends JpaRepository<GuiaDespacho, Long> {
    List<GuiaDespacho> findByTransportistaIdAndFechaCreacionBetween(Long transportistaId, Date desde, Date hasta);
}

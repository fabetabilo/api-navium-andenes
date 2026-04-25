package com.navium.andenes.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.navium.andenes.model.Anden;
import com.navium.andenes.model.EstadoAnden;

@Repository
public interface AndenRepository extends JpaRepository<Anden, Long> {
    
    Optional<Anden> findByCodigo(String codigo);
    
    List<Anden> findByEstado(EstadoAnden estado);
    
    List<Anden> findByZona(String zona);
    
    Optional<Anden> findByZonaAndNumero(String zona, int numero);
    
    boolean existsByZonaAndNumero(String zona, int numero);
    
}
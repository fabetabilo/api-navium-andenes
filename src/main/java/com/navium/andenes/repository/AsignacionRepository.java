package com.navium.andenes.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.navium.andenes.model.Asignacion;

@Repository
public interface AsignacionRepository extends JpaRepository<Asignacion, Long> {

    // obbtiene asignacion activa de un anden
    Optional<Asignacion> findByAndenIdAndHoraFinIsNull(Long andenId);

    List<Asignacion> findByAndenId(Long andenId);

}

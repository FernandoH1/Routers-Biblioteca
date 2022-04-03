package com.proyect.biblioteca.service.Impl;

import com.proyect.biblioteca.model.Recurso;
import com.proyect.biblioteca.repository.RecursosRepository;
import com.proyect.biblioteca.service.RecursoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.concurrent.ExecutionException;

@Service
public class RecursoServiceImpl implements RecursoService {

    @Autowired
    RecursosRepository recursosRepository;

    @Override
    public Flux<Recurso> findAll() {
        return this.recursosRepository.findAll();
    }

    @Override
    public Mono<Recurso> save(Recurso recurso) {
        return this.recursosRepository.save(recurso);
    }

    @Override
    public Mono<Recurso> delete(String id) {
        return this.recursosRepository
                .findById(id)
                .flatMap(recurso -> this.recursosRepository.deleteById(recurso.getId()).thenReturn(recurso));

    }

    @Override
    public Mono<Recurso> update(String id, Recurso recurso) {
        return this.recursosRepository.findById(id)
                .flatMap(recurso1 -> {
                    recurso.setId(id);
                    return save(recurso);
                })
                .switchIfEmpty(Mono.empty());
    }

    @Override
    public Mono<Recurso> findById(String id) {
        return this.recursosRepository.findById(id);
    }

    @Override
    public Mono<String> consultarDisponibilidad(@PathVariable("id") String id) {
        Mono<Recurso> recurso = recursosRepository.findById(id);
        return validarDisponibilidad(recurso);
    }

    @Override
    public Mono<String> validarDisponibilidad(Mono<Recurso> recurso) {
        try {
            return recurso.map(libro -> {
                var disponibilidad = libro.isPrestado() ? "No se encuentra dispobile desde: " + libro.getFechaDeSalida() : "Disponible";
                return Mono.just(disponibilidad);
            }).toFuture().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Mono<String> prestarUnRecurso(String id) {

        return recursosRepository.findById(id).flatMap(recurso -> {
                    if(recurso.isPrestado()){
                        return Mono.just("No esta disponible porque fue prestado el "+ recurso.getFechaDeSalida());
                    }
                    recurso.setPrestado(true);
                    recurso.setFechaDeSalida(LocalDate.now());
                    return recursosRepository.save(recurso).then(Mono.just("El recurso esta disponible"));
                }
        );
    }

    @Override
    public Flux<Recurso> recomendarPorTipoyArea(String tipo, String area) {
        return recursosRepository.findByTipo(tipo).filter(recurso -> recurso.getAreaTematica().equals(area));
    }

    @Override
    public Mono<Object> devolverRecurso(String id) {
        return recursosRepository.findById(id).flatMap(recurso-> {
            if(!recurso.isPrestado() ) {
                return Mono.just("el recurso ya se devolvió, No se puede devolver!!");
            }else{
                recurso.setPrestado(false);
                return recursosRepository.save(recurso);
            }
        } );
    }


}

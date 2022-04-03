package com.proyect.biblioteca.controller;

import com.proyect.biblioteca.model.Recurso;
import com.proyect.biblioteca.service.Impl.RecursoServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@RestController
@RequestMapping("/recurso")
@CrossOrigin(origins = "http://localhost:8080")

public class RecursoController {

    @Autowired
    RecursoServiceImpl recursoService;

    @GetMapping(value = "")
    private Flux<Recurso> allRecursos() {
        return this.recursoService.findAll();
    }

    @PostMapping(value = "")
    @ResponseStatus(HttpStatus.CREATED)
    private Mono<Recurso> saveRecurso(@RequestBody Recurso recurso) {
        return this.recursoService.save(recurso);
    }

    @DeleteMapping("/delete/{id}")
    @ResponseStatus(HttpStatus.OK)
    private Mono<ResponseEntity<Recurso>> deleteRecurso(@PathVariable("id") String id) {
        return this.recursoService.delete(id)
                .flatMap(recurso -> Mono.just(ResponseEntity.ok(recurso)))
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));

    }

    @PutMapping(value = "/edit/{id}")
    @ResponseStatus(HttpStatus.OK)
    private Mono<ResponseEntity<Recurso>> updateCliente(@PathVariable("id") String id, @RequestBody Recurso recurso) {
        return this.recursoService.update(id, recurso)
                .flatMap(recurso1 -> Mono.just(ResponseEntity.ok(recurso1)))
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));

    }

    @GetMapping(value = "/search/{id}")
    private Mono<Recurso> searchRecursoByID(@PathVariable("id") String id) {
        return this.recursoService.findById(id);
    }


    @GetMapping(value = "/isDisponible/{id}")
    public Mono<String> isDisponile(@PathVariable("id") String id){
        var isDisponible = recursoService.consultarDisponibilidad(id);
        if(isDisponible == null){
            return isDisponible;
        }
        return isDisponible;
    }

    @PutMapping("/prestar/{id}")
    public Mono prestarRecurso(@PathVariable("id") String id){
        return recursoService.prestarUnRecurso(id);
    }

    @GetMapping(value = "/recomendar/{tipo}/{area}")
    private Flux<Recurso> Recomendar(@PathVariable("tipo") String tipo, @PathVariable("area") String area) {
        return this.recursoService.recomendarPorTipoyArea(tipo,area);
    }

    @PutMapping("/devolver/{id}")
    public Mono devolverRecurso(@PathVariable("id") String id){
        return recursoService.devolverRecurso(id);
    }


}

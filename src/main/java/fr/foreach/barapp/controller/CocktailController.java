package fr.foreach.barapp.controller;

import fr.foreach.barapp.dtos.CocktailDto;
import fr.foreach.barapp.services.CocktailService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cocktails")
@Validated
public class CocktailController {

    private final CocktailService cocktailService;

    public CocktailController(CocktailService cocktailService) { this.cocktailService = cocktailService; }

    @PostMapping
    public ResponseEntity<Void> create(@RequestBody @Validated CocktailDto dto) {
        cocktailService.save(dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<CocktailDto> get(@PathVariable Long id) {
        return ResponseEntity.ok(cocktailService.find(id));
    }

    @GetMapping
    public ResponseEntity<List<CocktailDto>> list() {
        return ResponseEntity.ok(cocktailService.findAll());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@PathVariable Long id, @RequestBody CocktailDto dto) {
        cocktailService.update(dto, id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        cocktailService.remove(id);
        return ResponseEntity.noContent().build();
    }
}

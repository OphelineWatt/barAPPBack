package fr.foreach.barapp.controller;

import fr.foreach.barapp.dtos.IngredientDto;
import fr.foreach.barapp.services.IngredientService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ingredients")
@Validated
public class IngredientController {

    private final IngredientService ingredientService;

    public IngredientController(IngredientService ingredientService) { this.ingredientService = ingredientService; }

    @PostMapping
    public ResponseEntity<Void> create(@RequestBody @Validated IngredientDto dto) {
        ingredientService.save(dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<IngredientDto> get(@PathVariable Long id) {
        return ResponseEntity.ok(ingredientService.find(id));
    }

    @GetMapping
    public ResponseEntity<List<IngredientDto>> list() {
        return ResponseEntity.ok(ingredientService.findAll());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@PathVariable Long id, @RequestBody @Validated IngredientDto dto) {
        ingredientService.update(dto, id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        ingredientService.remove(id);
        return ResponseEntity.noContent().build();
    }
}

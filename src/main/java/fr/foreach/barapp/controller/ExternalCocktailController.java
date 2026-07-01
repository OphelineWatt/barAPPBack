package fr.foreach.barapp.controller;

import fr.foreach.barapp.client.CocktailDbClient;
import fr.foreach.barapp.dtos.ExternalCocktailDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/external/cocktails")
@RequiredArgsConstructor
public class ExternalCocktailController {

    private final CocktailDbClient cocktailDbClient;

    @GetMapping("/search")
    public ResponseEntity<List<ExternalCocktailDto>> search(@RequestParam String name) {
        return ResponseEntity.ok(cocktailDbClient.search(name));
    }
}

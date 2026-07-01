package fr.foreach.barapp.client;

import fr.foreach.barapp.dtos.ExternalCocktailDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

@Service
@Slf4j
public class CocktailDbClient {

    private static final String BASE_URL = "https://www.thecocktaildb.com/api/json/v1/1";

    private final RestClient restClient;

    public CocktailDbClient() {
        this.restClient = RestClient.builder()
                .baseUrl(BASE_URL)
                .build();
    }

    public List<ExternalCocktailDto> search(String name) {
        try {
            CocktailDbSearchResponse response = restClient.get()
                    .uri("/search.php?s={name}", name)
                    .retrieve()
                    .body(CocktailDbSearchResponse.class);

            if (response == null || response.getDrinks() == null) {
                return List.of();
            }

            return response.getDrinks().stream()
                    .map(d -> ExternalCocktailDto.builder()
                            .name(d.getName())
                            .imageUrl(d.getImageUrl())
                            .ingredients(d.getIngredients())
                            .build())
                    .toList();
        } catch (Exception e) {
            log.warn("TheCocktailDB search failed for '{}': {}", name, e.getMessage());
            return List.of();
        }
    }
}

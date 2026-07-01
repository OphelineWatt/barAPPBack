package fr.foreach.barapp.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.foreach.barapp.dtos.CocktailDto;
import fr.foreach.barapp.entities.Cocktail;
import fr.foreach.barapp.entities.CocktailPrice;
import fr.foreach.barapp.exceptions.ResourceNotFoundException;
import fr.foreach.barapp.mapper.CocktailMapper;
import fr.foreach.barapp.repositories.CategoryRepository;
import fr.foreach.barapp.repositories.CocktailPriceRepository;
import fr.foreach.barapp.repositories.CocktailRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class CocktailService {

    private final CocktailRepository cocktailRepository;
    private final CategoryRepository categoryRepository;
    private final CocktailPriceRepository cocktailPriceRepository;
    private final CocktailMapper cocktailMapper;

    public List<CocktailDto> findAll() {
        List<Cocktail> cocktails = cocktailRepository.findAll();
        return cocktails.stream().map(cocktailMapper::toDto).collect(Collectors.toList());
    }

    public CocktailDto find(Long id) {
        Cocktail cocktail = cocktailRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cocktail not found with id " + id));
        return cocktailMapper.toDto(cocktail);
    }

    public void save(CocktailDto dto) {
        Cocktail entity = cocktailMapper.toEntity(dto);
        if (dto.getCategoryId() != null) {
            entity.setCategory(categoryRepository.getReferenceById(dto.getCategoryId()));
        }
        Cocktail saved = cocktailRepository.save(entity);

        if (dto.getPrices() != null) {
            List<CocktailPrice> prices = dto.getPrices().stream()
                    .filter(p -> p.getSizeId() != null && p.getPrice() != null)
                    .map(p -> CocktailPrice.builder()
                            .cocktailId(saved.getId())
                            .sizeId(p.getSizeId())
                            .price(p.getPrice())
                            .build())
                    .collect(Collectors.toList());
            cocktailPriceRepository.saveAll(prices);
        }
    }

    public void update(CocktailDto dto, Long id) {
        Cocktail existing = cocktailRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cocktail not found with id " + id));
        // update fields
        existing.setName(dto.getName());
        existing.setDescription(dto.getDescription());
        existing.setImageUrl(dto.getImageUrl());
        existing.setActive(dto.isActive());
        cocktailRepository.save(existing);
    }

    public void remove(Long id) {
        if (!cocktailRepository.existsById(id)) {
            throw new ResourceNotFoundException("Cocktail not found with id " + id);
        }
        cocktailRepository.deleteById(id);
    }
}

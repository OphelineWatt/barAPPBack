package fr.foreach.barapp.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.foreach.barapp.dtos.IngredientDto;
import fr.foreach.barapp.entities.Ingredient;
import fr.foreach.barapp.exceptions.ResourceNotFoundException;
import fr.foreach.barapp.mapper.IngredientMapper;
import fr.foreach.barapp.repositories.IngredientRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class IngredientService {

    private final IngredientRepository ingredientRepository;
    private final IngredientMapper ingredientMapper;

    public List<IngredientDto> findAll() {
        return ingredientRepository.findAll().stream().map(ingredientMapper::toDto).collect(Collectors.toList());
    }

    public IngredientDto find(Long id) {
        Ingredient ingredient = ingredientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ingredient not found with id " + id));
        return ingredientMapper.toDto(ingredient);
    }

    public void save(IngredientDto dto) {
        Ingredient entity = ingredientMapper.toEntity(dto);
        ingredientRepository.save(entity);
    }

    public void update(IngredientDto dto, Long id) {
        Ingredient existing = ingredientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ingredient not found with id " + id));
        existing.setName(dto.getName());
        existing.setUnit(dto.getUnit());
        ingredientRepository.save(existing);
    }

    public void remove(Long id) {
        if (!ingredientRepository.existsById(id)) {
            throw new ResourceNotFoundException("Ingredient not found with id " + id);
        }
        ingredientRepository.deleteById(id);
    }
}

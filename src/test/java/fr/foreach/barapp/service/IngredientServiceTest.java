package fr.foreach.barapp.service;

import fr.foreach.barapp.dtos.IngredientDto;
import fr.foreach.barapp.entities.Ingredient;
import fr.foreach.barapp.exceptions.ResourceNotFoundException;
import fr.foreach.barapp.mapper.IngredientMapper;
import fr.foreach.barapp.repositories.IngredientRepository;
import fr.foreach.barapp.services.IngredientService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("IngredientService Unit Tests")
class IngredientServiceTest {

    @Mock
    private IngredientRepository ingredientRepository;

    @Mock
    private IngredientMapper ingredientMapper;

    private IngredientService ingredientService;

    private Ingredient testIngredient;
    private IngredientDto testDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ingredientService = new IngredientService(ingredientRepository, ingredientMapper);

        testIngredient = Ingredient.builder().id(1L).name("Rhum blanc").unit("cl").build();
        testDto = IngredientDto.builder().id(1L).name("Rhum blanc").unit("cl").build();
    }

    @Test
    @DisplayName("findAll should return list of mapped ingredients")
    void testFindAll() {
        when(ingredientRepository.findAll()).thenReturn(List.of(testIngredient));
        when(ingredientMapper.toDto(testIngredient)).thenReturn(testDto);

        List<IngredientDto> result = ingredientService.findAll();

        assertEquals(1, result.size());
        assertEquals("Rhum blanc", result.get(0).getName());
    }

    @Test
    @DisplayName("find should return ingredient when found")
    void testFindSuccess() {
        when(ingredientRepository.findById(1L)).thenReturn(Optional.of(testIngredient));
        when(ingredientMapper.toDto(testIngredient)).thenReturn(testDto);

        IngredientDto result = ingredientService.find(1L);

        assertEquals("Rhum blanc", result.getName());
    }

    @Test
    @DisplayName("find should throw ResourceNotFoundException when not found")
    void testFindNotFound() {
        when(ingredientRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> ingredientService.find(99L));
    }

    @Test
    @DisplayName("save should map dto to entity and persist it")
    void testSave() {
        when(ingredientMapper.toEntity(testDto)).thenReturn(testIngredient);

        ingredientService.save(testDto);

        verify(ingredientRepository, times(1)).save(testIngredient);
    }

    @Test
    @DisplayName("update should modify existing ingredient fields and persist")
    void testUpdateSuccess() {
        IngredientDto updateDto = IngredientDto.builder().name("Rhum ambré").unit("ml").build();
        when(ingredientRepository.findById(1L)).thenReturn(Optional.of(testIngredient));

        ingredientService.update(updateDto, 1L);

        assertEquals("Rhum ambré", testIngredient.getName());
        assertEquals("ml", testIngredient.getUnit());
        verify(ingredientRepository, times(1)).save(testIngredient);
    }

    @Test
    @DisplayName("update should throw ResourceNotFoundException when ingredient not found")
    void testUpdateNotFound() {
        when(ingredientRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> ingredientService.update(testDto, 99L));
        verify(ingredientRepository, never()).save(any());
    }

    @Test
    @DisplayName("remove should delete ingredient when it exists")
    void testRemoveSuccess() {
        when(ingredientRepository.existsById(1L)).thenReturn(true);

        ingredientService.remove(1L);

        verify(ingredientRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("remove should throw ResourceNotFoundException when ingredient not found")
    void testRemoveNotFound() {
        when(ingredientRepository.existsById(99L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> ingredientService.remove(99L));
        verify(ingredientRepository, never()).deleteById(any());
    }
}

package fr.foreach.barapp.service;

import fr.foreach.barapp.dtos.CocktailDto;
import fr.foreach.barapp.entities.Cocktail;
import fr.foreach.barapp.exceptions.ResourceNotFoundException;
import fr.foreach.barapp.mapper.CocktailMapper;
import fr.foreach.barapp.repositories.CocktailRepository;
import fr.foreach.barapp.services.CocktailService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("CocktailService Unit Tests")
class CocktailServiceTest {

    @Mock
    private CocktailRepository cocktailRepository;

    @Mock
    private CocktailMapper cocktailMapper;

    @InjectMocks
    private CocktailService cocktailService;

    private Cocktail testCocktail;
    private CocktailDto testDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        testCocktail = Cocktail.builder()
                .id(1L)
                .name("Mojito")
                .description("Cuba libre cousin")
                .imageUrl("http://example.com/mojito.png")
                .active(true)
                .build();

        testDto = CocktailDto.builder()
                .id(1L)
                .name("Mojito")
                .description("Cuba libre cousin")
                .imageUrl("http://example.com/mojito.png")
                .active(true)
                .build();
    }

    @Test
    @DisplayName("findAll should return list of mapped cocktails")
    void testFindAll() {
        when(cocktailRepository.findAll()).thenReturn(List.of(testCocktail));
        when(cocktailMapper.toDto(testCocktail)).thenReturn(testDto);

        List<CocktailDto> result = cocktailService.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Mojito", result.get(0).getName());
        verify(cocktailRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("find should return cocktail when found")
    void testFindSuccess() {
        when(cocktailRepository.findById(1L)).thenReturn(Optional.of(testCocktail));
        when(cocktailMapper.toDto(testCocktail)).thenReturn(testDto);

        CocktailDto result = cocktailService.find(1L);

        assertNotNull(result);
        assertEquals("Mojito", result.getName());
        verify(cocktailRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("find should throw ResourceNotFoundException when not found")
    void testFindNotFound() {
        when(cocktailRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> cocktailService.find(99L));
        verify(cocktailRepository, times(1)).findById(99L);
    }

    @Test
    @DisplayName("save should map dto to entity and persist it")
    void testSave() {
        when(cocktailMapper.toEntity(testDto)).thenReturn(testCocktail);

        cocktailService.save(testDto);

        verify(cocktailMapper, times(1)).toEntity(testDto);
        verify(cocktailRepository, times(1)).save(testCocktail);
    }

    @Test
    @DisplayName("update should modify existing cocktail fields and persist")
    void testUpdateSuccess() {
        CocktailDto updateDto = CocktailDto.builder()
                .name("Mojito Updated")
                .description("New description")
                .imageUrl("http://example.com/updated.png")
                .active(false)
                .build();

        when(cocktailRepository.findById(1L)).thenReturn(Optional.of(testCocktail));

        cocktailService.update(updateDto, 1L);

        assertEquals("Mojito Updated", testCocktail.getName());
        assertEquals("New description", testCocktail.getDescription());
        assertEquals("http://example.com/updated.png", testCocktail.getImageUrl());
        assertFalse(testCocktail.isActive());
        verify(cocktailRepository, times(1)).save(testCocktail);
    }

    @Test
    @DisplayName("update should throw ResourceNotFoundException when cocktail not found")
    void testUpdateNotFound() {
        when(cocktailRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> cocktailService.update(testDto, 99L));
        verify(cocktailRepository, never()).save(any());
    }

    @Test
    @DisplayName("remove should delete cocktail when it exists")
    void testRemoveSuccess() {
        when(cocktailRepository.existsById(1L)).thenReturn(true);

        cocktailService.remove(1L);

        verify(cocktailRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("remove should throw ResourceNotFoundException when cocktail not found")
    void testRemoveNotFound() {
        when(cocktailRepository.existsById(99L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> cocktailService.remove(99L));
        verify(cocktailRepository, never()).deleteById(any());
    }
}

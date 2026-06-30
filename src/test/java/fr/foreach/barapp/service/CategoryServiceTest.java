package fr.foreach.barapp.service;

import fr.foreach.barapp.dtos.CategoryDto;
import fr.foreach.barapp.entities.Category;
import fr.foreach.barapp.exceptions.ResourceNotFoundException;
import fr.foreach.barapp.mapper.CategoryMapper;
import fr.foreach.barapp.repositories.CategoryRepository;
import fr.foreach.barapp.services.CategoryService;

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

@DisplayName("CategoryService Unit Tests")
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryMapper categoryMapper;

    private CategoryService categoryService;

    private Category testCategory;
    private CategoryDto testDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        categoryService = new CategoryService(categoryRepository, categoryMapper);

        testCategory = Category.builder().id(1L).name("Classiques").description("Cocktails classiques").build();
        testDto = CategoryDto.builder().id(1L).name("Classiques").description("Cocktails classiques").build();
    }

    @Test
    @DisplayName("findAll should return list of mapped categories")
    void testFindAll() {
        when(categoryRepository.findAll()).thenReturn(List.of(testCategory));
        when(categoryMapper.toDto(testCategory)).thenReturn(testDto);

        List<CategoryDto> result = categoryService.findAll();

        assertEquals(1, result.size());
        assertEquals("Classiques", result.get(0).getName());
    }

    @Test
    @DisplayName("find should return category when found")
    void testFindSuccess() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));
        when(categoryMapper.toDto(testCategory)).thenReturn(testDto);

        CategoryDto result = categoryService.find(1L);

        assertEquals("Classiques", result.getName());
    }

    @Test
    @DisplayName("find should throw ResourceNotFoundException when not found")
    void testFindNotFound() {
        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> categoryService.find(99L));
    }

    @Test
    @DisplayName("save should map dto to entity and persist it")
    void testSave() {
        when(categoryMapper.toEntity(testDto)).thenReturn(testCategory);

        categoryService.save(testDto);

        verify(categoryRepository, times(1)).save(testCategory);
    }

    @Test
    @DisplayName("update should modify existing category fields and persist")
    void testUpdateSuccess() {
        CategoryDto updateDto = CategoryDto.builder().name("Tiki").description("Cocktails tiki").build();
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));

        categoryService.update(updateDto, 1L);

        assertEquals("Tiki", testCategory.getName());
        assertEquals("Cocktails tiki", testCategory.getDescription());
        verify(categoryRepository, times(1)).save(testCategory);
    }

    @Test
    @DisplayName("update should throw ResourceNotFoundException when category not found")
    void testUpdateNotFound() {
        when(categoryRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> categoryService.update(testDto, 99L));
        verify(categoryRepository, never()).save(any());
    }

    @Test
    @DisplayName("remove should delete category when it exists")
    void testRemoveSuccess() {
        when(categoryRepository.existsById(1L)).thenReturn(true);

        categoryService.remove(1L);

        verify(categoryRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("remove should throw ResourceNotFoundException when category not found")
    void testRemoveNotFound() {
        when(categoryRepository.existsById(99L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> categoryService.remove(99L));
        verify(categoryRepository, never()).deleteById(any());
    }
}

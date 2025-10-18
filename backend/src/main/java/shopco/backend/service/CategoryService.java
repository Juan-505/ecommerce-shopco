package shopco.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shopco.backend.entity.Category;
import shopco.backend.repository.CategoryRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CategoryService {
    
    @Autowired
    private CategoryRepository categoryRepository;
    
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }
    
    public Page<Category> getAllCategories(Pageable pageable) {
        return categoryRepository.findAll(pageable);
    }
    
    public Optional<Category> getCategoryById(String id) {
        return categoryRepository.findById(id);
    }
    
    public Optional<Category> getCategoryBySlug(String slug) {
        return categoryRepository.findBySlug(slug);
    }
    
    public List<Category> getActiveCategories() {
        return categoryRepository.findByActiveTrue();
    }
    
    public List<Category> getRootCategories() {
        return categoryRepository.findByParentIdIsNull();
    }
    
    public List<Category> getChildCategories(String parentId) {
        return categoryRepository.findByParentId(parentId);
    }
    
    public Category createCategory(Category category) {
        category.setCreatedAt(LocalDateTime.now());
        category.setUpdatedAt(LocalDateTime.now());
        return categoryRepository.save(category);
    }
    
    public Category updateCategory(Category category) {
        category.setUpdatedAt(LocalDateTime.now());
        return categoryRepository.save(category);
    }
    
    public void deleteCategory(String id) {
        categoryRepository.deleteById(id);
    }
    
    public Category activateCategory(String id) {
        Category category = categoryRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Category not found"));
        
        category.setActive(true);
        category.setUpdatedAt(LocalDateTime.now());
        
        return categoryRepository.save(category);
    }
    
    public Category deactivateCategory(String id) {
        Category category = categoryRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Category not found"));
        
        category.setActive(false);
        category.setUpdatedAt(LocalDateTime.now());
        
        return categoryRepository.save(category);
    }
}

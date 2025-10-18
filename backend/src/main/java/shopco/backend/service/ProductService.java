package shopco.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shopco.backend.entity.Product;
import shopco.backend.entity.ProductStatus;
import shopco.backend.repository.ProductRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ProductService {
    
    @Autowired
    private ProductRepository productRepository;
    
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }
    
    public Page<Product> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable);
    }
    
    public Optional<Product> getProductById(String id) {
        return productRepository.findById(id);
    }
    
    public Optional<Product> getProductBySlug(String slug) {
        return productRepository.findBySlug(slug);
    }
    
    public List<Product> getProductsByStatus(ProductStatus status) {
        return productRepository.findByStatus(status);
    }
    
    public Page<Product> getPublishedProducts(Pageable pageable) {
        return productRepository.findPublishedProductsOrderByCreatedAt(ProductStatus.PUBLISHED, pageable);
    }
    
    public List<Product> getProductsByBrand(String brandId) {
        return productRepository.findByBrandId(brandId);
    }
    
    public Page<Product> getPublishedProductsByBrand(String brandId, Pageable pageable) {
        return productRepository.findPublishedProductsByBrand(ProductStatus.PUBLISHED, brandId, pageable);
    }
    
    public List<Product> getProductsByCategory(String categoryId) {
        return productRepository.findByCategoryId(categoryId);
    }
    
    public Page<Product> getPublishedProductsByCategory(String categoryId, Pageable pageable) {
        return productRepository.findPublishedProductsByCategory(ProductStatus.PUBLISHED, categoryId, pageable);
    }
    
    public Page<Product> searchProducts(String keyword, Pageable pageable) {
        return productRepository.searchByKeyword(keyword, ProductStatus.PUBLISHED, pageable);
    }
    
    public Product createProduct(Product product) {
        product.setCreatedAt(LocalDateTime.now());
        product.setUpdatedAt(LocalDateTime.now());
        return productRepository.save(product);
    }
    
    public Product updateProduct(Product product) {
        product.setUpdatedAt(LocalDateTime.now());
        return productRepository.save(product);
    }
    
    public void deleteProduct(String id) {
        productRepository.deleteById(id);
    }
    
    public Product publishProduct(String id) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Product not found"));
        
        product.setStatus(ProductStatus.PUBLISHED);
        product.setUpdatedAt(LocalDateTime.now());
        
        return productRepository.save(product);
    }
    
    public Product archiveProduct(String id) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Product not found"));
        
        product.setStatus(ProductStatus.ARCHIVED);
        product.setUpdatedAt(LocalDateTime.now());
        
        return productRepository.save(product);
    }
    
    public Product draftProduct(String id) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Product not found"));
        
        product.setStatus(ProductStatus.DRAFT);
        product.setUpdatedAt(LocalDateTime.now());
        
        return productRepository.save(product);
    }
}

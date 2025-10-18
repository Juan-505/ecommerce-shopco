package shopco.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shopco.backend.entity.Product;
import shopco.backend.enums.ProductStatus;
import shopco.backend.service.ProductService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "*")
public class ProductController {
    
    @Autowired
    private ProductService productService;
    
    @GetMapping
    public ResponseEntity<Page<Product>> getAllProducts(Pageable pageable) {
        Page<Product> products = productService.getAllProducts(pageable);
        return ResponseEntity.ok(products);
    }
    
    @GetMapping("/all")
    public ResponseEntity<List<Product>> getAllProductsList() {
        List<Product> products = productService.getAllProducts();
        return ResponseEntity.ok(products);
    }
    
    @GetMapping("/published")
    public ResponseEntity<Page<Product>> getPublishedProducts(Pageable pageable) {
        Page<Product> products = productService.getPublishedProducts(pageable);
        return ResponseEntity.ok(products);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable String id) {
        Optional<Product> product = productService.getProductById(id);
        return product.map(ResponseEntity::ok)
                     .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/slug/{slug}")
    public ResponseEntity<Product> getProductBySlug(@PathVariable String slug) {
        Optional<Product> product = productService.getProductBySlug(slug);
        return product.map(ResponseEntity::ok)
                     .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Product>> getProductsByStatus(@PathVariable ProductStatus status) {
        List<Product> products = productService.getProductsByStatus(status);
        return ResponseEntity.ok(products);
    }
    
    @GetMapping("/brand/{brandId}")
    public ResponseEntity<List<Product>> getProductsByBrand(@PathVariable String brandId) {
        List<Product> products = productService.getProductsByBrand(brandId);
        return ResponseEntity.ok(products);
    }
    
    @GetMapping("/brand/{brandId}/published")
    public ResponseEntity<Page<Product>> getPublishedProductsByBrand(@PathVariable String brandId, Pageable pageable) {
        Page<Product> products = productService.getPublishedProductsByBrand(brandId, pageable);
        return ResponseEntity.ok(products);
    }
    
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<Product>> getProductsByCategory(@PathVariable String categoryId) {
        List<Product> products = productService.getProductsByCategory(categoryId);
        return ResponseEntity.ok(products);
    }
    
    @GetMapping("/category/{categoryId}/published")
    public ResponseEntity<Page<Product>> getPublishedProductsByCategory(@PathVariable String categoryId, Pageable pageable) {
        Page<Product> products = productService.getPublishedProductsByCategory(categoryId, pageable);
        return ResponseEntity.ok(products);
    }
    
    @GetMapping("/search")
    public ResponseEntity<Page<Product>> searchProducts(@RequestParam String keyword, Pageable pageable) {
        Page<Product> products = productService.searchProducts(keyword, pageable);
        return ResponseEntity.ok(products);
    }
    
    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        Product createdProduct = productService.createProduct(product);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdProduct);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable String id, @RequestBody Product product) {
        product.setId(id);
        Product updatedProduct = productService.updateProduct(product);
        return ResponseEntity.ok(updatedProduct);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable String id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
    
    @PostMapping("/{id}/publish")
    public ResponseEntity<Product> publishProduct(@PathVariable String id) {
        Product product = productService.publishProduct(id);
        return ResponseEntity.ok(product);
    }
    
    @PostMapping("/{id}/archive")
    public ResponseEntity<Product> archiveProduct(@PathVariable String id) {
        Product product = productService.archiveProduct(id);
        return ResponseEntity.ok(product);
    }
    
    @PostMapping("/{id}/draft")
    public ResponseEntity<Product> draftProduct(@PathVariable String id) {
        Product product = productService.draftProduct(id);
        return ResponseEntity.ok(product);
    }
}

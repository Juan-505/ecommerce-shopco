package shopco.backend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import shopco.backend.entity.Product;
import shopco.backend.enums.ProductStatus;
import shopco.backend.repository.ProductRepository;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    private Product testProduct;
    private List<Product> testProducts;

    @BeforeEach
    void setUp() {
        testProduct = new Product();
        testProduct.setId("product-1");
        testProduct.setName("Test Product");
        testProduct.setSlug("test-product");
        testProduct.setDescription("A test product");
        testProduct.setStatus(ProductStatus.PUBLISHED);
        testProduct.setCreatedAt(LocalDateTime.now());

        Product draftProduct = new Product();
        draftProduct.setId("product-2");
        draftProduct.setName("Draft Product");
        draftProduct.setSlug("draft-product");
        draftProduct.setStatus(ProductStatus.DRAFT);
        draftProduct.setCreatedAt(LocalDateTime.now());

        testProducts = Arrays.asList(testProduct, draftProduct);
    }

    @Test
    void getAllProducts_ShouldReturnAllProducts() {
        // Given
        when(productRepository.findAll()).thenReturn(testProducts);

        // When
        List<Product> result = productService.getAllProducts();

        // Then
        assertEquals(2, result.size());
        assertEquals("Test Product", result.get(0).getName());
        verify(productRepository).findAll();
    }

    @Test
    void getAllProductsWithPageable_ShouldReturnPageOfProducts() {
        // Given
        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<Product> productPage = new PageImpl<>(testProducts, pageRequest, 2);
        when(productRepository.findAll(pageRequest)).thenReturn(productPage);

        // When
        Page<Product> result = productService.getAllProducts(pageRequest);

        // Then
        assertEquals(2, result.getContent().size());
        assertEquals(2, result.getTotalElements());
        verify(productRepository).findAll(pageRequest);
    }

    @Test
    void getProductById_WhenProductExists_ShouldReturnProduct() {
        // Given
        when(productRepository.findById("product-1")).thenReturn(Optional.of(testProduct));

        // When
        Optional<Product> result = productService.getProductById("product-1");

        // Then
        assertTrue(result.isPresent());
        assertEquals("Test Product", result.get().getName());
        verify(productRepository).findById("product-1");
    }

    @Test
    void getProductById_WhenProductNotFound_ShouldReturnEmpty() {
        // Given
        when(productRepository.findById("non-existent")).thenReturn(Optional.empty());

        // When
        Optional<Product> result = productService.getProductById("non-existent");

        // Then
        assertFalse(result.isPresent());
        verify(productRepository).findById("non-existent");
    }

    @Test
    void getProductBySlug_WhenProductExists_ShouldReturnProduct() {
        // Given
        when(productRepository.findBySlug("test-product")).thenReturn(Optional.of(testProduct));

        // When
        Optional<Product> result = productService.getProductBySlug("test-product");

        // Then
        assertTrue(result.isPresent());
        assertEquals("test-product", result.get().getSlug());
        verify(productRepository).findBySlug("test-product");
    }

    @Test
    void getProductsByStatus_ShouldReturnProductsWithStatus() {
        // Given
        List<Product> publishedProducts = Arrays.asList(testProduct);
        when(productRepository.findByStatus(ProductStatus.PUBLISHED)).thenReturn(publishedProducts);

        // When
        List<Product> result = productService.getProductsByStatus(ProductStatus.PUBLISHED);

        // Then
        assertEquals(1, result.size());
        assertEquals(ProductStatus.PUBLISHED, result.get(0).getStatus());
        verify(productRepository).findByStatus(ProductStatus.PUBLISHED);
    }

    @Test
    void getPublishedProducts_ShouldReturnPublishedProducts() {
        // Given
        List<Product> publishedProducts = Arrays.asList(testProduct);
        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<Product> productPage = new PageImpl<>(publishedProducts, pageRequest, 1);
        when(productRepository.findPublishedProductsOrderByCreatedAt(ProductStatus.PUBLISHED, pageRequest))
                .thenReturn(productPage);

        // When
        Page<Product> result = productService.getPublishedProducts(pageRequest);

        // Then
        assertEquals(1, result.getContent().size());
        assertEquals(ProductStatus.PUBLISHED, result.getContent().get(0).getStatus());
        verify(productRepository).findPublishedProductsOrderByCreatedAt(ProductStatus.PUBLISHED, pageRequest);
    }

    @Test
    void getProductsByBrand_ShouldReturnProductsForBrand() {
        // Given
        when(productRepository.findByBrandId("brand-1")).thenReturn(testProducts);

        // When
        List<Product> result = productService.getProductsByBrand("brand-1");

        // Then
        assertEquals(2, result.size());
        verify(productRepository).findByBrandId("brand-1");
    }

    @Test
    void getPublishedProductsByBrand_ShouldReturnPublishedProductsForBrand() {
        // Given
        List<Product> publishedProducts = Arrays.asList(testProduct);
        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<Product> productPage = new PageImpl<>(publishedProducts, pageRequest, 1);
        when(productRepository.findPublishedProductsByBrand(ProductStatus.PUBLISHED, "brand-1", pageRequest))
                .thenReturn(productPage);

        // When
        Page<Product> result = productService.getPublishedProductsByBrand("brand-1", pageRequest);

        // Then
        assertEquals(1, result.getContent().size());
        verify(productRepository).findPublishedProductsByBrand(ProductStatus.PUBLISHED, "brand-1", pageRequest);
    }

    @Test
    void getProductsByCategory_ShouldReturnProductsForCategory() {
        // Given
        when(productRepository.findByCategoryId("category-1")).thenReturn(testProducts);

        // When
        List<Product> result = productService.getProductsByCategory("category-1");

        // Then
        assertEquals(2, result.size());
        verify(productRepository).findByCategoryId("category-1");
    }

    @Test
    void getPublishedProductsByCategory_ShouldReturnPublishedProductsForCategory() {
        // Given
        List<Product> publishedProducts = Arrays.asList(testProduct);
        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<Product> productPage = new PageImpl<>(publishedProducts, pageRequest, 1);
        when(productRepository.findPublishedProductsByCategory(ProductStatus.PUBLISHED, "category-1", pageRequest))
                .thenReturn(productPage);

        // When
        Page<Product> result = productService.getPublishedProductsByCategory("category-1", pageRequest);

        // Then
        assertEquals(1, result.getContent().size());
        verify(productRepository).findPublishedProductsByCategory(ProductStatus.PUBLISHED, "category-1", pageRequest);
    }

    @Test
    void searchProducts_ShouldReturnSearchResults() {
        // Given
        List<Product> searchResults = Arrays.asList(testProduct);
        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<Product> productPage = new PageImpl<>(searchResults, pageRequest, 1);
        when(productRepository.searchByKeyword("test", ProductStatus.PUBLISHED, pageRequest))
                .thenReturn(productPage);

        // When
        Page<Product> result = productService.searchProducts("test", pageRequest);

        // Then
        assertEquals(1, result.getContent().size());
        verify(productRepository).searchByKeyword("test", ProductStatus.PUBLISHED, pageRequest);
    }

    @Test
    void createProduct_ShouldCreateAndReturnProduct() {
        // Given
        Product newProduct = new Product();
        newProduct.setName("New Product");
        newProduct.setSlug("new-product");
        newProduct.setStatus(ProductStatus.DRAFT);

        when(productRepository.save(any(Product.class))).thenReturn(testProduct);

        // When
        Product result = productService.createProduct(newProduct);

        // Then
        assertNotNull(result);
        assertEquals("Test Product", result.getName());
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void updateProduct_ShouldUpdateAndReturnProduct() {
        // Given
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);

        // When
        Product result = productService.updateProduct(testProduct);

        // Then
        assertNotNull(result);
        verify(productRepository).save(testProduct);
    }

    @Test
    void deleteProduct_ShouldDeleteProduct() {
        // Given
        doNothing().when(productRepository).deleteById("product-1");

        // When
        productService.deleteProduct("product-1");

        // Then
        verify(productRepository).deleteById("product-1");
    }

    @Test
    void publishProduct_ShouldPublishProduct() {
        // Given
        when(productRepository.findById("product-1")).thenReturn(Optional.of(testProduct));
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);

        // When
        Product result = productService.publishProduct("product-1");

        // Then
        assertNotNull(result);
        verify(productRepository).findById("product-1");
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void publishProduct_WhenProductNotFound_ShouldThrowException() {
        // Given
        when(productRepository.findById("non-existent")).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, () -> 
            productService.publishProduct("non-existent"));
    }

    @Test
    void archiveProduct_ShouldArchiveProduct() {
        // Given
        when(productRepository.findById("product-1")).thenReturn(Optional.of(testProduct));
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);

        // When
        Product result = productService.archiveProduct("product-1");

        // Then
        assertNotNull(result);
        verify(productRepository).findById("product-1");
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void draftProduct_ShouldDraftProduct() {
        // Given
        when(productRepository.findById("product-1")).thenReturn(Optional.of(testProduct));
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);

        // When
        Product result = productService.draftProduct("product-1");

        // Then
        assertNotNull(result);
        verify(productRepository).findById("product-1");
        verify(productRepository).save(any(Product.class));
    }
}

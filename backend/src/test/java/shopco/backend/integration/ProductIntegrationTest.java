package shopco.backend.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureTestMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import shopco.backend.entity.Product;
import shopco.backend.enums.ProductStatus;
import shopco.backend.repository.ProductRepository;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureTestMvc
@ActiveProfiles("test")
@Transactional
class ProductIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Product testProduct;

    @BeforeEach
    void setUp() {
        // Clean up database
        productRepository.deleteAll();

        // Create test product
        testProduct = new Product();
        testProduct.setId("product-1");
        testProduct.setName("Test Product");
        testProduct.setSlug("test-product");
        testProduct.setDescription("A test product");
        testProduct.setStatus(ProductStatus.PUBLISHED);
        testProduct.setCreatedAt(LocalDateTime.now());
        testProduct.setUpdatedAt(LocalDateTime.now());
        productRepository.save(testProduct);
    }

    @Test
    void createProduct_ShouldCreateProductInDatabase() throws Exception {
        // Given
        Product newProduct = new Product();
        newProduct.setName("New Product");
        newProduct.setSlug("new-product");
        newProduct.setDescription("A new product");
        newProduct.setStatus(ProductStatus.DRAFT);

        // When & Then
        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newProduct)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("New Product"))
                .andExpect(jsonPath("$.slug").value("new-product"))
                .andExpect(jsonPath("$.status").value("DRAFT"));

        // Verify product was saved in database
        assertTrue(productRepository.findBySlug("new-product").isPresent());
    }

    @Test
    void getProductById_ShouldReturnProductFromDatabase() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/products/product-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("product-1"))
                .andExpect(jsonPath("$.name").value("Test Product"))
                .andExpect(jsonPath("$.slug").value("test-product"));
    }

    @Test
    void getProductBySlug_ShouldReturnProductFromDatabase() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/products/slug/test-product"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.slug").value("test-product"))
                .andExpect(jsonPath("$.name").value("Test Product"));
    }

    @Test
    void updateProduct_ShouldUpdateProductInDatabase() throws Exception {
        // Given
        Product updatedProduct = new Product();
        updatedProduct.setId("product-1");
        updatedProduct.setName("Updated Product");
        updatedProduct.setSlug("test-product");
        updatedProduct.setDescription("Updated description");
        updatedProduct.setStatus(ProductStatus.PUBLISHED);

        // When & Then
        mockMvc.perform(put("/api/products/product-1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedProduct)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Product"))
                .andExpect(jsonPath("$.description").value("Updated description"));

        // Verify product was updated in database
        Product savedProduct = productRepository.findById("product-1").orElseThrow();
        assertEquals("Updated Product", savedProduct.getName());
        assertEquals("Updated description", savedProduct.getDescription());
    }

    @Test
    void publishProduct_ShouldPublishProductInDatabase() throws Exception {
        // Given - Create draft product
        Product draftProduct = new Product();
        draftProduct.setId("product-2");
        draftProduct.setName("Draft Product");
        draftProduct.setSlug("draft-product");
        draftProduct.setStatus(ProductStatus.DRAFT);
        draftProduct.setCreatedAt(LocalDateTime.now());
        draftProduct.setUpdatedAt(LocalDateTime.now());
        productRepository.save(draftProduct);

        // When & Then
        mockMvc.perform(post("/api/products/product-2/publish"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PUBLISHED"));

        // Verify product was published in database
        Product savedProduct = productRepository.findById("product-2").orElseThrow();
        assertEquals(ProductStatus.PUBLISHED, savedProduct.getStatus());
    }

    @Test
    void archiveProduct_ShouldArchiveProductInDatabase() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/products/product-1/archive"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ARCHIVED"));

        // Verify product was archived in database
        Product savedProduct = productRepository.findById("product-1").orElseThrow();
        assertEquals(ProductStatus.ARCHIVED, savedProduct.getStatus());
    }

    @Test
    void draftProduct_ShouldDraftProductInDatabase() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/products/product-1/draft"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("DRAFT"));

        // Verify product was drafted in database
        Product savedProduct = productRepository.findById("product-1").orElseThrow();
        assertEquals(ProductStatus.DRAFT, savedProduct.getStatus());
    }

    @Test
    void getProductsByStatus_ShouldReturnProductsWithStatus() throws Exception {
        // Given - Create draft product
        Product draftProduct = new Product();
        draftProduct.setId("product-2");
        draftProduct.setName("Draft Product");
        draftProduct.setSlug("draft-product");
        draftProduct.setStatus(ProductStatus.DRAFT);
        draftProduct.setCreatedAt(LocalDateTime.now());
        draftProduct.setUpdatedAt(LocalDateTime.now());
        productRepository.save(draftProduct);

        // When & Then
        mockMvc.perform(get("/api/products/status/PUBLISHED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].status").value("PUBLISHED"));
    }

    @Test
    void getPublishedProducts_ShouldReturnPublishedProducts() throws Exception {
        // Given - Create draft product
        Product draftProduct = new Product();
        draftProduct.setId("product-2");
        draftProduct.setName("Draft Product");
        draftProduct.setSlug("draft-product");
        draftProduct.setStatus(ProductStatus.DRAFT);
        draftProduct.setCreatedAt(LocalDateTime.now());
        draftProduct.setUpdatedAt(LocalDateTime.now());
        productRepository.save(draftProduct);

        // When & Then
        mockMvc.perform(get("/api/products/published")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].status").value("PUBLISHED"));
    }

    @Test
    void searchProducts_ShouldReturnSearchResults() throws Exception {
        // Given - Create additional products
        Product searchProduct = new Product();
        searchProduct.setId("product-2");
        searchProduct.setName("Searchable Product");
        searchProduct.setSlug("searchable-product");
        searchProduct.setDescription("This is a searchable product");
        searchProduct.setStatus(ProductStatus.PUBLISHED);
        searchProduct.setCreatedAt(LocalDateTime.now());
        searchProduct.setUpdatedAt(LocalDateTime.now());
        productRepository.save(searchProduct);

        // When & Then
        mockMvc.perform(get("/api/products/search")
                .param("keyword", "searchable")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].name").value("Searchable Product"));
    }

    @Test
    void deleteProduct_ShouldDeleteProductFromDatabase() throws Exception {
        // When & Then
        mockMvc.perform(delete("/api/products/product-1"))
                .andExpect(status().isNoContent());

        // Verify product was deleted from database
        assertFalse(productRepository.findById("product-1").isPresent());
    }

    @Test
    void getAllProducts_ShouldReturnPaginatedResults() throws Exception {
        // Given - Create additional products
        for (int i = 2; i <= 5; i++) {
            Product product = new Product();
            product.setId("product-" + i);
            product.setName("Product " + i);
            product.setSlug("product-" + i);
            product.setStatus(ProductStatus.PUBLISHED);
            product.setCreatedAt(LocalDateTime.now());
            product.setUpdatedAt(LocalDateTime.now());
            productRepository.save(product);
        }

        // When & Then
        mockMvc.perform(get("/api/products")
                .param("page", "0")
                .param("size", "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(3))
                .andExpect(jsonPath("$.totalElements").value(5))
                .andExpect(jsonPath("$.totalPages").value(2));
    }
}

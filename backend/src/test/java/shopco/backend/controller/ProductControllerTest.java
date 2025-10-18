package shopco.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureTestMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import shopco.backend.entity.Product;
import shopco.backend.entity.ProductStatus;
import shopco.backend.service.ProductService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureTestMvc
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @Autowired
    private ObjectMapper objectMapper;

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
    void getAllProducts_ShouldReturnPageOfProducts() throws Exception {
        // Given
        Page<Product> productPage = new PageImpl<>(testProducts, PageRequest.of(0, 10), 2);
        when(productService.getAllProducts(any(PageRequest.class))).thenReturn(productPage);

        // When & Then
        mockMvc.perform(get("/api/products")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].name").value("Test Product"));
    }

    @Test
    void getAllProductsList_ShouldReturnListOfProducts() throws Exception {
        // Given
        when(productService.getAllProducts()).thenReturn(testProducts);

        // When & Then
        mockMvc.perform(get("/api/products/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void getPublishedProducts_ShouldReturnPublishedProducts() throws Exception {
        // Given
        List<Product> publishedProducts = Arrays.asList(testProduct);
        Page<Product> productPage = new PageImpl<>(publishedProducts, PageRequest.of(0, 10), 1);
        when(productService.getPublishedProducts(any(PageRequest.class))).thenReturn(productPage);

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
    void getProductById_WhenProductExists_ShouldReturnProduct() throws Exception {
        // Given
        when(productService.getProductById("product-1")).thenReturn(Optional.of(testProduct));

        // When & Then
        mockMvc.perform(get("/api/products/product-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("product-1"))
                .andExpect(jsonPath("$.name").value("Test Product"));
    }

    @Test
    void getProductById_WhenProductNotFound_ShouldReturn404() throws Exception {
        // Given
        when(productService.getProductById("non-existent")).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/products/non-existent"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getProductBySlug_WhenProductExists_ShouldReturnProduct() throws Exception {
        // Given
        when(productService.getProductBySlug("test-product")).thenReturn(Optional.of(testProduct));

        // When & Then
        mockMvc.perform(get("/api/products/slug/test-product"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.slug").value("test-product"));
    }

    @Test
    void getProductsByStatus_ShouldReturnProductsWithStatus() throws Exception {
        // Given
        List<Product> publishedProducts = Arrays.asList(testProduct);
        when(productService.getProductsByStatus(ProductStatus.PUBLISHED)).thenReturn(publishedProducts);

        // When & Then
        mockMvc.perform(get("/api/products/status/PUBLISHED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].status").value("PUBLISHED"));
    }

    @Test
    void getProductsByBrand_ShouldReturnProductsForBrand() throws Exception {
        // Given
        when(productService.getProductsByBrand("brand-1")).thenReturn(testProducts);

        // When & Then
        mockMvc.perform(get("/api/products/brand/brand-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void getPublishedProductsByBrand_ShouldReturnPublishedProductsForBrand() throws Exception {
        // Given
        List<Product> publishedProducts = Arrays.asList(testProduct);
        Page<Product> productPage = new PageImpl<>(publishedProducts, PageRequest.of(0, 10), 1);
        when(productService.getPublishedProductsByBrand("brand-1", any(PageRequest.class)))
                .thenReturn(productPage);

        // When & Then
        mockMvc.perform(get("/api/products/brand/brand-1/published")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(1));
    }

    @Test
    void getProductsByCategory_ShouldReturnProductsForCategory() throws Exception {
        // Given
        when(productService.getProductsByCategory("category-1")).thenReturn(testProducts);

        // When & Then
        mockMvc.perform(get("/api/products/category/category-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void searchProducts_ShouldReturnSearchResults() throws Exception {
        // Given
        List<Product> searchResults = Arrays.asList(testProduct);
        Page<Product> productPage = new PageImpl<>(searchResults, PageRequest.of(0, 10), 1);
        when(productService.searchProducts("test", any(PageRequest.class))).thenReturn(productPage);

        // When & Then
        mockMvc.perform(get("/api/products/search")
                .param("keyword", "test")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(1));
    }

    @Test
    void createProduct_ShouldCreateAndReturnProduct() throws Exception {
        // Given
        Product newProduct = new Product();
        newProduct.setName("New Product");
        newProduct.setSlug("new-product");
        newProduct.setStatus(ProductStatus.DRAFT);

        when(productService.createProduct(any(Product.class))).thenReturn(testProduct);

        // When & Then
        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newProduct)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Test Product"));
    }

    @Test
    void updateProduct_ShouldUpdateAndReturnProduct() throws Exception {
        // Given
        Product updatedProduct = new Product();
        updatedProduct.setId("product-1");
        updatedProduct.setName("Updated Product");

        when(productService.updateProduct(any(Product.class))).thenReturn(updatedProduct);

        // When & Then
        mockMvc.perform(put("/api/products/product-1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedProduct)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Product"));
    }

    @Test
    void deleteProduct_ShouldDeleteProduct() throws Exception {
        // Given
        doNothing().when(productService).deleteProduct("product-1");

        // When & Then
        mockMvc.perform(delete("/api/products/product-1"))
                .andExpect(status().isNoContent());

        verify(productService).deleteProduct("product-1");
    }

    @Test
    void publishProduct_ShouldPublishProduct() throws Exception {
        // Given
        Product publishedProduct = new Product();
        publishedProduct.setId("product-1");
        publishedProduct.setStatus(ProductStatus.PUBLISHED);

        when(productService.publishProduct("product-1")).thenReturn(publishedProduct);

        // When & Then
        mockMvc.perform(post("/api/products/product-1/publish"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PUBLISHED"));
    }

    @Test
    void archiveProduct_ShouldArchiveProduct() throws Exception {
        // Given
        Product archivedProduct = new Product();
        archivedProduct.setId("product-1");
        archivedProduct.setStatus(ProductStatus.ARCHIVED);

        when(productService.archiveProduct("product-1")).thenReturn(archivedProduct);

        // When & Then
        mockMvc.perform(post("/api/products/product-1/archive"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ARCHIVED"));
    }

    @Test
    void draftProduct_ShouldDraftProduct() throws Exception {
        // Given
        Product draftProduct = new Product();
        draftProduct.setId("product-1");
        draftProduct.setStatus(ProductStatus.DRAFT);

        when(productService.draftProduct("product-1")).thenReturn(draftProduct);

        // When & Then
        mockMvc.perform(post("/api/products/product-1/draft"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("DRAFT"));
    }
}

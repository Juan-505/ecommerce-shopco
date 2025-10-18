package shopco.backend.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import shopco.backend.entity.Product;
import shopco.backend.entity.ProductStatus;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, String> {
    
    Optional<Product> findBySlug(String slug);
    
    List<Product> findByStatus(ProductStatus status);
    
    List<Product> findByBrandId(String brandId);
    
    List<Product> findByCategoryId(String categoryId);
    
    @Query("SELECT p FROM Product p WHERE p.status = :status AND p.brand.id = :brandId")
    List<Product> findByStatusAndBrandId(@Param("status") ProductStatus status, @Param("brandId") String brandId);
    
    @Query("SELECT p FROM Product p WHERE p.status = :status AND p.category.id = :categoryId")
    List<Product> findByStatusAndCategoryId(@Param("status") ProductStatus status, @Param("categoryId") String categoryId);
    
    @Query("SELECT p FROM Product p WHERE p.status = :status AND (p.name LIKE %:keyword% OR p.description LIKE %:keyword%)")
    Page<Product> searchByKeyword(@Param("keyword") String keyword, @Param("status") ProductStatus status, Pageable pageable);
    
    @Query("SELECT p FROM Product p WHERE p.status = :status ORDER BY p.createdAt DESC")
    Page<Product> findPublishedProductsOrderByCreatedAt(@Param("status") ProductStatus status, Pageable pageable);
    
    @Query("SELECT p FROM Product p WHERE p.status = :status AND p.brand.id = :brandId ORDER BY p.createdAt DESC")
    Page<Product> findPublishedProductsByBrand(@Param("status") ProductStatus status, @Param("brandId") String brandId, Pageable pageable);
    
    @Query("SELECT p FROM Product p WHERE p.status = :status AND p.category.id = :categoryId ORDER BY p.createdAt DESC")
    Page<Product> findPublishedProductsByCategory(@Param("status") ProductStatus status, @Param("categoryId") String categoryId, Pageable pageable);
}

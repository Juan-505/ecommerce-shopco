package shopco.backend.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import shopco.backend.entity.Review;
import shopco.backend.enums.ReviewStatus;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, String> {
    
    List<Review> findByProductId(String productId);
    
    Page<Review> findByProductIdOrderByCreatedAtDesc(String productId, Pageable pageable);
    
    List<Review> findByUserId(String userId);
    
    List<Review> findByStatus(ReviewStatus status);
    
    List<Review> findByProductIdAndStatus(String productId, ReviewStatus status);
    
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.productId = :productId AND r.status = :status")
    Double findAverageRatingByProductIdAndStatus(@Param("productId") String productId, @Param("status") ReviewStatus status);
    
    @Query("SELECT COUNT(r) FROM Review r WHERE r.productId = :productId AND r.status = :status")
    Long countByProductIdAndStatus(@Param("productId") String productId, @Param("status") ReviewStatus status);
    
    @Query("SELECT COUNT(r) FROM Review r WHERE r.status = :status")
    Long countByStatus(@Param("status") ReviewStatus status);
}

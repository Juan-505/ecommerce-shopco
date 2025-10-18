package shopco.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import shopco.backend.entity.Brand;

import java.util.List;
import java.util.Optional;

@Repository
public interface BrandRepository extends JpaRepository<Brand, String> {
    
    Optional<Brand> findBySlug(String slug);
    
    List<Brand> findByActiveTrue();
    
    @Query("SELECT b FROM Brand b WHERE b.active = true ORDER BY b.name ASC")
    List<Brand> findActiveBrandsOrderByName();
    
    @Query("SELECT COUNT(b) FROM Brand b WHERE b.active = true")
    Long countActiveBrands();
}

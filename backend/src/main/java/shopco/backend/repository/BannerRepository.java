package shopco.backend.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import shopco.backend.entity.Banner;

import java.util.List;

@Repository
public interface BannerRepository extends JpaRepository<Banner, String> {
    
    List<Banner> findByActiveTrue();
    
    List<Banner> findByPositionAndActiveTrue(String position);
    
    List<Banner> findByActiveTrueOrderBySortOrderAsc();
    
    @Query("SELECT b FROM Banner b WHERE b.active = true AND b.position = :position ORDER BY b.sortOrder ASC")
    List<Banner> findActiveBannersByPositionOrderBySortOrder(@Param("position") String position);
    
    @Query("SELECT COUNT(b) FROM Banner b WHERE b.active = true")
    Long countActiveBanners();
    
    @Query("SELECT COUNT(b) FROM Banner b WHERE b.position = :position AND b.active = true")
    Long countActiveBannersByPosition(@Param("position") String position);
}

package shopco.backend.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import shopco.backend.entity.StaticPage;

import java.util.List;
import java.util.Optional;

@Repository
public interface StaticPageRepository extends JpaRepository<StaticPage, String> {
    
    Optional<StaticPage> findBySlug(String slug);
    
    List<StaticPage> findByActiveTrue();
    
    List<StaticPage> findByTitleContainingIgnoreCase(String title);
    
    @Query("SELECT p FROM StaticPage p WHERE p.active = true ORDER BY p.title ASC")
    List<StaticPage> findActivePagesOrderByTitle();
    
    @Query("SELECT COUNT(p) FROM StaticPage p WHERE p.active = true")
    Long countActivePages();
}

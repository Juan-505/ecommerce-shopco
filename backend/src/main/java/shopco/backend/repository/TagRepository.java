package shopco.backend.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import shopco.backend.entity.Tag;

import java.util.List;
import java.util.Optional;

@Repository
public interface TagRepository extends JpaRepository<Tag, String> {
    
    Optional<Tag> findBySlug(String slug);
    
    List<Tag> findByNameContainingIgnoreCase(String name);
    
    @Query("SELECT t FROM Tag t ORDER BY t.name ASC")
    List<Tag> findAllOrderByName();
    
    @Query("SELECT t FROM Tag t ORDER BY (SELECT COUNT(pt) FROM ProductTag pt WHERE pt.tag = t) DESC")
    List<Tag> findPopularTags(@Param("limit") int limit);
    
    @Query("SELECT COUNT(t) FROM Tag t")
    Long countAllTags();
}

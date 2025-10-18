package shopco.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import shopco.backend.entity.Category;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, String> {
    
    Optional<Category> findBySlug(String slug);
    
    List<Category> findByActiveTrue();
    
    List<Category> findByParentIdIsNull();
    
    List<Category> findByParentId(String parentId);
    
    @Query("SELECT c FROM Category c WHERE c.active = true ORDER BY c.sortOrder ASC")
    List<Category> findActiveCategoriesOrderBySortOrder();
    
    @Query("SELECT c FROM Category c WHERE c.parent.id = :parentId AND c.active = true ORDER BY c.sortOrder ASC")
    List<Category> findActiveChildCategories(@Param("parentId") String parentId);
    
    @Query("SELECT COUNT(c) FROM Category c WHERE c.parent.id = :parentId")
    Long countByParentId(@Param("parentId") String parentId);
}

package shopco.backend.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import shopco.backend.entity.Address;

import java.util.List;

@Repository
public interface AddressRepository extends JpaRepository<Address, String> {
    
    List<Address> findByUserId(String userId);
    
    Page<Address> findByUserId(String userId, Pageable pageable);
    
    List<Address> findByUserIdAndIsDefaultTrue(String userId);
    
    @Query("SELECT a FROM Address a WHERE a.userId = :userId ORDER BY a.isDefault DESC, a.createdAt DESC")
    List<Address> findByUserIdOrderByIsDefaultDescCreatedAtDesc(@Param("userId") String userId);
    
    @Query("SELECT COUNT(a) FROM Address a WHERE a.userId = :userId")
    Long countByUserId(@Param("userId") String userId);
}

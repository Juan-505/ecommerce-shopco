package shopco.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import shopco.backend.entity.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    
    Optional<User> findByEmail(String email);
    
    List<User> findByRole(String role);
    
    List<User> findByBanned(Boolean banned);
    
    @Query("SELECT u FROM User u WHERE u.email = :email AND u.banned = false")
    Optional<User> findActiveUserByEmail(@Param("email") String email);
    
    @Query("SELECT u FROM User u WHERE u.role = :role AND u.banned = false")
    List<User> findActiveUsersByRole(@Param("role") String role);
    
    @Query("SELECT COUNT(u) FROM User u WHERE u.role = :role")
    Long countByRole(@Param("role") String role);
    
    @Query("SELECT COUNT(u) FROM User u WHERE u.banned = true")
    Long countBannedUsers();
}

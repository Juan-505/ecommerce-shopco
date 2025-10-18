package shopco.backend.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import shopco.backend.entity.Coupon;
import shopco.backend.enums.CouponType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, String> {
    
    Optional<Coupon> findByCode(String code);
    
    List<Coupon> findByActiveTrueAndStartsAtBeforeAndEndsAtAfter(LocalDateTime startTime, LocalDateTime endTime);
    
    List<Coupon> findByType(CouponType type);
    
    List<Coupon> findByEndsAtBefore(LocalDateTime dateTime);
    
    List<Coupon> findByStartsAtAfter(LocalDateTime dateTime);
    
    @Query("SELECT c FROM Coupon c WHERE c.code = :code AND c.active = true AND c.startsAt <= :now AND c.endsAt >= :now")
    Optional<Coupon> findByCodeAndActiveTrueAndStartsAtBeforeAndEndsAtAfter(
        @Param("code") String code, 
        @Param("now") LocalDateTime now
    );
    
    @Query("SELECT COUNT(cu) FROM CouponUsage cu WHERE cu.userId = :userId AND cu.couponId = :couponId")
    Long countUsageByUserIdAndCouponId(@Param("userId") String userId, @Param("couponId") String couponId);
    
    @Query("SELECT c FROM Coupon c WHERE c.active = true ORDER BY c.createdAt DESC")
    List<Coupon> findActiveCouponsOrderByCreatedAtDesc();
    
    @Query("SELECT COUNT(c) FROM Coupon c WHERE c.active = true")
    Long countActiveCoupons();
}

package shopco.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shopco.backend.entity.Coupon;
import shopco.backend.enums.CouponType;
import shopco.backend.repository.CouponRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CouponService {
    
    @Autowired
    private CouponRepository couponRepository;
    
    public List<Coupon> getAllCoupons() {
        return couponRepository.findAll();
    }
    
    public Page<Coupon> getAllCoupons(Pageable pageable) {
        return couponRepository.findAll(pageable);
    }
    
    public Optional<Coupon> getCouponById(String id) {
        return couponRepository.findById(id);
    }
    
    public Optional<Coupon> getCouponByCode(String code) {
        return couponRepository.findByCode(code);
    }
    
    public List<Coupon> getActiveCoupons() {
        LocalDateTime now = LocalDateTime.now();
        return couponRepository.findByActiveTrueAndStartsAtBeforeAndEndsAtAfter(now, now);
    }
    
    public List<Coupon> getCouponsByType(CouponType type) {
        return couponRepository.findByType(type);
    }
    
    public List<Coupon> getExpiredCoupons() {
        LocalDateTime now = LocalDateTime.now();
        return couponRepository.findByEndsAtBefore(now);
    }
    
    public List<Coupon> getUpcomingCoupons() {
        LocalDateTime now = LocalDateTime.now();
        return couponRepository.findByStartsAtAfter(now);
    }
    
    public Coupon createCoupon(Coupon coupon) {
        coupon.setCreatedAt(LocalDateTime.now());
        return couponRepository.save(coupon);
    }
    
    public Coupon updateCoupon(Coupon coupon) {
        return couponRepository.save(coupon);
    }
    
    public void deleteCoupon(String id) {
        couponRepository.deleteById(id);
    }
    
    public Coupon activateCoupon(String id) {
        Coupon coupon = couponRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Coupon not found"));
        
        coupon.setActive(true);
        return couponRepository.save(coupon);
    }
    
    public Coupon deactivateCoupon(String id) {
        Coupon coupon = couponRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Coupon not found"));
        
        coupon.setActive(false);
        return couponRepository.save(coupon);
    }
    
    public boolean isCouponValid(String code) {
        LocalDateTime now = LocalDateTime.now();
        return couponRepository.findByCodeAndActiveTrueAndStartsAtBeforeAndEndsAtAfter(code, now, now)
            .isPresent();
    }
    
    public boolean canUseCoupon(String code, String userId) {
        Optional<Coupon> couponOpt = couponRepository.findByCode(code);
        if (couponOpt.isEmpty()) {
            return false;
        }
        
        Coupon coupon = couponOpt.get();
        LocalDateTime now = LocalDateTime.now();
        
        // Check if coupon is active and within date range
        if (!coupon.getActive() || now.isBefore(coupon.getStartsAt()) || now.isAfter(coupon.getEndsAt())) {
            return false;
        }
        
        // Check usage limit
        if (coupon.getUsageLimit() != null && coupon.getUsedCount() >= coupon.getUsageLimit()) {
            return false;
        }
        
        // Check per-user usage limit
        if (coupon.getUsageLimitPerUser() != null) {
            Long userUsageCount = couponRepository.countUsageByUserIdAndCouponId(userId, coupon.getId());
            if (userUsageCount >= coupon.getUsageLimitPerUser()) {
                return false;
            }
        }
        
        return true;
    }
    
    public Coupon incrementUsage(String code) {
        Coupon coupon = couponRepository.findByCode(code)
            .orElseThrow(() -> new RuntimeException("Coupon not found"));
        
        coupon.setUsedCount(coupon.getUsedCount() + 1);
        return couponRepository.save(coupon);
    }
}

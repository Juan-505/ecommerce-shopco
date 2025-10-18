package shopco.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shopco.backend.entity.Coupon;
import shopco.backend.enums.CouponType;
import shopco.backend.service.CouponService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/coupons")
@CrossOrigin(origins = "*")
public class CouponController {
    
    @Autowired
    private CouponService couponService;
    
    @GetMapping
    public ResponseEntity<Page<Coupon>> getAllCoupons(Pageable pageable) {
        Page<Coupon> coupons = couponService.getAllCoupons(pageable);
        return ResponseEntity.ok(coupons);
    }
    
    @GetMapping("/all")
    public ResponseEntity<List<Coupon>> getAllCouponsList() {
        List<Coupon> coupons = couponService.getAllCoupons();
        return ResponseEntity.ok(coupons);
    }
    
    @GetMapping("/active")
    public ResponseEntity<List<Coupon>> getActiveCoupons() {
        List<Coupon> coupons = couponService.getActiveCoupons();
        return ResponseEntity.ok(coupons);
    }
    
    @GetMapping("/type/{type}")
    public ResponseEntity<List<Coupon>> getCouponsByType(@PathVariable CouponType type) {
        List<Coupon> coupons = couponService.getCouponsByType(type);
        return ResponseEntity.ok(coupons);
    }
    
    @GetMapping("/expired")
    public ResponseEntity<List<Coupon>> getExpiredCoupons() {
        List<Coupon> coupons = couponService.getExpiredCoupons();
        return ResponseEntity.ok(coupons);
    }
    
    @GetMapping("/upcoming")
    public ResponseEntity<List<Coupon>> getUpcomingCoupons() {
        List<Coupon> coupons = couponService.getUpcomingCoupons();
        return ResponseEntity.ok(coupons);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Coupon> getCouponById(@PathVariable String id) {
        Optional<Coupon> coupon = couponService.getCouponById(id);
        return coupon.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/code/{code}")
    public ResponseEntity<Coupon> getCouponByCode(@PathVariable String code) {
        Optional<Coupon> coupon = couponService.getCouponByCode(code);
        return coupon.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public ResponseEntity<Coupon> createCoupon(@RequestBody Coupon coupon) {
        Coupon createdCoupon = couponService.createCoupon(coupon);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCoupon);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Coupon> updateCoupon(@PathVariable String id, @RequestBody Coupon coupon) {
        coupon.setId(id);
        Coupon updatedCoupon = couponService.updateCoupon(coupon);
        return ResponseEntity.ok(updatedCoupon);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCoupon(@PathVariable String id) {
        couponService.deleteCoupon(id);
        return ResponseEntity.noContent().build();
    }
    
    @PostMapping("/{id}/activate")
    public ResponseEntity<Coupon> activateCoupon(@PathVariable String id) {
        Coupon coupon = couponService.activateCoupon(id);
        return ResponseEntity.ok(coupon);
    }
    
    @PostMapping("/{id}/deactivate")
    public ResponseEntity<Coupon> deactivateCoupon(@PathVariable String id) {
        Coupon coupon = couponService.deactivateCoupon(id);
        return ResponseEntity.ok(coupon);
    }
    
    @GetMapping("/validate/{code}")
    public ResponseEntity<Boolean> validateCoupon(@PathVariable String code) {
        boolean isValid = couponService.isCouponValid(code);
        return ResponseEntity.ok(isValid);
    }
    
    @GetMapping("/can-use/{code}")
    public ResponseEntity<Boolean> canUseCoupon(@PathVariable String code, @RequestParam String userId) {
        boolean canUse = couponService.canUseCoupon(code, userId);
        return ResponseEntity.ok(canUse);
    }
    
    @PostMapping("/use/{code}")
    public ResponseEntity<Coupon> useCoupon(@PathVariable String code) {
        Coupon coupon = couponService.incrementUsage(code);
        return ResponseEntity.ok(coupon);
    }
}

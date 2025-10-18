package shopco.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shopco.backend.entity.Banner;
import shopco.backend.service.BannerService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/banners")
@CrossOrigin(origins = "*")
public class BannerController {
    
    @Autowired
    private BannerService bannerService;
    
    @GetMapping
    public ResponseEntity<Page<Banner>> getAllBanners(Pageable pageable) {
        Page<Banner> banners = bannerService.getAllBanners(pageable);
        return ResponseEntity.ok(banners);
    }
    
    @GetMapping("/all")
    public ResponseEntity<List<Banner>> getAllBannersList() {
        List<Banner> banners = bannerService.getAllBanners();
        return ResponseEntity.ok(banners);
    }
    
    @GetMapping("/active")
    public ResponseEntity<List<Banner>> getActiveBanners() {
        List<Banner> banners = bannerService.getActiveBanners();
        return ResponseEntity.ok(banners);
    }
    
    @GetMapping("/position/{position}")
    public ResponseEntity<List<Banner>> getBannersByPosition(@PathVariable String position) {
        List<Banner> banners = bannerService.getBannersByPosition(position);
        return ResponseEntity.ok(banners);
    }
    
    @GetMapping("/ordered")
    public ResponseEntity<List<Banner>> getBannersOrderBySortOrder() {
        List<Banner> banners = bannerService.getBannersOrderBySortOrder();
        return ResponseEntity.ok(banners);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Banner> getBannerById(@PathVariable String id) {
        Optional<Banner> banner = bannerService.getBannerById(id);
        return banner.map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public ResponseEntity<Banner> createBanner(@RequestBody Banner banner) {
        Banner createdBanner = bannerService.createBanner(banner);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdBanner);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Banner> updateBanner(@PathVariable String id, @RequestBody Banner banner) {
        banner.setId(id);
        Banner updatedBanner = bannerService.updateBanner(banner);
        return ResponseEntity.ok(updatedBanner);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBanner(@PathVariable String id) {
        bannerService.deleteBanner(id);
        return ResponseEntity.noContent().build();
    }
    
    @PostMapping("/{id}/activate")
    public ResponseEntity<Banner> activateBanner(@PathVariable String id) {
        Banner banner = bannerService.activateBanner(id);
        return ResponseEntity.ok(banner);
    }
    
    @PostMapping("/{id}/deactivate")
    public ResponseEntity<Banner> deactivateBanner(@PathVariable String id) {
        Banner banner = bannerService.deactivateBanner(id);
        return ResponseEntity.ok(banner);
    }
    
    @PutMapping("/{id}/sort-order")
    public ResponseEntity<Banner> updateSortOrder(@PathVariable String id, @RequestParam Integer sortOrder) {
        Banner banner = bannerService.updateSortOrder(id, sortOrder);
        return ResponseEntity.ok(banner);
    }
}

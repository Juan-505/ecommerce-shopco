package shopco.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shopco.backend.entity.Banner;
import shopco.backend.repository.BannerRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class BannerService {
    
    @Autowired
    private BannerRepository bannerRepository;
    
    public List<Banner> getAllBanners() {
        return bannerRepository.findAll();
    }
    
    public Page<Banner> getAllBanners(Pageable pageable) {
        return bannerRepository.findAll(pageable);
    }
    
    public Optional<Banner> getBannerById(String id) {
        return bannerRepository.findById(id);
    }
    
    public List<Banner> getActiveBanners() {
        return bannerRepository.findByActiveTrue();
    }
    
    public List<Banner> getBannersByPosition(String position) {
        return bannerRepository.findByPositionAndActiveTrue(position);
    }
    
    public List<Banner> getBannersOrderBySortOrder() {
        return bannerRepository.findByActiveTrueOrderBySortOrderAsc();
    }
    
    public Banner createBanner(Banner banner) {
        banner.setCreatedAt(LocalDateTime.now());
        banner.setUpdatedAt(LocalDateTime.now());
        return bannerRepository.save(banner);
    }
    
    public Banner updateBanner(Banner banner) {
        banner.setUpdatedAt(LocalDateTime.now());
        return bannerRepository.save(banner);
    }
    
    public void deleteBanner(String id) {
        bannerRepository.deleteById(id);
    }
    
    public Banner activateBanner(String id) {
        Banner banner = bannerRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Banner not found"));
        
        banner.setActive(true);
        banner.setUpdatedAt(LocalDateTime.now());
        
        return bannerRepository.save(banner);
    }
    
    public Banner deactivateBanner(String id) {
        Banner banner = bannerRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Banner not found"));
        
        banner.setActive(false);
        banner.setUpdatedAt(LocalDateTime.now());
        
        return bannerRepository.save(banner);
    }
    
    public Banner updateSortOrder(String id, Integer sortOrder) {
        Banner banner = bannerRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Banner not found"));
        
        banner.setSortOrder(sortOrder);
        banner.setUpdatedAt(LocalDateTime.now());
        
        return bannerRepository.save(banner);
    }
}

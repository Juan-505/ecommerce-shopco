package shopco.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shopco.backend.entity.Brand;
import shopco.backend.repository.BrandRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class BrandService {
    
    @Autowired
    private BrandRepository brandRepository;
    
    public List<Brand> getAllBrands() {
        return brandRepository.findAll();
    }
    
    public Page<Brand> getAllBrands(Pageable pageable) {
        return brandRepository.findAll(pageable);
    }
    
    public Optional<Brand> getBrandById(String id) {
        return brandRepository.findById(id);
    }
    
    public Optional<Brand> getBrandBySlug(String slug) {
        return brandRepository.findBySlug(slug);
    }
    
    public List<Brand> getActiveBrands() {
        return brandRepository.findByActiveTrue();
    }
    
    public Brand createBrand(Brand brand) {
        brand.setCreatedAt(LocalDateTime.now());
        brand.setUpdatedAt(LocalDateTime.now());
        return brandRepository.save(brand);
    }
    
    public Brand updateBrand(Brand brand) {
        brand.setUpdatedAt(LocalDateTime.now());
        return brandRepository.save(brand);
    }
    
    public void deleteBrand(String id) {
        brandRepository.deleteById(id);
    }
    
    public Brand activateBrand(String id) {
        Brand brand = brandRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Brand not found"));
        
        brand.setActive(true);
        brand.setUpdatedAt(LocalDateTime.now());
        
        return brandRepository.save(brand);
    }
    
    public Brand deactivateBrand(String id) {
        Brand brand = brandRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Brand not found"));
        
        brand.setActive(false);
        brand.setUpdatedAt(LocalDateTime.now());
        
        return brandRepository.save(brand);
    }
}

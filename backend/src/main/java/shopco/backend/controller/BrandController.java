package shopco.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shopco.backend.entity.Brand;
import shopco.backend.service.BrandService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/brands")
@CrossOrigin(origins = "*")
public class BrandController {
    
    @Autowired
    private BrandService brandService;
    
    @GetMapping
    public ResponseEntity<Page<Brand>> getAllBrands(Pageable pageable) {
        Page<Brand> brands = brandService.getAllBrands(pageable);
        return ResponseEntity.ok(brands);
    }
    
    @GetMapping("/all")
    public ResponseEntity<List<Brand>> getAllBrandsList() {
        List<Brand> brands = brandService.getAllBrands();
        return ResponseEntity.ok(brands);
    }
    
    @GetMapping("/active")
    public ResponseEntity<List<Brand>> getActiveBrands() {
        List<Brand> brands = brandService.getActiveBrands();
        return ResponseEntity.ok(brands);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Brand> getBrandById(@PathVariable String id) {
        Optional<Brand> brand = brandService.getBrandById(id);
        return brand.map(ResponseEntity::ok)
                   .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/slug/{slug}")
    public ResponseEntity<Brand> getBrandBySlug(@PathVariable String slug) {
        Optional<Brand> brand = brandService.getBrandBySlug(slug);
        return brand.map(ResponseEntity::ok)
                   .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public ResponseEntity<Brand> createBrand(@RequestBody Brand brand) {
        Brand createdBrand = brandService.createBrand(brand);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdBrand);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Brand> updateBrand(@PathVariable String id, @RequestBody Brand brand) {
        brand.setId(id);
        Brand updatedBrand = brandService.updateBrand(brand);
        return ResponseEntity.ok(updatedBrand);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBrand(@PathVariable String id) {
        brandService.deleteBrand(id);
        return ResponseEntity.noContent().build();
    }
    
    @PostMapping("/{id}/activate")
    public ResponseEntity<Brand> activateBrand(@PathVariable String id) {
        Brand brand = brandService.activateBrand(id);
        return ResponseEntity.ok(brand);
    }
    
    @PostMapping("/{id}/deactivate")
    public ResponseEntity<Brand> deactivateBrand(@PathVariable String id) {
        Brand brand = brandService.deactivateBrand(id);
        return ResponseEntity.ok(brand);
    }
}

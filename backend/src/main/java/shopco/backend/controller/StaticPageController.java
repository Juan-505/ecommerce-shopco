package shopco.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shopco.backend.entity.StaticPage;
import shopco.backend.service.StaticPageService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/pages")
@CrossOrigin(origins = "*")
public class StaticPageController {
    
    @Autowired
    private StaticPageService staticPageService;
    
    @GetMapping
    public ResponseEntity<Page<StaticPage>> getAllPages(Pageable pageable) {
        Page<StaticPage> pages = staticPageService.getAllPages(pageable);
        return ResponseEntity.ok(pages);
    }
    
    @GetMapping("/all")
    public ResponseEntity<List<StaticPage>> getAllPagesList() {
        List<StaticPage> pages = staticPageService.getAllPages();
        return ResponseEntity.ok(pages);
    }
    
    @GetMapping("/active")
    public ResponseEntity<List<StaticPage>> getActivePages() {
        List<StaticPage> pages = staticPageService.getActivePages();
        return ResponseEntity.ok(pages);
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<StaticPage>> searchPagesByTitle(@RequestParam String title) {
        List<StaticPage> pages = staticPageService.getPagesByTitleContaining(title);
        return ResponseEntity.ok(pages);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<StaticPage> getPageById(@PathVariable String id) {
        Optional<StaticPage> page = staticPageService.getPageById(id);
        return page.map(ResponseEntity::ok)
                  .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/slug/{slug}")
    public ResponseEntity<StaticPage> getPageBySlug(@PathVariable String slug) {
        Optional<StaticPage> page = staticPageService.getPageBySlug(slug);
        return page.map(ResponseEntity::ok)
                  .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public ResponseEntity<StaticPage> createPage(@RequestBody StaticPage page) {
        StaticPage createdPage = staticPageService.createPage(page);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPage);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<StaticPage> updatePage(@PathVariable String id, @RequestBody StaticPage page) {
        page.setId(id);
        StaticPage updatedPage = staticPageService.updatePage(page);
        return ResponseEntity.ok(updatedPage);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePage(@PathVariable String id) {
        staticPageService.deletePage(id);
        return ResponseEntity.noContent().build();
    }
    
    @PostMapping("/{id}/activate")
    public ResponseEntity<StaticPage> activatePage(@PathVariable String id) {
        StaticPage page = staticPageService.activatePage(id);
        return ResponseEntity.ok(page);
    }
    
    @PostMapping("/{id}/deactivate")
    public ResponseEntity<StaticPage> deactivatePage(@PathVariable String id) {
        StaticPage page = staticPageService.deactivatePage(id);
        return ResponseEntity.ok(page);
    }
}

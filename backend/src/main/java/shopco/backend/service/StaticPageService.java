package shopco.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shopco.backend.entity.StaticPage;
import shopco.backend.repository.StaticPageRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class StaticPageService {
    
    @Autowired
    private StaticPageRepository staticPageRepository;
    
    public List<StaticPage> getAllPages() {
        return staticPageRepository.findAll();
    }
    
    public Page<StaticPage> getAllPages(Pageable pageable) {
        return staticPageRepository.findAll(pageable);
    }
    
    public Optional<StaticPage> getPageById(String id) {
        return staticPageRepository.findById(id);
    }
    
    public Optional<StaticPage> getPageBySlug(String slug) {
        return staticPageRepository.findBySlug(slug);
    }
    
    public List<StaticPage> getActivePages() {
        return staticPageRepository.findByActiveTrue();
    }
    
    public List<StaticPage> getPagesByTitleContaining(String title) {
        return staticPageRepository.findByTitleContainingIgnoreCase(title);
    }
    
    public StaticPage createPage(StaticPage page) {
        page.setCreatedAt(LocalDateTime.now());
        page.setUpdatedAt(LocalDateTime.now());
        return staticPageRepository.save(page);
    }
    
    public StaticPage updatePage(StaticPage page) {
        page.setUpdatedAt(LocalDateTime.now());
        return staticPageRepository.save(page);
    }
    
    public void deletePage(String id) {
        staticPageRepository.deleteById(id);
    }
    
    public StaticPage activatePage(String id) {
        StaticPage page = staticPageRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Page not found"));
        
        page.setActive(true);
        page.setUpdatedAt(LocalDateTime.now());
        
        return staticPageRepository.save(page);
    }
    
    public StaticPage deactivatePage(String id) {
        StaticPage page = staticPageRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Page not found"));
        
        page.setActive(false);
        page.setUpdatedAt(LocalDateTime.now());
        
        return staticPageRepository.save(page);
    }
}

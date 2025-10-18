package shopco.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shopco.backend.entity.Tag;
import shopco.backend.repository.TagRepository;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class TagService {
    
    @Autowired
    private TagRepository tagRepository;
    
    public List<Tag> getAllTags() {
        return tagRepository.findAll();
    }
    
    public Page<Tag> getAllTags(Pageable pageable) {
        return tagRepository.findAll(pageable);
    }
    
    public Optional<Tag> getTagById(String id) {
        return tagRepository.findById(id);
    }
    
    public Optional<Tag> getTagBySlug(String slug) {
        return tagRepository.findBySlug(slug);
    }
    
    public List<Tag> getTagsByNameContaining(String name) {
        return tagRepository.findByNameContainingIgnoreCase(name);
    }
    
    public Tag createTag(Tag tag) {
        return tagRepository.save(tag);
    }
    
    public Tag updateTag(Tag tag) {
        return tagRepository.save(tag);
    }
    
    public void deleteTag(String id) {
        tagRepository.deleteById(id);
    }
    
    public List<Tag> getPopularTags(int limit) {
        return tagRepository.findPopularTags(limit);
    }
}

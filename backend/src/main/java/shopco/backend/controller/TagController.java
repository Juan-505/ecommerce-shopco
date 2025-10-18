package shopco.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shopco.backend.entity.Tag;
import shopco.backend.service.TagService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/tags")
@CrossOrigin(origins = "*")
public class TagController {
    
    @Autowired
    private TagService tagService;
    
    @GetMapping
    public ResponseEntity<Page<Tag>> getAllTags(Pageable pageable) {
        Page<Tag> tags = tagService.getAllTags(pageable);
        return ResponseEntity.ok(tags);
    }
    
    @GetMapping("/all")
    public ResponseEntity<List<Tag>> getAllTagsList() {
        List<Tag> tags = tagService.getAllTags();
        return ResponseEntity.ok(tags);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Tag> getTagById(@PathVariable String id) {
        Optional<Tag> tag = tagService.getTagById(id);
        return tag.map(ResponseEntity::ok)
                 .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/slug/{slug}")
    public ResponseEntity<Tag> getTagBySlug(@PathVariable String slug) {
        Optional<Tag> tag = tagService.getTagBySlug(slug);
        return tag.map(ResponseEntity::ok)
                 .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<Tag>> searchTagsByName(@RequestParam String name) {
        List<Tag> tags = tagService.getTagsByNameContaining(name);
        return ResponseEntity.ok(tags);
    }
    
    @GetMapping("/popular")
    public ResponseEntity<List<Tag>> getPopularTags(@RequestParam(defaultValue = "10") int limit) {
        List<Tag> tags = tagService.getPopularTags(limit);
        return ResponseEntity.ok(tags);
    }
    
    @PostMapping
    public ResponseEntity<Tag> createTag(@RequestBody Tag tag) {
        Tag createdTag = tagService.createTag(tag);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTag);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Tag> updateTag(@PathVariable String id, @RequestBody Tag tag) {
        tag.setId(id);
        Tag updatedTag = tagService.updateTag(tag);
        return ResponseEntity.ok(updatedTag);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTag(@PathVariable String id) {
        tagService.deleteTag(id);
        return ResponseEntity.noContent().build();
    }
}

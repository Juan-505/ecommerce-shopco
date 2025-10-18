package shopco.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import shopco.backend.entity.Tag;
import shopco.backend.service.TagService;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class TagControllerTest {

    @Mock
    private TagService tagService;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new TagController()).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void getAllTags_ShouldReturnPageOfTags() throws Exception {
        // Given
        Tag tag1 = createTestTag("1", "Electronics", "electronics");
        Tag tag2 = createTestTag("2", "Clothing", "clothing");
        List<Tag> tags = Arrays.asList(tag1, tag2);
        Page<Tag> page = new PageImpl<>(tags);
        PageRequest pageRequest = PageRequest.of(0, 10);

        when(tagService.getAllTags(any(PageRequest.class))).thenReturn(page);

        // When & Then
        mockMvc.perform(get("/api/tags")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].id").value("1"))
                .andExpect(jsonPath("$.content[1].id").value("2"));

        verify(tagService).getAllTags(any(PageRequest.class));
    }

    @Test
    void getAllTagsList_ShouldReturnListOfTags() throws Exception {
        // Given
        Tag tag1 = createTestTag("1", "Electronics", "electronics");
        Tag tag2 = createTestTag("2", "Clothing", "clothing");
        List<Tag> tags = Arrays.asList(tag1, tag2);

        when(tagService.getAllTags()).thenReturn(tags);

        // When & Then
        mockMvc.perform(get("/api/tags/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value("1"))
                .andExpect(jsonPath("$[1].id").value("2"));

        verify(tagService).getAllTags();
    }

    @Test
    void getTagById_WhenExists_ShouldReturnTag() throws Exception {
        // Given
        Tag tag = createTestTag("1", "Electronics", "electronics");
        when(tagService.getTagById("1")).thenReturn(Optional.of(tag));

        // When & Then
        mockMvc.perform(get("/api/tags/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.name").value("Electronics"))
                .andExpect(jsonPath("$.slug").value("electronics"));

        verify(tagService).getTagById("1");
    }

    @Test
    void getTagById_WhenNotExists_ShouldReturnNotFound() throws Exception {
        // Given
        when(tagService.getTagById("1")).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/tags/1"))
                .andExpect(status().isNotFound());

        verify(tagService).getTagById("1");
    }

    @Test
    void getTagBySlug_ShouldReturnTag() throws Exception {
        // Given
        Tag tag = createTestTag("1", "Electronics", "electronics");
        when(tagService.getTagBySlug("electronics")).thenReturn(Optional.of(tag));

        // When & Then
        mockMvc.perform(get("/api/tags/slug/electronics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.slug").value("electronics"));

        verify(tagService).getTagBySlug("electronics");
    }

    @Test
    void searchTagsByName_ShouldReturnMatchingTags() throws Exception {
        // Given
        Tag tag1 = createTestTag("1", "Electronics", "electronics");
        Tag tag2 = createTestTag("2", "Electronic Gadgets", "electronic-gadgets");
        List<Tag> tags = Arrays.asList(tag1, tag2);

        when(tagService.getTagsByNameContaining("electronic")).thenReturn(tags);

        // When & Then
        mockMvc.perform(get("/api/tags/search")
                .param("name", "electronic"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Electronics"))
                .andExpect(jsonPath("$[1].name").value("Electronic Gadgets"));

        verify(tagService).getTagsByNameContaining("electronic");
    }

    @Test
    void getPopularTags_ShouldReturnPopularTags() throws Exception {
        // Given
        Tag tag1 = createTestTag("1", "Electronics", "electronics");
        Tag tag2 = createTestTag("2", "Clothing", "clothing");
        List<Tag> tags = Arrays.asList(tag1, tag2);

        when(tagService.getPopularTags(10)).thenReturn(tags);

        // When & Then
        mockMvc.perform(get("/api/tags/popular")
                .param("limit", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));

        verify(tagService).getPopularTags(10);
    }

    @Test
    void getPopularTags_WithDefaultLimit_ShouldReturnPopularTags() throws Exception {
        // Given
        Tag tag1 = createTestTag("1", "Electronics", "electronics");
        List<Tag> tags = Arrays.asList(tag1);

        when(tagService.getPopularTags(10)).thenReturn(tags);

        // When & Then
        mockMvc.perform(get("/api/tags/popular"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));

        verify(tagService).getPopularTags(10);
    }

    @Test
    void createTag_ShouldReturnCreatedTag() throws Exception {
        // Given
        Tag tag = createTestTag("1", "Electronics", "electronics");
        when(tagService.createTag(any(Tag.class))).thenReturn(tag);

        // When & Then
        mockMvc.perform(post("/api/tags")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(tag)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.name").value("Electronics"))
                .andExpect(jsonPath("$.slug").value("electronics"));

        verify(tagService).createTag(any(Tag.class));
    }

    @Test
    void updateTag_ShouldReturnUpdatedTag() throws Exception {
        // Given
        Tag tag = createTestTag("1", "Electronics", "electronics");
        when(tagService.updateTag(any(Tag.class))).thenReturn(tag);

        // When & Then
        mockMvc.perform(put("/api/tags/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(tag)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.name").value("Electronics"));

        verify(tagService).updateTag(any(Tag.class));
    }

    @Test
    void deleteTag_ShouldReturnNoContent() throws Exception {
        // Given
        doNothing().when(tagService).deleteTag("1");

        // When & Then
        mockMvc.perform(delete("/api/tags/1"))
                .andExpect(status().isNoContent());

        verify(tagService).deleteTag("1");
    }

    private Tag createTestTag(String id, String name, String slug) {
        Tag tag = new Tag();
        tag.setId(id);
        tag.setName(name);
        tag.setSlug(slug);
        return tag;
    }
}

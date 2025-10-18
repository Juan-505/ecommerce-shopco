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
import shopco.backend.entity.StaticPage;
import shopco.backend.service.StaticPageService;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class StaticPageControllerTest {

    @Mock
    private StaticPageService staticPageService;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new StaticPageController()).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void getAllPages_ShouldReturnPageOfPages() throws Exception {
        // Given
        StaticPage page1 = createTestPage("1", "About Us", "about-us");
        StaticPage page2 = createTestPage("2", "Contact", "contact");
        List<StaticPage> pages = Arrays.asList(page1, page2);
        Page<StaticPage> page = new PageImpl<>(pages);
        PageRequest pageRequest = PageRequest.of(0, 10);

        when(staticPageService.getAllPages(any(PageRequest.class))).thenReturn(page);

        // When & Then
        mockMvc.perform(get("/api/pages")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].id").value("1"))
                .andExpect(jsonPath("$.content[1].id").value("2"));

        verify(staticPageService).getAllPages(any(PageRequest.class));
    }

    @Test
    void getAllPagesList_ShouldReturnListOfPages() throws Exception {
        // Given
        StaticPage page1 = createTestPage("1", "About Us", "about-us");
        StaticPage page2 = createTestPage("2", "Contact", "contact");
        List<StaticPage> pages = Arrays.asList(page1, page2);

        when(staticPageService.getAllPages()).thenReturn(pages);

        // When & Then
        mockMvc.perform(get("/api/pages/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value("1"))
                .andExpect(jsonPath("$[1].id").value("2"));

        verify(staticPageService).getAllPages();
    }

    @Test
    void getActivePages_ShouldReturnActivePages() throws Exception {
        // Given
        StaticPage page1 = createTestPage("1", "About Us", "about-us");
        StaticPage page2 = createTestPage("2", "Contact", "contact");
        List<StaticPage> pages = Arrays.asList(page1, page2);

        when(staticPageService.getActivePages()).thenReturn(pages);

        // When & Then
        mockMvc.perform(get("/api/pages/active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].active").value(true))
                .andExpect(jsonPath("$[1].active").value(true));

        verify(staticPageService).getActivePages();
    }

    @Test
    void searchPagesByTitle_ShouldReturnMatchingPages() throws Exception {
        // Given
        StaticPage page1 = createTestPage("1", "About Us", "about-us");
        StaticPage page2 = createTestPage("2", "About Company", "about-company");
        List<StaticPage> pages = Arrays.asList(page1, page2);

        when(staticPageService.getPagesByTitleContaining("about")).thenReturn(pages);

        // When & Then
        mockMvc.perform(get("/api/pages/search")
                .param("title", "about"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].title").value("About Us"))
                .andExpect(jsonPath("$[1].title").value("About Company"));

        verify(staticPageService).getPagesByTitleContaining("about");
    }

    @Test
    void getPageById_WhenExists_ShouldReturnPage() throws Exception {
        // Given
        StaticPage page = createTestPage("1", "About Us", "about-us");
        when(staticPageService.getPageById("1")).thenReturn(Optional.of(page));

        // When & Then
        mockMvc.perform(get("/api/pages/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.title").value("About Us"))
                .andExpect(jsonPath("$.slug").value("about-us"));

        verify(staticPageService).getPageById("1");
    }

    @Test
    void getPageById_WhenNotExists_ShouldReturnNotFound() throws Exception {
        // Given
        when(staticPageService.getPageById("1")).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/pages/1"))
                .andExpect(status().isNotFound());

        verify(staticPageService).getPageById("1");
    }

    @Test
    void getPageBySlug_ShouldReturnPage() throws Exception {
        // Given
        StaticPage page = createTestPage("1", "About Us", "about-us");
        when(staticPageService.getPageBySlug("about-us")).thenReturn(Optional.of(page));

        // When & Then
        mockMvc.perform(get("/api/pages/slug/about-us"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.slug").value("about-us"));

        verify(staticPageService).getPageBySlug("about-us");
    }

    @Test
    void createPage_ShouldReturnCreatedPage() throws Exception {
        // Given
        StaticPage page = createTestPage("1", "About Us", "about-us");
        when(staticPageService.createPage(any(StaticPage.class))).thenReturn(page);

        // When & Then
        mockMvc.perform(post("/api/pages")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(page)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.title").value("About Us"))
                .andExpect(jsonPath("$.slug").value("about-us"));

        verify(staticPageService).createPage(any(StaticPage.class));
    }

    @Test
    void updatePage_ShouldReturnUpdatedPage() throws Exception {
        // Given
        StaticPage page = createTestPage("1", "About Us", "about-us");
        when(staticPageService.updatePage(any(StaticPage.class))).thenReturn(page);

        // When & Then
        mockMvc.perform(put("/api/pages/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(page)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.title").value("About Us"));

        verify(staticPageService).updatePage(any(StaticPage.class));
    }

    @Test
    void deletePage_ShouldReturnNoContent() throws Exception {
        // Given
        doNothing().when(staticPageService).deletePage("1");

        // When & Then
        mockMvc.perform(delete("/api/pages/1"))
                .andExpect(status().isNoContent());

        verify(staticPageService).deletePage("1");
    }

    @Test
    void activatePage_ShouldReturnActivatedPage() throws Exception {
        // Given
        StaticPage page = createTestPage("1", "About Us", "about-us");
        when(staticPageService.activatePage("1")).thenReturn(page);

        // When & Then
        mockMvc.perform(post("/api/pages/1/activate"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.active").value(true));

        verify(staticPageService).activatePage("1");
    }

    @Test
    void deactivatePage_ShouldReturnDeactivatedPage() throws Exception {
        // Given
        StaticPage page = createTestPage("1", "About Us", "about-us");
        page.setActive(false);
        when(staticPageService.deactivatePage("1")).thenReturn(page);

        // When & Then
        mockMvc.perform(post("/api/pages/1/deactivate"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.active").value(false));

        verify(staticPageService).deactivatePage("1");
    }

    private StaticPage createTestPage(String id, String title, String slug) {
        StaticPage page = new StaticPage();
        page.setId(id);
        page.setTitle(title);
        page.setSlug(slug);
        page.setContent("This is the content of " + title);
        page.setActive(true);
        page.setSeoTitle("SEO Title for " + title);
        page.setSeoDesc("SEO Description for " + title);
        page.setCreatedAt(LocalDateTime.now());
        page.setUpdatedAt(LocalDateTime.now());
        return page;
    }
}

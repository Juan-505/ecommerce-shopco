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
import shopco.backend.entity.Banner;
import shopco.backend.service.BannerService;

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
class BannerControllerTest {

    @Mock
    private BannerService bannerService;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new BannerController()).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void getAllBanners_ShouldReturnPageOfBanners() throws Exception {
        // Given
        Banner banner1 = createTestBanner("1", "Banner 1", "top");
        Banner banner2 = createTestBanner("2", "Banner 2", "bottom");
        List<Banner> banners = Arrays.asList(banner1, banner2);
        Page<Banner> page = new PageImpl<>(banners);
        PageRequest pageRequest = PageRequest.of(0, 10);

        when(bannerService.getAllBanners(any(PageRequest.class))).thenReturn(page);

        // When & Then
        mockMvc.perform(get("/api/banners")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].id").value("1"))
                .andExpect(jsonPath("$.content[1].id").value("2"));

        verify(bannerService).getAllBanners(any(PageRequest.class));
    }

    @Test
    void getAllBannersList_ShouldReturnListOfBanners() throws Exception {
        // Given
        Banner banner1 = createTestBanner("1", "Banner 1", "top");
        Banner banner2 = createTestBanner("2", "Banner 2", "bottom");
        List<Banner> banners = Arrays.asList(banner1, banner2);

        when(bannerService.getAllBanners()).thenReturn(banners);

        // When & Then
        mockMvc.perform(get("/api/banners/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value("1"))
                .andExpect(jsonPath("$[1].id").value("2"));

        verify(bannerService).getAllBanners();
    }

    @Test
    void getActiveBanners_ShouldReturnActiveBanners() throws Exception {
        // Given
        Banner banner1 = createTestBanner("1", "Banner 1", "top");
        Banner banner2 = createTestBanner("2", "Banner 2", "bottom");
        List<Banner> banners = Arrays.asList(banner1, banner2);

        when(bannerService.getActiveBanners()).thenReturn(banners);

        // When & Then
        mockMvc.perform(get("/api/banners/active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].active").value(true))
                .andExpect(jsonPath("$[1].active").value(true));

        verify(bannerService).getActiveBanners();
    }

    @Test
    void getBannersByPosition_ShouldReturnBannersByPosition() throws Exception {
        // Given
        Banner banner1 = createTestBanner("1", "Banner 1", "top");
        Banner banner2 = createTestBanner("2", "Banner 2", "top");
        List<Banner> banners = Arrays.asList(banner1, banner2);

        when(bannerService.getBannersByPosition("top")).thenReturn(banners);

        // When & Then
        mockMvc.perform(get("/api/banners/position/top"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].position").value("top"))
                .andExpect(jsonPath("$[1].position").value("top"));

        verify(bannerService).getBannersByPosition("top");
    }

    @Test
    void getBannersOrderBySortOrder_ShouldReturnOrderedBanners() throws Exception {
        // Given
        Banner banner1 = createTestBanner("1", "Banner 1", "top");
        banner1.setSortOrder(1);
        Banner banner2 = createTestBanner("2", "Banner 2", "top");
        banner2.setSortOrder(2);
        List<Banner> banners = Arrays.asList(banner1, banner2);

        when(bannerService.getBannersOrderBySortOrder()).thenReturn(banners);

        // When & Then
        mockMvc.perform(get("/api/banners/ordered"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].sortOrder").value(1))
                .andExpect(jsonPath("$[1].sortOrder").value(2));

        verify(bannerService).getBannersOrderBySortOrder();
    }

    @Test
    void getBannerById_WhenExists_ShouldReturnBanner() throws Exception {
        // Given
        Banner banner = createTestBanner("1", "Banner 1", "top");
        when(bannerService.getBannerById("1")).thenReturn(Optional.of(banner));

        // When & Then
        mockMvc.perform(get("/api/banners/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.title").value("Banner 1"))
                .andExpect(jsonPath("$.position").value("top"));

        verify(bannerService).getBannerById("1");
    }

    @Test
    void getBannerById_WhenNotExists_ShouldReturnNotFound() throws Exception {
        // Given
        when(bannerService.getBannerById("1")).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/banners/1"))
                .andExpect(status().isNotFound());

        verify(bannerService).getBannerById("1");
    }

    @Test
    void createBanner_ShouldReturnCreatedBanner() throws Exception {
        // Given
        Banner banner = createTestBanner("1", "Banner 1", "top");
        when(bannerService.createBanner(any(Banner.class))).thenReturn(banner);

        // When & Then
        mockMvc.perform(post("/api/banners")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(banner)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.title").value("Banner 1"))
                .andExpect(jsonPath("$.position").value("top"));

        verify(bannerService).createBanner(any(Banner.class));
    }

    @Test
    void updateBanner_ShouldReturnUpdatedBanner() throws Exception {
        // Given
        Banner banner = createTestBanner("1", "Banner 1", "top");
        when(bannerService.updateBanner(any(Banner.class))).thenReturn(banner);

        // When & Then
        mockMvc.perform(put("/api/banners/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(banner)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.title").value("Banner 1"));

        verify(bannerService).updateBanner(any(Banner.class));
    }

    @Test
    void deleteBanner_ShouldReturnNoContent() throws Exception {
        // Given
        doNothing().when(bannerService).deleteBanner("1");

        // When & Then
        mockMvc.perform(delete("/api/banners/1"))
                .andExpect(status().isNoContent());

        verify(bannerService).deleteBanner("1");
    }

    @Test
    void activateBanner_ShouldReturnActivatedBanner() throws Exception {
        // Given
        Banner banner = createTestBanner("1", "Banner 1", "top");
        when(bannerService.activateBanner("1")).thenReturn(banner);

        // When & Then
        mockMvc.perform(post("/api/banners/1/activate"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.active").value(true));

        verify(bannerService).activateBanner("1");
    }

    @Test
    void deactivateBanner_ShouldReturnDeactivatedBanner() throws Exception {
        // Given
        Banner banner = createTestBanner("1", "Banner 1", "top");
        banner.setActive(false);
        when(bannerService.deactivateBanner("1")).thenReturn(banner);

        // When & Then
        mockMvc.perform(post("/api/banners/1/deactivate"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.active").value(false));

        verify(bannerService).deactivateBanner("1");
    }

    @Test
    void updateSortOrder_ShouldReturnUpdatedBanner() throws Exception {
        // Given
        Banner banner = createTestBanner("1", "Banner 1", "top");
        banner.setSortOrder(5);
        when(bannerService.updateSortOrder("1", 5)).thenReturn(banner);

        // When & Then
        mockMvc.perform(put("/api/banners/1/sort-order")
                .param("sortOrder", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sortOrder").value(5));

        verify(bannerService).updateSortOrder("1", 5);
    }

    private Banner createTestBanner(String id, String title, String position) {
        Banner banner = new Banner();
        banner.setId(id);
        banner.setTitle(title);
        banner.setImageUrl("https://example.com/banner.jpg");
        banner.setLinkUrl("https://example.com");
        banner.setPosition(position);
        banner.setSortOrder(1);
        banner.setActive(true);
        banner.setCreatedAt(LocalDateTime.now());
        banner.setUpdatedAt(LocalDateTime.now());
        return banner;
    }
}

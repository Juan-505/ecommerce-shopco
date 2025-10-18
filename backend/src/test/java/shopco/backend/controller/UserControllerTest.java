package shopco.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureTestMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import shopco.backend.entity.User;
import shopco.backend.service.UserService;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureTestMvc
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private User testUser;
    private List<User> testUsers;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId("user-1");
        testUser.setName("John Doe");
        testUser.setEmail("john@example.com");
        testUser.setRole("user");
        testUser.setBanned(false);
        testUser.setCreatedAt(LocalDateTime.now());

        User adminUser = new User();
        adminUser.setId("admin-1");
        adminUser.setName("Admin User");
        adminUser.setEmail("admin@example.com");
        adminUser.setRole("admin");
        adminUser.setBanned(false);
        adminUser.setCreatedAt(LocalDateTime.now());

        testUsers = Arrays.asList(testUser, adminUser);
    }

    @Test
    void getAllUsers_ShouldReturnPageOfUsers() throws Exception {
        // Given
        Page<User> userPage = new PageImpl<>(testUsers, PageRequest.of(0, 10), 2);
        when(userService.getAllUsers(any(PageRequest.class))).thenReturn(userPage);

        // When & Then
        mockMvc.perform(get("/api/users")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].name").value("John Doe"))
                .andExpect(jsonPath("$.content[1].name").value("Admin User"));
    }

    @Test
    void getAllUsersList_ShouldReturnListOfUsers() throws Exception {
        // Given
        when(userService.getAllUsers()).thenReturn(testUsers);

        // When & Then
        mockMvc.perform(get("/api/users/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("John Doe"));
    }

    @Test
    void getUserById_WhenUserExists_ShouldReturnUser() throws Exception {
        // Given
        when(userService.getUserById("user-1")).thenReturn(Optional.of(testUser));

        // When & Then
        mockMvc.perform(get("/api/users/user-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("user-1"))
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.email").value("john@example.com"));
    }

    @Test
    void getUserById_WhenUserNotFound_ShouldReturn404() throws Exception {
        // Given
        when(userService.getUserById("non-existent")).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/users/non-existent"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getUserByEmail_WhenUserExists_ShouldReturnUser() throws Exception {
        // Given
        when(userService.getUserByEmail("john@example.com")).thenReturn(Optional.of(testUser));

        // When & Then
        mockMvc.perform(get("/api/users/email/john@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("john@example.com"));
    }

    @Test
    void getUsersByRole_ShouldReturnUsersWithRole() throws Exception {
        // Given
        List<User> adminUsers = Arrays.asList(testUsers.get(1));
        when(userService.getUsersByRole("admin")).thenReturn(adminUsers);

        // When & Then
        mockMvc.perform(get("/api/users/role/admin"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].role").value("admin"));
    }

    @Test
    void createUser_ShouldCreateAndReturnUser() throws Exception {
        // Given
        User newUser = new User();
        newUser.setName("Jane Doe");
        newUser.setEmail("jane@example.com");
        newUser.setRole("user");

        when(userService.createUser(any(User.class))).thenReturn(testUser);

        // When & Then
        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newUser)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("John Doe"));
    }

    @Test
    void updateUser_ShouldUpdateAndReturnUser() throws Exception {
        // Given
        User updatedUser = new User();
        updatedUser.setId("user-1");
        updatedUser.setName("John Updated");
        updatedUser.setEmail("john@example.com");

        when(userService.updateUser(any(User.class))).thenReturn(updatedUser);

        // When & Then
        mockMvc.perform(put("/api/users/user-1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("John Updated"));
    }

    @Test
    void deleteUser_ShouldDeleteUser() throws Exception {
        // Given
        doNothing().when(userService).deleteUser("user-1");

        // When & Then
        mockMvc.perform(delete("/api/users/user-1"))
                .andExpect(status().isNoContent());

        verify(userService).deleteUser("user-1");
    }

    @Test
    void banUser_ShouldBanUser() throws Exception {
        // Given
        User bannedUser = new User();
        bannedUser.setId("user-1");
        bannedUser.setBanned(true);
        bannedUser.setBanReason("Test ban");

        when(userService.banUser(eq("user-1"), anyString(), any(LocalDateTime.class)))
                .thenReturn(bannedUser);

        // When & Then
        mockMvc.perform(post("/api/users/user-1/ban")
                .param("reason", "Test ban"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.banned").value(true))
                .andExpect(jsonPath("$.banReason").value("Test ban"));
    }

    @Test
    void unbanUser_ShouldUnbanUser() throws Exception {
        // Given
        User unbannedUser = new User();
        unbannedUser.setId("user-1");
        unbannedUser.setBanned(false);

        when(userService.unbanUser("user-1")).thenReturn(unbannedUser);

        // When & Then
        mockMvc.perform(post("/api/users/user-1/unban"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.banned").value(false));
    }

    @Test
    void updateUserRole_ShouldUpdateRole() throws Exception {
        // Given
        User updatedUser = new User();
        updatedUser.setId("user-1");
        updatedUser.setRole("admin");

        when(userService.updateUserRole("user-1", "admin")).thenReturn(updatedUser);

        // When & Then
        mockMvc.perform(put("/api/users/user-1/role")
                .param("role", "admin"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role").value("admin"));
    }

    @Test
    void countUsersByRole_ShouldReturnCount() throws Exception {
        // Given
        when(userService.countUsersByRole("admin")).thenReturn(5L);

        // When & Then
        mockMvc.perform(get("/api/users/stats/role/admin"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(5));
    }

    @Test
    void countBannedUsers_ShouldReturnCount() throws Exception {
        // Given
        when(userService.countBannedUsers()).thenReturn(2L);

        // When & Then
        mockMvc.perform(get("/api/users/stats/banned"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(2));
    }
}

package shopco.backend.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureTestMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import shopco.backend.entity.User;
import shopco.backend.repository.UserRepository;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureTestMvc
@ActiveProfiles("test")
@Transactional
class UserIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private User testUser;

    @BeforeEach
    void setUp() {
        // Clean up database
        userRepository.deleteAll();

        // Create test user
        testUser = new User();
        testUser.setId("user-1");
        testUser.setName("John Doe");
        testUser.setEmail("john@example.com");
        testUser.setRole("user");
        testUser.setBanned(false);
        testUser.setEmailVerified(true);
        testUser.setCreatedAt(LocalDateTime.now());
        testUser.setUpdatedAt(LocalDateTime.now());
        userRepository.save(testUser);
    }

    @Test
    void createUser_ShouldCreateUserInDatabase() throws Exception {
        // Given
        User newUser = new User();
        newUser.setName("Jane Doe");
        newUser.setEmail("jane@example.com");
        newUser.setRole("user");
        newUser.setBanned(false);
        newUser.setEmailVerified(false);

        // When & Then
        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newUser)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Jane Doe"))
                .andExpect(jsonPath("$.email").value("jane@example.com"))
                .andExpect(jsonPath("$.role").value("user"));

        // Verify user was saved in database
        assert userRepository.findByEmail("jane@example.com").isPresent();
    }

    @Test
    void getUserById_ShouldReturnUserFromDatabase() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/users/user-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("user-1"))
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.email").value("john@example.com"));
    }

    @Test
    void updateUser_ShouldUpdateUserInDatabase() throws Exception {
        // Given
        User updatedUser = new User();
        updatedUser.setId("user-1");
        updatedUser.setName("John Updated");
        updatedUser.setEmail("john@example.com");
        updatedUser.setRole("admin");

        // When & Then
        mockMvc.perform(put("/api/users/user-1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("John Updated"))
                .andExpect(jsonPath("$.role").value("admin"));

        // Verify user was updated in database
        User savedUser = userRepository.findById("user-1").orElseThrow();
        assertEquals("John Updated", savedUser.getName());
        assertEquals("admin", savedUser.getRole());
    }

    @Test
    void banUser_ShouldBanUserInDatabase() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/users/user-1/ban")
                .param("reason", "Test ban"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.banned").value(true))
                .andExpect(jsonPath("$.banReason").value("Test ban"));

        // Verify user was banned in database
        User savedUser = userRepository.findById("user-1").orElseThrow();
        assertTrue(savedUser.getBanned());
        assertEquals("Test ban", savedUser.getBanReason());
    }

    @Test
    void unbanUser_ShouldUnbanUserInDatabase() throws Exception {
        // Given - First ban the user
        testUser.setBanned(true);
        testUser.setBanReason("Test ban");
        userRepository.save(testUser);

        // When & Then
        mockMvc.perform(post("/api/users/user-1/unban"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.banned").value(false));

        // Verify user was unbanned in database
        User savedUser = userRepository.findById("user-1").orElseThrow();
        assertFalse(savedUser.getBanned());
        assertNull(savedUser.getBanReason());
    }

    @Test
    void updateUserRole_ShouldUpdateRoleInDatabase() throws Exception {
        // When & Then
        mockMvc.perform(put("/api/users/user-1/role")
                .param("role", "admin"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role").value("admin"));

        // Verify role was updated in database
        User savedUser = userRepository.findById("user-1").orElseThrow();
        assertEquals("admin", savedUser.getRole());
    }

    @Test
    void deleteUser_ShouldDeleteUserFromDatabase() throws Exception {
        // When & Then
        mockMvc.perform(delete("/api/users/user-1"))
                .andExpect(status().isNoContent());

        // Verify user was deleted from database
        assertFalse(userRepository.findById("user-1").isPresent());
    }

    @Test
    void getUsersByRole_ShouldReturnUsersWithRole() throws Exception {
        // Given - Create admin user
        User adminUser = new User();
        adminUser.setId("admin-1");
        adminUser.setName("Admin User");
        adminUser.setEmail("admin@example.com");
        adminUser.setRole("admin");
        adminUser.setBanned(false);
        adminUser.setEmailVerified(true);
        adminUser.setCreatedAt(LocalDateTime.now());
        adminUser.setUpdatedAt(LocalDateTime.now());
        userRepository.save(adminUser);

        // When & Then
        mockMvc.perform(get("/api/users/role/admin"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].role").value("admin"));
    }

    @Test
    void countUsersByRole_ShouldReturnCorrectCount() throws Exception {
        // Given - Create another user with same role
        User anotherUser = new User();
        anotherUser.setId("user-2");
        anotherUser.setName("Another User");
        anotherUser.setEmail("another@example.com");
        anotherUser.setRole("user");
        anotherUser.setBanned(false);
        anotherUser.setEmailVerified(true);
        anotherUser.setCreatedAt(LocalDateTime.now());
        anotherUser.setUpdatedAt(LocalDateTime.now());
        userRepository.save(anotherUser);

        // When & Then
        mockMvc.perform(get("/api/users/stats/role/user"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(2));
    }

    @Test
    void getAllUsers_ShouldReturnPaginatedResults() throws Exception {
        // Given - Create additional users
        for (int i = 2; i <= 5; i++) {
            User user = new User();
            user.setId("user-" + i);
            user.setName("User " + i);
            user.setEmail("user" + i + "@example.com");
            user.setRole("user");
            user.setBanned(false);
            user.setEmailVerified(true);
            user.setCreatedAt(LocalDateTime.now());
            user.setUpdatedAt(LocalDateTime.now());
            userRepository.save(user);
        }

        // When & Then
        mockMvc.perform(get("/api/users")
                .param("page", "0")
                .param("size", "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(3))
                .andExpect(jsonPath("$.totalElements").value(5))
                .andExpect(jsonPath("$.totalPages").value(2));
    }
}

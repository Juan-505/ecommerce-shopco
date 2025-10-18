package shopco.backend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import shopco.backend.entity.User;
import shopco.backend.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

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
    void getAllUsers_ShouldReturnAllUsers() {
        // Given
        when(userRepository.findAll()).thenReturn(testUsers);

        // When
        List<User> result = userService.getAllUsers();

        // Then
        assertEquals(2, result.size());
        assertEquals("John Doe", result.get(0).getName());
        assertEquals("Admin User", result.get(1).getName());
        verify(userRepository).findAll();
    }

    @Test
    void getAllUsersWithPageable_ShouldReturnPageOfUsers() {
        // Given
        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<User> userPage = new PageImpl<>(testUsers, pageRequest, 2);
        when(userRepository.findAll(pageRequest)).thenReturn(userPage);

        // When
        Page<User> result = userService.getAllUsers(pageRequest);

        // Then
        assertEquals(2, result.getContent().size());
        assertEquals(2, result.getTotalElements());
        verify(userRepository).findAll(pageRequest);
    }

    @Test
    void getUserById_WhenUserExists_ShouldReturnUser() {
        // Given
        when(userRepository.findById("user-1")).thenReturn(Optional.of(testUser));

        // When
        Optional<User> result = userService.getUserById("user-1");

        // Then
        assertTrue(result.isPresent());
        assertEquals("John Doe", result.get().getName());
        verify(userRepository).findById("user-1");
    }

    @Test
    void getUserById_WhenUserNotFound_ShouldReturnEmpty() {
        // Given
        when(userRepository.findById("non-existent")).thenReturn(Optional.empty());

        // When
        Optional<User> result = userService.getUserById("non-existent");

        // Then
        assertFalse(result.isPresent());
        verify(userRepository).findById("non-existent");
    }

    @Test
    void getUserByEmail_WhenUserExists_ShouldReturnUser() {
        // Given
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(testUser));

        // When
        Optional<User> result = userService.getUserByEmail("john@example.com");

        // Then
        assertTrue(result.isPresent());
        assertEquals("john@example.com", result.get().getEmail());
        verify(userRepository).findByEmail("john@example.com");
    }

    @Test
    void getActiveUserByEmail_WhenUserExists_ShouldReturnUser() {
        // Given
        when(userRepository.findActiveUserByEmail("john@example.com")).thenReturn(Optional.of(testUser));

        // When
        Optional<User> result = userService.getActiveUserByEmail("john@example.com");

        // Then
        assertTrue(result.isPresent());
        assertEquals("john@example.com", result.get().getEmail());
        verify(userRepository).findActiveUserByEmail("john@example.com");
    }

    @Test
    void getUsersByRole_ShouldReturnUsersWithRole() {
        // Given
        List<User> adminUsers = Arrays.asList(testUsers.get(1));
        when(userRepository.findByRole("admin")).thenReturn(adminUsers);

        // When
        List<User> result = userService.getUsersByRole("admin");

        // Then
        assertEquals(1, result.size());
        assertEquals("admin", result.get(0).getRole());
        verify(userRepository).findByRole("admin");
    }

    @Test
    void getActiveUsersByRole_ShouldReturnActiveUsersWithRole() {
        // Given
        List<User> adminUsers = Arrays.asList(testUsers.get(1));
        when(userRepository.findActiveUsersByRole("admin")).thenReturn(adminUsers);

        // When
        List<User> result = userService.getActiveUsersByRole("admin");

        // Then
        assertEquals(1, result.size());
        assertEquals("admin", result.get(0).getRole());
        verify(userRepository).findActiveUsersByRole("admin");
    }

    @Test
    void getBannedUsers_ShouldReturnBannedUsers() {
        // Given
        User bannedUser = new User();
        bannedUser.setId("banned-1");
        bannedUser.setBanned(true);
        List<User> bannedUsers = Arrays.asList(bannedUser);
        when(userRepository.findByBanned(true)).thenReturn(bannedUsers);

        // When
        List<User> result = userService.getBannedUsers();

        // Then
        assertEquals(1, result.size());
        assertTrue(result.get(0).getBanned());
        verify(userRepository).findByBanned(true);
    }

    @Test
    void createUser_ShouldCreateAndReturnUser() {
        // Given
        User newUser = new User();
        newUser.setName("Jane Doe");
        newUser.setEmail("jane@example.com");
        newUser.setRole("user");

        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        User result = userService.createUser(newUser);

        // Then
        assertNotNull(result);
        assertEquals("John Doe", result.getName());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void updateUser_ShouldUpdateAndReturnUser() {
        // Given
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        User result = userService.updateUser(testUser);

        // Then
        assertNotNull(result);
        verify(userRepository).save(testUser);
    }

    @Test
    void deleteUser_ShouldDeleteUser() {
        // Given
        doNothing().when(userRepository).deleteById("user-1");

        // When
        userService.deleteUser("user-1");

        // Then
        verify(userRepository).deleteById("user-1");
    }

    @Test
    void banUser_ShouldBanUser() {
        // Given
        when(userRepository.findById("user-1")).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        User result = userService.banUser("user-1", "Test ban", LocalDateTime.now().plusDays(7));

        // Then
        assertNotNull(result);
        verify(userRepository).findById("user-1");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void banUser_WhenUserNotFound_ShouldThrowException() {
        // Given
        when(userRepository.findById("non-existent")).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, () -> 
            userService.banUser("non-existent", "Test ban", LocalDateTime.now().plusDays(7)));
    }

    @Test
    void unbanUser_ShouldUnbanUser() {
        // Given
        when(userRepository.findById("user-1")).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        User result = userService.unbanUser("user-1");

        // Then
        assertNotNull(result);
        verify(userRepository).findById("user-1");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void updateUserRole_ShouldUpdateRole() {
        // Given
        when(userRepository.findById("user-1")).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        User result = userService.updateUserRole("user-1", "admin");

        // Then
        assertNotNull(result);
        verify(userRepository).findById("user-1");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void countUsersByRole_ShouldReturnCount() {
        // Given
        when(userRepository.countByRole("admin")).thenReturn(5L);

        // When
        Long result = userService.countUsersByRole("admin");

        // Then
        assertEquals(5L, result);
        verify(userRepository).countByRole("admin");
    }

    @Test
    void countBannedUsers_ShouldReturnCount() {
        // Given
        when(userRepository.countBannedUsers()).thenReturn(2L);

        // When
        Long result = userService.countBannedUsers();

        // Then
        assertEquals(2L, result);
        verify(userRepository).countBannedUsers();
    }
}

package shopco.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import shopco.backend.entity.User;
import shopco.backend.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    
    public Page<User> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }
    
    public Optional<User> getUserById(String id) {
        return userRepository.findById(id);
    }
    
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    
    public Optional<User> getActiveUserByEmail(String email) {
        return userRepository.findActiveUserByEmail(email);
    }
    
    public List<User> getUsersByRole(String role) {
        return userRepository.findByRole(role);
    }
    
    public List<User> getActiveUsersByRole(String role) {
        return userRepository.findActiveUsersByRole(role);
    }
    
    public List<User> getBannedUsers() {
        return userRepository.findByBanned(true);
    }
    
    public User createUser(User user) {
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }
    
    public User updateUser(User user) {
        user.setUpdatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }
    
    public void deleteUser(String id) {
        userRepository.deleteById(id);
    }
    
    public User banUser(String id, String reason, LocalDateTime banExpires) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        user.setBanned(true);
        user.setBanReason(reason);
        user.setBanExpires(banExpires);
        user.setUpdatedAt(LocalDateTime.now());
        
        return userRepository.save(user);
    }
    
    public User unbanUser(String id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        user.setBanned(false);
        user.setBanReason(null);
        user.setBanExpires(null);
        user.setUpdatedAt(LocalDateTime.now());
        
        return userRepository.save(user);
    }
    
    public User updateUserRole(String id, String role) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        user.setRole(role);
        user.setUpdatedAt(LocalDateTime.now());
        
        return userRepository.save(user);
    }
    
    public Long countUsersByRole(String role) {
        return userRepository.countByRole(role);
    }
    
    public Long countBannedUsers() {
        return userRepository.countBannedUsers();
    }
}

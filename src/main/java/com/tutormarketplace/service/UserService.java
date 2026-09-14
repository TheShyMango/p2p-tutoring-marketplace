package com.tutormarketplace.service;

import com.tutormarketplace.dto.UserDTO;
import com.tutormarketplace.model.PointWallet;
import com.tutormarketplace.model.User;
import com.tutormarketplace.model.UserRole;
import com.tutormarketplace.repository.PointWalletRepository;
import com.tutormarketplace.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserRepository userRepository;
    private final PointWalletRepository walletRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Transactional
    public User registerUser(String username, String email, String password, String fullName) {
        // Check for existing user
        if (userRepository.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("Username already exists");
        }
        if (userRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("Email already exists");
        }
        
        User user = User.builder()
            .username(username)
            .email(email)
            .password(passwordEncoder.encode(password))
            .fullName(fullName)
            .role(UserRole.STUDENT)
            .isActive(true)
            .isVerified(false)
            .build();
        
        user = userRepository.save(user);
        
        // Create initial wallet for user
        PointWallet wallet = PointWallet.builder()
            .user(user)
            .availableBalance(0L)
            .lockedBalance(0L)
            .totalEarned(0L)
            .totalSpent(0L)
            .build();
        
        walletRepository.save(wallet);
        
        return user;
    }
    
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    
    public User getUserById(Long userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
    }
    
    @Transactional
    public User updateUserProfile(Long userId, String fullName, String bio, String profileImageUrl) {
        User user = getUserById(userId);
        user.setFullName(fullName);
        user.setBio(bio);
        user.setProfileImageUrl(profileImageUrl);
        return userRepository.save(user);
    }
    
    public UserDTO toDTO(User user) {
        return UserDTO.builder()
            .id(user.getId())
            .username(user.getUsername())
            .email(user.getEmail())
            .fullName(user.getFullName())
            .bio(user.getBio())
            .profileImageUrl(user.getProfileImageUrl())
            .role(user.getRole().toString())
            .isActive(user.getIsActive())
            .isVerified(user.getIsVerified())
            .createdAt(user.getCreatedAt())
            .updatedAt(user.getUpdatedAt())
            .build();
    }
}

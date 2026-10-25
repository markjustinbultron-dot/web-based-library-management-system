package com.library.service;

import com.library.model.Role;
import com.library.model.User;
import com.library.repository.UserRepository;
import com.library.repository.TransactionRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
            TransactionRepository transactionRepository,
            PasswordEncoder passwordEncoder) {
this.userRepository = userRepository;
this.transactionRepository = transactionRepository;
this.passwordEncoder = passwordEncoder;
}



public User registerUser(User user) {
    validatePassword(user.getPassword());

    user.setId(null);
    user.setRole(Role.STUDENT);
    user.setBanned(false);

    user.setPassword(passwordEncoder.encode(user.getPassword()));

    return userRepository.save(user);
}



public User createTeacher(User user) {
    validatePassword(user.getPassword());

    user.setId(null);
    user.setRole(Role.TEACHER);
    user.setBanned(false);


    if (userRepository.existsByEmail(user.getEmail())) {
        throw new IllegalArgumentException("Email is already registered.");
    }

    if (user.getIdNumber() != null
            && !user.getIdNumber().isBlank()
            && userRepository.existsByIdNumber(user.getIdNumber())) {
        throw new IllegalArgumentException("ID number is already registered.");
    }

    user.setPassword(passwordEncoder.encode(user.getPassword()));

    return userRepository.save(user);
}


private void validatePassword(String password) {
    if (password == null || password.length() < 8) {
        throw new IllegalArgumentException(
            "Password must be at least 8 characters long."
        );
    }

    if (!password.matches(".*[A-Z].*")) {
        throw new IllegalArgumentException(
            "Password must contain at least one uppercase letter."
        );
    }

    if (!password.matches(".*[a-z].*")) {
        throw new IllegalArgumentException(
            "Password must contain at least one lowercase letter."
        );
    }

    if (!password.matches(".*[0-9].*")) {
        throw new IllegalArgumentException(
            "Password must contain at least one number."
        );
    }
}



    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    

public List<User> getManageableUsers() {
    return userRepository.findAll().stream()
            .filter(user -> user.getRole() == Role.STUDENT
                    || user.getRole() == Role.TEACHER)
            .toList();
}



public void deleteUser(Long userId) {
    User user = userRepository.findById(userId)
            .orElseThrow(() ->
                    new IllegalArgumentException("Account not found."));

    if (user.getRole() != Role.STUDENT
            && user.getRole() != Role.TEACHER) {
        throw new IllegalArgumentException(
                "Admin accounts cannot be deleted here.");
    }

    if (transactionRepository.existsByUserId(userId)) {
        throw new IllegalArgumentException(
                "This account cannot be deleted because it has borrowing history.");
    }

    userRepository.delete(user);
}



    public User findByEmailOrIdNumber(String identifier) {
        return userRepository.findByEmail(identifier)
                .orElseGet(() -> userRepository.findByIdNumber(identifier)
                .orElse(null));
    }
}
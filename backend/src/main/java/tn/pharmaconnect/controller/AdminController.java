package tn.pharmaconnect.controller;

import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tn.pharmaconnect.dto.UserDTO;
import tn.pharmaconnect.dto.UserRequest;
import tn.pharmaconnect.service.UserService;
import tn.pharmaconnect.util.AuthContext;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final UserService userService;

    public AdminController(UserService userService) {
        this.userService = userService;
    }

    private void requireAdmin(String roleHeader) {
        AuthContext.requireAdmin(AuthContext.requireRole(roleHeader));
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserDTO>> getUsers(
            @RequestHeader(value = AuthContext.HEADER_USER_ROLE, required = false) String roleHeader) {
        requireAdmin(roleHeader);
        return ResponseEntity.ok(userService.findAllUsers());
    }

    @PostMapping("/users")
    public ResponseEntity<UserDTO> createUser(
            @RequestHeader(value = AuthContext.HEADER_USER_ROLE, required = false) String roleHeader,
            @Valid @RequestBody UserRequest request) {
        requireAdmin(roleHeader);
        return ResponseEntity.ok(userService.createUser(request));
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<UserDTO> updateUser(
            @RequestHeader(value = AuthContext.HEADER_USER_ROLE, required = false) String roleHeader,
            @PathVariable Integer id,
            @Valid @RequestBody UserRequest request) {
        requireAdmin(roleHeader);
        return ResponseEntity.ok(userService.updateUser(id, request));
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(
            @RequestHeader(value = AuthContext.HEADER_USER_ROLE, required = false) String roleHeader,
            @PathVariable Integer id) {
        requireAdmin(roleHeader);
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}

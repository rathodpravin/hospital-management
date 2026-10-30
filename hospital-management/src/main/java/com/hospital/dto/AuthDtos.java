package com.hospital.dto;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
public class AuthDtos {
    public record LoginRequest(@NotBlank String username, @NotBlank String password) {}
    public record RegisterRequest(
        @NotBlank @Size(min = 3, max = 30) String username,
        @NotBlank @Size(min = 6, message = "Password must be at least 6 characters") String password,
        @NotBlank String name,
        @Past LocalDate dateOfBirth,
        String gender,
        @Pattern(regexp = "^[0-9+\\-]{7,15}$", message = "Invalid phone number") String phone,
        String address, String bloodGroup) {}
    public record AuthResponse(String token, String role, String username) {}
}

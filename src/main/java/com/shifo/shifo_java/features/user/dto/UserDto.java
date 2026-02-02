package com.shifo.shifo_java.features.user.dto;

import lombok.*;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UserDto {

    private Long id;

    private String username;

    private String email;

    private String phone;

    private String firstName;

    private String lastName;

    private String fullName;

    private Long roleId;

    private Boolean isActive;

    private Instant createdAt;

    private Instant updatedAt;
}


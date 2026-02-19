package com.shifo.shifo_java.features.user;

import com.shifo.shifo_java.common.dto.PagedResponseDto;
import com.shifo.shifo_java.features.user.dto.FilterUserDto;
import com.shifo.shifo_java.features.user.dto.UpdateUserDto;
import com.shifo.shifo_java.features.user.dto.UserDto;
import com.shifo.shifo_java.features.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserDto update(Long id, UpdateUserDto dto) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "User not found"
                ));

        // -------------------------------------------------
        // 1️⃣ Validate Email Change (only if modified)
        // -------------------------------------------------
        if (dto.getEmail() != null && !dto.getEmail().equals(user.getEmail())) {

            boolean exists = userRepository.findByEmail(dto.getEmail()).isPresent();

            if (exists) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "auth.errors.emailAlreadyInUse"
                );
            }

            user.setEmail(dto.getEmail());
        }

        // -------------------------------------------------
        // 2️⃣ Hash Password if Provided
        // -------------------------------------------------
        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            String hashed = passwordEncoder.encode(dto.getPassword());
            user.setPassword(hashed);
        }

        // -------------------------------------------------
        // 3️⃣ Partial Update Other Fields (PATCH behavior)
        // -------------------------------------------------
        if (dto.getFirstName() != null) {
            user.setFirstName(dto.getFirstName());
        }

        if (dto.getLastName() != null) {
            user.setLastName(dto.getLastName());
        }

        if (dto.getPhone() != null) {
            user.setPhone(dto.getPhone());
        }

        if (dto.getIsActive() != null) {
            user.setIsActive(dto.getIsActive());
        }

        // No explicit save required, but OK to keep:
        User saved = userRepository.save(user);

        return userMapper.mapUserToDto(saved);
    }

    @Transactional
    public void remove(Long id) {

        int affected = userRepository.softDelete(id);

        if (affected == 0) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "users.errors.notFound"
            );
        }
    }

    public PagedResponseDto<UserDto> findAll(FilterUserDto filterDto) {

        PagedResponseDto<User> page = userRepository.findAllWithFilter(filterDto);

        List<UserDto> mapped = page.getItems()
                .stream()
                .map(userMapper::mapUserToDto)
                .toList();

        return PagedResponseDto.<UserDto>builder()
                .items(mapped)
                .page(page.getPage())
                .limit(page.getLimit())
                .total(page.getTotal())
                .totalPages(page.getTotalPages())
                .build();
    }

    public UserDto findOne(Long id) {
        User user = userRepository.findByIdWithRole(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "User not found with id = " + id
                        )
                );

        return userMapper.mapUserToDto(user);
    }
}

package com.shifo.shifo_java.features.user;

import com.shifo.shifo_java.common.dto.PagedResponseDto;
import com.shifo.shifo_java.features.user.dto.FilterUserDto;
import com.shifo.shifo_java.features.user.dto.UpdateUserDto;
import com.shifo.shifo_java.features.user.dto.UserDto;
import com.shifo.shifo_java.features.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import com.shifo.shifo_java.common.exceptions.NotFoundException;
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
        User user = getUserOrThrow(id);

        updateEmailIfChanged(dto, user);
        updatePasswordIfProvided(dto, user);
        userMapper.updateEntity(dto, user);

        User saved = userRepository.save(user);
        return userMapper.mapUserToDto(saved);
    }

    @Transactional
    public void remove(Long id) {

        int affected = userRepository.softDelete(id);

        if (affected == 0) {
            throw new NotFoundException("users.errors.notFound");
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
                        new NotFoundException("User not found with id = " + id));

        return userMapper.mapUserToDto(user);
    }

    private User getUserOrThrow(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found"));
    }

    private void updateEmailIfChanged(UpdateUserDto dto, User user) {
        if (dto.getEmail() == null || dto.getEmail().equals(user.getEmail())) {
            return;
        }

        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Email already in use"
            );
        }

        user.setEmail(dto.getEmail());
    }

    private void updatePasswordIfProvided(UpdateUserDto dto, User user) {
        if (dto.getPassword() == null || dto.getPassword().isBlank()) {
            return;
        }

        user.setPassword(passwordEncoder.encode(dto.getPassword()));
    }
}

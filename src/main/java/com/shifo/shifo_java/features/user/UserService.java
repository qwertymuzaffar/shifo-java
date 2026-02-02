package com.shifo.shifo_java.features.user;

import com.shifo.shifo_java.features.user.dto.CreateUserDto;
import com.shifo.shifo_java.features.user.dto.FilterUserDto;
import com.shifo.shifo_java.features.user.dto.UpdateUserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.*;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // -----------------------------------------
    // CREATE USER
    // -----------------------------------------
    public User create(CreateUserDto dto) {

        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new ResponseStatusException(BAD_REQUEST,
                    "Пользователь с таким email уже существует");
        }

        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new ResponseStatusException(BAD_REQUEST,
                    "Пользователь с таким username уже существует");
        }

        User user = new User();
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setRoleId(dto.getRoleId());
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setIsActive(true);
        user.setPassword(passwordEncoder.encode(dto.getPassword()));

        return userRepository.save(user);
    }

    // -----------------------------------------
    // FIND ALL WITH FILTERS
    // -----------------------------------------
    public Page<User> findAll(FilterUserDto filter) {

        int page = (filter.getPage() != null ? filter.getPage() - 1 : 0);
        int limit = (filter.getLimit() != null ? filter.getLimit() : 10);

        Pageable pageable = PageRequest.of(page, limit);

        Specification<User> spec = Specification.where(null);

        // search by username
        if (filter.getSearch() != null && !filter.getSearch().isBlank()) {
            spec = spec.and((root, query, cb) ->
                    cb.like(root.get("username"), "%" + filter.getSearch() + "%")
            );
        }

        // filter by email
        if (filter.getEmail() != null && !filter.getEmail().isBlank()) {
            spec = spec.and((root, query, cb) ->
                    cb.like(root.get("email"), "%" + filter.getEmail() + "%")
            );
        }

        // filter by active status
        if (filter.getIsActive() != null) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("isActive"), filter.getIsActive())
            );
        }

        // filter by role ID
        if (filter.getRoleId() != null) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("roleId"), filter.getRoleId())
            );
        }

        return userRepository.findAll(spec, pageable);
    }

    // -----------------------------------------
    // FIND BY ID
    // -----------------------------------------
    public User findOne(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        NOT_FOUND,
                        "Пользователь с ID " + id + " не найден"
                ));
    }

    // -----------------------------------------
    // FIND BY USERNAME
    // -----------------------------------------
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElse(null);
    }

    // -----------------------------------------
    // FIND BY EMAIL
    // -----------------------------------------
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElse(null);
    }

    // -----------------------------------------
    // UPDATE USER
    // -----------------------------------------
    public User update(Long id, UpdateUserDto dto) {

        User user = findOne(id);

        // email uniqueness check
        if (dto.getEmail() != null && !dto.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(dto.getEmail())) {
                throw new ResponseStatusException(BAD_REQUEST,
                        "Пользователь с таким email уже существует");
            }
        }

        // update password if provided
        if (dto.getPassword() != null) {
            dto.setPassword(passwordEncoder.encode(dto.getPassword()));
        }

        // Apply updates
        if (dto.getFirstName() != null) user.setFirstName(dto.getFirstName());
        if (dto.getLastName() != null) user.setLastName(dto.getLastName());
        if (dto.getEmail() != null) user.setEmail(dto.getEmail());
        if (dto.getPassword() != null) user.setPassword(dto.getPassword());
        if (dto.getIsActive() != null) user.setIsActive(dto.getIsActive());

        if (dto.getRole() != null) {
            user.setRole(dto.getRole()); // OR convert to roleId if needed
        }

        return userRepository.save(user);
    }

    // -----------------------------------------
    // SOFT DELETE USER
    // -----------------------------------------
    public void remove(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResponseStatusException(
                    NOT_FOUND,
                    "Пользователь с ID " + id + " не найден"
            );
        }

        userRepository.softDelete(id);
    }
}



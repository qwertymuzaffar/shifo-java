package com.shifo.shifo_java.features.user.repository;

import com.shifo.shifo_java.common.dto.PagedResponseDto;
import com.shifo.shifo_java.features.user.User;
import com.shifo.shifo_java.features.user.dto.FilterUserDto;

public interface UserRepositoryCustom {
    PagedResponseDto<User> findAllWithFilter(FilterUserDto filter);
}

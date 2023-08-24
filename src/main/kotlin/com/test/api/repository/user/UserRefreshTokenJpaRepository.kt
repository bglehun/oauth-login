package com.test.api.repository.user

import com.test.api.entity.UserRefreshToken
import org.springframework.data.jpa.repository.*

interface UserRefreshTokenJpaRepository : JpaRepository<UserRefreshToken, String>

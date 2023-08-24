package com.test.api.repository.user

import com.test.api.entity.User
import org.springframework.data.jpa.repository.*

interface UserJpaRepository : JpaRepository<User, String> {
    fun findBySnsId(snsId: String): User?
}

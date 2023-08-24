package com.test.api.entity

import com.fasterxml.jackson.annotation.*
import jakarta.persistence.*

// @Table(name = "USER_REFRESH_TOKEN")
@Entity
class UserRefreshToken(
    @JsonIgnore
    @Id
    val userId: String,
    var refreshToken: String? = null,
)

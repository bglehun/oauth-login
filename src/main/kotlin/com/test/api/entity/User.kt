package com.test.api.entity

import com.fasterxml.jackson.annotation.JsonInclude
import jakarta.persistence.*
import jakarta.persistence.Id
import org.hibernate.annotations.SQLDelete
import org.hibernate.annotations.Where
import org.springframework.data.annotation.*
import java.net.URL
import java.time.*
import java.util.*

@JsonInclude(JsonInclude.Include.NON_NULL)
@Entity
// 이 데코레이터에 의해 user에 대한 모든 select 쿼리에 해당 조건이 추가됨
@Where(clause = "deleted_at is null")
@SQLDelete(sql = "UPDATE user SET deleted_at = current_timestamp WHERE id = ?")
@Table(name = "user")
class User(
    @Id
    val id: String = UUID.randomUUID().toString(),

    @Column(nullable = false)
    val snsId: String,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    val snsType: OauthProvider,

    var phoneNumber: String? = null,

    // Unique 제약조건은 not-null만 가능, 중복 검사는 Redis에서 진행
    var nickname: String? = null,

    var birthday: Date? = null,

    var recommenderCode: String? = null,

    @Enumerated(EnumType.STRING)
    var gender: Gender? = null,

    var profileImageUrl: URL? = null,

    var job: String? = null,

    var area: String? = null,

    var height: Int? = null,

    var religion: String? = null,

    @CreatedDate
    @Column(nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    // 굳이 필요 없다고 판단되면 제거
    var activatedAt: LocalDateTime? = null,

    var deletedAt: LocalDateTime? = null,
) {
    companion object {
        enum class Gender {
            M,
            F,
        }
    }
}

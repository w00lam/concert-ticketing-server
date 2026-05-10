package kr.hhplus.be.server.user.domain.model;

import jakarta.persistence.*;
import kr.hhplus.be.server.common.domain.AmountValidator;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@EntityListeners(AuditingEntityListener.class)
@Table(
        name = "users",
        uniqueConstraints = @UniqueConstraint(name = "uk_user_email", columnNames = "email"),
        indexes = {
                @Index(name = "idx_deleted_email", columnList = "deleted, email")
        }
)
public class User {
    @Id
    @GeneratedValue
    private UUID id;

    @Version
    private Integer version;

    @Column(nullable = false, length = 255, unique = true)
    private String email;

    @Column(nullable = false, length = 100)
    private String name;

    @Builder.Default
    @Column(nullable = false)
    private int points = 0;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Builder.Default
    @Column(nullable = false)
    private boolean deleted = false;

    public static User create(String email, String name) {
        return User.builder()
                .email(email)
                .name(name)
                .points(0)
                .deleted(false)
                .build();
    }

    public void addPoints(int amount) {
        AmountValidator.requireNonNegative(amount);
        this.points += amount;
    }

    public void deductPoints(int amount) {
        AmountValidator.requireNonNegative(amount);
        if (this.points < amount) throw UserExceptions.insufficientPoints();
        this.points -= amount;
    }
}

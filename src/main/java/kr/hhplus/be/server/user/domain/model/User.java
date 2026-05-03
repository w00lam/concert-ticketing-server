package kr.hhplus.be.server.user.domain.model;

import jakarta.persistence.*;
import kr.hhplus.be.server.common.exception.BusinessRuleViolationException;
import kr.hhplus.be.server.common.exception.ClientInputException;
import kr.hhplus.be.server.common.exception.ErrorCode;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
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

    public void addPoints(int amount) {
        validateNonNegativeAmount(amount);
        this.points += amount;
    }

    public void deductPoints(int amount) {
        validateNonNegativeAmount(amount);
        if (this.points < amount) throw new BusinessRuleViolationException(ErrorCode.INSUFFICIENT_POINTS, "포인트가 부족합니다.");
        this.points -= amount;
    }

    private void validateNonNegativeAmount(int amount) {
        // Point balance adjustments can be zero, but negative changes must use explicit charge/deduct flows.
        if (amount < 0) throw new ClientInputException(ErrorCode.AMOUNT_MUST_BE_NON_NEGATIVE, "금액은 음수일 수 없습니다.");
    }
}

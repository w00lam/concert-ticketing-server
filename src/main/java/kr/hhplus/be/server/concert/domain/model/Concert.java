package kr.hhplus.be.server.concert.domain.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
/**
 * Represents core state and rules in the concert domain.
 */

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "concerts")
@EntityListeners(AuditingEntityListener.class)
public class Concert {
    @Id
    @GeneratedValue
    @Column(name = "id", columnDefinition = "BINARY(16)")
    private UUID id;

    @Column(name = "title", length = 255, nullable = false)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @CreatedDate
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Builder.Default
    @OneToMany(
            mappedBy = "concert",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private List<ConcertDate> concertDates = new ArrayList<>();

    // 연관 엔티티 추가 헬퍼 메서드
    public void addDate(ConcertDate concertDate) {
        concertDates.add(concertDate);
        concertDate.setConcert(this);
    }

    public void removeDate(ConcertDate concertDate) {
        concertDates.remove(concertDate);
        concertDate.setConcert(null);
    }
}

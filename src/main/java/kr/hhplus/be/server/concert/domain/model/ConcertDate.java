package kr.hhplus.be.server.concert.domain.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Table(name = "concert_dates", uniqueConstraints = {@UniqueConstraint(name = "uk_concert_event_date", columnNames = {"concert_id", "event_date"})})
public class ConcertDate {
    @Id
    @GeneratedValue
    @Column(name = "id", columnDefinition = "BINARY(16)")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "concert_id", nullable = false)
    private Concert concert;

    @Column(name = "event_date", nullable = false)
    private LocalDate eventDate;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Builder.Default
    @Column(name = "deleted", nullable = false)
    private boolean deleted = false;

    public static ConcertDate create(Concert concert, LocalDate eventDate) {
        return ConcertDate.builder()
                .concert(concert)
                .eventDate(eventDate)
                .deleted(false)
                .build();
    }

    void setConcert(Concert concert) {
        this.concert = concert;
    }
}

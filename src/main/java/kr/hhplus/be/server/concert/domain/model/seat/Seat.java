package kr.hhplus.be.server.concert.domain.model.seat;

import jakarta.persistence.*;
import kr.hhplus.be.server.concert.domain.model.ConcertDate;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Table(
        name = "seats",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_concert_date_section_seat_row_number",
                columnNames = {"concert_date_id", "section", "seat_row", "number"}
        ),
        indexes = {@Index(name = "idx_concert_date_section_seat_row",
                columnList = "concert_date_id, section, seat_row")}
)
public class Seat {
    @Id
    @GeneratedValue
    @Column(name = "id", columnDefinition = "BINARY(16)")
    private UUID id;

    // 양방향 관계 helper
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "concert_date_id", nullable = false)
    private ConcertDate concertDate;

    @Column(name = "section", length = 20, nullable = false)
    private String section;

    @Column(name = "seat_row", length = 5, nullable = false)
    private String row;

    @Column(name = "number", length = 5, nullable = false)
    private String number;

    @Column(name = "grade", length = 20, nullable = false)
    private String grade;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Builder.Default
    @Column(name = "deleted", nullable = false)
    private boolean deleted = false;

    @Builder.Default
    @Column(name = "held", nullable = false)
    private boolean held = false;

    @Column(name = "hold_until")
    private LocalDateTime holdUntil;

}

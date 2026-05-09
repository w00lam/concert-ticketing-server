package kr.hhplus.be.server.reservation.infrastructure.event.adapter;

import kr.hhplus.be.server.reservation.application.port.out.DataPlatformClientPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
public class MockDataPlatformClient implements DataPlatformClientPort {
    @Override
    public void sendReservationConfirmed(UUID reservationId, UUID concertId) {
        log.info("[MOCK DATA PLATFORM] reservationId={}, concertId={}", reservationId, concertId);
    }

    @Override
    public void sendReservationCanceled(UUID reservationId, UUID concertId) {
        log.info("[MOCK DATA PLATFORM] CANCELED reservationId={}, concertId={}", reservationId, concertId);
    }
}

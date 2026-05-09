package kr.hhplus.be.server.unit.application.reservation.service;

import kr.hhplus.be.server.reservation.application.port.in.ConfirmReservationCommand;
import kr.hhplus.be.server.reservation.application.service.ConfirmReservationUseCaseImpl;
import kr.hhplus.be.server.reservation.application.service.ReservationConfirmationService;
import kr.hhplus.be.server.reservation.domain.model.Reservation;
import kr.hhplus.be.server.reservation.domain.model.ReservationStatus;
import kr.hhplus.be.server.unit.BaseUnitTest;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

public class ConfirmReservationUseCaseImplTest extends BaseUnitTest {
    @Mock
    ReservationConfirmationService reservationConfirmationService;

    @InjectMocks
    ConfirmReservationUseCaseImpl useCase;

    @Test
    void execute_returnsConfirmedReservationResult() {
        Reservation reservation = Reservation.builder()
                .id(fixedUUID())
                .status(ReservationStatus.CONFIRMED)
                .confirmedAt(fixedNow())
                .build();

        when(reservationConfirmationService.confirm(fixedUUID())).thenReturn(reservation);

        var result = useCase.execute(new ConfirmReservationCommand(fixedUUID()));

        assertThat(result.reservationId()).isEqualTo(fixedUUID());
        assertThat(result.status()).isEqualTo(ReservationStatus.CONFIRMED.name());
        assertThat(result.confirmedAt()).isEqualTo(fixedNow());
    }
}

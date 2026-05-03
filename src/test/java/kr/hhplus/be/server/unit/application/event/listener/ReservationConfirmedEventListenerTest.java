package kr.hhplus.be.server.unit.application.event.listener;

import kr.hhplus.be.server.application.event.listener.ReservationConfirmedEventListener;
import kr.hhplus.be.server.application.event.port.out.ReservationEventProducerPort;
import kr.hhplus.be.server.reservation.application.event.ReservationConfirmedEvent;
import kr.hhplus.be.server.unit.BaseUnitTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class ReservationConfirmedEventListenerTest extends BaseUnitTest {
    @Mock
    private ReservationEventProducerPort producerPort;

    @InjectMocks
    private ReservationConfirmedEventListener listener;

    @Test
    @DisplayName("고정된 UUID를 사용하여 이벤트가 발생했을 때 프로듀서가 정상 호출되는지 확인한다")
    void handle_ShouldCallProducerWithFixedUuids() {
        // given: BaseUnitTest의 fixedUUID 메서드 활용
        ReservationConfirmedEvent event = new ReservationConfirmedEvent(fixedUUID(), fixedUUID2());

        // when
        listener.handle(event);

        // then
        verify(producerPort, times(1)).sendConfirmedEvent(event);
    }
}

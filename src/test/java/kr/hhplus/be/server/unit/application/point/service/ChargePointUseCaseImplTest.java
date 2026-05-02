package kr.hhplus.be.server.unit.application.point.service;

import kr.hhplus.be.server.point.application.port.in.ChargePointCommand;
import kr.hhplus.be.server.point.application.port.in.ChargePointResult;
import kr.hhplus.be.server.point.application.port.out.PointRepositoryPort;
import kr.hhplus.be.server.point.application.service.ChargePointUseCaseImpl;
import kr.hhplus.be.server.user.application.port.out.UserRepositoryPort;
import kr.hhplus.be.server.point.domain.model.Point;
import kr.hhplus.be.server.point.domain.service.PointDomainService;
import kr.hhplus.be.server.user.domain.model.User;
import kr.hhplus.be.server.unit.BaseUnitTest;
import org.junit.jupiter.api.*;
import org.mockito.*;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ChargePointUseCaseImplTest extends BaseUnitTest {
    @Mock
    UserRepositoryPort userRepositoryPort;

    @Mock
    PointRepositoryPort pointRepositoryPort;

    @Mock
    PointDomainService pointDomainService;

    @InjectMocks
    ChargePointUseCaseImpl useCase;

    @Test
    @DisplayName("포인트 충전 요청 시 유저 조회 → 충전 Point 생성 → 저장 후 결과 반환")
    void execute_success() {

        // given
        UUID userId = fixedUUID();
        int amount = 5000;

        ChargePointCommand command = new ChargePointCommand(userId, amount);

        User user = User.builder()
                .id(userId)
                .email("test@example.com")
                .name("Tester")
                .points(0)
                .build();

        Point createdPoint = Point.builder()
                .id(fixedUUID())
                .amount(amount)
                .user(user)
                .build();

        Point savedPoint = Point.builder()
                .id(fixedUUID())
                .amount(amount)
                .user(user)
                .build();

        when(userRepositoryPort.findById(userId)).thenReturn(user);
        when(pointDomainService.createCharge(user, amount)).thenReturn(createdPoint);
        when(pointRepositoryPort.save(createdPoint)).thenReturn(savedPoint);

        // when
        ChargePointResult result = useCase.execute(command);

        // then
        assertEquals(savedPoint.getUser().getId(), result.userId());

        verify(userRepositoryPort).findById(userId);
        verify(pointDomainService).createCharge(user, amount);
        verify(pointRepositoryPort).save(createdPoint);
    }
}

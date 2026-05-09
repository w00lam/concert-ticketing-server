package kr.hhplus.be.server.unit.application.point.service;

import kr.hhplus.be.server.point.application.port.in.ChargePointCommand;
import kr.hhplus.be.server.point.application.port.in.ChargePointResult;
import kr.hhplus.be.server.point.application.port.out.PointRepositoryPort;
import kr.hhplus.be.server.point.application.service.ChargePointUseCaseImpl;
import kr.hhplus.be.server.point.domain.model.Point;
import kr.hhplus.be.server.point.domain.service.PointDomainService;
import kr.hhplus.be.server.unit.BaseUnitTest;
import kr.hhplus.be.server.user.application.port.out.UserRepositoryPort;
import kr.hhplus.be.server.user.domain.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

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
    @DisplayName("포인트 충전 요청 시 유저 조회 후 충전 처리 결과를 반환한다")
    void execute_success() {
        UUID userId = fixedUUID();
        int amount = 5000;
        ChargePointCommand command = new ChargePointCommand(userId, amount);
        User user = User.builder()
                .id(userId)
                .email("test@example.com")
                .name("Tester")
                .points(1000)
                .build();
        Point createdPoint = Point.createCharge(user, amount);

        when(userRepositoryPort.findById(userId)).thenReturn(user);
        when(pointDomainService.charge(user, amount)).thenAnswer(invocation -> {
            user.addPoints(amount);
            return createdPoint;
        });

        ChargePointResult result = useCase.execute(command);

        assertEquals(user.getId(), result.userId());
        assertEquals(6000, result.balance());
        assertEquals(6000, user.getPoints());
        verify(userRepositoryPort).findById(userId);
        verify(pointDomainService).charge(user, amount);
        verify(pointRepositoryPort).save(createdPoint);
    }
}

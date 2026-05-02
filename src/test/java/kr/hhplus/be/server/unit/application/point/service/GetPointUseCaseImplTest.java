package kr.hhplus.be.server.unit.application.point.service;

import kr.hhplus.be.server.point.application.port.in.GetPointQuery;
import kr.hhplus.be.server.point.application.port.in.GetPointResult;
import kr.hhplus.be.server.point.application.port.out.PointRepositoryPort;
import kr.hhplus.be.server.point.application.service.GetPointUseCaseImpl;
import kr.hhplus.be.server.point.domain.model.Point;
import kr.hhplus.be.server.point.domain.service.PointDomainService;
import kr.hhplus.be.server.unit.BaseUnitTest;
import org.junit.jupiter.api.*;
import org.mockito.*;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class GetPointUseCaseImplTest extends BaseUnitTest {
    @Mock
    PointRepositoryPort pointRepositoryPort;

    @Mock
    PointDomainService pointDomainService;

    @InjectMocks
    GetPointUseCaseImpl useCase;

    @Test
    @DisplayName("유저 포인트 조회 시 전체 포인트 목록을 바탕으로 잔액이 계산된다")
    void execute_success() {
        // given
        UUID userId = fixedUUID();
        GetPointQuery query = new GetPointQuery(userId);

        Point tx1 = Point.builder()
                .id(fixedUUID())
                .amount(1000)
                .deleted(false)
                .build();

        Point tx2 = Point.builder()
                .id(fixedUUID2())
                .amount(500)
                .deleted(false)
                .build();

        List<Point> points = List.of(tx1, tx2);

        when(pointRepositoryPort.findAllByUserId(userId))
                .thenReturn(points);

        when(pointDomainService.calculateBalance(points))
                .thenReturn(1500);

        // when
        GetPointResult result = useCase.execute(query);

        // then
        assertEquals(userId, result.userId());
        assertEquals(1500, result.balance());

        verify(pointRepositoryPort).findAllByUserId(userId);
        verify(pointDomainService).calculateBalance(points);
    }
}

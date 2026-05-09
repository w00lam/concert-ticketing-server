package kr.hhplus.be.server.unit.application.point.service;

import kr.hhplus.be.server.point.application.port.in.GetPointQuery;
import kr.hhplus.be.server.point.application.service.GetPointUseCaseImpl;
import kr.hhplus.be.server.user.application.port.out.UserRepositoryPort;
import kr.hhplus.be.server.user.domain.model.User;
import kr.hhplus.be.server.unit.BaseUnitTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class GetPointUseCaseImplTest extends BaseUnitTest {
    @Mock
    UserRepositoryPort userRepositoryPort;

    @InjectMocks
    GetPointUseCaseImpl useCase;

    @Test
    @DisplayName("User points are returned as current balance")
    void execute_success() {
        UUID userId = fixedUUID();
        GetPointQuery query = new GetPointQuery(userId);
        User user = User.builder()
                .id(userId)
                .points(1500)
                .build();

        when(userRepositoryPort.findById(userId)).thenReturn(user);

        var result = useCase.execute(query);

        assertEquals(userId, result.userId());
        assertEquals(1500, result.balance());

        verify(userRepositoryPort).findById(userId);
    }
}

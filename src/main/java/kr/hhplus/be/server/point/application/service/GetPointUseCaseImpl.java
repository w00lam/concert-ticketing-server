package kr.hhplus.be.server.point.application.service;

import kr.hhplus.be.server.point.application.port.in.GetPointQuery;
import kr.hhplus.be.server.point.application.port.in.GetPointResult;
import kr.hhplus.be.server.point.application.port.in.GetPointUseCase;
import kr.hhplus.be.server.user.application.port.out.UserRepositoryPort;
import kr.hhplus.be.server.user.domain.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;
/**
 * Implements the point use case and coordinates transactional work.
 */

@Service
@RequiredArgsConstructor
public class GetPointUseCaseImpl implements GetPointUseCase {
    private final UserRepositoryPort userRepositoryPort;

    @Override
    public GetPointResult execute(GetPointQuery query) {
        UUID userId = query.userId();
        User user = userRepositoryPort.findById(userId);

        return new GetPointResult(userId, user.getPoints());
    }
}

package kr.hhplus.be.server.point.application.service;

import kr.hhplus.be.server.point.application.port.in.ChargePointCommand;
import kr.hhplus.be.server.point.application.port.in.ChargePointResult;
import kr.hhplus.be.server.point.application.port.in.ChargePointUseCase;
import kr.hhplus.be.server.point.application.port.out.PointRepositoryPort;
import kr.hhplus.be.server.user.application.port.out.UserRepositoryPort;
import kr.hhplus.be.server.point.domain.model.Point;
import kr.hhplus.be.server.point.domain.service.PointDomainService;
import kr.hhplus.be.server.user.domain.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
/**
 * Implements the point use case and coordinates transactional work.
 */

@Service
@RequiredArgsConstructor
public class ChargePointUseCaseImpl implements ChargePointUseCase {
    private final UserRepositoryPort userRepositoryPort;
    private final PointRepositoryPort pointRepositoryPort;
    private final PointDomainService pointDomainService;

    @Override
    public ChargePointResult execute(ChargePointCommand command) {
        User user = userRepositoryPort.findById(command.userId());
        Point point = pointDomainService.createCharge(user, command.amount());
        Point saved = pointRepositoryPort.save(point);

        return new ChargePointResult(saved.getId(), saved.getAmount());
    }
}

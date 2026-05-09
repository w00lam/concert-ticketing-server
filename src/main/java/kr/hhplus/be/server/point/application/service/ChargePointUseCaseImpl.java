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
import org.springframework.transaction.annotation.Transactional;
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
    @Transactional
    public ChargePointResult execute(ChargePointCommand command) {
        User user = userRepositoryPort.findById(command.userId());
        Point point = pointDomainService.charge(user, command.amount());
        pointRepositoryPort.save(point);

        return new ChargePointResult(user.getId(), user.getPoints());
    }
}

package kr.hhplus.be.server.point.application.service;

import kr.hhplus.be.server.point.application.port.in.DeductPointCommand;
import kr.hhplus.be.server.point.application.port.in.DeductPointResult;
import kr.hhplus.be.server.point.application.port.in.DeductPointUseCase;
import kr.hhplus.be.server.user.application.port.out.UserRepositoryPort;
import kr.hhplus.be.server.user.domain.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
/**
 * Implements the point use case and coordinates transactional work.
 */

@Service
@RequiredArgsConstructor
public class DeductPointUseCaseImpl implements DeductPointUseCase {
    private final UserRepositoryPort userRepositoryPort;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public DeductPointResult execute(DeductPointCommand command) {
        User user = userRepositoryPort.findById(command.userId());

        user.deductPoints(command.amount());

        return new DeductPointResult(
                user.getId(),
                command.amount(),
                user.getPoints()
        );
    }
}

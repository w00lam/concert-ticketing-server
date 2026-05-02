package kr.hhplus.be.server.point.application.service;

import jakarta.transaction.Transactional;
import kr.hhplus.be.server.point.application.port.in.DeductPointCommand;
import kr.hhplus.be.server.point.application.port.in.DeductPointResult;
import kr.hhplus.be.server.point.application.port.in.DeductPointUseCase;
import kr.hhplus.be.server.application.user.port.out.UserRepositoryPort;
import kr.hhplus.be.server.domain.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeductPointUseCaseImpl implements DeductPointUseCase {
    private final UserRepositoryPort userRepositoryPort;

    @Override
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public DeductPointResult execute(DeductPointCommand command){
        User user=userRepositoryPort.findById(command.userId());

        user.deductPoints(command.amount());

        return new DeductPointResult(
                user.getId(),
                command.amount(),
                user.getPoints()
        );
    }
}

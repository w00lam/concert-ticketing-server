package kr.hhplus.be.server.point.application.service;

import kr.hhplus.be.server.point.application.port.in.GetPointQuery;
import kr.hhplus.be.server.point.application.port.in.GetPointResult;
import kr.hhplus.be.server.point.application.port.in.GetPointUseCase;
import kr.hhplus.be.server.point.application.port.out.PointRepositoryPort;
import kr.hhplus.be.server.point.domain.model.Point;
import kr.hhplus.be.server.point.domain.service.PointDomainService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GetPointUseCaseImpl implements GetPointUseCase {
    private final PointRepositoryPort pointRepositoryPort;
    private final PointDomainService pointDomainService;

    @Override
    public GetPointResult execute(GetPointQuery query) {
        UUID userId = query.userId();
        List<Point> points = pointRepositoryPort.findAllByUserId(userId);
        int balance = pointDomainService.calculateBalance(points);

        return new GetPointResult(userId, balance);
    }
}

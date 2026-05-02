package kr.hhplus.be.server.point.presentation.controller;

import jakarta.validation.Valid;
import kr.hhplus.be.server.common.presentation.ApiResponse;
import kr.hhplus.be.server.point.application.port.in.ChargePointCommand;
import kr.hhplus.be.server.point.application.port.in.ChargePointUseCase;
import kr.hhplus.be.server.point.application.port.in.GetPointQuery;
import kr.hhplus.be.server.point.application.port.in.GetPointUseCase;
import kr.hhplus.be.server.point.presentation.dto.ChargePointRequest;
import kr.hhplus.be.server.point.presentation.dto.ChargePointResponse;
import kr.hhplus.be.server.point.presentation.dto.GetPointResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/points")
public class PointController {
    private final ChargePointUseCase chargePointUseCase;
    private final GetPointUseCase getPointUseCase;

    @PostMapping("/charge")
    public ResponseEntity<ApiResponse<ChargePointResponse>> chargePoint(@Valid @RequestBody ChargePointRequest request) {
        var result = chargePointUseCase.execute(new ChargePointCommand(request.userId(), request.amount()));
        var response = ChargePointResponse.from(result);

        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping("/{userId}/balance")
    public ResponseEntity<ApiResponse<GetPointResponse>> getPoint(@PathVariable UUID userId) {
        var result = getPointUseCase.execute(new GetPointQuery(userId));
        var response = GetPointResponse.from(result);

        return ResponseEntity.ok(ApiResponse.ok(response));
    }
}

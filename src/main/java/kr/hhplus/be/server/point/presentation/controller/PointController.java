package kr.hhplus.be.server.point.presentation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Point", description = "포인트 충전 및 잔액 조회 API")
public class PointController {
    private final ChargePointUseCase chargePointUseCase;
    private final GetPointUseCase getPointUseCase;

    @PostMapping("/charge")
    @Operation(summary = "포인트 충전", description = "사용자에게 포인트를 충전합니다.")
    public ResponseEntity<ApiResponse<ChargePointResponse>> chargePoint(@Valid @RequestBody ChargePointRequest request) {
        var result = chargePointUseCase.execute(new ChargePointCommand(request.userId(), request.amount()));
        var response = ChargePointResponse.from(result);

        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping("/{userId}/balance")
    @Operation(summary = "포인트 잔액 조회", description = "사용자의 현재 포인트 잔액을 조회합니다.")
    public ResponseEntity<ApiResponse<GetPointResponse>> getPoint(
            @Parameter(description = "사용자 ID", required = true) @PathVariable UUID userId
    ) {
        var result = getPointUseCase.execute(new GetPointQuery(userId));
        var response = GetPointResponse.from(result);

        return ResponseEntity.ok(ApiResponse.ok(response));
    }
}

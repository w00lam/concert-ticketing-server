package kr.hhplus.be.server.reservation.presentation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import kr.hhplus.be.server.common.presentation.ApiResponse;
import kr.hhplus.be.server.reservation.application.port.in.ConfirmReservationCommand;
import kr.hhplus.be.server.reservation.application.port.in.ConfirmReservationUseCase;
import kr.hhplus.be.server.reservation.application.port.in.MakeReservationCommand;
import kr.hhplus.be.server.reservation.application.port.in.MakeReservationUseCase;
import kr.hhplus.be.server.reservation.presentation.dto.ConfirmReservationResponse;
import kr.hhplus.be.server.reservation.presentation.dto.MakeReservationRequest;
import kr.hhplus.be.server.reservation.presentation.dto.MakeReservationResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/reservations")
@AllArgsConstructor
@Tag(name = "Reservation", description = "좌석 예약 및 예약 확정 API")
public class ReservationController {
    private final MakeReservationUseCase makeReservationUseCase;
    private final ConfirmReservationUseCase confirmReservationUseCase;

    @PostMapping
    @Operation(summary = "좌석 예약", description = "사용자, 콘서트, 좌석 정보를 기반으로 임시 예약을 생성합니다.")
    public ResponseEntity<ApiResponse<MakeReservationResponse>> makeReservation(@Valid @RequestBody MakeReservationRequest request) {
        var result = makeReservationUseCase.execute(new MakeReservationCommand(request.userId(), request.concertId(), request.seatId()));
        var response = MakeReservationResponse.from(result);

        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PostMapping("/{reservationId}/confirm")
    @Operation(summary = "예약 확정", description = "임시 예약을 확정 상태로 전환합니다.")
    public ResponseEntity<ApiResponse<ConfirmReservationResponse>> confirmReservation(
            @Parameter(description = "예약 ID", required = true) @PathVariable UUID reservationId
    ) {
        var result = confirmReservationUseCase.execute(new ConfirmReservationCommand(reservationId));
        var response = ConfirmReservationResponse.from(result);

        return ResponseEntity.ok(ApiResponse.ok(response));
    }
}

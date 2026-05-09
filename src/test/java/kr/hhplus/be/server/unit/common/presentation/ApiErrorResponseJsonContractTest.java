package kr.hhplus.be.server.unit.common.presentation;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import kr.hhplus.be.server.common.exception.ClientInputException;
import kr.hhplus.be.server.common.exception.ErrorCode;
import kr.hhplus.be.server.common.presentation.GlobalExceptionHandler;
import kr.hhplus.be.server.unit.BaseUnitTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

public class ApiErrorResponseJsonContractTest extends BaseUnitTest {
    private MockMvc mockMvc;
    private LocalValidatorFactoryBean validator;

    @BeforeEach
    void setUpMockMvc() {
        validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        mockMvc = standaloneSetup(new ErrorContractController())
                .setControllerAdvice(new GlobalExceptionHandler())
                .setValidator(validator)
                .build();
    }

    @Test
    @DisplayName("business error response exposes empty errors array")
    void businessErrorResponse_exposesEmptyErrorsArray() throws Exception {
        mockMvc.perform(post("/contract/business-error"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("요청 본문은 필수입니다."))
                .andExpect(jsonPath("$.code").value("REQUEST_BODY_REQUIRED"))
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors").isEmpty());
    }

    @Test
    @DisplayName("validation error response exposes field errors array")
    void validationErrorResponse_exposesFieldErrorsArray() throws Exception {
        mockMvc.perform(post("/contract/validation-error")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("요청 필드 값이 올바르지 않습니다."))
                .andExpect(jsonPath("$.code").value("INVALID_REQUEST_FIELD"))
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors[0].field").value("name"))
                .andExpect(jsonPath("$.errors[0].message").value("must not be blank"));
    }

    @RestController
    private static class ErrorContractController {
        @PostMapping("/contract/business-error")
        void businessError() {
            throw new ClientInputException(ErrorCode.REQUEST_BODY_REQUIRED, "요청 본문은 필수입니다.");
        }

        @PostMapping("/contract/validation-error")
        void validationError(@Valid @RequestBody ValidationRequest request) {
        }
    }

    private record ValidationRequest(@NotBlank String name) {
    }
}

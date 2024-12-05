package ru.skillbox;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.springframework.http.HttpStatus;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

public class CaptchaMocks {

    public static void setupMockCaptchaResponse(WireMockServer mockService) {
        mockService.stubFor(WireMock.post(WireMock.urlEqualTo("/api/v1/storage"))
                .withMultipartRequestBody(aMultipart())
                .willReturn(WireMock.aResponse()
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"filename\":\"filename\"}")));
    }
}

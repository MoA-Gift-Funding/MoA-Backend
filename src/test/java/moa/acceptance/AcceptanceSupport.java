package moa.acceptance;

import static io.restassured.http.ContentType.JSON;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

public class AcceptanceSupport {

    public static RequestSpecification given() {
        return RestAssured
                .given()
                .contentType(JSON);
    }

    public static RequestSpecification given(String accessToken) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setBearerAuth(accessToken);
        return RestAssured
                .given()
                .headers(httpHeaders)
                .contentType(JSON);
    }

    public static void assertStatus(
            ExtractableResponse<Response> response,
            HttpStatus expected
    ) {
        assertThat(response.statusCode()).isEqualTo(expected.value());
    }
}

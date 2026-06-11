package br.com.sebodigital.api.exception;

import java.time.Instant;
import java.util.Map;

public record ErroResponse(
        Instant timestamp,
        int status,
        String erro,
        Map<String, String> campos) {

    public static ErroResponse of(int status, String erro) {
        return new ErroResponse(Instant.now(), status, erro, Map.of());
    }

    public static ErroResponse of(int status, String erro, Map<String, String> campos) {
        return new ErroResponse(Instant.now(), status, erro, campos);
    }
}

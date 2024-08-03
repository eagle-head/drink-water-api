package br.com.drinkwater.drinkwaterapi.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Builder;
import lombok.Getter;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;

@JsonInclude(Include.NON_NULL)
@Getter
@Builder
public class ProblemDetailResponse {

    private int status;
    private URI type;
    private String title;
    private String detail;
    private OffsetDateTime timestamp;
    private String userMessage;
    private List<Constraint> constraints;

    @Getter
    @Builder
    public static class Constraint {
        private String name;
        private String userMessage;
    }
}

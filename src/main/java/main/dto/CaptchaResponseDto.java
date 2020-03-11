package main.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.Set;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CaptchaResponseDto {
    private boolean succes;
    @JsonAlias("error-codes")
    private Set<String> errorCodes;
}

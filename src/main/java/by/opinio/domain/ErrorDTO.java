package by.opinio.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@AllArgsConstructor
@Builder
@Data
public class ErrorDTO {

    private List data;
    private boolean status;
    private String message;
}
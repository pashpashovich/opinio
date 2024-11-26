package by.opinio.API;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
@Getter
@ToString
public class ApiResponse<T> {

    private T data;
    private boolean status;
    private String message;

}
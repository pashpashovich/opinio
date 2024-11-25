package by.opinio.domain;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class CategoryDto {
    private UUID id;
    private String name;
}

package by.opinio.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PopularOrganizationDto {
    private UUID id;
    private String name;
    private Long subscriberCount; // Количество подписчиков
}
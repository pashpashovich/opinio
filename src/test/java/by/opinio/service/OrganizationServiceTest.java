package by.opinio.service;

import by.opinio.domain.OrganizationDto;
import by.opinio.entity.Bonus;
import by.opinio.entity.Organization;
import by.opinio.entity.Poll;
import by.opinio.entity.User;
import by.opinio.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
class OrganizationServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private OrganizationService organizationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getOrganizationInfo_ReturnsDto_WhenOrganizationExists() {
        // Given
        UUID organizationId = UUID.randomUUID();
        Organization organization = Organization.builder()
                .id(organizationId)
                .name("TechCorp")
                .description("Innovative solutions")
                .bonuses(List.of(
                        Bonus.builder()
                                .id(UUID.randomUUID())
                                .name("10% Discount")
                                .description("Discount on products")
                                .createdAt(LocalDateTime.now())
                                .updatedAt(LocalDateTime.now())
                                .build()
                ))
                .polls(List.of(
                        Poll.builder()
                                .id(UUID.randomUUID())
                                .title("Customer Feedback")
                                .description("Survey description")
                                .createdAt(LocalDateTime.now())
                                .updatedAt(LocalDateTime.now())
                                .build()
                ))
                .build();

        when(userRepository.findById(organizationId)).thenReturn(Optional.of(organization));

        // When
        OrganizationDto result = organizationService.getOrganizationInfo(organizationId);

        // Then
        assertNotNull(result);
        assertEquals(organizationId, result.getId());
        assertEquals("TechCorp", result.getName());
        assertEquals(1, result.getBonuses().size());
        assertEquals(1, result.getPolls().size());
    }

    @Test
    void getOrganizationInfo_ThrowsException_WhenOrganizationNotFound() {
        // Given
        UUID organizationId = UUID.randomUUID();

        when(userRepository.findById(organizationId)).thenReturn(Optional.empty());

        // When
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> organizationService.getOrganizationInfo(organizationId));
        // Then
        assertEquals("Organization not found", exception.getMessage());
    }

    @Test
    void getOrganizationInfo_ThrowsException_WhenNotAnOrganization() {
        // Given
        UUID userId = UUID.randomUUID();
        User user = User.builder()
                .id(userId)
                .username("user1")
                .password("pass123")
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // When
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> organizationService.getOrganizationInfo(userId));
        //Then
        assertEquals("The provided ID does not belong to an organization", exception.getMessage());
    }
}

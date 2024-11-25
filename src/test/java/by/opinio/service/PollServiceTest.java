package by.opinio.service;

import by.opinio.domain.PollDto;
import by.opinio.entity.Category;
import by.opinio.entity.Organization;
import by.opinio.entity.Poll;
import by.opinio.repository.CategoryRepository;
import by.opinio.repository.PollRepository;
import by.opinio.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PollServiceTest {

    @Mock
    private PollRepository pollRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private PollService pollService;

    @Test
    void shouldCreatePollSuccessfully() {
        UUID organizationId = UUID.randomUUID();
        UUID categoryId = UUID.randomUUID();

        Organization organization = Organization.builder()
                .id(organizationId)
                .name("Test Organization")
                .build();

        Category category = Category.builder()
                .id(categoryId)
                .name("Test Category")
                .build();

        PollDto pollDto = PollDto.builder()
                .title("Test Poll")
                .description("Test Description")
                .category(category)
                .createdBy(organization)
                .build();

        when(userRepository.findById(organizationId)).thenReturn(Optional.of(organization));
        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));

        pollService.createPoll(pollDto);

        verify(pollRepository, times(1)).save(any(Poll.class));
    }

    @Test
    void shouldGetAllPollsSuccessfully() {
        UUID pollId = UUID.randomUUID();
        UUID categoryId = UUID.randomUUID();
        UUID organizationId = UUID.randomUUID();

        Organization organization = Organization.builder()
                .id(organizationId)
                .name("Test Organization")
                .build();

        Category category = Category.builder()
                .id(categoryId)
                .name("Test Category")
                .build();

        Poll poll = Poll.builder()
                .id(pollId)
                .title("Test Poll")
                .description("Test Description")
                .category(category)
                .createdBy(organization)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(pollRepository.findAll()).thenReturn(List.of(poll));

        List<PollDto> polls = pollService.getAllPolls();

        assertEquals(1, polls.size());
        assertEquals(poll.getTitle(), polls.get(0).getTitle());
        assertEquals(poll.getDescription(), polls.get(0).getDescription());
        assertEquals(poll.getCategory(), polls.get(0).getCategory());
        assertEquals(poll.getCreatedBy(), polls.get(0).getCreatedBy());
        verify(pollRepository, times(1)).findAll();
    }

    @Test
    void shouldGetPollsByOrganizationSuccessfully() {
        UUID organizationId = UUID.randomUUID();
        UUID pollId = UUID.randomUUID();

        Organization organization = Organization.builder()
                .id(organizationId)
                .name("Test Organization")
                .build();

        Poll poll = Poll.builder()
                .id(pollId)
                .title("Test Poll")
                .description("Test Description")
                .createdBy(organization)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(userRepository.findById(organizationId)).thenReturn(Optional.of(organization));
        when(pollRepository.findByCreatedBy(organization)).thenReturn(List.of(poll));

        List<PollDto> polls = pollService.getPollsByOrganization(organizationId);

        assertEquals(1, polls.size());
        assertEquals(poll.getTitle(), polls.get(0).getTitle());
        assertEquals(poll.getDescription(), polls.get(0).getDescription());
        assertEquals(poll.getCategory(), polls.get(0).getCategory());
        assertEquals(poll.getCreatedBy(), polls.get(0).getCreatedBy());
        verify(pollRepository, times(1)).findByCreatedBy(organization);
    }
}

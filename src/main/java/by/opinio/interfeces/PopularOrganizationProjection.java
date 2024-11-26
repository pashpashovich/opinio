package by.opinio.interfeces;

import java.util.UUID;

public interface PopularOrganizationProjection {
    UUID getId();
    String getName();
    Long getSubscriberCount();
}
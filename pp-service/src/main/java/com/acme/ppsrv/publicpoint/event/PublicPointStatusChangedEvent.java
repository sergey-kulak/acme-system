package com.acme.ppsrv.publicpoint.event;

import com.acme.ppsrv.publicpoint.PublicPointStatus;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;

import java.util.UUID;

@Data
@Builder
public class PublicPointStatusChangedEvent {
    private UUID companyId;
    private UUID publicPointId;
    private PublicPointStatus fromStatus;
    private PublicPointStatus toStatus;

    @Tolerate
    public PublicPointStatusChangedEvent() {
    }
}

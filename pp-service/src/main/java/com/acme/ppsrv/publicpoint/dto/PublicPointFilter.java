package com.acme.ppsrv.publicpoint.dto;

import com.acme.ppsrv.publicpoint.PublicPointStatus;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;

import java.util.List;
import java.util.UUID;

@Data
@Builder
public class PublicPointFilter {
    private String namePattern;
    private List<PublicPointStatus> status;
    private UUID companyId;

    @Tolerate
    public PublicPointFilter() {
    }
}

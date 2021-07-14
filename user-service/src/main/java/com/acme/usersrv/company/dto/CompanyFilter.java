package com.acme.usersrv.company.dto;

import com.acme.usersrv.company.CompanyStatus;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;

import java.util.Collection;

@Data
@Builder
public class CompanyFilter {
    private String namePattern;
    private Collection<CompanyStatus> statuses;
    private String country;
    private String vatin;

    @Tolerate
    public CompanyFilter() {
    }
}

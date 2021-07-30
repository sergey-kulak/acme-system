package com.acme.usersrv.company.dto;

import com.acme.usersrv.company.CompanyStatus;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;

import java.util.Collection;

@Data
@Builder
public class CompanyFilter {
    @Parameter(description = "Search pattern for company full name with \"start with ignore case\" logic")
    private String namePattern;
    private Collection<CompanyStatus> status;
    private String country;
    private String vatin;

    @Tolerate
    public CompanyFilter() {
    }
}

package com.acme.usersrv.company.repository;

import com.acme.usersrv.company.Company;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface CompanyRepository extends ReactiveSortingRepository<Company, UUID>, CompanyRepositoryCustom {

    @Query("select count(1) > 0 from company " +
            "where vatin = upper($1) or reg_number = upper($2) or lower(full_name) = lower($3)")
    Mono<Boolean> existByMainParams(String vatin, String regNumber, String fullName);
}

package com.acme.menusrv.dish.service;

import com.acme.commons.security.NotAccountantAuthenticated;
import com.acme.menusrv.dish.dto.CreateDishDto;
import com.acme.menusrv.dish.dto.DishDto;
import com.acme.menusrv.dish.dto.DishFilter;
import com.acme.menusrv.dish.dto.DishNameDto;
import com.acme.menusrv.dish.dto.FullDetailsDishDto;
import com.acme.menusrv.dish.dto.UpdateDishDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;

@Validated
public interface DishService {
    @PreAuthorize("hasAnyAuthority('ADMIN','COMPANY_OWNER','PP_MANAGER','CHEF')")
    Mono<UUID> create(@Valid CreateDishDto createDto);

    @PreAuthorize("hasAnyAuthority('ADMIN','COMPANY_OWNER','PP_MANAGER','CHEF')")
    Mono<Void> update(UUID id, @Valid UpdateDishDto updateDto);

    @PreAuthorize("hasAnyAuthority('ADMIN','COMPANY_OWNER','PP_MANAGER','CHEF')")
    Mono<Void> delete(UUID id);

    @NotAccountantAuthenticated
    Mono<DishDto> findById(UUID id);

    @PreAuthorize("hasAnyAuthority('ADMIN','COMPANY_OWNER','PP_MANAGER','CHEF','COOK')")
    Mono<FullDetailsDishDto> findFullDetailsById(UUID id);

    @PreAuthorize("hasAnyAuthority('ADMIN','COMPANY_OWNER','PP_MANAGER','CHEF','COOK')")
    Mono<Page<DishDto>> find(@Valid DishFilter filter, Pageable pageable);

    @PreAuthorize("hasAnyAuthority('ADMIN','COMPANY_OWNER','PP_MANAGER','CHEF','COOK')")
    Mono<List<String>> findTags(UUID companyId, UUID publicPointId);

    @PreAuthorize("hasAnyAuthority('ADMIN','COMPANY_OWNER','PP_MANAGER','CHEF','COOK')")
    Flux<DishNameDto> findNames(UUID companyId, UUID publicPointId);
}

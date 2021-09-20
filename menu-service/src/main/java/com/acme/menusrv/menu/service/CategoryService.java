package com.acme.menusrv.menu.service;

import com.acme.menusrv.menu.dto.CategoryDto;
import com.acme.menusrv.menu.dto.CreateCategoryDto;
import com.acme.menusrv.menu.dto.UpdateCategoryDto;
import com.acme.menusrv.menu.dto.UpdateMenuDto;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.util.UUID;

@Validated
public interface CategoryService {
    @PreAuthorize("hasAnyAuthority('ADMIN','COMPANY_OWNER','PP_MANAGER')")
    Mono<UUID> create(@Valid CreateCategoryDto dto);

    @PreAuthorize("hasAnyAuthority('ADMIN','COMPANY_OWNER','PP_MANAGER')")
    Mono<Void> update(UUID id, @Valid UpdateCategoryDto dto);

    @PreAuthorize("hasAnyAuthority('ADMIN','COMPANY_OWNER','PP_MANAGER','CHEF','COOK')")
    Mono<CategoryDto> findById(UUID id);

    @PreAuthorize("hasAnyAuthority('ADMIN','COMPANY_OWNER','PP_MANAGER')")
    Mono<Void> update(@Valid UpdateMenuDto dto);

    @PreAuthorize("hasAnyAuthority('ADMIN','COMPANY_OWNER','PP_MANAGER','CHEF','COOK')")
    Flux<CategoryDto> findAll(UUID companyId, UUID publicPointId);
}

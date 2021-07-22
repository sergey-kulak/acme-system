package com.acme.usersrv.test;

import com.acme.usersrv.common.security.CompanyUser;
import com.acme.usersrv.common.utils.SecurityUtils;
import com.acme.usersrv.company.Company;
import com.acme.usersrv.company.CompanyStatus;
import com.acme.usersrv.company.repository.CompanyRepository;
import com.acme.usersrv.user.User;
import com.acme.usersrv.user.UserRole;
import com.acme.usersrv.user.UserStatus;
import com.acme.usersrv.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.crypto.password.PasswordEncoder;
import reactor.core.publisher.Mono;

import java.util.UUID;

public class TestEntityHelper {
    @Autowired
    private CompanyRepository companyRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public Mono<Company> createCompany() {
        return createCompany(CompanyStatus.ACTIVE);
    }

    public Mono<Company> createCompany(CompanyStatus status) {
        Company company = new Company();
        company.setFullName(RandomTestUtils.randomString("Company"));
        company.setCountry("BY");
        company.setCity(RandomTestUtils.randomString("City"));
        company.setAddress(RandomTestUtils.randomString("Address"));
        company.setEmail(RandomTestUtils.randomEmail());
        company.setVatin(RandomTestUtils.randomString("BY"));
        company.setRegNumber(RandomTestUtils.randomString("REG"));
        company.setStatus(status);
        return companyRepository.save(company);
    }

    public Mono<User> createUser(Company company, UserRole role) {
        User user = new User();
        user.setFirstName(RandomTestUtils.randomString("firstNaem"));
        user.setLastName(RandomTestUtils.randomString("lastName"));
        user.setEmail(RandomTestUtils.randomEmail());
        user.setStatus(UserStatus.ACTIVE);
        user.setPassword(passwordEncoder.encode(RandomTestUtils.randomString("pasW")));
        user.setCompanyId(company.getId());
        user.setRole(role);
        return userRepository.save(user);
    }

    public Mono<User> createCompanyOwner(Company company) {
        return createUser(company, UserRole.COMPANY_OWNER);
    }

    public Mono<User> createUserForLoggedUser(Company company) {
        return getCurrentUser()
                .zipWhen(user -> createUser(company, user.getRole()))
                .flatMap(data -> {
                    CompanyUser cmpUser = data.getT1();
                    User user = data.getT2();
                    user.setEmail(cmpUser.getUsername());
                    cmpUser.setCompanyId(company.getId());
                    cmpUser.setId(user.getId());
                    return userRepository.save(user);
                });
    }

    public Mono<Company> linkWithCurrentUser(Company company) {
        return getCurrentUser()
                .doOnSuccess(cmpUser -> cmpUser.setCompanyId(company.getId()))
                .map(user -> company);
    }

    public Mono<CompanyUser> getCurrentUser() {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .map(Authentication::getPrincipal)
                .cast(CompanyUser.class);
    }

    public Mono<Company> linkOtherCompanyWithCurrentUser(Company company) {
        return getCurrentUser()
                .doOnSuccess(cmpUser -> cmpUser.setCompanyId(UUID.randomUUID()))
                .map(user -> company);
    }
}

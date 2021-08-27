package com.acme.usersrv.test;

import com.acme.commons.security.CompanyUser;
import com.acme.commons.security.UserRole;
import com.acme.testcommons.RandomTestUtils;
import com.acme.testcommons.security.TestSecurityUtils;
import com.acme.usersrv.company.Company;
import com.acme.usersrv.company.CompanyStatus;
import com.acme.usersrv.company.repository.CompanyRepository;
import com.acme.usersrv.user.User;
import com.acme.usersrv.user.UserStatus;
import com.acme.usersrv.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import reactor.core.publisher.Mono;

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
        return TestSecurityUtils.getCurrentUser()
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
        return TestSecurityUtils.linkWithCurrentUser(company.getId())
                .then(Mono.just(company));
    }

    public Mono<Company> linkOtherCompanyWithCurrentUser(Company company) {
        return TestSecurityUtils.linkOtherCompanyWithCurrentUser()
                .then(Mono.just(company));
    }
}

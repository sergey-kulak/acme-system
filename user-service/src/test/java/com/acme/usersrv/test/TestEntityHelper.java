package com.acme.usersrv.test;

import com.acme.usersrv.company.Company;
import com.acme.usersrv.company.CompanyStatus;
import com.acme.usersrv.company.repository.CompanyRepository;
import com.acme.usersrv.user.User;
import com.acme.usersrv.user.UserRole;
import com.acme.usersrv.user.UserStatus;
import com.acme.usersrv.user.repository.UserRepository;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

public class TestEntityHelper {
    @Autowired
    private CompanyRepository companyRepository;
    @Autowired
    private UserRepository userRepository;

    public Mono<Company> createCompany() {
        Company company = new Company();
        company.setFullName(RandomTestUtils.randomString("Company"));
        company.setCountry("BY");
        company.setCity(RandomTestUtils.randomString("City"));
        company.setAddress(RandomTestUtils.randomString("Address"));
        company.setEmail(RandomTestUtils.randomEmail());
        company.setVatin(RandomTestUtils.randomString("BY"));
        company.setRegNumber(RandomTestUtils.randomString("REG"));
        company.setStatus(CompanyStatus.ACTIVE);
        return companyRepository.save(company);
    }

    public Mono<User> createUser(Company company, UserRole role) {
        User user = new User();
        user.setFirstName(RandomTestUtils.randomString("firstNaem"));
        user.setLastName(RandomTestUtils.randomString("lastName"));
        user.setEmail(RandomTestUtils.randomEmail());
        user.setStatus(UserStatus.ACTIVE);
        user.setPassword(RandomTestUtils.randomString("pasW"));
        user.setCompanyId(company.getId());
        user.setRole(role);
        return userRepository.save(user);
    }

    public Mono<User> createCompanyOwner(Company company) {
        return createUser(company, UserRole.COMPANY_OWNER);
    }
}

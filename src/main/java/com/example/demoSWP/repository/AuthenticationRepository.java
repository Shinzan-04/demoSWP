package com.example.demoSWP.repository;

import com.example.demoSWP.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthenticationRepository extends JpaRepository<Account, Long> {

    //findAccountByEmail
    Account findAccountByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByPhone(String phone);
}

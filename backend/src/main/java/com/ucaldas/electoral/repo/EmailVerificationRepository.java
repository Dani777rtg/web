package com.ucaldas.electoral.repo;

import com.ucaldas.electoral.domain.EmailVerification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmailVerificationRepository extends JpaRepository<EmailVerification, Long> {
    Optional<EmailVerification> findFirstByUserIdOrderByIdDesc(Long userId);
}

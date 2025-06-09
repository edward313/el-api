package com.easylearning.api.repository;

import com.easylearning.api.model.ExpertRegistration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ExpertRegistrationRepository extends JpaRepository<ExpertRegistration, Long>, JpaSpecificationExecutor<ExpertRegistration> {
    ExpertRegistration findFirstByPhoneOrEmail(String phone, String email);
}

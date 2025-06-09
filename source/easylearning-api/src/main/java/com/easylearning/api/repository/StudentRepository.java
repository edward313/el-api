package com.easylearning.api.repository;

import com.easylearning.api.dto.expert.MyStudentDto;
import com.easylearning.api.model.Student;
import com.easylearning.api.model.criteria.RegistrationCriteria;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long>, JpaSpecificationExecutor<Student> {
    Optional<Student> findByReferralCode(String code);
    Optional<Student> findByReferralCodeAndIsSeller(String code, Boolean isSeller);
    Optional<Student> findByReferralCodeAndIsSellerAndStatus(String code, Boolean isSeller, Integer status);
    Optional<Student> findByIdAndIsSeller(Long id, Boolean isSeller);
    Optional<Student> findFirstByIsSellerAndAccount_GoogleIdAndAccount_ResetPwdCodeAndAccount_Status(Boolean isSeller, String googleId, String code,Integer status);
    Optional<Student> findFirstByAccount_EmailAndAccount_Platform(String email, Integer platform);
    Optional<Student> findFirstByIsSellerAndAccountFacebookIdAndAccountResetPwdCodeAndAccountStatus(Boolean isSeller, String googleId, String code,Integer status);
    Optional<Student> findFirstByIsSystemSeller(Boolean isSystemSeller);
    @Transactional
    @Modifying
    void deleteAllByAccountId(Long accountId);
    Boolean existsByIdAndIsSeller(Long id, Boolean isSeller);
    @Transactional
    @Modifying
    @Query("UPDATE Student SET isSeller = false, referralCode = NULL")
    void resetStudentIsSeller();

    @Query("SELECT NEW com.easylearning.api.dto.expert.MyStudentDto(s.id, s.account.fullName, s.account.phone, s.account.email, s.account.avatarPath, COUNT(c.id)) " +
            "FROM Student s " +
            "INNER JOIN Registration r ON r.student.id = s.id " +
            "INNER JOIN Course c ON c.id = r.course.id " +
            "INNER JOIN Expert e ON e.id = c.expert.id " +
            "WHERE (:#{#criteria.expertId} IS NULL OR e.id = :#{#criteria.expertId}) " +
            "AND (:#{#criteria.studentName} IS NULL OR s.account.fullName LIKE %:#{#criteria.studentName}%) " +
            "GROUP BY s.id ")
    Page<MyStudentDto> findAllStudentByExpertId(@Param("criteria") RegistrationCriteria criteria, Pageable pageable);
}

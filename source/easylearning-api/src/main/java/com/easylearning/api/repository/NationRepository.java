package com.easylearning.api.repository;

import com.easylearning.api.model.Nation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface NationRepository extends JpaRepository<Nation, Long>, JpaSpecificationExecutor<Nation> {

    @Query("select n.id from Nation n where n.parent.id = :parentId")
    List<Long> getAllNationIdByParentId(@Param("parentId") List<Long> parentId);

    @Transactional
    @Modifying
    @Query("delete FROM Nation n WHERE n.parent.id in :parentIds")
    void deleteAllByParentIdInList(@Param("parentIds") List<Long> parentIds);

    Boolean existsByParentId(Long parentId);

    Optional<Nation> findByIdAndKind(Long id, Integer kind);
}

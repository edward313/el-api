package com.easylearning.api.repository;

import com.easylearning.api.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long>, JpaSpecificationExecutor<Category> {

    Category findByName(String name);
    Category findFirstByKind(Integer kind);
    Category findFirstByIdAndStatus(Long id, Integer status);
    @Query(value = "SELECT * FROM db_el_category WHERE kind <> :kind", nativeQuery = true)
    List<Category> findAllExceptKind(@Param("kind") Integer kind);
}

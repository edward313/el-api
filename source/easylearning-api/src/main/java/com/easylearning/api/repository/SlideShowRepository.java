package com.easylearning.api.repository;

import com.easylearning.api.model.SlideShow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface SlideShowRepository extends JpaRepository<SlideShow, Long>, JpaSpecificationExecutor<SlideShow> {
    SlideShow findByTitle(String title);
}

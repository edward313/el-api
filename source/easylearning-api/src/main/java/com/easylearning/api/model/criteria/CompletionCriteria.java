package com.easylearning.api.model.criteria;

import com.easylearning.api.model.*;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@Data
public class CompletionCriteria {
    private Long id;
    private Long studentId;
    private Long courseId;
    private Long lessonId;
    private Boolean isFinished;
    private Long secondProgress;
    public Specification<Completion> getSpecification() {
        return new Specification<Completion>() {
            private static final long serialVersionUID = 1L;

            @Override
            public Predicate toPredicate(Root<Completion> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<>();

                if (getId() != null) {
                    predicates.add(cb.equal(root.get("id"), getId()));
                }
                if (getIsFinished() != null) {
                    predicates.add(cb.equal(root.get("isFinished"), getIsFinished()));
                }
                if (getSecondProgress() != null) {
                    predicates.add(cb.equal(root.get("secondProgress"), getSecondProgress()));
                }
                if (getCourseId() != null) {
                    Join<Completion, Course> joinCourse = root.join("course", JoinType.INNER);
                    predicates.add(cb.equal(joinCourse.get("id"), getCourseId()));
                }
                if (getStudentId() != null) {
                    Join<Completion, Student> joinStudent = root.join("student", JoinType.INNER);
                    predicates.add(cb.equal(joinStudent.get("id"), getStudentId()));
                }
                if (getLessonId() != null) {
                    Join<Completion, Lesson> joinLesson = root.join("lesson", JoinType.INNER);
                    predicates.add(cb.equal(joinLesson.get("id"), getLessonId()));
                }
                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        };
    }
}

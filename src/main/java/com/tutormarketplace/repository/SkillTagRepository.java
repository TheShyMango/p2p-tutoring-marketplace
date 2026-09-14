package com.tutormarketplace.repository;

import com.tutormarketplace.model.SkillTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SkillTagRepository extends JpaRepository<SkillTag, Long> {
    List<SkillTag> findByUserId(Long userId);
    List<SkillTag> findBySkillName(String skillName);
    Optional<SkillTag> findByUserIdAndSkillName(Long userId, String skillName);
}

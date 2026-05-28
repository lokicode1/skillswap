package com.skillswap.skills;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SkillNeedRepository extends JpaRepository<SkillNeed, Long> {
  @Query("""
      select n from SkillNeed n
      where n.active = true
        and (lower(n.title) like lower(concat('%', :q, '%'))
          or lower(n.description) like lower(concat('%', :q, '%')))
      order by n.createdAt desc
      """)
  List<SkillNeed> searchActive(@Param("q") String q);
}


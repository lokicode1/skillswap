package com.skillswap.skills;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SkillOfferRepository extends JpaRepository<SkillOffer, Long> {
  @Query("""
      select o from SkillOffer o
      where o.active = true
        and (lower(o.title) like lower(concat('%', :q, '%'))
          or lower(o.description) like lower(concat('%', :q, '%')))
      order by o.createdAt desc
      """)
  List<SkillOffer> searchActive(@Param("q") String q);
}


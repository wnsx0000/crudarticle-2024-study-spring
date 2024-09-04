package springproject.crudarticle.repository;

import springproject.crudarticle.domain.Articleboard;
import springproject.crudarticle.domain.Member;

import java.util.List;

public interface JpaUnitedRepository extends org.springframework.data.jpa.repository.JpaRepository<Member,Long> {
    List<Member> findByName(String name);
}

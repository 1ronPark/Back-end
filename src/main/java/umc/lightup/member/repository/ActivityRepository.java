package umc.lightup.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import umc.lightup.member.domain.Activity;
import umc.lightup.member.domain.Member;

import java.util.List;

@Repository
public interface ActivityRepository extends JpaRepository<Activity, Long> {
    long removeAllByMember(Member member);
    List<Activity> findByMember(Member member);
}
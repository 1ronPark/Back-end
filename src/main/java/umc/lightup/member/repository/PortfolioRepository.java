package umc.lightup.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import umc.lightup.member.domain.Member;
import umc.lightup.member.domain.Portfolio;

import java.util.List;

@Repository
public interface PortfolioRepository extends JpaRepository<Portfolio, Long> {
    List<Portfolio> findByMember(Member member);
    // 다른 Member의 것을 삭제하면 안 되니 확인 절차 진행
    long removeByIdAndMemberEmail(Long id, String memberEmail);
}
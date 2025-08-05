package umc.lightup.region.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import umc.lightup.member.domain.Member;
import umc.lightup.region.domain.Region;

import java.util.List;

public interface RegionRepository extends JpaRepository<Region, Long> {

    @Query("SELECT DISTINCT r.siDo FROM Region r")
    List<String> findDistinctSiDo();

    List<Region> findBySiDo(String siDo);

/*    @Query("select r from MemberRegion mr join mr.region r where mr.member=:member")
    List<Region> findByMember(@Param("member") Member member);*/

    boolean existsBySiDo(String siDo);
    boolean existsBySiGunGu(String siGunGu);
}

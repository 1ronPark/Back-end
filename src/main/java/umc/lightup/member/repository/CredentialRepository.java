package umc.lightup.member.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import umc.lightup.member.domain.Credential;
import umc.lightup.member.domain.Member;
import umc.lightup.member.enums.CredentialType;

import java.util.List;
import java.util.Optional;

@Repository
public interface CredentialRepository extends JpaRepository<Credential, Long> {
    @Query("select c from Credential c join fetch c.member m where c.credentialType=:credentialType and m.email=:email")
    Optional<Credential> findByCredentialTypeAndEmail(@Param("credentialType") CredentialType credentialType, @Param("email") String email);
    @EntityGraph(attributePaths = {"member"})
    Optional<Credential> findByCredentialTypeAndCredential(CredentialType credentialType, String credential);
    boolean existsByCredentialTypeAndCredential(CredentialType credentialType, String credential);
    boolean existsByCredentialTypeAndMember(CredentialType credentialType, Member member);
    long countByMember(Member member);
    long removeByCredentialTypeAndMember(CredentialType credentialType, Member member);
    List<Credential> findAllByMemberEmail(String memberEmail);
}
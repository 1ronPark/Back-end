package umc.lightup.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import umc.lightup.member.domain.Credential;
import umc.lightup.member.enums.CredentialType;

import java.util.Optional;

@Repository
public interface CredentialRepository extends JpaRepository<Credential, Long> {
    @Query("select c from Credential c join fetch c.member m where c.credentialType=:credentialType and m.email=:email")
    Optional<Credential> findByCredentialTypeAndEmail(@Param("credentialType") CredentialType credentialType, @Param("email") String email);
}
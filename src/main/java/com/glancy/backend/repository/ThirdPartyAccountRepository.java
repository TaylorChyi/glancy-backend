package com.glancy.backend.repository;

import com.glancy.backend.entity.ThirdPartyAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ThirdPartyAccountRepository extends JpaRepository<ThirdPartyAccount, Long> {
    Optional<ThirdPartyAccount> findByProviderAndExternalId(String provider, String externalId);
}

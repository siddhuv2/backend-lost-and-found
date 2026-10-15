package com.college.lostfound.repository;

import com.college.lostfound.entity.Claim;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClaimRepository extends JpaRepository<Claim, Long> {

    List<Claim> findByClaimantIdOrderByCreatedAtDesc(Long claimantId);

    List<Claim> findByItemIdOrderByCreatedAtDesc(Long itemId);

    List<Claim> findByItemReportedByIdOrderByCreatedAtDesc(Long finderUserId);

    boolean existsByItemIdAndClaimantId(Long itemId, Long claimantId);
}

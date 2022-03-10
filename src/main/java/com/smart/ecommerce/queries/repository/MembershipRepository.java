package com.smart.ecommerce.queries.repository;

import com.smart.ecommerce.entity.checkout.Membership;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface MembershipRepository extends JpaRepository<Membership, String> {
    @Query(value = "SELECT idMembership AS idMembership, membership, acquirer, group_id, status_id FROM membership  m where m.group_id = :groupId ;", nativeQuery = true)
    List<Map<String, Object>> getMembershipByGroupId(@Param("groupId") String groupId);
}

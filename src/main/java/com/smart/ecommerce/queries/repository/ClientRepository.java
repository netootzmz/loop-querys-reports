package com.smart.ecommerce.queries.repository;

import com.smart.ecommerce.entity.admin.Client;
import com.smart.ecommerce.entity.configuration.ClientAccountInfo;
import com.smart.ecommerce.entity.configuration.ClientBillingDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;

public interface ClientRepository extends JpaRepository<Client, String> {
    List<Client> findAllByStatusId(Integer status_id);

    @Query(value = "select " +
            " CONCAT(u.name,' ',u.last_name1,IF(ISNULL(u.last_name2)=0,' ',''),u.last_name2) as name , " +
            "       u.language_id    as language, r.role_id as role, r.description as role_description, u.client_id as client, g.group_id as `group`, g.name as group_description, r.behavior_role_mode_id as role_mode_id" +
            " " +
            " " +
            " from user u " +
            "INNER JOIN role r ON r.role_id = u.role_id " +
            "INNER JOIN `group` g ON g.group_id = u.group_id " +
//			"INNER JOIN `core_user_profile` cup ON cup. " +
            " where u.user_id = :userId ;"  , nativeQuery = true)
    Map<String,Object> getUserInfo(@Param("userId")Integer userId);

    @Query(value = "SELECT " +
            " * " +
            " FROM client_billing_detail AS cbd " +
            " WHERE cbd.rfc = UPPER(:rfc) AND cbd.group_id = :groupId ;"  , nativeQuery = true)
    List<Map<String,Object>> getClientBillingDetailByRFCAndGroupId(@Param("rfc") String rfc, @Param("groupId") String groupId);

    @Query(value = "SELECT " +
            " * " +
            " FROM client_account_info AS cai " +
            " WHERE cai.clabe = :clabe AND cai.group_id = :groupId ;"  , nativeQuery = true)
    List<Map<String,Object>> getClientAccountInfoByClabeAndGroupId(@Param("clabe") String clabe, @Param("groupId")String branchId);

    @Query(value = "SELECT " +
            " * " +
            " FROM client_account_info AS cai " +
            " WHERE cai.group_id = :groupId ;"  , nativeQuery = true)
    ClientAccountInfo getClientAccountInfoByGroupId(@Param("groupId") String groupId);

    @Query(value = "SELECT " +
            " * " +
            " FROM client_billing_detail AS cbd " +
            " WHERE cbd.rfc = UPPER(:rfc) AND cbd.client_id = :clientId ;"  , nativeQuery = true)
    List<Map<String, Object>> getClientBillingDetailByRFCAndClientId(@Param("rfc") String rfc, @Param("clientId") String clientId);
}

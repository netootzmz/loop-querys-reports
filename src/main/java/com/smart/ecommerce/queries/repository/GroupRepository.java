package com.smart.ecommerce.queries.repository;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.smart.ecommerce.entity.admin.Group;

public interface GroupRepository extends JpaRepository<Group, String> {

    @Query(value = " select * from `group` " +
            "where group_id =:groupId ", nativeQuery = true)
    public Group getGroup(@Param("groupId") String groupId);

    @Query(value = " select * from group_conection " +
            "where group_id = :groupId ", nativeQuery = true)
    Map<String, Object> getGroupConection(@Param("groupId") String groupId);

    @Query(value = " select " +
            "       bin.bin_number as binNumber, " +
            "       bin.mes as mes, " +
            "       bin.monto_minimo as montoMinimo " +
            "from mockup_bines bin ", nativeQuery = true)
    List<Map<String, Object>> getMockupBIN();

    @Query(value = " select * from group_conection ", nativeQuery = true)
    List<Map<String, Object>> getGroupConectionAll();


    @Query(value = "select distinct ccsb.core_client_solution_id ,  " +
            "                corebin.initial_bin as initialBin,  " +
            "                corebin.final_bin   as finalBin,  " +
            "                cdbm.minimum_amount as montoMinimo,  " +
            "                corebin.bank_id as bankId,  " +
            "                ccb.name as nameBank,  " +
            "                mc.name             as mes  " +
            "from core_bin corebin  " +
            "         inner join core_cat_bank ccb on corebin.bank_id = ccb.cat_bank_id  " +
            "         inner join core_detail_bin_msi cdbm on corebin.core_bin_id = cdbm.core_bin_id  " +
            "         inner join mounth_catalog mc on cdbm.mounth_id = mc.mounth_catalog_id  " +
            "         inner join config_client_solution_bin ccsb on corebin.core_bin_id = ccsb.core_bin_id  " +
            "         inner join core_client_solution ccs on ccsb.core_client_solution_id = ccs.core_client_solution_id  " +
            "         inner join config_client_product ccp on ccs.core_client_solution_id = ccp.core_client_solution_id  " +
            "where corebin.status_id = 1  " +
            "  and ccb.status_id = 1  " +
            "  and cdbm.status_id = 1  " +
            "  and mc.status_id = 1  " +
            "  and ccsb.status_id = 1  " +
            "  and ccp.api_key = :apiKey ;", nativeQuery = true)
    List<Map<String, Object>> getBIN(@Param("apiKey") String apiKey);

    @Query(value = "select" +
            "       cs.core_solution_id as solutionId, " +
            "       cs.name as solutioName " +
            "from   core_client_solution ccsl " +
            "inner join client cte on ccsl.client_id = cte.client_id " +
            "inner join `group` gpo on cte.client_id = gpo.client_id " +
            "inner join core_solution cs on ccsl.core_solution_id = cs.core_solution_id " +
            "where gpo.group_id = :groupId", nativeQuery = true)
    List<Map<String, Object>> getSolutionByGroupId(@Param("groupId") String groupId);


  @Query(value = "SELECT * FROM membership  m where m.group_id = :groupId ;", nativeQuery = true)
    List<Map<String,Object>> getMembershipByGroupId(@Param("groupId") String groupId);

    @Query(value = "SELECT g.group_id AS groupId, g.name AS groupName, g.group_level_id AS groupLevelId, glc.name AS groupLevelName " +
            "FROM `group` g " +
            "INNER JOIN group_level_catalog glc ON g.group_level_id = glc.group_level_id " +
            "WHERE g.group_level_id = :groupLevelId AND g.parent_group_id = :groupId ;", nativeQuery = true)
    List<Map<String, String>> getGroupsByLevelIdAndParentGroupId(@Param("groupLevelId") Integer groupLevelId, @Param("groupId") String groupId);
}

package com.easylearning.api.repository;

import com.easylearning.api.dto.wallet.MoneySettings;
import com.easylearning.api.model.Settings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SettingsRepository extends JpaRepository<Settings, Long>, JpaSpecificationExecutor<Settings> {
    Settings findBySettingKey(String settingKey);

    Settings findBySettingKeyAndGroupName(String settingKey, String groupName);

    @Query(value ="SELECT " +
            "  (SELECT setting_value FROM db_el_settings WHERE setting_key = :minMoneyOutKey AND group_name = :groupName) AS minMoneyOut, " +
            "  (SELECT setting_value FROM db_el_settings WHERE setting_key = :minBalanceKey AND group_name = :groupName) AS minBalance", nativeQuery = true)
    MoneySettings getInfo(String minMoneyOutKey, String minBalanceKey, String groupName);

    @Query("SELECT s.settingValue " +
            "FROM Settings s " +
            "WHERE s.groupName = :groupName ")
    String findValueByGroupName(@Param("groupName") String groupName);

    @Query("SELECT s.settingValue " +
            "FROM Settings s " +
            "WHERE s.settingKey = :keyName ")
    String findValueByKey(@Param("keyName") String keyName);
}

package com.easylearning.api.mapper;

import com.easylearning.api.dto.wallet.WalletDto;
import com.easylearning.api.form.wallet.UpdateWalletForm;
import com.easylearning.api.model.Wallet;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        uses = {AccountMapper.class})
public interface WalletMapper {
    @Mapping(source = "id", target = "id")
    @Mapping(source = "balance", target = "balance")
    @Mapping(source = "kind", target = "kind")
    @Mapping(source = "walletNumber", target ="walletNumber")
    @Mapping(source = "account", target = "account", qualifiedByName = "fromAccountToDto")
    @Mapping(source = "modifiedDate", target = "modifiedDate")
    @Mapping(source = "createdDate", target = "createdDate")
    @Mapping(source = "status", target = "status")
    @BeanMapping(ignoreByDefault = true)
    @Named("fromEntityToWalletDto")
    WalletDto fromEntityToWalletDto(Wallet wallet);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "balance", target = "balance")
    @Mapping(source = "kind", target = "kind")
    @Mapping(source = "walletNumber", target ="walletNumber")
    @Mapping(source = "modifiedDate", target = "modifiedDate")
    @Mapping(source = "createdDate", target = "createdDate")
    @Mapping(source = "status", target = "status")
    @BeanMapping(ignoreByDefault = true)
    @Named("fromEntityToMyWalletDto")
    WalletDto fromEntityToMyWalletDto(Wallet wallet);

    @IterableMapping(elementTargetType = WalletDto.class, qualifiedByName = "fromEntityToWalletDto")
    @BeanMapping(ignoreByDefault = true)
    List<WalletDto> fromEntityToWalletDtoList(List<Wallet> wallet);

    @Mapping(source = "status", target = "status")
    @Mapping(source = "kind", target = "kind")
    @Mapping(source = "balance", target = "balance")
    @Mapping(source = "walletNumber", target = "walletNumber")
    @BeanMapping(ignoreByDefault = true)
    @Named("updateFromUpdateWalletFormToWalletEntity")
    void updateFromUpdateWalletFormToWalletEntity(UpdateWalletForm walletForm, @MappingTarget Wallet wallet);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "balance", target = "balance")
    @Mapping(source = "kind", target = "kind")
    @Mapping(source = "walletNumber", target ="walletNumber")
    @Mapping(source = "modifiedDate", target = "modifiedDate")
    @Mapping(source = "createdDate", target = "createdDate")
    @Mapping(source = "status", target = "status")
    @BeanMapping(ignoreByDefault = true)
    @Named("fromEntityToWalletDtoForWalletTransaction")
    WalletDto fromEntityToWalletDtoForWalletTransaction(Wallet wallet);
}

package com.easylearning.api.mapper;

import com.easylearning.api.dto.walletTransaction.WalletTransactionDto;
import com.easylearning.api.model.WalletTransaction;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        uses = {WalletMapper.class})
public interface WalletTransactionMapper {
    @Mapping(source = "id", target = "id")
    @Mapping(source = "money", target = "money")
    @Mapping(source = "kind", target = "kind")
    @Mapping(source = "state", target ="state")
    @Mapping(source = "note", target = "note")
    @Mapping(source = "lastBalance", target = "lastBalance")
    @Mapping(source = "wallet", target = "wallet", qualifiedByName = "fromEntityToWalletDto")
    @Mapping(source = "modifiedDate", target = "modifiedDate")
    @Mapping(source = "createdDate", target = "createdDate")
    @Mapping(source = "status", target = "status")
    @BeanMapping(ignoreByDefault = true)
    @Named("fromEntityToWalletTransactionDto")
    WalletTransactionDto fromEntityToWalletTransactionDto(WalletTransaction walletTransaction);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "money", target = "money")
    @Mapping(source = "kind", target = "kind")
    @Mapping(source = "state", target ="state")
    @Mapping(source = "note", target = "note")
    @Mapping(source = "lastBalance", target = "lastBalance")
    @Mapping(source = "wallet", target = "wallet", qualifiedByName = "fromEntityToWalletDtoForWalletTransaction")
    @Mapping(source = "modifiedDate", target = "modifiedDate")
    @Mapping(source = "createdDate", target = "createdDate")
    @Mapping(source = "status", target = "status")
    @BeanMapping(ignoreByDefault = true)
    @Named("fromEntityToMyWalletTransaction")
    WalletTransactionDto fromEntityToMyWalletTransaction(WalletTransaction walletTransaction);

    @IterableMapping(elementTargetType = WalletTransactionDto.class, qualifiedByName = "fromEntityToMyWalletTransaction")
    @BeanMapping(ignoreByDefault = true)
    List<WalletTransactionDto> fromEntityToMyWalletTransactionList(List<WalletTransaction> walletTransactions);

    @IterableMapping(elementTargetType = WalletTransactionDto.class, qualifiedByName = "fromEntityToWalletTransactionDto")
    @BeanMapping(ignoreByDefault = true)
    List<WalletTransactionDto> fromEntityToWalletTransactionDtoList(List<WalletTransaction> walletTransactions);
}
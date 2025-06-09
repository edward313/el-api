package com.easylearning.api.controller;

import com.easylearning.api.dto.ApiMessageDto;
import com.easylearning.api.dto.ErrorCode;
import com.easylearning.api.dto.ResponseListDto;
import com.easylearning.api.dto.walletTransaction.WalletTransactionDto;
import com.easylearning.api.exception.NotFoundException;
import com.easylearning.api.mapper.WalletTransactionMapper;
import com.easylearning.api.model.WalletTransaction;
import com.easylearning.api.model.criteria.WalletTransactionCriteria;
import com.easylearning.api.repository.WalletTransactionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/wallet-transaction")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Slf4j
public class WalletTransactionController extends ABasicController{
    @Autowired
    private WalletTransactionRepository walletTransactionRepository;
    @Autowired
    private WalletTransactionMapper walletTransactionMapper;

    @GetMapping(value = "/my-transaction", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('WT_MT')")
    public ApiMessageDto<ResponseListDto<List<WalletTransactionDto>>> myWalletTransaction(WalletTransactionCriteria walletTransactionCriteria, Pageable pageable) {

        ApiMessageDto<ResponseListDto<List<WalletTransactionDto>>> apiMessageDto = new ApiMessageDto<>();
        ResponseListDto<List<WalletTransactionDto>> responseListDto = new ResponseListDto<>();

        walletTransactionCriteria.setAccountId(getCurrentUser());

        Page<WalletTransaction> walletTransactions = walletTransactionRepository.findAll(walletTransactionCriteria.getSpecification(), pageable);
        responseListDto.setContent(walletTransactionMapper.fromEntityToMyWalletTransactionList(walletTransactions.getContent()));
        responseListDto.setTotalPages(walletTransactions.getTotalPages());
        responseListDto.setTotalElements(walletTransactions.getTotalElements());

        apiMessageDto.setData(responseListDto);
        apiMessageDto.setMessage("Get wallet transaction success");
        return apiMessageDto;
    }

    @GetMapping(value = "/get/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('WT_V')")
    public ApiMessageDto<WalletTransactionDto> get(@PathVariable("id") Long id) {
        ApiMessageDto<WalletTransactionDto> apiMessageDto = new ApiMessageDto<>();
        WalletTransaction walletTransaction = walletTransactionRepository.findById(id).orElse(null);
        if (walletTransaction == null) {
            throw new NotFoundException("Not found wallet transaction", ErrorCode.WALLET_TRANSACTION_ERROR_NOT_FOUND);
        }
        apiMessageDto.setData(walletTransactionMapper.fromEntityToWalletTransactionDto(walletTransaction));
        apiMessageDto.setMessage("Get wallet transaction success");
        return apiMessageDto;
    }
    @GetMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('WT_L')")
    public ApiMessageDto<ResponseListDto<List<WalletTransactionDto>>> getList(WalletTransactionCriteria walletTransactionCriteria, Pageable pageable) {

        ApiMessageDto<ResponseListDto<List<WalletTransactionDto>>> apiMessageDto = new ApiMessageDto<>();
        ResponseListDto<List<WalletTransactionDto>> responseListDto = new ResponseListDto<>();

        Page<WalletTransaction> walletTransactions = walletTransactionRepository.findAll(walletTransactionCriteria.getSpecification(), pageable);
        responseListDto.setContent(walletTransactionMapper.fromEntityToWalletTransactionDtoList(walletTransactions.getContent()));
        responseListDto.setTotalPages(walletTransactions.getTotalPages());
        responseListDto.setTotalElements(walletTransactions.getTotalElements());

        apiMessageDto.setData(responseListDto);
        apiMessageDto.setMessage("Get list wallet transaction success");
        return apiMessageDto;
    }
}
package com.easylearning.api.controller;

import com.easylearning.api.constant.LifeUniConstant;
import com.easylearning.api.dto.ApiMessageDto;
import com.easylearning.api.dto.ErrorCode;
import com.easylearning.api.dto.ResponseListDto;
import com.easylearning.api.dto.courseVersioning.CourseVersioningDto;
import com.easylearning.api.dto.version.VersionDto;
import com.easylearning.api.dto.wallet.CheckWalletBalanceDto;
import com.easylearning.api.dto.wallet.MoneySettings;
import com.easylearning.api.dto.wallet.WalletDto;
import com.easylearning.api.exception.NotFoundException;
import com.easylearning.api.exception.UnauthorizationException;
import com.easylearning.api.form.wallet.UpdateWalletForm;
import com.easylearning.api.mapper.WalletMapper;
import com.easylearning.api.model.Account;
import com.easylearning.api.model.Settings;
import com.easylearning.api.model.Version;
import com.easylearning.api.model.Wallet;
import com.easylearning.api.model.criteria.VersionCriteria;
import com.easylearning.api.model.criteria.WalletCriteria;
import com.easylearning.api.repository.AccountRepository;
import com.easylearning.api.repository.SettingsRepository;
import com.easylearning.api.repository.WalletRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/v1/wallet")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Slf4j
public class WalletController extends ABasicController{
    @Autowired
    private WalletRepository walletRepository;
    @Autowired
    private WalletMapper walletMapper;
    @Autowired
    private SettingsRepository settingsRepository;
    @Autowired
    private AccountRepository accountRepository;

    @GetMapping("/check-balance")
    @PreAuthorize("hasRole('W_MW')")
    public ApiMessageDto<CheckWalletBalanceDto> checkBalance() {
        ApiMessageDto<CheckWalletBalanceDto> apiMessageDto = new ApiMessageDto<>();
        Wallet wallet = walletRepository.findFirstByAccountId(getCurrentUser());

        CheckWalletBalanceDto checkWalletBalanceDto = new CheckWalletBalanceDto();
        checkWalletBalanceDto.setBalance(0.0);
        if (wallet != null && wallet.getBalance() != null){
            checkWalletBalanceDto.setBalance(wallet.getBalance());
        }

        apiMessageDto.setData(checkWalletBalanceDto);
        apiMessageDto.setMessage("Check balance successfully");
        return apiMessageDto;
    }

    @GetMapping(value = "/get/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('W_V')")
    public ApiMessageDto<WalletDto> get(@PathVariable("id") Long id) {
        ApiMessageDto<WalletDto> apiMessageDto = new ApiMessageDto<>();
        Wallet wallet = walletRepository.findById(id).orElse(null);
        if (wallet == null) {
            throw new NotFoundException("Not found wallet", ErrorCode.WALLET_ERROR_NOT_FOUND);
        }
        apiMessageDto.setData(walletMapper.fromEntityToWalletDto(wallet));
        apiMessageDto.setMessage("Get wallet success");
        return apiMessageDto;
    }
    @GetMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('W_L')")
    public ApiMessageDto<ResponseListDto<List<WalletDto>>> getList(WalletCriteria walletCriteria, Pageable pageable) {

        ApiMessageDto<ResponseListDto<List<WalletDto>>> apiMessageDto = new ApiMessageDto<>();
        ResponseListDto<List<WalletDto>> responseListDto = new ResponseListDto<>();

        Page<Wallet> wallets = walletRepository.findAll(walletCriteria.getSpecification(), pageable);
        responseListDto.setContent(walletMapper.fromEntityToWalletDtoList(wallets.getContent()));
        responseListDto.setTotalPages(wallets.getTotalPages());
        responseListDto.setTotalElements(wallets.getTotalElements());

        apiMessageDto.setData(responseListDto);
        apiMessageDto.setMessage("Get list wallet success");
        return apiMessageDto;
    }

    @DeleteMapping(value = "/delete/{id}")
    @PreAuthorize("hasRole('W_D')")
    public ApiMessageDto<String> delete(@PathVariable("id") Long id) {
        ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
        Wallet wallet = walletRepository.findById(id).orElse(null);
        if (wallet == null) {
            throw new NotFoundException("Not found wallet", ErrorCode.WALLET_ERROR_NOT_FOUND);
        }
        walletRepository.deleteById(id);
        apiMessageDto.setMessage("Delete wallet success");
        return apiMessageDto;
    }
    @PutMapping(value = "/update", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('W_U')")
    public ApiMessageDto<String> update(@Valid @RequestBody UpdateWalletForm updateWalletForm, BindingResult bindingResult) {
        ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
        Wallet existingWallet = walletRepository.findById(updateWalletForm.getId()).orElse(null);
        if(existingWallet == null){
            throw new NotFoundException("Not found wallet", ErrorCode.WALLET_ERROR_NOT_FOUND);
        }
        walletMapper.updateFromUpdateWalletFormToWalletEntity(updateWalletForm, existingWallet);
        walletRepository.save(existingWallet);
        apiMessageDto.setMessage("Update wallet success");
        return apiMessageDto;
    }
    @GetMapping(value = "/my-wallet", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('W_MW')")
    public ApiMessageDto<WalletDto> myWallet() {
        ApiMessageDto<WalletDto> apiMessageDto = new ApiMessageDto<>();
        Wallet wallet = walletRepository.findFirstByAccountId(getCurrentUser());
        if (wallet == null) {
            throw new NotFoundException("Not found wallet", ErrorCode.WALLET_ERROR_NOT_FOUND);
        }
        WalletDto walletDto = walletMapper.fromEntityToMyWalletDto(wallet);
        MoneySettings moneySettings = settingsRepository.getInfo(
                LifeUniConstant.SETTING_KEY_MIN_MONEY_OUT
                ,LifeUniConstant.SETTING_KEY_MIN_BALANCE
                ,LifeUniConstant.SETTING_GROUP_NAME_PAYOUT);
        walletDto.setMinBalance(moneySettings.getMinBalance());
        Settings settings = settingsRepository.findBySettingKey(LifeUniConstant.SETTING_KEY_TAX_PERCENT);
        if (settings != null) {
            walletDto.setTaxPercent(Integer.parseInt(settings.getSettingValue()));
        }
        walletDto.setMinMoneyOut(moneySettings.getMinMoneyOut());
        apiMessageDto.setData(walletDto);
        apiMessageDto.setMessage("Get wallet success");
        return apiMessageDto;
    }
    @PostMapping(value = "/create-missing-wallet", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiMessageDto<String> createWallet() {
        if(!isSuperAdmin()){
            throw new UnauthorizationException("Not allowed create");
        }
        walletRepository.updateKindForSellersAndExperts();
        ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
        List<Account> accounts = accountRepository.findAllUserWithOutWallet();
        List<Wallet> wallets = new ArrayList<>();
        for(Account account: accounts){
            Wallet wallet = new Wallet();
            wallet.setAccount(account);
            if(account.getKind() == LifeUniConstant.USER_KIND_STUDENT){
                wallet.setKind(LifeUniConstant.WALLET_KIND_SELLER);
            }
            else {
                wallet.setKind(LifeUniConstant.WALLET_KIND_EXPERT);
            }
            wallet.setWalletNumber(getUniqueWalletNumber());
            wallets.add(wallet);
        }
        walletRepository.saveAll(wallets);
        return apiMessageDto;
    }
}

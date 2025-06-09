package com.easylearning.api.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.easylearning.api.constant.LifeUniConstant;
import com.easylearning.api.dto.ApiMessageDto;
import com.easylearning.api.dto.ErrorCode;
import com.easylearning.api.dto.ResponseListDto;
import com.easylearning.api.dto.registerPayout.RegisterPayoutDto;
import com.easylearning.api.exception.BadRequestException;
import com.easylearning.api.exception.NotFoundException;
import com.easylearning.api.exception.UnauthorizationException;
import com.easylearning.api.form.account.BankInfo;
import com.easylearning.api.form.registerPayout.CancelRegisterPayoutForm;
import com.easylearning.api.form.registerPayout.CreateRegisterPayoutForm;
import com.easylearning.api.mapper.RegisterPayoutMapper;
import com.easylearning.api.model.*;
import com.easylearning.api.model.criteria.RegisterPayoutCriteria;
import com.easylearning.api.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("/v1/register-payout")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Slf4j
public class RegisterPayoutController extends ABasicController {
    @Autowired
    private RegisterPayoutRepository registerPayoutRepository;
    @Autowired
    private RegisterPayoutMapper registerPayoutMapper;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private ExpertRepository expertRepository;
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private WalletRepository walletRepository;
    @Autowired
    private RegisterPeriodRepository registerPeriodRepository;
    @Autowired
    private SettingsRepository settingsRepository;
    @Autowired
    private ResourceLoader resourceLoader;

    @GetMapping(value = "/get/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('RP_V')")
    public ApiMessageDto<RegisterPayoutDto> get(@PathVariable("id") Long id) {
        ApiMessageDto<RegisterPayoutDto> apiMessageDto = new ApiMessageDto<>();
        RegisterPayout registerPayout = registerPayoutRepository.findById(id).orElse(null);
        if (registerPayout == null) {
            throw new NotFoundException("Register payout is not found", ErrorCode.REGISTER_PAYOUT_ERROR_NOT_FOUND);
        }
        apiMessageDto.setData(registerPayoutMapper.fromEntityToRegisterPayoutDto(registerPayout));
        apiMessageDto.setMessage("Get register payout success");
        return apiMessageDto;
    }

    @GetMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('RP_L')")
    public ApiMessageDto<ResponseListDto<List<RegisterPayoutDto>>> list(RegisterPayoutCriteria criteria, Pageable pageable) {
        criteria.setIsOrderByCreatedDate(true);
        criteria.setKind(LifeUniConstant.REGISTER_PAYOUT_KIND_INDIVIDUAL);
        ApiMessageDto<ResponseListDto<List<RegisterPayoutDto>>> responseListObjApiMessageDto = new ApiMessageDto<>();
        Page<RegisterPayout> registerPayoutPage = registerPayoutRepository.findAll(criteria.getSpecification(), pageable);
        ResponseListDto<List<RegisterPayoutDto>> responseListObj = new ResponseListDto<>();
        List<RegisterPayoutDto> registerPayoutDtos = registerPayoutMapper.fromEntityToRegisterPayoutDtoList(registerPayoutPage.getContent());

        responseListObj.setContent(registerPayoutDtos);
        responseListObj.setTotalPages(registerPayoutPage.getTotalPages());
        responseListObj.setTotalElements(registerPayoutPage.getTotalElements());

        responseListObjApiMessageDto.setData(responseListObj);
        responseListObjApiMessageDto.setMessage("Get list register payout success");
        return responseListObjApiMessageDto;
    }
    @GetMapping(value = "/my-register-payout", produces = MediaType.APPLICATION_JSON_VALUE)
    public ApiMessageDto<ResponseListDto<List<RegisterPayoutDto>>> myRegisterPayout(RegisterPayoutCriteria criteria, Pageable pageable) {
        criteria.setAccountId(getCurrentUser());
        criteria.setKind(LifeUniConstant.REGISTER_PAYOUT_KIND_INDIVIDUAL);
        criteria.setIsOrderByCreatedDate(true);
        ApiMessageDto<ResponseListDto<List<RegisterPayoutDto>>> responseListObjApiMessageDto = new ApiMessageDto<>();
        Page<RegisterPayout> registerPayoutPage = registerPayoutRepository.findAll(criteria.getSpecification(), pageable);
        ResponseListDto<List<RegisterPayoutDto>> responseListObj = new ResponseListDto<>();
        List<RegisterPayoutDto> registerPayoutDtos = registerPayoutMapper.fromEntityToRegisterPayoutDtoList(registerPayoutPage.getContent());

        responseListObj.setContent(registerPayoutDtos);
        responseListObj.setTotalPages(registerPayoutPage.getTotalPages());
        responseListObj.setTotalElements(registerPayoutPage.getTotalElements());

        responseListObjApiMessageDto.setData(responseListObj);
        responseListObjApiMessageDto.setMessage("Get my register payout success");
        return responseListObjApiMessageDto;
    }

    @DeleteMapping(value = "/delete/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('RP_D')")
    public ApiMessageDto<String> delete(@PathVariable("id") Long id) {
        ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
        RegisterPayout registerPayout = registerPayoutRepository.findById(id).orElse(null);
        if (registerPayout == null) {
            throw new NotFoundException("Register payout is not found", ErrorCode.REGISTER_PAYOUT_ERROR_NOT_FOUND);
        }
        if(Objects.equals(registerPayout.getState(), LifeUniConstant.REGISTER_PAYOUT_STATE_APPROVED)){
            throw new BadRequestException("Can not delete approved Register Payout",ErrorCode.REGISTER_PAYOUT_ERROR_STATE_APPROVE);
        }
        if(Objects.equals(registerPayout.getState(), LifeUniConstant.REGISTER_PAYOUT_STATE_CALCULATED)){
            throw new BadRequestException("Can not delete calculated Register Payout",ErrorCode.REGISTER_PAYOUT_ERROR_STATE_CALCULATED);
        }
        if(Objects.equals(registerPayout.getState(), LifeUniConstant.REGISTER_PAYOUT_STATE_PENDING)){
            handleMoneyWhenCancelRegisterPayout(registerPayout, walletRepository);
        }
        registerPayoutRepository.deleteById(id);
        apiMessageDto.setResult(true);
        apiMessageDto.setMessage("Delete register payout success");
        return apiMessageDto;
    }

    @PostMapping(value = "/create", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('RP_C')")
    public ApiMessageDto<String> create(@Valid @RequestBody CreateRegisterPayoutForm createRegisterPayoutForm, BindingResult bindingResult) {
        if (!isExpert() && !isSeller()) {
            throw new UnauthorizationException("Not allowed create");
        }
        Account account = accountRepository.findById(getCurrentUser()).orElse(null);
        if (account == null) {
            throw new BadRequestException("Account is not found", ErrorCode.ACCOUNT_ERROR_NOT_FOUND);
        }
        String bankInfoString;
        if(isExpert()){
            Expert expert = expertRepository.findById(getCurrentUser()).orElse(null);
            if(expert==null){
                throw new BadRequestException("Expert is not found", ErrorCode.EXPERT_ERROR_NOT_FOUND);
            }
            if(expert.getIsSystemExpert()){
                throw new BadRequestException("System account can not create register payout", ErrorCode.REGISTER_PAYOUT_NOT_ALLOW_CREATE);
            }
            bankInfoString = expert.getBankInfo();
        }
        else {
            Student seller = studentRepository.findByIdAndIsSeller(getCurrentUser(),true).orElse(null);
            if(seller == null){
                throw new BadRequestException("Seller is not found", ErrorCode.SELLER_ERROR_NOT_FOUND);
            }
            if(seller.getIsSystemSeller()){
                throw new BadRequestException("System account can not create register payout", ErrorCode.REGISTER_PAYOUT_NOT_ALLOW_CREATE);
            }
            bankInfoString = seller.getBankInfo();

        }
        // check bankInfo
        if(StringUtils.isBlank(bankInfoString)){
            throw new BadRequestException("Bank info not enough",ErrorCode.REGISTER_PAYOUT_BANK_INFO_NOT_ENOUGH);
        }
        try {
            BankInfo bankInfo = objectMapper.readValue(bankInfoString,BankInfo.class);
            if(StringUtils.isBlank(bankInfo.getBankAgency()) || StringUtils.isBlank(bankInfo.getBankName())
                    || StringUtils.isBlank(bankInfo.getBankNameCustomer()) || StringUtils.isBlank(bankInfo.getBankNumber())){
                throw new BadRequestException("Bank info not enough",ErrorCode.REGISTER_PAYOUT_BANK_INFO_NOT_ENOUGH);
            }
        } catch (JsonProcessingException e) {
            log.error("Convert json to bankInfo false: "+e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
        ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();

        RegisterPayout registerPayout = registerPayoutMapper.fromCreateRegisterPayoutFormToEntity(createRegisterPayoutForm);
        String taxPercentValue = settingsRepository.findBySettingKey(LifeUniConstant.SETTING_KEY_TAX_PERCENT).getSettingValue();
        if (taxPercentValue == null) {
            throw new NotFoundException("Tax percent setting not found",ErrorCode.SETTINGS_ERROR_NOT_FOUND);
        }
        Integer taxPercent = Integer.parseInt(taxPercentValue);
        registerPayout.setTaxPercent(taxPercent);
        registerPayout.setTaxMoney((createRegisterPayoutForm.getMoney() * taxPercent)/100);
        registerPayout.setBankInfo(bankInfoString);
        registerPayout.setKind(LifeUniConstant.REGISTER_PAYOUT_KIND_INDIVIDUAL);
        registerPayout.setAccount(account);
        registerPayout.setAccountKind(account.getKind());
        registerPayout.setState(LifeUniConstant.REGISTER_PAYOUT_STATE_PENDING);
        Wallet wallet = walletRepository.findByAccountId(getCurrentUser());
        if(wallet == null){
            throw new NotFoundException("Wallet not found",ErrorCode.WALLET_ERROR_NOT_FOUND);
        }
        Double currentBalance = (wallet.getBalance() != null) ? wallet.getBalance() : 0.0;
        Double currentHoldingBalance = (wallet.getHoldingBalance() != null) ? wallet.getHoldingBalance() : 0.0;
        Double minBalance = getMinBalance(); // tiền còn lại tối thiểu
        Double minMoneyOut = getMinMoneyOut();// tiền rút tối thiểu
        Double moneyOut = createRegisterPayoutForm.getMoney();
        if(moneyOut < minMoneyOut) {
            throw new BadRequestException("The money you have requested to withdraw is less then the minimum", ErrorCode.REGISTER_PAYOUT_ERROR_NOT_REACH_MINIMUM);
        }
        if((currentBalance - moneyOut) < minBalance){
            throw new BadRequestException("Your remaining balance is not enough", ErrorCode.REGISTER_PAYOUT_ERROR_NOT_ENOUGH_BALANCE);
        }
        wallet.setBalance(currentBalance - moneyOut);
        wallet.setHoldingBalance(currentHoldingBalance + moneyOut);
        walletRepository.save(wallet);
        registerPayoutRepository.save(registerPayout);
        apiMessageDto.setMessage("Create register payout success");
        return apiMessageDto;
    }
    @PutMapping(value = "/cancel", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('RP_CAN')")
    public ApiMessageDto<String> cancel(@Valid @RequestBody CancelRegisterPayoutForm cancelRegisterPayoutForm, BindingResult bindingResult) {
        ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
        if (!isExpert() && !isSeller() && !isAdmin()) {
            throw new UnauthorizationException("Not allowed create");
        }
        Account account = accountRepository.findById(getCurrentUser()).orElse(null);
        if (account == null) {
            throw new BadRequestException("Account is not found", ErrorCode.ACCOUNT_ERROR_NOT_FOUND);
        }
        RegisterPayout registerPayout;
        if(isAdmin()){
            registerPayout = registerPayoutRepository.findById(cancelRegisterPayoutForm.getId()).orElse(null);
        }
        else {
            registerPayout = registerPayoutRepository.findByIdAndAccountId(cancelRegisterPayoutForm.getId(),getCurrentUser());
        }
        if (registerPayout == null) {
            throw new NotFoundException("Register payout not found", ErrorCode.REGISTER_PAYOUT_ERROR_NOT_FOUND);
        }
        if((isExpert() || isSeller()) && !registerPayout.getState().equals(LifeUniConstant.REGISTER_PAYOUT_STATE_PENDING)){
            throw new BadRequestException("Only allow expert/seller to cancel pending register payout",ErrorCode.REGISTER_PAYOUT_ERROR_ALLOW_CHANGE_STATE);
        }
        if(!Objects.equals(registerPayout.getState(), LifeUniConstant.REGISTER_PAYOUT_STATE_PENDING) && !Objects.equals(registerPayout.getState(), LifeUniConstant.REGISTER_PAYOUT_STATE_CALCULATED)){
            throw new BadRequestException("Can not cancel Register Payout",ErrorCode.REGISTER_PAYOUT_ERROR_ALLOW_CHANGE_STATE);
        }
        if(isAdmin()){
            // admin cancel calculated register payout
            RegisterPeriod registerPeriod = registerPayout.getRegisterPeriod();
            if(registerPeriod != null){
                //Update money in RegisterPayout Kind Sum
                RegisterPayout sumRegisterPayout = registerPayoutRepository.findFirstByRegisterPeriodIdAndAccountIdAndKind(registerPeriod.getId(), registerPayout.getAccount().getId(), LifeUniConstant.REGISTER_PAYOUT_KIND_SUM);
                if(sumRegisterPayout != null){
                    if(sumRegisterPayout.getMoney() <= registerPayout.getMoney()){
                        registerPayoutRepository.deleteById(sumRegisterPayout.getId());
                    }else {
                        sumRegisterPayout.setMoney(sumRegisterPayout.getMoney() - registerPayout.getMoney());
                        registerPayoutRepository.save(sumRegisterPayout);
                    }
                }
                //Update money in RegisterPeriod
                registerPeriod.setTotalPayout(registerPeriod.getTotalPayout() - (registerPayout.getMoney()));
                registerPeriodRepository.save(registerPeriod);
            }
            registerPayout.setState(LifeUniConstant.REGISTER_PAYOUT_STATE_ADMIN_CANCELLED);
        }
        // seller/expert cancel their Register Payout
        else {
            registerPayout.setState(LifeUniConstant.REGISTER_PAYOUT_STATE_CANCELLED);
        }
        handleMoneyWhenCancelRegisterPayout(registerPayout, walletRepository);
        registerPayoutRepository.save(registerPayout);
        apiMessageDto.setMessage("cancel register payout success !");
        return apiMessageDto;
    }
    @GetMapping(value = "/export-to-excel/{periodId}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @PreAuthorize("hasRole('RP_E')")
    public ResponseEntity<Resource> exportToExcel(@PathVariable("periodId") Long periodId) throws IOException {
        // tìm Payout period
        RegisterPeriod registerPeriod = registerPeriodRepository.findByIdAndState(periodId,LifeUniConstant.REGISTER_PERIOD_STATE_APPROVE);
        if(registerPeriod == null){
            throw new NotFoundException("Register period not found",ErrorCode.REGISTRATION_ERROR_NOT_FOUND);
        }
        RegisterPayoutCriteria criteria = new RegisterPayoutCriteria();
        criteria.setRegisterPeriodId(periodId);
        criteria.setIsIgnoreSystemSeller(true);
        criteria.setIsIgnoreSystemExpert(true);
        criteria.setKind(LifeUniConstant.REGISTER_PAYOUT_KIND_SUM);
        criteria.setIgnoreStates(List.of(LifeUniConstant.REGISTER_PAYOUT_STATE_CANCELLED, LifeUniConstant.REGISTER_PAYOUT_STATE_ADMIN_CANCELLED));
        criteria.setOrderByAccountName(true);
        List<RegisterPayout> registerPayouts = registerPayoutRepository.findAll(criteria.getSpecification());
        // Load the export template Excel file
        Resource resource = resourceLoader.getResource("classpath:templates/export_template.xlsx");
        InputStream inputStream = resource.getInputStream();
        Workbook workbook = new XSSFWorkbook(inputStream);

        // Get the first sheet (assuming it's the one you want to fill)
        Sheet sheet = workbook.getSheetAt(0);
        // set name
        Row nameRow = sheet.getRow(7);
        if (nameRow == null) {
            nameRow = sheet.createRow(7);
        }
        Cell cell = nameRow.getCell(0);
        if (cell == null) {
            cell = nameRow.createCell(0);
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String formattedStartDate = dateFormat.format(registerPeriod.getStartDate());
        String formattedEndDate = dateFormat.format(registerPeriod.getEndDate());
        cell.setCellValue("Kỳ: " + formattedStartDate + " tới " + formattedEndDate);

        // Fill data rows starting from the second row
        int rowNum = 10;
        int orderNumber = 1;
        for (RegisterPayout registerPayout : registerPayouts) {
            Row row = sheet.createRow(rowNum++);
            fillData(row, registerPayout,orderNumber);
            orderNumber++;
        }

        // Set the content type and headers for the response
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC+7"));
        dateFormat = new SimpleDateFormat("ddMMyyHHmmss");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC+7"));
        String formattedCurrentDate = dateFormat.format(calendar.getTime());
        //headers.setContentDispositionFormData("attachment", "uy_nhiem_chi_"+monthlyPeriod.getName()+"_"+formattedCurrentDate+".xlsx");
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + "uy_nhiem_chi_"+ registerPeriod.getName()+"_"+formattedCurrentDate+".xlsx");
        // Write the workbook content to a byte array
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        workbook.write(bos);
        ByteArrayResource byteArrayResource = new ByteArrayResource(bos.toByteArray());
        return ResponseEntity.ok()
                .headers(headers)
                .body(byteArrayResource);
    }
    void fillData(Row row, RegisterPayout registerPayout, int orderNumber) {
        // Create a new CellStyle for bordered cells
        CellStyle borderedCellStyle = row.getSheet().getWorkbook().createCellStyle();
        borderedCellStyle.setBorderBottom(BorderStyle.THIN);
        borderedCellStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());
        borderedCellStyle.setBorderLeft(BorderStyle.THIN);
        borderedCellStyle.setLeftBorderColor(IndexedColors.BLACK.getIndex());
        borderedCellStyle.setBorderRight(BorderStyle.THIN);
        borderedCellStyle.setRightBorderColor(IndexedColors.BLACK.getIndex());
        borderedCellStyle.setBorderTop(BorderStyle.THIN);
        borderedCellStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());

        createCell(row, 0, orderNumber,borderedCellStyle);
        createCell(row, 1, registerPayout.getAccount().getFullName(),borderedCellStyle);
        createCell(row, 2, registerPayout.getAccount().getPhone(),borderedCellStyle);
        createCell(row, 7, com.google.common.base.Objects.equal(registerPayout.getKind(),LifeUniConstant.PERIOD_DETAIL_KIND_EXPERT)?"Chuyên gia":"Nguời bán hàng",borderedCellStyle);
        if(registerPayout.getMoney() == null){
            registerPayout.setMoney(0D);
        }
        createCell(row, 8, registerPayout.getMoney(),borderedCellStyle);
        createCell(row, 9,registerPayout.getTaxPercent(),borderedCellStyle);
        createCell(row, 10, registerPayout.getTaxMoney(),borderedCellStyle);
        createCell(row, 11, registerPayout.getMoney() - registerPayout.getTaxMoney(),borderedCellStyle);
        //set bankInfo
        String bankInfoString = registerPayout.getBankInfo();
        BankInfo bankInfo = new BankInfo();
        if(StringUtils.isNotBlank(bankInfoString)){
            try {
                bankInfo = objectMapper.readValue(bankInfoString,BankInfo.class);
            } catch (JsonProcessingException e) {
                log.error("Convert json to bankInfo false: "+e.getMessage());
            }
        }
        createCell(row, 3, bankInfo.getBankNumber(),borderedCellStyle);
        createCell(row, 4, bankInfo.getBankNameCustomer(),borderedCellStyle);
        createCell(row, 5, bankInfo.getBankName(),borderedCellStyle);
        createCell(row, 6, bankInfo.getBankAgency(),borderedCellStyle);
    }

    //String value
    void createCell(Row row, int cellIndex, String value,CellStyle style) {
        Cell cell = row.createCell(cellIndex);
        cell.setCellStyle(style);
        if (value != null) {
            cell.setCellValue(value);
        }
    }

    //Double value
    void createCell(Row row, int cellIndex, Double value, CellStyle style) {
        Cell cell = row.createCell(cellIndex);
        cell.setCellStyle(style);
        if (value != null) {
            cell.setCellValue(Math.floor(value));
            // format data for double value
            DataFormat format = row.getSheet().getWorkbook().createDataFormat();
            style.setDataFormat(format.getFormat("#,##0"));
        }
    }

    //Integer value
    void createCell(Row row, int cellIndex, Integer value, CellStyle style) {
        Cell cell = row.createCell(cellIndex);
        cell.setCellStyle(style);
        if (value != null) {
            cell.setCellValue(value);
        }

    }

    void createCell(Row row, int cellIndex, Date value, CellStyle style) {
        Cell cell = row.createCell(cellIndex);
        cell.setCellStyle(style);
        if (value != null) {
            cell.setCellValue(value);

        }
    }
}
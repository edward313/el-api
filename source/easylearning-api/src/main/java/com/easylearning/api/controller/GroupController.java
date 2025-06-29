package com.easylearning.api.controller;

import com.easylearning.api.dto.ApiMessageDto;
import com.easylearning.api.dto.ResponseListDto;
import com.easylearning.api.exception.BadRequestException;
import com.easylearning.api.exception.NotFoundException;
import com.easylearning.api.exception.UnauthorizationException;
import com.easylearning.api.form.group.CreateGroupForm;
import com.easylearning.api.form.group.UpdateGroupForm;
import com.easylearning.api.model.Group;
import com.easylearning.api.model.Permission;
import com.easylearning.api.repository.GroupRepository;
import com.easylearning.api.repository.PermissionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/v1/group")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Slf4j
public class GroupController extends ABasicController{
    @Autowired
    GroupRepository groupRepository;
    @Autowired
    PermissionRepository permissionRepository;

    @PostMapping(value = "/create", produces= MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('GR_C')")
    public ApiMessageDto<String> create(@Valid @RequestBody CreateGroupForm createGroupForm, BindingResult bindingResult) {
        if(!isSuperAdmin()){
            throw new UnauthorizationException("Not allowed create.");
        }
        ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
        Group group = groupRepository.findFirstByName(createGroupForm.getName());
        if(group != null){
            throw new BadRequestException("Group name doesnt exist");
        }
        group = new Group();
        group.setName(createGroupForm.getName());
        group.setDescription(createGroupForm.getDescription());
        group.setKind(createGroupForm.getKind());
        List<Permission> permissions = new ArrayList<>();
        for(int i=0;i< createGroupForm.getPermissions().length;i++){
            Permission permission = permissionRepository.findById(createGroupForm.getPermissions()[i]).orElse(null);
            if(permission != null){
                permissions.add(permission);
            }
        }
        group.setStatus(1);
        group.setPermissions(permissions);
        groupRepository.save(group);
        apiMessageDto.setMessage("Create group success");
        return apiMessageDto;
    }

    @PutMapping(value = "/update", produces= MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('GR_U')")
    public ApiMessageDto<String> update(@Valid @RequestBody UpdateGroupForm updateGroupForm, BindingResult bindingResult) {
        if(!isSuperAdmin()){
            throw new UnauthorizationException("Not allowed update.");
        }
        ApiMessageDto<String> apiMessageDto = new ApiMessageDto<>();
        Group group = groupRepository.findById(updateGroupForm.getId()).orElse(null);
        if(group == null){
            throw new NotFoundException("Group name doesnt exist");
        }
        //check su ton tai cua group name khac khi dat ten.
        Group otherGroup = groupRepository.findFirstByName(updateGroupForm.getName());
        if(otherGroup != null && !Objects.equals( updateGroupForm.getId(),otherGroup.getId()) ){
            throw new BadRequestException("Cant update this group name because it is exist");
        }
        group.setName(updateGroupForm.getName());
        group.setDescription(updateGroupForm.getDescription());
        List<Permission> permissions = new ArrayList<>();
        for(int i=0;i< updateGroupForm.getPermissions().length;i++){
            Permission permission = permissionRepository.findById(updateGroupForm.getPermissions()[i]).orElse(null);
            if(permission != null){
                permissions.add(permission);
            }
        }
        group.setPermissions(permissions);
        groupRepository.save(group);
        apiMessageDto.setMessage("Update group success");

        return apiMessageDto;
    }

    @GetMapping(value = "/get/{id}", produces= MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('GR_V')")
    public ApiMessageDto<Group> get(@PathVariable("id")  Long id) {
        if(!isSuperAdmin()){
            throw new UnauthorizationException("Not allowed to get.");
        }
        ApiMessageDto<Group> apiMessageDto = new ApiMessageDto<>();
        Group group =groupRepository.findById(id).orElse(null);
        apiMessageDto.setData(group);
        apiMessageDto.setMessage("Get group success");
        return apiMessageDto;
    }

    @GetMapping(value = "/list", produces= MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('GR_L')")
    public ApiMessageDto<ResponseListDto<Group>> list(@RequestParam(required = true)  int kind, Pageable pageable) {
        if(!isSuperAdmin()){
            throw new UnauthorizationException("Not allowed list group.");
        }

        ApiMessageDto<ResponseListDto<Group>> apiMessageDto = new ApiMessageDto<>();
        Page<Group> groups = groupRepository
                .findAllByKind(kind, PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(new Sort.Order(Sort.Direction.DESC, "createdDate"))));
        ResponseListDto<Group> responseListDto = new ResponseListDto(groups.getContent() , groups.getTotalElements(), groups.getTotalPages());
        apiMessageDto.setData(responseListDto);
        apiMessageDto.setMessage("list group success");
        return apiMessageDto;
    }
}

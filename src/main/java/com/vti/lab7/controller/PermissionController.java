package com.vti.lab7.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.vti.lab7.dto.PermissionDTO;
import com.vti.lab7.dto.response.RestData;
import com.vti.lab7.service.PermissionService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RestController
@RequestMapping("api/v1/permissions")
@RequiredArgsConstructor
public class PermissionController {

    private final PermissionService permissionService;

    @PreAuthorize("hasAuthority('read_permission')")
    @GetMapping
    public ResponseEntity<Object> getAllPermissions() {
        List<PermissionDTO> responseDto = permissionService.getAllPermissions();
        RestData<?> restData = new RestData<>(200, null, null, responseDto);
        return ResponseEntity.ok().body(restData);
    }

    @PreAuthorize("hasAuthority('read_permission')")
    @GetMapping("/{id}")
    public ResponseEntity<Object> getPermissionById(@PathVariable Long id) {
        PermissionDTO responseDto = permissionService.getPermissionById(id);
        RestData<?> restData = new RestData<>(200, null, null, responseDto);
        return ResponseEntity.ok().body(restData);
    }

    @PreAuthorize("hasAuthority('create_permission')")
    @PostMapping
    public ResponseEntity<Object> createPermission(@Valid @RequestBody PermissionDTO permissionDTO) {
        PermissionDTO responseDto = permissionService.createPermission(permissionDTO);
        RestData<?> restData = new RestData<>(201, null, null, responseDto);
        return ResponseEntity.ok().body(restData);
    }

    @PreAuthorize("hasAuthority('update_permission')")
    @PutMapping("/{id}")
    public ResponseEntity<Object> updatePermission(@PathVariable Long id,
            @Valid @RequestBody PermissionDTO permissionDTO) {
        PermissionDTO responseDto = permissionService.updatePermission(id, permissionDTO);
        RestData<?> restData = new RestData<>(200, null, null, responseDto);
        return ResponseEntity.ok().body(restData);
    }

    @PreAuthorize("hasAuthority('delete_permission')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deletePermission(@PathVariable Long id) {
        permissionService.deletePermission(id);
        RestData<?> restData = new RestData<>(200, null,
                String.format("Permission with ID %d has been successfully deleted.", id), null);
        return ResponseEntity.ok().body(restData);
    }
}
package com.appmodz.executionmodule.model;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Entity
@lombok.Getter
@lombok.Setter
@lombok.ToString
@Table(name="RolePermissions")
public class RolePermissions implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="Role_Permission_Id")
    private long rolePermissionId;

    @OneToOne
    @JoinColumn(name="Role_Id")
    private Role roleId;

    @OneToMany(targetEntity=Permission.class, fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name="Role_Permissions")
    private List<Permission> permissions;

    @Column(name="RolePermission_Created_On")
    @CreationTimestamp
    private Date rolePermissionCreatedOn;

    @Column(name="RolePermission_Updated_On")
    @UpdateTimestamp
    private Date rolePermissionUpdatedOn;
}

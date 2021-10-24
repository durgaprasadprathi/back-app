package com.appmodz.executionmodule.model;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@lombok.Getter
@lombok.Setter
@lombok.ToString
@Table(name="Permissions")
public class Permission implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="Permission_Id")
    private long permissionId;

    @Column(name="Permission_Description")
    private String permissionDescription;

    @Column(name="Permission_Name")
    private String permissionName;

    @Column(name="Permission_Scope")
    private String permissionScope;

    @Column(name="Permission_Created_On")
    @CreationTimestamp
    private Date permissionCreatedOn;

    @Column(name="Permission_Updated_On")
    @UpdateTimestamp
    private Date permissionUpdatedOn;
}

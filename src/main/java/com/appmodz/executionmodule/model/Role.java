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
@Table(name="Roles")
public class Role implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="Role_Id")
    private long roleId;

    @Column(name="Role_Description")
    private String roleDescription;

    @Column(name="Role_Name")
    private String roleName;

    @Column(name="Role_Created_On")
    @CreationTimestamp
    private Date roleCreatedOn;

    @Column(name="Role_Updated_On")
    @UpdateTimestamp
    private Date roleUpdatedOn;
}

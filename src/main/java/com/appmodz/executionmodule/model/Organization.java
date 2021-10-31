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
@Table(name="Organizations")
public class Organization implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="Organization_Id")
    private long organizationId;

    @Column(name="Organization_Name")
    private String organizationName;

    @Column(name="Organization_Created_On")
    @CreationTimestamp
    private Date organizationCreatedOn;

    @Column(name="Organization_Updated_On")
    @UpdateTimestamp
    private Date organizationUpdatedOn;
}

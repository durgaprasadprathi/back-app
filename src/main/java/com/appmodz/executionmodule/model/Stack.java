package com.appmodz.executionmodule.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.vladmihalcea.hibernate.type.json.JsonType;
import org.hibernate.annotations.*;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

@Entity
@lombok.Getter
@lombok.Setter
@lombok.ToString
@Table(name="Stacks")
@TypeDefs({
        @TypeDef(name = "json", typeClass = JsonType.class)
})
public class Stack implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="Stack_Id")
    private long stackId;

    @OneToOne
    @JoinColumn(name="Stack_Owner_Id")
    private User owner;

    @OneToOne
    @JoinColumn(name="Stack_Workspace_Name")
    private TerraformBackend terraformBackend;

    @ManyToOne
    @JoinColumn(name="Stack_Organization_Id")
    private Organization organization;

    @Type(type = "json")
    @Column(name="Stack_State",columnDefinition = "json")
    private Object stackState;

    @Type(type = "json")
    @Column(name="Stack_Draft_State",columnDefinition = "json")
    private Object stackDraftState;

    @Column(name="Stack_Location")
    private String stackLocation;

    @ManyToOne
    @JoinColumn(name="Stack_Workspace_Id")
    private Workspace workspace;

    @Column(name="AWS_Region")
    private String awsRegion;

    @Column(name="AWS_Access_Key")
    private String awsAccessKey;

    @Column(name="AWS_Secret_Access_Key")
    private String awsSecretAccessKey;

    @Column(name="Stack_Created_On")
    @CreationTimestamp
    private Date stackCreatedOn;

    @Column(name="Stack_Updated_On")
    @UpdateTimestamp
    private Date stackUpdatedOn;
}

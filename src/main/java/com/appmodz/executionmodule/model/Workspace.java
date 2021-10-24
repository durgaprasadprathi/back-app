package com.appmodz.executionmodule.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@lombok.Getter
@lombok.Setter
@lombok.ToString
@Table(name="Workspaces")
public class Workspace {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="Workspace_Id")
    private long workspaceId;

    @Column(name="Workspace_Name")
    private String workspaceName;

    @OneToOne
    @JoinColumn(name="Workspace_Owner_Id")
    private User owner;

    @ManyToOne
    @JoinColumn(name="Workspace_Organization_Id")
    private Organization organization;

    @OneToMany(targetEntity=Stack.class, mappedBy = "workspace", fetch = FetchType.EAGER)
    @JsonIdentityInfo(
            generator= ObjectIdGenerators.PropertyGenerator.class,
            property="stackId",
            scope = Stack.class,
            resolver = DedupingObjectIdResolver.class
    )
    @JsonIdentityReference(alwaysAsId=true)
    private List<Stack> stacks;

    @Column(name="Workspace_Created_On")
    @CreationTimestamp
    private Date workspaceCreatedOn;

    @Column(name="Workspace_Updated_On")
    @UpdateTimestamp
    private Date workspaceUpdatedOn;
}

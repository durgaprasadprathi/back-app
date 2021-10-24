package com.appmodz.executionmodule.model;

import javax.persistence.*;
import java.util.List;

@Entity
@lombok.Getter
@lombok.Setter
@lombok.ToString
@Table(name="Components")
public class Component {


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="Component_Id")
    private Long id;

    @Column(name="Component_Terraform_Resource_Name")
    private String resourceName;

    @Column(name="Component_Name")
    private String name;

    @Column(name="Component_Is_Parent")
    private Boolean isParent;

    @Column(name="Component_Path")
    private String path;

    @OneToMany(targetEntity=Property.class, fetch = FetchType.EAGER)
    @JoinColumn(name="Component_Properties")
    private List<Property> properties;

    @Column(name="Component_Parent_Id")
    private Long parentId;
}

package com.appmodz.executionmodule.model;

import javax.persistence.*;

@Entity
@lombok.Getter
@lombok.Setter
@lombok.ToString
@Table(name="Properties")
public class Property {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="Property_Id")
    private long id;

    @Column(name="Property_Name")
    private String name;

    @Column(name="Property_Type")
    private String type;

    @Column(name="Property_Default_Value")
    private String defaultValue;

    @Column(name="Property_Root_Name")
    private String propertyRootName;

    @Column(name="Property_Pulumi_Name")
    private String propertyPulumiName;

    @Column(name="Property_Is_MultiValuable")
    private Boolean isMultiValuable;

}

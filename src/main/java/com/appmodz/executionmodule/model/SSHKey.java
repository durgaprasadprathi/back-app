package com.appmodz.executionmodule.model;

import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@lombok.Getter
@lombok.Setter
@lombok.ToString
@Table(name="SSH_Keys")
public class SSHKey implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="SSH_Key_Id")
    private long sshKeyId;

    @ManyToOne
    @JoinColumn(name="SSH_Stack_Id")
    private Stack stack;

    @ManyToOne
    @JoinColumn(name="SSH_Uploader_User_Id")
    private User user;

    @Column(name="SSH_Key_Location")
    private String sshKeyLocation;

    @Column(name="SSH_Key_Created_On")
    @CreationTimestamp
    private Date sshKeyCreatedOn;

}

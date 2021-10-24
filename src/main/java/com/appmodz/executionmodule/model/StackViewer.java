package com.appmodz.executionmodule.model;

import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@lombok.Getter
@lombok.Setter
@lombok.ToString
@Table(name="Stack_Viewers")
public class StackViewer implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="Id")
    private long id;

    @ManyToOne
    @JoinColumn(name="Stack_Id")
    private Stack stack;

    @ManyToOne
    @JoinColumn(name="Stack_Viewer_Id")
    private User user;

    @Column(name="Stack_Viewer_Updated_On")
    @UpdateTimestamp
    private Date stackViewerUpdatedOn;
}

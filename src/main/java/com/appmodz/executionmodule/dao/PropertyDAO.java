package com.appmodz.executionmodule.dao;

import com.appmodz.executionmodule.model.Component;
import com.appmodz.executionmodule.model.Property;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class PropertyDAO {

    private final
    SessionFactory sessionFactory;

    @Autowired
    public PropertyDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public void save(Property property) {
        this.sessionFactory.getCurrentSession().saveOrUpdate(property);
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public Property get(long id) {
        Query query = this.sessionFactory
                .getCurrentSession()
                .createQuery("from Property where id = :id")
                .setParameter("id",id);
        query.setMaxResults(1);
        return (Property) query.uniqueResult();
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public Property getByName(String name) {
        Query query = this.sessionFactory
                .getCurrentSession()
                .createQuery("from Property where name = :name")
                .setParameter("name",name);
        query.setMaxResults(1);
        return (Property) query.uniqueResult();
    }

}

package com.appmodz.executionmodule.dao;

import com.appmodz.executionmodule.model.Component;
import com.appmodz.executionmodule.model.Organization;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class ComponentDAO {

    private final
    SessionFactory sessionFactory;

    @Autowired
    public ComponentDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public void save(Component component) {
        this.sessionFactory.getCurrentSession().saveOrUpdate(component);
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public Component get(long id) {
        Query query = this.sessionFactory
                .getCurrentSession()
                .createQuery("from Component where id = :id")
                .setParameter("id",id);
        query.setMaxResults(1);
        return (Component) query.uniqueResult();
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public List getParents() {
        Query query = this.sessionFactory
                .getCurrentSession()
                .createQuery("from Component where isParent = :isParent")
                .setParameter("isParent",true);
        return query.list();
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public List getComponentsByParentId(long parentId) {
        Query query = this.sessionFactory
                .getCurrentSession()
                .createQuery("from Component where parentId = :parentId")
                .setParameter("parentId",parentId);
        return query.list();
    }
}

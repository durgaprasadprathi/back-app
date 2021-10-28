package com.appmodz.executionmodule.dao;

import com.appmodz.executionmodule.model.Permission;
import com.appmodz.executionmodule.model.RolePermissions;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class PermissionDAO {

    private final
    SessionFactory sessionFactory;

    @Autowired
    public PermissionDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public void save(Permission permission) {
        this.sessionFactory.getCurrentSession().saveOrUpdate(permission);
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public Permission get(long permissionId) {
        Query query = this.sessionFactory
                .getCurrentSession()
                .createQuery("from Permission where permissionId = :permissionId")
                .setParameter("permissionId",permissionId);
        query.setMaxResults(1);
        return (Permission) query.uniqueResult();
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public List getAll() {
        Query query = this.sessionFactory
                .getCurrentSession()
                .createQuery("from Permission");
        return query.list();
    }

}

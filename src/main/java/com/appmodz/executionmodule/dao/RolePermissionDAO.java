package com.appmodz.executionmodule.dao;

import com.appmodz.executionmodule.model.Role;
import com.appmodz.executionmodule.model.RolePermissions;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class RolePermissionDAO {

    private final
    SessionFactory sessionFactory;

    @Autowired
    public RolePermissionDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public void save(RolePermissions rolePermissions) {
        this.sessionFactory.getCurrentSession().saveOrUpdate(rolePermissions);
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public RolePermissions get(long rolePermissionId) {
        Query query = this.sessionFactory
                .getCurrentSession()
                .createQuery("from RolePermissions where rolePermissionId = :rolePermissionId")
                .setParameter("rolePermissionId",rolePermissionId);
        query.setMaxResults(1);
        return (RolePermissions) query.uniqueResult();
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public RolePermissions getByRoleId(long roleId) {
        try {
            Query query = this.sessionFactory
                    .getCurrentSession()
                    .createQuery("from RolePermissions r where r.roleId.roleId = :roleId")
                    .setParameter("roleId", roleId);
            query.setMaxResults(1);
            return (RolePermissions) query.uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}

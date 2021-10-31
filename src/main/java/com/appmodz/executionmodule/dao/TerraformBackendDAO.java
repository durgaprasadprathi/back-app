package com.appmodz.executionmodule.dao;

import com.appmodz.executionmodule.model.Stack;
import com.appmodz.executionmodule.model.TerraformBackend;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class TerraformBackendDAO {
    private final
    SessionFactory sessionFactory;

    @Autowired
    public TerraformBackendDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public void save(TerraformBackend terraformBackend) {
        this.sessionFactory.getCurrentSession().saveOrUpdate(terraformBackend);
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public TerraformBackend get(long id) {
        Query query = this.sessionFactory
                .getCurrentSession()
                .createQuery("from TerraformBackend where id = :id")
                .setParameter("id",id);
        query.setMaxResults(1);
        return (TerraformBackend) query.uniqueResult();
    }
}

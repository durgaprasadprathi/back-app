package com.appmodz.executionmodule.dao;

import com.appmodz.executionmodule.dto.SearchResultDTO;
import com.appmodz.executionmodule.dto.SearchRequestDTO;
import com.appmodz.executionmodule.model.User;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Repository
public class UserDAO {

    private final
    SessionFactory sessionFactory;

    @Autowired
    public UserDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public void save(User user) {
        this.sessionFactory.getCurrentSession().saveOrUpdate(user);
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public User get(long userId) {
        Query query = this.sessionFactory
                .getCurrentSession()
                .createQuery("from User where userId = :userId")
                .setParameter("userId",userId);
        query.setMaxResults(1);
        return (User) query.uniqueResult();
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public User getByUsername(String userName) {
        Query query = this.sessionFactory
                .getCurrentSession()
                .createQuery("from User where userName = :userName")
                .setParameter("userName",userName);
        query.setMaxResults(1);
        return (User) query.uniqueResult();
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public void delete(User user) {
        this.sessionFactory
                .getCurrentSession().delete(user);
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public List getAll() {
        Query query = this.sessionFactory
                .getCurrentSession()
                .createQuery("from User");
        return query.list();
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public SearchResultDTO search(SearchRequestDTO searchRequestDTO) {
        Long count = (Long) this.sessionFactory
                .getCurrentSession().createQuery("select count(*) from User").uniqueResult();

        CriteriaBuilder builder = this.sessionFactory
                .getCurrentSession().getCriteriaBuilder();

        CriteriaQuery<User> criteriaQuery = builder.createQuery(User.class);
        Root<User> root = criteriaQuery.from(User.class);
        criteriaQuery.where(builder.or(builder.like(root.get("userFirstName"),"%"+ searchRequestDTO.getSearch()+"%"),
                builder.like(root.get("userLastName"),"%"+ searchRequestDTO.getSearch()+"%"),
                builder.like(root.get("userName"),"%"+ searchRequestDTO.getSearch()+"%")));
        List<Order> orderList = new ArrayList();
        if(searchRequestDTO.getSort().getAttribute().equals("id"))
            searchRequestDTO.getSort().setAttribute("userId");
        if (searchRequestDTO.getSort().getSort().equals("desc")) {
            orderList.add(builder.desc(root.get(searchRequestDTO.getSort().getAttribute())));
        } else {
            orderList.add(builder.asc(root.get(searchRequestDTO.getSort().getAttribute())));
        }
        criteriaQuery.orderBy(orderList);
        Query query = this.sessionFactory.getCurrentSession().createQuery(criteriaQuery);
        ScrollableResults scrollable = query.scroll(ScrollMode.SCROLL_INSENSITIVE);
        SearchResultDTO searchResultDTO = new SearchResultDTO();
        if(scrollable.last()) {
            searchResultDTO.setTotal(count);
            query.setFirstResult((searchRequestDTO.getPageNo()-1)* searchRequestDTO.getItemPerPage())
                    .setMaxResults(searchRequestDTO.getItemPerPage());
            searchResultDTO.setData(Collections.unmodifiableList(query.list()));
        }
        scrollable.close();
        return searchResultDTO;
    }
}

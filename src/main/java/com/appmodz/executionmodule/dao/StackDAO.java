package com.appmodz.executionmodule.dao;

import com.appmodz.executionmodule.dto.SearchRequestDTO;
import com.appmodz.executionmodule.dto.SearchResultDTO;
import com.appmodz.executionmodule.model.Role;
import com.appmodz.executionmodule.model.Stack;
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
public class StackDAO {

    private final
    SessionFactory sessionFactory;

    @Autowired
    public StackDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public void save(Stack stack) {
        this.sessionFactory.getCurrentSession().saveOrUpdate(stack);
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public Stack get(long stackId) {
        Query query = this.sessionFactory
                .getCurrentSession()
                .createQuery("from Stack where stackId = :stackId")
                .setParameter("stackId",stackId);
        query.setMaxResults(1);
        return (Stack) query.uniqueResult();
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    public void delete(Stack stack) {
        this.sessionFactory
                .getCurrentSession().delete(stack);
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public List getAll() {
        Query query = this.sessionFactory
                .getCurrentSession()
                .createQuery("from Stack");
        return query.list();
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public List getByWorkspaceId(long workspaceId) {
        Query query = this.sessionFactory
                .getCurrentSession()
                .createQuery("from Stack s where s.workspace.workspaceId = :workspaceId")
                .setParameter("workspaceId", workspaceId);
        return query.list();
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public SearchResultDTO search(SearchRequestDTO searchRequestDTO) {
        Long count = (Long) this.sessionFactory
                .getCurrentSession().createQuery("select count(*) from Stack").uniqueResult();

        CriteriaBuilder builder = this.sessionFactory
                .getCurrentSession().getCriteriaBuilder();

        CriteriaQuery<Stack> criteriaQuery = builder.createQuery(Stack.class);
        Root<Stack> root = criteriaQuery.from(Stack.class);
        criteriaQuery.where(builder.like(root.get("terraformBackend").get("name"),"%"+ searchRequestDTO.getSearch()+"%"));
        List<Order> orderList = new ArrayList();
        if(searchRequestDTO.getSort().getAttribute().equals("id"))
            searchRequestDTO.getSort().setAttribute("stackId");
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

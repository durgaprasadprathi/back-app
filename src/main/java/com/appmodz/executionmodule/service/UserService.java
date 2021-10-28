package com.appmodz.executionmodule.service;

import com.appmodz.executionmodule.dao.OrganizationDAO;
import com.appmodz.executionmodule.dao.RoleDAO;
import com.appmodz.executionmodule.dao.UserDAO;
import com.appmodz.executionmodule.dto.PermissionDTO;
import com.appmodz.executionmodule.dto.SearchResultDTO;
import com.appmodz.executionmodule.dto.SearchRequestDTO;
import com.appmodz.executionmodule.dto.UserRequestDTO;
import com.appmodz.executionmodule.model.Organization;
import com.appmodz.executionmodule.model.Role;
import com.appmodz.executionmodule.model.User;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    UserDAO userDAO;

    @Autowired
    OrganizationDAO organizationDAO;

    @Autowired
    RoleDAO roleDAO;

    @Autowired
    UtilService utilService;

    public User getUserById(long userId) throws Exception{
        User user = userDAO.get(userId);
        if(user==null)
            throw new Exception("No user with this user id exists");
        else {
            if(!utilService.checkPermission(

                    PermissionDTO.builder()
                            .organizationId(user.getUserOrganization().getOrganizationId())
                            .userId(userId).build()

                    ,"GET_USER"))
                throw new Exception("GET USER ACTION NOT PERMITTED FOR THIS USER");
        }
            return user;
    }

    public User getUserByUsername(String userName) throws Exception{
        User user = userDAO.getByUsername(userName);
        if(user==null)
            throw new Exception("No user with this user id exists");
        else
            return user;
    }

    public User createUser(UserRequestDTO userRequestDTO) throws Exception{
        if(!utilService.checkPermission(
                PermissionDTO.builder()
                        .organizationId(userRequestDTO.getOrganizationId())
                        .build(),"CREATE_USER"))
            throw new Exception("CREATE USER ACTION NOT PERMITTED FOR THIS USER");
        User user = new User();
        user.setUserFirstName(userRequestDTO.getFirstName());
        user.setUserLastName(userRequestDTO.getLastName());
        user.setUserName(userRequestDTO.getUserName());
        user.setUserPhoneNumber(userRequestDTO.getPhoneNumber());
        user.setUserAddress1(userRequestDTO.getAddress1());
        user.setUserAddress2(userRequestDTO.getAddress2());
        user.setUserCountry(userRequestDTO.getCountry());
        user.setUserCountryCode(userRequestDTO.getCountryCode());
        user.setUserEmail(userRequestDTO.getEmailId());
        User checkUser = userDAO.getByUsername(userRequestDTO.getUserName());
        if (checkUser!=null)
            throw new Exception("User with this username already exists");
        user.setUserPasswordHash(userRequestDTO.getPassword());
        Organization organization = organizationDAO.get(userRequestDTO.getOrganizationId());
        if (organization==null)
            throw new Exception("Organization with this id not found");
        Role role = roleDAO.get(userRequestDTO.getRoleId());
        if (role==null)
            throw new Exception("Role with this id not found");
        user.setUserOrganization(organization);
        user.setUserRole(role);
        userDAO.save(user);
        return user;
    }

    public User updateUser(UserRequestDTO userRequestDTO) throws Exception{
        User user = userDAO.get(userRequestDTO.getId());
        if(!utilService.checkPermission(
                PermissionDTO.builder()
                        .organizationId(user.getUserOrganization().getOrganizationId())
                        .userId(user.getUserId()).build()
                ,"UPDATE_USER"))
            throw new Exception("UPDATE USER ACTION NOT PERMITTED FOR THIS USER");
        if(userRequestDTO.getFirstName()!=null)
        user.setUserFirstName(userRequestDTO.getFirstName());
        if(userRequestDTO.getLastName()!=null)
        user.setUserLastName(userRequestDTO.getLastName());
        if(userRequestDTO.getUserName()!=null) {
            User checkUser = userDAO.getByUsername(userRequestDTO.getUserName());
            if (checkUser != null)
                throw new Exception("User with this username already exists");
            user.setUserName(userRequestDTO.getUserName());
        }
        if(userRequestDTO.getPassword()!=null)
        user.setUserPasswordHash(userRequestDTO.getPassword());
        if(userRequestDTO.getOrganizationId()!=null) {
            Organization organization = organizationDAO.get(userRequestDTO.getOrganizationId());
            if (organization == null)
                throw new Exception("Organization with this id not found");
            user.setUserOrganization(organization);
        }
        if(userRequestDTO.getRoleId()!=null) {
            Role role = roleDAO.get(userRequestDTO.getRoleId());
            if (role == null)
                throw new Exception("Role with this id not found");
            user.setUserRole(role);
        }
        if(userRequestDTO.getPhoneNumber()!=null)
        user.setUserPhoneNumber(userRequestDTO.getPhoneNumber());
        if(userRequestDTO.getAddress1()!=null)
        user.setUserAddress1(userRequestDTO.getAddress1());
        if(userRequestDTO.getAddress2()!=null)
        user.setUserAddress2(userRequestDTO.getAddress2());
        if(userRequestDTO.getCountry()!=null)
        user.setUserCountry(userRequestDTO.getCountry());
        if(userRequestDTO.getCountryCode()!=null)
        user.setUserCountryCode(userRequestDTO.getCountryCode());
        if (userRequestDTO.getEmailId()!=null)
        user.setUserEmail(userRequestDTO.getEmailId());
        userDAO.save(user);
        return user;
    }

    public void deleteUser(Long id) throws Exception {
        User user = userDAO.get(id);
        if(!utilService.checkPermission(
                PermissionDTO.builder()
                        .organizationId(user.getUserOrganization().getOrganizationId())
                        .userId(user.getUserId()).build()
                ,"DELETE_USER"))
            throw new Exception("DELETE USER ACTION NOT PERMITTED FOR THIS USER");
        userDAO.delete(user);
    }

    public String deleteMultipleUsers(UserRequestDTO userRequestDTO) throws Exception{
        List<Long> ids = userRequestDTO.getIds();
        StringBuilder exceptions = new StringBuilder();
        StringBuilder successes = new StringBuilder();
        for (long id: ids) {
            User user = userDAO.get(id);
            if(!utilService.checkPermission(
                    PermissionDTO.builder()
                            .organizationId(user.getUserOrganization().getOrganizationId())
                            .userId(user.getUserId()).build()
                    ,"DELETE_USER"))
                exceptions.append("DELETE USER ACTION NOT PERMITTED FOR THIS USER FOR ID").append(id);
            try {
                userDAO.delete(user);
                successes.append("Successfully Deleted ").append(id).append("\n");
            } catch (DataIntegrityViolationException e) {
                e.printStackTrace();
                exceptions.append("Unable To Delete id ").append(id).append(" Due To Possible Foreign Key associations").append("\n");
            }
        }

        if(exceptions.toString().length()>0)
            throw new Exception(exceptions.toString());

        return successes.toString();
    }

    public List listUsers() {
        List<User> users = userDAO.getAll();
        users = users.stream().filter(u->utilService.checkPermission(
                PermissionDTO.builder()
                        .organizationId(u.getUserOrganization().getOrganizationId())
                        .userId(u.getUserId()).build(),
                "GET_USER")).collect(Collectors.toList());
        return users;
    }

    public SearchResultDTO searchUsers(SearchRequestDTO searchRequestDTO) {
        SearchResultDTO users = userDAO.search(searchRequestDTO);
        users.setData((List) users.getData().stream()
                .filter(u->utilService.checkPermission(
                        PermissionDTO.builder()
                                .organizationId(((User) u).getUserOrganization().getOrganizationId())
                                .userId(((User) u).getUserId()).build()
                      ,
                        "GET_USER")).collect(Collectors.toList()));
        return users;
    }

}

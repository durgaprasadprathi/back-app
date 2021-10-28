package com.appmodz.executionmodule.service;

import com.appmodz.executionmodule.dao.*;
import com.appmodz.executionmodule.dto.*;
import com.appmodz.executionmodule.model.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FileUtils;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StackService {

    @Autowired
    StackDAO stackDAO;

    @Autowired
    UserDAO userDAO;

    @Autowired
    WorkspaceDAO workspaceDAO;

    @Autowired
    OrganizationDAO organizationDAO;

    @Autowired
    TerraformBackendDAO terraformBackendDAO;

    @Autowired
    UtilService utilService;

    @Autowired
    private Environment env;

    private Boolean PULUMI = true;

    public Stack getStackById(long stackId) throws Exception{
        Stack stack = stackDAO.get(stackId);
        if(stack==null)
            throw new Exception("No stack with this stack id exists");
        if(!utilService.checkPermission(
                PermissionDTO.builder()
                        .workspaceId(stack.getWorkspace().getWorkspaceId())
                        .organizationId(stack.getWorkspace().getOrganization().getOrganizationId())
                        .userId(stack.getWorkspace().getOwner().getUserId())
                        .build()
                , "GET_STACK"))
            throw new Exception("GET STACK ACTION NOT PERMITTED FOR THIS USER");
        return stack;
    }

    public Stack saveState(CanvasRequestDTO canvasRequestDTO) throws Exception{
        Stack stack = this.getStackById(canvasRequestDTO.getStackId());
        stack.setStackDraftState(canvasRequestDTO.getDraftState());
        stackDAO.save(stack);
        return stack;
    }

    public Stack createStack(StackRequestDTO stackRequestDTO) throws Exception{
        Stack stack = new Stack();
        User user = userDAO.get(stackRequestDTO.getOwnerId());
        Workspace workspace = workspaceDAO.get(stackRequestDTO.getWorkspaceId());
        if(!utilService.checkPermission(
                PermissionDTO.builder()
                        .workspaceId(stackRequestDTO.getWorkspaceId())
                        .organizationId(workspace.getOrganization().getOrganizationId())
                        .userId(stackRequestDTO.getOwnerId())
                        .build()
                , "CREATE_STACK"))
            throw new Exception("CREATE STACK ACTION NOT PERMITTED FOR THIS USER");
        stack.setOwner(user);
        stack.setWorkspace(workspace);
        TerraformBackend terraformBackend = new TerraformBackend();
        terraformBackend.setName(stackRequestDTO.getName());
        terraformBackendDAO.save(terraformBackend);
        stack.setTerraformBackend(terraformBackend);

        stack.setAwsAccessKey(stackRequestDTO.getAwsAccessKey());
        stack.setAwsRegion(stackRequestDTO.getAwsRegion());
        stack.setAwsSecretAccessKey(stackRequestDTO.getAwsSecretAccessKey());

        stackDAO.save(stack);

        if(PULUMI) {

            File workspace_folder = new File(env.getProperty("WORKING_DIR")+stack.getWorkspace().getWorkspaceId());
            if (!workspace_folder.exists()) {
                workspace_folder.mkdir();
            }

            File file = new File(env.getProperty("WORKING_DIR")+stack.getWorkspace().getWorkspaceId()+"/"+stack.getStackId());
            if (!file.exists()) {
                if (file.mkdir()) {


                    StackConfigDTO stackConfigDTO = new StackConfigDTO();
                    stackConfigDTO.setStackName(stack.getTerraformBackend().getName());
                    stackConfigDTO.setProjectName(workspace.getWorkspaceName());
                    stackConfigDTO.setAwsRegion(stack.getAwsRegion());
                    stackConfigDTO.setAwsAccessKey(stack.getAwsAccessKey());
                    stackConfigDTO.setAwsSecretKey(stack.getAwsSecretAccessKey());

                    ObjectMapper mapper = new ObjectMapper();

                    mapper.writeValue(new File(env.getProperty("WORKING_DIR")+stack.getWorkspace().getWorkspaceId()+"/"+stack.getStackId()+"/config.json"),
                            stackConfigDTO);
                    stack.setStackLocation(env.getProperty("WORKING_DIR")+
                            stack.getWorkspace().getWorkspaceId()+"/"+stack.getStackId());
                    stackDAO.save(stack);
                    return stack;
                } else {
                    throw new Exception("Error in folder creation");
                }
            } else {
                throw new Exception("Already Exists");
            }


        } else {
            File workspace_folder = new File(env.getProperty("WORKING_DIR")+stack.getWorkspace().getWorkspaceId());
            if (!workspace_folder.exists()) {
                workspace_folder.mkdir();
            }
            File file = new File(env.getProperty("WORKING_DIR")+stack.getWorkspace().getWorkspaceId()+"/"+stack.getStackId());
            if (!file.exists()) {
                if (file.mkdir()) {

                    File source = new File(env.getProperty("WORKING_DIR")+"basic_template");
                    File dest = new File(env.getProperty("WORKING_DIR")+
                            stack.getWorkspace().getWorkspaceId()+"/"+stack.getStackId());
                    try {
                        FileUtils.copyDirectory(source, dest);
                        stack.setStackLocation(env.getProperty("WORKING_DIR")+
                                stack.getWorkspace().getWorkspaceId()+"/"+stack.getStackId());
                        stackDAO.save(stack);
                        return stack;
                    } catch (Exception e) {
                        e.printStackTrace();
                        throw new Exception("Error in folder creation");
                    }
                } else {
                    throw new Exception("Error in folder creation");
                }
            } else {
                throw new Exception("Already Exists");
            }
        }
    }

    public Stack editStack(StackRequestDTO stackRequestDTO) throws Exception{
        Stack stack = stackDAO.get(stackRequestDTO.getId());
        if(!utilService.checkPermission(
                PermissionDTO.builder()
                        .workspaceId(stack.getWorkspace().getWorkspaceId())
                        .organizationId(stack.getWorkspace().getOrganization().getOrganizationId())
                        .userId(stack.getOwner().getUserId())
                        .build()
                , "UPDATE_STACK"))
            throw new Exception("UPDATE STACK ACTION NOT PERMITTED FOR THIS USER");
        if(stackRequestDTO.getOwnerId()!=null&&stackRequestDTO.getWorkspaceId()!=null) {
            User user = userDAO.get(stackRequestDTO.getOwnerId());
            Workspace workspace = workspaceDAO.get(stackRequestDTO.getWorkspaceId());
            if (user.getUserOrganization().getOrganizationId()!=workspace.getOrganization().getOrganizationId()) {
                throw new Exception("Stack owner belongs to different organization");
            }
            stack.setWorkspace(workspace);
            stack.setOwner(user);
        }
        else if(stackRequestDTO.getOwnerId()!=null) {
            User user = userDAO.get(stackRequestDTO.getOwnerId());
            if (user.getUserOrganization().getOrganizationId()!=stack.getWorkspace().getOrganization().getOrganizationId()) {
                throw new Exception("Stack owner belongs to different organization");
            }
            stack.setOwner(user);
        }
        else if(stackRequestDTO.getWorkspaceId()!=null) {
            Workspace workspace = workspaceDAO.get(stackRequestDTO.getWorkspaceId());
            if (stack.getOwner().getUserOrganization().getOrganizationId()!=stack.getWorkspace().getOrganization().getOrganizationId()) {
                throw new Exception("Stack owner belongs to different organization");
            }
            stack.setWorkspace(workspace);
        }
        if (stackRequestDTO.getTerraformBackendId()!=null){
            TerraformBackend terraformBackend = terraformBackendDAO.get(stackRequestDTO.getTerraformBackendId());
            if(stackRequestDTO.getName()!=null) {
                terraformBackend.setName(stackRequestDTO.getName());
                terraformBackendDAO.save(terraformBackend);
            }

            stack.setTerraformBackend(terraformBackend);
        }
        if(stackRequestDTO.getName()!=null) {
            TerraformBackend terraformBackend = stack.getTerraformBackend();
            terraformBackend.setName(stackRequestDTO.getName());
            terraformBackendDAO.save(terraformBackend);
        }
        if(stackRequestDTO.getAwsAccessKey()!=null)
        stack.setAwsAccessKey(stackRequestDTO.getAwsAccessKey());
        if(stackRequestDTO.getAwsRegion()!=null)
        stack.setAwsRegion(stackRequestDTO.getAwsRegion());
        if(stackRequestDTO.getAwsSecretAccessKey()!=null)
        stack.setAwsSecretAccessKey(stackRequestDTO.getAwsSecretAccessKey());

        stackDAO.save(stack);
        return stack;
    }


    public void deleteStack(Long id) throws Exception{
        Stack stack = stackDAO.get(id);
        if(!utilService.checkPermission(
                PermissionDTO.builder()
                        .workspaceId(stack.getWorkspace().getWorkspaceId())
                        .organizationId(stack.getWorkspace().getOrganization().getOrganizationId())
                        .userId(stack.getOwner().getUserId())
                        .build()
               , "DELETE_STACK"))
            throw new Exception("DELETE STACK ACTION NOT PERMITTED FOR THIS USER");
        stackDAO.delete(stack);
    }

    public String deleteMultipleStacks(StackRequestDTO stackRequestDTO) throws Exception{
        List<Long> ids = stackRequestDTO.getIds();
        StringBuilder exceptions = new StringBuilder();
        StringBuilder successes = new StringBuilder();
        for (long id: ids) {
            Stack stack = stackDAO.get(id);
            if(!utilService.checkPermission(
                    PermissionDTO.builder()
                            .workspaceId(stack.getWorkspace().getWorkspaceId())
                            .organizationId(stack.getWorkspace().getOrganization().getOrganizationId())
                            .userId(stack.getOwner().getUserId())
                            .build()
                    , "DELETE_STACK"))
                exceptions.append("DELETE STACK ACTION NOT PERMITTED FOR THIS USER FOR ID ").append(id);
            try {
                stackDAO.delete(stack);
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

    public List listStacks() {
        List<Stack> stacks = stackDAO.getAll();
        stacks =  stacks.stream().filter(s->utilService.checkPermission(
                PermissionDTO.builder()
                        .organizationId(s.getWorkspace().getOrganization().getOrganizationId())
                        .workspaceId(s.getWorkspace().getWorkspaceId())
                        .userId(s.getOwner().getUserId())
                        .build(),
                "GET_STACK")).collect(Collectors.toList());
        return stacks;
    }

    public List listStacksByWorkspaceId(long workspaceId) {
        List<Stack> stacks = stackDAO.getByWorkspaceId(workspaceId);
        stacks =  stacks.stream().filter(s->utilService.checkPermission(
                PermissionDTO.builder()
                        .organizationId(s.getWorkspace().getOrganization().getOrganizationId())
                        .workspaceId(s.getWorkspace().getWorkspaceId())
                        .userId(s.getOwner().getUserId())
                        .build(),
                "GET_STACK")).collect(Collectors.toList());
        return stacks;
    }

    public SearchResultDTO searchStacks(SearchRequestDTO searchRequestDTO) {
        SearchResultDTO searchResultDTO = stackDAO.search(searchRequestDTO);
        searchResultDTO.setData(
                (List)searchResultDTO.getData().stream().filter(s->utilService.checkPermission(
                        PermissionDTO.builder()
                                .organizationId(((Stack)s).getWorkspace().getOrganization().getOrganizationId())
                                .workspaceId(((Stack)s).getWorkspace().getWorkspaceId())
                                .userId(((Stack)s).getOwner().getUserId())
                                .build()
                        ,
                        "GET_STACK")).collect(Collectors.toList())
        );
        return searchResultDTO;
    }
}

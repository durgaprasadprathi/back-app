package com.appmodz.executionmodule.service;

import com.appmodz.executionmodule.dto.PulumiRequestDTO;
import com.appmodz.executionmodule.dto.StackConfigDTO;
import com.appmodz.executionmodule.model.Stack;
import com.appmodz.executionmodule.util.Processes;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.File;

@Service
public class PulumiService {

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    private Environment env;

    public void workspaceInit(Stack stack) throws Exception{
        File file = new File(env.getProperty("WORKING_DIR")+stack.getWorkspace().getWorkspaceId()+"/"+stack.getStackId());
        FileUtils.cleanDirectory(file);
        File source = new File(env.getProperty("WORKING_DIR")+"pulumi_stack_package.json");
        File dest = new File(env.getProperty("WORKING_DIR")+
                stack.getWorkspace().getWorkspaceId()+"/"+stack.getStackId());
        StackConfigDTO stackConfigDTO = new StackConfigDTO();
        stackConfigDTO.setStackName(stack.getTerraformBackend().getName());
        stackConfigDTO.setProjectName(stack.getWorkspace().getWorkspaceName());
        stackConfigDTO.setAwsRegion(stack.getAwsRegion());
        stackConfigDTO.setAwsAccessKey(stack.getAwsAccessKey());
        stackConfigDTO.setAwsSecretKey(stack.getAwsSecretAccessKey());

        ObjectMapper mapper = new ObjectMapper();

        mapper.writeValue(new File(env.getProperty("WORKING_DIR")+stack.getWorkspace().getWorkspaceId()+"/"+stack.getStackId()+"/config.json"),
                stackConfigDTO);
    }

    public Object pulumiStatewiseMovement(Stack stack) throws Exception {
        this.workspaceInit(stack);
        PulumiRequestDTO pulumiRequestDTO = new PulumiRequestDTO();
        pulumiRequestDTO.setDraftState(stack.getStackDraftState());
        pulumiRequestDTO.setStackPath(stack.getStackLocation());

        String reqUrl = env.getProperty("PULUMI_BASE_URL")+"/pulumi-save";

        System.out.println(reqUrl);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        System.out.println(pulumiRequestDTO.toString());

        HttpEntity<String> entity = new HttpEntity<String>(pulumiRequestDTO.toString(), headers);
        String result = restTemplate.postForObject(reqUrl, entity, String.class);
        return this.pulumiInit(stack);
    };

    private Object pulumiInit(Stack stack) throws Exception{
        PulumiRequestDTO pulumiRequestDTO = new PulumiRequestDTO();
        pulumiRequestDTO.setDraftState(stack.getStackDraftState());
        pulumiRequestDTO.setStackPath(stack.getStackLocation());

        String reqUrl = env.getProperty("PULUMI_BASE_URL")+"/pulumi-init";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<String>(pulumiRequestDTO.toString(), headers);
        String result = restTemplate.postForObject(reqUrl, entity, String.class);
        return result;
    }

    public Object pulumiValidate(Stack stack) throws Exception{
        PulumiRequestDTO pulumiRequestDTO = new PulumiRequestDTO();
        pulumiRequestDTO.setDraftState(stack.getStackDraftState());
        pulumiRequestDTO.setStackPath(stack.getStackLocation());

        String reqUrl = env.getProperty("PULUMI_BASE_URL")+"/pulumi-validate";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<String>(pulumiRequestDTO.toString(), headers);
        String result = restTemplate.postForObject(reqUrl, entity, String.class);
        return result;
    }

    public Object pulumiPreview(Stack stack) throws Exception{
        PulumiRequestDTO pulumiRequestDTO = new PulumiRequestDTO();
        pulumiRequestDTO.setDraftState(stack.getStackDraftState());
        pulumiRequestDTO.setStackPath(stack.getStackLocation());

        String reqUrl = env.getProperty("PULUMI_BASE_URL")+"/pulumi-preview";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<String>(pulumiRequestDTO.toString(), headers);
        String result = restTemplate.postForObject(reqUrl, entity, String.class);
        return result;
    }

    public Object pulumiUp(Stack stack) throws Exception{
        PulumiRequestDTO pulumiRequestDTO = new PulumiRequestDTO();
        pulumiRequestDTO.setDraftState(stack.getStackDraftState());
        pulumiRequestDTO.setStackPath(stack.getStackLocation());

        String reqUrl = env.getProperty("PULUMI_BASE_URL")+"/pulumi-up";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<String>(pulumiRequestDTO.toString(), headers);
        String result = restTemplate.postForObject(reqUrl, entity, String.class);
        return result;
    }

    public Object pulumiDestroy(Stack stack) throws Exception{
        PulumiRequestDTO pulumiRequestDTO = new PulumiRequestDTO();
        pulumiRequestDTO.setDraftState(stack.getStackDraftState());
        pulumiRequestDTO.setStackPath(stack.getStackLocation());

        String reqUrl = env.getProperty("PULUMI_BASE_URL")+"/pulumi-destroy";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<String>(pulumiRequestDTO.toString(), headers);
        String result = restTemplate.postForObject(reqUrl, entity, String.class);
        return result;
    }
}

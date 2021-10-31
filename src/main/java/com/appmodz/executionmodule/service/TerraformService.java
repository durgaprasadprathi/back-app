package com.appmodz.executionmodule.service;

import com.appmodz.executionmodule.dao.ComponentDAO;
import com.appmodz.executionmodule.dao.PropertyDAO;
import com.appmodz.executionmodule.dao.StackDAO;
import com.appmodz.executionmodule.model.Component;
import com.appmodz.executionmodule.model.Property;
import com.appmodz.executionmodule.model.Stack;
import com.appmodz.executionmodule.util.Processes;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Service
public class TerraformService {

    @Autowired
    StackDAO stackDAO;

    @Autowired
    ComponentDAO componentDAO;

    @Autowired
    PropertyDAO propertyDAO;

    @Autowired
    private Environment env;

    public static void copyFolderContents(String source, String dest) throws Exception{
        File sourceFile = new File(source);
        File destFile = new File(dest);
        try {
            FileUtils.copyDirectory(sourceFile, destFile);

        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Error in folder creation");
        }
    }

    public void copyModule(Stack stack, String componentPath) throws Exception{
        String moduleTemplatePath = env.getProperty("WORKING_DIR")+componentPath;
        String workspaceModuleTemplatePath = env.getProperty("WORKING_DIR")+stack.getWorkspace()
                .getWorkspaceId()+"/"+stack.getStackId()+"/"+componentPath;
        Path path = Paths.get(workspaceModuleTemplatePath);
        Files.createDirectories(path);
        copyFolderContents(moduleTemplatePath,workspaceModuleTemplatePath);
    }

    public void writeVariables(Stack stack, String variables) throws Exception{
        File tfVarsfile = new File(env.getProperty("WORKING_DIR")+stack.getWorkspace().getWorkspaceId()+"/"+stack.getStackId()+"/terraform.tfvars");
        FileWriter variablesWriter = new FileWriter(tfVarsfile, false);
        variablesWriter.write(variables);
        variablesWriter.close();
    }

    public void workspaceInit(Stack stack) throws Exception{
        File file = new File(env.getProperty("WORKING_DIR")+stack.getWorkspace().getWorkspaceId()+"/"+stack.getStackId());
        FileUtils.cleanDirectory(file);
        File source = new File(env.getProperty("WORKING_DIR")+"basic_template");
        File dest = new File(env.getProperty("WORKING_DIR")+stack.getWorkspace().getWorkspaceId()+"/"+stack.getStackId());
        FileUtils.copyDirectory(source, dest);
    }

    public void modifyMainTfAndVariables(Stack stack,
                                    HashMap<Long,JsonNode> idToJsonMap,
                                    HashMap<Long,Component> idToComponentMap,
                                   HashMap<Long,JsonNode> componentJSONMap,
                                   JsonNode connections
                                   ) throws Exception {

        HashMap<Long,String> connectionVariables = new HashMap<Long,String>();

        for(JsonNode objNode: connections) {
            long to = objNode.get("to").longValue();
            long from = objNode.get("from").longValue();
            String toPort = objNode.get("toPort").asText();
            String toVariable = toPort.split("-")[3];
            Component toComponent = idToComponentMap.get(to);
            Component fromComponent = idToComponentMap.get(from);
            String connection = toVariable+" = "+"module."+fromComponent.getResourceName()+"."+toVariable;
            connectionVariables.put(to,connection);
        }


        StringBuilder maintfStr = new StringBuilder("");

        StringBuilder mainVariablesStr = new StringBuilder("");

        HashSet<String> vars = new HashSet<>();

        for (Map.Entry mapElement : componentJSONMap.entrySet()) {
            Long key = (Long)mapElement.getKey();
            JsonNode objNode = idToJsonMap.get(key);
            System.out.println(objNode);
            Component component = componentDAO.get(objNode.get("componentId").asLong());
            StringBuilder moduleStr = new StringBuilder(System.getProperty("line.separator")
                    +System.getProperty("line.separator")+
                    "module "+
                    "\""+component.getResourceName()+"\" "+"{"+System.getProperty("line.separator"));
            moduleStr.append("source = \"" + "./").append(component.getPath()).append("\"").append(System.getProperty("line.separator"));
            List<Property> properties = component.getProperties();
            for (Property property: properties) {
                if(property.getPropertyRootName()!=null) {
                    if(!vars.contains(property.getPropertyRootName())) {
                        moduleStr.append(property.getName()).append(" = ").append("var.").append(property.getPropertyRootName()).append(System.getProperty("line.separator"));
                        mainVariablesStr.append("variable " + "\"").append(property.getPropertyRootName())
                                .append("\"").append("{").append(System.getProperty("line.separator"))
                                .append("type = ").append(property.getType()).append(System.getProperty("line.separator"))
                                .append("}").append(System.getProperty("line.separator"))
                                .append(System.getProperty("line.separator"));
                        vars.add(property.getPropertyRootName());
                    }
                } else {
                    String conn = connectionVariables.get(key);
                    moduleStr.append(conn).append(System.getProperty("line.separator"));
                }
            }
            moduleStr.append("}").append(System.getProperty("line.separator")).append(System.getProperty("line.separator"));

            maintfStr.append(moduleStr);
        }


        File mainTfFile = new File(env.getProperty("WORKING_DIR")+stack.getWorkspace().getWorkspaceId()+"/"+stack.getStackId()+"/main.tf");
        FileWriter mainTfWriter = new FileWriter(mainTfFile, true);
        mainTfWriter.write(maintfStr.toString());
        mainTfWriter.close();

        File variableTfFile = new File(env.getProperty("WORKING_DIR")+stack.getWorkspace().getWorkspaceId()+"/"+stack.getStackId()+"/variables.tf");
        FileWriter variableTfWriter = new FileWriter(variableTfFile, true);
        variableTfWriter.write(mainVariablesStr.toString());
        variableTfWriter.close();

    }

    public Object terraformStateWiseMovement(Stack stack) throws Exception{
            workspaceInit(stack);
            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonNode = mapper.valueToTree(stack.getStackDraftState());
            JsonNode canvasState = jsonNode.get("canvasState");
            JsonNode variables = jsonNode.get("properties");
            JsonNode connections = jsonNode.get("connections");
            HashMap<Long,JsonNode> idToJsonMap=new HashMap<Long,JsonNode>();
            HashMap<Long,Component> idToComponentMap=new HashMap<Long,Component>();
            HashMap<Long,JsonNode> componentJSONMap=new HashMap<Long,JsonNode>();
            HashMap<Long,Component> componentObjMap=new HashMap<Long,Component>();
            if(!canvasState.isArray())
                throw new Exception("CanvasState Is Not An Array");

            for (final JsonNode objNode : canvasState) {
                long componentId = objNode.get("componentId").asLong();
                Component component = componentDAO.get(componentId);
                idToJsonMap.put(objNode.get("key").asLong(),objNode);
                idToComponentMap.put(objNode.get("key").asLong(),component);
                if(!componentObjMap.containsKey(componentId)) {
                    copyModule(stack,component.getPath());
                    componentJSONMap.put(objNode.get("key").asLong(),objNode);
                    componentObjMap.put(componentId,component);
                }
            }

            if(!connections.isArray())
                throw new Exception("Connections is not an array");

            modifyMainTfAndVariables(stack,idToJsonMap,
                    idToComponentMap,componentJSONMap,connections);

            if(!variables.isArray())
                throw new Exception("Variables is not an array");

            StringBuilder variableStr = new StringBuilder("");
            variableStr.append("aws_region").append(" = ").append("\"").append(stack.getAwsRegion())
                    .append("\"").append(System.getProperty("line.separator"));
            variableStr.append("access_key").append(" = ").append("\"").append(stack.getAwsAccessKey())
                    .append("\"").append(System.getProperty("line.separator"));
            variableStr.append("secret_key").append(" = ").append("\"").append(stack.getAwsSecretAccessKey())
                    .append("\"").append(System.getProperty("line.separator"));
            variableStr.append("workspace_name").append(" = ").append("\"").append(stack.getTerraformBackend().getName())
                    .append("\"").append(System.getProperty("line.separator"));

            HashMap<String,String> variableMap=new HashMap<String,String>();

            for (final JsonNode objNode: variables) {

                JsonNode properties = objNode.get("properties");
                Iterator<String> fieldNames = properties.fieldNames();

                while(fieldNames.hasNext()) {
                    String fieldName = fieldNames.next();
                    String fieldValue = properties.get(fieldName).textValue();
                    Property property = propertyDAO.getByName(fieldName);
                    String rootName = property.getPropertyRootName();
                    if(rootName != null) {
                        if(property.getIsMultiValuable()) {
                            if(variableMap.containsKey(rootName)) {
                                String value = variableMap.get(rootName);
                                value = value +","+fieldValue;
                                variableMap.put(rootName,value);
                            } else {
                                variableMap.put(rootName,fieldValue);
                            }
                        } else {
                            variableMap.put(rootName,fieldValue);
                        }
                    }
                }

            }

            for (Map.Entry mapElement : variableMap.entrySet()) {
                String variable = (String) mapElement.getKey();
                variableStr.append(variable).append(" = ").append("\"").append(variableMap.get(variable))
                        .append("\"").append(System.getProperty("line.separator"));
            }

            writeVariables(stack,variableStr.toString());
            stack.setStackState(stack.getStackDraftState());
            stackDAO.save(stack);
            return terraformInit(stack);
    }

    public Object terraformInit(Stack stack) throws Exception{
        Processes processes = new Processes();
        String response = processes.run(env.getProperty("WORKING_DIR")+stack.getWorkspace().getWorkspaceId()+"/"+stack.getStackId(),
                "terraform","init","-backend-config" ,"conn_str="+env.getProperty("TERRAFORM_POSTGRES_CONN_STR"));
        return response;
    }

    public Object terraformValidate(Stack stack) throws Exception{
        Processes processes = new Processes();
        String response = processes.run(env.getProperty("WORKING_DIR")+stack.getWorkspace().getWorkspaceId()+"/"+stack.getStackId(),"terraform","validate","-json");
        return response;
    }

    public Object terraformPlan(Stack stack) throws Exception{
        Processes processes = new Processes();
        String response = processes.run(env.getProperty("WORKING_DIR")+stack.getWorkspace().getWorkspaceId()+"/"+stack.getStackId(),"terraform","plan","-json");
        return response;
    }

    public Object terraformApply(Stack stack) throws Exception{
        Processes processes = new Processes();
        String response = processes.run(env.getProperty("WORKING_DIR")+stack.getWorkspace().getWorkspaceId()+"/"+stack.getStackId(),"terraform","apply","-json","-auto-approve");
        return response;
    }
}

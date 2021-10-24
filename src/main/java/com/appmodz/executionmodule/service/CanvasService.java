package com.appmodz.executionmodule.service;

import com.appmodz.executionmodule.dao.ComponentDAO;
import com.appmodz.executionmodule.dto.CanvasComponent;
import com.appmodz.executionmodule.dto.ComponentProperty;
import com.appmodz.executionmodule.dto.FileOrFolder;
import com.appmodz.executionmodule.model.Component;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Service
public class CanvasService {

    @Autowired
    ComponentDAO componentDAO;

    @Autowired
    private Environment env;

    public List listComponents() {
        List<Component> parents= componentDAO.getParents();
        List <CanvasComponent> canvasComponents = new ArrayList<>();
        for(Component parent: parents) {
            CanvasComponent parentCanvasComponent = new CanvasComponent();
            parentCanvasComponent.setId(parent.getId());
            parentCanvasComponent.setName(parent.getName());
            parentCanvasComponent.setChildren(componentDAO.getComponentsByParentId(parent.getId()));
            canvasComponents.add(parentCanvasComponent);
        }
        return canvasComponents;
    }

    private static FileOrFolder traverse(File file) {
        if(!file.exists())
            return null;
        FileOrFolder fileOrFolder = new FileOrFolder();
        fileOrFolder.setName(file.getName());
        if (file.isDirectory()) {
            fileOrFolder.setIsFile(false);
            File[] children = file.listFiles();
            List<FileOrFolder> data = new ArrayList<>();
            assert children != null;
            for (File child : children) {
                data.add(traverse(child));
            }
            fileOrFolder.setData(data);
        }else {
            fileOrFolder.setIsFile(true);
        }
        return fileOrFolder;
    }

    private static String readFile(String path, Charset encoding)
            throws IOException
    {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }

    public FileOrFolder getFolderStructure(String path) throws Exception {
        System.out.println(path);
        System.out.println(env.getProperty("WORKING_DIR")+path);
        File file = new File(env.getProperty("WORKING_DIR")+path);
        if(!file.exists())
            throw new Exception("File or Folder Not Present");
        return traverse(file);
    }

    public String getFileContent(String path) throws Exception {
        File file = new File(env.getProperty("WORKING_DIR")+path);
        if(!file.exists())
            throw new Exception("File or Folder Not Present");
        return readFile(env.getProperty("WORKING_DIR")+path, StandardCharsets.UTF_8);
    }

}

package com.appmodz.executionmodule.controller.v1;

import com.appmodz.executionmodule.dto.CanvasRequestDTO;
import com.appmodz.executionmodule.dto.FileOrFolder;
import com.appmodz.executionmodule.dto.OrganizationRequestDTO;
import com.appmodz.executionmodule.dto.ResponseDTO;
import com.appmodz.executionmodule.model.Stack;
import com.appmodz.executionmodule.service.CanvasService;
import com.appmodz.executionmodule.service.PulumiService;
import com.appmodz.executionmodule.service.StackService;
import com.appmodz.executionmodule.service.TerraformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController("v1CanvasController")
@RequestMapping("/v1/canvas")
public class CanvasController {

    @Autowired
    CanvasService canvasService;

    @Autowired
    StackService stackService;

    @Autowired
    TerraformService terraformService;

    @Autowired
    PulumiService pulumiService;

    private Boolean PULUMI = true;

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public Object handle(Exception ex, HttpServletRequest request) {
        ResponseDTO responseDTO = new ResponseDTO("failure",ex.getMessage(),null);
        return responseDTO;
    }

    @RequestMapping(value="/components",method= RequestMethod.GET, produces="application/json")
    @ResponseBody
    public Object getComponents() throws Exception{
        return new ResponseDTO("success",null,
                canvasService.listComponents());
    }


    @RequestMapping(value="/code/folder",method= RequestMethod.POST, produces="application/json")
    @ResponseBody
    public Object getFolderStructure(@RequestBody(required = false) CanvasRequestDTO canvasRequestDTO) throws Exception{
        Stack stack = stackService.getStackById(canvasRequestDTO.getStackId());
        String path = ""+stack.getWorkspace().getWorkspaceId()+"/"+stack.getStackId()+canvasRequestDTO.getPath();
        return new ResponseDTO("success",null,
                canvasService.getFolderStructure(path));
    }

    @RequestMapping(value="/code/file",method= RequestMethod.POST, produces="application/json")
    @ResponseBody
    public Object getFileContents(@RequestBody(required = false) CanvasRequestDTO canvasRequestDTO) throws Exception{
        Stack stack = stackService.getStackById(canvasRequestDTO.getStackId());
        String path = ""+stack.getWorkspace().getWorkspaceId()+"/"+stack.getStackId()+canvasRequestDTO.getPath();
        return new ResponseDTO("success",null,
                canvasService.getFileContent(path));
    }

    @RequestMapping(value="/save",method= RequestMethod.POST, produces="application/json")
    @ResponseBody
    public Object saveDraftState(@RequestBody(required = false) CanvasRequestDTO canvasRequestDTO) throws Exception{
        if(canvasRequestDTO.getIsDraft() == null || canvasRequestDTO.getIsDraft())
        return new ResponseDTO("success",null,
                stackService.saveState(canvasRequestDTO));
        else {
            Stack stack = stackService.getStackById(canvasRequestDTO.getStackId());
            if(PULUMI)
                return pulumiService.pulumiStatewiseMovement(stack);
            return new ResponseDTO("success", null,
                    terraformService.terraformStateWiseMovement(stack));
        }

    }

    @RequestMapping(value="/validate",method= RequestMethod.POST, produces="application/json")
    @ResponseBody
    public Object validate(@RequestBody(required = false) CanvasRequestDTO canvasRequestDTO) throws Exception{
             Stack stack = stackService.getStackById(canvasRequestDTO.getStackId());
            if(PULUMI)
                return pulumiService.pulumiValidate(stack);
            return new ResponseDTO("success",null,
                    terraformService.terraformValidate(stack));

    }

    @RequestMapping(value="/plan",method= RequestMethod.POST, produces="application/json")
    @ResponseBody
    public Object plan(@RequestBody(required = false) CanvasRequestDTO canvasRequestDTO) throws Exception{
        Stack stack = stackService.getStackById(canvasRequestDTO.getStackId());
        if(PULUMI)
            return pulumiService.pulumiPreview(stack);
        return new ResponseDTO("success",null,
                terraformService.terraformPlan(stack));

    }

    @RequestMapping(value="/publish",method= RequestMethod.POST, produces="application/json")
    @ResponseBody
    public Object publish(@RequestBody(required = false) CanvasRequestDTO canvasRequestDTO) throws Exception{
        Stack stack = stackService.getStackById(canvasRequestDTO.getStackId());
        if(PULUMI)
            return pulumiService.pulumiUp(stack);
        return new ResponseDTO("success",null,
                terraformService.terraformApply(stack));

    }
}

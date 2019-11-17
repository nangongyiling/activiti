package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;

import org.activiti.bpmn.model.BpmnModel;
import org.activiti.engine.HistoryService;
import org.activiti.engine.IdentityService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;  
import org.activiti.engine.TaskService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.history.HistoricActivityInstance;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.image.ProcessDiagramGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.demo.service.FlowService;

@Controller

public class TestController {
	
	private Logger log = LoggerFactory.getLogger(FlowService.class);
	
	
	@Autowired
	private FlowService flowService;
	@Autowired
	private ProcessEngine processEngine;
	@Autowired  
	private RuntimeService runtimeService;  
	@Autowired  
	private TaskService taskService;  
	@Autowired  
	private HistoryService historyService;  
	@Autowired  
	private RepositoryService repositoryService;  
	@Autowired  
	private ProcessEngineConfigurationImpl processEngineConfiguration; 

	@RequestMapping("/page")
	@ResponseBody
	public String test() {
		return "test";
	}
	
	@RequestMapping("/startFlow")
	@ResponseBody
	public String startFlow() {
		flowService.startFlow(null);
		return "ok";
	}
	
	@RequestMapping("/edit")
	@ResponseBody
	public String edit(String key) {
		flowService.finishTask(key,"edit",new HashMap<>());
		return "edit";
	}
	
	@RequestMapping("/review")
	@ResponseBody
	public String review(String key,int ok) {
		Map<String,Object> map = new HashMap<>();
		map.put("ok",ok);
		flowService.finishTask(key,"review",map);
		return "review";
	}
	
	public void queryProImg(String processInstanceId) throws Exception {
		//获取历史流程实例  
        HistoricProcessInstance processInstance =  historyService.createHistoricProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();  
       
		//根据流程定义获取输入流
		InputStream is = repositoryService.getProcessDiagram(processInstance.getProcessDefinitionId());
		BufferedImage bi = ImageIO.read(is);
		File file = new File("demo2.png");
		if(!file.exists()) file.createNewFile();
		FileOutputStream fos = new FileOutputStream(file);
		ImageIO.write(bi, "png", fos);
		fos.close();
		is.close();
		System.out.println("图片生成成功");
 
		List<Task> tasks = taskService.createTaskQuery().taskCandidateUser("userId").list();
		for(Task t : tasks) {
			System.out.println(t.getName());
		}
	}
	
	
	@RequestMapping(value = "/image", method = RequestMethod.GET)
    public void image(HttpServletResponse response,
     @RequestParam String processInstanceId) {
        try {
            InputStream is = getDiagram(processInstanceId);
            if (is == null)
                return;

            response.setContentType("image/png");

            BufferedImage image = ImageIO.read(is);
            OutputStream out = response.getOutputStream();
            ImageIO.write(image, "png", out);

            is.close();
            out.close();
        } catch (Exception ex) {
            log.error("查看流程图失败", ex);
        }
    }
 	 
   
	public InputStream getDiagram(String processInstanceId) {
	    //获得流程实例
	    ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
	            .processInstanceId(processInstanceId).singleResult();
	    String processDefinitionId ="";
	    if (processInstance == null) {
	        //查询已经结束的流程实例
	        HistoricProcessInstance processInstanceHistory =
	                historyService.createHistoricProcessInstanceQuery()
	                        .processInstanceId(processInstanceId).singleResult();
	        if (processInstanceHistory == null)
	            return null;
	        else
	            processDefinitionId = processInstanceHistory.getProcessDefinitionId();
	    } else {
	        processDefinitionId = processInstance.getProcessDefinitionId();
	    }

	    //使用宋体
	    String fontName = "宋体";
	    //获取BPMN模型对象
	    BpmnModel model = repositoryService.getBpmnModel(processDefinitionId);
	    //获取流程实例当前的节点，需要高亮显示
	    List<String> currentActs = Collections.EMPTY_LIST;
	    if (processInstance != null)
	        currentActs = runtimeService.getActiveActivityIds(processInstance.getId());

	    return processEngine.getProcessEngineConfiguration()
	            .getProcessDiagramGenerator()
	            .generateDiagram(model, "png", currentActs, new ArrayList<String>(),
	                    fontName, fontName, fontName, null, 1.0);
	}

}

package com.example.demo.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Resource;

import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.activiti.engine.task.TaskQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FlowService {

	private Logger logger = LoggerFactory.getLogger(FlowService.class);
	
	@Autowired
	protected RuntimeService runtimeService;
	
	@Autowired
	protected TaskService taskService;
	
	public void start(DelegateExecution execution ) {
		logger.info("初始化模板成功");
	}
	
	public void upload(DelegateExecution execution ) {
		logger.info("上传至国家专利系统成功");
	}
	
	public void startFlow(Map<String,Object> map) {
		String uuid = UUID.randomUUID().toString();


		uuid = uuid.replace("-", "");

		ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("myProcess",uuid,map);
	}
	
	public void finishTask(String key,String taskKey,Map<String,Object> map) {
		List<ProcessInstance> list = runtimeService.createProcessInstanceQuery().processInstanceBusinessKey(key).processDefinitionKey("myProcess").active().list();
		
		TaskQuery taskQuery = taskService.createTaskQuery();
		taskQuery.taskDefinitionKey(taskKey).active();
		taskQuery.processInstanceId(list.get(0).getProcessInstanceId());
		Task task = taskQuery.singleResult();
		taskService.complete(task.getId(), map);
	}
	
	public void completeTask(String taskId,String userId,String result) {
		//获取流程实例
		taskService.claim(taskId, userId);
		
		Map<String,Object> vars = new HashMap<String,Object>();
		vars.put("sign", "true");
		taskService.complete(taskId, vars);
	}

}

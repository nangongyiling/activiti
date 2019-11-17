//package com.example.demo;
//
//import java.io.IOException;
//
//import javax.sql.DataSource;
//
//import org.activiti.spring.SpringProcessEngineConfiguration;
//import org.activiti.spring.autodeployment.SingleResourceAutoDeploymentStrategy;
//import org.springframework.context.annotation.Bean;
//import org.springframework.core.io.Resource;
//import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
//import org.springframework.core.io.support.ResourcePatternResolver;
//import org.springframework.transaction.PlatformTransactionManager;
//
//public class ActivitiConfig {
//
//	@Bean
//	public SpringProcessEngineConfiguration processEngineConfiguration(
//			DataSource dataSource,PlatformTransactionManager transactionManager)throws IOException {
//		SpringProcessEngineConfiguration configuration = new SpringProcessEngineConfiguration();
//		configuration.setDataSource(dataSource);
//        configuration.setDatabaseType("mysql");
//        configuration.setTransactionManager(transactionManager);
//        configuration.setDatabaseSchemaUpdate(SpringProcessEngineConfiguration.DB_SCHEMA_UPDATE_TRUE);
//        configuration.setAsyncExecutorMessageQueueMode(true);
//        configuration.setAsyncExecutorActivate(false);
//        configuration.setJobManager(null);
//        configuration.setCreateDiagramOnDeploy(true);
//        configuration.setActivityFontName("宋体");
//        configuration.setAnnotationFontName("宋体");
//        configuration.setLabelFontName("宋体");
//        configuration.setActiviti5CompatibilityEnabled(false);
//
//        configuration.setDeploymentMode(SingleResourceAutoDeploymentStrategy.DEPLOYMENT_MODE);
//        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
//        Resource[] resources = resolver.getResources("classpath*:processes/*.bpmn");
//        configuration.setDeploymentResources(resources);
//        /*
//         * 扩展异常重试逻辑
//         */
//        configuration.setFailedJobCommandFactory(null);
//
//		return configuration;
//	}
//}

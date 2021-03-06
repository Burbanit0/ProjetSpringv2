package org.rygn.kanban;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.util.Collection;

import org.hamcrest.collection.IsCollectionWithSize;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.rygn.kanban.domain.Developer;
import org.rygn.kanban.domain.Task;
import org.rygn.kanban.service.DeveloperService;
import org.rygn.kanban.service.TaskService;
import org.rygn.kanban.utils.Constants;
import org.rygn.kanban.utils.TaskMoveAction;
import org.rygn.kanban.utils.ValidationError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@RunWith(SpringRunner.class)
@ActiveProfiles(profiles = "test")
@AutoConfigureMockMvc
public class TaskControllerTest {
	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private DeveloperService developerService;
	
	@Autowired
	private TaskService taskService;
	
	@Test
	public void testGetAllTasks() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/tasks")
								.accept(MediaType.APPLICATION_JSON))
								.andExpect(status().isOk());
	}
	
	@Test
	public void testNewTask() throws Exception {
		Developer developer = this.developerService.findAllDevelopers().iterator().next();
		
		Task task = new Task();
		task.setTitle("task2");
		task.setNbHoursForecast(0);
		task.setNbHoursReal(0);
		task.setType(this.taskService.findTaskType(Constants.TASK_TYPE_BUG_ID));
		task.addDeveloper(developer);
		
		ObjectMapper mapper = new ObjectMapper();
		byte[] taskAsBytes = mapper.writeValueAsBytes(task);
		
		mockMvc.perform(MockMvcRequestBuilders.post("/tasks")
								.contentType(MediaType.APPLICATION_JSON).content(taskAsBytes))				
							    .andExpect(status().isOk())
							    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
			    ;
		Collection<Task> tasks = this.taskService.findAllTasks();
		Assert.assertEquals(2, tasks.size());
		
		boolean found = false;
		
		for (Task currentTask : tasks) {
			if (currentTask.getTitle().equals("task2")) {
				found = true;
				Assert.assertEquals(Constants.TASK_STATUS_TODO_LABEL, currentTask.getStatus().getLabel());
				this.taskService.deleteTask(currentTask);
			}
		}
		Assert.assertTrue(found);
	}
	
	@Test
	public void testNewTaskWithError() throws Exception {
		
		Developer developer = this.developerService.findAllDevelopers().iterator().next();
		Task task = new Task();
		task.setTitle("");
		task.setNbHoursForecast(150);
		task.setNbHoursReal(0);
		task.setType(this.taskService.findTaskType(Constants.TASK_TYPE_BUG_ID));
		task.addDeveloper(developer);
		
		ObjectMapper mapper = new ObjectMapper();
        byte[] taskAsBytes = mapper.writeValueAsBytes(task);
        
        mockMvc.perform(MockMvcRequestBuilders.post("/tasks")
				.contentType(MediaType.APPLICATION_JSON).content(taskAsBytes))				
			    .andExpect(status().is(400))
			    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
			    
        ;
        
        Collection<Task> tasks = this.taskService.findAllTasks();
		Assert.assertEquals(1, tasks.size());
		
	}
	
	@Test
	public void testMoveTask() throws Exception {
Developer developer = this.developerService.findAllDevelopers().iterator().next();
		
		Task task = new Task();
		task.setTitle("task2");
		task.setNbHoursForecast(0);
		task.setNbHoursReal(0);
		task.setType(this.taskService.findTaskType(Constants.TASK_TYPE_BUG_ID));
		task.addDeveloper(developer);
		
		task = this.taskService.createTask(task);
		
		Collection<Task> tasks = this.taskService.findAllTasks();
		
		for (Task currentTask : tasks) {
			
			if (currentTask.getTitle().equals("task2")) {

				ObjectMapper mapper = new ObjectMapper();
				
				TaskMoveAction moveRight = new TaskMoveAction();
				moveRight.setAction(Constants.MOVE_RIGHT_ACTION);
								
		        byte[] moveRightAsBytes = mapper.writeValueAsBytes(moveRight);
				
		        mockMvc.perform(MockMvcRequestBuilders.patch("/tasks/" + currentTask.getId())
						.contentType(MediaType.APPLICATION_JSON).content(moveRightAsBytes))				
					    .andExpect(status().isOk())
					    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
					    ;
				
				task = this.taskService.findTask(currentTask.getId());
				
				Assert.assertEquals(Constants.TASK_STATUS_DOING_LABEL, task.getStatus().getLabel());
				
				TaskMoveAction moveLeft = new TaskMoveAction();
				moveLeft.setAction(Constants.MOVE_LEFT_ACTION);
				
				byte[] moveLeftAsBytes = mapper.writeValueAsBytes(moveLeft);
				
				mockMvc.perform(MockMvcRequestBuilders.patch("/tasks/" + currentTask.getId())
						.contentType(MediaType.APPLICATION_JSON).content(moveLeftAsBytes))				
					    .andExpect(status().isOk())
					    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
					    ;
				
				task = this.taskService.findTask(currentTask.getId());
				
				Assert.assertEquals(Constants.TASK_STATUS_TODO_LABEL, task.getStatus().getLabel());
				
				this.taskService.deleteTask(currentTask);
			}
		}
	}
}


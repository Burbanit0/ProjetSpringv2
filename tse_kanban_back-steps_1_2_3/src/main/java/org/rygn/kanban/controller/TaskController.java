package org.rygn.kanban.controller;

import java.util.Collection;

import javax.validation.Valid;

import org.rygn.kanban.dao.TaskRepository;
import org.rygn.kanban.domain.Task;
import org.rygn.kanban.service.TaskService;
import org.rygn.kanban.utils.Constants;
import org.rygn.kanban.utils.TaskMoveAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TaskController {
	@Autowired
	private TaskService taskService;
	
	@GetMapping("/tasks")
	Collection<Task> findAllTasks() {
		return this.taskService.findAllTasks();
	}
	
	@PostMapping("/tasks")
	Task newTask(@Valid @RequestBody Task newTask) {
		return this.taskService.createTask(newTask);
	} 
	
	@PatchMapping(path = "/tasks/{id}")
	Task movetask(@RequestBody TaskMoveAction taskMoveAction, @PathVariable Long id) {
		Task task = this.taskService.findTask(id);
		if (Constants.MOVE_LEFT_ACTION.equals(taskMoveAction.getAction())) { 
			task = this.taskService.moveLeftTask(task);
		}
		else if (Constants.MOVE_RIGHT_ACTION.equals(taskMoveAction.getAction())) {
			task = this.taskService.moveRightTask(task);
		}
		else {
			throw new IllegalStateException();
		}
		return task;
	}
}

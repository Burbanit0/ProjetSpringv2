package org.rygn.kanban.controller;

import java.util.Collection;

import org.rygn.kanban.dao.TaskRepository;
import org.rygn.kanban.domain.Task;
import org.rygn.kanban.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TaskController {
	@Autowired
	private TaskService taskService;
	
	@Autowired
	private TaskRepository taskRepository;
	
	@GetMapping("/tasks")
	Collection<Task> findAllTasks() {
		return taskService.findAllTasks();
	}
	
	@PostMapping("/tasks")
	Task newTask(@RequestBody Task newTask) {
		return taskRepository.save(newTask);
	}
	
	@PatchMapping(path = "/tasks/{id}", consumes = "application/json-patch+json")
	public ResponseEntity<Task> updateTask(@PathVariable Long id, @RequestBody JsonPatch patch) {
		try {
			Task task = 
		}
	}
	
	
}

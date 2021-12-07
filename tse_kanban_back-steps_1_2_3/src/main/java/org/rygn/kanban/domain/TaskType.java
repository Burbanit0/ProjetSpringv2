package org.rygn.kanban.domain;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
@Entity
public class TaskType {

	@NotNull(message = "Type cannot be empty")
	private @Id Long id;
	
	private String label;
	
	public TaskType(Long id, String label) {
		this.id = id;
		this.label = label;
	}
	
	public TaskType() {
	}
}

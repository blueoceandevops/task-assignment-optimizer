package domain;

import java.io.Serializable;
import java.util.HashSet;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.AnchorShadowVariable;
import org.optaplanner.core.api.domain.variable.CustomShadowVariable;
import org.optaplanner.core.api.domain.variable.PlanningVariable;
import org.optaplanner.core.api.domain.variable.PlanningVariableGraphType;
import org.optaplanner.core.api.domain.variable.PlanningVariableReference;

import domain.solver.StartTimeUpdatingVariableListener;

@PlanningEntity
public class Task extends TaskOrEmployee implements Serializable {

	private static final long serialVersionUID = 1L;

	@PlanningVariable(valueRangeProviderRefs = { "employeeRange",
			"taskRange" }, graphType = PlanningVariableGraphType.CHAINED)
	private TaskOrEmployee previousTaskOrEmployee;

	@AnchorShadowVariable(sourceVariableName = "previousTaskOrEmployee")
	private Employee employee;

	private TaskType taskType;

	@CustomShadowVariable(variableListenerClass = StartTimeUpdatingVariableListener.class,
			// Arguable, to adhere to API specs (although this works), nextTask and employee
			// should also be a source,
			// because this shadow must be triggered after nextTask and employee (but there
			// is no need to be triggered by those)
			sources = { @PlanningVariableReference(variableName = "previousTaskOrEmployee") })
	private Integer startTime; // In minutes

	public Task() {
	}
	
	public Task(String id, int priority, int duration, HashSet<Skill> skills) {
		taskType = new TaskType();
		taskType.setId(id);
		taskType.setPriority(Priority.fromValue(priority));
		taskType.setDuration(duration);
		taskType.setRequiredSkillList(skills);
	}

	// ************************************************************************
	// Getters / Setters
	// ************************************************************************

	public TaskOrEmployee getPreviousTaskOrEmployee() {
		return previousTaskOrEmployee;
	}

	public void setPreviousTaskOrEmployee(TaskOrEmployee previousTaskOrEmployee) {
		this.previousTaskOrEmployee = previousTaskOrEmployee;
	}

	@Override
	public Employee getEmployee() {
		return employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

	public TaskType getTaskType() {
		return taskType;
	}

	public void setTaskType(TaskType taskType) {
		this.taskType = taskType;
	}

	public Integer getStartTime() {
		return startTime;
	}

	public void setStartTime(Integer startTime) {
		this.startTime = startTime;
	}

	// ************************************************************************
	// Complex methods
	// ************************************************************************

	@Override
	public String toString() {
		return String.format("Task [previousTaskOrEmployee=%s, employee=%s, taskType=%s, startTime=%s]",
				previousTaskOrEmployee, employee, taskType, startTime);
	}
	
	@Override
	public Integer getEndTime() {
		if (getStartTime() == null)
			return taskType.getDuration();
		return getStartTime() + taskType.getDuration();
	}

	public int getMissingSkillCount() {
		int count = 0;

		for (Skill skill : taskType.getRequiredSkillList()) {
			if (employee != null && !employee.getSkillSet().contains(skill))
				count++;
		}

		return count;
	}

	public Priority getPriority() {
		return this.taskType.getPriority();
	}

}

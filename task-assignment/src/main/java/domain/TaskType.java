package domain;

import java.io.Serializable;
import java.util.Set;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("TaTaskType")
public class TaskType implements Serializable {

	private static final long serialVersionUID = 1123759308496138538L;

	private String id;
	private int duration;
	private Set<Skill> requiredSkillList;
	private Priority priority;

	public String getId() {
		return id;		
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	public Set<Skill> getRequiredSkillList() {
		return requiredSkillList;
	}

	public void setRequiredSkillList(Set<Skill> requiredSkillList) {
		this.requiredSkillList = requiredSkillList;
	}

	public Priority getPriority() {
		return priority;
	}

	public void setPriority(Priority priority) {
		this.priority = priority;
	}

	@Override
	public String toString() {
		return String.format("TaskType [id=%s, duration=%s, requiredSkillList=%s, priority=%s]", id, duration,
				requiredSkillList, priority);
	}
}

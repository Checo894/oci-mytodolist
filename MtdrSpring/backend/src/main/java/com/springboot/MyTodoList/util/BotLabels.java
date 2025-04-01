package com.springboot.MyTodoList.util;

public enum BotLabels {
	
	SHOW_MAIN_SCREEN("Show Main Screen"), 
	// HIDE_MAIN_SCREEN("Hide Main Screen"),
	// LIST_ALL_ITEMS("List All Items"), 
	// ADD_NEW_ITEM("Add New Item"),
	// DONE("DONE"),
	// UNDO("UNDO"),
	// DELETE("DELETE"),
	// MY_TODO_LIST("MY TODO LIST"),
	// DASH("-"),
	SHARE_PHONE("📱 Share My Phone Number"),
	CREATE_TASK("🆕 Crear Tarea"),
	ASSIGN_TO_SPRINT("📌 Assign Task to Sprint"), 
	VIEW_SPRINT_TASKS("📋 View Tasks by Sprint"), 
	MY_SUBTASKS("👀 View My Subtasks"), 
	CREATE_SPRINT("📅 Create Sprint"),
	VIEW_DEVELOPERS("👥 Ver Developers");





	private String label;

	BotLabels(String enumLabel) {
		this.label = enumLabel;
	}

	public String getLabel() {
		return label;
	}

}

package com.springboot.MyTodoList.controller;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import com.springboot.MyTodoList.model.Developer;
import com.springboot.MyTodoList.model.Sprint;
import com.springboot.MyTodoList.model.ToDoItem;
import com.springboot.MyTodoList.model.Subtask;
import com.springboot.MyTodoList.service.DeveloperService;
import com.springboot.MyTodoList.service.ToDoItemService;
import com.springboot.MyTodoList.service.SubtaskService;
import com.springboot.MyTodoList.service.SprintService;
import com.springboot.MyTodoList.util.BotCommands;
import com.springboot.MyTodoList.util.BotHelper;
import com.springboot.MyTodoList.util.BotLabels;
import com.springboot.MyTodoList.util.BotMessages;
import com.springboot.MyTodoList.util.SprintAssignmentSession;
import com.springboot.MyTodoList.util.SprintCreationSession;
import com.springboot.MyTodoList.util.TaskCreationSession;


public class ToDoItemBotController extends TelegramLongPollingBot {

	private static final Logger logger = LoggerFactory.getLogger(ToDoItemBotController.class);
	private ToDoItemService toDoItemService;
	private String botName;
	private Map<Long, Long> chatSessionMap = new HashMap<>();
	private SubtaskService subtaskService;
	private Map<Long, Long> pendingCompletionMap = new HashMap<>();
	private Map<Long, TaskCreationSession> taskSessionMap = new HashMap<>();
	private SprintService sprintService;
	private Map<Long, SprintAssignmentSession> sprintAssignmentMap = new HashMap<>();
	private Map<Long, String> stateMap = new HashMap<>();
	private Map<Long, SprintCreationSession> sprintSessionMap = new HashMap<>();


	private DeveloperService developerService;
	public ToDoItemBotController(String botToken, String botName, ToDoItemService toDoItemService, DeveloperService developerService, SubtaskService subtaskService, SprintService sprintService) {
		super(botToken);
		this.toDoItemService = toDoItemService;
		this.botName = botName;
		this.developerService = developerService;
		this.subtaskService = subtaskService;
		this.sprintService = sprintService;
	}


	@Override
	public void onUpdateReceived(Update update) {
		
		if (update.hasMessage() && update.getMessage().hasContact()) {
			Long chatId = update.getMessage().getChatId();
			String phoneNumber = update.getMessage().getContact().getPhoneNumber();

			Developer dev = developerService.getByPhoneNumber(phoneNumber);
			if (dev != null) {
				chatSessionMap.put(chatId, dev.getId());
				BotHelper.sendMessageToTelegram(chatId, "✅ Sesión iniciada como: " + dev.getName(), this);
				BotHelper.showMainMenu(chatId, this);
			} else {
				BotHelper.sendMessageToTelegram(chatId, "⚠️ No se encontró ningún developer con ese número.", this);
				BotHelper.showMainMenu(chatId, this);
			}
			return;
		}


		if (update.hasMessage() && update.getMessage().hasText()) {

			String messageTextFromTelegram = update.getMessage().getText();
			long chatId = update.getMessage().getChatId();

			if (messageTextFromTelegram.equals(BotCommands.START_COMMAND.getCommand())
					|| messageTextFromTelegram.equals(BotLabels.SHOW_MAIN_SCREEN.getLabel())) {

				SendMessage messageToTelegram = new SendMessage();
				messageToTelegram.setChatId(chatId);
				messageToTelegram.setText(BotMessages.HELLO_MYTODO_BOT.getMessage());

				ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
				List<KeyboardRow> keyboard = new ArrayList<>();

				// // first row
				KeyboardRow row = new KeyboardRow();
				// row.add(BotLabels.LIST_ALL_ITEMS.getLabel());
				// row.add(BotLabels.ADD_NEW_ITEM.getLabel());
				// // Add the first row to the keyboard
				// keyboard.add(row);

				// second row
				row = new KeyboardRow();
				row.add(BotLabels.SHOW_MAIN_SCREEN.getLabel());
				// row.add(BotLabels.HIDE_MAIN_SCREEN.getLabel());
				// keyboard.add(row);

				// third row
				KeyboardRow subtaskRow = new KeyboardRow();
				subtaskRow.add(BotLabels.MY_SUBTASKS.getLabel());
				keyboard.add(subtaskRow);

				KeyboardRow taskRow = new KeyboardRow();
				taskRow.add(BotLabels.CREATE_TASK.getLabel());
				keyboard.add(taskRow);

				KeyboardRow sprintRow = new KeyboardRow();
				sprintRow.add(BotLabels.CREATE_SPRINT.getLabel());
				keyboard.add(sprintRow);

				KeyboardRow assignRow = new KeyboardRow();
				assignRow.add(BotLabels.ASSIGN_TO_SPRINT.getLabel());
				keyboard.add(assignRow);

				KeyboardRow viewSprintRow = new KeyboardRow();
				viewSprintRow.add(BotLabels.VIEW_SPRINT_TASKS.getLabel());
				keyboard.add(viewSprintRow);

				KeyboardRow devRow = new KeyboardRow();
				devRow.add(BotLabels.VIEW_DEVELOPERS.getLabel());
				keyboard.add(devRow);


				// fourth row
				KeyboardRow phoneRow = new KeyboardRow();
				KeyboardButton sharePhoneButton = new KeyboardButton(BotLabels.SHARE_PHONE.getLabel());
				sharePhoneButton.setRequestContact(true);
				phoneRow.add(sharePhoneButton);
				keyboard.add(phoneRow);

				// Set the keyboard
				keyboardMarkup.setKeyboard(keyboard);

				// Add the keyboard markup
				messageToTelegram.setReplyMarkup(keyboardMarkup);

				try {
					execute(messageToTelegram);
				} catch (TelegramApiException e) {
					logger.error(e.getLocalizedMessage(), e);
				}

			} else if (messageTextFromTelegram.equals(BotCommands.MY_SUBTASKS.getCommand())) {
				
				if (!chatSessionMap.containsKey(chatId)) {
					BotHelper.sendMessageToTelegram(chatId, "⚠️ Primero comparte tu número para iniciar sesión.", this);
					BotHelper.showMainMenu(chatId, this);
					return;
				}
			
				Long developerId = chatSessionMap.get(chatId);
				List<Subtask> subtasks = subtaskService.getSubtasksByDeveloper(developerId);
			
				if (subtasks.isEmpty()) {
					BotHelper.sendMessageToTelegram(chatId, "No tienes subtareas asignadas. 🎉", this);
				} else {
					SendMessage message = new SendMessage();
					message.setChatId(chatId);
					message.setText("📋 Tus Subtareas Activas:");
			
					ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
					List<KeyboardRow> keyboard = new ArrayList<>();
			
					for (Subtask s : subtasks) {
						KeyboardRow row = new KeyboardRow();
						row.add(s.getTitle());
						String action = s.isCompleted() ? "Descompletar" : "Completar";
						row.add(s.getId() + " - " + action);
						keyboard.add(row);
					}
					
			
					KeyboardRow volver = new KeyboardRow();
					volver.add(BotLabels.SHOW_MAIN_SCREEN.getLabel());
					keyboard.add(volver);
			
					keyboardMarkup.setKeyboard(keyboard);
					message.setReplyMarkup(keyboardMarkup);
			
					try {
						execute(message);
					} catch (TelegramApiException e) {
						logger.error(e.getLocalizedMessage(), e);
					}
				}
			} else if (messageTextFromTelegram.equals(BotLabels.MY_SUBTASKS.getLabel())) {
				// Simula comando /mis-subtareas
				update.getMessage().setText(BotCommands.MY_SUBTASKS.getCommand());
				onUpdateReceived(update);
				return;

			}  else if (messageTextFromTelegram.matches("^\\d+ - (Completar|Descompletar)$")) {
				
				String[] parts = messageTextFromTelegram.split(" - ");
				Long subtaskId = Long.parseLong(parts[0]);
				boolean markAsDone = parts[1].equalsIgnoreCase("Completar");
			
				Subtask subtask = subtaskService.getSubtaskById(subtaskId).getBody();
				if (subtask == null || !subtask.getAssignedDeveloperId().equals(chatSessionMap.get(chatId))) {
					BotHelper.sendMessageToTelegram(chatId, "❌ No tienes acceso a esta subtarea.", this);
					return;
				}
			
				if (markAsDone) {
					// si la marcamos como completada, preguntar por actualHours como ya tienes
					pendingCompletionMap.put(chatId, subtaskId);
					BotHelper.sendMessageToTelegram(chatId, "🕒 ¿Cuántas horas reales tomó esta subtarea?", this);
				} else {
					subtask.setCompleted(false);
					subtask.setActualHours(null);
					subtaskService.updateSubtask(subtaskId, subtask);
					BotHelper.sendMessageToTelegram(chatId, "↩️ Subtarea marcada como *no completada*.", this);
					BotHelper.showMainMenu(chatId, this);
				}
			} else if (subtaskService.existsByTitle(messageTextFromTelegram.trim())) {
				
				String title = messageTextFromTelegram.trim();
			
				Subtask s = subtaskService.getByTitleAndDeveloper(title, chatSessionMap.get(chatId));
				if (s == null) {
					BotHelper.sendMessageToTelegram(chatId, "❌ No se encontró la subtarea o no está asignada a ti.", this);
					return;
				}
			
				ToDoItem task = s.getMainTask();
				Sprint sprint = task.getSprint();
			
				StringBuilder msg = new StringBuilder("📄 *Detalles de la Subtarea:*\n\n");
			
				msg.append("🔹 *Título:* ").append(s.getTitle()).append("\n");
				msg.append("⏱️ *Horas estimadas:* ").append(s.getEstimatedHours()).append("\n\n");
			
				msg.append("🗂 *Tarea Principal:*\n");
				msg.append("• Título: ").append(task.getTitle()).append("\n");
				msg.append("• Descripción: ").append(task.getDescription() == null ? "Ninguna" : task.getDescription()).append("\n");
			
				if (sprint != null) {
					msg.append("📆 *Sprint:*\n");
					msg.append("• Número: ").append(sprint.getSprintNumber()).append("\n");
					msg.append("• ID: ").append(sprint.getId()).append("\n");
					msg.append("• Fechas: ").append(sprint.getStartDate()).append(" ➡ ").append(sprint.getEndDate()).append("\n");
				} else {
					msg.append("📆 *Sprint:* Ninguno\n");
				}
			
				BotHelper.sendMessageToTelegram(chatId, msg.toString(), this);
				BotHelper.showMainMenu(chatId, this);
			} else if (messageTextFromTelegram.equals(BotLabels.CREATE_SPRINT.getLabel())) {
				update.getMessage().setText("/crear-sprint");
				onUpdateReceived(update);
				return;
			} else if (messageTextFromTelegram.equals("/crear-sprint")) {
							
				if (!chatSessionMap.containsKey(chatId)) {
					BotHelper.sendMessageToTelegram(chatId, "⚠️ Primero comparte tu número para iniciar sesión.", this);
					BotHelper.showMainMenu(chatId, this);
					return;
				}
			
				Long devId = chatSessionMap.get(chatId);
				Developer dev = developerService.getById(devId).orElse(null);
				if (dev == null || !dev.getRole().equalsIgnoreCase("projectmanager")) {
					BotHelper.sendMessageToTelegram(chatId, "❌ Solo los Project Managers pueden crear sprints.", this);
					return;
				}
			
				SprintCreationSession session = new SprintCreationSession();
				session.state = SprintCreationSession.State.WAITING_NUMBER;
				sprintSessionMap.put(chatId, session);
			
				BotHelper.sendMessageToTelegram(chatId, "📌 ¿Cuál es el número del sprint?", this);
			} else if (sprintSessionMap.containsKey(chatId)) {
				SprintCreationSession session = sprintSessionMap.get(chatId);
				String input = messageTextFromTelegram.trim();

				switch (session.state) {
					case WAITING_NUMBER:
						try {
							session.sprintNumber = Integer.parseInt(input);
							session.state = SprintCreationSession.State.WAITING_START_DATE;
							BotHelper.sendMessageToTelegram(chatId, "📅 Ingresa la *fecha de inicio* (YYYY-MM-DD):", this);
						} catch (NumberFormatException e) {
							BotHelper.sendMessageToTelegram(chatId, "❌ Número inválido. Intenta de nuevo.", this);
						}
						break;

					case WAITING_START_DATE:
						try {
							session.startDate = LocalDate.parse(input);
							session.state = SprintCreationSession.State.WAITING_END_DATE;
							BotHelper.sendMessageToTelegram(chatId, "📅 Ingresa la *fecha de fin* (YYYY-MM-DD):", this);
						} catch (Exception e) {
							BotHelper.sendMessageToTelegram(chatId, "❌ Fecha inválida. Usa el formato YYYY-MM-DD.", this);
						}
						break;

					case WAITING_END_DATE:
						try {
							session.endDate = LocalDate.parse(input);

							if (!session.endDate.isAfter(session.startDate)) {
								BotHelper.sendMessageToTelegram(chatId, "⚠️ La fecha de fin debe ser posterior a la de inicio.", this);
								return;
							}

							Sprint sprint = new Sprint();
							sprint.setSprintNumber(session.sprintNumber);
							sprint.setStartDate(session.startDate);
							sprint.setEndDate(session.endDate);

							Sprint created = sprintService.createSprint(sprint);
							BotHelper.sendMessageToTelegram(chatId, "✅ Sprint #" + created.getSprintNumber() + " creado con éxito.", this);
						} catch (Exception e) {
							BotHelper.sendMessageToTelegram(chatId, "❌ Error al crear el sprint.", this);
						} finally {
							sprintSessionMap.remove(chatId);
							BotHelper.showMainMenu(chatId, this);
						}
						break;
				}
			} else if (messageTextFromTelegram.equals("/asignar-sprint")) {
			
				if (!chatSessionMap.containsKey(chatId)) {
					BotHelper.sendMessageToTelegram(chatId, "⚠️ Primero debes compartir tu número.", this);
					BotHelper.showMainMenu(chatId, this);
					return;
				}
			
				Long devId = chatSessionMap.get(chatId);
				Developer dev = developerService.getById(devId).orElse(null);
				if (dev == null || !dev.getRole().equalsIgnoreCase("projectmanager")) {
					BotHelper.sendMessageToTelegram(chatId, "❌ Solo los Project Managers pueden asignar tareas a sprints.", this);
					return;
				}
			
				List<ToDoItem> unassignedTasks = toDoItemService.findAll().stream()
					.filter(item -> item.getSprint() == null)
					.toList();
			
				if (unassignedTasks.isEmpty()) {
					BotHelper.sendMessageToTelegram(chatId, "🎉 No hay tareas pendientes de asignar a sprints.", this);
					return;
				}
			
				// Mostrar tareas sin sprint
				StringBuilder msg = new StringBuilder("📋 Tareas sin sprint:\n\n");
				for (ToDoItem t : unassignedTasks) {
					msg.append("🆔 ").append(t.getID()).append(": ").append(t.getTitle()).append("\n");
				}
			
				SprintAssignmentSession session = new SprintAssignmentSession();
				session.state = SprintAssignmentSession.State.WAITING_TASK_SELECTION;
				sprintAssignmentMap.put(chatId, session);
			
				BotHelper.sendMessageToTelegram(chatId, msg.append("\n✏️ Escribe el ID de la tarea que deseas asignar:").toString(), this);
			} else if (messageTextFromTelegram.equals("/ver-sprint")) {
			
				List<Sprint> sprints = sprintService.findAll();  // Asegúrate de tener este método
			
				if (sprints.isEmpty()) {
					BotHelper.sendMessageToTelegram(chatId, "❌ No hay sprints disponibles.", this);
					return;
				}
			
				StringBuilder msg = new StringBuilder("📋 *Sprints disponibles:*\n\n");
			
				for (Sprint s : sprints) {
					msg.append("🆔 ID ").append(s.getId())
					   .append(": Sprint #").append(s.getSprintNumber())
					   .append(" – del ").append(s.getStartDate())
					   .append(" al ").append(s.getEndDate())
					   .append("\n");
				}
			
				msg.append("\n✏️ *Escribe el ID del sprint que deseas consultar:*");
			
				BotHelper.sendMessageToTelegram(chatId, msg.toString(), this);
				// Guardar que este chat está esperando input de sprintId
				stateMap.put(chatId, "WAITING_SPRINT_ID_FOR_VIEW");
			} else if (messageTextFromTelegram.equals("/ver-developers")) {
			
				if (!chatSessionMap.containsKey(chatId)) {
					BotHelper.sendMessageToTelegram(chatId, "⚠️ Primero comparte tu número para iniciar sesión.", this);
					BotHelper.showMainMenu(chatId, this);
					return;
				}
			
				Long devId = chatSessionMap.get(chatId);
				Developer dev = developerService.getById(devId).orElse(null);
			
				if (dev == null || !dev.getRole().equalsIgnoreCase("projectmanager")) {
					BotHelper.sendMessageToTelegram(chatId, "❌ Solo los Project Managers pueden ver esta información.", this);
					return;
				}
			
				List<Developer> devs = developerService.getAll();
				if (devs.isEmpty()) {
					BotHelper.sendMessageToTelegram(chatId, "📭 No hay developers registrados.", this);
				} else {
					StringBuilder msg = new StringBuilder("🧑‍💻 *Desarrolladores registrados:*\n\n");
					for (Developer d : devs) {
						msg.append("🆔 ").append(d.getId())
						   .append(" – ").append(d.getName())
						   .append("\n");
					}
					BotHelper.sendMessageToTelegram(chatId, msg.toString(), this);
				}
			
				BotHelper.showMainMenu(chatId, this);
			} else if (messageTextFromTelegram.equals(BotLabels.VIEW_DEVELOPERS.getLabel())) {
				update.getMessage().setText("/ver-developers");
				onUpdateReceived(update);
				return;
			}
			
			
			
			
				// else if (messageTextFromTelegram.indexOf(BotLabels.DONE.getLabel()) != -1) {

				// String done = messageTextFromTelegram.substring(0,
				// 		messageTextFromTelegram.indexOf(BotLabels.DASH.getLabel()));
				// Long id = Long.valueOf(done);

				// try {

				// 	ToDoItem item = getToDoItemById(id).getBody();
				// 	item.setDone(true);
				// 	updateToDoItem(item, id);
				// 	BotHelper.sendMessageToTelegram(chatId, BotMessages.ITEM_DONE.getMessage(), this);

				// } catch (Exception e) {
				// 	logger.error(e.getLocalizedMessage(), e);
				// }

			// } else if (messageTextFromTelegram.indexOf(BotLabels.UNDO.getLabel()) != -1) {

			// 	String undo = messageTextFromTelegram.substring(0,
			// 			messageTextFromTelegram.indexOf(BotLabels.DASH.getLabel()));
			// 	Long id = Long.valueOf(undo);

			// 	try {

			// 		ToDoItem item = getToDoItemById(id).getBody();
			// 		item.setDone(false);
			// 		updateToDoItem(item, id);
			// 		BotHelper.sendMessageToTelegram(chatId, BotMessages.ITEM_UNDONE.getMessage(), this);

			// 	} catch (Exception e) {
			// 		logger.error(e.getLocalizedMessage(), e);
			// 	}

			// } else if (messageTextFromTelegram.indexOf(BotLabels.DELETE.getLabel()) != -1) {

			// 	String delete = messageTextFromTelegram.substring(0,
			// 			messageTextFromTelegram.indexOf(BotLabels.DASH.getLabel()));
			// 	Long id = Long.valueOf(delete);

			// 	try {

			// 		deleteToDoItem(id).getBody();
			// 		BotHelper.sendMessageToTelegram(chatId, BotMessages.ITEM_DELETED.getMessage(), this);

			// 	} catch (Exception e) {
			// 		logger.error(e.getLocalizedMessage(), e);
			// 	}

			// }  
			else if (messageTextFromTelegram.equals(BotLabels.CREATE_TASK.getLabel())) {
				update.getMessage().setText("/crear-tarea");
				onUpdateReceived(update);
				BotHelper.showMainMenu(chatId, this);
				return;
			} else if (messageTextFromTelegram.equals(BotLabels.ASSIGN_TO_SPRINT.getLabel())) {
				update.getMessage().setText("/asignar-sprint");
				onUpdateReceived(update);
				BotHelper.showMainMenu(chatId, this);
				return;
			} else if (messageTextFromTelegram.equals(BotLabels.VIEW_SPRINT_TASKS.getLabel())) {
				update.getMessage().setText("/ver-sprint");
				onUpdateReceived(update);
				
				return;
			}
			
			// else if (messageTextFromTelegram.equals(BotCommands.HIDE_COMMAND.getCommand())
			// 		|| messageTextFromTelegram.equals(BotLabels.HIDE_MAIN_SCREEN.getLabel())) {

			// 	BotHelper.sendMessageToTelegram(chatId, BotMessages.BYE.getMessage(), this);

			// } else if (messageTextFromTelegram.equals(BotCommands.TODO_LIST.getCommand())
			// 		|| messageTextFromTelegram.equals(BotLabels.LIST_ALL_ITEMS.getLabel())
			// 		|| messageTextFromTelegram.equals(BotLabels.MY_TODO_LIST.getLabel())) {

			// 	List<ToDoItem> allItems = getAllToDoItems();
			// 	ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
			// 	List<KeyboardRow> keyboard = new ArrayList<>();

			// 	// command back to main screen
			// 	KeyboardRow mainScreenRowTop = new KeyboardRow();
			// 	mainScreenRowTop.add(BotLabels.SHOW_MAIN_SCREEN.getLabel());
			// 	keyboard.add(mainScreenRowTop);

			// 	KeyboardRow firstRow = new KeyboardRow();
			// 	firstRow.add(BotLabels.ADD_NEW_ITEM.getLabel());
			// 	keyboard.add(firstRow);

			// 	KeyboardRow myTodoListTitleRow = new KeyboardRow();
			// 	myTodoListTitleRow.add(BotLabels.MY_TODO_LIST.getLabel());
			// 	keyboard.add(myTodoListTitleRow);

			// 	List<ToDoItem> activeItems = allItems.stream().filter(item -> item.isDone() == false)
			// 			.collect(Collectors.toList());

			// 	for (ToDoItem item : activeItems) {

			// 		KeyboardRow currentRow = new KeyboardRow();
			// 		currentRow.add(item.getDescription());
			// 		currentRow.add(item.getID() + BotLabels.DASH.getLabel() + BotLabels.DONE.getLabel());
			// 		keyboard.add(currentRow);
			// 	}

			// 	List<ToDoItem> doneItems = allItems.stream().filter(item -> item.isDone() == true)
			// 			.collect(Collectors.toList());

			// 	for (ToDoItem item : doneItems) {
			// 		KeyboardRow currentRow = new KeyboardRow();
			// 		currentRow.add(item.getDescription());
			// 		currentRow.add(item.getID() + BotLabels.DASH.getLabel() + BotLabels.UNDO.getLabel());
			// 		currentRow.add(item.getID() + BotLabels.DASH.getLabel() + BotLabels.DELETE.getLabel());
			// 		keyboard.add(currentRow);
			// 	}

			// 	// command back to main screen
			// 	KeyboardRow mainScreenRowBottom = new KeyboardRow();
			// 	mainScreenRowBottom.add(BotLabels.SHOW_MAIN_SCREEN.getLabel());
			// 	keyboard.add(mainScreenRowBottom);

			// 	keyboardMarkup.setKeyboard(keyboard);

			// 	SendMessage messageToTelegram = new SendMessage();
			// 	messageToTelegram.setChatId(chatId);
			// 	messageToTelegram.setText(BotLabels.MY_TODO_LIST.getLabel());
			// 	messageToTelegram.setReplyMarkup(keyboardMarkup);

			// 	try {
			// 		execute(messageToTelegram);
			// 	} catch (TelegramApiException e) {
			// 		logger.error(e.getLocalizedMessage(), e);
			// 	}

			// } else if (messageTextFromTelegram.equals(BotCommands.ADD_ITEM.getCommand())
			// 		|| messageTextFromTelegram.equals(BotLabels.ADD_NEW_ITEM.getLabel())) {
			// 	try {
			// 		SendMessage messageToTelegram = new SendMessage();
			// 		messageToTelegram.setChatId(chatId);
			// 		messageToTelegram.setText(BotMessages.TYPE_NEW_TODO_ITEM.getMessage());
			// 		// hide keyboard
			// 		ReplyKeyboardRemove keyboardMarkup = new ReplyKeyboardRemove(true);
			// 		messageToTelegram.setReplyMarkup(keyboardMarkup);

			// 		// send message
			// 		execute(messageToTelegram);

			// 	} catch (Exception e) {
			// 		logger.error(e.getLocalizedMessage(), e);
			// 	}

			// } 
			else if (pendingCompletionMap.containsKey(chatId)) {
				try {
					Double actualHours = Double.parseDouble(messageTextFromTelegram.replace(",", "."));
					Long subtaskId = pendingCompletionMap.get(chatId);
			
					// Buscar la subtarea original
					Subtask subtask = subtaskService.getSubtaskById(subtaskId).getBody();
					if (subtask != null) {
						subtask.setCompleted(true);
						subtask.setActualHours(actualHours);

						// Asegurar que los campos requeridos estén presentes (defensivo)
						if (subtask.getTitle() == null) {
							subtask.setTitle("Subtarea sin título");
						}
						if (subtask.getAssignedDeveloperId() == null) {
							subtask.setAssignedDeveloperId(chatSessionMap.get(chatId));
						}

						subtaskService.updateSubtask(subtaskId, subtask);
			
						BotHelper.sendMessageToTelegram(chatId, "✅ Subtarea marcada como completada en " + actualHours + " horas.", this);
					} else {
						BotHelper.sendMessageToTelegram(chatId, "❌ No se encontró la subtarea.", this);
						BotHelper.showMainMenu(chatId, this);
					}
			
				} catch (NumberFormatException e) {
					BotHelper.sendMessageToTelegram(chatId, "⚠️ Por favor ingresa un número válido, como 1.5 o 2.75", this);
				} catch (Exception e) {
					logger.error("Error al completar subtarea", e);
					BotHelper.sendMessageToTelegram(chatId, "❌ Ocurrió un error al guardar la subtarea.", this);
				} finally {
					pendingCompletionMap.remove(chatId); // limpiar sesión
					BotHelper.showMainMenu(chatId, this); // Mostrar menú principal
				}
				
			} else if (messageTextFromTelegram.equals("/crear-tarea")) {
				Long devId = chatSessionMap.get(chatId);
			
				Developer dev = developerService.getById(devId).orElse(null);
				if (dev == null || !dev.getRole().equalsIgnoreCase("projectmanager")) {
					BotHelper.sendMessageToTelegram(chatId, "⚠️ Solo los Project Managers pueden crear tareas.", this);
					return;
				}
			
				TaskCreationSession session = new TaskCreationSession();
				session.state = TaskCreationSession.State.WAITING_TITLE;
				taskSessionMap.put(chatId, session);
			
				BotHelper.sendMessageToTelegram(chatId, "📝 ¿Cuál es el título de la nueva tarea?", this);
			} else if (taskSessionMap.containsKey(chatId)) {
    TaskCreationSession session = taskSessionMap.get(chatId);
    String input = messageTextFromTelegram.trim();

    switch (session.state) {
        case WAITING_TITLE:
            session.title = input;
            session.state = TaskCreationSession.State.WAITING_DESCRIPTION;
            BotHelper.sendMessageToTelegram(chatId, "🧾 ¿Descripción de la tarea? (escribe 'ninguna' si no aplica)", this);
            break;

        case WAITING_DESCRIPTION:
            session.description = input.equalsIgnoreCase("ninguna") ? "" : input;
            session.state = TaskCreationSession.State.WAITING_SPRINT;
            BotHelper.sendMessageToTelegram(chatId, "📅 ¿ID del sprint? (escribe 'ninguno' si no aplica)", this);
            break;

        case WAITING_SPRINT:
            if (!input.equalsIgnoreCase("ninguno")) {
                try {
                    session.sprintId = Long.parseLong(input);
                } catch (NumberFormatException e) {
                    BotHelper.sendMessageToTelegram(chatId, "❌ ID inválido. Ingresa un número o 'ninguno'.", this);
                    return;
                }
            }
            session.state = TaskCreationSession.State.WAITING_SUBTASK_COUNT;
            BotHelper.sendMessageToTelegram(chatId, "🔢 ¿Cuántas subtareas quieres agregar?", this);
            break;

        case WAITING_SUBTASK_COUNT:
            try {
                session.expectedSubtaskCount = Integer.parseInt(input);
                session.state = TaskCreationSession.State.WAITING_SUBTASK_TITLE;
                BotHelper.sendMessageToTelegram(chatId, "📌 Título de la subtarea 1:", this);
            } catch (NumberFormatException e) {
                BotHelper.sendMessageToTelegram(chatId, "❌ Número inválido. Intenta de nuevo.", this);
            }
            break;

        case WAITING_SUBTASK_TITLE:
            session.currentSubtask = new Subtask();
            session.currentSubtask.setTitle(input);
            session.state = TaskCreationSession.State.WAITING_SUBTASK_HOURS;
            BotHelper.sendMessageToTelegram(chatId, "⏱️ ¿Horas estimadas (máx 4)?", this);
            break;

        case WAITING_SUBTASK_HOURS:
            try {
                double hours = Double.parseDouble(input.replace(",", "."));
                if (hours <= 0 || hours > 4) {
                    BotHelper.sendMessageToTelegram(chatId, "❌ El tiempo estimado debe estar entre 0 y 4 horas.", this);
                    return;
                }
                session.currentSubtask.setEstimatedHours(hours);
                session.state = TaskCreationSession.State.WAITING_SUBTASK_DEVELOPER;
                BotHelper.sendMessageToTelegram(chatId, "👤 Ingresa el ID o número de teléfono del developer asignado:", this);
            } catch (NumberFormatException e) {
                BotHelper.sendMessageToTelegram(chatId, "❌ Valor inválido. Intenta con un número (ej: 2.5)", this);
            }
            break;

        case WAITING_SUBTASK_DEVELOPER:
            Developer assigned = null;
            if (input.startsWith("+")) {
                assigned = developerService.getByPhoneNumber(input);
            } else {
                try {
                    assigned = developerService.getById(Long.parseLong(input)).orElse(null);
                } catch (NumberFormatException e) {
                    // fallthrough
                }
            }

            if (assigned == null) {
                BotHelper.sendMessageToTelegram(chatId, "❌ Developer no encontrado. Intenta con ID o número.", this);
                return;
            }

            session.currentSubtask.setAssignedDeveloperId(assigned.getId());
            session.subtasks.add(session.currentSubtask);
            session.currentSubtaskIndex++;

            if (session.currentSubtaskIndex < session.expectedSubtaskCount) {
                session.state = TaskCreationSession.State.WAITING_SUBTASK_TITLE;
                BotHelper.sendMessageToTelegram(chatId, "📌 Título de la subtarea " + (session.currentSubtaskIndex + 1) + ":", this);
            } else {
                // Finalizar: crear tarea y subtareas
           ToDoItem newTask = new ToDoItem();
			newTask.setTitle(session.title);
			newTask.setDescription(session.description);
			newTask.setSprint(session.sprintId != null ? sprintService.findById(session.sprintId).orElse(null) : null);
			newTask.setStatus("Not Started"); // ✅ status requerido
			ToDoItem savedTask = toDoItemService.addToDoItem(newTask);


                for (Subtask st : session.subtasks) {
                    st.setMainTask(savedTask);
                    subtaskService.addSubtask(savedTask.getID(), st);
                }

                BotHelper.sendMessageToTelegram(chatId, "✅ Tarea creada con " + session.subtasks.size() + " subtareas.", this);
                taskSessionMap.remove(chatId);
				BotHelper.showMainMenu(chatId, this);
            }
            break;
    }
} else if (sprintAssignmentMap.containsKey(chatId)) {
    SprintAssignmentSession session = sprintAssignmentMap.get(chatId);
    String input = messageTextFromTelegram.trim();

    switch (session.state) {
        case WAITING_TASK_SELECTION:
            try {
                Long taskId = Long.parseLong(input);
                ToDoItem task = toDoItemService.getItemById(taskId).getBody();

                if (task == null || task.getSprint() != null) {
                    BotHelper.sendMessageToTelegram(chatId, "❌ Tarea no válida o ya tiene sprint.", this);
                    return;
                }

                session.selectedTaskId = taskId;
                session.state = SprintAssignmentSession.State.WAITING_SPRINT_ID;

                BotHelper.sendMessageToTelegram(chatId, "📌 Ingresa el ID del sprint al que deseas asignarla:", this);

            } catch (NumberFormatException e) {
                BotHelper.sendMessageToTelegram(chatId, "❌ Por favor ingresa un ID válido.", this);
            }
            break;

        case WAITING_SPRINT_ID:
            try {
                Long sprintId = Long.parseLong(input);
                ToDoItem task = toDoItemService.getItemById(session.selectedTaskId).getBody();
                if (task == null) {
                    BotHelper.sendMessageToTelegram(chatId, "❌ La tarea ya no existe.", this);
                    sprintAssignmentMap.remove(chatId);
					BotHelper.showMainMenu(chatId, this);
                    return;
                }

                Sprint sprint = new Sprint();
                sprint.setId(sprintId);
                task.setSprint(sprint);

                ToDoItem updated = toDoItemService.updateToDoItem(task.getID(), task);

                if (updated != null) {
                    BotHelper.sendMessageToTelegram(chatId, "✅ Tarea asignada al sprint correctamente.", this);
                } else {
                    BotHelper.sendMessageToTelegram(chatId, "❌ Falló la actualización de la tarea.", this);
					BotHelper.showMainMenu(chatId, this);
                }
            } catch (NumberFormatException e) {
                BotHelper.sendMessageToTelegram(chatId, "❌ ID de sprint inválido.", this);
            }
            sprintAssignmentMap.remove(chatId);
			BotHelper.showMainMenu(chatId, this);
            break;
    }
} else if (stateMap.getOrDefault(chatId, "").equals("WAITING_SPRINT_ID_FOR_VIEW")) {
    try {
        Long sprintId = Long.parseLong(messageTextFromTelegram.trim());
        List<ToDoItem> tasks = toDoItemService.findAll().stream()
                .filter(task -> task.getSprint() != null && task.getSprint().getId().equals(sprintId))
                .toList();

        if (tasks.isEmpty()) {
            BotHelper.sendMessageToTelegram(chatId, "📭 Este sprint no tiene tareas asignadas.", this);
        } else {
            StringBuilder msg = new StringBuilder("📌 *Tareas del Sprint ID " + sprintId + ":*\n\n");

            for (ToDoItem t : tasks) {
                msg.append("🔹 *").append(t.getTitle()).append("*\n")
                   .append("• Estado: ").append(t.getStatus()).append("\n")
                   .append("• Progreso: ").append(String.format("%.0f", t.getProgress())).append("%\n\n");
            }

            BotHelper.sendMessageToTelegram(chatId, msg.toString(), this);
        }
    } catch (NumberFormatException e) {
        BotHelper.sendMessageToTelegram(chatId, "❌ ID inválido. Intenta con un número.", this);
    }

    stateMap.remove(chatId); // Limpiar estado
	BotHelper.showMainMenu(chatId, this);
}


		
			else {
				try {
					ToDoItem newItem = new ToDoItem();
					newItem.setDescription(messageTextFromTelegram);
					newItem.setCreation_ts(OffsetDateTime.now());
					newItem.setDone(false);
					ResponseEntity entity = addToDoItem(newItem);

					SendMessage messageToTelegram = new SendMessage();
					messageToTelegram.setChatId(chatId);
					messageToTelegram.setText(BotMessages.NEW_ITEM_ADDED.getMessage());

					execute(messageToTelegram);
				} catch (Exception e) {
					logger.error(e.getLocalizedMessage(), e);
				}
			}
		}
	}

	@Override
	public String getBotUsername() {		
		return botName;
	}

	// GET /todolist
	public List<ToDoItem> getAllToDoItems() { 
		return toDoItemService.findAll();
	}

	// GET BY ID /todolist/{id}
	public ResponseEntity<ToDoItem> getToDoItemById(@PathVariable Long id) {
		try {
			ResponseEntity<ToDoItem> responseEntity = toDoItemService.getItemById(id);
			return new ResponseEntity<ToDoItem>(responseEntity.getBody(), HttpStatus.OK);
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	// PUT /todolist
	public ResponseEntity addToDoItem(@RequestBody ToDoItem todoItem) throws Exception {
		ToDoItem td = toDoItemService.addToDoItem(todoItem);
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.set("location", "" + td.getID());
		responseHeaders.set("Access-Control-Expose-Headers", "location");
		// URI location = URI.create(""+td.getID())

		return ResponseEntity.ok().headers(responseHeaders).build();
	}

	// UPDATE /todolist/{id}
	public ResponseEntity updateToDoItem(@RequestBody ToDoItem toDoItem, @PathVariable Long id) {
		try {
			ToDoItem toDoItem1 = toDoItemService.updateToDoItem(id, toDoItem);
			System.out.println(toDoItem1.toString());
			return new ResponseEntity<>(toDoItem1, HttpStatus.OK);
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
			return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
		}
	}

	// DELETE todolist/{id}
	public ResponseEntity<Boolean> deleteToDoItem(@PathVariable("id") Long id) {
		Boolean flag = false;
		try {
			flag = toDoItemService.deleteToDoItem(id);
			return new ResponseEntity<>(flag, HttpStatus.OK);
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
			return new ResponseEntity<>(flag, HttpStatus.NOT_FOUND);
		}
	}

}
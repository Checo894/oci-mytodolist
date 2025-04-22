package com.springboot.MyTodoList.util;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import com.springboot.MyTodoList.model.Developer;
import com.springboot.MyTodoList.service.DeveloperService;

public class BotHelper {

	private static final Logger logger = LoggerFactory.getLogger(BotHelper.class);

	public static void sendMessageToTelegram(Long chatId, String text, TelegramLongPollingBot bot) {

		try {
			// prepare message
			SendMessage messageToTelegram = new SendMessage();
			messageToTelegram.setChatId(chatId);
			messageToTelegram.setText(text);

			// hide keyboard
			ReplyKeyboardRemove keyboardMarkup = new ReplyKeyboardRemove(true);
			messageToTelegram.setReplyMarkup(keyboardMarkup);

			// send message
			bot.execute(messageToTelegram);

		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}
	}
	public static void sendMarkdownMessageToTelegram(Long chatId, String text, TelegramLongPollingBot bot) {
		try {
			SendMessage messageToTelegram = new SendMessage();
			messageToTelegram.setChatId(chatId);
			messageToTelegram.setText(text);
			messageToTelegram.setParseMode("Markdown");
	
			ReplyKeyboardRemove keyboardMarkup = new ReplyKeyboardRemove(true);
			messageToTelegram.setReplyMarkup(keyboardMarkup);
	
			bot.execute(messageToTelegram);
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
		}
	}
	public static void showMainMenu(Long chatId, Long developerId, DeveloperService developerService, TelegramLongPollingBot bot) {

        if (developerId == null) {
            SendMessage message = new SendMessage();
            message.setChatId(chatId);
            message.setText("👋 Antes de comenzar, comparte tu número de teléfono:");
        
            ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
            KeyboardRow phoneRow = new KeyboardRow();
            KeyboardButton sharePhone = new KeyboardButton(BotLabels.SHARE_PHONE.getLabel());
            sharePhone.setRequestContact(true);
            phoneRow.add(sharePhone);
        
            keyboardMarkup.setKeyboard(List.of(phoneRow));
            message.setReplyMarkup(keyboardMarkup);
        
            try {
                bot.execute(message);
            } catch (Exception e) {
                LoggerFactory.getLogger(BotHelper.class).error("Error mostrando menú limitado", e);
            }
            return;
        }

        Developer dev = developerService.getById(developerId).orElse(null);
        String role = (dev != null) ? dev.getRole() : "unknown";
        
        try {

            ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
            List<KeyboardRow> keyboard = new ArrayList<>();

            // KeyboardRow commonRow1 = new KeyboardRow();
            // commonRow1.add(BotLabels.LIST_ALL_ITEMS.getLabel());
            // commonRow1.add(BotLabels.ADD_NEW_ITEM.getLabel());
            // keyboard.add(commonRow1);

            KeyboardRow commonRow1 = new KeyboardRow();
            commonRow1.add(BotLabels.MY_SUBTASKS.getLabel());
            keyboard.add(commonRow1);

            if ("developer".equalsIgnoreCase(role)) {
                KeyboardRow statsRow = new KeyboardRow();
                statsRow.add(BotLabels.MY_STATS.getLabel());
                keyboard.add(statsRow);
            } 

            if ("projectmanager".equalsIgnoreCase(role)) {
                KeyboardRow pmRow1 = new KeyboardRow();
                pmRow1.add(BotLabels.CREATE_TASK.getLabel());
                pmRow1.add(BotLabels.ASSIGN_TO_SPRINT.getLabel());

                KeyboardRow pmRow2 = new KeyboardRow();
                pmRow2.add(BotLabels.VIEW_SPRINT_TASKS.getLabel());
                pmRow2.add(BotLabels.CREATE_SPRINT.getLabel());

                KeyboardRow pmRow3 = new KeyboardRow();
                pmRow3.add(BotLabels.VIEW_DEVELOPERS.getLabel());

                keyboard.add(pmRow1);
                keyboard.add(pmRow2);
                keyboard.add(pmRow3);
            }

            KeyboardRow finalRow = new KeyboardRow();
            KeyboardButton sharePhone = new KeyboardButton(BotLabels.SHARE_PHONE.getLabel());
            sharePhone.setRequestContact(true);
            finalRow.add(sharePhone);
            keyboard.add(finalRow);

            keyboardMarkup.setKeyboard(keyboard);

            SendMessage message = new SendMessage();
            message.setChatId(chatId);
            message.setText("🏠 Menú principal:");
            message.setReplyMarkup(keyboardMarkup);
            bot.execute(message);


            // SendMessage message = new SendMessage();
            // message.setChatId(chatId);
            // message.setText("🏠 Volver al menú principal:");

            // ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
            // List<KeyboardRow> keyboard = new ArrayList<>();

            // // 👉 Aquí defines todos tus botones:
            // KeyboardRow row1 = new KeyboardRow();
            // // row1.add(BotLabels.LIST_ALL_ITEMS.getLabel());
            // // row1.add(BotLabels.ADD_NEW_ITEM.getLabel());

            // // KeyboardRow row2 = new KeyboardRow();
            // row1.add(BotLabels.SHOW_MAIN_SCREEN.getLabel());
            // // row2.add(BotLabels.HIDE_MAIN_SCREEN.getLabel());

            // // KeyboardRow row3 = new KeyboardRow();
            // // row3.add(BotLabels.MY_SUBTASKS.getLabel());

            // // KeyboardRow row4 = new KeyboardRow();
            // // row4.add(BotLabels.CREATE_TASK.getLabel());

            // // KeyboardRow row5 = new KeyboardRow();
            // // row5.add(BotLabels.ASSIGN_TO_SPRINT.getLabel());

            // // KeyboardRow row6 = new KeyboardRow();
            // // row6.add(BotLabels.VIEW_SPRINT_TASKS.getLabel());

            // // KeyboardRow phoneRow = new KeyboardRow();
            // // KeyboardButton sharePhone = new KeyboardButton(BotLabels.SHARE_PHONE.getLabel());
            // // sharePhone.setRequestContact(true);
            // // phoneRow.add(sharePhone);

            // keyboard.add(row1);
            // // keyboard.add(row2);
            // // keyboard.add(row3);
            // // keyboard.add(row4);
            // // keyboard.add(row5);
            // // keyboard.add(row6);
            // // keyboard.add(phoneRow);

            // keyboardMarkup.setKeyboard(keyboard);
            // message.setReplyMarkup(keyboardMarkup);

            // bot.execute(message);
        } catch (Exception e) {
            LoggerFactory.getLogger(BotHelper.class).error("Error al mostrar menú principal", e);
        }
    }

}

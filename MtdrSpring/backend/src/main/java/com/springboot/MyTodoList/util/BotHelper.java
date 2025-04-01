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
	public static void showMainMenu(Long chatId, TelegramLongPollingBot bot) {
    try {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("🏠 Volver al menú principal:");

        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboard = new ArrayList<>();

        // 👉 Aquí defines todos tus botones:
        KeyboardRow row1 = new KeyboardRow();
        // row1.add(BotLabels.LIST_ALL_ITEMS.getLabel());
        // row1.add(BotLabels.ADD_NEW_ITEM.getLabel());

        // KeyboardRow row2 = new KeyboardRow();
        row1.add(BotLabels.SHOW_MAIN_SCREEN.getLabel());
        // row2.add(BotLabels.HIDE_MAIN_SCREEN.getLabel());

        // KeyboardRow row3 = new KeyboardRow();
        // row3.add(BotLabels.MY_SUBTASKS.getLabel());

        // KeyboardRow row4 = new KeyboardRow();
        // row4.add(BotLabels.CREATE_TASK.getLabel());

        // KeyboardRow row5 = new KeyboardRow();
        // row5.add(BotLabels.ASSIGN_TO_SPRINT.getLabel());

        // KeyboardRow row6 = new KeyboardRow();
        // row6.add(BotLabels.VIEW_SPRINT_TASKS.getLabel());

        // KeyboardRow phoneRow = new KeyboardRow();
        // KeyboardButton sharePhone = new KeyboardButton(BotLabels.SHARE_PHONE.getLabel());
        // sharePhone.setRequestContact(true);
        // phoneRow.add(sharePhone);

        keyboard.add(row1);
        // keyboard.add(row2);
        // keyboard.add(row3);
        // keyboard.add(row4);
        // keyboard.add(row5);
        // keyboard.add(row6);
        // keyboard.add(phoneRow);

        keyboardMarkup.setKeyboard(keyboard);
        message.setReplyMarkup(keyboardMarkup);

        bot.execute(message);
    } catch (Exception e) {
        LoggerFactory.getLogger(BotHelper.class).error("Error al mostrar menú principal", e);
    }
}

}

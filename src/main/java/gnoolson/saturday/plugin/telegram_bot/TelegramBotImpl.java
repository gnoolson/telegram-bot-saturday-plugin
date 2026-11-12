package gnoolson.saturday.plugin.telegram_bot;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pengrad.telegrambot.model.BotCommand;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.AnswerCallbackQuery;
import com.pengrad.telegrambot.request.DeleteMyCommands;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.request.SetMyCommands;
import com.pengrad.telegrambot.response.BaseResponse;
import com.pengrad.telegrambot.response.SendResponse;
import gnoolson.saturday_plugin_api.ScriptLauncherGateway;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Log4j2
public class TelegramBotImpl implements TelegramBot {

    private static final ObjectMapper JSON_MAPPER = new ObjectMapper();

    static {
        JSON_MAPPER.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
    }

    private final int queueLength;
    private final ConcurrentLinkedQueue<Update> queue = new ConcurrentLinkedQueue<>();
    private final com.pengrad.telegrambot.TelegramBot pengradTgBot;
    private final String scriptId;
    private final ScriptLauncherGateway scriptLauncherGateway;
    private final boolean executeScript;
    private final ExecutorService executor = Executors.newCachedThreadPool();
    @Getter
    private final String id;
    @Getter
    private final String username;
    @Getter
    private final long createdTs;

    /*
     *
     *
     * */
    public TelegramBotImpl(String id,
                           String username,
                           String token,
                           String scriptId,
                           int queueLength,
                           boolean executeScript,
                           ScriptLauncherGateway scriptLauncherGateway) {

        this.id = id;
        this.username = username;
        this.scriptId = scriptId;
        this.scriptLauncherGateway = scriptLauncherGateway;
        this.executeScript = executeScript;
        this.createdTs = System.currentTimeMillis();
        this.pengradTgBot = new com.pengrad.telegrambot.TelegramBot(token);
        this.pengradTgBot.setUpdatesListener((updates) -> {
            updates.forEach(this::handle);
            return -1;
        });
        this.queueLength = queueLength;
    }

    @Override
    public synchronized Optional<String> getUpdate() {
        Update update = queue.poll();

        if (update == null) {
            return Optional.empty();
        } else {
            try {
                return Optional.of(JSON_MAPPER.writeValueAsString(update));
            } catch (Exception e) {
                log.warn(e);
                return Optional.empty();
            }
        }
    }

    @Override
    public synchronized String setCommands(List<Command> commands) {
        BotCommand[] result = new BotCommand[commands.size()];
        int i = 0;

        for (Command command : commands) {
            result[i] = new BotCommand(command.getCommand(), command.getDescription());
            ++i;
        }

        SetMyCommands setMyCommands = new SetMyCommands(result);
        BaseResponse response = pengradTgBot.execute(setMyCommands);

        return responseToJSON(response);
    }

    @Override
    public synchronized String deleteCommands() {
        BaseResponse response = pengradTgBot.execute(new DeleteMyCommands());
        return responseToJSON(response);
    }

    @Override
    public synchronized String deleteMessage(DeleteMessage deleteMessage) {
        BaseResponse response = pengradTgBot.execute(new com.pengrad.telegrambot.request.DeleteMessage(deleteMessage.getChatId(), deleteMessage.getMessageId()));
        return responseToJSON(response);
    }

    @Override
    public synchronized String sendMarkup(Markup markup) {
        InlineKeyboardButton[][] rows = new InlineKeyboardButton[markup.getKeyboard().size()][];
        int i = 0;

        for (List<Button> buttons : markup.getKeyboard()) {
            InlineKeyboardButton[] cols = new InlineKeyboardButton[buttons.size()];
            int j = 0;

            for (Button button : buttons) {
                InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton(button.getText());
                inlineKeyboardButton.setCallbackData(button.getCallbackData());
                cols[j] = inlineKeyboardButton;
                ++j;
            }

            rows[i] = cols;
            ++i;
        }

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup(rows);

        SendMessage message = new SendMessage(markup.getTextMessage().getChatId(), markup.getTextMessage().getText());
        message.setReplyMarkup(inlineKeyboardMarkup);
        SendResponse response = pengradTgBot.execute(message);

        return responseToJSON(response);
    }

    @Override
    public synchronized String sendAnswerCallbackQuery(String queryId) {
        BaseResponse response = pengradTgBot.execute(new AnswerCallbackQuery(queryId));
        return responseToJSON(response);
    }

    @Override
    public synchronized String sendTextMessage(TextMessage textMessage) {
        SendMessage message = new SendMessage(textMessage.getChatId(), textMessage.getText());
        SendResponse response = pengradTgBot.execute(message);

        return responseToJSON(response);
    }

    @Override
    public synchronized String editMessageText(EditMessageText editMessageText) {
        com.pengrad.telegrambot.request.EditMessageText request = new com.pengrad.telegrambot.request.EditMessageText(
                editMessageText.getChatId(), editMessageText.getMessageId(), editMessageText.getNewText()
        );
        BaseResponse response = pengradTgBot.execute(request);

        return responseToJSON(response);
    }

    @Override
    public synchronized void stop() {
        executor.shutdown();
        pengradTgBot.removeGetUpdatesListener();
        pengradTgBot.shutdown();
    }

    /*
     *
     *
     * */
    private synchronized void handle(Update update) {
        try {
            if (queue.size() >= queueLength) {
                queue.poll();
            }

            queue.add(update);

            if (executeScript) {
                executor.execute(() -> {
                    scriptLauncherGateway.execute(scriptId, Collections.emptyMap());
                });
            }
        } catch (Exception e) {
            log.warn("Exception", e);
        }
    }

    private static String responseToJSON(BaseResponse baseResponse) {
        try {
            return JSON_MAPPER.writeValueAsString(baseResponse);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}

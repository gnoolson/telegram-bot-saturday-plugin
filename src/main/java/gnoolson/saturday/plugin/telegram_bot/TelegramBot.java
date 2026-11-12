package gnoolson.saturday.plugin.telegram_bot;

import java.util.List;
import java.util.Optional;

public interface TelegramBot {

    String getId();

    String getUsername();

    long getCreatedTs();

    Optional<String> getUpdate();

    String setCommands(List<Command> commands);

    String deleteCommands();

    String deleteMessage(DeleteMessage deleteMessage);

    String sendMarkup(Markup markup);

    String sendAnswerCallbackQuery(String queryId);

    void stop();

    String sendTextMessage(TextMessage textMessage);

    String editMessageText(EditMessageText editMessageText);

}

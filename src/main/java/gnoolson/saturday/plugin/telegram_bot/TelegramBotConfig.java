package gnoolson.saturday.plugin.telegram_bot;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class TelegramBotConfig {

    private final String token;
    private final int queueSize;

    private final boolean executeUpdateHandler;
    private final String updateHandlerScriptId;
    private final boolean executeExceptionHandler;
    private final String exceptionHandlerScriptId;

}

package gnoolson.saturday.plugin.telegram_bot;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor
@Getter
public class TextMessage {

    private final String chatId;
    private final String text;

}

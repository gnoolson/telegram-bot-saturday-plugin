package gnoolson.saturday.plugin.telegram_bot;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@ToString
@EqualsAndHashCode
@Getter
@RequiredArgsConstructor
public class Button {

    private final String text;
    private final String callbackData;

}

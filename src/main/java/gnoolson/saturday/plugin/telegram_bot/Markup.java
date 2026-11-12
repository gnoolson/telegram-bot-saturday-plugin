package gnoolson.saturday.plugin.telegram_bot;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.List;

@ToString
@EqualsAndHashCode
@Getter
@RequiredArgsConstructor
public class Markup {

    private final TextMessage textMessage;
    private final List<List<Button>> keyboard;

}

package gnoolson.saturday.plugin.telegram_bot;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@EqualsAndHashCode
@Getter
@RequiredArgsConstructor
public class Command {

    private final String command;
    private final String description;

}

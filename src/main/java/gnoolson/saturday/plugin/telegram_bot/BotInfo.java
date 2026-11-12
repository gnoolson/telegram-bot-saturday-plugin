package gnoolson.saturday.plugin.telegram_bot;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class BotInfo {

    private final String id;
    private final String username;
    private final long createdTs;

}

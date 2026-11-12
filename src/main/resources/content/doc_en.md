<div class="pb-5">
    <a href="/plugin/tg">Active bots</a>
</div>

The `tg` module provides basic functionality for working with Telegram bots. A bot can:

- send text messages;
- edit or delete its own text messages;
- set its commands;
- delete its commands;
- send menus (sets of buttons).

This module uses the `com.pengrad.telegrambot.TelegramBot` Java library (version 10.3).

Before you start, you need to retrieve or create a bot instance by calling the `getOrCreateBot` function and passing:

- the bot identifier (used for caching);
- the path to the `*.properties` configuration file.

Once created, the bot is automatically started and cached. Subsequent calls with the same identifier will return the
same bot instance.

Incoming messages can be processed in two ways:

- the bot automatically triggers a specified script upon receiving a new update. To use this option, configure the
  corresponding settings in the `.properties` file (see example below);
- a script runs on a schedule (for example, every second) and manually checks for new updates.

### API

```lua
local TG = require("tg")
local bot = TG.getOrCreateBot("bot", "file:./plugins/tg/bot.properties")
--[[
# bot.properties file contents
tg.token=1234567890:AABBCCDDEEFF11223344556677889900AA_           # token obtained from BotFather
tg.username=bot_username                                          # bot username (specified during creation)
tg.script.execute=false                                           # execute the script when the bot receives an incoming message
tg.script.script_id=xxxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxx            # script ID. Can be omitted if tg.script.execute=false
tg.queue=64                                                       # update queue size. If exceeded, the oldest updates will be dropped
]]

-- retrieves an incoming update from the queue
-- returns a table, or nil if there are no updates 
local update = bot:getUpdate()

-- sets commands in the bot menu 
-- returns the response from Telegram (table) 
local response = bot:setCommands({
       { command = "command_1", description = "text_description_1_" },
       { command = "command_2", description = "text_description_2_" },
    })
    
-- deletes configured commands
-- returns the response from Telegram (table) 
response = bot:deleteCommands()   
    
-- sends a text message
-- chatId can be retrieved from the incoming message (update)
-- returns the response from Telegram (table) 
response = bot:sendTextMessage({ chatId = "_chat_id_", text = "Hello World!" })

-- deletes a text message 
-- chatId can be retrieved from the incoming message (update) 
-- messageId can be retrieved from the Telegram response after calling sendTextMessage
-- returns the response from Telegram (table) 
response = bot:deleteMessage({ chatId = "_chat_id_", messageId = 1234 })

-- edits a text message
-- chatId can be retrieved from the incoming message (update)
-- messageId can be retrieved from the Telegram response after calling sendTextMessage
-- returns the response from Telegram (table) 
response = bot:editMessageText({ chatId = "_chat_id_", messageId = 1234, newText = "text" })

-- sends a set of buttons (inline keyboard)
-- chatId can be retrieved from the incoming message (update) 
-- returns the response from Telegram (table) 
response = bot:sendMarkup({
             chatId = update.message.chat.id,
             text = "Select button",
             markup = {
                { -- first row
                   {
                      text = "BUTTON 1",
                      callbackData = "query_1",
                   }
                },
                { -- second row
                   {
                      text = "BUTTON 2",
                      callbackData = "query_2",
                   }
                },
             },
          })

-- removes the loading indicator from a button after it is clicked
-- callback_query_id can be retrieved from the update object
-- returns the response from Telegram (table) 
response = bot:sendAnswerCallbackQuery("_callback_query_id_")
```

### General Usage Example: Command -> Button -> Text Message

###### This example script needs to be run on a schedule every second.

```
local TG = require("tg")
local Cache = require("cache")

local bot = TG.getOrCreateBot("bot", "file:./plugins/tg/bot.properties")

local flag = Cache.get("example:telegram:commands")
-- if bot commands have not been set yet, configure them
if not flag then
	bot:setCommands({
		{ command = "command_1", description = "_text_description_1_" },
		{ command = "command_2", description = "_text_description_2_" },
	})
	Cache.put("example:telegram:commands", true, 1000 * 60 * 60)
end

while true do
	local update = bot:getUpdate()
	if update == nil then
		return
	end

	-- if the update is a plain text message
	if update.message ~= nil then
		local text = update.message.text
		if (text == "/command_1") or (text == "/command_2") then
			bot:sendMarkup({
				chatId = update.message.chat.id,
				text = "Select button",
				markup = {
					{ -- first row
						{
							text = "BUTTON 1/1",
							callbackData = "query_1_1",
						},
						{
							text = "BUTTON 1/2",
							callbackData = "query_1_2",
						},
					},
					{ -- second row
						{
							text = "BUTTON 2/1",
							callbackData = "query_2_1",
						},
						{
							text = "BUTTON 2/2",
							callbackData = "query_2_2",
						},
					},
				},
			})
		else
			bot:sendTextMessage({ chatId = update.message.chat.id, text = "Your message: " .. update.message.text })
		end
	end

	-- if the update is a button click (callback query)
	if update.callback_query ~= nil then
		bot:sendAnswerCallbackQuery(update.callback_query.id)
		local data = update.callback_query.data
		bot:sendTextMessage({ chatId = update.callback_query.message.chat.id, text = "Query: " .. data })
	end
end

-- stop the bot
-- TG.stopBot(bot.id)

-- returns information about active bots (table, array)
local list = TG.info()

Output.print(list)

-- output:
[ {
  "username" : "bot_username",
  "id" : "bot",
  "created" : 1794473762289
} ]
```
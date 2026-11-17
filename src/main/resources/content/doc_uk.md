<div class="pb-5">
    <a href="/plugin/tg">Активні боти</a>
</div>

Модуль `tg` надає базову функціональність для роботи з Telegram-ботами. Бот може:

- надсилати текстові повідомлення;
- редагувати, видаляти свої текстові повідомлення;
- вказувати свої команди;
- видаляти свої команди;
- надсилати меню (набір кнопок).

Для роботи використовується Java-бібліотека `com.pengrad.telegrambot.TelegramBot` версії 10.3.

Перед початком роботи необхідно отримати або створити екземпляр бота. Для цього потрібно викликати функцію
`getOrCreateBot`, передавши:

- ідентифікатор бота (для зберігання в кеші);
- шлях до файлу налаштувань `*.properties`.

Створений бот автоматично запускається та кешується. Повторні виклики з тим самим ідентифікатором повертатимуть той
самий екземпляр бота.

Обробка вхідних повідомлень може виконуватися двома способами:

- бот автоматично запускає вказаний сценарій після отримання нового оновлення. Для цього необхідно задати відповідні
  налаштування у файлі `.properties` (див. приклад);
- сценарій запускається за розкладом (наприклад, щосекунди) та самостійно перевіряє наявність нових оновлень.

### API

```lua
local TG = require("tg")
local bot = TG.getOrCreateBot("bot", "file:./plugins/tg/bot.properties")
--[[
# вміст файлу bot.properties
tg.token=1234567890:AABBCCDDEEFF11223344556677889900AA_              # токен, отриманий від BotFather
tg.update_handler.script.execute=false                               # виконувати сценарій, коли бот отримує вхідне оновлення
tg.update_handler.script.id=xxxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxx       # Id сценарію. Якщо tg.update_handler.script.execute=false, можна не вказувати
tg.exception_handler.script.execute=false                            # виконувати сценарій, коли виникають помилки при отриманні оновлень (підключення). Інформація про помилку буде в Args
tg.exception_handler.script.id=xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx  # Id сценарію для обробки помилок. Якщо tg.exception_handler.script.execute=false, можна не вказувати
tg.queue_size=64                                                     # розмір черги оновлень. Якщо кількість буде перевищена, старіші оновлення видалятимуться
]]

-- отримання вхідного оновлення з черги
-- повертає table. Якщо оновлень немає, повертає nil 
local update = bot:getUpdate()

-- встановлення команд у меню бота 
-- повертає відповідь від Telegram (table) 
local response = bot:setCommands({
       { command = "command_1", description = "text_description_1_" },
       { command = "command_2", description = "text_description_2_" },
    })
    
-- видалення встановлених команд
-- повертає відповідь від Telegram (table) 
response = bot:deleteCommands()   
    
-- надсилання текстового повідомлення
-- chatId можна дізнатися з вхідного повідомлення (update)
-- повертає відповідь від Telegram (table) 
response = bot:sendTextMessage({ chatId = "_chat_id_", text = "Hello World!" })

-- видалення текстового повідомлення 
-- chatId можна дізнатися з вхідного повідомлення (update) 
-- messageId можна дізнатися з відповіді від Telegram після виклику sendTextMessage
-- повертає відповідь від Telegram (table) 
response = bot:deleteMessage({ chatId = "_chat_id_", messageId = 1234 })

-- редагування текстового повідомлення
-- chatId можна дізнатися з вхідного повідомлення (update)
-- messageId можна дізнатися з відповіді від Telegram після виклику sendTextMessage
-- повертає відповідь від Telegram (table) 
response = bot:editMessageText({ chatId = "_chat_id_", messageId = 1234, newText = "text" })

-- надсилання набору кнопок
-- chatId можна дізнатися з вхідного повідомлення (update) 
-- повертає відповідь від Telegram (table) 
response = bot:sendMarkup({
             chatId = update.message.chat.id,
             text = "Select button",
             markup = {
                { -- перший рядок
                   {
                      text = "BUTTON 1",
                      callbackData = "query_1",
                   }
                },
                { -- другий рядок
                   {
                      text = "BUTTON 2",
                      callbackData = "query_2",
                   }
                },
             },
          })

-- видалення стану завантаження (preload) з кнопки після її натискання
-- callback_query_id можна дізнатися з повідомлення (update)
-- повертає відповідь від Telegram (table) 
response = bot:sendAnswerCallbackQuery("_callback_query_id_")
```

### Загальний приклад роботи: Команда -> Кнопка -> Текстове повідомлення

###### Цей приклад сценарію потрібно запускати розкладом кожну секунду

```lua
local TG = require("tg")
local Cache = require("cache")

local bot = TG.getOrCreateBot("bot", "file:./plugins/tg/bot.properties")

local flag = Cache.get("example:telegram:commands")
-- якщо команди бота ще не встановлені — встановити їх
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

	-- якщо оновлення є простим текстовим повідомленням
	if update.message ~= nil then
		local text = update.message.text
		if (text == "/command_1") or (text == "/command_2") then
			bot:sendMarkup({
				chatId = update.message.chat.id,
				text = "Select button",
				markup = {
					{ -- перший рядок
						{
							text = "BUTTON 1/1",
							callbackData = "query_1_1",
						},
						{
							text = "BUTTON 1/2",
							callbackData = "query_1_2",
						},
					},
					{ -- другий рядок
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

	-- якщо оновлення є відповіддю на натискання кнопки
	if update.callback_query ~= nil then
		bot:sendAnswerCallbackQuery(update.callback_query.id)
		local data = update.callback_query.data
		bot:sendTextMessage({ chatId = update.callback_query.message.chat.id, text = "Query: " .. data })
	end
end


-- зупинка бота
-- TG.stopBot(bot.id)

-- повертає інформацію про активних ботів (table, array)
local list = TG.info()
Output.print(list)

-- що буде надруковано
[ {
  "id" : "bot",
  "created" : 1794473762289,
  "numberOfUpdatesInQueue" : 0
} ]
```
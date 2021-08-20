# lig2-timelapse-sender
Application for sending day/week/month/year timelapses for https://lyubov-i-golubi.ru/ to https://t.me/joinchat/4BjxucabmfZmOWRi channel

java -Xmx32m -Xmx32m -Xss256k -jar -Dquarkus.datasource.password=<replace> -Dtelegram.bot.api-key=<replace> -Dtelegram.channel.chat-id=<replace> lig2-timelapse-sender-1.0.0-runner.jar
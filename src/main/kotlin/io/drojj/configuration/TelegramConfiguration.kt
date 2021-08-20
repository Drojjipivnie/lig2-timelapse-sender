package io.drojj.configuration

import io.drojj.VideoSenderBot
import io.quarkus.arc.DefaultBean
import org.eclipse.microprofile.config.inject.ConfigProperty
import javax.enterprise.context.Dependent
import javax.enterprise.inject.Produces


@Dependent
class TelegramConfiguration {

    @ConfigProperty(name = "telegram.bot.api-key")
    lateinit var apiKey: String

    @ConfigProperty(name = "telegram.channel.chat-id")
    lateinit var chatId: String

    @Produces
    @DefaultBean
    fun bot(): VideoSenderBot {
        return VideoSenderBot(apiKey, chatId)
    }

}
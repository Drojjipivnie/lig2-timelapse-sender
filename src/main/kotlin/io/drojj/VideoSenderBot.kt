package io.drojj

import io.drojj.dao.Video
import io.drojj.dao.VideoType
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.TelegramBotsApi
import org.telegram.telegrambots.meta.api.methods.send.SendVideo
import org.telegram.telegrambots.meta.api.objects.InputFile
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession
import org.threeten.extra.YearQuarter
import org.threeten.extra.YearWeek
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.time.format.TextStyle
import java.util.*


class VideoSenderBot(
    private val apiKey: String,
    private val chatId: String,
) : TelegramLongPollingBot() {

    init {
        val botsApi = TelegramBotsApi(DefaultBotSession::class.java)
        botsApi.registerBot(this)
    }

    override fun getBotToken() = apiKey

    override fun getBotUsername() = "ЛИГ 2-сезон. Таймлапсы"

    override fun onUpdateReceived(update: Update?) {
        //do nothing
    }

    fun sendMessage(video: Video) {
        execute(SendVideo(chatId, InputFile(video.file)).apply {
            width = 1280
            height = 720
            supportsStreaming = true
            caption = """
            📌 #${video.type.hashTag}
            
            📅 За период: ${getPeriod(video)}
            """.trimIndent()
        })
    }

    private fun getPeriod(video: Video): String {
        return when (video.type) {
            VideoType.DAY -> {
                LocalDate.parse(video.name).format(dayFormatter)
            }
            VideoType.WEEK -> {
                val yearWeek = YearWeek.parse(video.name)
                val startDay = yearWeek.atDay(DayOfWeek.MONDAY).format(dayFormatter)
                val endDay = yearWeek.atDay(DayOfWeek.SUNDAY).format(dayFormatter)
                "$startDay - $endDay"
            }
            VideoType.MONTH -> {
                YearMonth.parse(video.name).month.getDisplayName(TextStyle.FULL_STANDALONE, locale)
                    .replaceFirstChar { it.titlecase(locale) }
            }
            else -> {
                val yearQuarter = YearQuarter.parse(video.name)

                val startDay = yearQuarter.atDay(1).format(dayFormatter)
                val endDay = yearQuarter.atDay(yearQuarter.lengthOfQuarter()).format(dayFormatter)
                "$startDay - $endDay"
            }
        }
    }

    companion object {
        val locale: Locale = Locale("ru")
        var dayFormatter: DateTimeFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(locale)
    }

}
package io.drojj

import io.drojj.dao.VideosDAO
import io.quarkus.scheduler.Scheduled
import org.slf4j.LoggerFactory
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject

@ApplicationScoped
class VideosPoller {

    @Inject
    lateinit var videosDAO: VideosDAO

    @Inject
    lateinit var bot: VideoSenderBot

    @Scheduled(cron = "0/30 30-59 21 ? * * *", identity = "task-job", concurrentExecution = Scheduled.ConcurrentExecution.SKIP)
    fun poll() {
        LOGGER.info("Prepare to poll videos")
        val videos = videosDAO.notUploadedVideos()
        LOGGER.info("Finished. Found {} not uploaded videos", videos.size)
        for (video in videos) {
            LOGGER.info("Processing video {}", video.toString())
            try {
                bot.sendMessage(video)
                videosDAO.markAsUploaded(video)
            } catch (e: Exception) {
                LOGGER.error("Exception while processing video", e)
            }
            LOGGER.info("Finished processing video {}", video)
        }
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(VideosPoller::class.java)
    }
}
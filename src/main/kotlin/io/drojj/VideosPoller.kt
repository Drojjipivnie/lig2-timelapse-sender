package io.drojj

import io.drojj.dao.VideoType
import io.drojj.dao.VideosDAO
import io.quarkus.scheduler.Scheduled
import org.slf4j.LoggerFactory
import java.util.concurrent.*
import java.util.concurrent.atomic.AtomicInteger
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject

@ApplicationScoped
class VideosPoller {

    @Inject
    lateinit var videosDAO: VideosDAO

    @Inject
    lateinit var bot: VideoSenderBot

    val executorService = ThreadPoolExecutor(0, VideoType.values().size, 60L, TimeUnit.SECONDS, SynchronousQueue(), PollerThreadFactory())

    @Scheduled(cron = "0/30 30-59 21 ? * * *", identity = "video-pollers-job", concurrentExecution = Scheduled.ConcurrentExecution.SKIP)
    fun poll() {
        LOGGER.info("Prepare to poll videos")
        val videos = videosDAO.notUploadedVideos()
        LOGGER.info("Finished. Found {} not uploaded videos", videos.size)
        if (videos.isNotEmpty()) {
            val countDownLatch = CountDownLatch(videos.size)
            for (video in videos) {
                executorService.execute {
                    LOGGER.info("Processing video {}", video.toString())
                    try {
                        bot.sendMessage(video)
                        videosDAO.markAsUploaded(video)
                    } catch (e: Exception) {
                        LOGGER.error("Exception while processing video", e)
                    } finally {
                        countDownLatch.countDown()
                        LOGGER.info("Finished processing video {}", video)
                    }
                }
            }
            countDownLatch.await()
            LOGGER.info("Finished processing all videos")
        }
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(VideosPoller::class.java)
    }

    private class PollerThreadFactory : ThreadFactory {
        private val group = ThreadGroup("video-pollers")
        private val threadNumber = AtomicInteger(1)
        private val namePrefix = "VideoPollers_Worker-"

        override fun newThread(runnable: Runnable): Thread {
            return Thread(group, runnable, namePrefix + threadNumber.getAndIncrement(), 0)
        }
    }
}
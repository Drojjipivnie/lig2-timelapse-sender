package io.drojj.dao

interface VideosDAO {

    fun notUploadedVideos(): List<Video>

    fun markAsUploaded(video: Video)

}
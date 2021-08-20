package io.drojj.dao

import io.agroal.api.AgroalDataSource
import java.io.File
import javax.enterprise.context.ApplicationScoped
import javax.inject.Inject

@ApplicationScoped
class VideosDAOImpl : VideosDAO {

    @Inject
    lateinit var dataSource: AgroalDataSource

    override fun notUploadedVideos(): List<Video> {
        val list = mutableListOf<Video>()
        dataSource.connection.use { con ->
            con.prepareStatement("SELECT * FROM lig2.videos WHERE uploaded = false").use { stmt ->
                stmt.executeQuery().use {
                    while (it.next()) {
                        list.add(
                            Video(
                                it.getInt("id"),
                                it.getString("name"),
                                VideoType.valueOf(it.getString("type")),
                                File(it.getString("file_path")),
                                it.getBoolean("uploaded")
                            )
                        )
                    }
                }
            }
        }
        return list
    }

    override fun markAsUploaded(video: Video) {
        dataSource.connection.use { con ->
            con.prepareStatement("UPDATE lig2.videos SET uploaded = true WHERE id = ?").use {
                it.setInt(1, video.id)
                it.executeUpdate()
            }
        }
    }

}
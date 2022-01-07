package io.drojj.dao

import java.io.File

data class Video(val id: Int, val name: String, val type: VideoType, val file: File, val uploaded: Boolean)

enum class VideoType(val hashTag: String) {
    DAY("ДЕНЬ"), WEEK("НЕДЕЛЯ"), MONTH("МЕСЯЦ"), QUARTER("КВАРТАЛ")
}
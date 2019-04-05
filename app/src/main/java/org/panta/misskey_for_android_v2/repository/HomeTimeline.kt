package org.panta.misskey_for_android_v2.repository

import android.util.Log
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.panta.misskey_for_android_v2.entity.RequestTimelineProperty
import java.net.URL

class HomeTimeline(private val domain: String, private val authKey: String) : AbsTimeline(URL("$domain/api/notes/timeline")) {

    private val mapper = jacksonObjectMapper()

    override fun createRequestTimelineJson(
        sinceId: String?,
        untilId: String?,
        sinceDate: Long?,
        untilDate: Long?
    ): String {
        Log.d("HomeTimeline", "値は sinceId:$sinceId, untilId:$untilId, sinceDate:$sinceDate ,untilDate:$untilDate, ")
        val rtp = RequestTimelineProperty()
        rtp.i = authKey
        rtp.limit = 20
        rtp.sinceId = sinceId
        rtp.untilId = untilId
        rtp.sinceDate = sinceDate
        rtp.untilDate = untilDate


        return mapper.writeValueAsString(rtp)
    }
}
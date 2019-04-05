package org.panta.misskey_for_android_v2.entity

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import java.io.Serializable

@JsonIgnoreProperties(ignoreUnknown=true)  data class FileProperty(
    @JsonProperty("id") val id: String? = null,
    @JsonProperty("createdAt") val createdAt: String? = null,
    @JsonProperty("type") val type: String? = null,
    @JsonProperty("md5") val md5: String? = null,
    @JsonProperty("userId") val userId: String? = null,
    @JsonProperty("folderId") val folderId: String? = null,
    @JsonProperty("comment") val comment: String? = null,
    @JsonProperty("properties") val properties: Propertie? = null,
    @JsonProperty("isSensitive") val isSensitive: Boolean? = null,
    @JsonProperty("url") val url: String? = null,
    @JsonProperty("webpublicUrl") val webPublicUrl: String? = null,
    @JsonProperty("thumbnalUrl") val thumbnalUrl: String? = null,
    @JsonProperty("attachedNoteIds") val attachedNoteIds: List<String?>? = null
):Serializable
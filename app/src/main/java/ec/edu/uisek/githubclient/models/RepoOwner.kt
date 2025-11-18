package ec.edu.uisek.githubclient.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class RepoOwner (
    val id: Long,
    val login: String,
    @SerializedName(value = "avatar_url")
    val avatar_url: String,
): Parcelable
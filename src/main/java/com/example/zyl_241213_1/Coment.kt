package com.example.zyl_241213_1

import android.os.Parcel
import android.os.Parcelable
import java.io.Serializable

data class Comment(
    val time: String, // 发表评论的时间
    val owner: String, // 发表评论的user
    val time_of_record: String, // 评论的record的发表时间
    val content: String // 评论内容
) : Serializable, Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(time)
        parcel.writeString(owner)
        parcel.writeString(time_of_record)
        parcel.writeString(content)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Comment> {
        override fun createFromParcel(parcel: Parcel): Comment {
            return Comment(parcel)
        }

        override fun newArray(size: Int): Array<Comment?> {
            return arrayOfNulls(size)
        }
    }
}
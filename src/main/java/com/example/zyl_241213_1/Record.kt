package com.example.zyl_241213_1

import android.os.Parcel
import android.os.Parcelable
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

data class Record(
    val owner: String,
    val title: String,
    val mood: String,
    val time: String,
    val content: String
) : Parcelable {
    // 从 JSONObject 创建 Record 实例的构造函数
    constructor(jsonObject: JSONObject) : this(
        owner = jsonObject.getString("owner"),
        title = jsonObject.getString("title"),
        mood = jsonObject.getString("mood"),
        time = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date(jsonObject.getLong("time"))),
        content = jsonObject.getString("content")
    )

    // Parcelable实现部分
    constructor(parcel: Parcel) : this(
        owner = parcel.readString() ?: "",
        title = parcel.readString() ?: "",
        mood = parcel.readString() ?: "",
        time = parcel.readString() ?: "",
        content = parcel.readString() ?: ""
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(owner)
        parcel.writeString(title)
        parcel.writeString(mood)
        parcel.writeString(time)
        parcel.writeString(content)
    }

    override fun describeContents(): Int {
        return 0 // 一般返回0
    }

    companion object CREATOR : Parcelable.Creator<Record> {
        override fun createFromParcel(parcel: Parcel): Record {
            return Record(parcel)
        }

        override fun newArray(size: Int): Array<Record?> {
            return arrayOfNulls(size)
        }
    }
}

import android.os.Parcel
import android.os.Parcelable

class Post (
    var postId: String? = null ,
    var postDec: String? = null,
    var isLike :Boolean? =null,
    var postImage: String? = null,
    var numberOfNum: Int? = null,
    var clubName: String? = null,
    var teacherId: String? = null,
    var teacherName: String? = null,
    var teacherImage: String? = null,
    var numberOfComment:Int?= null ,
    var likeBy:ArrayList<String> ? =null

):Parcelable{
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Boolean::class.java.classLoader) as? Boolean,
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int,

    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(postId)
        parcel.writeString(postDec)
        parcel.writeValue(isLike)
        parcel.writeString(postImage)
        parcel.writeValue(numberOfNum)
        parcel.writeString(clubName)
        parcel.writeString(teacherId)
        parcel.writeString(teacherName)
        parcel.writeString(teacherImage)
        parcel.writeValue(numberOfComment)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Post> {
        override fun createFromParcel(parcel: Parcel): Post {
            return Post(parcel)
        }

        override fun newArray(size: Int): Array<Post?> {
            return arrayOfNulls(size)
        }
    }

}

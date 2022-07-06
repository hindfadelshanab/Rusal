package developer.citypalestine8936ps.new_home_feature.comments

data class NewComment(
    val docId: String = "",
    val commentText: String = "",
    val time: Long = System.currentTimeMillis(),
    val sendDocId: String = ""
)

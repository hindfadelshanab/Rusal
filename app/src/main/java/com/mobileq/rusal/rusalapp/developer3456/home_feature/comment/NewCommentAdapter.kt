package developer.citypalestine8936ps.new_home_feature.comments

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mobileq.rusal.rusalapp.developer3456.databinding.CommentItemBinding
import com.squareup.picasso.Picasso

class NewCommentAdapter(
    private val context: Context,
    private var comments: MutableList<CommentModelForAdapter>
) : RecyclerView.Adapter<NewCommentAdapter.NewCommentViewHolder>() {

    fun updateData(data: MutableList<CommentModelForAdapter>) {
        try {
            this.comments = data
            notifyDataSetChanged()
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    fun addComment(comment: CommentModelForAdapter) {
        try {
            comments.add(comment)
            notifyItemInserted(comments.size - 1)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    fun modifyComment(comment: NewComment) {
        try {
            val commentPosition = comments.indexOfFirst { it.comment.docId == comment.docId }
            val oldComment = comments[commentPosition]
            comments[commentPosition] = oldComment.copy(comment = comment)
            notifyItemChanged(commentPosition)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    fun removeComment(comment: NewComment) {
        try {
            val commentPosition = comments.indexOfFirst { it.comment.docId == comment.docId }
            val removeResult = comments.removeIf { it.comment.docId == comment.docId }
            if (removeResult)
                notifyItemRemoved(commentPosition)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    class NewCommentViewHolder(private val binding: CommentItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bindItem(commentModelForAdapter: CommentModelForAdapter) {
            val comment = commentModelForAdapter.comment
            val sender = commentModelForAdapter.sender
            if (sender.image!!.isNotEmpty())
                Picasso.get().load(sender.image).into(binding.imageSenderComment)
            binding.txtUserNameComment.text = sender.name
            binding.txtComment.text = comment.commentText
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewCommentViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = CommentItemBinding.inflate(inflater, parent, false)
        return NewCommentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NewCommentViewHolder, position: Int) {
        val currentComment = comments[position]
        holder.bindItem(currentComment)
    }

    override fun getItemCount(): Int = comments.size
}
package developer.citypalestine8936ps.new_home_feature

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mobileq.rusal.rusalapp.developer3456.R
import com.mobileq.rusal.rusalapp.developer3456.databinding.PostItemBinding
import com.mobileq.rusal.rusalapp.developer3456.home_feature.PostModelForAdapter
import com.mobileq.rusal.rusalapp.developer3456.home_feature.NewPost
import com.mobileq.rusal.rusalapp.developer3456.new_home_feature.NewPostListener
import com.squareup.picasso.Picasso


class NewPostAdapter(
    private val context: Context,
    private var posts: MutableList<PostModelForAdapter>,
    private val loggedUserId: String,
    private val postListener: NewPostListener?,
    private val isPublic:Boolean
) : RecyclerView.Adapter<NewPostAdapter.NewPostViewHolder>() {

    fun updateData(data: MutableList<PostModelForAdapter>) {
        posts = data
        notifyDataSetChanged()
    }

    fun addPost(newPost: PostModelForAdapter) {
        posts.add(newPost)
        Log.d("TAG", "addPost: pppp")
        notifyItemInserted(posts.size - 1)
    }

    fun clearPost(){
        posts.clear()
        notifyDataSetChanged()
    }

    fun updatePost(newPost: NewPost) {
        val postPosition = posts.indexOfFirst { it.post.docId == newPost.docId }
        val oldPost = posts[postPosition]
        posts[postPosition] = oldPost.copy(post = newPost)
        notifyItemChanged(postPosition)
    }


    fun removePost(newPost: NewPost) {
        val postPosition = posts.indexOfFirst { it.post.docId == newPost.docId }
        val removeResult = posts.removeIf { it.post.docId == newPost.docId }
        if (removeResult)
            notifyItemRemoved(postPosition)
    }

    inner class NewPostViewHolder(private val binding: PostItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bindItem(modelForAdapter: PostModelForAdapter) {
            val post = modelForAdapter.post
            val author = modelForAdapter.author
            val comments = modelForAdapter.comment
            if (comments!=null){
                binding.txtNumberOfComment.text = "${comments.size.toString()} تعليق"

            }

            binding.txtPostUserName.text = author.name
            if (author.image!!.isNotEmpty()) {
                Picasso.get().load(author.image).into(binding.imagePostUserProfile)
            }

            binding.imagePostUserProfile.setOnClickListener{
                postListener?.onClickAuthor(author)
            }
//            binding.tvPostTime.text =
//                "${post.time.toConversationDateFormat()}\n${post.time.toConversationTimeFormat()}"


            /*Optional Post Data*/
            if (post.content.isNotEmpty()) {
                binding.txtPostDescription.visibility = View.VISIBLE
                binding.txtPostDescription.text = post.content
            } else {
                binding.txtPostDescription.visibility = View.GONE
            }

            if (post.imageUrl.isNotEmpty()) {
                binding.imgPostImage.visibility = View.VISIBLE
                Picasso.get().load(post.imageUrl).into(binding.imgPostImage)
            } else {
                binding.imgPostImage.visibility = View.GONE
            }

            if (isPublic){
                binding.linearComment.visibility =View.GONE
                binding.linearLike.visibility =View.GONE
            }
            /*Counters*/
//            binding.tvPostLikeCount.text = when (val likes = post.likes.size) {
//                0 -> {
//                    context.getString(R.string.no_likes_yet)
//                }
//                1 -> {
//                    context.getString(R.string.like_count, likes)
//                }
//                else -> {
//                    context.getString(R.string.likes_count, likes)
//                }
//            }
            binding.txtNumberOfLike.text = "${post.likes.size} اعجاب"
            /*Like Button*/
            binding.imgLike.setImageResource(
                if (modelForAdapter.post.likes.contains(loggedUserId)) {
                    R.drawable.ic__fill_heart
                } else {
                    R.drawable.ic_heart
                }
            )

            /*Listeners*/
            binding.linearComment.setOnClickListener {
                postListener?.onClickComment(post)
            }
            binding.linearLike.setOnClickListener {
                postListener?.onClickLike(post)
            }
            binding.imgPostImage.setOnClickListener {
                postListener?.onClickImage(post)
            }
            binding.imgShare.setOnClickListener {
                val intent = Intent()
                intent.action = Intent.ACTION_SEND
                intent.putExtra(Intent.EXTRA_TEXT, post.content)
                intent.type = "text/plain"
                context.startActivity(Intent.createChooser(intent, "Share To:"))
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewPostViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = PostItemBinding.inflate(inflater, parent, false)
        return NewPostViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NewPostViewHolder, position: Int) {
        val currentPost = posts[position]
        holder.bindItem(currentPost)
    }

    override fun getItemCount(): Int = posts.size
}
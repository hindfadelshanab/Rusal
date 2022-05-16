package com.mobileq.rusal.rusalapp.developer3456

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.mobileq.rusal.rusalapp.developer3456.adapter.PostAdapter
import com.mobileq.rusal.rusalapp.developer3456.databinding.ActivityMainBinding
import com.mobileq.rusal.rusalapp.developer3456.model.Post

class MainActivity : AppCompatActivity() {

    private var adapter: PostAdapter? = null

    lateinit var binding : ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var data :ArrayList<Post> = ArrayList<Post>();
        data.add(Post("الممارسة الاعلامية يجب ان توضع في سياق تقاطع اربع حقول اساسية" , true ,R.drawable.mmm))
        data.add(Post(" الوسائط استخدام عدة وسائط لتقديم المعلومات. " , false ,R.drawable.ww))
        data.add(Post("الفن عبارة عن مجموعة متنوعة من الأنشطة البشرية في إنشاء أعمال بصرية أو سمعية أو أداء" , true ,R.drawable.ffff))
       data.add(Post("الممارسة الاعلامية يجب ان توضع في سياق تقاطع اربع حقول اساسية" , true ,R.drawable.mmm))
        data.add(Post("الوسائط هو دمجا بين النصوص، والرسومات، والحركة، والصور، والفيديو، والصوت" , false ,R.drawable.ww))
        data.add(Post("الفن عبارة عن مجموعة متنوعة من الأنشطة البشرية في إنشاء أعمال بصرية أو سمعية أو أداء" , true ,R.drawable.ffff))


        adapter = PostAdapter(this , data)
        binding.postRc.adapter = adapter
        binding.postRc.layoutManager = LinearLayoutManager(this)
    }
}
package com.mobileq.rusal.rusalapp.developer3456

import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.view.View
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.get
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import androidx.viewpager2.widget.ViewPager2
import com.mobileq.rusal.rusalapp.developer3456.adapter.IntroSliderAdapter
import com.mobileq.rusal.rusalapp.developer3456.databinding.ActivitySliderScreenBinding
import com.mobileq.rusal.rusalapp.developer3456.utilites.Constants
import com.mobileq.rusal.rusalapp.developer3456.utilites.PreferenceManager

class SliderScreenActivity : AppCompatActivity() {


    private lateinit var binding : ActivitySliderScreenBinding
    private var preferenceManager: PreferenceManager? = null


    private lateinit var introSliderAdapter :IntroSliderAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySliderScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)


        preferenceManager = PreferenceManager(applicationContext)
        if (preferenceManager!!.getBoolean(Constants.KEY_IS_SIGNED_IN) && preferenceManager!!.getBoolean(
                Constants.KEY_IS_Student)) {
            val intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)
            finish()
        }else if (preferenceManager!!.getBoolean(Constants.KEY_IS_SIGNED_IN) && preferenceManager!!.getBoolean(
                Constants.KEY_IS_Teacher)) {
            val intent = Intent(applicationContext, MainActivity2::class.java)
            startActivity(intent)
            finish()

        }
        introSliderAdapter= IntroSliderAdapter(
            listOf(
                IntroSlide(
                    "تواصل مع المعلمين .",
                     "يوفر تطبيق رسل محادثات جماعية مع المعلمين والطلاب للنادي ,وايضا محادثات فردية",
                    R.drawable.slid1
                ),
                IntroSlide(
                    "سجل وشارك معنا",
                    "شاركنا بماذا تفكر وتفاعل على منشورات المعلمين والطلاب في نوادي الاعلام ,الوسائط والفنون."
                     ,
                    R.drawable.slid2
                ),
                IntroSlide(
                    "شارك موهبتك",
                    "يتيح لك تطبيق رسل مشاركة موهبتك ومراجعتها من قبل المعلمين ."
                    ,
                    R.drawable.slid3
                )
            )
        )
//        var introSliderViewPager  = findViewById<ViewPager2>(R.id.introSliderViewPager);
//        var buttonNext  = findViewById<Button>(R.id.buttonNext);
//        var textSkipIntro  = findViewById<TextView>(R.id.textSkipIntro);
       //  indicatorContainer  = findViewById<LinearLayout>(R.id.indicatorContainer);

        binding.introSliderViewPager.adapter = introSliderAdapter
        setupIndicators()
        setCurrentIndicator(0)
        binding.introSliderViewPager.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                setCurrentIndicator(position)
            }
        })

        binding.buttonNext.setOnClickListener {
            if (    binding.introSliderViewPager.currentItem + 1 < introSliderAdapter.itemCount) {
                binding.introSliderViewPager.currentItem += 1
            } else {
                Intent(applicationContext, SignInActivity::class.java).also {
                    startActivity(it)
                    finish()
                }
            }
        }

        binding.textSkipIntro.setOnClickListener {
            Intent(applicationContext, SignInActivity::class.java).also {
                startActivity(it)
                finish()
            }
        }


    }

    private fun setupIndicators() {
        val indicators = arrayOfNulls<ImageView>(introSliderAdapter.itemCount)
        val layoutParams: LinearLayout.LayoutParams =
            LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
        layoutParams.setMargins(8, 0, 8, 0)

        for (i in indicators.indices) {
            indicators[i] = ImageView(applicationContext)
            indicators[i].apply {
                this?.setImageDrawable(
                    ContextCompat.getDrawable(
                        applicationContext,
                        R.drawable.indicator_inactive
                    )

                )
                this?.layoutParams = layoutParams
            }

            binding.indicatorContainer.addView(indicators[i])
        }
    }

    private fun setCurrentIndicator(index: Int) {
        val chilCount = binding.indicatorContainer.childCount
        for (i in 0 until chilCount) {
            val imageView = binding.indicatorContainer[i] as ImageView
            if (i == index) {
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(
                        applicationContext,
                        R.drawable.indicator_active
                    )
                )
            } else {
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(
                        applicationContext,
                        R.drawable.indicator_inactive
                    )
                )
            }
        }
    }


}
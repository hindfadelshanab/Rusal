package com.mobileq.rusal.rusalapp.developer3456.image_preview_feature

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.mobileq.rusal.rusalapp.developer3456.R
import com.mobileq.rusal.rusalapp.developer3456.databinding.FragmentImagePreviewDialogBinding
import com.mobileq.rusal.rusalapp.developer3456.utilites.Constants
import com.mobileq.rusal.rusalapp.developer3456.utilites.load


class ImagePreviewDialog : DialogFragment() {

    private lateinit var binding: FragmentImagePreviewDialogBinding
    private var imageUrl: String = ""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentImagePreviewDialogBinding.inflate(inflater)
        return binding.apply {
            ibBack.setOnClickListener { dismiss() }
//            toolbar.setNavigationOnClickListener { dismiss() }
        }.root

    }



    override fun onStart() {
        super.onStart()
        val dialog: Dialog? = dialog
        if (dialog != null) {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.MATCH_PARENT
            dialog.window?.setLayout(width, height)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.AppTheme_FullScreenDialog)

        arguments?.let {
            imageUrl = it.getString(Constants.KEY_IMAGE_PREVIEW_URL, "") ?: ""
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated: imageUrl $imageUrl")
        binding.ivImagePreview.load(requireContext(), imageUrl)
    }


    companion object {
        private const val TAG = "ImagePreviewDialog"
        fun display(
            fragmentManager: FragmentManager,
            imageUrl: String = ""
        ): ImagePreviewDialog {
            val commentsDialog = ImagePreviewDialog()
            imageUrl.let {
                commentsDialog.arguments = bundleOf(Constants.KEY_IMAGE_PREVIEW_URL to imageUrl)
            }
            commentsDialog.show(fragmentManager, TAG)
            return commentsDialog
        }
    }
}
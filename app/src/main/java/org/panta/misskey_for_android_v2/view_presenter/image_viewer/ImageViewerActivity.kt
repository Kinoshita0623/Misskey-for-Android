package org.panta.misskey_for_android_v2.view_presenter.image_viewer

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_image_viewer.*
import kotlinx.android.synthetic.main.content_main.*
import org.panta.misskey_for_android_v2.R
import java.util.*

class ImageViewerActivity : AppCompatActivity() {

    companion object{
        const val IMAGE_URL_LIST = "IMAGE_VIEW_ACTIVITY_IMAGE_URL_LIST"
        const val CLICKED_IMAGE_URL = "CLICKED_IMAGE_URL_IMAGE_VIEWER_ACTIVITY"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_viewer)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val intent = intent
        val imageArray = intent.getStringArrayExtra(IMAGE_URL_LIST)
        val clickedImageIndex = intent.getIntExtra(CLICKED_IMAGE_URL, 0)

        val imageList = imageArray.toList()

        Log.d("ImageViewerActivity", imageList.toString())
        Log.d("ImageViewerActivity", imageList.size.toString())
        val pagerAdapter = ImagePageAdapter(supportFragmentManager, imageList)
        image_view_pager.offscreenPageLimit = imageList.size
        image_view_pager.adapter = pagerAdapter

        image_view_pager.currentItem = clickedImageIndex

    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId){
            android.R.id.home ->{
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}

package org.panta.misskey_for_android_v2.view_presenter.image_viewer

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_image_view.*
import org.panta.misskey_for_android_v2.R
import java.net.URL

class ImageViewFragment : Fragment(){

    var mImageUrl: String? = null

    companion object{
        private const val IMAGE_VIEW_FRAGMENT_URL = "IMAGE_VIEW_FRAGMENT_URL"
        fun getInstance(imageUrl: String): ImageViewFragment{
            val bundle = Bundle()
            bundle.putString(IMAGE_VIEW_FRAGMENT_URL, imageUrl)

            val fragment = ImageViewFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        mImageUrl = arguments?.getString(IMAGE_VIEW_FRAGMENT_URL)

        return inflater.inflate(R.layout.item_image_view, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(mImageUrl == null){

        }else{
            image_url_view.text = mImageUrl
            Picasso
                .get()
                .load(mImageUrl)
                .into(imageView)
        }
    }
}
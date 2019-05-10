package org.panta.misskey_for_android_v2.interfaces

import org.panta.misskey_for_android_v2.view_data.FollowViewData

interface FollowFollowerContract{
    interface Presenter : BasePresenter{
        fun getOldItems()
        fun getNewItems()
        fun getItems()
        fun onFollowUnFollowButtonClicked(item: FollowViewData)
    }

    interface View : BaseView<Presenter>{
        fun showOldItems(list: List<FollowViewData>)
        fun showNewItems(list: List<FollowViewData>)
        fun showItems(list: List<FollowViewData>)
        fun showError(e: Exception)
        fun stopRefreshing()
        fun updateItem(item: FollowViewData)
        fun removeItem(item: FollowViewData)

    }
}
package org.panta.misskey_for_android_v2.adapter

import android.graphics.Color
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.widget.*
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_note.view.*
import org.panta.misskey_for_android_v2.R
import org.panta.misskey_for_android_v2.entity.FileProperty
import org.panta.misskey_for_android_v2.entity.Note
import org.panta.misskey_for_android_v2.entity.User
import org.panta.misskey_for_android_v2.interfaces.ItemClickListener
import org.panta.misskey_for_android_v2.interfaces.NoteClickListener
import org.panta.misskey_for_android_v2.interfaces.UserClickListener
import org.panta.misskey_for_android_v2.usecase.RoundedTransformation
import org.panta.misskey_for_android_v2.view_data.NoteViewData

open class NoteViewHolder(itemView: View, private val linearLayoutManager: LinearLayoutManager?) : RecyclerView.ViewHolder(itemView){

    private var contentClickListener: NoteClickListener? = null
    private var userClickListener: UserClickListener? = null

    private val timelineItem = itemView.base_layout
    private val whoReactionUserLink: Button = itemView.who_reaction_user_link
    private val userIcon: ImageView = itemView.user_icon
    private val userName: TextView = itemView.user_name
    private val userId: TextView = itemView.user_id
    private val noteText: TextView = itemView.note_text

    private val imageView1: ImageView = itemView.image_1
    private val imageView2: ImageView = itemView.image_2
    private val imageView3: ImageView = itemView.image_3
    private val imageView4: ImageView = itemView.image_4
    private val imageViewList: List<ImageView> = listOf(imageView1, imageView2, imageView3, imageView4)

    private val subNote = itemView.sub_note
    private val subUserIcon = itemView.sub_user_icon
    private val subUserName = itemView.sub_user_name
    private val subUserId = itemView.sub_user_id
    private val subNoteText: TextView = itemView.sub_text

    private val reactionView: RecyclerView = itemView.reaction_view

    private val replyButton: ImageButton = itemView.reply_button
    private val replyCount: TextView = itemView.reply_count
    private val reNoteButton: ImageButton = itemView.re_note_button
    private val reNoteCount: TextView = itemView.re_note_count
    private val reactionButton: ImageButton = itemView.reaction_button
    private val descriptionButton: ImageButton = itemView.description_button

    private val showThreadButton: Button = itemView.show_thread_button

    fun setNote(content: NoteViewData){
        whoReactionUserLink.visibility = View.GONE
        backgroundColor(0)
        invisibleSubContents()
        setNoteContent(content.note)
        setRelationNoteListener(content.note.id, content.note, timelineItem, noteText)

        setReplyCount(content.note.replyCount)
        setReNoteCount(content.note.reNoteCount)
        setFourControlButtonListener(content.note, content)
        showThreadButton.visibility = View.GONE
        subNote.visibility = View.GONE
        setReactionCount(content, content.note)
    }

    fun setReNote(content: NoteViewData){
        backgroundColor(0)
        invisibleSubContents()
        setWhoReactionUserLink(content.note.user, "リノート")
        setNoteContent(content.note.renote!!)
        setRelationNoteListener(content.note.renote.id, content.note.renote, timelineItem, noteText)

        setReplyCount(content.note.renote.replyCount)
        setReNoteCount(content.note.renote.reNoteCount)
        setFourControlButtonListener(content.note.renote, content)
        showThreadButton.visibility = View.GONE
        subNote.visibility = View.GONE
        setReactionCount(content, content.note.renote)

    }
    fun setQuoteReNote(content: NoteViewData){
        backgroundColor(0)
        setWhoReactionUserLink(content.note.user, "引用リノート")
        setNoteContent(content.note)
        setSubContent(content.note.renote!!)
        setRelationNoteListener(content.note.id, content.note, timelineItem)
        setRelationNoteListener(content.note.renote.id, content.note.renote, subNoteText)

        setReplyCount(content.note.replyCount)
        setReNoteCount(content.note.reNoteCount)
        setFourControlButtonListener(content.note, content)
        showThreadButton.visibility = View.GONE
        subNote.visibility = View.VISIBLE
        setReactionCount(content, content.note)
    }

    fun setReply(content: NoteViewData){
        backgroundColor(0)
        invisibleSubContents()
        setNoteContent(content.note)
        setWhoReactionUserLink(content.note.user, "クソリプ")
        setRelationNoteListener(content.note.id, content.note, timelineItem, noteText, showThreadButton)

        setReplyCount(content.note.replyCount)
        setReNoteCount(content.note.reNoteCount)
        setFourControlButtonListener(content.note, content)
        showThreadButton.visibility = View.VISIBLE
        subNote.visibility = View.GONE
        setReactionCount(content, content.note)

    }

    fun setReplyTo(content: NoteViewData){
        whoReactionUserLink.visibility = View.GONE
        invisibleSubContents()
        backgroundColor(1)
        setNoteContent(content.note)
        setRelationNoteListener(content.note.id, content.note, timelineItem, noteText)

        setReplyCount(content.note.replyCount)
        setReNoteCount(content.note.reNoteCount)
        setFourControlButtonListener(content.note, content)
        showThreadButton.visibility = View.GONE
        setReactionCount(content, content.note)
    }



    fun invisibleReactionCount(){
        reactionView.visibility = View.GONE
    }

    fun addOnItemClickListener(listener: NoteClickListener?){
        contentClickListener = listener
    }
    fun addOnUserClickListener(listener: UserClickListener?){
        this.userClickListener = listener
    }

    private fun setReactionCount(viewData: NoteViewData, note: Note){
        if(linearLayoutManager == null ){
            reactionView.visibility = View.GONE
        }else{
            val adapter = ReactionRecyclerAdapter(viewData.reactionCountPairList , note.myReaction)
            adapter.reactionItemClickListener = object : ItemClickListener<String>{
                override fun onClick(e: String) {
                    Log.d("NoteViewHolder", "setReactionCountがクリックされた")
                    contentClickListener?.onReactionClicked(note.id, note, e)
                }
            }
            reactionView.adapter = adapter
            reactionView.layoutManager = linearLayoutManager
            reactionView.visibility = View.VISIBLE
        }

    }

    private fun invisibleSubContents(){
        subUserIcon.visibility = View.GONE
        subUserName.visibility = View.GONE
        subUserId.visibility = View.GONE
        subNoteText.visibility =View.GONE
    }

    private fun setWhoReactionUserLink(user: User?, status: String){
        whoReactionUserLink.visibility = View.VISIBLE
        val text = "${user?.name?:user?.userName}さんが${status}しました"
        injectionTextGoneWhenNull(text, whoReactionUserLink)
        whoReactionUserLink.setOnClickListener{
            if(user != null){
                userClickListener?.onClickedUser(user)
            }
        }
    }


    private fun setNoteContent(note: Note){
        injectionName(note.user?.name, note.user?.userName, userName)
        injectionId(note.user?.userName, note.user?.host, userId)
        roundInjectionImage(note.user?.avatarUrl?:"non", userIcon)
        injectionTextGoneWhenNull(note.text, noteText)
        setRelationUserListener(note.user!!, userName, userId, userIcon)
        setImage(filterImageData(note))

    }

    private fun setSubContent(note: Note){
        injectionName(note.user?.name, note.user?.userName, subUserName)
        injectionId(note.user?.userName, note.user?.host, subUserId)
        roundInjectionImage(note.user?.avatarUrl?:"non", subUserIcon)
        injectionTextGoneWhenNull(note.text, subNoteText)
        setRelationUserListener(note.user!!, subUserName, subUserId, subUserIcon)

    }


    private fun setImage(fileList: List<FileProperty>){

        val imageClickListener = View.OnClickListener { p0 ->
            val clickedImageIndex = when(p0){
                imageView1 -> 0
                imageView2 -> 1
                imageView3 -> 2
                imageView4 -> 3
                else -> 0
            }

            val urlList: List<String> = fileList.map{it.url}.filter{it != null && it.isNotBlank()}.map{it.toString()}
            contentClickListener?.onImageClicked(clickedImageIndex, urlList.toTypedArray())
        }

        imageViewList.forEach{
            it.visibility = View.GONE
            it.setOnClickListener(imageClickListener)
        }


        for(n in 0.until(fileList.size)){
            injectionImage(fileList[n].url!!, imageViewList[n], fileList[n].isSensitive)
        }

    }

    //NP
    private fun setReplyCount(count: Int){
        injectionTextInvisible(count.toString(), replyCount, "0")

    }

    //NP
    private fun setReNoteCount(count: Int){
        injectionTextInvisible(count.toString(), reNoteCount, "0")
    }

    private fun setFourControlButtonListener(note: Note, viewData: NoteViewData){
        replyButton.setOnClickListener {
            contentClickListener?.onReplyButtonClicked(note.id, note)
        }
        reNoteButton.setOnClickListener {
            contentClickListener?.onReNoteButtonClicked(note.id, note)
        }
        reactionButton.setOnClickListener {
            contentClickListener?.onReactionClicked(note.id, note, null)
        }
        descriptionButton.setOnClickListener {
            contentClickListener?.onDescriptionButtonClicked(viewData.note.id, viewData.note)
        }
    }


    private fun backgroundColor(code: Int){
        if(code == 1){
            timelineItem.setBackgroundColor(Color.parseColor("#d3d3d3"))
        }else{
            timelineItem.setBackgroundColor(Color.WHITE/*Color.parseColor("#fff3f3f3")*/)
        }
    }

    //nullの場合はGONE
    private fun injectionTextGoneWhenNull(text: String?, view: TextView){
        if(text == null){
            view.visibility = View.GONE
        }else{
            view.visibility = View.VISIBLE
            view.text= text
        }
    }

    private fun injectionTextInvisible(text: String?, view:TextView, targetValue: String?){
        if(text == targetValue){
            view.visibility = View.INVISIBLE
        }else{
            view.visibility = View.VISIBLE
            view.text= text
        }
    }

    private fun injectionName(name: String?, id: String?, view: TextView){
        view.text = if(name == null) id.toString() else name.toString()
        view.visibility = View.VISIBLE
    }

    private fun injectionId(id: String?, host: String?, view: TextView){
        view.text = if(host == null) "@$id" else "@$id@$host"
        view.visibility = View.VISIBLE
    }

    private fun setRelationUserListener(user: User, vararg viewList: View){
        viewList.forEach {
            it.setOnClickListener{
                userClickListener?.onClickedUser(user)
            }
        }
    }

    private fun setRelationNoteListener(noteId: String, note: Note, vararg  view: View){
        view.forEach {
            it.setOnClickListener{
                contentClickListener?.onNoteClicked(noteId, note)
            }
        }
    }

    private fun filterImageData(data: Note): List<FileProperty>{

        val fileList = data.files ?: return emptyList()
        val nonNullUrlList = ArrayList<FileProperty>()
        for(n in fileList){
            val isImage = n?.type != null && n.type.startsWith("image")
            if(isImage && n?.url != null){
                nonNullUrlList.add(n)
            }
        }
        return nonNullUrlList
    }

    //FIXME picassoに依存してしまているので修正
    private fun injectionImage(imageUrl: String, imageView: ImageView, isSensitive: Boolean?){
        imageView.visibility = View.VISIBLE

        if(isSensitive != null && isSensitive){
            imageView.setImageResource(R.drawable.sensitive_image)
        }else{
            Picasso
                .get()
                .load(imageUrl)
                .into(imageView)
        }

    }

    private fun roundInjectionImage(imageUrl: String, imageView: ImageView){
        val trfm = RoundedTransformation(30, 0)
        Picasso
            .get()
            .load(imageUrl)
            .transform(trfm)
            .into(imageView)
    }

}
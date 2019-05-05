package org.panta.misskey_for_android_v2.view_presenter.note_editor

import org.panta.misskey_for_android_v2.constant.NoteType

import org.panta.misskey_for_android_v2.entity.CreateNoteProperty
import org.panta.misskey_for_android_v2.entity.ConnectionProperty
import org.panta.misskey_for_android_v2.repository.NoteRepository
import java.io.File

class EditNotePresenter(private val mView: EditNoteContract.View, private val connectionInfo: ConnectionProperty) : EditNoteContract.Presenter{

    private val noteBuilder = CreateNoteProperty.Builder(connectionInfo.i)
    private val noteRepository = NoteRepository(connectionInfo.domain)

    private var noteType: NoteType = NoteType.CREATE

    private var fileList = ArrayList<File>()

    override fun setText(text: String) {
        noteBuilder.text = if(text.isBlank()){
            null
        }else{
            text
        }
    }

    override fun setNoteType(type: Int, targetId: String?) {
        this.noteType = NoteType.getEnumFromInt(type)
        if(noteType == NoteType.RE_NOTE){
            noteBuilder.renoteId = targetId!!
        }else if(noteType == NoteType.REPLY){
            noteBuilder.replyId = targetId!!
        }
    }

    override fun postNote() {
        val fileNameList = fileList.map{ it.path }.toTypedArray()
        when(noteType){
            NoteType.CREATE ->{
                val check = ! noteBuilder.text.isNullOrBlank() && noteBuilder.text!!.length < 1500
                if(check) mView.startPost(noteBuilder, fileNameList)

            }
            NoteType.REPLY ->{
                val check = ! noteBuilder.text.isNullOrBlank() && noteBuilder.text!!.length < 1500
                if(check && noteBuilder.replyId != null)mView.startPost(noteBuilder, fileNameList)

            }
            NoteType.RE_NOTE ->{
                if(noteBuilder.renoteId != null){
                    mView.startPost(noteBuilder, fileNameList)
                }
            }
        }
    }

    override fun setVisibility(visibility: String) {
        noteBuilder.visibility = visibility
    }

    override fun setFile(file: File) {
        fileList.add(file)
    }
    override fun start() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
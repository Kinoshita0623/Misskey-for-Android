package org.panta.misskey_for_android_v2.repository

import org.panta.misskey_for_android_v2.constant.getInstanceInfoList
import org.panta.misskey_for_android_v2.entity.ConnectionProperty
import org.panta.misskey_for_android_v2.interfaces.ISharedPreferenceOperator
import org.panta.misskey_for_android_v2.util.sha256

class SecretRepository(private val sharedPreferenceOperator: ISharedPreferenceOperator){

    companion object{
        private const val APP_DOMAIN = "misskey_account_domain"
        private const val APP_USER_TOKEN = "misskey_account_user_token"
        private const val APP_USER_PRIMARY_ID = "misskey_account_primary_id"
    }

    fun getDomain(): String?{
        return sharedPreferenceOperator.getString(APP_DOMAIN, null)
    }

    fun getUserToken(): String?{
        return sharedPreferenceOperator.getString(APP_USER_TOKEN, null)
    }

    fun getUserPrimaryId(): String?{
        return sharedPreferenceOperator.getString(APP_USER_PRIMARY_ID, null)
    }

    fun getConnectionInfo(): ConnectionProperty?{
        val domain = getDomain()
        val userToken = getUserToken()
        val userPrimaryId = getUserPrimaryId()

        val instanceInfoList = getInstanceInfoList()
        val appSecret = instanceInfoList.firstOrNull{
            it.domain == domain
        }?.appSecret
        return if(appSecret == null || domain == null || userToken == null || userPrimaryId == null){
            return null
        }else{
            val i =  sha256("$userToken$appSecret")
            ConnectionProperty(domain, i, userPrimaryId)
        }
        /*return if(appSecret != null && domain != null && userToken != null){
            val i =  sha256("$userToken$appSecret")
            ConnectionProperty(domain, i)
        }else{
            null
        }*/
    }

    fun putDomain(domain: String){
        sharedPreferenceOperator.putString(APP_DOMAIN, domain)
    }

    fun putUserToken(token: String){
        sharedPreferenceOperator.putString(APP_USER_TOKEN, token)
    }

    fun putUserPrimaryId(id: String){
        sharedPreferenceOperator.putString(APP_USER_PRIMARY_ID, id)
    }
}
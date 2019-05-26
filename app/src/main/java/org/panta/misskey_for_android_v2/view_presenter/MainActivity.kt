package org.panta.misskey_for_android_v2.view_presenter

import android.app.UiModeManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager.POP_BACK_STACK_INCLUSIVE
import android.support.v4.view.GravityCompat
import android.support.v4.view.MenuItemCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.app.AppCompatDelegate
import android.support.v7.widget.SwitchCompat
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Switch
import android.widget.Toast
import android.widget.ToggleButton
import com.squareup.picasso.Picasso
import com.vanniktech.emoji.EmojiManager
import com.vanniktech.emoji.twitter.TwitterEmojiProvider
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.nav_header_main.*
import kotlinx.android.synthetic.main.nav_header_main.view.*
import org.panta.misskey_for_android_v2.R
import org.panta.misskey_for_android_v2.constant.ApplicationConstant
import org.panta.misskey_for_android_v2.constant.FollowFollowerType
import org.panta.misskey_for_android_v2.constant.ThemeType
import org.panta.misskey_for_android_v2.constant.TimelineTypeEnum
import org.panta.misskey_for_android_v2.entity.ConnectionProperty
import org.panta.misskey_for_android_v2.entity.User
import org.panta.misskey_for_android_v2.interfaces.ISharedPreferenceOperator
import org.panta.misskey_for_android_v2.interfaces.MainContract
import org.panta.misskey_for_android_v2.service.NotificationService
import org.panta.misskey_for_android_v2.storage.SharedPreferenceOperator
import org.panta.misskey_for_android_v2.util.setThemeFromType
import org.panta.misskey_for_android_v2.view_presenter.follow_follower.FollowFollowerActivity
import org.panta.misskey_for_android_v2.view_presenter.mixed_timeline.MixedTimelineFragment
import org.panta.misskey_for_android_v2.view_presenter.note_editor.EditNoteActivity
import org.panta.misskey_for_android_v2.view_presenter.notification.NotificationFragment
import org.panta.misskey_for_android_v2.view_presenter.timeline.TimelineFragment
import org.panta.misskey_for_android_v2.view_presenter.user.UserActivity
import org.panta.misskey_for_android_v2.view_presenter.user_auth.AuthActivity

private const val FRAGMENT_HOME = "FRAGMENT_HOME"
private const val FRAGMENT_OTHER = "FRAGMENT_OTHER"
//const val DOMAIN_AUTH_KEY_TAG = "MainActivityUserDomainAndAuthKey"


class MainActivity : AbsBaseActivity(), NavigationView.OnNavigationItemSelectedListener, MainContract.View {

    companion object {
        const val SHOW_FRAGMENT_TAG = "MainActivityShowFragmentTag"
        const val HOME = 0
        const val MIX = 1
        const val NOTIFICATION = 2
    }

    //一番目に表示されるフラグメント
    private var showFirstFragment = 0

    override lateinit var mPresenter: MainContract.Presenter
    //private var i: String? = null
    //private var domain: String? = null
    private lateinit var sharedOperator: ISharedPreferenceOperator

    private lateinit var mNotificationEnabledSwitch: Switch

    private lateinit var mUiModeManager: UiModeManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedOperator = SharedPreferenceOperator(this)
        mPresenter = MainPresenter(this, sharedOperator)
        //setThemeFromType(this)
        if(super.isNightMode()){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }else{
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

        EmojiManager.install(TwitterEmojiProvider())
        
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)

        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)

        /*~~basic init*/

        showFirstFragment = intent.getIntExtra(SHOW_FRAGMENT_TAG, 0)


        fab.setOnClickListener {
            mPresenter.takeEditNote()
        }
        mPresenter.start()

        mPresenter.getPersonalMiniProfile()

        mPresenter.initDisplay()

        mPresenter.getPersonalMiniProfile()

        title = "ホーム"

        val header = nav_view.getHeaderView(0)
        header.following_text.setOnClickListener {
            mPresenter.getFollowFollower(FollowFollowerType.FOLLOWING)
        }

        header.follower_text.setOnClickListener{
            mPresenter.getFollowFollower(FollowFollowerType.FOLLOWER)
        }


        val customView = nav_view.menu.findItem(R.id.nav_notification_toggle_switch).actionView
        mNotificationEnabledSwitch = customView.findViewById(R.id.switch_button)
        mNotificationEnabledSwitch.setOnCheckedChangeListener { _, b ->
            mPresenter.isEnabledNotification(b)
        }
        mPresenter.isEnabledNotification()

        mUiModeManager = getSystemService(Context.UI_MODE_SERVICE) as UiModeManager

        //AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)



    }


    override fun initDisplay(connectionInfo: ConnectionProperty) {
        val sf = supportFragmentManager
        val ft = sf.beginTransaction()
        if(showFirstFragment == NOTIFICATION){
            setFragment(NotificationFragment.getInstance(connectionInfo), FRAGMENT_OTHER)
            title = "通知"
        }else{
            ft.replace(R.id.main_container, TimelineFragment.getInstance(info = connectionInfo  , type = TimelineTypeEnum.HOME))
            ft.commit()
        }


        bottom_navigation.setOnNavigationItemSelectedListener {
            return@setOnNavigationItemSelectedListener when(it.itemId){
                R.id.home_timeline ->{
                    setFragment(TimelineFragment.getInstance(connectionInfo, type = TimelineTypeEnum.HOME), FRAGMENT_HOME)
                    title = "ホーム"
                    true
                }
                R.id.mix_timeline ->{
                    setFragment(MixedTimelineFragment.getInstance(connectionInfo), FRAGMENT_OTHER)
                    title = "炊き込みご飯おいしい"
                    true
                }
                R.id.notification_item ->{
                    setFragment(NotificationFragment.getInstance(connectionInfo), FRAGMENT_OTHER)
                    title = "通知"
                    true
                }
                R.id.message_item ->{
                    false
                }
                else -> false

            }
        }


    }

    override fun showAuthActivity() {
        runOnUiThread{
            val intent = Intent(applicationContext, AuthActivity::class.java)
            startActivity(intent)
        }
    }

    override fun showPersonalMiniProfile(user: User) {
        runOnUiThread {
            if(user.avatarUrl != null && my_account_icon != null){
                Picasso
                    .get()
                    .load(user.avatarUrl)
                    .into(my_account_icon)
            }

            my_name?.text = user.name?: user.userName

            val userName = if(user.host != null){
                "@${user.userName}@${user.host}"
            }else{
                "@${user.userName}"
            }
            my_user_name?.text = userName

            follower_count?.text = user.followersCount.toString()
            following_count?.text= user.followingCount.toString()

        }
    }

    override fun showPersonalProfilePage(user: User, connectionInfo: ConnectionProperty) {
        UserActivity.startActivity(applicationContext, user)
    }

    override fun showEditNote(connectionInfo: ConnectionProperty) {
        val intent = Intent(applicationContext, EditNoteActivity::class.java)
        //intent.putExtra(EditNoteActivity.CONNECTION_INFO, info)
        startActivity(intent)
    }

    override fun showFollowFollower(connectionInfo: ConnectionProperty, user: User, type: FollowFollowerType) {
        FollowFollowerActivity.startActivity(applicationContext, type, user.id)
    }

    override fun showMisskeyOnBrowser(url: Uri) {
        startActivity(Intent(Intent.ACTION_VIEW, url))
    }

    override fun showIsEnabledNotification(enabled: Boolean) {
        Log.d("MainActivity", "選択中 $enabled")
        mNotificationEnabledSwitch.isChecked = enabled
    }

    override fun startNotificationService() {
        startService(Intent(applicationContext, NotificationService::class.java))
        Toast.makeText(applicationContext, "通知はONです", Toast.LENGTH_SHORT).show()
    }

    override fun stopNotificationService() {
        stopService(Intent(applicationContext, NotificationService::class.java))
        Toast.makeText(applicationContext, "通知がOFFになりました", Toast.LENGTH_SHORT).show()
    }

    private fun setFragment(fragment: Fragment, fragmentName: String){
        val fm = supportFragmentManager
        val ft = fm.beginTransaction()
        ft.replace(R.id.main_container, fragment)

        val count = fm.backStackEntryCount

        if(fragmentName == FRAGMENT_OTHER){
            ft.addToBackStack(fragmentName)
        }
        ft.commit()

        fm.addOnBackStackChangedListener( object : android.support.v4.app.FragmentManager.OnBackStackChangedListener{
            override fun onBackStackChanged() {
                if(fm.backStackEntryCount <= count){
                    fm.popBackStack(FRAGMENT_OTHER, POP_BACK_STACK_INCLUSIVE)
                    fm.removeOnBackStackChangedListener(this)
                    bottom_navigation.menu.getItem(0).isChecked = true
                }
            }
        })
    }

    override fun onBackPressed() {

        //NavigationDrawerが開いているときに戻るボタンを押したときの動作
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
            return
        }
        val selectedItemId = bottom_navigation?.selectedItemId
        if(R.id.home_timeline != selectedItemId){
            bottom_navigation.selectedItemId = R.id.home_timeline
        }else{
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_profile -> mPresenter.getPersonalProfilePage()
            R.id.nav_open_web_misskey -> mPresenter.openMisskeyOnBrowser()
            R.id.nav_ui_mode -> {
                //AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)

                if(super.isNightMode()){
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    super.putTheme(false)

                }else{
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    super.putTheme(true)
                    //mUiModeManager.nightMode = UiModeManager.MODE_NIGHT_NO
                    //mUiModeManager.nightMode = UiModeManager.MODE_NIGHT_YES

                }
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return false
    }

}

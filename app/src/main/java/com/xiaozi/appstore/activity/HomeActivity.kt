package com.xiaozi.appstore.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import cc.fish.cld_ctrl.appstate.entity.RespUpdate
import cc.fish.coreui.BaseFragment
import cc.fish.coreui.BaseFragmentActivity
import com.xiaozi.appstore.R
import com.xiaozi.appstore.activity.fragments.AppFragment
import com.xiaozi.appstore.activity.fragments.GameFragment
import com.xiaozi.appstore.activity.fragments.HomeFragment
import com.xiaozi.appstore.activity.fragments.MineFragment
import com.xiaozi.appstore.plugin.ZToast
import com.xiaozi.appstore.plugin.safety
import kotlinx.android.synthetic.main.a_home.*

/**
 * Created by fish on 18-1-4.
 */
class HomeActivity : BaseFragmentActivity() {

    companion object {
        private val KEY_IS_UPDATE = "IS_UPDATE"
        private val KEY_UPDATE_DATA = "IS_UPDATE"
        fun open(ctx: Context, update: RespUpdate?) {
            ctx.startActivity(Intent(ctx, HomeActivity::class.java).apply {
                putExtra(KEY_IS_UPDATE, update != null)
                putExtra(KEY_UPDATE_DATA, update ?: return@apply)
            })
        }
    }

    private val BOTTOM_ICONS_UNCHECKED = arrayOf(
            R.drawable.icon_home_tab_main_un,
            R.drawable.icon_home_tab_app_un,
            R.drawable.icon_home_tab_game_un,
            R.drawable.icon_home_tab_mine_un
    )
    private val BOTTOM_ICONS_CHECKED = arrayOf(
            R.drawable.icon_home_tab_main,
            R.drawable.icon_home_tab_app,
            R.drawable.icon_home_tab_game,
            R.drawable.icon_home_tab_mine)
    private val BOTTOM_TEXT_ARRAY = arrayOf("首页", "应用", "游戏", "我的")
    private val FRAGMENTS_KLS = arrayOf(
            HomeFragment::class.java,
            AppFragment::class.java,
            GameFragment::class.java,
            MineFragment::class.java)
    private val mBtmParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1.0f)
    val TAB_MAIN = 0
    val TAB_APPS = 1
    val TAB_GAME = 2
    val TAB_MINE = 3

    private var mCheckedFragmentID = TAB_MAIN
    private var mLastClick: Long = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.a_home)
        super.onCreate(savedInstanceState)
        if (intent.getBooleanExtra(KEY_IS_UPDATE, false))
            ZToast("null update info")
        else
            ZToast(intent.getSerializableExtra(KEY_UPDATE_DATA).toString())
    }

    override fun onItemClick(item: View?, index: Int) {
        mCheckedFragmentID = index
    }

    override fun initView() {
    }

    override fun putFragments() = FRAGMENTS_KLS

    override fun getBottomItemView(index: Int) = bottomLayoutInflater.inflate(R.layout.l_home_bottom, null).apply {
        (findViewById<LinearLayout>(R.id.home_page_bottom_layout)).layoutParams = mBtmParams
        findViewById<ImageView>(R.id.home_page_bottom_image).setImageResource(BOTTOM_ICONS_UNCHECKED[index])
        (findViewById<TextView>(R.id.home_page_bottom_btn_name)).text = BOTTOM_TEXT_ARRAY[index]
    }

    override fun getFLid() = R.id.fl_home_body

    override fun getBottomLayout() = ll_home_bottom

    override fun checkAllBottomItem(item: View?, position: Int, isChecked: Boolean) {
        item.safety { findViewById<ImageView>(R.id.home_page_bottom_image).setImageResource(if (isChecked) BOTTOM_ICONS_CHECKED[position] else BOTTOM_ICONS_UNCHECKED[position]) }
    }

    override fun onBackPressed() {
        if (System.currentTimeMillis() - mLastClick < 2000) {
            val homeIntent = Intent(Intent.ACTION_MAIN)
            homeIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            homeIntent.addCategory(Intent.CATEGORY_HOME)
            startActivity(homeIntent)
        } else {
            ZToast("再按一下后退键退出程序")
            mLastClick = System.currentTimeMillis()
        }
    }

    fun changeFragment(index: Int) = setTabSel(bottomLayout?.getChildAt(index), index)
}
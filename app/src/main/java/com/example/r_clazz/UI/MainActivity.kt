package com.example.r_clazz.UI

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.r_clazz.Fragments.Classes
import com.example.r_clazz.Fragments.Discover
import com.example.r_clazz.Fragments.Me
import com.example.r_clazz.Fragments.Message
import com.example.r_clazz.R
import kotlin.system.exitProcess

class MainActivity : AppCompatActivity(),View.OnClickListener {
    lateinit var classes:RelativeLayout
    lateinit var message:RelativeLayout
    lateinit var discover:RelativeLayout
    lateinit var me:RelativeLayout

    private var class_fragment:Fragment?=null
    private var message_fragment:Fragment?=null
    private var discover_fragment:Fragment?=null
    private var me_fragment:Fragment?=null
    private var currentFragment:Fragment?=null

    lateinit var class_pic:ImageView
    lateinit var message_pic:ImageView
    lateinit var discover_pic:ImageView
    lateinit var me_pic:ImageView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
        initUI()
        initTab()
    }


    fun initUI(){
        classes = findViewById(R.id.rl_home)
        message = findViewById(R.id.rl_online)
        discover = findViewById(R.id.rl_discover)
        me = findViewById(R.id.rl_me)

        classes.setOnClickListener(this)
        message.setOnClickListener(this)
        discover.setOnClickListener(this)
        me.setOnClickListener(this)

        class_pic = findViewById(R.id.iv_home)
        message_pic = findViewById(R.id.iv_i_online)
        discover_pic = findViewById(R.id.iv_discover)
        me_pic = findViewById(R.id.iv_me)
    }

    fun initTab(){
        if (class_fragment == null){
            class_fragment = Classes()
        }
        if (!class_fragment!!.isAdded()) {
            // 提交事务
            supportFragmentManager.beginTransaction()
                .add(R.id.content_layout, class_fragment!!).commit()
            // 记录当前Fragment
            currentFragment = class_fragment
            // 设置图片文本的变化
            class_pic.setImageResource(R.mipmap.home_3_glyph_24)
            message_pic.setImageResource(R.mipmap.calendar_2_outline_24)
            me_pic.setImageResource(R.mipmap.single_01_outline_24)
            discover_pic.setImageResource(R.mipmap.phone_outline_24)
        }
    }
    override fun onClick(view: View) {
        when (view.id) {
            R.id.rl_home // 课程
            -> clickTab1Layout()
            R.id.rl_online // 私信
            -> clickTab2Layout()
            R.id.rl_discover // 发现
            -> clickTab4Layout()
            R.id.rl_me // 我的
            -> clickTab5Layout()
            else -> {
            }
        }
    }
    /**
     * 点击第一个tab
     */
    private fun clickTab1Layout() {
        if (class_fragment == null) {
            class_fragment = Classes()
        }
        addOrShowFragment(supportFragmentManager.beginTransaction(), class_fragment!!)

        // 设置底部tab变化
        class_pic.setImageResource(R.mipmap.home_3_glyph_24)
        message_pic.setImageResource(R.mipmap.calendar_2_outline_24)
        me_pic.setImageResource(R.mipmap.single_01_outline_24)
        discover_pic.setImageResource(R.mipmap.phone_outline_24)
    }
    /**
     * 点击第二个tab
     */
    private fun clickTab2Layout() {
        if (message_fragment == null) {
            message_fragment = Message()
        }
        addOrShowFragment(supportFragmentManager.beginTransaction(), message_fragment!!)
        class_pic.setImageResource(R.mipmap.home_3_outline_24)
        message_pic.setImageResource(R.mipmap.calendar_2_glyph_24)
        me_pic.setImageResource(R.mipmap.single_01_outline_24)
        discover_pic.setImageResource(R.mipmap.phone_outline_24)

    }

    /**
     * 点击第三个tab
     */
    private fun clickTab4Layout() {
        if (discover_fragment == null) {
            discover_fragment = Discover()
        }
        addOrShowFragment(supportFragmentManager.beginTransaction(), discover_fragment!!)

        class_pic.setImageResource(R.mipmap.home_3_outline_24)
        message_pic.setImageResource(R.mipmap.calendar_2_outline_24)
        me_pic.setImageResource(R.mipmap.single_01_outline_24)
        discover_pic.setImageResource(R.mipmap.phone_glyph_24)

    }

    /**
     * 点击第三个tab
     */
    private fun clickTab5Layout() {
        if (me_fragment == null) {
            me_fragment = Me()
        }
        addOrShowFragment(supportFragmentManager.beginTransaction(), me_fragment!!)

        class_pic.setImageResource(R.mipmap.home_3_outline_24)
        message_pic.setImageResource(R.mipmap.calendar_2_outline_24)
        me_pic.setImageResource(R.mipmap.single_01_glyph_24)
        discover_pic.setImageResource(R.mipmap.phone_outline_24)

    }

    /**
     * 加入或者显示碎片
     *
     * @param transaction
     * @param fragment
     */
    private fun addOrShowFragment(
        transaction: FragmentTransaction,
        fragment: Fragment
    ) {
        if (currentFragment === fragment)
            return
        if (!fragment.isAdded) { // 假设当前fragment未被加入，则加入到Fragment管理器中
            transaction.hide(currentFragment!!)
                .add(R.id.content_layout, fragment).commit()
        } else {
            transaction.hide(currentFragment!!).show(fragment).commit()
        }
        currentFragment = fragment
    }
    //退出时的时间
    private var mExitTime: Long = 0

    //对返回键进行监听
   override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {

        if (keyCode == KeyEvent.KEYCODE_BACK && event.repeatCount == 0) {
            exit()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    //退出方法
    private fun exit() {
        if (System.currentTimeMillis() - mExitTime > 2000) {
            Toast.makeText(this@MainActivity, "再按一次退出应用", Toast.LENGTH_SHORT).show()
            mExitTime = System.currentTimeMillis()
        } else {
            //用户退出处理
            finish()
            exitProcess(0)
        }
    }

}

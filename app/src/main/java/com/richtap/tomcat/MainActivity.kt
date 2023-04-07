package com.richtap.tomcat

import android.graphics.drawable.AnimationDrawable
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.SoundPool
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.MotionEvent
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.richtap.tomcat.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityMainBinding

    // 手指按下的点为(x1, y1)手指离开屏幕的点为(x2, y2)
    private var x1 = 0f
    private var x2 = 0f
    private var y1 = 0f
    private var y2 = 0f
    // SoundPool可以同时播放多个声音 短声音
    private var pool: SoundPool? = null
    private var soundList = ArrayList<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar!!.hide()
        hideStatusBar()

        binding.btnPokeBelly.setOnClickListener(this)
        binding.btnFoot.setOnClickListener(this)
        binding.btnTail.setOnClickListener(this)
        binding.btnLeftFace.setOnClickListener(this)
        binding.btnRightFace.setOnClickListener(this)

        initSoundList()
        // 动画集
        val anim = binding.layoutAnimation.drawable as AnimationDrawable
        anim.start()
    }

    private fun initSoundList() {
        // SoundPool需要分版本进行处理（sdk版本21前后）
        val builder = SoundPool.Builder()
        // 传入最多播放音频数量,
        builder.setMaxStreams(1)
        // AudioAttributes是一个封装音频各种属性的方法
        val attrBuilder = AudioAttributes.Builder()
        // 设置音频流的合适的属性
        attrBuilder.setLegacyStreamType(AudioManager.STREAM_MUSIC)
        // 加载一个AudioAttributes
        builder.setAudioAttributes(attrBuilder.build())
        pool = builder.build()

        // 将音乐加载到集合中
        soundList.add(0, pool!!.load(this, R.raw.fart003_11025, 1))
        soundList.add(1, pool!!.load(this, R.raw.cymbal, 1))
        soundList.add(2, pool!!.load(this, R.raw.p_poke_foot3, 1))
        soundList.add(3, pool!!.load(this, R.raw.p_belly, 1))
        soundList.add(4, pool!!.load(this, R.raw.p_foot, 1))
        soundList.add(5, pool!!.load(this, R.raw.angry, 1))
        soundList.add(6, pool!!.load(this, R.raw.miaoaoao, 1))
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event?.action == MotionEvent.ACTION_DOWN) {
            // 当手指按下的时候
            x1 = event.x
            y1 = event.y
        }
        if (event?.action == MotionEvent.ACTION_UP) {
            // 当手指离开的时候
            x2 = event.x
            y2 = event.y
            if (x1 - x2 > 100) { // 向左滑
            } else if (x2 - x1 > 100) { // 向右滑
                playAnimation(R.drawable.fart)
                playSound(soundList[2])
            }
            if (y2 - y1 > 100) { // 向下滑
                playAnimation(R.drawable.touch_nose)
                playSound(soundList[6])
            }
        }
        return super.onTouchEvent(event)
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.layout_animation -> {}
            R.id.btn_pokeBelly -> {
                playAnimation(R.drawable.poke_belly_left)
                playSound(soundList[3])
            }
            R.id.btn_foot -> {
                playAnimation(R.drawable.poke_foot)
                playSound(soundList[4])
            }
            R.id.btn_tail -> {
                playAnimation(R.drawable.poke_belly_right)
                playSound(soundList[5])
            }
            R.id.btn_leftFace -> {
                playAnimation(R.drawable.swipe_left)
                playSound(soundList[1])
            }
            R.id.btn_rightFace -> {
                playAnimation(R.drawable.swipe_right)
                playSound(soundList[1])
            }
            else -> {}
        }
    }

    /**
     * 播放声音
     */
    private fun playSound(soundId: Int) {
        pool!!.play(soundId, 1f, 1f, 1, 0, 1f) // 声音id,左声道，右声道，优先级，循环，速率
    }

    /**
     * 播放动画
     */
    private fun playAnimation(res: Int) {
        binding.layoutAnimation.setImageResource(res)
        val anim = binding.layoutAnimation.drawable as AnimationDrawable
        anim.isOneShot = true // 是否播放一次(true将循环一次，然后停止并保持最后一帧。如果它设置为false，则动画将循环)
        if (anim.isRunning) { // 如果动画已经播放过
            anim.stop() // 如果动画运行了，停止动画，动画才可以重新播放
        }
        // 启动动画
        anim.start()
        var time = 0
        for (i in 0 until anim.numberOfFrames) {
            time += anim.getDuration(i)
        }
        Handler(mainLooper).postDelayed({ // 重新设置背景,并播放动画
            binding.layoutAnimation.setImageResource(R.drawable.breath)
            val anim2 = binding.layoutAnimation.drawable as AnimationDrawable
            anim2.start()
        }, time.toLong())
    }

    private fun hideStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            //window.insetsController?.hide(WindowInsets.Type.statusBars())
            window.insetsController?.hide(WindowInsets.Type.systemBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }
    }
}
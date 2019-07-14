package com.nitkarsh.infogit

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.nitkarsh.infogit.utils.Fonts
import kotlinx.android.synthetic.main.activity_splash.*
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class SplashActivity : AppCompatActivity(), CoroutineScope {

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job + exception

    private lateinit var job: Job

    private val exception = CoroutineExceptionHandler { _, exception -> run { exception.printStackTrace() } }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        job = Job()
        tvWelcome.typeface = Fonts.mavenMedium(this)

//       Coroutines for activate to another activity after a delay of1500 ms on IO thread
        GlobalScope.launch(Dispatchers.IO + job + exception) {
            delay(1500)
            startActivity(Intent(this@SplashActivity, MainActivity::class.java))
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right)
        }
    }

    override fun onResume() {
        if (job.isCancelled) {
            job.start()
        }
        super.onResume()
    }

    override fun onDestroy() {
        job.cancel()
        super.onDestroy()
    }
}

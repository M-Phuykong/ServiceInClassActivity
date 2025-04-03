package edu.temple.myapplication

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.widget.Button
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    lateinit var timeBinder: TimerService.TimerBinder
    lateinit var timerTextView: TextView
    var isConnected = false

    val timeHandler = Handler(Looper.getMainLooper()){
        timerTextView.text = it.what.toString()
        true
    }

    val serviceConnect = object: ServiceConnection{

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            timeBinder = service as TimerService.TimerBinder
            timeBinder.setHandler(timeHandler)
            isConnected = true
        }

        override fun onServiceDisconnected(p0: ComponentName?) {
            isConnected = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        timerTextView = findViewById<TextView>(R.id.textView)

        bindService(
            Intent(this, TimerService::class.java),
            serviceConnect,
            BIND_AUTO_CREATE
        )

        findViewById<Button>(R.id.startButton).setOnClickListener {
            if (isConnected) {
                if (!timeBinder.isRunning) {
                    timeBinder.start(15)
                }
                else {
                    timeBinder.pause()
                }

            }
        }
        
        findViewById<Button>(R.id.stopButton).setOnClickListener {
            if (isConnected) timeBinder.stop()
        }
    }

    override fun onDestroy() {
        unbindService(serviceConnect)
        super.onDestroy()
    }
}


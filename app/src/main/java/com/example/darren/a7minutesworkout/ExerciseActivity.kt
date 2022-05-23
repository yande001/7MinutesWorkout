package com.example.darren.a7minutesworkout

import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.darren.a7minutesworkout.databinding.ActivityExerciseBinding
import java.util.*
import kotlin.collections.ArrayList

class ExerciseActivity : AppCompatActivity(), TextToSpeech.OnInitListener {
    private var binding: ActivityExerciseBinding? = null
    private var restTimer: CountDownTimer? = null
    private var restTimerDuration: Long = 10000
    private var restProgress = 0

    private var exerciseTimer: CountDownTimer? = null
    private var exerciseTimerDuration: Long = 30000
    private var exerciseProgress = 0

    private var exerciseList: ArrayList<ExerciseModel>? = null
    private var currentExercisePosition = -1

    private var tts: TextToSpeech? = null

    private var player: MediaPlayer? = null

    private var exerciseStatusAdapter: ExerciseStatusAdapter? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExerciseBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        setSupportActionBar(binding?.toolBarExercise)

        if (supportActionBar != null){
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }

        exerciseList = Constants.defaultExerciseList()

        tts = TextToSpeech(this, this)

        binding?.toolBarExercise?.setNavigationOnClickListener {
            onBackPressed()
        }

        resetRestTimer()
        setUpExerciseStatusRV()
    }

    private fun setUpExerciseStatusRV(){
        binding?.rvExerciseStatus?.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        exerciseStatusAdapter = exerciseList?.let { ExerciseStatusAdapter(it) }
        binding?.rvExerciseStatus?.adapter = exerciseStatusAdapter

    }

    private fun startRestTimer(){

        restTimer = object:CountDownTimer(1000, 1000){
            override fun onTick(millisUtilFinish: Long) {
                restProgress++
                binding?.progressBarRest?.progress = 10 - restProgress
                binding?.tvTimerRest?.text = (millisUtilFinish/1000).toString()
            }
            override fun onFinish() {
                currentExercisePosition++
                exerciseList!![currentExercisePosition].setIsSelected(true)
                exerciseStatusAdapter!!.notifyDataSetChanged()
                resetExerciseTimer()
            }
        }.start()
    }

    private fun startExerciseTimer(){

        exerciseTimer = object: CountDownTimer(1000, 1000){
            override fun onTick(millisUntilFinish: Long) {
                exerciseProgress++
                binding?.progressBarExercise?.progress = 30 - exerciseProgress
                binding?.tvTimerExercise?.text = (millisUntilFinish/1000).toString()
            }
            override fun onFinish() {
                if(currentExercisePosition < exerciseList!!.size - 1 ) {
                    exerciseList!![currentExercisePosition].setIsSelected(false)
                    exerciseList!![currentExercisePosition].setIsCompleted(true)
                    exerciseStatusAdapter!!.notifyDataSetChanged()
                    resetRestTimer()
                }else{
                    val intent = Intent(this@ExerciseActivity, FinishActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
        }.start()
    }

    private fun resetRestTimer(){

        try{
            val soundURI = Uri.parse("android.resource://com.example.darren.a7minutesworkout/"
                + R.raw.press_start)
            player = MediaPlayer.create(applicationContext, soundURI)
            player?.isLooping = false
            player?.start()
        } catch (e: Exception){
            e.printStackTrace()
        }

        binding?.ivExercise?.visibility = View.GONE
        binding?.flExercise?.visibility = View.GONE
        binding?.tvExerciseName?.visibility = View.GONE
        binding?.flRest?.visibility = View.VISIBLE
        binding?.tvGetReady?.visibility = View.VISIBLE
        binding?.tvUpcomingExerciseLabel?.visibility = View.VISIBLE
        binding?.tvUpcomingExerciseName?.visibility = View.VISIBLE

        restTimer?.let {
            it.cancel()
            restProgress = 0
        }

        binding?.tvUpcomingExerciseName?.text = exerciseList!![currentExercisePosition+1].getName()


        startRestTimer()
    }

    private fun resetExerciseTimer(){
        binding?.flRest?.visibility = View.GONE
        binding?.tvTimerExercise?.text = (exerciseTimerDuration/1000).toString()
        binding?.ivExercise?.visibility = View.VISIBLE
        binding?.tvGetReady?.visibility = View.GONE
        binding?.tvExerciseName?.text = exerciseList!![currentExercisePosition].getName()
        binding?.ivExercise?.setImageResource(exerciseList!![currentExercisePosition].getImage())
        binding?.flExercise?.visibility = View.VISIBLE
        binding?.tvExerciseName?.visibility = View.VISIBLE
        binding?.tvUpcomingExerciseLabel?.visibility = View.GONE
        binding?.tvUpcomingExerciseName?.visibility = View.GONE


        exerciseTimer?.let {
            it.cancel()
            exerciseProgress = 0
        }

        startExerciseTimer()

        speakOut(exerciseList!![currentExercisePosition].getName())


    }

    override fun onDestroy() {
        super.onDestroy()
        restTimer?.let {
            it.cancel()
            restProgress = 0
        }

        exerciseTimer?.let {
            it.cancel()
            exerciseProgress = 0
        }
        tts?.let {
            it.stop()
            it.shutdown()
        }
        player?.let {
            it.stop()
        }

        binding = null
    }

    override fun onInit(status: Int) {
        if(status == TextToSpeech.SUCCESS){
            val result = tts!!.setLanguage(Locale.US)

            if (result == TextToSpeech.LANG_MISSING_DATA ||
                result == TextToSpeech.LANG_NOT_SUPPORTED){
                Log.e("TTS","Language not supported!")
            }
        } else{
            Log.e("TTS","Initialization failed!")
        }
    }

    private fun speakOut(text: String){
        tts!!.speak(text, TextToSpeech.QUEUE_FLUSH, null, "")
    }

}
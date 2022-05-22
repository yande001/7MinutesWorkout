package com.example.darren.a7minutesworkout

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.Toast
import com.example.darren.a7minutesworkout.databinding.ActivityExerciseBinding

class ExerciseActivity : AppCompatActivity() {
    private var binding: ActivityExerciseBinding? = null
    private var restTimer: CountDownTimer? = null
    private var restTimerDuration: Long = 10000
    private var restProgress = 0

    private var exerciseTimer: CountDownTimer? = null
    private var exerciseTimerDuration: Long = 30000
    private var exerciseProgress = 0

    private var exerciseList: ArrayList<ExerciseModel>? = null
    private var currentExercisePosition = -1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExerciseBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        setSupportActionBar(binding?.toolBarExercise)

        if (supportActionBar != null){
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }

        exerciseList = Constants.defaultExerciseList()

        binding?.toolBarExercise?.setNavigationOnClickListener {
            onBackPressed()
        }

        resetRestTimer()
    }

    private fun startRestTimer(){

        restTimer = object:CountDownTimer(restTimerDuration, 1000){
            override fun onTick(millisUtilFinish: Long) {
                restProgress++
                binding?.progressBarRest?.progress = 10 - restProgress
                binding?.tvTimerRest?.text = (millisUtilFinish/1000).toString()
            }
            override fun onFinish() {
                currentExercisePosition++
                startExerciseTimer()
            }
        }.start()
    }

    private fun startExerciseTimer(){

        resetExerciseTimer()

        exerciseTimer = object: CountDownTimer(5000, 1000){
            override fun onTick(millisUntilFinish: Long) {
                exerciseProgress++
                binding?.progressBarExercise?.progress = 30 - exerciseProgress
                binding?.tvTimerExercise?.text = (millisUntilFinish/1000).toString()
            }
            override fun onFinish() {
                if(currentExercisePosition < exerciseList!!.size - 1 ) {

                    resetRestTimer()
                }else{
                    Toast.makeText(this@ExerciseActivity, "Finished workout", Toast.LENGTH_LONG).show()
                }
            }
        }.start()
    }

    private fun resetRestTimer(){
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

        binding?.tvUpcomingExerciseName?.text = exerciseList!![currentExercisePosition+1].getName()


    }

    override fun onDestroy() {
        super.onDestroy()
        resetRestTimer()
        exerciseTimer?.let {
            it.cancel()
            exerciseProgress = 0
        }
        binding = null
    }


}
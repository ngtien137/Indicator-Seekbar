package com.luza.indicatorseekbardemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.luza.indicatorseekbar.IndicatorSeekbar
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(),View.OnClickListener {

    private val TAG = IndicatorSeekbar.TAG

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        seekbar.setListener(object:IndicatorSeekbar.ISeekbarListener{
            override fun onSeeking(progress: Int) {
                Log.e(TAG,"Seeking, Progress: $progress")
            }

            override fun onStopSeeking(progress: Int) {
                Log.e(TAG,"Stop Seeking, Progress: $progress")
            }
        })
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.btnProgress->{
                if (edtProgress.text.isNotEmpty())
                    seekbar.setProgress(edtProgress.text.toString().toInt())
            }
            R.id.btnRange->{
                val min = edtMin.text.toString()
                val max = edtMax.text.toString()
                if (min.isNotEmpty()&&max.isNotEmpty()){
                    seekbar.setRange(min.toInt(),max.toInt())
                }
            }
        }
    }

}

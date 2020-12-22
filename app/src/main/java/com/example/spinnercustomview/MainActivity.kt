package com.example.spinnercustomview

import android.content.Context
import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.transition.ChangeBounds
import android.transition.TransitionManager
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.Transition
import com.example.expandablespinner.ExpandableSpinner
import java.util.zip.Inflater
const val TAG = "fatal_error"

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val spinner = findViewById<ExpandableSpinner>(R.id.mainSpinner)
        val root = findViewById<LinearLayout>(R.id.root_view)
        val listTwo = arrayListOf<String>("Ленинский", "Сталинский", "Путинский")
        val bg = findViewById<FrameLayout>(R.id.bg_container)

        spinner.init(root,listTwo,R.layout.main_spinner_item,R.id.spinner_text_view,{ view: TextView, item: String ->
            view.text = item
        }){item ->
            Log.d(TAG, "onCreate: $item")
        }

        spinner.setOnExpandChangeListener { isExpanded ->
            val params = bg.layoutParams
            if(!isExpanded){
                params.width = dpToPx(300)
            }else{
                params.width = ViewGroup.LayoutParams.MATCH_PARENT
            }
            bg.layoutParams = params
            Log.d(TAG, "onCreate: $isExpanded")
        }
//        spinner.isAnimateWidth(true,root,500)
    }
}

fun dpToPx(dp: Int): Int {
    return (dp * Resources.getSystem().displayMetrics.density).toInt()
}

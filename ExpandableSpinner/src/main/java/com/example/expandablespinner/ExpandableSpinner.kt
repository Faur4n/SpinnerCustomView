package com.example.expandablespinner

import android.animation.LayoutTransition
import android.content.Context
import android.media.Image
import android.transition.ChangeBounds
import android.transition.Transition
import android.transition.TransitionManager
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.AutoTransition


class ExpandableSpinner : FrameLayout {

    constructor(context: Context) : super(context)

    constructor(context: Context,attrs: AttributeSet) : super(context,attrs)

    constructor(context: Context,attrs: AttributeSet,defStyleAttr: Int ) : super(context,attrs,defStyleAttr)

    constructor(context: Context,attrs: AttributeSet,defStyleAttr: Int, defStyleableRes: Int )
            : super(context,attrs,defStyleAttr,defStyleableRes)

    private var isExpanded = false
    private var layoutManager : SpinnerLinearLayoutManager? = null
    private var arrowView : ImageView? = null
    private var sceneRoot : ViewGroup = this
    private lateinit var recyclerView: RecyclerView
    private var minHeight = 0
    private var expandChangeListener : ((Boolean) -> Unit)? = null

    init {
        init(context)
    }

    private var duration = 500L

    fun setDuration(value : Long){
        duration = value
    }

    fun <T>init(root: ViewGroup,
                items: ArrayList<T>,
                @LayoutRes layoutId : Int,
                @IdRes textViewId : Int,
                onBind: ((view: TextView,item : T) -> Unit)? = null,
                listener: (item: T) -> Unit,
    ){
        sceneRoot = root

        val adapter = SpinnerAdapter(layoutId, textViewId, items){item, isChanged ->

            val transition = AutoTransition()
            transition.duration = duration/2
            androidx.transition.TransitionManager.beginDelayedTransition(sceneRoot, transition)

            if (isExpanded) {
                isExpanded = false
                collapseRecycler(recyclerView, minHeight)
            } else {
                isExpanded = true
                expandRecycler(recyclerView)
            }
            expandChangeListener?.invoke(isExpanded)

            if(isChanged)
                listener(item)
        }
        adapter.hasStableIds()

        if (onBind != null) {
            adapter.setOnBindCallback(onBind)
        }

        recyclerView.adapter = adapter
    }

    fun setOnExpandChangeListener(listener : (isExpanded : Boolean) -> Unit){
        expandChangeListener = listener
    }

    private fun init(context: Context) {
        val view = inflate(context, R.layout.spinner_layout, this)
        Log.d("fatal_error", "init: $")
        recyclerView = view.findViewById(R.id.main_recycler)
        arrowView = view.findViewById(R.id.spinner_arrow)
        layoutManager = SpinnerLinearLayoutManager(context)
        recyclerView.layoutManager = layoutManager
        recyclerView.overScrollMode = View.OVER_SCROLL_NEVER

        val tv = TypedValue()
        minHeight = if(context.theme.resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            TypedValue.complexToDimensionPixelSize(tv.data, resources.displayMetrics)
        }else{
            0
        }
        collapseRecycler(recyclerView,minHeight)

    }

    private fun collapseRecycler(view: RecyclerView, height : Int){
        val params = view.layoutParams
        params.height = height
        view.layoutParams = params
        val animator = arrowView?.animate()
        animator?.rotation(0f)
        animator?.duration = duration
        animator?.start()

        layoutManager?.setScrollEnabled(isExpanded)
    }

    private fun expandRecycler(view: RecyclerView){
        val params = view.layoutParams
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT
        view.layoutParams = params
        val animator = arrowView?.animate()
        animator?.rotation(180f)
        animator?.duration = duration
        animator?.start()

        layoutManager?.setScrollEnabled(isExpanded)
    }

}

class SpinnerLinearLayoutManager(context: Context?) : LinearLayoutManager(context) {
    private var isScrollEnabled = true

    fun setScrollEnabled(isEnabled : Boolean){
        isScrollEnabled = isEnabled
    }

    override fun canScrollVertically(): Boolean {
        return isScrollEnabled && super.canScrollVertically()
    }
}
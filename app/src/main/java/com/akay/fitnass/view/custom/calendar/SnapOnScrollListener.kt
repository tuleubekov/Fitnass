package com.akay.fitnass.view.custom.calendar

import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper

class SnapOnScrollListener(private val snapHelper: SnapHelper,
                           var onSnapPositionChangeListener: OnSnapPositionChangeListener? = null
) : RecyclerView.OnScrollListener() {

    private var snapPosition = RecyclerView.NO_POSITION

    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
        if (newState == RecyclerView.SCROLL_STATE_IDLE) {
            maybeNotifySnapPositionChange(recyclerView)
        }
    }

    private fun maybeNotifySnapPositionChange(recyclerView: RecyclerView) {
        val snapPosition = snapHelper.getSnapPosition(recyclerView)
        val snapPositionChanged = this.snapPosition != snapPosition
        if (snapPositionChanged) {
            onSnapPositionChangeListener?.onSnapPositionChange(snapPosition)
            this.snapPosition = snapPosition
        }
    }

}

fun SnapHelper.getSnapPosition(recyclerView: RecyclerView): Int {
    val layoutManager = recyclerView.layoutManager ?: return RecyclerView.NO_POSITION
    val snapView = findSnapView(layoutManager) ?: return RecyclerView.NO_POSITION
    return layoutManager.getPosition(snapView)
}
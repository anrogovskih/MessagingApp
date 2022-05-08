package com.axmor.fsinphone.videomessages.ui.listeners

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import timber.log.Timber

abstract class EndlessRecyclerOnScrollListener(private val mLinearLayoutManager: LinearLayoutManager) :
    RecyclerView.OnScrollListener() {

    private var previousTotal = 0 // The total number of items in the data set after the last load
    private var loading = true // True if we are still waiting for the last set of data to load.
    private val visibleThreshold = 5 // The minimum amount of items to have below your current scroll position before loading more.
    private var firstVisibleItem: Int = 0
    private var visibleItemCount: Int = 0
    private var totalItemCount: Int = 0
    private var total: Int? = null

    /**
     * This should be called when some items are removed from RecyclerView
     */
    fun reset(){
        previousTotal = 0
    }

    /**
     * If total is unknown, just ignore this method
     */
    fun setTotal(total: Int?){
        this.total = total
    }

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)

        visibleItemCount = recyclerView.childCount
        totalItemCount = mLinearLayoutManager.itemCount
        firstVisibleItem = mLinearLayoutManager.findFirstVisibleItemPosition()
        Timber.d("onScrolled, loading = $loading, previousTotal = $previousTotal, totalItemCount = $totalItemCount, visibleItemCount = $visibleItemCount, firstVisibleItem = $firstVisibleItem")

        //check if there are more items to load
        if (totalItemCount >= total?: Int.MAX_VALUE) return

        //if totalItemCount has changed, it means we have loaded the next page
        if (loading) {
            if (totalItemCount > previousTotal) {
                loading = false
                previousTotal = totalItemCount
            }
        }

        //if we have reached the threshold and not loading, load more
        if (!loading && totalItemCount - visibleItemCount <= firstVisibleItem + visibleThreshold) {
            loading = true
            onLoadMore()
        }
    }

    abstract fun onLoadMore()
}
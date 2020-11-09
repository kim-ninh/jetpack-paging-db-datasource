package paging.android.example.com.pagingsample

import android.annotation.SuppressLint
import android.util.Log
import androidx.paging.PositionalDataSource
import androidx.room.InvalidationTracker

@SuppressLint("RestrictedApi")
class CheesePositionalDataSource(
        private val db: CheeseDb
): PositionalDataSource<Cheese>() {

    private val observer: InvalidationTracker.Observer =
            object : InvalidationTracker.Observer("Cheese") {
                override fun onInvalidated(tables: MutableSet<String>) {
                    invalidate()
                }
            }

    init {
        db.invalidationTracker.addWeakObserver(observer)
    }

    override fun loadInitial(params: LoadInitialParams, callback: LoadInitialCallback<Cheese>) {
        val totalCount = db.cheeseDao().countTotal()
        val loadPosition = computeInitialLoadPosition(params, totalCount)
        val loadSize = computeInitialLoadSize(params, loadPosition, totalCount)

        Log.i(TAG, "loadInitial: startPosition $loadPosition, size $loadSize")
        val cheeses = db.cheeseDao().allCheesesByName(loadPosition, loadSize)
        callback.onResult(cheeses, loadPosition, totalCount)
    }

    override fun loadRange(params: LoadRangeParams, callback: LoadRangeCallback<Cheese>) {
        Log.i(TAG, "loadRange: startPosition ${params.startPosition}, size ${params.loadSize}")
        val cheeses = db.cheeseDao().allCheesesByName(params.startPosition, params.loadSize)
        callback.onResult(cheeses)
    }

    companion object{
        private val TAG = CheesePositionalDataSource::class.java.simpleName
    }
}
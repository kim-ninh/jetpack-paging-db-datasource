package paging.android.example.com.pagingsample

import android.annotation.SuppressLint
import android.util.Log
import androidx.paging.ItemKeyedDataSource
import androidx.room.InvalidationTracker

@SuppressLint("RestrictedApi")
class CheeseItemKeyedDataSource(
        val db: CheeseDb) : ItemKeyedDataSource<Int, Cheese>() {

    private val observer: InvalidationTracker.Observer =
            object : InvalidationTracker.Observer("Cheese") {
                override fun onInvalidated(tables: MutableSet<String>) {
                    invalidate()
                }
            }

    init {
        db.invalidationTracker.addWeakObserver(observer)
    }

    override fun isInvalid(): Boolean {
        db.invalidationTracker.refreshVersionsSync()
        return super.isInvalid()
    }

    override fun getKey(item: Cheese): Int = item.id

    override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Cheese>) {
        val initKey = params.requestedInitialKey
        Log.i(TAG, "loadInitial: key $initKey, size: ${params.requestedLoadSize}")
        var cheeses = if (initKey == null) {
            db.cheeseDao().allCheesesById(params.requestedLoadSize)
        } else {
            db.cheeseDao().allCheesesByIdBefore(params.requestedLoadSize, initKey)
        }

        val totalCount = db.cheeseDao().countTotal()
        val totalBefore = db.cheeseDao().totalBeforeId(cheeses.firstOrNull()?.id ?: 0)

//        val cheeses = db.cheeseDao().allCheesesById(params.requestedLoadSize)
        callback.onResult(cheeses, totalBefore, totalCount)
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Cheese>) {
        Log.i(TAG, "loadAfter: key: ${params.key}, size: ${params.requestedLoadSize}")
        val cheeses = db.cheeseDao().allCheesesByIdAfter(params.requestedLoadSize, params.key)
        callback.onResult(cheeses)
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Cheese>) {
        Log.i(TAG, "loadBefore: key: ${params.key}, size: ${params.requestedLoadSize}")
        val cheeses = db.cheeseDao().allCheesesByIdBefore(params.requestedLoadSize, params.key)
        callback.onResult(cheeses)
    }

    companion object {
        val TAG = CheeseItemKeyedDataSource::class.java.simpleName
    }
}
package paging.android.example.com.pagingsample

import android.annotation.SuppressLint
import androidx.paging.ItemKeyedDataSource
import androidx.room.InvalidationTracker

@SuppressLint("RestrictedApi")
class CheeseItemKeyedDataSource(
        val db: CheeseDb): ItemKeyedDataSource<String, Cheese>() {

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

    override fun getKey(item: Cheese): String = item.name

    override fun loadInitial(params: LoadInitialParams<String>, callback: LoadInitialCallback<Cheese>) {
        val initKey = params.requestedInitialKey
        val cheeses = if (initKey == null){
            db.cheeseDao().allCheesesByName(params.requestedLoadSize)
        }else{
            db.cheeseDao().allCheesesByName(params.requestedLoadSize, initKey)
        }

        callback.onResult(cheeses)
    }

    override fun loadAfter(params: LoadParams<String>, callback: LoadCallback<Cheese>) {
        val cheeses = db.cheeseDao().allCheesesByName(params.requestedLoadSize, params.key)
        callback.onResult(cheeses)
    }

    override fun loadBefore(params: LoadParams<String>, callback: LoadCallback<Cheese>) {
        
    }
}
package paging.android.example.com.pagingsample

import android.annotation.SuppressLint
import androidx.paging.ItemKeyedDataSource
import androidx.paging.PagingSource
import androidx.room.InvalidationTracker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@SuppressLint("RestrictedApi")
class CheeseItemKeyedPagingSource(
        val db: CheeseDb): PagingSource<String, Cheese>() {

    private val observer: InvalidationTracker.Observer =
            object : InvalidationTracker.Observer("Cheese") {
                override fun onInvalidated(tables: MutableSet<String>) {
                    invalidate()
                }
            }

    init {
        db.invalidationTracker.addWeakObserver(observer)
    }

    override suspend fun load(params: LoadParams<String>): LoadResult<String, Cheese> {
        val cheeses = withContext(Dispatchers.IO){
            when (params) {
                is LoadParams.Refresh -> {
                    db.cheeseDao().allCheesesByName(params.loadSize)
                }
                is LoadParams.Append -> {
                    db.cheeseDao().allCheesesByName(params.loadSize, params.key)
                }
                else -> {
                    emptyList()
                }
            }
        }

        return LoadResult.Page(cheeses, null, cheeses.lastOrNull()?.name)
    }
}
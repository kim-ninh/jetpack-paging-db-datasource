package paging.android.example.com.pagingsample

import androidx.paging.PositionalDataSource

class CheesePositionalDataSource(
        private val db: CheeseDb
): PositionalDataSource<Cheese>() {

    override fun loadInitial(params: LoadInitialParams, callback: LoadInitialCallback<Cheese>) {
        val totalCount = db.cheeseDao().countTotal()
        val loadPosition = computeInitialLoadPosition(params, totalCount)
        val loadSize = computeInitialLoadSize(params, loadPosition, totalCount)

        val cheeses = db.cheeseDao().allCheesesByName(loadPosition, loadSize)
        callback.onResult(cheeses, loadPosition, totalCount)
    }

    override fun loadRange(params: LoadRangeParams, callback: LoadRangeCallback<Cheese>) {
        val cheeses = db.cheeseDao().allCheesesByName(params.startPosition, params.loadSize)
        callback.onResult(cheeses)
    }
}
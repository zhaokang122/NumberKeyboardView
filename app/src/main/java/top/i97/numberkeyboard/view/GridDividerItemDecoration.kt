package top.i97.numberkeyboard.view

import android.content.Context
import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.support.annotation.ColorInt
import android.support.annotation.ColorRes
import android.support.annotation.DimenRes
import android.support.v4.content.ContextCompat
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.StaggeredGridLayoutManager
import android.util.TypedValue
import android.view.View

/**
 * grid divider item decoration
 *
 * @author Plain
 * @date 2019-12-26 15:06
 */
class GridDividerItemDecoration private constructor(
    private val mHorizonSpan: Int,
    private val mVerticalSpan: Int,
    color: Int,
    private val mShowLastLine: Boolean
) : RecyclerView.ItemDecoration() {

    private val mDivider: Drawable

    init {
        mDivider = ColorDrawable(color)
    }

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        drawHorizontal(c, parent)
        drawVertical(c, parent)
    }

    private fun drawHorizontal(c: Canvas, parent: RecyclerView) {
        val childCount = parent.childCount
        for (i in 0 until childCount) {
            val child = parent.getChildAt(i)
            if (isLastRaw(parent, i, getSpanCount(parent), childCount) && !mShowLastLine) {
                continue
            }
            val params = child.layoutParams as RecyclerView.LayoutParams
            val left = child.left - params.leftMargin
            val right = child.right + params.rightMargin
            val top = child.bottom + params.bottomMargin
            val bottom = top + mHorizonSpan
            mDivider.setBounds(left, top, right, bottom)
            mDivider.draw(c)
        }
    }

    private fun drawVertical(c: Canvas, parent: RecyclerView) {
        val childCount = parent.childCount
        for (i in 0 until childCount) {
            val child = parent.getChildAt(i)
            if ((parent.getChildViewHolder(child).adapterPosition + 1) % getSpanCount(parent) == 0) {
                continue
            }
            val params = child.layoutParams as RecyclerView.LayoutParams
            val top = child.top - params.topMargin
            val bottom = child.bottom + params.bottomMargin + mHorizonSpan
            val left = child.right + params.rightMargin
            var right = left + mVerticalSpan
            if (i == childCount - 1) {
                right -= mVerticalSpan
            }
            mDivider.setBounds(left, top, right, bottom)
            mDivider.draw(c)
        }
    }

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val adapter = parent.adapter
        if (null != adapter) {
            val spanCount = getSpanCount(parent)
            val childCount = adapter.itemCount
            val itemPosition = (view.layoutParams as RecyclerView.LayoutParams).viewLayoutPosition
            if (itemPosition < 0) {
                return
            }
            val column = itemPosition % spanCount
            val bottom: Int

            val left = column * mVerticalSpan / spanCount
            val right = mVerticalSpan - (column + 1) * mVerticalSpan / spanCount

            bottom = if (isLastRaw(parent, itemPosition, spanCount, childCount)) {
                if (mShowLastLine) {
                    mHorizonSpan
                } else {
                    0
                }
            } else {
                mHorizonSpan
            }
            outRect.set(left, 0, right, bottom)
        }
    }

    private fun getSpanCount(parent: RecyclerView): Int {
        var mSpanCount = -1
        val layoutManager = parent.layoutManager
        if (layoutManager is GridLayoutManager) {
            mSpanCount = layoutManager.spanCount
        } else if (layoutManager is StaggeredGridLayoutManager) {
            mSpanCount = layoutManager.spanCount
        }
        return mSpanCount
    }

    private fun isLastRaw(
        parent: RecyclerView,
        pos: Int,
        spanCount: Int,
        childCount: Int
    ): Boolean {
        val layoutManager = parent.layoutManager
        if (layoutManager is GridLayoutManager) {
            return getResult(pos, spanCount, childCount)
        } else if (layoutManager is StaggeredGridLayoutManager) {
            val orientation = layoutManager.orientation
            return if (orientation == StaggeredGridLayoutManager.VERTICAL) {
                getResult(pos, spanCount, childCount)
            } else {
                (pos + 1) % spanCount == 0
            }
        }
        return false
    }

    private fun getResult(pos: Int, spanCount: Int, childCount: Int): Boolean {
        val remainCount = childCount % spanCount
        return if (remainCount == 0) {
            pos >= childCount - spanCount
        } else {
            pos >= childCount - childCount % spanCount
        }
    }


    class Builder(private val mContext: Context) {
        private val mResources: Resources = mContext.resources
        private var mShowLastLine: Boolean = false
        private var mHorizonSpan: Int = 0
        private var mVerticalSpan: Int = 0
        private var mColor: Int = 0

        init {
            mShowLastLine = true
            mHorizonSpan = 0
            mVerticalSpan = 0
            mColor = Color.WHITE
        }

        fun setColorResource(@ColorRes resource: Int): Builder {
            setColor(ContextCompat.getColor(mContext, resource))
            return this
        }

        fun setColor(@ColorInt color: Int): Builder {
            mColor = color
            return this
        }

        fun setVerticalSpan(@DimenRes vertical: Int): Builder {
            this.mVerticalSpan = mResources.getDimensionPixelSize(vertical)
            return this
        }

        fun setVerticalSpan(mVertical: Float): Builder {
            this.mVerticalSpan = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_PX,
                mVertical,
                mResources.displayMetrics
            ).toInt()
            return this
        }

        fun setHorizontalSpan(@DimenRes horizontal: Int): Builder {
            this.mHorizonSpan = mResources.getDimensionPixelSize(horizontal)
            return this
        }

        fun setHorizontalSpan(horizontal: Float): Builder {
            this.mHorizonSpan = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_PX,
                horizontal,
                mResources.displayMetrics
            ).toInt()
            return this
        }

        fun setShowLastLine(show: Boolean): GridDividerItemDecoration.Builder {
            mShowLastLine = show
            return this
        }

        fun build(): GridDividerItemDecoration {
            return GridDividerItemDecoration(mHorizonSpan, mVerticalSpan, mColor, mShowLastLine)
        }
    }
}

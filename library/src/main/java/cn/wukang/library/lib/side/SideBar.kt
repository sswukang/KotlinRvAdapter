package cn.wukang.library.lib.side

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.support.annotation.ColorInt
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import cn.wukang.library.R
import java.util.*


/**
 * 侧边选择条
 *
 * @author wukang
 */
class SideBar : View {
    companion object {
        private const val DEFAULT_TEXT_SIZE = 14 // sp
        private const val DEFAULT_MAX_OFFSET = 80 //dp

        private val DEFAULT_INDEX_ITEMS: Array<String> = arrayOf("A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z")

        const val POSITION_RIGHT = 0
        const val POSITION_LEFT = 1
        const val TEXT_ALIGN_CENTER = 0
        const val TEXT_ALIGN_LEFT = 1
        const val TEXT_ALIGN_RIGHT = 2
    }

    private var mIndexItems: Array<String>

    /**
     * the index in [.mIndexItems] of the current selected index item,
     * it's reset to -1 when the finger up
     */
    private var mCurrentIndex: Int = -1

    /**
     * Y coordinate of the point where finger is touching,
     * the baseline is top of [.mStartTouchingArea]
     * it's reset to -1 when the finger up
     */
    private var mCurrentY: Float = (-1).toFloat()

    private val mPaint: Paint
    private var mTextColor: Int
    private val mTextSize: Float

    /**
     * the height of each index item
     */
    private var mIndexItemHeight: Float = 0.toFloat()

    /**
     * offset of the current selected index item
     */
    private var mMaxOffset: Float

    /**
     * [.mStartTouching] will be set to true when [MotionEvent.ACTION_DOWN]
     * happens in this area, and the side bar should start working.
     */
    private val mStartTouchingArea: RectF = RectF()

    /**
     * height and width of [.mStartTouchingArea]
     */
    private var mBarHeight: Float = 0.toFloat()
    private var mBarWidth: Float = 0.toFloat()

    /**
     * Flag that the finger is starting touching.
     * If true, it means the [MotionEvent.ACTION_DOWN] happened but
     * [MotionEvent.ACTION_UP] not yet.
     */
    private var mStartTouching: Boolean = false

    /**
     * if true, the [OnSelectIndexItemListener.onSelectIndexItem]
     * will not be called until the finger up.
     * if false, it will be called when the finger down, up and move.
     */
    private var mLazyRespond: Boolean

    /**
     * the position of the side bar, default is [.POSITION_RIGHT].
     * You can set it to [.POSITION_LEFT] for people who use phone with left hand.
     */
    private var mSideBarPosition: Int

    /**
     * the alignment of items, default is [.TEXT_ALIGN_CENTER].
     */
    private var mTextAlignment: Int

    /**
     * observe the current selected index item
     */
    private var onSelectIndexItemListener: OnSelectIndexItemListener? = null

    /**
     * the baseline of the first index item text to draw
     */
    private var mFirstItemBaseLineY: Float = 0.toFloat()

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        if (attrs != null) {
            val typedArray: TypedArray = context.obtainStyledAttributes(attrs, R.styleable.SideBar)
            mLazyRespond = typedArray.getBoolean(R.styleable.SideBar_sideLazyRespond, false)
            mTextColor = typedArray.getColor(R.styleable.SideBar_sideTextColor, Color.GRAY)
            mMaxOffset = typedArray.getDimension(R.styleable.SideBar_sideMaxOffset, dp2px(DEFAULT_MAX_OFFSET))
            mSideBarPosition = typedArray.getInt(R.styleable.SideBar_sidePosition, POSITION_RIGHT)
            mTextAlignment = typedArray.getInt(R.styleable.SideBar_sideTextAlignment, TEXT_ALIGN_CENTER)
            typedArray.recycle()
        } else {
            mLazyRespond = false
            mTextColor = Color.GRAY
            mMaxOffset = dp2px(DEFAULT_MAX_OFFSET)
            mSideBarPosition = POSITION_RIGHT
            mTextAlignment = TEXT_ALIGN_CENTER
        }

        mTextSize = sp2px(DEFAULT_TEXT_SIZE)
        mIndexItems = DEFAULT_INDEX_ITEMS

        mPaint = Paint()
        mPaint.isAntiAlias = true
        mPaint.color = mTextColor
        mPaint.textSize = mTextSize
        when (mTextAlignment) {
            TEXT_ALIGN_CENTER -> mPaint.textAlign = Paint.Align.CENTER
            TEXT_ALIGN_LEFT -> mPaint.textAlign = Paint.Align.LEFT
            TEXT_ALIGN_RIGHT -> mPaint.textAlign = Paint.Align.RIGHT
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val height: Float = View.MeasureSpec.getSize(heightMeasureSpec).toFloat()
        val width: Float = View.MeasureSpec.getSize(widthMeasureSpec).toFloat()

        val fontMetrics: Paint.FontMetrics = mPaint.fontMetrics
        mIndexItemHeight = fontMetrics.bottom - fontMetrics.top
        mBarHeight = mIndexItems.size * mIndexItemHeight

        // calculate the width of the longest text as the width of side bar
        for (indexItem: String in mIndexItems) {
            mBarWidth = Math.max(mBarWidth, mPaint.measureText(indexItem))
        }

        val areaLeft: Float = if (mSideBarPosition == POSITION_LEFT) 0F else width - mBarWidth - paddingRight.toFloat()
        val areaRight: Float = if (mSideBarPosition == POSITION_LEFT) paddingLeft.toFloat() + areaLeft + mBarWidth else width
        val areaTop: Float = height / 2 - mBarHeight / 2
        val areaBottom: Float = areaTop + mBarHeight
        mStartTouchingArea.set(
                areaLeft,
                areaTop,
                areaRight,
                areaBottom)

        // the baseline Y of the first item' text to draw
        mFirstItemBaseLineY = height / 2 - mIndexItems.size * mIndexItemHeight / 2 + (mIndexItemHeight / 2 - (fontMetrics.descent - fontMetrics.ascent) / 2) - fontMetrics.ascent
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // draw each item
        for (i: Int in mIndexItems.indices) {
            val baseLineY: Float = mFirstItemBaseLineY + mIndexItemHeight * i

            // calculate the scale factor of the item to draw
            val scale: Float = getItemScale(i)

            mPaint.alpha = if (i == mCurrentIndex) 255 else (255 * (1 - scale)).toInt()

            mPaint.textSize = mTextSize + mTextSize * scale

            var baseLineX = 0f
            if (mSideBarPosition == POSITION_LEFT) {
                when (mTextAlignment) {
                    TEXT_ALIGN_CENTER -> baseLineX = paddingLeft.toFloat() + mBarWidth / 2 + mMaxOffset * scale
                    TEXT_ALIGN_LEFT -> baseLineX = paddingLeft + mMaxOffset * scale
                    TEXT_ALIGN_RIGHT -> baseLineX = paddingLeft.toFloat() + mBarWidth + mMaxOffset * scale
                }
            } else {
                when (mTextAlignment) {
                    TEXT_ALIGN_CENTER -> baseLineX = width.toFloat() - paddingRight.toFloat() - mBarWidth / 2 - mMaxOffset * scale
                    TEXT_ALIGN_RIGHT -> baseLineX = width.toFloat() - paddingRight.toFloat() - mMaxOffset * scale
                    TEXT_ALIGN_LEFT -> baseLineX = width.toFloat() - paddingRight.toFloat() - mBarWidth - mMaxOffset * scale
                }
            }

            // draw
            canvas.drawText(/* item text to draw */ mIndexItems[i], /* baseLine X */ baseLineX, /* baseLine Y */ baseLineY, mPaint)
        }

        // reset paint
        mPaint.alpha = 255
        mPaint.textSize = mTextSize
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (mIndexItems.isEmpty()) {
            return super.onTouchEvent(event)
        }

        val eventY: Float = event.y
        val eventX: Float = event.x
        mCurrentIndex = getSelectedIndex(eventY)

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                return if (mStartTouchingArea.contains(eventX, eventY)) {
                    mStartTouching = true
                    if (!mLazyRespond) {
                        onSelectIndexItemListener?.onSelectIndexItem(mIndexItems[mCurrentIndex])
                    }
                    invalidate()
                    true
                } else {
                    mCurrentIndex = -1
                    false
                }
            }
            MotionEvent.ACTION_MOVE -> {
                if (mStartTouching && !mLazyRespond) {
                    onSelectIndexItemListener?.onSelectIndexItem(mIndexItems[mCurrentIndex])
                }
                invalidate()
                return true
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                if (mLazyRespond) {
                    onSelectIndexItemListener?.onSelectIndexItem(mIndexItems[mCurrentIndex])
                }
                mCurrentIndex = -1
                mStartTouching = false
                invalidate()
                return true
            }
            else -> return super.onTouchEvent(event)
        }
    }

    private fun dp2px(dp: Int): Float = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(), context.resources.displayMetrics)

    private fun sp2px(sp: Int): Float = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp.toFloat(), context.resources.displayMetrics)

    /**
     * calculate the scale factor of the item to draw
     *
     * @param index the index of the item in array [.mIndexItems]
     * @return the scale factor of the item to draw
     */
    private fun getItemScale(index: Int): Float {
        var scale = 0f
        if (mCurrentIndex != -1) {
            val distance: Float = Math.abs(mCurrentY - (mIndexItemHeight * index + mIndexItemHeight / 2)) / mIndexItemHeight
            scale = 1 - distance * distance / 16
            scale = Math.max(scale, 0f)
        }
        return scale
    }

    private fun getSelectedIndex(eventY: Float): Int {
        mCurrentY = eventY - (height / 2 - mBarHeight / 2)
        if (mCurrentY <= 0) {
            return 0
        }

        var index: Int = (mCurrentY / this.mIndexItemHeight).toInt()
        if (index >= this.mIndexItems.size) {
            index = this.mIndexItems.size - 1
        }
        return index
    }

    fun setIndexItems(vararg indexItems: String) {
        mIndexItems = Arrays.copyOf(indexItems, indexItems.size)
        requestLayout()
    }

    fun setTextColor(@ColorInt color: Int) {
        mTextColor = color
        mPaint.color = color
        invalidate()
    }

    fun setPosition(position: Int) {
        if (position != POSITION_RIGHT && position != POSITION_LEFT) {
            throw IllegalArgumentException("the position must be POSITION_RIGHT or POSITION_LEFT")
        }

        mSideBarPosition = position
        requestLayout()
    }

    fun setMaxOffset(offset: Int) {
        mMaxOffset = offset.toFloat()
        invalidate()
    }

    fun setLazyRespond(lazyRespond: Boolean) {
        mLazyRespond = lazyRespond
    }

    fun setTextAlign(align: Int) {
        if (mTextAlignment == align) {
            return
        }
        when (align) {
            TEXT_ALIGN_CENTER -> mPaint.textAlign = Paint.Align.CENTER
            TEXT_ALIGN_LEFT -> mPaint.textAlign = Paint.Align.LEFT
            TEXT_ALIGN_RIGHT -> mPaint.textAlign = Paint.Align.RIGHT
            else -> throw IllegalArgumentException("the alignment must be TEXT_ALIGN_CENTER, TEXT_ALIGN_LEFT or TEXT_ALIGN_RIGHT")
        }
        mTextAlignment = align
        invalidate()
    }

    fun setOnSelectIndexItemListener(onSelectIndexItemListener: OnSelectIndexItemListener) {
        this.onSelectIndexItemListener = onSelectIndexItemListener
    }

    interface OnSelectIndexItemListener {
        fun onSelectIndexItem(index: String)
    }
}
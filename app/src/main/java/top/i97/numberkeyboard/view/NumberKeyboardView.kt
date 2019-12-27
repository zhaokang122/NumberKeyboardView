package top.i97.numberkeyboard.view

import android.content.Context
import android.graphics.Color
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.RelativeLayout
import kotlinx.android.synthetic.main.layout_number_keyboard.view.*

import java.util.ArrayList
import java.util.HashMap

import top.i97.numberkeyboard.Constant
import top.i97.numberkeyboard.R

/**
 * custom number keyboard view
 *
 * @author Plain
 * @date 2019-12-26 15:00
 */
class NumberKeyboardView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr) {

    private var keyValueList: MutableList<Map<String, String>>? = null

    private lateinit var rvKeyboard: RecyclerView

    /**
     * get key board adapter
     *
     * @return NumberKeyboardAdapter
     */
    var adapter: NumberKeyboardAdapter? = null
        private set

    init {
        init(context)
    }

    /**
     * initial
     */
    private fun init(context: Context) {
        val view = LayoutInflater.from(context).inflate(
            R.layout.layout_number_keyboard,
            this,
            false
        )
        rvKeyboard = view.findViewById(R.id.rvKeyboard)
        initKeyValueList()
        initRv(context)
        addView(view)
    }

    /**
     * initial key value list
     */
    private fun initKeyValueList() {
        if (null == keyValueList) {
            keyValueList = ArrayList()
            for (i in 1..12) {
                val map = HashMap<String, String>()
                when {
                    i < Constant.KEYBOARD_ZERO -> map[Constant.MAP_KEY_NAME] = i.toString()
                    i == Constant.KEYBOARD_ZERO -> map[Constant.MAP_KEY_NAME] = "."
                    i == Constant.KEYBOARD_DEL -> map[Constant.MAP_KEY_NAME] = 0.toString()
                    else -> map[Constant.MAP_KEY_NAME] = ""
                }
                keyValueList!!.add(map)
            }
        }
    }

    /**
     * initial recycler view
     */
    private fun initRv(context: Context) {
        adapter = NumberKeyboardAdapter(keyValueList)
        rvKeyboard.layoutManager = GridLayoutManager(context, 3)
        val dividerItemDecoration = GridDividerItemDecoration.Builder(context)
            .setShowLastLine(false)
            .setHorizontalSpan(R.dimen.SIZE_1)
            .setVerticalSpan(R.dimen.SIZE_1)
            .setColor(Color.parseColor("#ebebeb"))
            .build()
        rvKeyboard.addItemDecoration(dividerItemDecoration)
        rvKeyboard.adapter = adapter
    }

    /**
     * get key value list
     *
     * @return key value list
     */
    fun getKeyValueList(): List<Map<String, String>>? {
        return keyValueList
    }

}

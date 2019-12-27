package top.i97.numberkeyboard.view

import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder

import top.i97.numberkeyboard.Constant
import top.i97.numberkeyboard.R

/**
 * number keyboard adapter
 *
 * @author Plain
 * @date 2019-12-26 15:00
 */
class NumberKeyboardAdapter internal constructor(data: List<Map<String, String>>?) :

    BaseQuickAdapter<Map<String, String>, BaseViewHolder>(R.layout.item_number_keyboard, data) {

    override fun convert(viewHolder: BaseViewHolder, item: Map<String, String>) {
        val tvKey = viewHolder.getView<TextView>(R.id.tvKey)
        val rlDel = viewHolder.getView<RelativeLayout>(R.id.rlDel)
        val position = viewHolder.layoutPosition
        if (position == Constant.KEYBOARD_DEL) {
            rlDel.visibility = View.VISIBLE
            tvKey.visibility = View.INVISIBLE
        } else {
            rlDel.visibility = View.INVISIBLE
            tvKey.visibility = View.VISIBLE
            tvKey.text = item[Constant.MAP_KEY_NAME]
        }
    }

}

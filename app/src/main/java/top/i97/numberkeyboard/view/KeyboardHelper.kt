package top.i97.numberkeyboard.view

import android.annotation.SuppressLint
import android.app.Activity
import android.text.Editable
import android.text.InputType
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.EditText

import com.chad.library.adapter.base.BaseQuickAdapter
import top.i97.numberkeyboard.Constant

import java.lang.reflect.Method

import top.i97.numberkeyboard.listener.OnKeyboardChangeListener

/**
 * Keyboard helper
 *
 * @author Plain
 * @date 2019-12-26 15:32
 */
class KeyboardHelper {

    private var valueList: List<Map<String, String>>? = null
    private var listener: BaseQuickAdapter.OnItemClickListener? = null
    private var watcher: TextWatcher? = null
    private var lastCallbackResult = ""

    /**
     * ban system key board
     *
     * @param context  Activity
     * @param editText EditText
     */
    @SuppressLint("ObsoleteSdkInt")
    fun banSystemKeyboard(context: Activity, editText: EditText) {
        if (android.os.Build.VERSION.SDK_INT <= 10) {
            editText.inputType = InputType.TYPE_NULL
        } else {
            context.window.setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
            )
            try {
                val cls = EditText::class.java
                val setShowSoftInputOnFocus: Method
                setShowSoftInputOnFocus = cls.getMethod(
                    "setShowSoftInputOnFocus",
                    Boolean::class.javaPrimitiveType!!
                )
                setShowSoftInputOnFocus.isAccessible = true
                setShowSoftInputOnFocus.invoke(editText, false)
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }

    /**
     * bind
     *
     * @param editText           EditText
     * @param numberKeyboardView NumberKeyboardView
     * @param listener           OnKeyBoardListener
     */
    fun bind(
        editText: EditText,
        numberKeyboardView: NumberKeyboardView,
        listener: OnKeyboardChangeListener
    ) {
        editText.requestFocus()
        editText.isSaveEnabled = false
        valueList = numberKeyboardView.getKeyValueList()
        val adapter = numberKeyboardView.adapter
        if (null != adapter && !(null == valueList || valueList!!.isEmpty())) {
            processKeyClick(editText, adapter)
            observedEditText(editText, listener)
        }
    }

    /**
     * observed editText
     *
     * @param editText EditText
     * @param listener OnKeyBoardListener
     */
    private fun observedEditText(editText: EditText, listener: OnKeyboardChangeListener) {
        if (null == watcher) {
            watcher = object : TextWatcher {

                override fun beforeTextChanged(
                    s: CharSequence,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                    //Empty
                }

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    //Empty
                }

                override fun afterTextChanged(s: Editable) {
                    val inputVal = editText.text.toString()
                    val effectiveVal = perfectDecimal(inputVal, 3, 2)
                    if (effectiveVal != inputVal) {
                        editText.setText(effectiveVal)
                        val pos = editText.text.length
                        editText.setSelection(pos)
                    }
                    callBackInputResult(listener, effectiveVal)
                }

                /**
                 * input validation
                 *
                 * @param inputVal input val
                 * @param maxBeforeDot max integer digits
                 * @param maxDecimal max decimal digits
                 * @return effective value
                 */
                private fun perfectDecimal(
                    inputVal: String,
                    maxBeforeDot: Int,
                    maxDecimal: Int
                ): String {
                    var inputVal = inputVal
                    if (inputVal.isEmpty()) return ""
                    if (inputVal[0] == '.') inputVal = "0$inputVal"
                    val max = inputVal.length
                    val rFinal = StringBuilder()
                    var after = false
                    var i = 0
                    var up = 0
                    var decimal = 0
                    var t: Char
                    while (i < max) {
                        t = inputVal[i]
                        if (t != '.' && !after) {
                            up++
                            if (up > maxBeforeDot) return rFinal.toString()
                        } else if (t == '.') {
                            after = true
                        } else {
                            decimal++
                            if (decimal > maxDecimal) return rFinal.toString()
                        }
                        rFinal.append(t)
                        i++
                    }
                    return rFinal.toString()
                }
            }
        }
        editText.addTextChangedListener(watcher)
    }

    /**
     * call back input result
     *
     * @param listener OnKeyBoardListener
     * @param str      result
     */
    private fun callBackInputResult(listener: OnKeyboardChangeListener?, str: String) {
        if (null != listener) {
            if (str != lastCallbackResult) {
                lastCallbackResult = str
                listener.onTextChange(str)
            }
        }
    }

    /**
     * process key click
     */
    private fun processKeyClick(editText: EditText, adapter: NumberKeyboardAdapter) {
        adapter.onItemClickListener =
            BaseQuickAdapter.OnItemClickListener { _,
                                                   _,
                                                   position ->
                respondKeyClick(editText, position)
            }
    }

    /**
     * respond to keyboard clicks
     *
     * @param et  EditText
     * @param pos position
     */
    private fun respondKeyClick(et: EditText, pos: Int) {
        if (pos < 11 && pos != 9) { // click number 0 - 9
            var amount = et.text.toString().trim { it <= ' ' }
            amount += valueList!![pos][Constant.MAP_KEY_NAME]
            val index = et.selectionStart
            // cannot enter zero in the first place
            if (pos == 10 && index == 0) {
                return
            }
            if (index == amount.length - 1) {
                et.setText(amount)
                val ea = et.text
                et.setSelection(ea.length)
            } else {
                val editable = et.text
                editable.insert(index, valueList!![pos][Constant.MAP_KEY_NAME])
            }
        } else {
            if (pos == 9) { // click dot
                var amount = et.text.toString().trim { it <= ' ' }
                if (TextUtils.isEmpty(amount)) {
                    return
                }
                val index = et.selectionStart
                if (index == amount.length && !amount.contains(".")) {
                    amount += valueList!![pos][Constant.MAP_KEY_NAME] ?: error("")
                    et.setText(amount)
                    val ea = et.text
                    et.setSelection(ea.length)
                } else if (index > 0 && !amount.contains(".")) {
                    val editable = et.text
                    editable.insert(index, valueList!![pos][Constant.MAP_KEY_NAME])
                }
            }
            if (pos == 11) { // click delete
                var amount = et.text.toString().trim { it <= ' ' }
                if (amount.isNotEmpty()) {
                    val index = et.selectionStart
                    if (index == amount.length) {
                        amount = amount.substring(0, amount.length - 1)
                        et.setText(amount)
                        val ea = et.text
                        et.setSelection(ea.length)
                    } else {
                        if (index != 0) {
                            val editable = et.text
                            editable.delete(index - 1, index)
                        }
                    }
                }
            }
        }
    }

    /**
     * un bind
     */
    fun unBind() {
        if (null != listener) {
            listener = null
        }
        if (null != watcher) {
            watcher = null
        }
        lastCallbackResult = ""
    }

    companion object {

        @Volatile
        private var instance: KeyboardHelper? = null
            get() {
                if (field == null) {
                    field = KeyboardHelper()
                }
                return field
            }

        @Synchronized
        fun get(): KeyboardHelper {
            return instance!!
        }
    }


}

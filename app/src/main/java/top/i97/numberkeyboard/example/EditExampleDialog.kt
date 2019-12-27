package top.i97.numberkeyboard.example

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.BottomSheetDialogFragment
import android.support.design.widget.CoordinatorLayout
import android.text.TextUtils
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import kotlinx.android.synthetic.main.dialog_input_deit.*

import top.i97.numberkeyboard.R
import top.i97.numberkeyboard.listener.OnEditDialogResultListener
import top.i97.numberkeyboard.listener.OnKeyboardChangeListener
import top.i97.numberkeyboard.view.KeyboardHelper
import top.i97.numberkeyboard.view.NumberKeyboardView

/**
 * edit example dialog
 *
 * @author Plain
 * @date 2019-12-26 15:24
 */
class EditExampleDialog : BottomSheetDialogFragment() {

    private lateinit var helper: KeyboardHelper
    private var inputContent: String? = null
    private var listener: OnEditDialogResultListener? = null
    private lateinit var editText: EditText
    private lateinit var numberKeyboard: NumberKeyboardView
    private lateinit var cancel: TextView
    private lateinit var ok: TextView

    companion object {

        fun newInstance(): EditExampleDialog {
            return EditExampleDialog()
        }

    }

    fun setListener(listener: OnEditDialogResultListener): EditExampleDialog {
        this.listener = listener
        return this
    }

    override fun onStart() {
        super.onStart()

        if (null == view) {
            return
        }

        val parent = view!!.parent as View
        val behavior = BottomSheetBehavior.from(parent)
        view!!.measure(0, 0)
        behavior.peekHeight = view!!.measuredHeight
        val params = parent.layoutParams as CoordinatorLayout.LayoutParams
        params.gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL
        parent.layoutParams = params

        val window = dialog.window
        if (window != null) {
            val decorView = window.decorView
            decorView.setPadding(0, 0, 0, 0)
            window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            window.setWindowAnimations(R.style.DialogAnimation)
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.dialog_input_deit, container, false)
        view.isSaveEnabled = false
        editText = view.findViewById(R.id.edit)
        numberKeyboard = view.findViewById(R.id.numberKeyboard)
        cancel = view.findViewById(R.id.cancel)
        ok = view.findViewById(R.id.ok)
        helper = KeyboardHelper.get()
        initView()
        return view
    }

    /**
     * initial view
     */
    private fun initView() {
        cancel.setOnClickListener {
            cancel()
        }
        ok.setOnClickListener {
            ok()
        }
        activity?.let { helper.banSystemKeyboard(it, editText) }
        helper.bind(editText, numberKeyboard, object : OnKeyboardChangeListener {
            override fun onTextChange(text: String) {
                inputContent = text
            }
        })
    }

    override fun onResume() {
        super.onResume()
        editText.requestFocus()
    }

    private fun cancel() {
        dismiss()
    }

    private fun ok() {
        if (TextUtils.isEmpty(inputContent)) {
            Toast.makeText(context, "输入内容不能为空！", Toast.LENGTH_SHORT).show()
            return
        }
        listener?.onResult(inputContent ?: "")
        dismiss()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        helper.unBind()
    }

}

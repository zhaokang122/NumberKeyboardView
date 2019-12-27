package top.i97.numberkeyboard

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import top.i97.numberkeyboard.example.EditExampleDialog
import top.i97.numberkeyboard.listener.OnEditDialogResultListener

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btnShow.setOnClickListener {
            EditExampleDialog
                .newInstance()
                .setListener(object : OnEditDialogResultListener {
                    override fun onResult(result: String) {
                        tvContent.text = result
                    }
                })
                .show(supportFragmentManager, "DEMO_DIALOG")
        }
    }

}

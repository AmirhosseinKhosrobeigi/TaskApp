package io.github.amirhosseinkhosrobeigi.taskapp.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import io.github.amirhosseinkhosrobeigi.taskapp.mvp.model.ModelMainActivity
import io.github.amirhosseinkhosrobeigi.taskapp.mvp.presenter.PresenterMainActivity
import io.github.amirhosseinkhosrobeigi.taskapp.mvp.view.ViewMainActivity

class MainActivity : AppCompatActivity() {

    private lateinit var presenter: PresenterMainActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val view = ViewMainActivity(this)
        setContentView(view.binding.root)

        presenter = PresenterMainActivity(view, ModelMainActivity(this))
        presenter.onCreate()
    }

    override fun onDestroy() {
        presenter.onDestroy()
        super.onDestroy()
    }

}
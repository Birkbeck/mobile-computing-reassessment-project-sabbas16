package com.example.movietracker

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val button : FloatingActionButton = findViewById(R.id.fabAddMovie)


        button.setOnClickListener(View.OnClickListener {
            val intent = Intent(this, AddMovieActivity::class.java)
            startActivity(intent)
        })

    }
}
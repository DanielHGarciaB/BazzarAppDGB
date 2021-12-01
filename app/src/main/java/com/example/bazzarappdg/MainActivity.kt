package com.example.bazzarappdg

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import com.facebook.CallbackManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    private var editEmail: EditText? = null
    private var editPassword: EditText? = null

    var db = FirebaseFirestore.getInstance()

    private val GOOGLE_SING_IN = 100
    private val callbackManager = CallbackManager.Factory.create()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        editEmail = findViewById(R.id.editEmail);
        editPassword = findViewById(R.id.editPassword);
    }

    fun onLogin(view: android.view.View) {
        var email = editEmail!!.text.toString();
        var password = editPassword!!.text.toString();

        if (email.isNotEmpty() && password.isNotEmpty()) {

            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener {

                    if (it.isSuccessful) {
                        showHome(email, ProviderType.BASIC)
                    } else {
                        getToast(resources.getString(R.string.test_errorAuth));
                    }
                }

        } else {
            getToast(resources.getString(R.string.test_errorlogin));
        }
    }

    private fun showHome(username: String, provider: ProviderType) {

        val homeIntent = Intent(this, HomeActivity::class.java).apply {
            putExtra("email", username)
            putExtra("provider", provider.toString())
        }

        startActivity(homeIntent)

        getToast(resources.getString(R.string.test_welcome));

    }


    fun onRegister(botonRegister: android.view.View) {
        val registerIntent = Intent(this, RegisterActivity::class.java)
        startActivity(registerIntent)
    }

    private fun getToast(message: String) {
        Toast.makeText(
            applicationContext,
            message,
            Toast.LENGTH_LONG
        ).show();
    }

    fun googleLogin(view: android.view.View) {
        val googleConf = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(resources.getString(R.string.default_web_client_id2))
            .requestEmail().build()

        val googleClient = GoogleSignIn.getClient(this, googleConf)
        googleClient.signOut()
        startActivityForResult(googleClient.signInIntent, GOOGLE_SING_IN)
    }
}
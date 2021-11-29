package com.example.bazzarappdg

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import com.facebook.CallbackManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class MainActivity : AppCompatActivity() {
    private var editUserName: EditText? = null
    private var editPassword: EditText? = null

    private val GOOGLE_SING_IN = 100
    private val callbackManager = CallbackManager.Factory.create()

    override fun onCreate(savedInstanceState: Bundle?) {
        Thread.sleep(2000)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        editUserName = findViewById(R.id.edtUsername)
        editPassword = findViewById(R.id.edtPassword)
        title = resources.getString(R.string.test_login)

    }
    fun onLogin(botonLogin: android.view.View) {
        var username = editUserName!!.text.toString();
        var password = editPassword!!.text.toString();

        if (username.isNotEmpty() && password.isNotEmpty()) {

            FirebaseAuth.getInstance().signInWithEmailAndPassword(username, password)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        showHome(username, ProviderType.BASIC)
                    } else {
                        getToast(resources.getString(R.string.test_errorAuth));
                    }
                }

        } else {
            getToast(resources.getString(R.string.test_errorlogin));
        }

    }

    fun onRegister(botonRegister: android.view.View) {
        val registerIntent = Intent(this, RegisterActivity::class.java)
        startActivity(registerIntent)
        getToast(resources.getString(R.string.test_register));
    }

    private fun showHome(username: String, provider: ProviderType) {

        val homeIntent = Intent(this, HomeActivity::class.java).apply {
            putExtra("email", username)
            putExtra("provider", provider.toString())
        }

        startActivity(homeIntent)

        getToast(resources.getString(R.string.test_welcome));
    }

    fun googleLogin(view: android.view.View) {

        val googleConf = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(resources.getString(R.string.default_web_client_id2))
            .requestEmail().build()

        val googleClient = GoogleSignIn.getClient(this, googleConf)
        googleClient.signOut()
        startActivityForResult(googleClient.signInIntent, GOOGLE_SING_IN)

    }

    private fun getToast(message: String) {
        Toast.makeText(
            applicationContext,
            message,
            Toast.LENGTH_LONG
        ).show();
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == GOOGLE_SING_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)

            try {
                val account = task.getResult(ApiException::class.java)
                if (account != null) {
                    val credencial = GoogleAuthProvider.getCredential(account.idToken, null)
                    FirebaseAuth.getInstance().signInWithCredential(credencial)
                        .addOnCompleteListener {
                            if (it.isSuccessful) {
                                showHome(account.email ?: "", ProviderType.GOOGLE)
                            } else {
                                getToast(resources.getString(R.string.test_errorAuth));
                            }
                        }
                }
            } catch (e: ApiException) {
                getToast(resources.getString(R.string.test_errorAuth));
            }
        }
    }
}
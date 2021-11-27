package com.example.bazzarappdg

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.Switch
import android.widget.Toast
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.regex.Pattern

class RegisterActivity : AppCompatActivity() {

    //variables para capturar datos del formulario de registro
    private var editName: EditText? = null
    private var editLastName: EditText? = null
    private var editMobile: EditText? = null
    private var editEmail: EditText? = null
    private var editPassword: EditText? = null
    private var editPassword2: EditText? = null
    private var stTerms: Switch? = null

    var db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        editName = findViewById(R.id.editName);
        editLastName = findViewById(R.id.editLastName);
        editMobile = findViewById(R.id.editMobile);
        editEmail = findViewById(R.id.editEmail);
        editPassword = findViewById(R.id.editPassword);
        editPassword2 = findViewById(R.id.editPassword2);
        stTerms = findViewById(R.id.stTerms);

        title = "Register"
    }

    //realiza el registro en la base de datos
    fun onRegister(view: android.view.View) {

        var name = editName!!.text.toString();
        var lastname = editLastName!!.text.toString();
        var mobile = editMobile!!.text.toString();
        var email = editEmail!!.text.toString();
        var password = editPassword!!.text.toString();
        var password2 = editPassword2!!.text.toString();
        var terms = stTerms!!.isChecked;

        if (Validation(email,password,password2,name,lastname,mobile,terms)) {

            //Save Auth
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        //Save User
                        db.collection("user").document(email).set(
                            hashMapOf("password" to password,
                                "name" to name,
                                "lastname" to lastname,
                                "mobile" to mobile,
                                "terms" to terms))
                        showHome(email, ProviderType.BASIC)
                    } else {
                        getToast("Failed to authenticate");
                    }
                }
        }else{
            getToast("Validate Error");
        }

    }

    private fun Validation(username: String, password: String, password2: String, name: String, lastname: String, mobile: String, terms: Boolean): Boolean {
        //Reset
        editEmail!!.setBackground(resources.getDrawable(R.drawable.customborderok))
        var editEmailLayout = findViewById<TextInputLayout>( R.id.editEmailLayout)
        editEmailLayout!!.setHint("example@main.com")

        editPassword!!.setBackground(resources.getDrawable(R.drawable.customborderok))
        var editPasswordLayout = findViewById<TextInputLayout>( R.id.editPasswordLayout)
        editPasswordLayout!!.setHint("Your Pasword")

        editPassword2!!.setBackground(resources.getDrawable(R.drawable.customborderok))
        var editPasswordLayout2 = findViewById<TextInputLayout>( R.id.editPasswordLayout2)
        editPasswordLayout2!!.setHint("Confirm Your Pasword")

        editName!!.setBackground(resources.getDrawable(R.drawable.customborderok))
        var editNameLayout = findViewById<TextInputLayout>( R.id.editNameLayout)
        editNameLayout!!.setHint("Your Name")

        editLastName!!.setBackground(resources.getDrawable(R.drawable.customborderok))
        var editLastNameLayout = findViewById<TextInputLayout>( R.id.editLastNameLayout)
        editLastNameLayout!!.setHint("Your Last Name")

        editMobile!!.setBackground(resources.getDrawable(R.drawable.customborderok))
        var editMobileLayout = findViewById<TextInputLayout>( R.id.editMobileLayout)
        editMobileLayout!!.setHint("3001234567")

        stTerms!!.setBackground(resources.getDrawable(R.drawable.customborderok))

        //Regex
        val uppercase: Pattern = Pattern.compile("[A-Z]")
        val lowercase: Pattern = Pattern.compile("[a-z]")
        val digit: Pattern = Pattern.compile("[0-9]")
        val character: Pattern = Pattern.compile("[!#\$%&'*+/=?^_`{|}~-]")
        val email: Pattern = Pattern.compile("^[a-z0-9!#\$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#\$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\$")

        //Validation
        var validation : Boolean = true;

        if (name.isEmpty()) {
            editNameLayout!!.setHint("Empty Name")
            editName!!.setBackground(resources.getDrawable(R.drawable.custombordererror))
            validation=false
        }

        if (lastname.isEmpty()) {
            editLastNameLayout!!.setHint("Empty Last Name")
            editLastName!!.setBackground(resources.getDrawable(R.drawable.custombordererror))
            validation=false
        }

        if (mobile.isEmpty()) {
            editMobileLayout!!.setHint("Empty Mobile")
            editMobile!!.setBackground(resources.getDrawable(R.drawable.custombordererror))
            validation=false
        }

        if (!terms){
            stTerms!!.setBackground(resources.getDrawable(R.drawable.custombordererror))
            validation=false
        }

        //Validate Password

        if (password2.isEmpty()) {
            editPasswordLayout2!!.setHint("Empty Confirm Password")
            editPassword2!!.setBackground(resources.getDrawable(R.drawable.custombordererror))
            validation=false

        }

        if (password.isEmpty()) {
            editPasswordLayout!!.setHint("Empty Password")
            editPassword!!.setBackground(resources.getDrawable(R.drawable.custombordererror))
            validation=false

        }else{
            if (password.length < 8) {
                editPasswordLayout!!.setHint("Minimum 8 characters")
                editPassword!!.setBackground(resources.getDrawable(R.drawable.custombordererror))
                validation=false

            }else{
                if (!lowercase.matcher(password).find()) {
                    editPasswordLayout!!.setHint("At least one lowercase")
                    editPassword!!.setBackground(resources.getDrawable(R.drawable.custombordererror))
                    validation=false

                }else{
                    if (!uppercase.matcher(password).find()) {
                        editPasswordLayout!!.setHint("At least one uppercase")
                        editPassword!!.setBackground(resources.getDrawable(R.drawable.custombordererror))
                        validation=false
                    }else{

                        if (!digit.matcher(password).find()){
                            editPasswordLayout!!.setHint("At least one number")
                            editPassword!!.setBackground(resources.getDrawable(R.drawable.custombordererror))
                            validation=false

                        }else{
                            if (!character.matcher(password).find()){
                                editPasswordLayout!!.setHint("At least one character")
                                editPassword!!.setBackground(resources.getDrawable(R.drawable.custombordererror))
                                validation=false
                            }else{
                                if (password!=password2){
                                    editPasswordLayout!!.setHint("Passwords do not match")
                                    editPassword!!.setBackground(resources.getDrawable(R.drawable.custombordererror))
                                    editPasswordLayout2!!.setHint("Passwords do not match")
                                    editPassword2!!.setBackground(resources.getDrawable(R.drawable.custombordererror))
                                    validation=false
                                }
                            }
                        }
                    }
                }
            }
        }

        //Validation Email
        if (username.isEmpty()) {
            editEmailLayout!!.setHint("EmptyMail")
            editEmail!!.setBackground(resources.getDrawable(R.drawable.custombordererror))
            validation=false
        }else{
            if (!email.matcher(username).find()){
                editEmailLayout!!.setHint("It is not an e-mail")
                editEmail!!.setBackground(resources.getDrawable(R.drawable.custombordererror))
                validation=false
            }
        }

        return validation;
    }

    private fun getToast(message: String) {
        Toast.makeText(
            applicationContext,
            message,
            Toast.LENGTH_SHORT
        ).show();
    }

    private fun showHome(username: String, provider: ProviderType) {

        val homeIntent = Intent(this, HomeActivity::class.java).apply {
            putExtra("email", username)
            putExtra("provider", provider.toString())
        }

        startActivity(homeIntent)

        getToast("Welcome");
    }

}
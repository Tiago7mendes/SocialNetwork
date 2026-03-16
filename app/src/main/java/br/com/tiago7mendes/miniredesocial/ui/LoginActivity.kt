package br.com.tiago7mendes.miniredesocial.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import br.com.tiago7mendes.miniredesocial.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupFireBase()
        setupListener()
    }

    fun setupFireBase(){
        firebaseAuth = FirebaseAuth.getInstance()
    }

    private fun setupListener() {
        binding.btnLogin.setOnClickListener{
            autenticarUsuario()
        }
        binding.btnCreateUser.setOnClickListener{
            createUser()
        }
    }

    fun createUser(){
        startActivity(Intent(this, SignUpActivity::class.java))
    }

    fun autenticarUsuario(){
        val email = binding.edtEmail.text.toString()
        val password = binding.edtSenha.text.toString()
        firebaseAuth
            .signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    startActivity(Intent(this, HomeActivity::class.java))
                    Toast.makeText(this, "Login Realiado com Sucesso!", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this, "Erro no login", Toast.LENGTH_LONG).show()
                }
            }
    }
}
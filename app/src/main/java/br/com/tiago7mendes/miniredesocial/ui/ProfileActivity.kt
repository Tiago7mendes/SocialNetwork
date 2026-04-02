package br.com.tiago7mendes.miniredesocial.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import br.com.tiago7mendes.miniredesocial.auth.UserAuth
import br.com.tiago7mendes.miniredesocial.dao.UserDAO
import br.com.tiago7mendes.miniredesocial.databinding.ActivityProfileBinding
import br.com.tiago7mendes.miniredesocial.model.User
import br.com.tiago7mendes.miniredesocial.util.Base64Converter

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding
    private lateinit var userDAO: UserDAO
    private val userAuth = UserAuth()

    private val galeria = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        uri?.let {
            binding.imgProfile.setImageURI(it)
        } ?: Toast.makeText(this, "Nenhuma foto selecionada", Toast.LENGTH_LONG).show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userDAO = UserDAO(this)
        carregarDadosUsuario()
        setupClickListeners()
    }

    private fun carregarDadosUsuario() {
        val email = userAuth.getEmailUsuarioLogado() ?: return
        userDAO.getByEmail(email,
            onSuccess = { user ->
                user?.let {
                    binding.edtUserName.setText(it.username)
                    binding.edtNameComplete.setText(it.nomeCompleto)
                }
            },
            onFailure = { msg ->
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
            }
        )
    }

    private fun setupClickListeners() {
        binding.btnChangePhoto.setOnClickListener {
            galeria.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
        binding.btnSave.setOnClickListener {
            salvarPerfil()
        }
    }

    private fun salvarPerfil() {
        val email = userAuth.getEmailUsuarioLogado() ?: return
        val username = binding.edtUserName.text.toString().trim()
        val nomeCompleto = binding.edtNameComplete.text.toString().trim()
        val fotoPerfilString = Base64Converter.drawableToString(binding.imgProfile.drawable)

        val user = User(email, username, nomeCompleto, fotoPerfilString)

        userDAO.save(user,
            onSuccess = {
                Toast.makeText(this, "Perfil salvo!", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, HomeActivity::class.java))
                finish()
            },
            onFailure = { msg ->
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
            }
        )
    }
}
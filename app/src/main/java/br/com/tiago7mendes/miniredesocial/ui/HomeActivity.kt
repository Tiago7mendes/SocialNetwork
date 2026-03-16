package br.com.tiago7mendes.miniredesocial.ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import br.com.tiago7mendes.miniredesocial.dao.UserDAO
import br.com.tiago7mendes.miniredesocial.databinding.ActivityHomeBinding
import br.com.tiago7mendes.miniredesocial.util.Base64Converter
import com.google.firebase.auth.FirebaseAuth

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    private lateinit var userDAO: UserDAO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userDAO = UserDAO(this)
        carregarDadosUsuario()
    }

    private fun carregarDadosUsuario() {
        val email = FirebaseAuth.getInstance().currentUser?.email ?: return

        userDAO.getByEmail(email,
            onSuccess = { user ->
                user?.let {
                    try {
                        val bitmap = Base64Converter.stringToBitmap(it.fotoPerfil)
                        binding.imgLogo.setImageBitmap(bitmap)
                    } catch (e: Exception) {
                        binding.imgLogo
                    }

                    binding.txtUsername.text = it.username
                    binding.txtNomeCompleto.text = it.nomeCompleto
                } ?: run {
                    Toast.makeText(this, "Perfil não encontrado", Toast.LENGTH_SHORT).show()
                }
            },
            onFailure = { msg ->
                Toast.makeText(this, "Erro: $msg", Toast.LENGTH_SHORT).show()
            }
        )
    }
}

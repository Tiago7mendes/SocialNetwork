package br.com.tiago7mendes.miniredesocial.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.tiago7mendes.miniredesocial.adapter.PostAdapter
import br.com.tiago7mendes.miniredesocial.dao.UserDAO
import br.com.tiago7mendes.miniredesocial.databinding.ActivityHomeBinding
import br.com.tiago7mendes.miniredesocial.model.Post
import br.com.tiago7mendes.miniredesocial.util.Base64Converter
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    private lateinit var userDAO: UserDAO
    private lateinit var adapter: PostAdapter
    private var posts = ArrayList<Post>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userDAO = UserDAO(this)
        carregarDadosUsuario()
        editarPerfil()
        carregarFeed()
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
                        // Mantém a imagem padrão se a conversão falhar
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

    private fun editarPerfil() {
        binding.btnEditProfile.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
            finish()
        }
    }

    private fun carregarFeed() {
        binding.btnCarregarFeed.setOnClickListener {
            val db = Firebase.firestore
            db.collection("posts").get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val documents = task.result
                        posts = ArrayList()
                        for (document in documents.documents) {
                            val imageString = document.data!!["imageString"].toString()
                            val bitmap = Base64Converter.stringToBitmap(imageString)
                            val descricao = document.data!!["descricao"].toString()
                            posts.add(Post(descricao, bitmap))
                        }
                        adapter = PostAdapter(posts.toTypedArray())
                        binding.recyclerView.layoutManager = LinearLayoutManager(this)
                        binding.recyclerView.adapter = adapter
                    } else {
                        Toast.makeText(this, "Erro ao carregar feed", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }
}
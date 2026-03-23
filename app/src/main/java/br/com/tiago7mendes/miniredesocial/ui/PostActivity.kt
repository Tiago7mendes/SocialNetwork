package br.com.tiago7mendes.miniredesocial.ui

import android.graphics.Bitmap
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import br.com.tiago7mendes.miniredesocial.databinding.ActivityPostBinding
import com.android.volley.Request
import com.android.volley.toolbox.ImageRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley

class PostActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPostBinding

    // Coloque o IP do seu servidor aqui
    private val BASE_URL = "http://seu_ip:8080"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Recebe o ID do post via Intent (enviado pela HomeActivity)
        val postId = intent.getIntExtra("POST_ID", 1)
        buscarPost(postId)
    }

    private fun buscarPost(postId: Int) {
        val queue = Volley.newRequestQueue(this)
        val url = "$BASE_URL/posts/$postId"

        val jsonRequest = JsonObjectRequest(
            Request.Method.GET,
            url,
            null,
            { response ->
                try {
                    binding.txtDescricao.text = response.getString("descricao")

                    val urlImage = "$BASE_URL/images/" + response.getString("foto")
                    val imageRequest = ImageRequest(
                        urlImage,
                        { bitmap ->
                            binding.imgPost.setImageBitmap(bitmap)
                        },
                        0,
                        0,
                        ImageView.ScaleType.CENTER_CROP,
                        Bitmap.Config.RGB_565,
                        { error ->
                            error.printStackTrace()
                            Toast.makeText(this, "Erro ao carregar imagem", Toast.LENGTH_SHORT).show()
                        }
                    )
                    Volley.newRequestQueue(this).add(imageRequest)

                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(this, "Erro ao processar post", Toast.LENGTH_SHORT).show()
                }
            },
            { error ->
                error.printStackTrace()
                Toast.makeText(this, "Erro ao carregar post: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        )

        queue.add(jsonRequest)
    }
}
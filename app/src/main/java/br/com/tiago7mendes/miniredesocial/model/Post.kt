package br.com.tiago7mendes.miniredesocial.model

import android.graphics.Bitmap

data class Post(
    val descricao: String = "",
    val imagem: Bitmap? = null
)
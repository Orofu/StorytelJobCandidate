package app.storytel.candidate.com.utils

import android.graphics.drawable.Drawable
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.widget.TextView
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Workaround so glide can load sites that has User-Agent in its header.
 */
fun RequestManager.loadWithAgent(url: String?): RequestBuilder<Drawable> {
    return load(GlideUrl(url, LazyHeaders.Builder()
            .addHeader("User-Agent", "your-user-agent")
            .build()))
}

/**
 * Extension function for making a text underlined in a [TextView].
 */
fun TextView.underline(text: String?) {
    val content = SpannableString(text ?: "")
    content.setSpan(UnderlineSpan(), 0, content.length, 0)
    setText(content)
}

/**
 *  Function for creating a [Retrofit] instance with the given url.
 *
 * @param baseUrl The base url the [Retrofit] should point to.
 */
fun createRetrofitInstance(baseUrl: String): Retrofit {
    return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
}
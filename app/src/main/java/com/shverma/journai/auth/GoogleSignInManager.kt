package com.shverma.journai.auth

import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.shverma.journai.R

class GoogleSignInManager(context: Context) {

    val client: GoogleSignInClient

    init {

        val gso = GoogleSignInOptions.Builder(
            GoogleSignInOptions.DEFAULT_SIGN_IN
        )
            .requestIdToken(
                context.getString(R.string.default_web_client_id)
            )
            .requestEmail()
            .build()

        client = GoogleSignIn.getClient(context, gso)
    }
}
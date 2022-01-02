package com.cookandroid.eunstagram

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {
    var auth : FirebaseAuth? = null
    var googleSignInClient : GoogleSignInClient? = null
    var GOOGLE_LOGIN_CODE = 9001 // 리퀘스트 코드

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        // authentication library addition
        auth = FirebaseAuth.getInstance()

        // 로그인 버튼 이벤트
        email_login_button.setOnClickListener {
            // 파이어베이스에서 이메일 로그인 허가 필요
            emailLogin()
        }
        // 구글 버튼 이벤트
        google_sign_in_button.setOnClickListener{
            // 구글 로그인의 첫번째 단계
            googleLogin()
        }

        // 구글 로그인 옵션 (구글 API 키 필요)
        var gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)
    }
    // 구글 로그인 함수
    fun googleLogin(){
        var signInIntent = googleSignInClient?.signInIntent
        startActivityForResult(signInIntent, GOOGLE_LOGIN_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == GOOGLE_LOGIN_CODE) {
            // 구글에서 넘겨주는 로그인 결과 값
            var result = data?.let { Auth.GoogleSignInApi.getSignInResultFromIntent(data) }
            if (result != null) {
                if (result.isSuccess) {
                    var account = result?.signInAccount
                    // 두번째 단계 - 구글에서 파이어베이스로 넘겨줌
                    firebaseAuthWithGoogle(account)
                }
            }
        }
    }
    fun firebaseAuthWithGoogle(account : GoogleSignInAccount?){
        // 어카운트 내의 토큰을 파이어베이스로 넘겨줌
        var credential = GoogleAuthProvider.getCredential(account?.idToken, null)
        // 결과 값 받는 부분 (구글 로그인 허가 필요)
        auth?.signInWithCredential(credential)
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // 아이디와 패스워드가 일치한 경우
                    moveMainPage(task.result?.user)
                } else {
                    // 틀려서 로그인에 실패한 경우
                    Toast.makeText(this, task.exception?.message, Toast.LENGTH_LONG).show()
                }
            }
    }
    fun signinAndSignup(){
        auth?.createUserWithEmailAndPassword(email_edittext.text.toString(), password_edittext.text.toString())
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // 계정 생성 성공
                    Toast.makeText(this,
                    getString(R.string.signup_complete), Toast.LENGTH_SHORT).show()
                    // 다음페이지 호출
                    moveMainPage(auth?.currentUser)
                } else if (task.exception?.message.isNullOrEmpty()) {
                    // 로그인 에러시 토스트 메시지
                    Toast.makeText(this, task.exception?.message, Toast.LENGTH_LONG).show()
                } else {
                    // 계정 생성도 아니고 로그인 에러도 아닌 경우 로그인
                    signinEmail()
                }
        }
    }
    // 이메일 로그인
    fun emailLogin() {
        if (email_edittext.text.toString().isNullOrEmpty() || password_edittext.text.toString().isNullOrEmpty()) {
            Toast.makeText(this, getString(R.string.signout_fail_null),
                    Toast.LENGTH_SHORT).show()
        } else {
            signinAndSignup()
        }
    }

    fun signinEmail(){
        auth?.signInWithEmailAndPassword(email_edittext.text.toString(), password_edittext.text.toString())
                ?.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        //로그인 성공 및 다음페이지 호출
                        moveMainPage(auth?.currentUser)
                    } else {
                        //로그인 실패
                        Toast.makeText(this, task.exception!!.message, Toast.LENGTH_SHORT).show()
                    }
                }
    }
    // 다음 페이지로 넘어가는 함수
    fun moveMainPage(user:FirebaseUser?){
        if(user != null) {
            // 로그인에 성공한 경우 메인 액티비티 실행
            startActivity(Intent(this, MainActivity::class.java))

        }
    }
}
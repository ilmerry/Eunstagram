package com.cookandroid.eunstagram.navigation

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.cookandroid.eunstagram.R
import com.cookandroid.eunstagram.navigation.model.ContentDTO
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.activity_add_photo.*
import java.text.SimpleDateFormat
import java.util.*

class AddPhotoActivity : AppCompatActivity() {
    var PICK_IMAGE_FROM_ALBUM = 0   // 리퀘스트 코드
    var storage : FirebaseStorage? = null // 파이어베이스 스토리지 타입 변수
    var photoUri : Uri? = null  // 사진 uri를 담을 변수
    var auth : FirebaseAuth? = null
    var firestore : FirebaseFirestore? = null   // DB
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_photo)

        // 스토리지 초기화
        storage = FirebaseStorage.getInstance()
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // 화면 열자마자 앨범 실행
        var photoPickerIntent = Intent(Intent.ACTION_PICK)
        photoPickerIntent.type = "image/*"
        startActivityForResult(photoPickerIntent, PICK_IMAGE_FROM_ALBUM)

        // 버튼 이벤트
        addphoto_btn_upload.setOnClickListener{
            contentUpload()
        }
    }
    // 업로드한 이미지 받는 함수
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == PICK_IMAGE_FROM_ALBUM) {
            // 선택된 이미지의 경로가 여기로 넘어옴
            photoUri = data?.data
            addphoto_image.setImageURI(photoUri)
        } else{
            // 취소 버튼 눌렀을 때
            finish()
        }
    }
    fun contentUpload(){
        // 스토리지에 업로드할 때 이미지명이 중복되지 않도록 파일명 결정하는 코드
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "IMAGE_" + timestamp + "_.png"

        val storageRef = storage?.reference?.child("images")?.child(imageFileName)

        // 구글에서 권장하는 promise 방식으로 파이어베이스 업로드
        storageRef?.putFile(photoUri!!)?.addOnSuccessListener{
            storageRef.downloadUrl.addOnSuccessListener { uri ->
                val contentDTO = ContentDTO()

                // Insert downloadUrl of image
                contentDTO.imageUrl = uri.toString()

                // Insert uid of user
                contentDTO.uid = auth?.currentUser?.uid

                // Insert userID
                contentDTO.userId = auth?.currentUser?.email

                // Insert explain of content
                contentDTO.explain = addphoto_edit_explain.text.toString()

                // Insert time
                contentDTO.timestamp = System.currentTimeMillis()

                System.out.println(contentDTO)
                firestore?.collection("images")?.document()?.set(contentDTO)
                System.out.println(firestore?.collection("images")?.document()?.get())

                setResult(Activity.RESULT_OK)

                // 정상 종료
                finish()
            }

        // 파이어베이스에 파일 업로드 방식 두번째
        /*storageRef?.putFile(photoUri!!)?.addOnSuccessListener {
            // 업로드 성공시 이미지 주소 받아와서 데이터모델 생성
            storageRef.downloadUrl.addOnSuccessListener { uri ->
                // 위와 동일
            }
        }*/
        }
    }
}
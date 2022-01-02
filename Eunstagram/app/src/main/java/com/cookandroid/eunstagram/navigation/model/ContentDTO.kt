package com.cookandroid.eunstagram.navigation.model

import java.sql.Timestamp

// 업로드된 데이터를 체계적으로 관리하기 위한 데이터 모델
data class ContentDTO(
    var explain : String? = null,
    var imageUrl : String? = null,
    var uid : String? = null,
    var userId : String? = null,
    var timestamp: Long? = null,
    var favoriteCount : Int = 0,
    var favorites : Map<String, Boolean> = HashMap()){
    /*
    * 사용자가 입력한 사진 설명
    * 이미지 uri
    * 사용자 이메일
    * 어느 사용자의 이미지인지
    * 업로드 시간
    * 좋아요 수
    * 중복 좋아요가 되지 않도록 관리하는 해시맵
    */

    // 댓글 관리 클래스
    data class Comment(
        var uid : String? = null,
        var userId: String? = null,
        var comment: String? = null,
        var timestamp: Long? = null){
        /*
        * 사용자 id
        * 사용자 이메일
        * 코멘트
        * 언제 코멘트를 달았는지지
       */
    }
}
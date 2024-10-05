package com.example.haechorom

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.DialogFragment
import com.example.haechorom.api.dto.response.JosaPostResponse

class CustomDialogFragment : DialogFragment() {

    companion object {
        // newInstance 메서드는 JosaPostResponse 데이터를 가진 CustomDialogFragment 인스턴스를 생성
        fun newInstance(josa: JosaPostResponse): CustomDialogFragment {
            val fragment = CustomDialogFragment() // 새로운 CustomDialogFragment 객체를 생성
            val args = Bundle() // Bundle 객체를 생성
            args.putParcelable("josa", josa)  // putParcelable 사용
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val josa = arguments?.getParcelable<JosaPostResponse>("josa") // getParcelable 사용
        Log.d("모달 데이터", "받은 조사 데이터: $josa") // 테스트
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("조사자 : " + josa?.josaName)
        // id, serialNumber, josaName, josaDate, lat, lng, coastName, coastLength, collectBag, trashType, josaStatus
        val formattedLat = String.format("%.2f", josa?.lat)  // 위도를 소수점 두 번째 자리까지 포맷
        val formattedLng = String.format("%.2f", josa?.lng)  // 경도를 소수점 두 번째 자리까지 포맷
        val formattedDate = josa?.josaDate?.replace("T", " ")  // T를 공백으로 대체

        val trashTypeKor = when (josa?.trashType) {  // 쓰레기 종류에 대한 한글 번역
            "FISHING_NET" -> "폐어구류"
            "BUOY" -> "부표류"
            "DAILY_TRASH" -> "생활쓰레기류"
            "LARGE_TRASH" -> "대형 투기쓰레기류"
            "WOOD" -> "초목류"
            else -> "기타"
        }

        builder.setMessage(
            """
            조사 일련번호: ${josa?.serialNumber}
            조사일 : $formattedDate
            해안명: ${josa?.coastName}
            위도 : $formattedLat
            경도 : $formattedLng
            해안 길이 : ${josa?.coastLength}(m)
            예측량 : ${josa?.collectBag}(L)
            주요 쓰레기 종류 : $trashTypeKor
        """.trimIndent()
        )
        // 닫기 버튼 추가
        builder.setPositiveButton("닫기") { dialog, _ ->
            dialog.dismiss()
        }

        return builder.create()
    }
}
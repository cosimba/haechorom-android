package com.example.haechorom.mode2

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.example.haechorom.api.dto.response.CleanPostResponse

class CustomCleanDialogFragment : DialogFragment() {

    companion object {
        fun newInstance(clean: CleanPostResponse): CustomCleanDialogFragment {
            val fragment = CustomCleanDialogFragment()
            val args = Bundle()
            args.putParcelable("clean", clean)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val clean = arguments?.getParcelable<CleanPostResponse>("clean")
        val builder = AlertDialog.Builder(requireContext())

        builder.setTitle("청소자 : ${clean?.cleanName}")

        // 쓰레기 종류와 상태를 포맷
        val trashTypeKor = when (clean?.trashType) {
            "FISHING_NET" -> "폐어구류"
            "BUOY" -> "부표류"
            "DAILY_TRASH" -> "생활쓰레기류"
            "LARGE_TRASH" -> "대형 투기쓰레기류"
            "WOOD" -> "초목류"
            else -> "기타"
        }

        val cleanStatusKor = when (clean?.cleanStatus) {
            "NOT_STARTED" -> "청소 미시작"
            "IN_PROGRESS" -> "청소 진행 중"
            "COMPLETED" -> "청소 완료"
            else -> "기타"
        }

        val formattedLat = String.format("%.2f", clean?.lat)
        val formattedLng = String.format("%.2f", clean?.lng)
        val formattedDate = clean?.cleanDate?.replace("T", " ")

        builder.setMessage("""
            청소 일련번호: ${clean?.serialNumber}
            청소일: $formattedDate
            해안명: ${clean?.coastName}
            위도: $formattedLat
            경도: $formattedLng
            해안 길이: ${clean?.coastLength}(m)
            수거량: ${clean?.collectVal}(L)
            주요 쓰레기 종류: $trashTypeKor
            상태: $cleanStatusKor
        """.trimIndent())

        builder.setPositiveButton("닫기") { dialog, _ ->
            dialog.dismiss()
        }

        return builder.create()
    }
}
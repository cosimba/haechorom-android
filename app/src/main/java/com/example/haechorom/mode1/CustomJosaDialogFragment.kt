package com.example.haechorom.mode1

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.example.haechorom.R
import com.example.haechorom.api.dto.response.JosaPostResponse
import org.w3c.dom.Text

class CustomJosaDialogFragment : DialogFragment() {

    companion object {
        // newInstance 메서드는 JosaPostResponse 데이터를 가진 CustomDialogFragment 인스턴스를 생성
        fun newInstance(josa: JosaPostResponse): CustomJosaDialogFragment {
            val fragment = CustomJosaDialogFragment() // 새로운 CustomDialogFragment 객체를 생성
            val args = Bundle() // Bundle 객체를 생성
            args.putParcelable("josa", josa)  // putParcelable 사용
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = AlertDialog.Builder(requireContext()).create()
        val inflater = requireActivity().layoutInflater
        val view = inflater.inflate(R.layout.custom_dialog_josa, null)

        val josa = arguments?.getParcelable<JosaPostResponse>("josa") // getParcelable 사용

        // id, serialNumber, josaName, josaDate, lat, lng, coastName, coastLength, collectBag, trashType, josaStatus
        val formattedLat = String.format("%.2f", josa?.lat)  // 위도를 소수점 두 번째 자리까지 포맷
        val formattedLng = String.format("%.2f", josa?.lng)  // 경도를 소수점 두 번째 자리까지 포맷

        val trashTypeKor = when (josa?.trashType) {  // 쓰레기 종류에 대한 한글 번역
            "FISHING_NET" -> "폐어구류"
            "BUOY" -> "부표류"
            "DAILY_TRASH" -> "생활쓰레기류"
            "LARGE_TRASH" -> "대형 투기쓰레기류"
            "WOOD" -> "초목류"
            else -> "기타"
        }

        val josaName = view.findViewById<TextView>(R.id.josa_name)
        val josaDate = view.findViewById<TextView>(R.id.josa_date)
        val coastName = view.findViewById<TextView>(R.id.coastName)
        val coastLength = view.findViewById<TextView>(R.id.coastLength)
        val collectBag = view.findViewById<TextView>(R.id.collectBag)
        val trashType = view.findViewById<TextView>(R.id.trashType)

        // 데이터 바인딩
        josaName.text = "조사자 : ${josa?.josaName}"
        josaDate.text = "조사일 : ${josa?.josaDate?.replace("T", " ")}" // T를 공백으로 대체
        coastName.text = "해안명 : ${josa?.coastName}"
        coastLength.text = "해안 길이 : ${josa?.coastLength}(m)"
        collectBag.text = "예측량 : ${josa?.collectBag}(L)"
        trashType.text = "주요 쓰레기 종류 : $trashTypeKor"

        // 닫기 버튼 이벤트
        val closeButton = view.findViewById<Button>(R.id.btn_close)
        closeButton.setOnClickListener {
            dialog.dismiss()
        }

        dialog.setView(view)
        return dialog
    }
}
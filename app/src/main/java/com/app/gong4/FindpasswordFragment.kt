package com.app.gong4

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.navigation.findNavController
import com.app.gong4.DTO.RequestFindPwdBody
import com.app.gong4.DTO.RequestLoginBody
import com.app.gong4.DTO.ResponseFindPwdBody
import com.app.gong4.DTO.ResponseLoginBody
import com.app.gong4.api.RequestServer
import com.app.gong4.databinding.FragmentFindpasswordBinding
import com.app.gong4.databinding.FragmentLoginBinding
import com.app.gong4.util.CommonTextWatcher
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FindpasswordFragment : Fragment() {

    private lateinit var binding: FragmentFindpasswordBinding
    val requestServer = RequestServer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val mainActivity = activity as MainActivity
        mainActivity.hideBottomNavigationBar(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFindpasswordBinding.inflate(inflater, container, false)
        //확인 버튼 비활성화
        binding.confirmButton.isEnabled = false

        checkInput()
        goConfirm()

        return binding.root
    }

    fun checkInput() {
        binding.emailEditText.addTextChangedListener(CommonTextWatcher(
            afterChanged = { text ->
                binding.confirmButton.isEnabled = binding.emailEditText.text.toString() != ""
            }
        ))
    }

    /* 확인 버튼 */
    fun goConfirm(){
        binding.confirmButton.setOnClickListener {
            val email = binding.emailEditText.text.toString()
            binding.validEmailTextView.text = ""
            binding.waitingView.visibility = View.VISIBLE
            requestServer.userService.findPwd(RequestFindPwdBody(email)).enqueue(object :
                Callback<ResponseFindPwdBody>{
                override fun onResponse(
                    call: Call<ResponseFindPwdBody>,
                    response: Response<ResponseFindPwdBody>
                ) {
                    binding.waitingView.visibility = View.INVISIBLE
                    if(response.isSuccessful){
                        var repos: ResponseFindPwdBody? = response.body()
                        it.findNavController().navigate(R.id.action_findpasswordFragment_to_loginFragment)
                    }else{
                        val error = response.errorBody()!!.string().trimIndent()
                        val result = Gson().fromJson(error, ResponseFindPwdBody::class.java)
                        binding.validEmailTextView.text = result.msg
                        binding.confirmButton.isEnabled = false
                    }
                }

                override fun onFailure(call: Call<ResponseFindPwdBody>, t: Throwable) {
                    Log.d("결과 - 통신 실패", t.toString())
                    binding.waitingView.visibility = View.INVISIBLE
                    Toast.makeText(context,"서버와의 통신이 원활하지 않습니다.",Toast.LENGTH_SHORT)
                }

            })
        }
    }
}
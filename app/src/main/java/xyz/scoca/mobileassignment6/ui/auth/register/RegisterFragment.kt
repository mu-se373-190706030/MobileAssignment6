package xyz.scoca.mobileassignment6.ui.auth.register

import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import xyz.scoca.assignment6.model.AuthResponse
import xyz.scoca.mobileassignment6.R
import xyz.scoca.mobileassignment6.data.local.ClientPreferences
import xyz.scoca.mobileassignment6.databinding.FragmentRegisterBinding
import xyz.scoca.mobileassignment6.network.NetworkHelper

class RegisterFragment : Fragment() {
    private lateinit var binding : FragmentRegisterBinding
    private var username = ""
    private var email = ""
    private var password = ""
    private var gender = "male"
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegisterBinding.inflate(inflater,container,false)

        binding.btnRegister.setOnClickListener {
            validateUser()
        }
        return binding.root
    }

    private fun register(username: String, email: String, password: String, gender: String) {
        binding.progressBar.visibility = View.VISIBLE
        NetworkHelper().userService?.register(username,email,password,gender)
            ?.enqueue(object : Callback<AuthResponse> {
                override fun onResponse(
                    call: Call<AuthResponse>,
                    response: Response<AuthResponse>
                ) {
                    binding.progressBar.visibility = View.GONE
                    val status = response.body()?.status.toString()
                    val errorMsg = response.body()?.message.toString()

                    if (status == "true") {
                        with(ClientPreferences(requireContext())) {
                            this.setUserEmail(response.body()?.user!![0].email)
                            this.setRememberMe(true)
                        }
                        findNavController().navigate(R.id.action_registerFragment_to_homeFragment)
                    } else
                        Toast.makeText(requireContext(), errorMsg, Toast.LENGTH_SHORT)
                            .show()
                }

                override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(requireContext(),t.message.toString(), Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun validateUser() {
        username = binding.etRegisterUsername.text.toString()
        email = binding.etRegisterEmail.text.toString()
        password = binding.etRegisterPassword.text.toString()

        binding.radioGender.setOnCheckedChangeListener { radioGroup, id ->
            val radioButton : RadioButton = radioGroup.findViewById(id)
            gender = radioButton.text.toString()
        }

        if (TextUtils.isEmpty(username)) {
            binding.etRegisterUsername.error = "Enter name"
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.etRegisterEmail.error = "Invalid email format"
        } else if (TextUtils.isEmpty(password)) {
            binding.etRegisterPassword.error = "Enter password"
        } else {
            binding.progressBar.visibility = View.VISIBLE
            register(username,email,password,gender)
        }
    }

}
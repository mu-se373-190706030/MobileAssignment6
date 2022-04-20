package xyz.scoca.mobileassignment6.ui.auth.login

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import xyz.scoca.assignment6.model.AuthResponse
import xyz.scoca.mobileassignment6.R
import xyz.scoca.mobileassignment6.data.local.ClientPreferences
import xyz.scoca.mobileassignment6.databinding.FragmentLoginBinding
import xyz.scoca.mobileassignment6.network.NetworkHelper


class LoginFragment : Fragment() {
    private lateinit var binding : FragmentLoginBinding
    private var username: String = ""
    private var password: String = ""
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater,container,false)
        initListeners()
        isUserLoggedIn()

        binding.btnLogin.setOnClickListener {
            login()
        }
        binding.btnLoginToRegister.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }
        return binding.root
    }

    private fun login() {
        username = binding.etLoginUsername.text.toString()
        password = binding.etLoginPassword.text.toString()
        binding.progressBar.visibility = View.VISIBLE

        NetworkHelper().userService?.login(username, password)
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
                        findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
                    } else
                        Toast.makeText(requireContext(), errorMsg, Toast.LENGTH_SHORT)
                            .show()
                }

                override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(requireContext(), t.message.toString(), Toast.LENGTH_SHORT)
                        .show()
                }
            })
    }

    private fun initListeners() {
        binding.etLoginUsername.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(p0: Editable?) {
                checkFields()
            }
        })

        binding.etLoginPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(p0: Editable?) {
                checkFields()
            }
        })
    }

    private fun checkFields() {
        if (!binding.etLoginUsername.text.isNullOrEmpty() && !binding.etLoginPassword.text.isNullOrEmpty()) {
            binding.btnLogin.isEnabled = true
            binding.btnLogin.alpha = 1F
        } else {
            binding.btnLogin.isEnabled = false
            binding.btnLogin.alpha = 0.2F
        }
    }

    private fun isUserLoggedIn() {
        if (ClientPreferences(requireContext()).isRememberMe()) {
            findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
        }
    }
}
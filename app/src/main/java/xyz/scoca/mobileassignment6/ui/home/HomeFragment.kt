package xyz.scoca.mobileassignment6.ui.home

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import xyz.scoca.mobileassignment6.R
import xyz.scoca.mobileassignment6.data.local.ClientPreferences
import xyz.scoca.mobileassignment6.databinding.FragmentHomeBinding
import xyz.scoca.mobileassignment6.network.NetworkHelper


class HomeFragment : Fragment() {
    private lateinit var binding : FragmentHomeBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater,container,false)
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        fetchUserData()

        binding.buttonLogout.setOnClickListener {
            logout()
        }
        binding.btnUpdate.setOnClickListener {
            updateUser()
        }
        binding.btnDelete.setOnClickListener {
            deleteUser()
        }
        return binding.root
    }

    private fun fetchUserData() {
        var username = ""
        var gender = ""
        val email = ClientPreferences(requireContext()).getUserEmail().toString()
        binding.tvUserEmail.text = email

        NetworkHelper().userService?.fetchUser(email)
            ?.enqueue(object : Callback<xyz.scoca.mobileassignment6.model.crud.User>{
                override fun onResponse(
                    call: Call<xyz.scoca.mobileassignment6.model.crud.User>,
                    response: Response<xyz.scoca.mobileassignment6.model.crud.User>
                ) {
                    username = response.body()?.user?.username.toString()
                    gender = response.body()?.user?.gender.toString()

                    binding.etUsername.setText(username)
                    binding.etGender.setText(gender)
                }

                override fun onFailure(
                    call: Call<xyz.scoca.mobileassignment6.model.crud.User>,
                    t: Throwable
                ) {
                    Toast.makeText(requireContext(),t.message.toString(),Toast.LENGTH_SHORT).show()
                }

            })
    }

    private fun updateUser(){
        val username = binding.etUsername.text.toString()
        val email = ClientPreferences(requireContext()).getUserEmail().toString()
        val gender = binding.etGender.text.toString()

        NetworkHelper().userService?.updateUser(username,email,gender)
            ?.enqueue(object : Callback<xyz.scoca.mobileassignment6.model.crud.User>{
                override fun onResponse(
                    call: Call<xyz.scoca.mobileassignment6.model.crud.User>,
                    response: Response<xyz.scoca.mobileassignment6.model.crud.User>
                ) {
                    Toast.makeText(requireContext(),"Successfully Updated.",Toast.LENGTH_SHORT).show()
                }

                override fun onFailure(
                    call: Call<xyz.scoca.mobileassignment6.model.crud.User>,
                    t: Throwable
                ) {
                    Toast.makeText(requireContext(),t.message.toString(),Toast.LENGTH_SHORT).show()
                }

            })
    }

    private fun deleteUser(){
        val email = ClientPreferences(requireContext()).getUserEmail().toString()

        NetworkHelper().userService?.deleteUser(email)
            ?.enqueue(object : Callback<xyz.scoca.mobileassignment6.model.crud.User>{
                override fun onResponse(
                    call: Call<xyz.scoca.mobileassignment6.model.crud.User>,
                    response: Response<xyz.scoca.mobileassignment6.model.crud.User>
                ) {
                    Toast.makeText(requireContext(),"Account Deleted Successfully",Toast.LENGTH_SHORT).show()
                    logout()
                }

                override fun onFailure(
                    call: Call<xyz.scoca.mobileassignment6.model.crud.User>,
                    t: Throwable
                ) {
                    Toast.makeText(requireContext(),t.message.toString(),Toast.LENGTH_SHORT).show()
                    Log.e("ERRORRRRR",t.message.toString())
                }

            })
    }
    private fun logout() {
        ClientPreferences(requireContext()).clearSharedPref()
        findNavController().navigate(R.id.action_homeFragment_to_loginFragment)
    }


}
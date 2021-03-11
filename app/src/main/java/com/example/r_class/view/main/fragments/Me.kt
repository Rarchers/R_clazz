package com.example.r_class.view.main.fragments

import android.app.ProgressDialog
import android.content.Intent
import android.drm.ProcessedData
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.r_class.R
import com.example.r_class.model.adapters.CourseAdapter
import com.example.r_class.net.Connection
import com.example.r_class.utils.SPUtils
import com.example.r_class.view.login.LoginActivity
import com.example.r_class.viewmodel.CourseViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Me.newInstance] factory method to
 * create an instance of this fragment.
 */
class Me : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_me, container, false)

        val name = view.findViewById<TextView>(R.id.textView9)
        val code = view.findViewById<TextView>(R.id.textView11)
        val pwd = view.findViewById<TextView>(R.id.textView13)
        val identify = view.findViewById<TextView>(R.id.textView15)

        val spUtils = activity?.applicationContext?.let { SPUtils(it) }

        name.text = spUtils?.name
        code.text = spUtils?.userName
        identify.text = spUtils?.identify
        val pwdLen = spUtils?.password?.length ?: 0
        val buffer = StringBuffer()
        repeat(pwdLen){
            buffer.append("*")
        }
        pwd.text = buffer.toString()

        val exit = view.findViewById<ConstraintLayout>(R.id.exit)

        exit.setOnClickListener {
            startActivity(Intent(activity,LoginActivity::class.java))
            activity?.finish()
        }

        return view
    }



    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment Me.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Me().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
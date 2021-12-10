package com.example.bazzarappdg.ui.slideshow

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bazzarappdg.*
import com.example.bazzarappdg.databinding.FragmentSlideshowBinding
import com.google.firebase.firestore.FirebaseFirestore

class SlideshowFragment : Fragment() {

    private lateinit var slideshowViewModel: SlideshowViewModel
    private var _binding: FragmentSlideshowBinding? = null
    private val binding get() = _binding!!

    private lateinit var recycleView: RecyclerView
    private var db = FirebaseFirestore.getInstance()
    private var listOrder = mutableListOf<OrderEntity>()
    private lateinit var orderAdapter: OrderAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        slideshowViewModel =
            ViewModelProvider(this).get(SlideshowViewModel::class.java)

        _binding = FragmentSlideshowBinding.inflate(inflater, container, false)
        val root: View = binding.root

        //Recycler
        recycleView = root.findViewById<RecyclerView>(R.id.reclyclerOrder)
        recycleView.layoutManager = LinearLayoutManager(activity)
        recycleView.setHasFixedSize(true)

        getAllOrderNew()
        orderAdapter = OrderAdapter(listOrder);
        recycleView.adapter = orderAdapter;

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun getAllOrderNew() {
        listOrder.clear()

        val prefs = requireActivity().getSharedPreferences(resources.getString(R.string.preds_file), Context.MODE_PRIVATE)
        var email = prefs.getString("email", null).toString()

        db.collection("car").whereEqualTo("state", true).whereEqualTo("email",email)
            .get().addOnSuccessListener {
                if (it.any()) {

                    var idOrder = 1;
                    for (item in it) {

                        var des = ""

                        db.collection("car").document(item.id).collection("products")
                            .get().addOnSuccessListener { products ->
                                if (products.any()) {
                                    for (product in products) {
                                        des = des + product.data["title"] + " x " + product.data["amount"] + "\n"
                                    }

                                    listOrder.add(
                                        OrderEntity(
                                            "Order" + " " + idOrder,
                                            des
                                        )
                                    )
                                    idOrder++
                                    orderAdapter.notifyDataSetChanged();
                                }
                            }
                    }
                    orderAdapter.notifyDataSetChanged();
                }
            }
    }

}
package com.example.bazzarappdg

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import androidx.fragment.app.setFragmentResultListener
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso

class ProductFragment : Fragment() {

    private var db = FirebaseFirestore.getInstance()

    private var textproduct: TextView? = null
    private var imageproduct: ImageView? = null
    private var idProduct: TextView? = null
    private var desProduct: TextView? = null
    private var listViewComments: ListView? = null
    private var listComments = arrayListOf<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        var root = inflater.inflate(R.layout.fragment_product, container, false)

        textproduct = root.findViewById<TextView>(R.id.textProduct)
        imageproduct = root.findViewById<ImageView>(R.id.imageProduct)
        desProduct = root.findViewById<TextView>(R.id.desProduct)
        idProduct = root.findViewById<TextView>(R.id.idProduct)
        listViewComments = root.findViewById<ListView>(R.id.listComments)

        setFragmentResultListener("key") { requestKey, bundle ->
            //Load Product
            bundle.getString("product")?.let { loadProduct(it) };
        }

        return root
    }


    private fun loadProduct(product: String) {

        db.collection("product").document(product).get()
            .addOnSuccessListener {
                textproduct!!.setText(it.get("title") as String)
                Picasso.get().load(it.get("imagen").toString()).into(imageproduct!!)
                idProduct!!.setText(it.id)
                desProduct!!.setText(it.get("description") as String)

            }

        db.collection("product").document(product).collection("comments").get()
            .addOnSuccessListener {

                if (it.any()) {

                    for (comment in it) {
                        listComments.add(
                            comment.get("user").toString() + " : " + comment.get("comment").toString()
                        )
                    }

                    listViewComments!!.adapter = activity?.let { it1 ->
                        ArrayAdapter(
                            it1,
                            android.R.layout.simple_dropdown_item_1line,
                            listComments
                        )
                    }

                }
            }
    }


}
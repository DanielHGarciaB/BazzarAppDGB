package com.example.bazzarappdg

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.Navigation
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import java.util.*

class ProductFragment : Fragment() {

    private var db = FirebaseFirestore.getInstance()

    private var textproduct: TextView? = null
    private var imageproduct: ImageView? = null
    private var idProduct: TextView? = null
    private var desProduct: TextView? = null
    private var desText: TextView? = null
    private var listViewComments: ListView? = null
    private var listComments = arrayListOf<String>()
    private var commentText : TextView? = null
    private var categoryAndsellerProduct: TextView? = null
    private var costProduct: TextView? = null
    private var amountText: TextView? = null
    private var costUnit = 0
    private var image = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        var root = inflater.inflate(R.layout.fragment_product, container, false)

        textproduct = root.findViewById<TextView>(R.id.textProduct)
        imageproduct = root.findViewById<ImageView>(R.id.imageProduct)
        desProduct = root.findViewById<TextView>(R.id.desProduct)
        desText = root.findViewById<TextView>(R.id.desText)
        idProduct = root.findViewById<TextView>(R.id.idProduct)
        listViewComments = root.findViewById<ListView>(R.id.listComments)
        categoryAndsellerProduct = root.findViewById<TextView>(R.id.categoryAndsellerProduct)
        costProduct = root.findViewById<TextView>(R.id.costProduct)
        commentText = root.findViewById<TextView>(R.id.commentText)
        amountText = root.findViewById<TextView>(R.id.amount)

        setFragmentResultListener("key") { requestKey, bundle ->
            //Load Product
            bundle.getString("products")?.let { loadProduct(it) };
        }

        var buttonLess = root.findViewById<Button>(R.id.less)
        buttonLess.setOnClickListener {
            Log.d(ContentValues.TAG, "Less")

            var amount =  amountText!!.text.toString().toInt()

            if(amount > 1){
                amount -= 1
                amountText!!.text = amount.toString()
            }

            costProduct!!.text = (costUnit * amount).toString()

        }

        var buttonMore = root.findViewById<Button>(R.id.more)
        buttonMore.setOnClickListener {
            Log.d(ContentValues.TAG, "more")
            var amount =  amountText!!.text.toString().toInt()

            if(amount < 10){
                amount += 1
                amountText!!.text = amount.toString()
            }

            costProduct!!.text = (costUnit * amount).toString()

        }

        var buttonAddCar = root.findViewById<ImageView>(R.id.addCar)
        buttonAddCar.setOnClickListener {
            Log.d(ContentValues.TAG, "car")

            //Save Car
            var idCar = ""
            val getPrefs = requireActivity().getSharedPreferences(resources.getString(R.string.preds_file), Context.MODE_PRIVATE)
            idCar = getPrefs.getString("car", null).toString()
            var email = getPrefs.getString("email", null).toString()

            if(idCar == null || idCar.isEmpty() || idCar == "null"){
                idCar = UUID.randomUUID().toString()
                val setPrefs = requireActivity().getSharedPreferences(resources.getString(R.string.preds_file), Context.MODE_PRIVATE).edit()
                setPrefs.putString("car",idCar)
                setPrefs.apply()
            }

            db.collection("car").document(idCar).set(hashMapOf("state" to false,"email" to email))

            db.collection("car").document(idCar)
                .collection("products").document(idProduct!!.text.toString()).set(
                    hashMapOf("amount" to amountText!!.text.toString(),
                        "cost" to costUnit!!.toString(),
                        "image" to image,
                        "title" to textproduct!!.text.toString(),
                    ))

            var nav = Navigation.createNavigateOnClickListener(R.id.nav_gallery)
            nav.onClick(view);

        }

        return root
    }


    private fun loadProduct(product: String) {

        db.collection("products").document(product).get()
            .addOnSuccessListener {
                textproduct!!.text = it.get("title") as String
                Picasso.get().load(it.get("imagen").toString()).into(imageproduct!!)
                image = it.get("imagen").toString()

                idProduct!!.text = it.id

                var des = it.get("description") as String
                if(des.isNotEmpty()){
                    desProduct!!.text = des
                    desText!!.text = "Description"
                }

                categoryAndsellerProduct!!.text = it.get("category").toString() +" "+it.get("seller").toString()
                costProduct!!.text =  it.get("cost").toString()
                costUnit = it.get("cost").toString().toInt()
            }

        db.collection("products").document(product).collection("commentation").get()
            .addOnSuccessListener {

                if (it.any()) {

                    for (comment in it) {
                        listComments.add(
                            comment.get("user").toString()
                                    + " : " + "Stars"
                                    + " " + comment.get("score").toString()
                                    + " : " + comment.get("comment").toString()
                        )
                    }

                    listViewComments!!.adapter = activity?.let { it1 ->
                        ArrayAdapter(
                            it1,
                            android.R.layout.simple_dropdown_item_1line,
                            listComments
                        )
                    }

                    commentText!!.text = resources.getString(R.string.test_Comments)
                }
            }
                /*.addOnFailureListener { exception ->
                Log.d(TAG, "Error getting documents: ", exception)
                }*/
    }

}
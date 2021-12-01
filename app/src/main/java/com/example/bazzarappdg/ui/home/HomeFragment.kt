package com.example.bazzarappdg.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.bazzarappdg.R
import com.example.bazzarappdg.databinding.FragmentHomeBinding
import android.content.ContentValues.TAG
import android.util.Log
import android.widget.*
import androidx.appcompat.widget.SearchView
import androidx.core.view.get
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import android.R.attr.category
import android.content.Context
import androidx.navigation.Navigation
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.preference.PreferenceManager
import android.content.SharedPreferences
import com.example.bazzarappdg.*


public class HomeFragment : Fragment(), SearchView.OnQueryTextListener,
    AdapterView.OnItemSelectedListener, ProductAdapter.OnItemClickListener {

    private lateinit var homeViewModel: HomeViewModel
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var recycleView: RecyclerView
    private var db = FirebaseFirestore.getInstance()
    private var listProduct = mutableListOf<ProductEntity>()
    private lateinit var productAdapter: ProductAdapter

    private lateinit var svSearch: SearchView

    private lateinit var spinnerCategory: Spinner
    private lateinit var spinnerSeller: Spinner
    private var listCategory = arrayListOf<String>()
    private var listSeller = arrayListOf<String>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        //Recycler
        recycleView = root.findViewById<RecyclerView>(R.id.reclycler)
        recycleView.layoutManager = LinearLayoutManager(activity)
        recycleView.setHasFixedSize(true)

        getAllProduct()
        productAdapter = ProductAdapter(listProduct, this);
        recycleView.adapter = productAdapter;

        //Search
        svSearch = root.findViewById<SearchView>(R.id.svSearch)
        svSearch.setOnQueryTextListener(this)

        //Spinner Category
        spinnerCategory = root.findViewById(R.id.category_spinner)
        spinnerCategory.onItemSelectedListener = this
        listCategory.add(resources.getString(R.string.test_All))

        activity?.let {
            ArrayAdapter(
                it,
                android.R.layout.simple_spinner_item,
                listCategory
            ).also { adapter ->
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinnerCategory.adapter = adapter
            }
        }

        //Spinner Seller
        spinnerSeller = root.findViewById(R.id.seller_spinner)
        spinnerSeller.onItemSelectedListener = this
        listSeller.add(resources.getString(R.string.test_All))

        activity?.let {
            ArrayAdapter(
                it,
                android.R.layout.simple_spinner_item,
                listSeller
            ).also { adapter ->
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinnerSeller.adapter = adapter
            }
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun getAllProduct() {
        listProduct.clear()

        db.collection("products").get().addOnSuccessListener { result ->
            for (document in result) {
                Log.d(TAG, "${document.id} => ${document.data}")

                if (!listCategory.contains(document.data["category"].toString())) {
                    listCategory.add(document.data["category"].toString())
                }

                if (!listSeller.contains(document.data["seller"].toString())) {
                    listSeller.add(document.data["seller"].toString())
                }

                var productExist = listProduct.find { it.id == document.id }

                if (productExist == null) {
                    listProduct.add(
                        ProductEntity(
                            document.data["imagen"].toString(),
                            document.data["title"].toString(),
                            document.data["cost"].toString(),
                            document.data["category"].toString(),
                            document.data["seller"].toString(),
                            document.id,
                            averageScore(document.id)
                        )
                    )
                }

            }
            productAdapter.notifyDataSetChanged();
        }

    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return true;
    }

    override fun onQueryTextChange(newText: String?): Boolean {

        if (newText!!.isEmpty()) {
            getAllProduct()
        } else {
            searchForTitle(newText)
        }

        return true;
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

        if (parent != null && parent.getItemAtPosition(position).toString().isNotEmpty()) {

            var category: String = spinnerCategory.getSelectedItem().toString()
            var seller: String = spinnerSeller.getSelectedItem().toString()

            var all = resources.getString(R.string.test_All)

            if (category == all && seller == all) {
                getAllProduct()
            } else {
                if (category == all) {
                    filterForSeller(seller)
                } else {
                    if (seller == all) {
                        filterForCategory(category)
                    } else {
                        filterForCategoryAndSeller(category, seller)
                    }
                }
            }
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {

    }

    private fun filterForCategory(category: String) {
        listProduct.clear()

        db.collection("products")
            .whereEqualTo("category", category)
            .get().addOnSuccessListener { result ->
                for (document in result) {
                    Log.d(TAG, "${document.id} => ${document.data}")

                    var productExist = listProduct.find { it.id == document.id }

                    if (productExist == null) {
                        listProduct.add(
                            ProductEntity(
                                document.data["imagen"].toString(),
                                document.data["title"].toString(),
                                document.data["cost"].toString(),
                                document.data["category"].toString(),
                                document.data["seller"].toString(),
                                document.id,
                                averageScore(document.id)
                            )
                        )
                    }
                }
                productAdapter.notifyDataSetChanged();
            }
    }

    private fun filterForSeller(seller: String) {
        listProduct.clear()

        db.collection("products")
            .whereEqualTo("seller", seller)
            .get().addOnSuccessListener { result ->
                for (document in result) {
                    Log.d(TAG, "${document.id} => ${document.data}")

                    var productExist = listProduct.find { it.id == document.id }

                    if (productExist == null) {
                        listProduct.add(
                            ProductEntity(
                                document.data["imagen"].toString(),
                                document.data["title"].toString(),
                                document.data["cost"].toString(),
                                document.data["category"].toString(),
                                document.data["seller"].toString(),
                                document.id,
                                averageScore(document.id)
                            )
                        )
                    }
                }
                productAdapter.notifyDataSetChanged();
            }
    }

    private fun filterForCategoryAndSeller(category: String, seller: String) {
        listProduct.clear()

        db.collection("products")
            .whereEqualTo("seller", seller).whereEqualTo("category", category)
            .get().addOnSuccessListener { result ->
                for (document in result) {
                    Log.d(TAG, "${document.id} => ${document.data}")

                    var productExist = listProduct.find { it.id == document.id }

                    if (productExist == null) {
                        listProduct.add(
                            ProductEntity(
                                document.data["imagen"].toString(),
                                document.data["title"].toString(),
                                document.data["cost"].toString(),
                                document.data["category"].toString(),
                                document.data["seller"].toString(),
                                document.id,
                                averageScore(document.id)
                            )
                        )
                    }
                }
                productAdapter.notifyDataSetChanged();
            }
    }

    private fun searchForTitle(newText: String) {
        listProduct.clear()
        db.collection("products")
            .whereGreaterThanOrEqualTo("title", newText)
            .whereLessThanOrEqualTo("title", (newText + "\uF7FF"))
            .get().addOnSuccessListener { result ->
                for (document in result) {
                    Log.d(TAG, "${document.id} => ${document.data}")

                    var productExist = listProduct.find { it.id == document.id }

                    if (productExist == null) {
                        listProduct.add(
                            ProductEntity(
                                document.data["imagen"].toString(),
                                document.data["title"].toString(),
                                document.data["cost"].toString(),
                                document.data["category"].toString(),
                                document.data["seller"].toString(),
                                document.id,
                                averageScore(document.id)
                            )
                        )
                    }
                }
                productAdapter.notifyDataSetChanged();
            }
    }

    override fun onItemClick(position: Int) {

        val productItem: ProductEntity = listProduct[position]

        //Go  ProductActivity
        val prefs = requireActivity().getSharedPreferences(resources.getString(R.string.preds_file), Context.MODE_PRIVATE)
        val email = prefs.getString("email", null)

        var bundle =Bundle()
        bundle.putString("email",email)
        bundle.putString("products",productItem.id)
        parentFragmentManager.setFragmentResult("key",bundle)

        var nav = Navigation.createNavigateOnClickListener(R.id.nav_product)
        nav.onClick(view);

    }

    private fun averageScore(product: String):Double{

        var average : Double = 0.0

        db.collection("products").document(product).collection("score").get()
            .addOnSuccessListener {

                if (it.any()) {
                    for (score in it) {
                        average += score.get("score").toString().toDouble()
                    }
                    average /= it.count()
                    productAdapter.notifyDataSetChanged();
                }
            }

        return average
    }


}
package com.lediya.infosys.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.lediya.infosys.R
import com.lediya.infosys.databinding.ListScreenFragmentBinding
import com.lediya.infosys.model.Row
import com.lediya.infosys.utility.ResultType
import com.lediya.infosys.utility.Utils
import com.lediya.infosys.view.adapter.CountryAdapter
import com.lediya.infosys.view.viewModel.ListScreenViewModel

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class ListScreenFragment : Fragment() {
    private lateinit var binding: ListScreenFragmentBinding
    private lateinit var viewModel: ListScreenViewModel
    private lateinit var adapter: CountryAdapter
    private var pullRefresh = false
    private var internetConnectivityBoolean =false
    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            inflater, R.layout.list_screen_fragment,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProviders.of(requireActivity()).get(ListScreenViewModel::class.java)
        adapter = CountryAdapter()
        observer()
        setTitle(getString(R.string.app_name))
    }
    override fun onStart() {
        super.onStart()
        loadData()
    }
    /** observer used to observe the live data */
    private fun observer(){
        viewModel.fetchResult.observe(
            viewLifecycleOwner, Observer { event ->
                event.getContentIfNotHandled()?.let { result ->
                    when (result.resultType) {
                        ResultType.PENDING -> {
                            showLoading()
                        }
                        ResultType.SUCCESS -> {
                            viewModel.getCountryDataFromDatabase()
                        }
                        ResultType.FAILURE -> {
                            hideLoading()
                            if(internetConnectivityBoolean){
                                binding.errorTextData.text = getString(R.string.no_internet_toast)
                            }else {
                                binding.errorTextData.text = getString(R.string.failure_toast)
                            }

                        }
                    }
                }
            }
        )
        viewModel.countryList?.observe(
            viewLifecycleOwner, Observer { event ->
                event.getContentIfNotHandled()?.let {
                        it ->
                    hideLoading()
                    if(!it.isNullOrEmpty()){
                        showData(it)
                    }else{
                        binding.errorTextData.text = getString(R.string.failure_toast)
                    }
                }
            }
        )
        viewModel.title.observe(viewLifecycleOwner, Observer {
            if(!it.isNullOrBlank()){
                setTitle(it)
            }

        })
        binding.swipeRefreshLayout.setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener {
            if (!internetConnectivityBoolean) {
                pullRefresh = true
                loadData()
            }
        })
    }
    /**Set the title*/
    private fun setTitle(title:String){
        (activity as AppCompatActivity).supportActionBar?.title = title
    }
    /** Method used to set the data in recyclerview*/
    private fun showData(countryData:List<Row>) {
        val sortList = mutableListOf<Row>()
        binding.recyclerView.layoutManager = LinearLayoutManager(requireActivity())
        countryData.let {
            for (item in it ) {
                if(!item.title.isNullOrBlank()&&!item.description.isNullOrBlank()&&!item.description.isNullOrBlank()){
                    sortList.add(item)
                }
            }
            adapter.setItems(sortList)
            binding. recyclerView.addItemDecoration(
                DividerItemDecoration(
                    binding.recyclerView.context,
                    (binding.recyclerView.layoutManager as LinearLayoutManager).orientation
                )
            )
            binding.recyclerView.adapter = adapter
        }
    }
    /** checks connectivity and request data call, if its offline data available in local database , it will load the data */
    private fun loadData(){
        if(Utils.isConnectedToNetwork(requireActivity())){
            showLoading()
            binding.errorTextData.visibility = View.GONE
            viewModel.downloadCountryData()
        }else{
            internetConnectivityBoolean = true
            viewModel.getCountryDataFromDatabase()
        }

    }
    /** hide the progress bar*/
    private fun hideLoading() {
        if(!pullRefresh){
            binding.progressBar.visibility = View.GONE
            activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        }else{
            binding.swipeRefreshLayout.isRefreshing = false
        }

    }
    /** shows the progress bar*/
    private fun showLoading() {
        if(!pullRefresh){
            binding.progressBar.visibility = View.VISIBLE
            activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            activity?.window?.setFlags(
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
            )
        }else{
            binding.swipeRefreshLayout.isRefreshing = true
        }

    }
}
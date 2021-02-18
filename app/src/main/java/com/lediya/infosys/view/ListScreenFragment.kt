package com.lediya.infosys.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
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
    private var adapter = CountryAdapter()
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
        viewModel = ViewModelProviders.of(requireActivity()).get(ListScreenViewModel::class.java)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observer()
        setTitle(getString(R.string.app_name))
        setAdapter()
    }
    override fun onStart() {
        super.onStart()
        loadData()
    }
    /**set adapter*/
    private fun setAdapter(){
        binding.recyclerView.layoutManager = LinearLayoutManager(requireActivity())
        binding. recyclerView.addItemDecoration(
            DividerItemDecoration(
                binding.recyclerView.context,
                (binding.recyclerView.layoutManager as LinearLayoutManager).orientation
            )
        )
        binding.recyclerView.adapter = adapter
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
                        }
                        ResultType.FAILURE -> {
                          checkInternetConnectivity()
                        }
                    }
                }
            }
        )
        viewModel.getResult.observe(
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
                                setFailedToast()

                        }
                    }
                }
            }
        )
       viewModel.countryList.observe(viewLifecycleOwner, Observer {
                event ->
            event.getContentIfNotHandled().let { it ->
                hideLoading()
                if(!it.isNullOrEmpty()){
                      showData(it)
                }
            }
        })
        viewModel.title.observe(viewLifecycleOwner, Observer {
            if(!it.isNullOrBlank()){
                setTitle(it)
            }
        })
        binding.swipeRefreshLayout.setOnRefreshListener(SwipeRefreshLayout.OnRefreshListener {
                viewModel.pullRefresh.value = true
                loadData()
        })
    }
    /**Set the title*/
    private fun setTitle(title:String){
        (activity as AppCompatActivity).supportActionBar?.title = title
    }


    private fun loadData(){
        showLoading()
        viewModel.getCountryData()
    }
    /** hide the progress bar*/
    private fun hideLoading() {
        binding.errorTextData.visibility =View.GONE
        if(viewModel.pullRefresh.value!=true){
            binding.progressBar.visibility = View.GONE
            activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        }else{
              binding.swipeRefreshLayout.isRefreshing = false
        }
    }
    /** shows the progress bar*/
    private fun showLoading() {
        if(viewModel.pullRefresh.value!=true){
            binding.progressBar.visibility = View.VISIBLE
            activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            activity?.window?.setFlags(
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        }else{
            binding.swipeRefreshLayout.isRefreshing = false
        }
    }
    /** Method used to set the data in adapter */
    private fun showData(countryList:List<Row>){
        val sortList = mutableListOf<Row>()
        countryList.let {
            for (item in it ) {
                if(!item.title.isNullOrBlank()&&!item.description.isNullOrBlank()&&!item.description.isNullOrBlank()){
                    sortList.add(item)
                }
            }

            adapter.setItems(sortList)
        }
    }
    /** checks connectivity and request data call.. */
    private fun checkInternetConnectivity(){
        if(Utils.isConnectedToNetwork(requireActivity())){
            viewModel.downloadCountryData()
        }else{
            hideLoading()
            setNoInternetAvailable()
        }
    }
    private fun setNoInternetAvailable(){
        binding.errorTextData.visibility =View.VISIBLE
        binding.errorTextData.text = getString(R.string.no_internet_toast)
    }
    private fun setFailedToast(){
         if(viewModel.pullRefresh.value!=true){
             binding.errorTextData.visibility =View.VISIBLE
             binding.errorTextData.text = getString(R.string.failure_toast)
         }
    }
}
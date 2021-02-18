package com.lediya.infosys.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.lediya.infosys.R
import com.lediya.infosys.databinding.ListRowItemBinding
import com.lediya.infosys.model.Row

class CountryAdapter constructor(): RecyclerView.Adapter<CountryAdapter.ViewHolder>() {

    private lateinit var binding: ListRowItemBinding
    private  var countryList = mutableListOf<Row>()
    /**
     * Create the view holder for the country list data */
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        binding = createBinding(parent)
        return ViewHolder(binding)
    }
    /**
     * Create binding  for the country list data */
    private fun createBinding(parent: ViewGroup): ListRowItemBinding {
        return DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.list_row_item,
            parent,
            false
        )
    }

    /**
     * initialise adapter with data
     */
    fun setItems(itemList: List<Row>) {
        this.countryList.clear()
        this.countryList.addAll(itemList)
        notifyDataSetChanged()
    }
    /**
     * To get item count size*/
    override fun getItemCount(): Int {
        return countryList.size
    }
    /**
     * Set the data using the bind view holder
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = countryList[position]
        if(!item.title.isNullOrBlank()&&!item.description.isNullOrBlank()&&!item.description.isNullOrBlank()){
            holder.binding.titleText.text = item.title
            holder.binding.descriptionText.text = item.description
            if(!item.imageHref.isNullOrBlank()){
                    Glide.with(holder.binding.image.context)
                        .load(item.imageHref)
                        .apply(
                            RequestOptions().transform( CenterCrop(), RoundedCorners(25))
                            .placeholder(R.drawable.ic_loading_anim)
                            .error(R.drawable.ic_broken_image)
                                .diskCacheStrategy(DiskCacheStrategy.ALL))

                        .into(holder.binding.image)
            }else{
                Glide.with(holder.binding.image.context).load(R.drawable.ic_broken_image)
                    .apply(RequestOptions().placeholder(R.drawable.ic_loading_anim))
                    .into(holder.binding.image)
            }
        }
    }

    class ViewHolder(val binding: ListRowItemBinding) :
        RecyclerView.ViewHolder(binding.root)
}
package com.tanzeelmarwat.expandabledynamicfaqs

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView


import kotlinx.android.synthetic.main.faqs_item.view.*

class MyItemRecyclerViewAdapter(
    private val mValues: List<FAQs>
) : RecyclerView.Adapter<MyItemRecyclerViewAdapter.ViewHolder>() {

    private val mOnClickListener: View.OnClickListener

    init {
        mOnClickListener = View.OnClickListener { v ->
            val item = v.tag as FAQs
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.faqs_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mValues[position]
        holder.ecvFaqs.setTitle(-1, item.question!!)
        holder.ecvFaqs.findViewById<TextView>(R.id.tv_answer).text = item.answer!!
        holder.ecvFaqs.findViewById<TextView>(R.id.tv_answer).post(Runnable {
            holder.ecvFaqs.findViewById<TextView>(R.id.tv_answer).minLines = holder.ecvFaqs.findViewById<TextView>(R.id.tv_answer).lineCount
        })

        with(holder.mView) {
            tag = item
            setOnClickListener(mOnClickListener)
        }
    }

    override fun getItemCount(): Int = mValues.size

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val ecvFaqs: ExpandableCardView = mView.ecv_faqs
    }
}

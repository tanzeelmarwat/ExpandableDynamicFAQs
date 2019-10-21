package com.tanzeelmarwat.expandabledynamicfaqs

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

class ItemFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_item_list, container, false)

        // Populate Data
        var faqs: ArrayList<FAQs> = ArrayList()
        faqs.add(FAQs("Question No. 1", "This is a one line answer."))
        faqs.add(FAQs("Question No. 2", "This is first line.\n" +
                "This is second line."))
        faqs.add(FAQs("Question No. 3", "This is first line.\n" +
                "This is second line.\n" +
                "This is third line."))
        faqs.add(FAQs("Question No. 4", "This is first line.\n" +
                "This is second line.\n" +
                "This is third line.\n" +
                "This is fourth line."))
        faqs.add(FAQs("Question No. 5", "This is first line.\n" +
                "This is second line.\n" +
                "This is third line.\n" +
                "This is fourth line.\n" +
                "This is fifth line."))
        // Set the adapter
        if (view is RecyclerView) {
            with(view) {
                adapter = MyItemRecyclerViewAdapter(faqs)
            }
        }
        return view
    }
}

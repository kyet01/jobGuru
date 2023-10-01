package com.example.jobguru.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.example.jobguru.R
import com.example.jobguru.model.EmployerModel
import com.example.jobguru.model.JobModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ApplJobAdapter(private var jobList: ArrayList<JobModel>) :
    RecyclerView.Adapter<ApplJobAdapter.ViewHolder>() {

    private lateinit var jListener: onItemClickListener

    private val _jobCompanyName = MutableLiveData<String>()
    val jobCompanyName: LiveData<String>
        get() = _jobCompanyName

    interface onItemClickListener{
        fun onItemClick(position: Int)
    }

    // Update setData function to accept filtered list
    fun setData(jobList: List<JobModel>) {
        this.jobList.clear() // Clear the existing data
        this.jobList.addAll(jobList) // Add all elements from the provided list
        notifyDataSetChanged()
    }

    fun setOnItemClickListener(clickListener: onItemClickListener){
        jListener = clickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.appl_job_list_item, parent, false)
        return ViewHolder(itemView, jListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentJob = jobList[position]
        holder.tvJobTitle.text = currentJob.jobTitle
        holder.tvJobWorkState.text = currentJob.jobWorkState
        holder.tvJobMinSalary.text = String.format("%.2f", currentJob.jobMinSalary)
        holder.tvJobMaxSalary.text = String.format("%.2f", currentJob.jobMaxSalary)
        getEmpData(holder, currentJob.empId)
    }

    override fun getItemCount(): Int {
        return jobList.size
    }

    class ViewHolder(itemView: View, clickListener: onItemClickListener) : RecyclerView.ViewHolder(itemView) {

        val tvJobTitle : TextView = itemView.findViewById(R.id.tvJobTitle)
        val tvJobWorkState : TextView = itemView.findViewById(R.id.tvJobWorkState)
        val tvJobMinSalary : TextView = itemView.findViewById(R.id.tvJobMinSalary)
        val tvJobMaxSalary : TextView = itemView.findViewById(R.id.tvJobMaxSalary)
        val tvJobCompanyName : TextView = itemView.findViewById(R.id.tvJobCompanyName)

        init {
            itemView.setOnClickListener {
                clickListener.onItemClick(adapterPosition)
            }
        }
    }

    private fun getEmpData(holder: ViewHolder, empId: String?){
        val dbRef = FirebaseDatabase.getInstance().getReference("Employers")
        val query = dbRef.orderByChild("empId").equalTo(empId)

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val empData = snapshot.children.first().getValue(EmployerModel::class.java)

                    holder.tvJobCompanyName.text = empData?.empName
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle the error if needed
            }
        })
    }
}
package com.espparki.molisnail.admin.usuarios;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.espparki.molisnail.R;

import java.util.List;

public class AppointmentAdapter extends RecyclerView.Adapter<AppointmentAdapter.AppointmentViewHolder> {

    private final List<Appointment> futureAppointments;
    private final List<Appointment> pastAppointments;
    private final OnDeleteAppointmentListener deleteListener;

    public interface OnDeleteAppointmentListener {
        void onDelete(Appointment appointment);
    }

    public AppointmentAdapter(List<Appointment> futureAppointments, List<Appointment> pastAppointments, OnDeleteAppointmentListener deleteListener) {
        this.futureAppointments = futureAppointments;
        this.pastAppointments = pastAppointments;
        this.deleteListener = deleteListener;
    }

    @Override
    public int getItemViewType(int position) {
        return position < futureAppointments.size() ? 0 : 1;
    }

    @NonNull
    @Override
    public AppointmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.admin_item_appointment, parent, false);
        return new AppointmentViewHolder(view, viewType == 0);
    }

    @Override
    public void onBindViewHolder(@NonNull AppointmentViewHolder holder, int position) {
        Appointment appointment = position < futureAppointments.size()
                ? futureAppointments.get(position)
                : pastAppointments.get(position - futureAppointments.size());
        holder.bind(appointment);
    }

    @Override
    public int getItemCount() {
        return futureAppointments.size() + pastAppointments.size();
    }

    class AppointmentViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvDate, tvTime, tvService;
        private final Button btnDelete;

        public AppointmentViewHolder(@NonNull View itemView, boolean isFutureAppointment) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tvAppointmentDate);
            tvTime = itemView.findViewById(R.id.tvAppointmentTime);
            tvService = itemView.findViewById(R.id.tvAppointmentService);
            btnDelete = itemView.findViewById(R.id.btnDeleteAppointment);

            if (isFutureAppointment) {
                btnDelete.setVisibility(View.VISIBLE);
                btnDelete.setOnClickListener(v -> deleteListener.onDelete(futureAppointments.get(getAdapterPosition())));
            } else {
                btnDelete.setVisibility(View.GONE);
            }
        }

        public void bind(Appointment appointment) {
            tvDate.setText(appointment.getFecha());
            tvTime.setText(appointment.getHora());
            tvService.setText(appointment.getServicio());
        }
    }
}

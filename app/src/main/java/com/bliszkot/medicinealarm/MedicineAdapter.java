package com.bliszkot.medicinealarm;

import static androidx.recyclerview.widget.RecyclerView.Adapter;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

public class MedicineAdapter extends Adapter<MedicineAdapter.ViewHolder> {

    MedicineData[] medicineData;
    Context context;

    public MedicineAdapter(MedicineData[] medicineData, Context context) {
        this.medicineData = medicineData;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.card_view_list, parent, false);
        return new ViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final MedicineData medicineDataList = medicineData[position];
        holder.medicineText.setText(medicineDataList.getMedicineName());
        holder.dateText.setText(medicineDataList.getDate());
        holder.timeText.setText(medicineDataList.getTime());

        holder.itemView.setOnLongClickListener(v -> {
            String reqCodeString = medicineDataList.getRequestCode();
            if (reqCodeString == null || reqCodeString.isEmpty()) {
                Toast.makeText(context, "Please set an alarm.", Toast.LENGTH_SHORT).show();
            } else {
                DbHelper dbHelper = new DbHelper(context);
                SQLiteDatabase db = dbHelper.getWritableDatabase();

                String selection = DbHelper.FeedEntry.COLUMN_NAME_MEDICINE_NAME + " LIKE ?";
                String[] selectionArgs = { ""+medicineDataList.getMedicineName() };
                db.delete(DbHelper.FeedEntry.TABLE_NAME, selection, selectionArgs);
                Toast.makeText(context, "Delete success.", Toast.LENGTH_SHORT).show();

                AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                Intent alarmReceiverIntent= new Intent(context, AlarmReceiver.class);

                int reqCode = Integer.parseInt(reqCodeString);
                PendingIntent alarmIntent = PendingIntent.getBroadcast(context, reqCode, alarmReceiverIntent, PendingIntent.FLAG_IMMUTABLE);
                alarmManager.cancel(alarmIntent);
                alarmReceiverIntent.putExtra("extra", "stop");

                context.sendBroadcast(alarmReceiverIntent);
            }
            return false;
        });
    }

    @Override
    public int getItemCount() {
        return medicineData.length;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView medicineText;
        TextView dateText;
        TextView timeText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            medicineText = itemView.findViewById(R.id.medcineNameMain);
            dateText = itemView.findViewById(R.id.dateTextMain);
            timeText = itemView.findViewById(R.id.timeTextMain);
        }
    }
}

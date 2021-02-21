package prj_2.stu_1505005.ViewAdapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.HashMap;
import prj_2.stu_1505005.R;

/**
 * StepsRecyclerViewAdapter is adapter for Route steps list RecyclerView in NavigationActivity's
 * DirectionFragment.
 */
public class StepsRecyclerViewAdapter extends RecyclerView.Adapter<StepsRecyclerViewAdapter.ViewHolder>{
    LayoutInflater layoutInflater;
    Context context;
    HashMap<Integer, ArrayList<String>> directionsInformation;

    /**
     *
     * @param context Context
     * @param directionsInformation HashMap<Integer, ArrayList<String>>
     */
    public StepsRecyclerViewAdapter(Context context, HashMap<Integer, ArrayList<String>> directionsInformation) {
        this.context = context;
        this.directionsInformation = directionsInformation;
    }

    /**
     * Called when RecyclerView needs a new of the given type to represent an item.
     * @param parent ViewGroup
     * @param viewType int
     * @return vh ViewHolder
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        layoutInflater = LayoutInflater.from(context);
        View v = layoutInflater.inflate(R.layout.steps_list_item, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    /**
     * if directionsInformation is null return empty item
     * Else fill item with directionsInformation's ArrayList
     * @param holder ViewHolder NonNull
     * @param position int
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if(directionsInformation.size() == 0){
            holder.current_step_title.setText(context.getString(R.string.current_step_empty_warning));
            holder.current_step_distance_text.setText(context.getString(R.string.current_step_empty_warning));
            holder.current_step_duration_text.setText(context.getString(R.string.current_step_empty_warning));
            holder.current_step_starting_text.setText(context.getString(R.string.current_step_empty_warning));
            holder.current_step_destination_text.setText(context.getString(R.string.current_step_empty_warning));
            holder.current_step_maneuver_text.setText(context.getString(R.string.current_step_empty_warning));
        } else {
            ArrayList<String> current_step_info = directionsInformation.get(position);
            String step_title = "Step - " + (position + 1);
            holder.current_step_title.setText(step_title);
            holder.current_step_distance_text.setText(current_step_info.get(0));
            holder.current_step_duration_text.setText(current_step_info.get(1));
            holder.current_step_starting_text.setText(current_step_info.get(2));
            holder.current_step_destination_text.setText(current_step_info.get(3));
            holder.current_step_maneuver_text.setText(current_step_info.get(4));
        }
        //card view
        holder.card.setTag(holder);
    }

    /**
     * Items represent directionsInformation's ArrayList
     * @return directionsInformation HashMap<Integer, ArrayList<String>> size
     */
    @Override
    public int getItemCount() {
        return (directionsInformation == null) ? 0 : directionsInformation.size();
    }

    /**
     * Item UI elements initiation with ViewHolder
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView current_step_title;
        TextView current_step_distance_text;
        TextView current_step_duration_text;
        TextView current_step_starting_text;
        TextView current_step_destination_text;
        TextView current_step_maneuver_text;
        LinearLayout card;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            current_step_title = itemView.findViewById(R.id.current_step_title);
            current_step_distance_text = itemView.findViewById(R.id.current_step_distance_text);
            current_step_duration_text = itemView.findViewById(R.id.current_step_duration_text);
            current_step_starting_text = itemView.findViewById(R.id.current_step_starting_text);
            current_step_destination_text = itemView.findViewById(R.id.current_step_destination_text);
            current_step_maneuver_text = itemView.findViewById(R.id.current_step_maneuver_text);
            card = itemView.findViewById(R.id.steps_list_item);
        }
    }
}

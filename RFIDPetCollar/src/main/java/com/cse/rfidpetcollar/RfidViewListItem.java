package com.cse.rfidpetcollar;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.cse.rfidpetcollar.adapter.RfidViewListAdapter;
import com.cse.rfidpetcollar.model.RfidViewItem;
import com.cse.rfidpetcollar.sql.DatabaseHelper;

import java.util.List;

/**
 * Created by Joseph on 2/5/14.
 */
public class RfidViewListItem implements RfidViewItem {
    private final String         name;
    private final String         rfidId;
    private final boolean        allowed;
    //private final LayoutInflater inflater;
    private boolean isChecked = false;

    public RfidViewListItem(String text1, String rfidId, boolean allowed) {
        this.name = text1;
        this.rfidId = rfidId;
        this.allowed = allowed;
        //this.inflater = inflater;
    }

    @Override
    public int getViewType() {
        return RfidViewListAdapter.RowType.LIST_ITEM.ordinal();
    }

    @Override
    public View getView(LayoutInflater inflater, final View convertView) {
        final View view;
        if (convertView == null) {
            view = (View) inflater.inflate(R.layout.list_item, null);
            // Do some initialization
        } else {
            view = convertView;
        }

        final Switch toggle = (Switch) view.findViewById(R.id.toggle_button);
        toggle.setChecked(allowed);
        toggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (view != null) {
                    Context ctx = view.getContext();

                    if (ctx != null) {
                        DatabaseHelper dbHelper = new DatabaseHelper(ctx);

                        String rfidId = "";

                        // update pet in DB
                        List<Pet> pets = dbHelper.getAllPets();
                        for (Pet pet : pets) {
                            if (pet.getName().equals(name)) {
                                pet.setAllowed(toggle.isChecked());
                                dbHelper.updatePet(pet);
                                rfidId = pet.getRfidId();
                            }
                        }

                        Intent intent = new Intent(ctx, TogglePermissionActivity.class);
                        intent.putExtra("RFID_TAG", rfidId);
                        intent.putExtra("IS_ALLOWED", toggle.isChecked());
                        intent.putExtra("BLE_DEVICE", MainActivity.mDevice);
                        ctx.startActivity(intent);
                    }
                }
            }
        });

        TextView text1 = (TextView) view.findViewById(R.id.list_content);

        text1.setText(name);

        return view;
    }

    public String getRfidId(){
        return rfidId;
    }

    public boolean isChecked(){
        return this.isChecked;
    }

    public void setIsChecked(boolean checked){
        this.isChecked = checked;
    }

}
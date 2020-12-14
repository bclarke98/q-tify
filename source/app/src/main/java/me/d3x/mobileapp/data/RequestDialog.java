package me.d3x.mobileapp.data;

import android.app.Dialog;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import java.util.function.Consumer;

import me.d3x.mobileapp.util.Qtify;

public class RequestDialog extends DialogFragment {

    private String title;

    private String posBtnText;
    private String negBtnText;
    private Consumer<String> onConfirm;

    public RequestDialog(String title, String posBtnText, String negBtnText){
        this.title = title;
        this.posBtnText = posBtnText;
        this.negBtnText = negBtnText;
        this.onConfirm = (s)->{};
    }

    public void setOnConfirm(Consumer<String> onC){
        this.onConfirm = onC;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(Qtify.getInstance().getActivity());
        builder.setMessage(this.title);
        if(this.posBtnText != null)
            builder.setPositiveButton(this.posBtnText, (dialog, id) -> { onConfirm.accept(""); });

        if(this.negBtnText != null)
            builder.setNegativeButton(this.negBtnText, (dialog, id) -> {});

        return builder.create();
    }
}

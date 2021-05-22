package com.codelab.live;

import android.app.Activity;
import android.app.AlertDialog;


public class LoadingDialog {

    private Activity activity;
    private AlertDialog alertDialog;

    public LoadingDialog(Activity activity) {
        this.activity = activity;
    }

    public void startLoading() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity, R.style.loadingDialogStyle);
        builder.setView(R.layout.dialog_layout);
        builder.setCancelable(false);
        alertDialog = builder.create();
        //binding.rotateLoading.start();
        alertDialog.show();
    }

    public void stopLoading() {
        alertDialog.dismiss();
    }
}

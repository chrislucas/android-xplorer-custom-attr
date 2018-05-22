package xplore.dialogfragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.support.v4.app.DialogFragment;
import android.view.View;

/**
 * Futuramente implementar um metodo callback para
 * {@link Dialog#setOnDismissListener(DialogInterface.OnDismissListener)}
 * {@link Dialog#setOnShowListener(DialogInterface.OnShowListener)} (DialogInterface.OnDismissListener)}
 * */

public interface DialogFragmentOnclickListener {
    void accessViewDialogFragment(View rootView);
    void accessAlertDialogBuilder(AlertDialog.Builder builder);
    void accessAlertDialogBuilder(Dialog dialog);
    void changeDimensionDialog(Dialog view);
    void changeDimensionViewDialog();
    DialogInterface.OnDismissListener actionOnDismissListener();
}

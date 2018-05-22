package xplore.dialogfragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import java.util.Locale;

public class MainActivity extends AppCompatActivity implements DialogFragmentOnclickListener {

    private CustomDialogFragment dialogFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dialogFragment = CustomDialogFragment.newInstance(
                this
                , R.layout.layout_dialog_fragment_fullscreen
                , R.style.FullScreenDialog
                , true
            );
    }

    public void openDialogFragment(View view) {
        if (!dialogFragment.isAdded())
            dialogFragment.show(getSupportFragmentManager(), dialogFragment.getClass().getSimpleName());
    }


    @Override
    public void accessViewDialogFragment(View rootView) {
        ZoomableImageView zoomableImageView = rootView.findViewById(R.id.image_info_dialog);
        if (zoomableImageView != null) {
            Log.i("DIMENSION_ZOOM_IMG_VIEW"
                    , String.format("Dimensao: %dx%d"
                            , zoomableImageView.getMeasuredWidth(), zoomableImageView.getMeasuredHeight()));
        }
        else {
            Log.e("EXCP_ZOOMABLE_IMG_VIEW", "Imagem não foi encontrada no layout");
        }
    }

    @Override
    public void accessAlertDialogBuilder(AlertDialog.Builder builder) {
        /**
         * Aqui podemos definir se o nosso fragment vai ter botao
         * de 'Resposta positiva/negativa', adicionar uma implementacao
         * de {@link DialogInterface.OnClickListener} entre outras coisas
         **/
    }

    @Override
    public void accessAlertDialogBuilder(Dialog dialog) {
        /**
         * como esse dialog vai servir para mostrar uma imagem redimensionavel
         * nao precisamos dos botoes
         * */
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button pos = ((AlertDialog) dialog).getButton(DialogInterface.BUTTON_POSITIVE);
                pos.setEnabled(false);
                pos.setVisibility(View.GONE);
                Button neg = ((AlertDialog) dialog).getButton(DialogInterface.BUTTON_NEGATIVE);
                neg.setEnabled(false);
                neg.setVisibility(View.GONE);
            }
        });
    }


    /**
     * Mudar a dimensao do dialog
     * */
    @Override
    public void changeDimensionDialog(Dialog dialog) {
        //s(dialog);
    }

    private void s(Dialog dialog) {
        if (dialog.getWindow() != null) {
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            layoutParams.copyFrom(dialog.getWindow().getAttributes());
            layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
            layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
            layoutParams.gravity = Gravity.CENTER;
            dialog.getWindow().setAttributes(layoutParams);
        }
    }


    /**
     * Mudar a dimensao do layout.
     * */
    @Override
    public void changeDimensionViewDialog() {
        /**
         * O codigo abaixo eh um exemplo. O melhor é implmentar
         * ou {@link DialogFragmentOnclickListener#changeDimensionDialog(Dialog)}
         * */
        View view = dialogFragment.getViewDialogFragment();
        if (view != null) {
            DisplayMetrics dm   = getResources().getDisplayMetrics();
            double w = dm.widthPixels, h = dm.heightPixels;
            double percentWidth     = 1;
            double percentHeight    = 1;
            view.setMinimumWidth((int)(w*percentWidth));
            view.setMinimumHeight((int)(h*percentHeight));
            Log.i("DIMENSION", String.format(Locale.getDefault()
                    , "Dimensao da tela %f %f\nDimensao da View %f, %f"
                    , w
                    , h
                    , w*percentWidth
                    , h*percentHeight
                    )
            );
        }
    }


    @Override
    public DialogInterface.OnDismissListener actionOnDismissListener() {
        return new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {}
        };
    }
}

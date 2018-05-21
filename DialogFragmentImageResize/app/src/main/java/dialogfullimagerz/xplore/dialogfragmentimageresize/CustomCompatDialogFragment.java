package dialogfullimagerz.xplore.dialogfragmentimageresize;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

/**
 * Created by r028367 on 14/08/2017.
 */

public class CustomCompatDialogFragment extends DialogFragment {

    private static final String BUNDLE_ID_LAYOUT            = "BUNDLE_ID_LAYOUT";
    private static final String BUNDLE_RES_STYLE            = "BUNDLE_RES_STYLE";
    private static final String BUNDLE_TITLE_DIALOG         = "BUNDLE_TITLE_DIALOG";
    private static final String BUNDLE_TEXT_POSITIVE_BUTTON = "BUNDLE_TEXT_POSITIVE_BUTTON";
    private static final String BUNDLE_TEXT_NEGATIVE_BUTTON = "BUNDLE_TEXT_NEGATIVE_BUTTON";



    public interface Callback {
        // assinatura para implementacao de um listener ao clicar nos botões do DialogFragment
        DialogInterface.OnClickListener onClickListener(DialogFragment dialog);
        // notificar a classe que instanciou essa DialogFragment
        void callback(DialogFragment dialog, View view);
    }

    private int mIdLayoutDialog;
    private String mTitleDialog, mTextPositiveButton = null, mTextNegativeButton = null;
    private Drawable mIcon;
    private double mPercentageWidthWindow = 0.0d, mPercentageHeightWindow = 0.0d;
    private Callback callback;

    @StyleRes
    private int style;

    private Dialog dialog;

    public CustomCompatDialogFragment() {
        super();
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public static CustomCompatDialogFragment newInstance(@LayoutRes  int idLayout, String titleDialog) {
        CustomCompatDialogFragment customCompatDialogFragment = new CustomCompatDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(BUNDLE_ID_LAYOUT, idLayout);
        bundle.putString(BUNDLE_TITLE_DIALOG, titleDialog);
        customCompatDialogFragment.setArguments(bundle);
        return customCompatDialogFragment;
    }


    public static CustomCompatDialogFragment newInstance(@LayoutRes int idLayout, String titleDialog, String textPositiveButton, String textNegativeButton) {
        CustomCompatDialogFragment customCompatDialogFragment = new CustomCompatDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(BUNDLE_ID_LAYOUT, idLayout);
        bundle.putString(BUNDLE_TITLE_DIALOG, titleDialog);
        bundle.putString(BUNDLE_TEXT_POSITIVE_BUTTON, textPositiveButton);
        bundle.putString(BUNDLE_TEXT_NEGATIVE_BUTTON, textNegativeButton);
        customCompatDialogFragment.setArguments(bundle);
        return customCompatDialogFragment;
    }

    public static CustomCompatDialogFragment newInstance(@LayoutRes int idLayout, int styleRes, String titleDialog) {
        CustomCompatDialogFragment customCompatDialogFragment = new CustomCompatDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(BUNDLE_ID_LAYOUT, idLayout);
        bundle.putString(BUNDLE_TITLE_DIALOG, titleDialog);
        bundle.putInt(BUNDLE_RES_STYLE, styleRes);
        customCompatDialogFragment.setArguments(bundle);
        return customCompatDialogFragment;
    }

    public void setmIcon(Drawable mIcon) {
        this.mIcon = mIcon;
    }

    public void setmPercentageWidthWindow(double mPercentageWidthWindow) {
        this.mPercentageWidthWindow = (mPercentageWidthWindow > 1.0 || mPercentageWidthWindow < 0.1) ? 0.0 : mPercentageWidthWindow;
    }

    public void setmPercentageHeightWindow(double mPercentageHeightWindow) {
        this.mPercentageHeightWindow = (mPercentageHeightWindow > 1.0 || mPercentageHeightWindow < 0.1) ? 0.0 : mPercentageHeightWindow;
    }

    @Override
    public Dialog getDialog() {
        return this.dialog;
    }

    /**
     * segundo metodo executado depois do newInstance
     * */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof Callback && callback == null) {
            this.callback = (Callback) context;
        }
    }

    /**
     * 2
     * chamado depois do onAttach
     * */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null) {
            Bundle bundle = getArguments();
            this.mIdLayoutDialog = bundle.getInt(BUNDLE_ID_LAYOUT);
            this.mTitleDialog = bundle.getString(BUNDLE_TITLE_DIALOG);
            this.style = bundle.getInt(BUNDLE_RES_STYLE, 0);
            if( bundle.keySet().contains(BUNDLE_TEXT_NEGATIVE_BUTTON) ) {
                this.mTextNegativeButton = bundle.getString(BUNDLE_TEXT_NEGATIVE_BUTTON);
            }
            if( bundle.keySet().contains(BUNDLE_TEXT_POSITIVE_BUTTON) ) {
                this.mTextPositiveButton = bundle.getString(BUNDLE_TEXT_POSITIVE_BUTTON);
            }
        }
        setRetainInstance(true);
    }

    private View layoutInflateDialog;
    /**
     * Override to build your own custom Dialog container.  This is typically
     * used to show an AlertDialog instead of a generic Dialog; when doing so,
     * {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)} (LayoutInflater, ViewGroup, Bundle)} does not need
     * to be implemented since the AlertDialog takes care of its own content.
     * <p>
     * <p>This method will be called after {@link #onCreate(Bundle)} and
     * before {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}.  The
     * default implementation simply instantiates and returns a {@link Dialog}
     * class.
     * <p>
     * <p><em>Note: DialogFragment own the {@link Dialog#setOnCancelListener
     * Dialog.setOnCancelListener} and {@link Dialog#setOnDismissListener
     * Dialog.setOnDismissListener} callbacks.  You must not set them yourself.</em>
     * To find out about these events, override {@link #onCancel(DialogInterface)}
     * and {@link #onDismiss(DialogInterface)}.</p>
     *
     * @param savedInstanceState The last saved instance state of the Fragment,
     *                           or null if this is a freshly created Fragment.
     * @return Return a new Dialog instance to be displayed by the Fragment.
     *
     * Executa apos o onCreate
     *
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        if (getActivity() != null && getContext() != null) {
            FragmentActivity fragmentActivity = getActivity();
            AlertDialog.Builder builder = (style == 0) ? new AlertDialog.Builder(fragmentActivity) : new AlertDialog.Builder(fragmentActivity, style);

            DialogInterface.OnClickListener onClickListener = null;

            if(callback != null)
                onClickListener = callback.onClickListener(this);

            if(this.mIdLayoutDialog != 0) {
                LayoutInflater layoutInflater = fragmentActivity.getLayoutInflater();
                layoutInflateDialog = layoutInflater.inflate(this.mIdLayoutDialog, null);
                builder.setIcon(mIcon);
                builder.setView(layoutInflateDialog);

                if(this.mTitleDialog != null && ! this.mTitleDialog.equals("")) {
                    builder.setTitle(this.mTitleDialog);
                }

                builder.setCancelable(true);

                /**
                 *
                 * */
                if(onClickListener != null) {
                    /*
                    builder.setPositiveButton(mTextPositiveButton != null
                                    ? mTextPositiveButton : getContext().getString(R.string.yes)
                            , onClickListener
                    );

                    builder.setPositiveButton(mTextNegativeButton != null
                                    ? mTextNegativeButton : getContext().getString(R.string.no)
                            , onClickListener
                    );
                    */
                }
            }

            this.dialog = builder.create();
            if (onClickListener == null) {
                dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialog) {
                        Button b = ((AlertDialog) dialog ).getButton(AlertDialog.BUTTON_POSITIVE);
                        b.setEnabled(false);
                        b.setVisibility(View.GONE);
                        b = ((AlertDialog) dialog ).getButton(AlertDialog.BUTTON_NEGATIVE);
                        b.setEnabled(false);
                        b.setVisibility(View.GONE);
                    }
                });
            }

            if(mPercentageHeightWindow > 0d && mPercentageWidthWindow > 0d) {
                changeDimensionViewDialogUsingDisplayMetrics();
            }
            else {
                /**
                 * Recuperar as dimensoes da tela e aplicar um percentual
                 * dela ao layout do dialog que sera exibido
                 * */
                changeDimensionsDialog(dialog);
            }
        }


        return dialog;
    }

    /**
     * Executa apos o onCreateDialog
     **/

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return layoutInflateDialog;
    }

    /**
     * Executa apos o onCreateView
     **/
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    /**
     * Metodo executado apos onViewCreated
     * super.getView();
     * */
    @Nullable
    @Override
    public View getView() {
        if(this.callback != null) {
            this.callback.callback(this, layoutInflateDialog);
        }
        return layoutInflateDialog;
    }

    /**
     * Display the dialog, adding the fragment to the given FragmentManager.  This
     * is a convenience for explicitly creating a transaction, adding the
     * fragment to it with the given tag, and committing it.  This does
     * <em>not</em> add the transaction to the back stack.  When the fragment
     * is dismissed, a new transaction will be executed to remove it from
     * the activity.
     *
     * @param manager The FragmentManager this fragment will be added to.
     * @param tag     The tag for this fragment, as per
     *                {@link FragmentTransaction#add(Fragment, String) FragmentTransaction.add}.
     */
    @Override
    public void show(FragmentManager manager, String tag) {
        super.show(manager, tag);
    }

    public View getLayoutInflateDialog() {
        return layoutInflateDialog;
    }

    @Override
    public void onDestroyView() {
        Dialog dialog = getDialog();
        if (dialog != null && getRetainInstance())
            dialog.setOnDismissListener(null);
        super.onDestroyView();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }


    public void changeDimensionViewDialogUsingWindow() {
        // variavel que contera as dimensoes da tela
        Rect rect = new Rect();
        Window window = getActivity().getWindow();
        // recuperarndo as dimensoes da tela
        window.getDecorView().getWindowVisibleDisplayFrame(rect);
        double w = rect.width(), h = rect.height();
        this.layoutInflateDialog.setMinimumWidth((int)(w * mPercentageWidthWindow));
        this.layoutInflateDialog.setMinimumHeight((int)(h * mPercentageHeightWindow));
    }

    public void changeDimensionViewDialogUsingDisplayMetrics() {
        DisplayMetrics dm   = getResources().getDisplayMetrics();
        double w = dm.widthPixels, h = dm.heightPixels;
        this.layoutInflateDialog.setMinimumWidth((int)(w * mPercentageWidthWindow));
        this.layoutInflateDialog.setMinimumHeight((int)(h * mPercentageHeightWindow));
    }

    public void changeDimensionsDialog(Dialog dialog) {
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(dialog.getWindow().getAttributes());
        layoutParams.width  = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
        dialog.getWindow().setAttributes(layoutParams);
    }

    public void changeDimesionsDialogDefault(Dialog dialog) {
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(dialog.getWindow().getAttributes());
        DisplayMetrics dm   = getResources().getDisplayMetrics();
        layoutParams.width  = (int) (dm.widthPixels * mPercentageWidthWindow);
        layoutParams.height = (int) (dm.heightPixels * mPercentageWidthWindow);
        dialog.getWindow().setAttributes(layoutParams);
    }





    /**
     * Display the dialog, adding the fragment using an existing transaction
     * and then committing the transaction.
     *
     * @param transaction An existing transaction in which to add the fragment.
     * @param tag         The tag for this fragment, as per
     *                    {@link FragmentTransaction#add(Fragment, String) FragmentTransaction.add}.
     * @return Returns the identifier of the committed transaction, as per
     * {@link FragmentTransaction#commit() FragmentTransaction.commit()}.
     */
    @Override
    public int show(FragmentTransaction transaction, String tag) {
        return super.show(transaction, tag);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if(callback != null) {
            this.callback = null;
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            mIdLayoutDialog = savedInstanceState.getInt(BUNDLE_ID_LAYOUT);
            mTitleDialog = savedInstanceState.getString(BUNDLE_TITLE_DIALOG);
            style = savedInstanceState.getInt(BUNDLE_RES_STYLE);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(outState != null) {
            outState.putInt(BUNDLE_ID_LAYOUT, mIdLayoutDialog);
            outState.putString(BUNDLE_TITLE_DIALOG, mTitleDialog);
            outState.putInt(BUNDLE_RES_STYLE, style);
        }
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if(savedInstanceState != null) {
            mIdLayoutDialog = savedInstanceState.getInt(BUNDLE_ID_LAYOUT);
            mTitleDialog = savedInstanceState.getString(BUNDLE_TITLE_DIALOG);
            style = savedInstanceState.getInt(BUNDLE_RES_STYLE);
        }
    }

}

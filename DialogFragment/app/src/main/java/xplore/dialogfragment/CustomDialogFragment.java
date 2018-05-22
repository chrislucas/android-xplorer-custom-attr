package xplore.dialogfragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.lang.ref.SoftReference;

public class CustomDialogFragment extends DialogFragment {

    private static SoftReference<CustomDialogFragment> instance = null;

    private DialogFragmentOnclickListener dialogFragmentOnclickListener;

    @LayoutRes
    private int layoutResource;

    @StyleRes
    private int styleOfDialogFragment;

    private Drawable iconDialogFragment;

    private String title;

    private Dialog dialog;

    private View viewDialogFragment;

    private boolean cancelable;

    private static final String BUNDLE_TITLE = "BUNDLE_TITLE";
    private static final String BUNDLE_LAYOUT_RES = "BUNDLE_LAYOUT_RES";
    private static final String BUNDLE_STYLE_DIALOG_FRAGMENT = "BUNDLE_STYLE_DIALOG_FRAGMENT";
    private static final String BUNDLE_CANCELABLE = "BUNDLE_CANCELABLE";


    @Override
    public Dialog getDialog() {
        return this.dialog;
    }

    public CustomDialogFragment() {}

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (outState != null) {
            outState.putString(BUNDLE_TITLE, title);
            outState.putInt(BUNDLE_LAYOUT_RES, layoutResource);
            outState.putInt(BUNDLE_STYLE_DIALOG_FRAGMENT, styleOfDialogFragment);
            outState.putBoolean(BUNDLE_CANCELABLE, cancelable);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            title = savedInstanceState.getString(BUNDLE_TITLE);
            layoutResource = savedInstanceState.getInt(BUNDLE_LAYOUT_RES);
            styleOfDialogFragment = savedInstanceState.getInt(BUNDLE_STYLE_DIALOG_FRAGMENT);
            cancelable = savedInstanceState.getBoolean(BUNDLE_CANCELABLE);
        }
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null) {
            title = savedInstanceState.getString(BUNDLE_TITLE);
            layoutResource = savedInstanceState.getInt(BUNDLE_LAYOUT_RES);
            styleOfDialogFragment = savedInstanceState.getInt(BUNDLE_STYLE_DIALOG_FRAGMENT);
            cancelable = savedInstanceState.getBoolean(BUNDLE_CANCELABLE);
        }
    }

    public synchronized static CustomDialogFragment newInstance(DialogFragmentOnclickListener dialogFragmentOnclickListener
            , @LayoutRes int layoutResource, @StyleRes int style, boolean cancelable) {
        instance = new SoftReference<>(new CustomDialogFragment());
        instance.get().dialogFragmentOnclickListener = dialogFragmentOnclickListener;
        instance.get().layoutResource = layoutResource;
        instance.get().styleOfDialogFragment = style;
        instance.get().cancelable = cancelable;
        return instance.get();
    }


    public synchronized static CustomDialogFragment newInstance(DialogFragmentOnclickListener dialogFragmentOnclickListener
            , @LayoutRes int layoutResource, @StyleRes int style,  String titleDialogFragment, boolean cancelable) {
        instance = new SoftReference<>(new CustomDialogFragment());
        instance.get().dialogFragmentOnclickListener = dialogFragmentOnclickListener;
        instance.get().layoutResource = layoutResource;
        instance.get().title = titleDialogFragment;
        instance.get().styleOfDialogFragment = style;
        instance.get().cancelable = cancelable;
        return instance.get();
    }

    public synchronized static CustomDialogFragment newInstance(DialogFragmentOnclickListener dialogFragmentOnclickListener
            , @LayoutRes int layoutResource, @StyleRes int style, String titleDialogFragment, Drawable icon,  boolean cancelable) {
        CustomDialogFragment customDialogFragment =
                newInstance(dialogFragmentOnclickListener, layoutResource
                        , style, titleDialogFragment, cancelable);
        instance = new SoftReference<>(customDialogFragment);
        instance.get().iconDialogFragment = icon;
        return instance.get();
    }

    /**
     * Executado apos o metodo {@link CustomDialogFragment#newInstance}
     * */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof DialogFragmentOnclickListener && dialogFragmentOnclickListener == null) {
            dialogFragmentOnclickListener = (DialogFragmentOnclickListener) context;
        }
    }


    /**
     * Executado apos o metodo {@link CustomDialogFragment#onAttach(Context)}
     * */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /**
         * Quando definimos {@link DialogFragment#setRetainInstance(true)}
         * Ao rotacionar a tela, o Dialog Some Para corrigir isso
         * veja a discussao no link
         * https://issuetracker.google.com/issues/36929400
         * https://issuetracker.google.com/issues/36929400
         *
         *
         * A solucao eh sobreescrever o metodo {@link onDestroyView()}
         * e antes de chamar a versao do metodo na classe pai, definir
         * o listener de Dismiss do Dialog
         * */

        setRetainInstance(true);
    }

    /**
     * Executado apos o metodo {@link CustomDialogFragment#onCreate(Bundle)}
     * */
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            title = savedInstanceState.getString(BUNDLE_TITLE);
            layoutResource = savedInstanceState.getInt(BUNDLE_LAYOUT_RES);
            styleOfDialogFragment = savedInstanceState.getInt(BUNDLE_STYLE_DIALOG_FRAGMENT);
            cancelable = savedInstanceState.getBoolean(BUNDLE_CANCELABLE);
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), styleOfDialogFragment);
        if (iconDialogFragment != null) {
            builder.setIcon(iconDialogFragment);
        }
        viewDialogFragment = LayoutInflater.from(getContext())
                .inflate(layoutResource, null);
        builder.setView(viewDialogFragment);
        if (title != null && ! title.isEmpty())
            builder.setTitle(title);

        builder.setCancelable(cancelable);
        this.dialog = builder.create();

        this.dialogFragmentOnclickListener.accessAlertDialogBuilder(builder);
        this.dialogFragmentOnclickListener.changeDimensionDialog(dialog);
        this.dialogFragmentOnclickListener.accessDialog(dialog);
        this.dialogFragmentOnclickListener.changeDimensionViewDialog();
        return this.dialog;
    }


    /**
     * Executado apos o metodo {@link CustomDialogFragment#onCreateDialog(Bundle)} (Context)}
     * */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return viewDialogFragment;
    }

    /**
     * Executado apos o metodo {@link CustomDialogFragment#onCreateView(LayoutInflater, ViewGroup, Bundle)}}
     * */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    /**
     * Executado apos o metodo {@link CustomDialogFragment#onViewCreated(View, Bundle)}
     * */
    @Nullable
    @Override
    public View getView() {
        if (this.dialogFragmentOnclickListener != null) {
            this.dialogFragmentOnclickListener.accessViewDialogFragment(viewDialogFragment);
        }
        return viewDialogFragment;
    }

    @Override
    public int show(FragmentTransaction transaction, String tag) {
        return super.show(transaction, tag);
    }

    @Override
    public void show(FragmentManager manager, String tag) {
        super.show(manager, tag);
    }

    @Override
    public void showNow(FragmentManager manager, String tag) {
        super.showNow(manager, tag);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
    }

    public View getViewDialogFragment() {
        return viewDialogFragment;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        Dialog dialog = getDialog();
        if (dialog != null && getRetainInstance())
            dialog.setOnDismissListener(dialogFragmentOnclickListener.actionOnDismissListener());
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


}

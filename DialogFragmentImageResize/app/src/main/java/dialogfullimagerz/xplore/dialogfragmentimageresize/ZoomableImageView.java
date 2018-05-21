package dialogfullimagerz.xplore.dialogfragmentimageresize;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

/**
 * Created by r028367 on 07/08/2017.
 */

public class ZoomableImageView extends AppCompatImageView {
    /**
     * Para realizar as operacoes de translacao e escala na imagem, vamos usar uma matriz
     * de concatenacao
     * */
    private Matrix matrix = new Matrix();

    private static final int NONE   = 0;
    private static final int DRAG   = 1;
    private static final int ZOOM   = 2;
    private static final int CLICK  = 3;
    private int mode = NONE;

    private PointF lastPositionTaped  = new PointF();       // objeto para armazenar o ponto final do evento de toque na tela
    private PointF startPositionTaped = new PointF();       // objeto para armazenar o ponto inicial do evento de toque na tela
    private float startScale;                               // proporcao entre a largura e a altura original da imagem
    private float minOffsetTranslation = 1f;
    private float maxOffsetTranslation = 2.5f;
    private float minScaleZoom = 1f;
    private float maxScaleZoom = 2.5f;
    private float[] matrixValues;
    private float diffRawScalingWidth, diffRawScalingHeight;
    private float saveFactorTranslation = 1f    // escala para executar a operacao de translacao
            , saveFactorZoom = 1f;               // escala para executar o Zoom in
    private float
              differenceWidth                   // distancia entre a imagem apos aplicar a operacao Scale e a largura da tela
            , differenceHeight                  // distancia entre a imagem apos aplicar a operacao Scale e a altura da tela
            , imageViewWidthScaling             // Largura da imagem apos aplicar a operacao de Scale sobre a largura intrinseca da image
            , imageViewHeightScaling            // Altura da imagem apos aplicar a operacao de Scale sobre a altura intrinseca da image
            , mWidthImageView                   // Largura (raw/bruta) original da ImageView pega atraves do metodo getMeasuredWidth()
            , mHeightImageView;                 // Altura (raw/bruta) original da ImageView pega atraves do metodo getMeasuredHeight()


    private boolean applyScaleDoubleTap = false;
    //
    private ScaleGestureDetector mScaleDetector;
    // Especializacao da classe de Deteccao de gesto(toques) que pode ser implementado numa especializacao de View
    private MyGestureDetector gestureDetector;
    private Context context;

    public ZoomableImageView(Context context) {
        super(context);
        init(context);
    }

    public ZoomableImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ZoomableImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        implTouch(event);
        return true;
    }

    // nao preciso implementar OnTouchListener
    // setOnTouchListener(getOnTouchListener());
    private OnTouchListener getOnTouchListener() {
        return new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                implTouch(event);
                return true;
            }
        };
    }

    private void init(Context context) {
        super.setClickable(true);
        this.context = context;
        this.mScaleDetector = new ScaleGestureDetector(context, new MyScaleListener());
        this.matrix.setTranslate(1f, 1f);
        this.matrixValues = new float[9];
        setImageMatrix(matrix);
        /**
         * Log.i("MATRIX_INIT", matrix.toString());
         * Definindo o modo de operacao de scale sera feito na ImageView
         * */
        setScaleType(ScaleType.MATRIX);
        GestureDetector.SimpleOnGestureListener simpleOnGestureListener = new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDoubleTap(MotionEvent e) {
                setScaleType(ScaleType.MATRIX);
                float width = getWidth(), height = getHeight();
                Log.i("DOUBLE_TAP", String.format("wh(%f %f)", width, height));
                if(!applyScaleDoubleTap) {
                    //matrix.postScale(0.4f, 0.4f, width/2, height/2);
                    //setImageMatrix(matrix);
                    //invalidate();
                    applyScaleDoubleTap = true;
                }

                return true;
            }

            @Override
            public boolean onDoubleTapEvent(MotionEvent e) {
                //Log.i("ON_DOUBLE_TAP_EVENT", "DOUBLE_TAP");
                return true;
            }

            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                //Log.i("ON_SINGLE_TAP_CONF", "SINGLE_TAP");
                return true; //super.onSingleTapConfirmed(e);
            }

            @Override
            public boolean onContextClick(MotionEvent e) {
                return true; //super.onContextClick(e);
            }
        };
        this.gestureDetector = new MyGestureDetector(context, simpleOnGestureListener);
    }

    private void implTouch(MotionEvent event) {
        mScaleDetector.onTouchEvent(event);
        matrix.getValues(matrixValues);
        /**
         * Constantes
         * {@link Matrix.MPERSP_0}, {@link Matrix.MPERSP_1}, {@link Matrix.MPERSP_2}
         * {@link Matrix.MSCALE_X}, {@link Matrix.MSCALE_Y}
         * {@link Matrix.MSKEW_X}, {@link Matrix.MSKEW_Y}
         * {@link Matrix.ScaleToFit}
         * */
        float translateX = matrixValues[Matrix.MTRANS_X];
        float translateY = matrixValues[Matrix.MTRANS_Y];
        // acao realizada ao tocar na tela
        int action = event.getAction() & MotionEvent.ACTION_MASK;
        // posicao atual tocada
        float currentX = event.getX();
        float currentY = event.getY();
        //Log.i("ACTION_MOVE", String.format("EVENT_TOUCH_SCREEN(%f, %f).", currentX, currentY));
        /**
         * Ponto da tela
         * A coordenada de dispositivo
         * da esq -> para dir X
         * de cima -> para baixo y
         *
         * */
        PointF currentPositionTaped = new PointF(currentX, currentY);
        switch (action) {
            /**
             * Gesto de pressionar a tela
             * */
            case MotionEvent.ACTION_DOWN:
                //Log.i("ACTION_MOVE", "ACTION_DOWN");
                currentX = event.getX();
                currentY = event.getY();
                lastPositionTaped.set(currentX, currentY);
                startPositionTaped.set(lastPositionTaped);
                mode = DRAG;
                break;
            /**
             * Gesto de remover o dedo da tela
             * */
            case MotionEvent.ACTION_UP:
                int diffX = (int) Math.abs(currentPositionTaped.x - startPositionTaped.x);
                int diffY = (int) Math.abs(currentPositionTaped.y - startPositionTaped.y);
                //Log.i("ACTION_UP", String.format("DIFFXY (%d %d)\n", diffX, diffY));
                if(diffX < CLICK && diffY < CLICK) {
                    /**
                     * Executa um click se OnclickListener tiver sido implementado
                     * */
                    boolean hasOnclickListener = performClick();
                    //Log.i("PERFORM_CLICK", String.valueOf(hasOnclickListener));
                }
                mode = NONE;
                break;
            /**
             * Evento capturado entre o ACTION_DOWN e ACTION_UP. Esse evento contem
             * as coordenadas do ponto na tela mais recentes pressionado pelo usuario
             * e todos os pontos anteriores desde o ultimo ACTION_DOWN o ACTION_MOVE
             * */
            case MotionEvent.ACTION_MOVE:
                /**
                 *  (mode == DRAG) == V
                 *  O usuario pressionou o dedo (MotionEvent.ACTION_DOWN) sobre a imagem e a arrastou
                 *
                 *  (mode == ZOOM) == V
                 *  O usuario pressionou 2 ou mais dedos na tela (case MotionEvent.ACTION_POINTER_DOWN)
                 *  e fez um movimento de abrir ou fechar os dedos (pinch)
                 *
                 *  Se o usuario estiver fazendo ZOOM OUT, tambem devemos fazer uma translacao, uma vez que a imagem
                 *  pode ter sido deslocada para esquerda ou para direita e ao efetuar ZOOM OUT, a imagem ocupara
                 *  uma largura menor do que a da tela
                 * */
                if((mode == ZOOM || mode == DRAG) && saveFactorTranslation > minOffsetTranslation && saveFactorTranslation < maxOffsetTranslation) {
                    float deltaX = currentPositionTaped.x - lastPositionTaped.x
                            , deltaY = currentPositionTaped.y - lastPositionTaped.y;
                    float translateXImagePosition  = Math.round(imageViewWidthScaling * saveFactorTranslation);
                    float translateYImagePosition  = Math.round(imageViewHeightScaling * saveFactorTranslation);
                    int imageViewWidth   = getWidth();
                    int imageViewHeight  = getHeight();

                    Log.i("ACTION_MOVE", String.format("DELTA X(%f) Y(%f)", deltaX, deltaY));
                    Log.i("ACTION_MOVE", String.format("SAVED FACTOR TRANSLATE(%f)", saveFactorTranslation));
                    Log.i("ACTION_MOVE", String.format("ORIGINAL DIMENSION W(%d) H(%d)", imageViewWidth, imageViewHeight));
                    Log.i("ACTION_MOVE", String.format("TRANSLATE IMG X(%f) Y(%f)", translateXImagePosition, translateYImagePosition));
                    Log.i("ACTION_MOVE", String.format("TRANSLATE X(%f) Y(%f)", translateX, translateY));
                    Log.i("ACTION_MOVE", String.format("DISTANCE Right(%f) Bottom(%f)", differenceWidth, differenceHeight));
                    Log.i("ACTION_MOVE", "\n\n");
                    boolean allowMovimentX  = true, allowMovimentY  = true;
                    /**
                     * Se a imagem ainda cabe na tela restringir o movimento para esquerda/direita
                     * e para cima e para baixo
                     * */
                    if(translateYImagePosition <= imageViewHeight && translateXImagePosition <= imageViewWidth)
                    {
                        break;
                    }

                    if (translateXImagePosition < imageViewWidth || deltaX == 0)
                    {
                        // ao aplicar o valor de deslocamento(translacao) em X na imagem, a largura
                        // dela for menor do que a largura original, restrinja o movimento de transçacao na coordenada X
                        deltaX = 0;
                        allowMovimentX = false;
                    }

                    if(deltaX == 0 || deltaY == 0) {
                        deltaY = 0;
                        allowMovimentY = false;
                    }

                    else if(translateYImagePosition < imageViewHeight)
                    {
                        // se aplicarmos o valor de deslocamento em Y na imagem, a altura dela
                        // for menor que a altura da imagem original, restrinja o movimento de transçacao na coordenada Y
                        deltaY = 0;
                        allowMovimentY = false;
                    }
                     // movimentos de cima para baixo e vice versa
                    if( allowMovimentY )
                    {
                        float sum = translateY + deltaY;
                        Log.i("ACTION_MOVE", String.format("TRANSY+DELTAY (%f)", sum));
                        if(sum > 0)
                        {
                            deltaY = -translateY;
                        }
                        else if(sum < -differenceHeight)
                        {
                            deltaY = -(translateY + differenceHeight);
                        }
                    }
                    //  da esquerda para direita
                    if( allowMovimentX )
                    {
                        float sum = translateX + deltaX;
                        Log.i("ACTION_MOVE", String.format("TRANSX+DELTAX (%f)", sum));
                        if(sum > 0)
                        {
                            deltaX = -translateX;
                        }
                        else if(sum < -differenceWidth)
                        {
                            deltaX = -(translateX + differenceWidth);
                        }
                    }
                    deltaX = deltaX == -0.0f ? 0.0f : deltaX;
                    deltaY = deltaY == -0.0f ? 0.0f : deltaY;
                    matrix.postTranslate(deltaX, deltaY);
                    Log.i("ACTION_MOVE", String.format("DELTA TRANSLATION(%f, %f)\n%s", deltaX, deltaY, matrix.toString()));
                    lastPositionTaped.set(currentPositionTaped.x, currentPositionTaped.y);
                }
                break;
            /**
             * A non-primary pointer has gone down.
             * Quando o usuario pressionada a tela com 2 ou mais dedos
             * o evento (MotionEvent.ACTION_POINTER_DOWN) e executado
             * */
            case MotionEvent.ACTION_POINTER_DOWN:
                //Log.i("ACTION_MOVE", "ACTION_POINTER_DOWN");
                currentX = event.getX();
                currentY = event.getY();
                lastPositionTaped.set(currentX, currentY);
                startPositionTaped.set(lastPositionTaped);
                mode = ZOOM;
                break;
            /**
             * A non-primary pointer has gone up.
             * Quando o usuario toca na tela com 2 ou mais dedos, ao
             * remove-los o evento (MotionEvent.ACTION_POINTER_UP) eh
             * executado ateh que sobre somente 1 dedo, quando o ultimo
             * dedo for removido da tela, o evento ACTION_UP sera executado
             * */
            case MotionEvent.ACTION_POINTER_UP:
                //Log.i("ACTION_MOVE", "ACTION_POINTER_UP");
                mode = NONE;
                break;
        }
        gestureDetector.onTouchEvent(event);
        setImageMatrix(matrix);
        invalidate();
    }

    /**
     *
     * Metodo responsavel por mensurar a View
     *
     * Segundo a documentacao, para manter o contrato com a super classe, quando sobreescrevermos
     * esse metodo devemos chamar o onMeasure da super classe.
     * */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        /**
         * Encapsula o metodo {@link Drawable#getIntrinsicWidth()}
         * responsavel por retornar o valor de largura intrinseco que o Drawable
         * possui, incluindo tambem valores de padding, caso a imagem possua
         * */
        int intrWidth   = getIntrWidth();
        int intrHeight  = getIntrHeight();
        // @return The raw measured mWidthImageView of this view. // valor bruto ?
        mWidthImageView = getMeasuredWidth();
        // @return The raw measured mHeightImageView of this view. // valor bruto ?
        mHeightImageView = getMeasuredHeight();
        // Escala proporcional. Razao entre widthRaw/instrinsicWidth ou heightRaw/intrinsicHeight
        //
        startScale = mWidthImageView > mHeightImageView ? (mHeightImageView / intrHeight)
                : (mWidthImageView / intrWidth);
        /*
        Log.i("ON_MEASURE", String.format("Dimensao bruta (%f, %f)\nIntrinsic W/H(%d %d)"
            , mWidthImageView, mHeightImageView, intrWidth, intrHeight)
        );
        */
        /**
         * TODO explicar porque faco a operacao de Scale aqui
         * */
        matrix.setScale(startScale, startScale);
        imageViewWidthScaling   = startScale * intrWidth;
        imageViewHeightScaling  = startScale * intrHeight;
        Log.i("LISTENER_ACTION_MOVE", String.format("Original Scale(%f)\n", startScale));
        //Log.i("LISTENER_ACTION_MOVE", String.format("Dimension pos Scaling(%f, %f)\n Escala %f", imageViewWidthScaling, imageViewHeightScaling, scale));
        diffRawScalingHeight    = (mHeightImageView - imageViewHeightScaling);
        diffRawScalingWidth     = (mWidthImageView - imageViewWidthScaling);
        float midRawWidth       = diffRawScalingWidth / 2.0f;
        float midRawHeight      = diffRawScalingHeight / 2.0f;
        /*
        Log.i("ON_MEASURE", String.format("difference(%f, %f).\nTransalacao (%f, %f).\n%s"
                , diffRawScalingWidth
                , diffRawScalingHeight
                , midRawWidth
                , midRawHeight
                , matrix.toString()
            )
        );
        */
        matrix.postTranslate(midRawWidth, midRawHeight);
        setImageMatrix(matrix);

        /**
         * Ainda segundo a documentacao
         * If this method is overridden, it is the subclass's responsibility to make sure the measured
         * height and width are at least the view's minimum height and width (getSuggestedMinimumHeight() and getSuggestedMinimumWidth()).
         * */
        int minW = getSuggestedMinimumWidth();
        int minH = getSuggestedMinimumHeight();
        int w = resolveSize(minW, widthMeasureSpec);
        int h = resolveSize(minH, heightMeasureSpec);
        setMeasuredDimension(w, h);
    }

    /**
     * {@link GestureDetector}
     *  Classe capaz de detectar diversos evetos de toque atraves da classe {@link MotionEvent}.
     *
     *  os metodos em {@link OnGestureListener} irao informar
     *  a aplicacao quando um motion event ocorrer.
     *  Essa classe so opera utilizando MotionEvent
     * */
    private class MyGestureDetector extends GestureDetector {
        /**
         * Creates a GestureDetector with the supplied listener.
         * You may only use this constructor from a {@link android.os.Looper} thread.
         *
         * @param context  the application's context
         * @param listener the listener invoked for all the callbacks, this must
         *                 not be null.
         * @throws NullPointerException if {@code listener} is null.
         * @see android.os.Handler#Handler()
         */
        public MyGestureDetector(Context context, OnGestureListener listener) {
            super(context, listener);
        }

        @Override
        public boolean onGenericMotionEvent(MotionEvent ev) {
            return super.onGenericMotionEvent(ev);
        }
    }

    /**
     * Class responsavel por implementar a operacao de Scale na imagem
     * */
    private class MyScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        private void debugScaleGestureDetector(ScaleGestureDetector detector, float scaleFactor) {
            Log.i("ON_SCALE_MY_LISTENER", String.format("ON_SCALE Scale Factor %f.\n (Current,Previous)(%f,%f)"
                    , scaleFactor
                    , detector.getCurrentSpan()     // a media entre cada ponto que forma o gesto executado na tela do celular
                    , detector.getPreviousSpan()    // idem para
                    )
            );
        }

        /**
         * Responde a evento de Scale em Views, reportados por Pointer Motion
         * */
        @Override
        public boolean onScale(ScaleGestureDetector scaleGestureDetector) {
            /**
             * Return the scaling factor from the previous scale event to the current
             * event. This value is defined as
             * ({@link #getCurrentSpan()} / {@link #getPreviousSpan()}).
             *
             * @return The current scaling factor.
             */
            float scaleFactor = scaleGestureDetector.getScaleFactor();
            float newScaleTransaction = saveFactorTranslation * scaleFactor;
            float newScaleZoom = saveFactorZoom * scaleFactor;
/*
            Log.i("LISTENER_ACTION_MOVE", String.format("SCALE FACTOR (%f)\nSCALE TRANS (%f)\nSCALE ZOOM (%f)"
                    , scaleFactor, newScaleTransaction, newScaleZoom));
 */
            /**
             * Verifica se a escala esta dentro do intervalor [min,max] para redimensionar
             * a imagem
             * */
            if(newScaleTransaction < maxOffsetTranslation && newScaleTransaction > minOffsetTranslation) {
                saveFactorTranslation = newScaleTransaction;
            }

            if(newScaleZoom < maxScaleZoom && newScaleZoom > minScaleZoom) {
                saveFactorZoom = newScaleZoom;
                // Largura da ImageView
                float width = getWidth();
                // altura da ImageView
                float height = getHeight();
                /**
                 * imageViewWidthScaling e imageViewHeightScaling sao definidos no metodo
                 * onMeasure.
                 * Os valores sao atributos usando o seguinte calculo
                 * scale = mWidthImageView > mHeightImageView ? mHeightImageView / intrHeight : mWidthImageView / intrWidth;
                 * */
                // diferenca na largura da imagem original para a escalada
                differenceWidth = (imageViewWidthScaling * saveFactorZoom) - width;
                //  diferenca na altura da imagem original para a escalada
                differenceHeight = (imageViewHeightScaling * saveFactorZoom) - height;
                float scaleBitmapWidth = imageViewWidthScaling * saveFactorZoom;//scaleFactor;
                float scaleBitmapHeight = imageViewHeightScaling * saveFactorZoom;//scaleFactor;
/*
                Log.i("LISTENER_ACTION_MOVE", String.format("Dimensao antes (%f %f)\nDimensao depois (%f %f)\nFactor (%f)\nScale (%f)"
                        , imageViewWidthScaling
                        , imageViewHeightScaling
                        , scaleBitmapWidth
                        , scaleBitmapHeight
                        , scaleFactor
                        , saveFactorZoom
                ));
                Log.i("LISTENER_ACTION_MOVE", String.format("Diff W/H(%f %f)", differenceWidth, differenceHeight));
*/
                if(scaleBitmapWidth < width || scaleBitmapHeight < height) {
                    matrix.postScale(scaleFactor, scaleFactor, width / 2, height / 2);
                }
                else {
                    /**
                     * {@link ScaleGestureDetector#getFocusX()}
                     * Pegar a posicao X de onde ocorreu o evento de toque na View que implementa
                     * o metodo onTouchEvent
                     * E
                     * */
                    float detectFocusPx = scaleGestureDetector.getFocusX()
                            , detectFocusPy = scaleGestureDetector.getFocusY();
                    //Log.i("LISTENER_ACTION_MOVE", String.format("DETECT FOCUS  XY(%f %f)", detectFocusPx, detectFocusPy));
                    matrix.postScale(scaleFactor, scaleFactor, detectFocusPx, detectFocusPy);
                }
                /*
                float [] f = new float[9];
                matrix.getValues(f);
                Log.i("LISTENER_ACTION_MOVE", String.format("Operation Scale(%f,%f)", f[Matrix.MTRANS_X], f[Matrix.MTRANS_Y]));
                */
            }
            return true;
        }

        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            //Log.i("LISTENER_ACTION_MOVE", "SCALE BEGIN");
            mode = ZOOM;
            return true;
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {
            mode = NONE;
            //Log.i("LISTENER_ACTION_MOVE", "SCALE END");
        }
    }

    private int getIntrWidth() {
        Drawable drawable = getDrawable();
        return  drawable != null ? drawable.getIntrinsicWidth() : 0;
    }

    private int getIntrHeight() {
        Drawable drawable = getDrawable();
        return  drawable != null ? drawable.getIntrinsicHeight() : 0;
    }
}

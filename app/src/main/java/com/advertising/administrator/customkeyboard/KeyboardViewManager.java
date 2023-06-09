package com.advertising.administrator.customkeyboard;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Rect;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class KeyboardViewManager implements KeyboardView.OnKeyboardActionListener {
    //键盘的根布局
    private final FrameLayout frameLayout;
    //英文键盘和数字键盘标记
    public static Integer NUMBERXML = R.xml.keyboard_number_abc;
    public static Integer ENGLISHXML = R.xml.keyboard_english;
    private static List<EditText> showSystem;

    private static Map<EditText, onSureClickListener> editList;
    private EditText currentEditText;
    private EditText focusReplace;
    private static Context context;
    private final Keyboard keyboardEnglish;
    private final Keyboard keyboardNumber;
    //标识英文键盘大小写切换
    private boolean isCapital = true;
    private final KeyboardView keyboardView;
    //标识数字键盘和英文键盘的切换
    private boolean isShift;

    private FrameLayout rootView;
    boolean hideing = true;

    private KeyboardViewManager() {

        //创建打气筒
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        //把键盘布局解析成对象
        frameLayout = (FrameLayout) layoutInflater.inflate(R.layout.content_keyboardview, null);
        keyboardView = frameLayout.findViewById(R.id.keyboardView);
        //创建keyboard
        keyboardNumber = new Keyboard(context, NUMBERXML);
        keyboardEnglish = new Keyboard(context, ENGLISHXML);
        //把创建的键盘布局设置给控件
        keyboardView.setKeyboard(keyboardNumber);
        //给键盘设置监听
        keyboardView.setOnKeyboardActionListener(this);
        for (EditText key : editList.keySet()) {
            key.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean b) {

                    Log.e("焦点：",b+"");

                    if (b) {
//                        ((EditText)view).setCursorVisible(true);
//                        ((EditText)view).requestFocus(); // 让光标得到焦点
//                        ((EditText)view).setSelection(((EditText)view).getText().length()); // 将光标置于文本的末尾

                        if (showSystem.contains(view)) {
                            Log.e("焦点：","隐藏自定义键盘");
                            hideSoftKeyboard();
                            currentEditText = (EditText) view;

                            InputMethodManager inputMethodManager = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);

                           // currentEditText.setInputType(InputType.TYPE_NULL); // 切换到纯文本输入模式
                            inputMethodManager.showSoftInput(currentEditText, InputType.TYPE_CLASS_TEXT); // 显示系统键盘

//                            currentEditText.setCursorVisible(true);
//                            currentEditText.requestFocus(); // 让光标得到焦点
//                            currentEditText.setSelection(currentEditText.getText().length()); // 将光标置于文本的末尾
                        } else {
                            Log.e("焦点：","显示自定义键盘");
                           // showCursor(currentEditText);
                            currentEditText = (EditText) view;
                            SystemSoftKeyUtils.hideSoftInput(context, view);
                            showSoftKeyboard();
                        }

                        currentEditText.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                showCursor(currentEditText);
                            }
                        }, 300);

                    }
                    Log.e("焦点：","22="+currentEditText.hasFocus());

//                    if(!currentEditText.hasFocus()){
//                        hideSoftKeyboard();
//                    }

                }
            });
        }
    }

    public void showCursor(EditText editText) {
        editText.setCursorVisible(true);
        editText.requestFocus(); // 让光标得到焦点
        editText.setSelection(editText.getText().length()); // 将光标置于文本的末尾
    }

    /**
     * 显示自定义键盘
     */
    public void showSoftKeyboard() {
        //根据设置的输入类型，动态切换键盘
        int inputType = currentEditText.getInputType();
        Log.e("输入类型inputType=",inputType+"");

        if (inputType == 2||inputType==2002||inputType==8194) {
            keyboardView.setKeyboard(keyboardNumber);
            isShift = true;
        } else {
            keyboardView.setKeyboard(keyboardEnglish);
            isShift = false;
        }
        //如果遮挡住，计算需要上移的距离，进行上移
        isCover();
        //键盘显示和隐藏
        if (frameLayout.getVisibility() == View.GONE) {
            Animation show = AnimationUtils.loadAnimation(context, R.anim.down_to_up);
            frameLayout.startAnimation(show);
            show.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    frameLayout.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
        }


    }

    /**
     * 隐藏自定义键盘
     */
    public void hideSoftKeyboard() {

        Log.e("方法：","hideSoftKeyboard");

        if (frameLayout.getVisibility() == View.VISIBLE && hideing) {
            hideing = false;
            Object tag = rootView.getTag();
            if (tag != null) {
                //遍历所有的子View，让其向上移动改移动的高度
                for (int i = 0; i < rootView.getChildCount(); i++) {
                    if (rootView.getChildAt(i) != frameLayout) {
                        ObjectAnimator.ofFloat(rootView.getChildAt(i), "translationY", 0).setDuration(200).start();
                    }
                }
            }
            //设置隐藏动画
            Animation hide = AnimationUtils.loadAnimation(context, R.anim.up_to_hide);
            frameLayout.startAnimation(hide);
            hide.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    currentEditText.setCursorVisible(false);
                    focusReplace.requestFocus();
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    frameLayout.setVisibility(View.GONE);
                    hideing = true;
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });

        }
      //  currentEditText.setFocusable(false);
      //  currentEditText.clearFocus();
    }


    /**
     * 添加键盘到布局中去，这里应该去调用隐藏系统键盘
     *
     * @param rootView
     * @return
     */
    public KeyboardViewManager addKeyboardView(FrameLayout rootView) {
        this.rootView = rootView;
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                Gravity.BOTTOM);
        FrameLayout.LayoutParams params2 = new FrameLayout.LayoutParams(0, 0, Gravity.BOTTOM);
        rootView.addView(frameLayout, params);
        focusReplace = new EditText(context);
        rootView.addView(focusReplace, params2);
        return this;
    }


    //================================键盘监听事件回调==============================================

    @Override
    public void onPress(int i) {

    }

    @Override
    public void onRelease(int i) {

    }

    @Override
    public void onKey(int primaryCode, int[] ints) {
        //获取文本内容
        Editable editable = currentEditText.getText();
        //  获取光标位置
        int start = currentEditText.getSelectionStart();
        switch (primaryCode) {

            case -1://切换大小写按钮
                shiftEnglish();
                keyboardView.setKeyboard(keyboardEnglish);
                break;
            case -2://字母和数字切换按钮
//                if (currentEditText.getInputType() == 2 && keyboardView.getKeyboard() == keyboardNumber) {
//                    Toast.makeText(context, "只能输入数字", Toast.LENGTH_SHORT).show();
//                } else {
//                    shiftKeyboard();
//                }
                hideSoftKeyboard();
                currentEditText.clearFocus();
                break;

            case -4://完成按钮
                //光标不显示
                hideSoftKeyboard();
                if (editList.get(currentEditText) != null) {
                    editList.get(currentEditText).onSureClick();
                }
                currentEditText.clearFocus();
                break;


            case -5://删除光标前字符
                if (!TextUtils.isEmpty(editable)) {
                    if (start > 0) {
                        editable.delete(start - 1, start);
                    }
                }
                break;

            default://普通的按键就直接去把字符串设置到EditText上即可
                //在光标处插入字符
                editable.insert(start, Character.toString((char) primaryCode));
                break;
        }


    }

    @Override
    public void onText(CharSequence charSequence) {

    }

    @Override
    public void swipeLeft() {

    }

    @Override
    public void swipeRight() {

    }

    @Override
    public void swipeDown() {

    }

    @Override
    public void swipeUp() {

    }

    /**
     * 如果输入框呗遮挡就向下移动
     *
     * @return
     */
    public void isCover() {
        //获取传递过来的跟布局的宽高
        Rect rect = new Rect();
        frameLayout.getWindowVisibleDisplayFrame(rect);
        //计算当前获取焦点的输入框在屏幕中的位置
        int[] vLocation = new int[2];
        currentEditText.getLocationOnScreen(vLocation);
        int keyboardTop = vLocation[1] + currentEditText.getHeight() / 2 + currentEditText.getPaddingBottom() + currentEditText.getPaddingTop();
        //输入框或基线View的到屏幕的距离 + 键盘高度 如果 超出了屏幕的承载范围, 就需要移动.
        int moveHeight = rect.bottom - keyboardTop - keyboardView.getHeight();
        moveHeight = Math.min(moveHeight, 0);
        if (moveHeight != 0) {
            rootView.setTag("move");
            //遍历所有的子View，让其向上移动改移动的高度
            for (int i = 0; i < rootView.getChildCount(); i++) {
                if (rootView.getChildAt(i) != frameLayout) {
                    rootView.getChildAt(i).setTranslationY(moveHeight);
                    ObjectAnimator.ofFloat(rootView.getChildAt(i), "translationY", 0, moveHeight).setDuration(200).start();
                }
            }
        }

    }


    //===========================Builder模式==============================================
    public static Builder builder() {
        return new Builder();
    }


    public static final class Builder {

        private Builder() {
            editList = new HashMap<>();
            showSystem = new ArrayList<>();
        }


        //如果页面有Eidttist，解决键盘冲突，这个方法必须写
        public Builder bindEditText(EditText... editText) {
            for (int i = 0; i < editText.length; i++) {
                //隐藏系统软键盘冲突，需要配合清单文件一起使用: android:windowSoftInputMode="stateHidden|stateUnchanged"
                SystemSoftKeyUtils.hideSystemSoftKeyboard(editText[i]);
                editList.put(editText[i], null);
            }

            return this;
        }

        public Builder bindEditTextCallBack(EditText editText, onSureClickListener onSurelistener) {
            editList.put(editText, onSurelistener);
            return this;
        }

        public Builder showSystemKeyboard(EditText... editTexts) {
            showSystem.addAll(Arrays.asList(editTexts));
            for (int i = 0; i < editTexts.length; i++) {
                editList.put(editTexts[i], null);
               // editTexts[i].clearFocus();
            }

            return this;
        }


        public void hideSystemKeyboard(EditText... editTexts) {
            showSystem.removeAll(Arrays.asList(editTexts));
            for (int i = 0; i < editTexts.length; i++) {
                editList.remove(editTexts[i]);
                //editTexts[i].clearFocus();
            }

        }

        public KeyboardViewManager build(Context context1) {
            context = context1;
            return new KeyboardViewManager();
        }


    }

    //==============================================================================================

    /**
     * 英文键盘大小写切换
     */
    private void shiftEnglish() {
        //获取所有的key
        List<Keyboard.Key> keyList = keyboardEnglish.getKeys();
        for (Keyboard.Key key : keyList) {
            if (key.label != null && isKey(key.label.toString())) {
                if (isCapital) {
                    key.label = key.label.toString().toLowerCase();
                    key.codes[0] = key.codes[0] + 32;
                } else {
                    key.label = key.label.toString().toUpperCase();
                    key.codes[0] = key.codes[0] - 32;
                }
            }
        }
        isCapital = !isCapital;
    }


    /**
     * 判断此key是否正确，且存在
     *
     * @param key
     * @return
     */
    private boolean isKey(String key) {
        String lowercase = "abcdefghijklmnopqrstuvwxyz";
        if (lowercase.indexOf(key.toLowerCase()) > -1) {
            return true;
        }
        return false;
    }


    //==========================切换键盘=================================================

    /**
     * 切换键盘
     */
    private void shiftKeyboard() {
        if (isShift) {
            keyboardView.setKeyboard(keyboardEnglish);
            keyboardView.invalidate();
        } else {
            keyboardView.setKeyboard(keyboardNumber);
            keyboardView.invalidate();
        }
        isShift = !isShift;
    }


    //===================================点击确定回调==============================================

    public interface onSureClickListener {
        void onSureClick();
    }


}

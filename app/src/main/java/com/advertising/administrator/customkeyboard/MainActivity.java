package com.advertising.administrator.customkeyboard;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private EditText edit1;
    private EditText edit2;
    private EditText edit3;
    private EditText edit4;
    private KeyboardViewManager keyboardViewManager;
    private boolean isCustomKeyboardShown = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FrameLayout rootView = findViewById(R.id.rootView);
        edit1 = findViewById(R.id.edit1);
        edit2 = findViewById(R.id.edit2);
        edit3 = findViewById(R.id.edit3);
        edit4 = findViewById(R.id.edit4);

        final Button btn = findViewById(R.id.btn);

        btn.setText("系统键盘");

        final KeyboardViewManager.Builder builder = KeyboardViewManager.builder();

        keyboardViewManager = builder
                .bindEditText(edit1, edit2, edit3)
                .bindEditTextCallBack(edit1, new KeyboardViewManager.onSureClickListener() {
                    @Override
                    public void onSureClick() {

                    }
                })
                .bindEditTextCallBack(edit2, new KeyboardViewManager.onSureClickListener() {
                    @Override
                    public void onSureClick() {

                    }
                })
                .build(this)
                .addKeyboardView(rootView);



// 切换自定义键盘和系统键盘
//        if (isCustomKeyboardShown) {
//            // 自定义键盘已经显示，切换到系统键盘
//            edit2.setInputType(InputType.TYPE_CLASS_TEXT); // 切换到纯文本输入模式
//            inputMethodManager.showSoftInput(edit2, 0); // 显示系统键盘
//            isCustomKeyboardShown = false; // 自定义键盘未显示
//        } else {
//            // 自定义键盘未显示，切换到自定义键盘
//            edit2.setInputType(InputType.TYPE_NULL); // 禁用系统键盘
//            inputMethodManager.hideSoftInputFromWindow(edit2.getWindowToken(), 0); // 隐藏系统键盘
//           // customKeyboard.setVisibility(View.VISIBLE); // 显示自定义键盘
//            isCustomKeyboardShown = true; // 自定义键盘已显示
//        }




        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 获取当前EditText输入框的输入法管理器
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (isCustomKeyboardShown) {
                    builder.hideSystemKeyboard(edit2);
                    btn.setText("系统键盘");

                  //  edit2.setInputType(InputType.TYPE_NULL); // 禁用系统键盘
                    edit2.setFocusable(true);
                    inputMethodManager.hideSoftInputFromWindow(edit2.getWindowToken(), InputType.TYPE_CLASS_TEXT); // 隐藏系统键盘

                    keyboardViewManager.showSoftKeyboard();
                } else {
                    builder.showSystemKeyboard(edit2);
                    btn.setText("简拼键盘");
                    keyboardViewManager.hideSoftKeyboard();

                    // 自定义键盘已经显示，切换到系统键盘,
                   // edit2.setInputType(InputType.TYPE_CLASS_TEXT); // 切换到纯文本输入模式
                    inputMethodManager.showSoftInput(edit2, InputType.TYPE_CLASS_TEXT); // 显示系统键盘
                }

                isCustomKeyboardShown = !isCustomKeyboardShown;

                edit2.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        showCursor(edit2);
                    }
                }, 0);

            }
        });



    }

    public void showCursor(EditText editText) {
        editText.setCursorVisible(true);
        editText.requestFocus(); // 让光标得到焦点
        editText.setSelection(editText.getText().length()); // 将光标置于文本的末尾
    }


//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        keyboardViewManager.hideSoftKeyboard();
//        return super.onTouchEvent(event);
//    }
}

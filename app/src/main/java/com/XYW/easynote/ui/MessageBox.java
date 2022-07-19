package com.XYW.easynote.ui;

import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.XYW.easynote.R;

public class MessageBox {

    private static CreateMessageBox mDialog;

    public interface MessageBoxOnCkickListener {
        void onClick();
    }

    public interface MessageBoxOnCancelListener {
        void onCancel();
    }

    public interface MessageBoxOnDismissListener {
        void onDismiss();
    }

    static public class CreateMessageBox extends Dialog {

        public CreateMessageBox(@NonNull Context context, int themeResId) {
            super(context, themeResId);
        }

        static public class Builder {
            private View mLayout;
            private final Context context;

            private final TextView TextView_Title, TextView_Content;
            private final Button Button_negative, Button_positive;
            private final View View_Icon, View_cutLine_Port, View_cutLine_Land;
            private final ImageView ImageView_Icon;
            private final ProgressBar ProgressBar;
            private final LinearLayout LinearLayout_Title, LinearLayout_Content, View_Content, LinearLayout_Button;

            private int numButton = 0;
            private boolean cancelAble = true, canceledOnTouchOutside = true;
            private MessageBoxOnCancelListener onCancelListener;
            private MessageBoxOnDismissListener onDismissListener;

            public Builder(Context context) {
                this.context = context;

                if (mDialog != null && mDialog.isShowing()) {
                    mDialog.dismiss();
                }

                mDialog = new CreateMessageBox(context, R.style.Theme_AppCompat_Dialog);
                LayoutInflater inflater =
                        (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                //加载布局文件
                mLayout = inflater.inflate(R.layout.dialog_messagebox, null, false);
                //添加布局文件到 Dialog
                mDialog.addContentView(mLayout, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT));

                mDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

                TextView_Title = mLayout.findViewById(R.id.TextView_Title);
                TextView_Content = mLayout.findViewById(R.id.TextView_Content);

                Button_negative = mLayout.findViewById(R.id.Button_negative);
                Button_positive = mLayout.findViewById(R.id.Button_positive);
                Button_negative.setVisibility(View.GONE);
                Button_positive.setVisibility(View.GONE);

                View_Icon = mLayout.findViewById(R.id.View_messageBoxIcon);
                View_cutLine_Port = mLayout.findViewById(R.id.View_cutLine_Port);
                View_cutLine_Land = mLayout.findViewById(R.id.View_cutLine_Land);

                ImageView_Icon = mLayout.findViewById(R.id.ImageView_messageBoxIcon);

                ProgressBar = mLayout.findViewById(R.id.Progressbar_messageBoxPGB);
                ProgressBar.setVisibility(View.GONE);

                LinearLayout_Title = mLayout.findViewById(R.id.LinearLayout_messageBoxTitle);
                LinearLayout_Content = mLayout.findViewById(R.id.LinearLayout_messageContent);
                View_Content = mLayout.findViewById(R.id.LinearLayout_usersMessageContent);
                LinearLayout_Button = mLayout.findViewById(R.id.LineatLayout_messageBoxButton);
                LinearLayout_Title.setVisibility(View.GONE);
                LinearLayout_Button.setVisibility(View.GONE);
            }

            public Builder setTitle(String str) {
                TextView_Title.setText(str);
                LinearLayout_Title.setVisibility(View.VISIBLE);
                return this;
            }

            public Builder setMessage(String str) {
                TextView_Content.setText(str);
                TextView_Content.setVisibility(View.VISIBLE);
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) ProgressBar.getLayoutParams();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    params.setMarginEnd(10);
                } else {
                    params.setMargins(0, 0, 10, 0);
                }
                ProgressBar.setLayoutParams(params);
                return this;
            }

            public Builder setIcon(int resId) {
                View_Icon.setVisibility(View.GONE);
                ImageView_Icon.setVisibility(View.VISIBLE);
                ImageView_Icon.setImageResource(resId);
                return this;
            }

            public Builder setNegativeButton(String str, MessageBoxOnCkickListener listener) {
                numButton++;
                LinearLayout_Button.setVisibility(View.VISIBLE);
                Button_negative.setVisibility(View.VISIBLE);
                Button_negative.setText(str);
                Button_negative.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        MessageBox.dismiss();
                        try {
                            listener.onClick();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                return this;
            }

            public Builder setPositiveButton(String str, MessageBoxOnCkickListener listener) {
                numButton++;
                LinearLayout_Button.setVisibility(View.VISIBLE);
                Button_positive.setVisibility(View.VISIBLE);
                Button_positive.setText(str);
                Button_positive.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        MessageBox.dismiss();
                        try {
                            listener.onClick();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                return this;
            }

            public Builder setCancelable(boolean b) {
                cancelAble = b;
                return this;
            }

            public Builder setCanceledOnTouchOutside(boolean b) {
                canceledOnTouchOutside = b;
                return this;
            }

            public Builder setProgressbar(boolean b) {
                if (b) {
                    ProgressBar.setVisibility(View.VISIBLE);
                }
                return this;
            }

            public Builder setView(View view) {
                View_Content.addView(view);
                View_Content.setVisibility(View.VISIBLE);
                LinearLayout_Content.setVisibility(View.GONE);
                return this;
            }

            public Builder addView(View view) {
                View_Content.addView(view);
                View_Content.setVisibility(View.VISIBLE);
                return this;
            }

            public Builder setLayout(int resId) {
                LayoutInflater inflater =
                        (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                //加载布局文件
                mLayout = inflater.inflate(resId, null, false);
                return this;
            }

            public Builder setLayout(View layout) {
                mLayout = layout;
                return this;
            }

            public Builder setOnCancelListener(MessageBoxOnCancelListener listener) {
                onCancelListener = listener;
                return this;
            }

            public Builder setOnDismissListener(MessageBoxOnDismissListener listener) {
                onDismissListener = listener;
                return this;
            }

            public CreateMessageBox create() {
                if (numButton >= 1) {
                    View_cutLine_Land.setVisibility(View.VISIBLE);
                }
                if (numButton >= 2) {
                    View_cutLine_Port.setVisibility(View.VISIBLE);
                }
                mDialog.setContentView(mLayout);
                mDialog.setCancelable(cancelAble);                //用户可以点击后退键关闭 Dialog
                mDialog.setCanceledOnTouchOutside(canceledOnTouchOutside);   //用户不可以点击外部来关闭 Dialog
                mDialog.setOnCancelListener(dialogInterface -> {
                    try {
                        onCancelListener.onCancel();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
                mDialog.setOnDismissListener(dialogInterface -> {
                    try {
                        onDismissListener.onDismiss();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
                return mDialog;
            }
        }
    }


    static public void dismiss() {
        try {
            mDialog.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

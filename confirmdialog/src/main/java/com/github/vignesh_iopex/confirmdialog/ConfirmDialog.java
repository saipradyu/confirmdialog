/*
 * Copyright 2014 Vignesh Periasami
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package com.github.vignesh_iopex.confirmdialog;

import android.app.Activity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

public class ConfirmDialog {
  private static final String TAG = ConfirmDialog.class.getSimpleName();

  private Activity activity;
  private DialogEventListener dialogEventListener;
  private View contentView;
  private String contentText;

  // Views on confirm dialog.
  private ViewGroup dialogView;
  private ViewGroup dialogContent;
  private ViewGroup dialogContentContainer;
  private ViewGroup dialogOverlay;
  private TextView contentTextView;
  private Button btnConfirm;
  private Button btnCancel;
  private String confirmBtnText;
  private String cancelBtnText;

  private ConfirmDialog(Activity activity) {
    this.activity = activity;
  }

  private static ConfirmDialog build(Builder builder) {
    ConfirmDialog dialog = new ConfirmDialog(builder.activity);
    dialog.contentView = builder.contentView;
    dialog.contentText = builder.contentText;
    dialog.dialogEventListener = builder.eventListener;
    dialog.confirmBtnText = builder.confirmBtnText;
    dialog.cancelBtnText = builder.cancelBtnText;
    return dialog;
  }

  /**
   * set #dialogView before calling injectViews.
   */
  private void injectViews() {
    this.dialogContent = (ViewGroup) dialogView.findViewById(R.id.alert_content);
    this.dialogContentContainer = (ViewGroup) dialogView.findViewById(R.id.alert_container);
    this.dialogOverlay = (ViewGroup) dialogView.findViewById(R.id.confirm_overlay);
    this.contentTextView = (TextView) dialogView.findViewById(R.id.confirm_text);
    this.btnConfirm = (Button) dialogView.findViewById(R.id.btn_confirm);
    this.btnCancel = (Button) dialogView.findViewById(R.id.btn_cancel);
    this.dialogContent.setOnClickListener(null);
  }

  private void renderDialogContent() {
    Animation animation = AnimationUtils.loadAnimation(activity, R.anim.slide_from_bottom);
    this.dialogContentContainer.setVisibility(View.VISIBLE);
    this.dialogContentContainer.startAnimation(animation);
  }

  private void dismissDialogContent() {
    Animation animation = AnimationUtils.loadAnimation(activity, R.anim.slide_to_bottom);
    animation.setAnimationListener(new Animation.AnimationListener() {
      @Override
      public void onAnimationStart(Animation animation) {

      }

      @Override
      public void onAnimationEnd(Animation animation) {
        ((ViewGroup) dialogView.getParent()).removeView(dialogView);
      }

      @Override
      public void onAnimationRepeat(Animation animation) {

      }
    });
    this.dialogContentContainer.startAnimation(animation);
    this.dialogContentContainer.setVisibility(View.GONE);
  }

  public void injectListeners() {
    // set button listeners.
    this.btnConfirm.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (dialogEventListener != null)
          dialogEventListener.onConfirm();
        dismissDialogContent();
      }
    });

    this.dialogOverlay.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (dialogEventListener != null)
          dialogEventListener.onDismiss();
        dismissDialogContent();
      }
    });

    this.btnCancel.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (dialogEventListener != null)
          dialogEventListener.onCancel();
        dismissDialogContent();
      }
    });
  }

  public void show() {
    LayoutInflater inflater = activity.getLayoutInflater();
    dialogView = (ViewGroup) inflater.inflate(R.layout.confirm_dialog, null);
    injectViews();
    injectListeners();

    if (contentView != null) {
      dialogContent.removeAllViews();
      dialogContent.addView(contentView);
    } else if (!TextUtils.isEmpty(contentText)) {
      contentTextView.setText(contentText);
    }

    // populate views
    if (!TextUtils.isEmpty(confirmBtnText)) {
      btnConfirm.setText(confirmBtnText);
    }

    if (!TextUtils.isEmpty(cancelBtnText)) {
      btnCancel.setText(cancelBtnText);
    }

    ViewGroup parent = (ViewGroup) activity.findViewById(android.R.id.content);
    parent.addView(dialogView);
    renderDialogContent();
  }

  public static class Builder {
    private View contentView;
    private DialogEventListener eventListener;
    private Activity activity;
    private String contentText;
    private String confirmBtnText;
    private String cancelBtnText;

    public Builder(Activity activity) {
      this.activity = activity;
    }

    public Builder setContentView(View view) {
      this.contentView = view;
      return this;
    }

    public Builder setContextText(String contentText) {
      this.contentText = contentText;
      return this;
    }

    public Builder setEventListener(DialogEventListener eventListener) {
      this.eventListener = eventListener;
      return this;
    }

    public Builder setConfirmBtnText(String btnText) {
      this.confirmBtnText = btnText;
      return this;
    }

    public Builder setCancelBtnText(String btnText) {
      this.cancelBtnText = btnText;
      return this;
    }

    public ConfirmDialog create() {
      return ConfirmDialog.build(this);
    }
  }

  public interface DialogEventListener {
    void onConfirm();
    void onCancel();
    void onDismiss();
  }

}
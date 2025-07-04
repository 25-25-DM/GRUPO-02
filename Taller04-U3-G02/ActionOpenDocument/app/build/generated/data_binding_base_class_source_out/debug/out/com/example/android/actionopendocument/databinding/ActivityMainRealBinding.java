// Generated by view binder compiler. Do not edit!
package com.example.android.actionopendocument.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.Guideline;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.example.android.actionopendocument.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class ActivityMainRealBinding implements ViewBinding {
  @NonNull
  private final FrameLayout rootView;

  @NonNull
  public final Guideline bottomGuide;

  @NonNull
  public final TextView callToAction;

  @NonNull
  public final FrameLayout container;

  @NonNull
  public final ConstraintLayout noDocumentView;

  @NonNull
  public final Button openFile;

  @NonNull
  public final ImageView openFileIcon;

  @NonNull
  public final Guideline topGuide;

  private ActivityMainRealBinding(@NonNull FrameLayout rootView, @NonNull Guideline bottomGuide,
      @NonNull TextView callToAction, @NonNull FrameLayout container,
      @NonNull ConstraintLayout noDocumentView, @NonNull Button openFile,
      @NonNull ImageView openFileIcon, @NonNull Guideline topGuide) {
    this.rootView = rootView;
    this.bottomGuide = bottomGuide;
    this.callToAction = callToAction;
    this.container = container;
    this.noDocumentView = noDocumentView;
    this.openFile = openFile;
    this.openFileIcon = openFileIcon;
    this.topGuide = topGuide;
  }

  @Override
  @NonNull
  public FrameLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static ActivityMainRealBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static ActivityMainRealBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.activity_main_real, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static ActivityMainRealBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.bottom_guide;
      Guideline bottomGuide = ViewBindings.findChildViewById(rootView, id);
      if (bottomGuide == null) {
        break missingId;
      }

      id = R.id.call_to_action;
      TextView callToAction = ViewBindings.findChildViewById(rootView, id);
      if (callToAction == null) {
        break missingId;
      }

      FrameLayout container = (FrameLayout) rootView;

      id = R.id.no_document_view;
      ConstraintLayout noDocumentView = ViewBindings.findChildViewById(rootView, id);
      if (noDocumentView == null) {
        break missingId;
      }

      id = R.id.open_file;
      Button openFile = ViewBindings.findChildViewById(rootView, id);
      if (openFile == null) {
        break missingId;
      }

      id = R.id.open_file_icon;
      ImageView openFileIcon = ViewBindings.findChildViewById(rootView, id);
      if (openFileIcon == null) {
        break missingId;
      }

      id = R.id.top_guide;
      Guideline topGuide = ViewBindings.findChildViewById(rootView, id);
      if (topGuide == null) {
        break missingId;
      }

      return new ActivityMainRealBinding((FrameLayout) rootView, bottomGuide, callToAction,
          container, noDocumentView, openFile, openFileIcon, topGuide);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}

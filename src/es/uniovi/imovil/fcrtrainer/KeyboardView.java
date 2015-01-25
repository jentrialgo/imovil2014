package es.uniovi.imovil.fcrtrainer;

import java.util.ArrayList;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.Editable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.view.View.OnClickListener;

/**
 * Esta vista se utiliza para gestionar una teclado que contiene los números
 * hexadecimales, el punto y una tecla para borrar. Debe incluirse un layout
 * llamado keyboard_panel de esta manera:
 * 
      <es.uniovi.imovil.fcrtrainer.KeyboardView
            android:id="@+id/keyboard"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_gravity="center" />

 * Si el layout padre incluye un EditText con identificador "text_view_answer",
 * el teclado se asigna automáticamente a ese identificador. Si no, se debe
 * utilizar la función assignTextView().
 * 
 * Si se añade la opción only_binary a true, sólo se generan teclas para binario
 */
public class KeyboardView extends LinearLayout implements OnClickListener {
	private EditText mEditText = null; // this receives the keystrokes
	private static String delChar = "◀";

	public KeyboardView(Context context, AttributeSet attrs) {
		super(context, attrs);

		int layoutId = getLayoutId(context, attrs);
		
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rootView = inflater.inflate(layoutId, this, true);

		if (isInEditMode()) {
			return;
		}

		ArrayList<View> allButtons = rootView.getTouchables();
		for (View v : allButtons) {
			v.setOnClickListener(this);
		}
		
		((Button) rootView.findViewById(R.id.key_delete)).setText(delChar);
	}

	private int getLayoutId(Context context, AttributeSet attrs) {
		 TypedArray a = context.getTheme().obtainStyledAttributes(
			        attrs,
			        R.styleable.KeyboardView,
			        0, 0);
		 
		 try {
			 boolean onlyBinary = a.getBoolean(
					 R.styleable.KeyboardView_only_binary, false);
			 if (onlyBinary) {
				 return R.layout.keyboard_binary_panel;
			 } else {
				 return R.layout.keyboard_full_panel;
			 }
		 } finally {
			 a.recycle();
		 }
	}

	public void assignEditText(EditText textView) {
		mEditText = textView;
	}
	
	@Override
	public void onClick(View view) {
		if (mEditText == null) {
			// No textView assigned. By default, we look for one with id answer
			View parent = (View) getParent();
			mEditText = (EditText) parent.findViewById(R.id.text_view_answer);
			if (mEditText == null) { // not found
				return;
			}
		}

		Button button = (Button) view;
		CharSequence keyPressed = button.getText();

		int start = mEditText.getSelectionStart();
		int end = mEditText.getSelectionEnd();
		int realStart = Math.min(start, end);
		int realEnd = Math.max(start, end);

		Editable text = mEditText.getEditableText();

		if (button.getId() == R.id.key_delete) {
			if (text.length() <= 0 || realStart-1 < 0)
				return;
			
			if (start == end) {
				text.delete(start-1, start);
			} else {
				text.delete(realStart, realEnd);
			}
		} else {
			text.replace(realStart, realEnd, 
					keyPressed, 0, keyPressed.length());
		}
	}
}

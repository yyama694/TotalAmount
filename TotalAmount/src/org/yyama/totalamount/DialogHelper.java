package org.yyama.totalamount;

import java.math.BigDecimal;
import java.util.Locale;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

public class DialogHelper {

	public static AlertDialog makeDialog(final MainActivity act) {
		final int scale;
		if (Locale.JAPAN.equals(Locale.getDefault())) {
			scale = 0;
		} else {
			scale = 2;
		}
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(act);
		alertDialogBuilder.setTitle(R.string.add);
		final EditText et = new EditText(act);
		// �����̂ݓ��͉\�B�h�l�d�ɂ���Ă͓��{�ꓙ�����͂ł��Ă��܂��B
		et.setInputType(InputType.TYPE_CLASS_NUMBER
				| InputType.TYPE_NUMBER_FLAG_DECIMAL);
		alertDialogBuilder.setView(et);
		alertDialogBuilder.setPositiveButton("OK",
				new AlertDialog.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						try {
							Double.parseDouble(et.getText().toString());
						} catch (NumberFormatException e) {
							return;
						}
						act.costList
								.add(new BigDecimal(et.getText().toString())
										.setScale(scale,
												BigDecimal.ROUND_HALF_UP));
						act.saveData();
						act.draw();
					}
				});

		final AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
			@Override
			public void onShow(DialogInterface dialog) {
				// �\�t�g�L�[�{�[�h���o��
				InputMethodManager inputMethodManager = (InputMethodManager) act
						.getSystemService(Context.INPUT_METHOD_SERVICE);
				inputMethodManager.showSoftInput(et, 0);
			}
		});

		et.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_DONE) {
					try {
						Double.parseDouble(et.getText().toString());
					} catch (NumberFormatException e) {
						alertDialog.dismiss();
						return false;
					}
					act.costList.add(new BigDecimal(et.getText().toString())
							.setScale(scale, BigDecimal.ROUND_HALF_UP));
					act.saveData();
					act.draw();
				}
				alertDialog.dismiss();
				return true;
			}
		});
		return alertDialog;
	}
}

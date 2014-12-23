package org.yyama.totalamount;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class LocalFileActivity extends Activity implements OnClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.select_file);
		initAdview();
		showFileList();
		findViewById(R.id.open_btn).setOnClickListener(this);
		findViewById(R.id.open_btn).setEnabled(false);
		findViewById(R.id.delete_btn).setOnClickListener(this);
		findViewById(R.id.delete_btn).setEnabled(false);
		findViewById(R.id.cancel_btn).setOnClickListener(this);
	}

	private AdView mAdView;

	private void initAdview() {
		// adView ���쐬����
		mAdView = new AdView(this);
		mAdView.setAdUnitId("ca-app-pub-2505812570403600/6940550178");
		mAdView.setAdSize(AdSize.BANNER);
		mAdView.setPadding(0, 0, 0, 5);
		// AdRequest adRequest = new
		// AdRequest.Builder().build();
		LinearLayout ll = (LinearLayout) findViewById(R.id.linear_layout3);
		ll.addView(mAdView);
		AdRequest adRequest = new AdRequest.Builder().addTestDevice(
				"2D6B2CDFA13324C63449E43857621522").build();
		mAdView.loadAd(adRequest);
	}

	private void showFileList() {
		String[] filesStr = fileList();
		final LinearLayout ll = (LinearLayout) findViewById(R.id.select_file_list);
		String fileTitle = null; // �t�@�C���\���p������
		for (int i = filesStr.length - 1; i >= 0; i--) {
			String s = filesStr[i];
			if (s.equals("sumFile.csv"))
				continue;
			fileTitle = " " + s.substring(3, 7) + "/" + s.substring(7, 9) + "/"
					+ s.substring(9, 11) + " " + s.substring(12, 14) + ":"
					+ s.substring(14, 16) + ":" + s.substring(16, 18) + " ";
			if (s.length() >= 23) {
				fileTitle += s.substring(19, s.length() - 4);
			}
			LayoutInflater lf = LayoutInflater.from(this);
			View v = lf.inflate(R.layout.list_view_row, null);
			TextView tv = (TextView) v.findViewById(R.id.row_textview1);
			tv.setText(fileTitle);
			TextView tv2 = (TextView) v.findViewById(R.id.row_textview2);
			tv2.setText(s);
			LinearLayout ll2 = (LinearLayout) v
					.findViewById(R.id.linear_layout2);
			ll2.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// ���ׂĂ̍s�̑I����������A�N���b�N�s��I����Ԃɕς���B
					for (int j = 0; j < ll.getChildCount(); j++) {
						LinearLayout t = (LinearLayout) ll.getChildAt(j);
						t.setSelected(false);
					}
					v.setSelected(true);
					findViewById(R.id.open_btn).setEnabled(true);
					findViewById(R.id.delete_btn).setEnabled(true);

				}
			});
			ll.addView(ll2);
		}

	}

	@Override
	public void onPause() {
		mAdView.pause();
		super.onPause();
	}

	@Override
	public void onResume() {
		super.onResume();
		mAdView.resume();
	}

	@Override
	public void onDestroy() {
		mAdView.destroy();
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		Intent intent = getIntent();
		switch (v.getId()) {
		case R.id.open_btn:
			// �@�t�@�C�����J������
			setResult(RESULT_OK, intent);
			intent.putExtra("file_name", getSelectFileName());
			intent.putExtra("file_title", getSelectFileTitle());

			finish();
			break;

		case R.id.delete_btn:
			// �t�@�C�����폜���鏈��
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setPositiveButton(R.string.del,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							deleteFile(getSelectFileName());
							LinearLayout ll = (LinearLayout) findViewById(R.id.select_file_list);
							ll.removeAllViews();
							findViewById(R.id.open_btn).setEnabled(false);
							findViewById(R.id.delete_btn).setEnabled(false);
							showFileList();
						}
					});
			builder.setNeutralButton(R.string.cancel,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {

						}
					});
			builder.setTitle(getString(R.string.delete_confirmation));
			TextView tv = new TextView(this);
			tv.setText(getSelectFileTitle());
			tv.setTextColor(Color.GRAY);
			builder.setView(tv);
			builder.create().show();

			break;
		case R.id.cancel_btn:
			// �t�@�C������L�����Z������
			setResult(RESULT_CANCELED, intent);
			finish();
			break;
		default:
			break;
		}
	}

	private String getSelectFileName() {
		LinearLayout ll = (LinearLayout) findViewById(R.id.select_file_list);
		for (int i = 0; i < ll.getChildCount(); i++) {
			if (ll.getChildAt(i).isSelected()) {
				LinearLayout ll2 = (LinearLayout) ll.getChildAt(i);
				return ((TextView) ll2.getChildAt(1)).getText().toString();
			}
		}
		return null;
	}

	private String getSelectFileTitle() {
		LinearLayout ll = (LinearLayout) findViewById(R.id.select_file_list);
		for (int i = 0; i < ll.getChildCount(); i++) {
			if (ll.getChildAt(i).isSelected()) {
				LinearLayout ll2 = (LinearLayout) ll.getChildAt(i);
				return ((TextView) ll2.getChildAt(0)).getText().toString();
			}
		}
		return null;
	}
}

package org.yyama.totalamount;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

public class MainActivity extends Activity implements OnClickListener {
	private AdView adView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// adView を作成する
		adView = new AdView(this);
		adView.setAdUnitId("ca-app-pub-2505812570403600/4533228970");
		adView.setAdSize(AdSize.BANNER);
		// AdRequest adRequest = new
		// AdRequest.Builder().build();
		AdRequest adRequest = new AdRequest.Builder().addTestDevice(
				"F3B1B2779DEF816F9B31AA6C6DC57C3F").build();
		RelativeLayout rl = (RelativeLayout) findViewById(R.id.RelativeLayout1);
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
				ViewGroup.LayoutParams.WRAP_CONTENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
		rl.addView(adView, lp);
		adView.loadAd(adRequest);

		Button btn = (Button) findViewById(R.id.button1);
		btn.setOnClickListener(this);
		btn = (Button) findViewById(R.id.button2);
		btn.setOnClickListener(this);
		loadData();
		draw();
	}

	public List<BigDecimal> costList = new ArrayList<BigDecimal>();

	private static final String FILE_NAME = "sumFile.csv";

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.button1:
			// ダイアログを表示
			DialogHelper.makeDialog(this).show();
			break;
		case R.id.button2:
			deleteFile(FILE_NAME);
			costList.clear();
			draw();
			break;
		}
	}

	private static final int WRAP_CONTENT = ViewGroup.LayoutParams.WRAP_CONTENT;
	private static final int MATCH_PARENT = ViewGroup.LayoutParams.MATCH_PARENT;

	public void draw() {
		loadData();

		LinearLayout lv = (LinearLayout) findViewById(R.id.linerLayout1);
		// lv.removeAllViews();
		while (lv.getChildCount() > 1) {
			Log.d("debug", String.valueOf(lv.getChildCount()));
			lv.removeViewAt(1);
		}
		BigDecimal sum = new BigDecimal("0");
		int cnt = 0;

		DecimalFormat df;
		if (Locale.JAPAN.equals(Locale.getDefault())) {
			// 日本語の処理
			df = new DecimalFormat("#,##0");
		} else {
			// 日本語以外の処理
			df = new DecimalFormat("#,##0.00");
		}
		for (BigDecimal i : costList) {
			final int cnt2 = cnt;
			cnt++;
			sum = sum.add(i);
			Log.d("fukuchi", "sum=" + sum.toString());
			TextView cost = new TextView(this);
			cost.setGravity(Gravity.RIGHT);
			cost.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 30);
			cost.setText(df.format(i));
			cost.setWidth(0);
			cost.setPadding(2, 2, 15, 2);
			// cost.setBackgroundColor(Color.RED);
			TextView tv = new TextView(this);
			tv.setWidth(0);
			tv.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
			tv.setText(String.valueOf(cnt));
			tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
			// tv.setBackgroundColor(Color.BLUE);

			TextView unit = new TextView(this);
			unit.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
			unit.setText(getString(R.string.unit) + " ");
			// unit.setBackgroundColor(Color.BLUE);

			Button btn = new Button(this);
			btn.setText(getString(R.string.del));
			btn.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10);
			btn.setPadding(5, 5, 5, 5);
			btn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					costList.remove(cnt2);
					saveData();
					draw();
				}
			});
			LinearLayout lh = new LinearLayout(this);
			lh.addView(tv, new LinearLayout.LayoutParams(WRAP_CONTENT,
					MATCH_PARENT, 0.1f));
			lh.addView(cost, new LinearLayout.LayoutParams(WRAP_CONTENT,
					WRAP_CONTENT, 0.88f));
			lh.addView(unit, new LinearLayout.LayoutParams(WRAP_CONTENT,
					WRAP_CONTENT, 0.01f));
			lh.addView(btn, new LinearLayout.LayoutParams(150, MATCH_PARENT,
					0.01f));
			lv.addView(lh, new LinearLayout.LayoutParams(MATCH_PARENT,
					WRAP_CONTENT));

		}
		((TextView) findViewById(R.id.textView4)).setText(" " + df.format(sum));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);

		return true;
	}

	@Override
	public void onPause() {
		adView.pause();
		super.onPause();
	}

	@Override
	public void onResume() {
		super.onResume();
		adView.resume();
	}

	@Override
	public void onDestroy() {
		adView.destroy();
		super.onDestroy();
	}

	public void saveData() {
		FileOutputStream fos = null;
		try {
			fos = openFileOutput(FILE_NAME, Context.MODE_PRIVATE);
			for (BigDecimal i : costList) {
				fos.write(String.valueOf(i).getBytes());
				fos.write(",".getBytes());
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void loadData() {
		FileInputStream fis = null;
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		try {
			byte[] b = new byte[1024];
			fis = openFileInput(FILE_NAME);
			while (true) {
				int len = fis.read(b);
				if (len < 0) {
					break;
				}
				bout.write(b, 0, len);
			}
			Log.d("test", bout.toString());
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		costList.clear();

		String[] arr = bout.toString().split(",");
		for (String s : arr) {
			if (s == null || s.equals("")) {
				continue;
			}
			costList.add(new BigDecimal(s));
		}
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		switch (id) {
		case R.id.file_save:
			TaFile.fileSave(this);
			break;
		case R.id.file_open:
			TaFile.fileLoad(this);
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == TaFile.REQUEST_CD) {
			switch (resultCode) {
			case RESULT_OK:
				InputStream in = null;
				BufferedReader br = null;
				try {
					in = openFileInput(data.getStringExtra("file_name"));
					br = new BufferedReader(new InputStreamReader(in));
					costList.clear();
					String tmp = br.readLine();
					if (tmp != null) {
						String[] strArr = tmp.split(",");
						for (String s : strArr) {
							if (s != null && !s.equals("")) {
								costList.add(new BigDecimal(s));
							}
						}
					}
					saveData();
					draw();
					Toast.makeText(
							this,
							getString(R.string.opend_file)
									+ System.lineSeparator()
									+ data.getStringExtra("file_title"),
							Toast.LENGTH_LONG).show();
				} catch (Exception e) {
					e.printStackTrace();
					Toast.makeText(this, getString(R.string.file_open_error),
							Toast.LENGTH_LONG).show();
				}

				break;
			case RESULT_CANCELED:
				break;
			default:
				break;
			}
		}
	}

}

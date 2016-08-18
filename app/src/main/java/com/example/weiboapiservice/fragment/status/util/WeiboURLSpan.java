package com.example.weiboapiservice.fragment.status.util;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Parcel;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.weiboapiservice.BaseApplication;
import com.example.weiboapiservice.R;
import com.thefinestartist.finestwebview.FinestWebView;

/**
 * User: qii
 * Date: 12-8-20
 */
public class WeiboURLSpan extends ClickableSpan {
    private final String mURL;
    private int color;

    public WeiboURLSpan(String url) {
        this.mURL = url;
    }

    public WeiboURLSpan(Parcel src) {
        this.mURL = src.readString();
    }

    public int getSpanTypeId() {
        return 11;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mURL);
    }

    public String getURL() {
        return this.mURL;
    }

    public void onClick(View widget) {
        Log.v(WeiboURLSpan.class.getSimpleName(), String.format("the link(%s) was clicked ", new Object[]{this.getURL()}));
        Uri uri = Uri.parse(this.getURL());
        Context context = widget.getContext();
        if (uri.getScheme().startsWith("http")) {
            new FinestWebView.Builder(context)
                    .theme(R.style.AppTheme)
                    .toolbarScrollFlags(0)
                    .titleColorRes(R.color.finestWhite)
                    .titleDefault("Website")
                    .iconDefaultColorRes(R.color.finestWhite)
                    .disableIconMenu(true)
                    .show(getURL());
         }else {
            Intent intent = new Intent("android.intent.action.VIEW", uri);
            intent.putExtra("com.android.browser.application_id", context.getPackageName());
            context.startActivity(intent);
//            intent = new Intent("android.intent.action.VIEW", uri);
//            intent.putExtra("com.android.browser.application_id", context.getPackageName());
//            context.startActivity(intent);
        }

    }

    public void onLongClick(View widget) {
        Uri data = Uri.parse(this.getURL());
        if (data != null) {
            String d = data.toString();
            String newValue = "";
            if (d.startsWith("org.aisen.android.ui")) {
                int cm = d.lastIndexOf("/");
                newValue = d.substring(cm + 1);
            } else if (d.startsWith("http")) {
                newValue = d;
            }
            if (!TextUtils.isEmpty(newValue)) {
                ClipboardManager cm1 = (ClipboardManager) widget.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                cm1.setPrimaryClip(ClipData.newPlainText("ui", newValue));
                Toast.makeText(widget.getContext(), "Coyp Success!", Toast.LENGTH_SHORT).show();
            }
        }

    }

    public void updateDrawState(TextPaint tp) {
        tp.setColor(BaseApplication.getInstance().getResources().getColor(R.color.colorNormalLinks));
    }

    public void setColor(int color) {
        this.color = color;
    }
}
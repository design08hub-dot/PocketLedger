package com.pocketledger.app;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import androidx.core.content.FileProvider;
import androidx.webkit.WebViewAssetLoader;
import java.io.File;

public class MainActivity extends Activity {
    private static final int REQ_FILE = 1, REQ_PERM = 2;
    private WebView web;
    private ValueCallback<Uri[]> pending;
    private Uri camUri;

    @Override protected void onCreate(Bundle b) {
        super.onCreate(b);
        web = new WebView(this);
        setContentView(web);
        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
            requestPermissions(new String[]{Manifest.permission.CAMERA}, REQ_PERM);

        WebSettings s = web.getSettings();
        s.setJavaScriptEnabled(true);
        s.setDomStorageEnabled(true);

        final WebViewAssetLoader loader = new WebViewAssetLoader.Builder()
            .addPathHandler("/assets/", new WebViewAssetLoader.AssetsPathHandler(this)).build();
        web.setWebViewClient(new WebViewClient() {
            @Override public WebResourceResponse shouldInterceptRequest(WebView v, WebResourceRequest r) {
                return loader.shouldInterceptRequest(r.getUrl());
            }
            @Override public boolean shouldOverrideUrlLoading(WebView v, WebResourceRequest r) {
                if ("appassets.androidplatform.net".equals(r.getUrl().getHost())) return false;
                startActivity(new Intent(Intent.ACTION_VIEW, r.getUrl()));
                return true;
            }
        });
        web.setWebChromeClient(new WebChromeClient() {
            @Override public boolean onShowFileChooser(WebView w, ValueCallback<Uri[]> cb, FileChooserParams p) {
                if (pending != null) pending.onReceiveValue(null);
                pending = cb;
                openChooser();
                return true;
            }
        });
        web.loadUrl("https://appassets.androidplatform.net/assets/index.html");
    }

    private void openChooser() {
        Intent pick = new Intent(Intent.ACTION_GET_CONTENT);
        pick.addCategory(Intent.CATEGORY_OPENABLE);
        pick.setType("image/*");
        Intent chooser = Intent.createChooser(pick, "Bill photo");
        camUri = null;
        if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            try {
                File dir = new File(getCacheDir(), "photos");
                dir.mkdirs();
                File f = File.createTempFile("bill", ".jpg", dir);
                camUri = FileProvider.getUriForFile(this, "com.pocketledger.app.files", f);
                Intent cam = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                cam.putExtra(MediaStore.EXTRA_OUTPUT, camUri);
                cam.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{cam});
            } catch (Exception ignored) { camUri = null; }
        }
        try { startActivityForResult(chooser, REQ_FILE); }
        catch (Exception e) { pending.onReceiveValue(null); pending = null; }
    }

    @Override protected void onActivityResult(int req, int res, Intent data) {
        super.onActivityResult(req, res, data);
        if (req == REQ_FILE && pending != null) {
            Uri[] r = null;
            if (res == RESULT_OK) {
                if (data != null && data.getData() != null) r = new Uri[]{data.getData()};
                else if (camUri != null) r = new Uri[]{camUri};
            }
            pending.onReceiveValue(r);
            pending = null;
        }
    }
}

package ist.tec.launcher

import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.webkit.WebViewClient

class MainActivity : AppCompatActivity() {
    private lateinit var webView: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        webView = findViewById(R.id.webview)

        val webSettings = webView.settings
        webSettings.javaScriptEnabled = true
        
        webView.addJavascriptInterface(AndroidBridge(this), "Android")

        // Check index.html is loaded
        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)

                // Get a list of installed apps
                val intent = Intent(Intent.ACTION_MAIN, null)
                intent.addCategory(Intent.CATEGORY_LAUNCHER)
                val packageManager : PackageManager = packageManager
                val apps = applicationContext.packageManager.queryIntentActivities(intent, 0)

                for (app in apps) {
                    val appName = app.activityInfo.loadLabel(packageManager).toString()
                    val appPackageName = app.activityInfo.packageName.toString()
                    val js = """appToAppend = document.createElement('div');
                        appToAppend.setAttribute('class', 'app');
                        |appToAppend.setAttribute('id', '$appPackageName');
                        |appToAppend.innerHTML = '$appName';
                        |appToAppend.addEventListener('click', function() { Android.launchApp('$appPackageName'); });
                        |document.getElementById('appList').appendChild(appToAppend);
                    """.trimMargin()
                    webView.evaluateJavascript(js, null)
                }
            }
        }
        webView.loadUrl("file:///android_asset/index.html")
    }
}

class AndroidBridge(private val activity: MainActivity) {
    @JavascriptInterface
    fun launchApp(packageName: String) {
        val intent = activity.packageManager.getLaunchIntentForPackage(packageName)
        activity.startActivity(intent)
    }
}

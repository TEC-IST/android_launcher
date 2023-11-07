# android_launcher

https://m.youtube.com/watch?v=KaFJVg5vmRA

This launcher uses Android Webview for its UI, which allows the user full control over their home screen via HTML/CSS/Javascript.  It does not run in full screen mode, so the default notifications bar and back/home/task switch navigation is preserved.  In order to set this as your launcher, you might need to go to: Settings --> Apps --> Default apps --> Home app

From the Kotlin app side of things, the application can inject Javascript (and therefore add/modify dom elements) via webView.evaluateJavascript("your javascript here", null).  For example, that's how it adds the app list:

```
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
```

From the Webview UI side of things, Javascript can call functions in the form of Android.yourFunction(yourParameters) via a @JavascriptInterface bridge.  For example, that's how it makes the apps in the app list launchable:

```
class AndroidBridge(private val activity: MainActivity) {
    @JavascriptInterface
    fun launchApp(packageName: String) {
        val intent = activity.packageManager.getLaunchIntentForPackage(packageName)
        activity.startActivity(intent)
    }
}
```

The default look has a black background to minimize screen battery usage on OLED devices.

```
body {
    background-color: black; /* background color CSS name or #hex */
    color: #fff; /* Text color CSS name or #hex */
    font-family: Arial, sans-serif;
    margin: 0;
    padding: 0;
}
```
...
```
<body>
<div class="container">
    <h1>Welcome to the TEC.IST Launcher</h1>
    <p>Add any web content you like here</p>
    <div id="appList"></div>
</div>
<script>
</script>
</body>
```

![image](https://github.com/TEC-IST/android_launcher/blob/main/screenshots/screenshot.png)

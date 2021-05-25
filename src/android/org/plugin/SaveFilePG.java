package org.plugin;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;

import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import java.io.IOException;

public class SaveFilePG extends CordovaPlugin {
  private static final String TAG = "SaveFilePG";

  public void initialize(CordovaInterface cordova, CordovaWebView webView) {
    super.initialize(cordova, webView);
    Log.d(TAG, "Inicializando SaveFilePG");
  }

  @RequiresApi(api = Build.VERSION_CODES.Q)
  public boolean execute(String action, JSONArray args, final CallbackContext callbackContext) throws JSONException {
    String mensaje = "";
    PluginResult result;
    if ("saveFile".equals(action)) {
        mensaje = saveFile(args);
    }
    result = new PluginResult(PluginResult.Status.OK, mensaje);
    callbackContext.sendPluginResult(result);
    return true;
  }

  @RequiresApi(api = Build.VERSION_CODES.Q)
  public String saveFile(JSONArray args) throws JSONException {
    String ret = "";
    String filename = args.getString(0);
    String texto = args.getString(1);
    try {
      Uri uri = new org.plugin.FileUtils().generatePdf(webView.getContext(), texto, filename);

      if (uri != null) {
        ret = "El documento pdf se ha guardado correctamente.";
      } else {
        ret = "Ocurrió un inconveniente mientras se generaba el documento.";
      }
    } catch (IOException e) {
      ret = "Ocurrió un inconveniente mientras se generaba el documento.";
    }
    return ret;
  }
}

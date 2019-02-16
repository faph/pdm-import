/* 
 * The MIT License
 *
 * Copyright 2008-2019 Florenz A. P. Hollebrandse.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package hec.dssgui.plugins;

import java.util.prefs.Preferences;

public class PreferencesHandler {

   public static void setPreference(String keyName, String keyValue) {
      Preferences prefs = Preferences.userNodeForPackage(hec.dssgui.plugins.PdmDialog.class);

      final String PREF_NAME = keyName;
      String newValue = keyValue;

      prefs.put(PREF_NAME, newValue);
   }

   public static void setPreference(String keyName, int keyValue) {
      Preferences prefs = Preferences.userNodeForPackage(hec.dssgui.plugins.PdmDialog.class);

      final String PREF_NAME = keyName;
      int newValue = keyValue;

      prefs.putInt(PREF_NAME, newValue);
   }

   public static String getPreference(String keyName) {
      Preferences prefs = Preferences.userNodeForPackage(hec.dssgui.plugins.PdmDialog.class);

      final String PREF_NAME = keyName;
      String defaultValue = "";

      String propertyValue = prefs.get(PREF_NAME, defaultValue);
      return propertyValue;
   }
    
   public static int getPreferenceInt(String keyName) {
      Preferences prefs = Preferences.userNodeForPackage(hec.dssgui.plugins.PdmDialog.class);

      final String PREF_NAME = keyName;
      int defaultValue = 0;

      int propertyValue = prefs.getInt(PREF_NAME, defaultValue);
      return propertyValue;
   }
}


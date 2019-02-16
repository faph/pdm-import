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

import java.util.ArrayList;
import java.util.List;
import javax.swing.JComboBox;

public class InpsHandler {

   private List inpList;
   private static int shortFileLength;
   private static JComboBox comboBox;

   public void PushToCombo() {
      int fileIndex;
      String displayFile;

      comboBox.removeAllItems();
      for (fileIndex = 0; fileIndex < inpList.size(); fileIndex++) {
         displayFile = (String) inpList.get(fileIndex);

         if (displayFile.length() > shortFileLength) {
            displayFile = displayFile.substring(0, 3) + "..." +
                    displayFile.substring(displayFile.length() - (shortFileLength - 6));
         }
         comboBox.addItem(displayFile);
      }
   }

   public void addInpFile(String folder) {
      int fileIndex;

      if (!inpList.contains(folder)) {
         inpList.add(0, folder); //add in first row
         if (inpList.size() == 11) { //if list longer than 10 remove row 11
            inpList.remove(10);
         }
      } else {
         int existingFileIndex = inpList.indexOf(folder);
         for (fileIndex = existingFileIndex; fileIndex > 0; fileIndex--) {
            inpList.set(fileIndex, inpList.get(fileIndex - 1)); //move all folders above the existing folder one row donw
         }
         inpList.set(0, folder); //move existing folder to one
      }
      PushToCombo();
      saveInpFiles();
   }

   public void moveToTop(int selectedFileIndex) {
      int fileIndex;
      Object selectedFile = inpList.get(selectedFileIndex);

      for (fileIndex = selectedFileIndex; fileIndex > 0; fileIndex--) {
         inpList.set(fileIndex, inpList.get(fileIndex - 1)); //move all folders above the existing folder one row donw
      }
      inpList.set(0, selectedFile); //move existing folder to one
      PushToCombo();
      saveInpFiles();
   }

   public void saveInpFiles() {
      int fileIndex;

      for (fileIndex = 0; fileIndex < inpList.size(); fileIndex++) {
         PreferencesHandler.setPreference("inpFile" + fileIndex, (String) inpList.get(fileIndex));
      }
   }

   public String getInpFile(int fileIndex) {
      String result;

      if (fileIndex < inpList.size()) {
         result = (String) inpList.get(fileIndex);
      } else {
         result = (String) inpList.get(0);
      }
      return result;
   }

   /** Creates a new instance of ExportFoldersHandler */
   public InpsHandler(JComboBox combo) {
      int fileIndex;

      shortFileLength = 50;
      comboBox = combo;

      inpList = new ArrayList();

      fileIndex = 0;
      while ((PreferencesHandler.getPreference("inpFile" + fileIndex)) != "") {
         inpList.add(PreferencesHandler.getPreference("inpFile" + fileIndex));
         fileIndex++;
      }
      PushToCombo();
   }
}

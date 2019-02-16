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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JOptionPane;
import javax.swing.UIManager;

import hec.dssgui.ListSelection;
import rma.awt.FlatPanelButton;

public class ControlPdmPlugin {

   private static ListSelection _listSelection;

   public static void main(Object[] args) {
      if (args.length > 0 && args[0] instanceof ListSelection) {
         _listSelection = (ListSelection) args[0];
      }
      UIManager.put("TextField.disabledBackground", UIManager.get("TextField.inactiveBackground"));

      final ListSelection listSelection = (ListSelection) args[0];
      FlatPanelButton pdmButton = new FlatPanelButton("PDM import...");
      pdmButton.addActionListener(new ActionListener() {

         public void actionPerformed(ActionEvent e) {
            String currentDirectory = listSelection.getDSSFilename();
            if (currentDirectory == null || currentDirectory.length() == 0) {
               JOptionPane.showMessageDialog(listSelection, "Please select a DSS file",
                       "No file selected", JOptionPane.WARNING_MESSAGE);
               return;
            }

            PdmDialog pdmDialog = new PdmDialog(_listSelection, true);
            pdmDialog.setLocationRelativeTo(listSelection);
            pdmDialog.setVisible(true);

         }
      });
      listSelection.getToolBar().add(pdmButton);
   }
}

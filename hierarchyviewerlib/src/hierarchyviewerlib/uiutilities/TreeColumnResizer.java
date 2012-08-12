/*
 * Copyright (c) Shanghai Zhiping Technology Co.,Limited
 * Author: Binhua Liu
 * Web Site: www.vowei.com
 * License: GPL v3 (http://www.gnu.org/copyleft/gpl.html)
 * A Part of source code come from "Android Open Source Project" 
 */

package hierarchyviewerlib.uiutilities;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TreeColumn;

public class TreeColumnResizer {

    private TreeColumn mColumn1;

    private TreeColumn mColumn2;

    private Composite mControl;

    private int mColumn1Width;

    private int mColumn2Width;

    private final static int MIN_COLUMN1_WIDTH = 18;

    private final static int MIN_COLUMN2_WIDTH = 3;

    public TreeColumnResizer(Composite control, TreeColumn column1, TreeColumn column2) {
        this.mControl = control;
        this.mColumn1 = column1;
        this.mColumn2 = column2;
        control.addListener(SWT.Resize, resizeListener);
        column1.addListener(SWT.Resize, column1ResizeListener);
        column2.setResizable(false);
    }

    private Listener resizeListener = new Listener() {
        public void handleEvent(Event e) {
            if (mColumn1Width == 0 && mColumn2Width == 0) {
                mColumn1Width = (mControl.getBounds().width - 18) / 2;
                mColumn2Width = (mControl.getBounds().width - 18) / 2;
            } else {
                int dif = mControl.getBounds().width - 18 - (mColumn1Width + mColumn2Width);
                int columnDif = Math.abs(mColumn1Width - mColumn2Width);
                int mainColumnChange = Math.min(Math.abs(dif), columnDif);
                int left = Math.max(0, Math.abs(dif) - columnDif);
                if (dif < 0) {
                    if (mColumn1Width > mColumn2Width) {
                        mColumn1Width -= mainColumnChange;
                    } else {
                        mColumn2Width -= mainColumnChange;
                    }
                    mColumn1Width -= left / 2;
                    mColumn2Width -= left - left / 2;
                } else {
                    if (mColumn1Width > mColumn2Width) {
                        mColumn2Width += mainColumnChange;
                    } else {
                        mColumn1Width += mainColumnChange;
                    }
                    mColumn1Width += left / 2;
                    mColumn2Width += left - left / 2;
                }
            }
            mColumn1.removeListener(SWT.Resize, column1ResizeListener);
            mColumn1.setWidth(mColumn1Width);
            mColumn2.setWidth(mColumn2Width);
            mColumn1.addListener(SWT.Resize, column1ResizeListener);
        }
    };

    private Listener column1ResizeListener = new Listener() {
        public void handleEvent(Event e) {
            int widthDif = mColumn1Width - mColumn1.getWidth();
            mColumn1Width -= widthDif;
            mColumn2Width += widthDif;
            boolean column1Changed = false;

            // Strange, but these constants make the columns look the same.

            if (mColumn1Width < MIN_COLUMN1_WIDTH) {
                mColumn2Width -= MIN_COLUMN1_WIDTH - mColumn1Width;
                mColumn1Width += MIN_COLUMN1_WIDTH - mColumn1Width;
                column1Changed = true;
            }
            if (mColumn2Width < MIN_COLUMN2_WIDTH) {
                mColumn1Width += mColumn2Width - MIN_COLUMN2_WIDTH;
                mColumn2Width = MIN_COLUMN2_WIDTH;
                column1Changed = true;
            }
            if (column1Changed) {
                mColumn1.removeListener(SWT.Resize, this);
                mColumn1.setWidth(mColumn1Width);
                mColumn1.addListener(SWT.Resize, this);
            }
            mColumn2.setWidth(mColumn2Width);
        }
    };
}

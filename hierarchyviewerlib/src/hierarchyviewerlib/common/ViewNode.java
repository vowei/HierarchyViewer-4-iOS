/*
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package hierarchyviewerlib.common;

import org.eclipse.swt.graphics.Image;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class ViewNode {

    //public static enum ProfileRating {
    //    RED, YELLOW, GREEN, NONE
    //};

    //private static final double RED_THRESHOLD = 0.8;

    //private static final double YELLOW_THRESHOLD = 0.5;
	
	public String path="";

    public static final String MISCELLANIOUS = "miscellaneous";

    public String id="NO_ID";

    //public String name;
    
    public String type;
    
    //type with prefix, for android
    public String fullType;

    //public String hashCode;

    public List<Property> properties = new ArrayList<Property>();

    public Map<String, Property> namedProperties = new HashMap<String, Property>();

    public ViewNode parent;

    public List<ViewNode> children = new ArrayList<ViewNode>();

    public int left;

    public int top;

    public int width;

    public int height;

    public int scrollX=0;

    public int scrollY=0;

    public int paddingLeft=0;

    public int paddingRight=0;

    public int paddingTop=0;

    public int paddingBottom=0;

    public int marginLeft=0;

    public int marginRight=0;

    public int marginTop=0;

    public int marginBottom=0;

    public int baseline=0;

    public boolean willNotDraw=false;

    public boolean hasMargins=false;

    public boolean hasFocus=false;

    public int index;

    public int elementIndex;
    
    public boolean addedToMap=false;
    
    //public double measureTime;

    //public double layoutTime;

    //public double drawTime;

    //public ProfileRating measureRating = ProfileRating.NONE;

    //public ProfileRating layoutRating = ProfileRating.NONE;

    //public ProfileRating drawRating = ProfileRating.NONE;

    public Set<String> categories = new TreeSet<String>();

    public Image image;

    public int imageReferences = 1;

    public int viewCount;

    public boolean filtered;

    public int protocolVersion;

    public ViewNode()
    {
    	
    }
    
/*    public ViewNode(ViewNode parent, String data) {
        this.parent = parent;
        index = this.parent == null ? 0 : this.parent.children.size();
        if (this.parent != null) {
            this.parent.children.add(this);
        }
        int delimIndex = data.indexOf('@');
        name = data.substring(0, delimIndex);
        data = data.substring(delimIndex + 1);
        delimIndex = data.indexOf(' ');
        //hashCode = data.substring(0, delimIndex);
        loadProperties(data.substring(delimIndex + 1).trim());

        //measureTime = -1;
        //layoutTime = -1;
        //drawTime = -1;
    }*/
    

    public void dispose() {
        final int N = children.size();
        for (int i = 0; i < N; i++) {
            children.get(i).dispose();
        }
        dereferenceImage();
    }

    public void referenceImage() {
        imageReferences++;
    }

    public void dereferenceImage() {
        imageReferences--;
        if (image != null && imageReferences == 0) {
            image.dispose();
        }
    }

/*    private void loadProperties(String data) {
        int start = 0;
        boolean stop;
        do {
            int index = data.indexOf('=', start);
            ViewNode.Property property = new ViewNode.Property();
            property.name = data.substring(start, index);

            int index2 = data.indexOf(',', index + 1);
            int length = Integer.parseInt(data.substring(index + 1, index2));
            start = index2 + 1 + length;
            property.value = data.substring(index2 + 1, index2 + 1 + length);

            properties.add(property);
            namedProperties.put(property.name, property);

            stop = start >= data.length();
            if (!stop) {
                start += 1;
            }
        } while (!stop);

        Collections.sort(properties, new Comparator<ViewNode.Property>() {
            public int compare(ViewNode.Property source, ViewNode.Property destination) {
                return source.name.compareTo(destination.name);
            }
        });

        id = namedProperties.get("mID").value; //$NON-NLS-1$

        left =
 namedProperties.containsKey("mLeft") ? getInt("mLeft", 0) : getInt("layout:mLeft", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                        0);
        top = namedProperties.containsKey("mTop") ? getInt("mTop", 0) : getInt("layout:mTop", 0); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        width =
                namedProperties.containsKey("getWidth()") ? getInt("getWidth()", 0) : getInt( //$NON-NLS-1$ //$NON-NLS-2$
                        "layout:getWidth()", 0); //$NON-NLS-1$
        height =
                namedProperties.containsKey("getHeight()") ? getInt("getHeight()", 0) : getInt( //$NON-NLS-1$ //$NON-NLS-2$
                        "layout:getHeight()", 0); //$NON-NLS-1$
        scrollX =
                namedProperties.containsKey("mScrollX") ? getInt("mScrollX", 0) : getInt( //$NON-NLS-1$ //$NON-NLS-2$
                        "scrolling:mScrollX", 0); //$NON-NLS-1$
        scrollY =
                namedProperties.containsKey("mScrollY") ? getInt("mScrollY", 0) : getInt( //$NON-NLS-1$ //$NON-NLS-2$
                        "scrolling:mScrollY", 0); //$NON-NLS-1$
        paddingLeft =
                namedProperties.containsKey("mPaddingLeft") ? getInt("mPaddingLeft", 0) : getInt( //$NON-NLS-1$ //$NON-NLS-2$
                        "padding:mPaddingLeft", 0); //$NON-NLS-1$
        paddingRight =
                namedProperties.containsKey("mPaddingRight") ? getInt("mPaddingRight", 0) : getInt( //$NON-NLS-1$ //$NON-NLS-2$
                        "padding:mPaddingRight", 0); //$NON-NLS-1$
        paddingTop =
                namedProperties.containsKey("mPaddingTop") ? getInt("mPaddingTop", 0) : getInt( //$NON-NLS-1$ //$NON-NLS-2$
                        "padding:mPaddingTop", 0); //$NON-NLS-1$
        paddingBottom =
                namedProperties.containsKey("mPaddingBottom") ? getInt("mPaddingBottom", 0) //$NON-NLS-1$ //$NON-NLS-2$
                        : getInt("padding:mPaddingBottom", 0); //$NON-NLS-1$
        marginLeft =
                namedProperties.containsKey("layout_leftMargin") ? getInt("layout_leftMargin", //$NON-NLS-1$ //$NON-NLS-2$
                        Integer.MIN_VALUE) : getInt("layout:layout_leftMargin", Integer.MIN_VALUE); //$NON-NLS-1$
        marginRight =
                namedProperties.containsKey("layout_rightMargin") ? getInt("layout_rightMargin", //$NON-NLS-1$ //$NON-NLS-2$
                        Integer.MIN_VALUE) : getInt("layout:layout_rightMargin", Integer.MIN_VALUE); //$NON-NLS-1$
        marginTop =
                namedProperties.containsKey("layout_topMargin") ? getInt("layout_topMargin", //$NON-NLS-1$ //$NON-NLS-2$
                        Integer.MIN_VALUE) : getInt("layout:layout_topMargin", Integer.MIN_VALUE); //$NON-NLS-1$
        marginBottom =
                namedProperties.containsKey("layout_bottomMargin") ? getInt("layout_bottomMargin", //$NON-NLS-1$ //$NON-NLS-2$
                        Integer.MIN_VALUE)
                        : getInt("layout:layout_bottomMargin", Integer.MIN_VALUE); //$NON-NLS-1$
        baseline =
                namedProperties.containsKey("getBaseline()") ? getInt("getBaseline()", 0) : getInt( //$NON-NLS-1$ //$NON-NLS-2$
                        "layout:getBaseline()", 0); //$NON-NLS-1$
        willNotDraw =
                namedProperties.containsKey("willNotDraw()") ? getBoolean("willNotDraw()", false) //$NON-NLS-1$ //$NON-NLS-2$
                        : getBoolean("drawing:willNotDraw()", false); //$NON-NLS-1$
        hasFocus =
                namedProperties.containsKey("hasFocus()") ? getBoolean("hasFocus()", false) //$NON-NLS-1$ //$NON-NLS-2$
                        : getBoolean("focus:hasFocus()", false); //$NON-NLS-1$

        hasMargins =
                marginLeft != Integer.MIN_VALUE && marginRight != Integer.MIN_VALUE
                        && marginTop != Integer.MIN_VALUE && marginBottom != Integer.MIN_VALUE;

        for (String name : namedProperties.keySet()) {
            int index = name.indexOf(':');
            if (index != -1) {
                categories.add(name.substring(0, index));
            }
        }
        if (categories.size() != 0) {
            categories.add(MISCELLANIOUS);
        }
    }*/

    //public void setProfileRatings() {
        //final int N = children.size();
        //if (N > 1) {
            //double totalMeasure = 0;
            //double totalLayout = 0;
            //double totalDraw = 0;
            //for (int i = 0; i < N; i++) {
               // ViewNode child = children.get(i);
                //totalMeasure += child.measureTime;
                //totalLayout += child.layoutTime;
                //totalDraw += child.drawTime;
            //}
            //for (int i = 0; i < N; i++) {
               // ViewNode child = children.get(i);
                //if (child.measureTime / totalMeasure >= RED_THRESHOLD) {
                    //child.measureRating = ProfileRating.RED;
                //} else if (child.measureTime / totalMeasure >= YELLOW_THRESHOLD) {
                //    child.measureRating = ProfileRating.YELLOW;
                //} else {
                //    child.measureRating = ProfileRating.GREEN;
                //}
                //if (child.layoutTime / totalLayout >= RED_THRESHOLD) {
                //    child.layoutRating = ProfileRating.RED;
                //} else if (child.layoutTime / totalLayout >= YELLOW_THRESHOLD) {
                //    child.layoutRating = ProfileRating.YELLOW;
                //} else {
                //    child.layoutRating = ProfileRating.GREEN;
                //}
                //if (child.drawTime / totalDraw >= RED_THRESHOLD) {
                //    child.drawRating = ProfileRating.RED;
                //} else if (child.drawTime / totalDraw >= YELLOW_THRESHOLD) {
                //    child.drawRating = ProfileRating.YELLOW;
                //} else {
                //    child.drawRating = ProfileRating.GREEN;
                //}
            //}
        //}
        //for (int i = 0; i < N; i++) {
        //    children.get(i).setProfileRatings();
        //}
    //}

    public void setViewCount() {
        viewCount = 1;
        final int N = children.size();
        for (int i = 0; i < N; i++) {
            ViewNode child = children.get(i);
            child.setViewCount();
            viewCount += child.viewCount;
        }
    }

    public void filter(String text) {
        int dotIndex = type.lastIndexOf('.');
        String shortName = (dotIndex == -1) ? type : type.substring(dotIndex + 1);
        filtered =
                !text.equals("") //$NON-NLS-1$
                        && (shortName.toLowerCase().contains(text.toLowerCase()) || (!id
                                .equals("NO_ID") && id.toLowerCase().contains(text.toLowerCase()))); //$NON-NLS-1$
        final int N = children.size();
        for (int i = 0; i < N; i++) {
            children.get(i).filter(text);
        }
    }

    /*private boolean getBoolean(String name, boolean defaultValue) {
        Property p = namedProperties.get(name);
        if (p != null) {
            try {
                return Boolean.parseBoolean(p.value);
            } catch (NumberFormatException e) {
                return defaultValue;
            }
        }
        return defaultValue;
    }

    private int getInt(String name, int defaultValue) {
        Property p = namedProperties.get(name);
        if (p != null) {
            try {
                return Integer.parseInt(p.value);
            } catch (NumberFormatException e) {
                return defaultValue;
            }
        }
        return defaultValue;
    }*/

    @Override
    public String toString() {
        return type; //+ "@" + hashCode; //$NON-NLS-1$
    }

    public static class Property {
        public String name;

        public String value;

        @Override
        public String toString() {
            return name + '=' + value;
        }
    }
    
}

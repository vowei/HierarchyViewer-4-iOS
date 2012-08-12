/*
 * Copyright (c) Shanghai Zhiping Technology Co.,Limited
 * Writer: Binhua Liu
 * Web Site: www.vowei.com
 * License: GPL v3 (http://www.gnu.org/copyleft/gpl.html)
 * A Part of source code come from "Android Open Source Project" 
 */

package hierarchyviewerlib.test;

import hierarchyviewerlib.common.ViewNode;
import hierarchyviewerlib.models.TreeViewModel;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.antlr.runtime.RecognitionException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.osgi.framework.Bundle;

import cc.iqa.iquery.IPseudoAttribute;
import cc.iqa.iquery.IPseudoClass;
import cc.iqa.iquery.ITreeNode;
import cc.iqa.iquery.iQueryIdeParser;
import cc.iqa.iquery.iQueryParser;

public class AllTests {
	
	static String sNodeName="UIATarget- name-iPhone Simulator rect-{{0, 0}, {320, 480}}";
	//static String sLogPath="/home/binhua/Result/Automation Results.plist";
	static String sLogPath;
	
	static
	{
		Bundle bundle = Platform.getBundle("hierarchyviewerlib.test"); 
		URL url = bundle.getResource("Logs/Automation Results.plist");
		URL fileURL;
		try {
			fileURL = FileLocator.toFileURL(url);
			sLogPath=fileURL.getPath();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	private static void registerPseudoAttributes(iQueryParser parser) {
		parser.registerPseudoAttribute("top", new IPseudoAttribute() {
			public String resolve(ITreeNode node) {
				return node.getProperty(":top").getValue();
			}
		});
		parser.registerPseudoAttribute("left", new IPseudoAttribute() {
			public String resolve(ITreeNode node) {
				return node.getProperty(":left").getValue();
			}
		});
		parser.registerPseudoAttribute("bottom", new IPseudoAttribute() {
			public String resolve(ITreeNode node) {
				return node.getProperty(":bottom").getValue();
			}
		});
		parser.registerPseudoAttribute("right", new IPseudoAttribute() {
			public String resolve(ITreeNode node) {
				return node.getProperty(":right").getValue();
			}
		});
		parser.registerPseudoAttribute("width", new IPseudoAttribute() {
			public String resolve(ITreeNode node) {
				return node.getProperty(":width").getValue();
			}
		});
		parser.registerPseudoAttribute("height", new IPseudoAttribute() {
			public String resolve(ITreeNode node) {
				return node.getProperty(":height").getValue();
			}
		});
	}
	
	@Before
	public void before() throws Exception
	{
		
	}

	public List<ITreeNode> getRoot()
	{
		TreeViewModel model = TreeViewModel.getModel();
		model.loadLogFile(sLogPath);
		model.setCurrentData(sNodeName);
		ViewNode viewNode=model.getTree().viewNode;
		List<ITreeNode> candidates=  new ArrayList<ITreeNode>();
		candidates.add(viewNode);
		return candidates;
	}
	
	//测试对UIAElement的扩展
	@Test
	public void Test1() throws IOException, RecognitionException
	{
		iQueryParser parser = iQueryIdeParser.createParser("> [value >= 59%]");
		List<ITreeNode> candidates=  getRoot();
		List<ITreeNode> outTreeNodeList = parser.query(candidates);
		//List<String> errors= parser.getErrors();
		Assert.assertTrue( outTreeNodeList.size()==2);
		
		parser = iQueryIdeParser.createParser("> :button");
		ITreeNode segmentedControl= candidates.get(0).getChildren().get(3);
		List<ITreeNode> candidates1=new ArrayList<ITreeNode>();
		candidates1.add(segmentedControl);
		outTreeNodeList = parser.query(candidates1);
		Assert.assertTrue( outTreeNodeList.size()==3);
	}
	
	//"([attr (>|<|>=|<=) float])比较属性值"
	@Test
	public void Test2() throws IOException, RecognitionException
	{
		iQueryParser parser = iQueryIdeParser.createParser("> [value >= 59%]");
		List<ITreeNode> candidates=  getRoot();
		List<ITreeNode> outTreeNodeList = parser.query(candidates);
		//List<String> errors= parser.getErrors();
		Assert.assertTrue( outTreeNodeList.size()==2);
		
		parser = iQueryIdeParser.createParser("> [value >= 0.59]");
		outTreeNodeList = parser.query(candidates);
		Assert.assertTrue( outTreeNodeList.size()==2);
		
		parser = iQueryIdeParser.createParser("> [:height > 31]", false);
		registerPseudoAttributes(parser);
		outTreeNodeList = parser.query(candidates);
		Assert.assertTrue( outTreeNodeList.size()==6);
		
		for(ITreeNode treeNode:outTreeNodeList)
		{
			String value = treeNode.getProperty(":height").getValue();
			Assert.assertTrue( value!=null);
			int height= Integer.parseInt(value);
			Assert.assertTrue( height>31);
		}
		
		parser = iQueryIdeParser.createParser("> [:height >= 31]", false);
		registerPseudoAttributes(parser);
		outTreeNodeList = parser.query(candidates);
		Assert.assertTrue( outTreeNodeList.size()==8);
		
		for(ITreeNode treeNode:outTreeNodeList)
		{
			String value = treeNode.getProperty(":height").getValue();
			Assert.assertTrue( value!=null);
			int height= Integer.parseInt(value);
			Assert.assertTrue( height>=31);
		}
		
		parser = iQueryIdeParser.createParser("> [:height < 31]", false);
		registerPseudoAttributes(parser);
		outTreeNodeList = parser.query(candidates);
		Assert.assertTrue( outTreeNodeList.size()==2);
		
		for(ITreeNode treeNode:outTreeNodeList)
		{
			String value = treeNode.getProperty(":height").getValue();
			Assert.assertTrue( value!=null);
			int height= Integer.parseInt(value);
			Assert.assertTrue( height<31);
		}
		
		
		parser = iQueryIdeParser.createParser("> [:height <= 31]", false);
		registerPseudoAttributes(parser);
		outTreeNodeList = parser.query(candidates);
		Assert.assertTrue( outTreeNodeList.size()==4);
		
		for(ITreeNode treeNode:outTreeNodeList)
		{
			String value = treeNode.getProperty(":height").getValue();
			Assert.assertTrue( value!=null);
			int height= Integer.parseInt(value);
			Assert.assertTrue( height<=31);
		}
	}
	
	//"测试parseNum函数"
	@Test
	public void Test3()
	{
/*	    Assert.assertTrue(.Equals(0.59, parseNum("59%"));
	    Assert.assertTrue(.Equals(0.59, parseNum(0.59));
	    Assert.assertTrue(.Equals(0.59, parseNum("0.59"));
	    Assert.assertTrue(.Equals(59, parseNum("59"));
	    Assert.assertTrue(.Equals(0.0059, parseNum("0.59%"));
	    Assert.assertTrue(.Equals(0.59, parseNum("59%  "));
	    Assert.assertTrue(.Equals(0.59, parseNum("   59%"));
	    Assert.assertTrue(.Equals(59.59, parseNum("59.59"));*/
	}
	
	//"[:attr = float])匹配伪属性"
	@Test
	public void Test4() throws IOException, RecognitionException
	{
		iQueryParser parser = iQueryIdeParser.createParser("> [:height = 31]", false);
		registerPseudoAttributes(parser);
		List<ITreeNode> candidates=  getRoot();
		List<ITreeNode> outTreeNodeList = parser.query(candidates);
		//List<String> errors= parser.getErrors();
		Assert.assertTrue( outTreeNodeList.size()==2);
		String type = outTreeNodeList.get(0).getType();
		Assert.assertTrue( type.equalsIgnoreCase("UIATextField"));
		type = outTreeNodeList.get(1).getType();
		Assert.assertTrue( type.equalsIgnoreCase("UIASecureTextField"));
		
		parser = iQueryIdeParser.createParser("> [:top = 214]", false);
		registerPseudoAttributes(parser);
		outTreeNodeList = parser.query(candidates);
		Assert.assertTrue( outTreeNodeList.size()==1);
		type = outTreeNodeList.get(0).getType();
		Assert.assertTrue( type.equalsIgnoreCase("UIASwitch"));
		
		parser = iQueryIdeParser.createParser("> [:left = 204]", false);
		registerPseudoAttributes(parser);
		outTreeNodeList = parser.query(candidates);
		Assert.assertTrue( outTreeNodeList.size()==1);
		type = outTreeNodeList.get(0).getType();
		Assert.assertTrue( type.equalsIgnoreCase("UIASwitch"));
		
		parser = iQueryIdeParser.createParser("> [:width = 79]", false);
		registerPseudoAttributes(parser);
		outTreeNodeList = parser.query(candidates);
		Assert.assertTrue( outTreeNodeList.size()==1);
		type = outTreeNodeList.get(0).getType();
		Assert.assertTrue( type.equalsIgnoreCase("UIASwitch"));
		
		// TODO: Case Issue，没有:bottom的值为241的控件
		parser = iQueryIdeParser.createParser("> [:bottom = 241]", false);
		registerPseudoAttributes(parser);
		outTreeNodeList = parser.query(candidates);
		Assert.assertTrue( outTreeNodeList.size()==1);
		type = outTreeNodeList.get(0).getType();
		Assert.assertTrue( type.equalsIgnoreCase("UIASwitch"));
		
		parser = iQueryIdeParser.createParser("> [:right = 283]", false);
		registerPseudoAttributes(parser);
		outTreeNodeList = parser.query(candidates);
		Assert.assertTrue( outTreeNodeList.size()==1);
		type = outTreeNodeList.get(0).getType();
		Assert.assertTrue( type.equalsIgnoreCase("UIASwitch"));
		
	}
	
	//"(#id)根据给定的ID匹配一个元素"
	@Test
	public void Test5() throws IOException, RecognitionException
	{
		iQueryParser parser = iQueryIdeParser.createParser("#Pickers");
		List<ITreeNode> candidates=  getRoot();
		List<ITreeNode> outTreeNodeList = parser.query(candidates);
		//List<String> errors= parser.getErrors();
		Assert.assertTrue( outTreeNodeList.get(0).getName().equalsIgnoreCase("Pickers"));
		
		// TODO: Case Issue，在输入里，没有一个控件的name属性是tbTest的。
		parser = iQueryIdeParser.createParser("#tbTest");
		outTreeNodeList = parser.query(candidates);
		String type = outTreeNodeList.get(0).getType();
		Assert.assertTrue( type.equalsIgnoreCase("UIATextField"));
	}
	
	//(element)根据给定的元素名匹配所有元素
	@Test
	public void Test6() throws IOException, RecognitionException
	{
		iQueryParser parser = iQueryIdeParser.createParser("UIAWindow >> UIAButton");
		List<ITreeNode> candidates=  getRoot();
		List<ITreeNode> outTreeNodeList = parser.query(candidates);
		Assert.assertTrue( outTreeNodeList.size()==6);
		Assert.assertTrue( outTreeNodeList.get(0).getName().equalsIgnoreCase("Pickers"));
		
		parser = iQueryIdeParser.createParser("UIAWindow > UIATextField");
		outTreeNodeList = parser.query(candidates);
		Assert.assertTrue( outTreeNodeList.size()==1);
		String type = outTreeNodeList.get(0).getType();
		Assert.assertTrue( type.equalsIgnoreCase("UIATextField"));
	}
	
	//"(*)匹配所有元素"
	@Test
	public void Test7() throws IOException, RecognitionException
	{
		iQueryParser parser = iQueryIdeParser.createParser("UIAWindow >> *");
		List<ITreeNode> candidates=  getRoot();
		List<ITreeNode> outTreeNodeList = parser.query(candidates);
		Assert.assertTrue( outTreeNodeList.size()==16);
		
		parser = iQueryIdeParser.createParser("> *");
		ITreeNode navigationBar =  candidates.get(0).getChildren().get(0);
		List<ITreeNode> candidates1=new ArrayList<ITreeNode>();
		candidates1.add(navigationBar);
		outTreeNodeList = parser.query(candidates1);
		Assert.assertTrue( outTreeNodeList.size()==2);
		String type = outTreeNodeList.get(1).getType();
		Assert.assertTrue( type.equalsIgnoreCase("UIAStaticText"));
	}
	
	/*
	 * 并集我们并不支持，因为jquery是尽量操作更多的元素，但是iquery却相反，希望精
	//(selector1,selector2,selectorN)指定任意多个选择器，并将匹配到的元素合并到一个结果内
	@Test
	public void Test8() throws IOException, RecognitionException
	{
		iQueryParser parser = iQueryIdeParser.createParser("#Pickers");
		List<ITreeNode> candidates=  getRoot();
		List<ITreeNode> outTreeNodeList = parser.query(candidates);
		
		
	}
	*/
	
	//"(ancestor >> descendant)在给定的祖先元素下匹配所有的后代元素"
	@Test
	public void Test9() throws IOException, RecognitionException
	{
		iQueryParser parser = iQueryIdeParser.createParser("UIAWindow >> UIAButton");
		List<ITreeNode> candidates=  getRoot();
		List<ITreeNode> outTreeNodeList = parser.query(candidates);
		Assert.assertTrue( outTreeNodeList.size()==6);
		
		parser = iQueryIdeParser.createParser("UIAWindow > UIASegmentedControl > UIAButton");
		outTreeNodeList = parser.query(candidates);
		Assert.assertTrue( outTreeNodeList.size()==3);
	}
	
	//"(parent > child)在给定的父元素下匹配所有的子元素"
	@Test
	public void Test10() throws IOException, RecognitionException
	{
		iQueryParser parser = iQueryIdeParser.createParser("UIAWindow > UIAButton");
		List<ITreeNode> candidates=  getRoot();
		List<ITreeNode> outTreeNodeList = parser.query(candidates);
		Assert.assertTrue( outTreeNodeList.size()==3);
		Assert.assertTrue( outTreeNodeList.get(0).getName().equalsIgnoreCase("Pickers"));
		
		parser = iQueryIdeParser.createParser("UIAWindow > UIASegmentedControl > UIAButton");
		outTreeNodeList = parser.query(candidates);
		Assert.assertTrue( outTreeNodeList.size()==3);
		Assert.assertTrue( outTreeNodeList.get(1).getName().equalsIgnoreCase("Second"));
	}
	
	//"(prev + next)匹配所有紧接在 prev 元素后的 next 元素"
	@Test
	public void Test11() throws IOException, RecognitionException
	{
		iQueryParser parser = iQueryIdeParser.createParser("UIAWindow > UIANavigationBar > UIAImage + UIAStaticText");
		List<ITreeNode> candidates=  getRoot();
		List<ITreeNode> outTreeNodeList = parser.query(candidates);
		Assert.assertTrue( outTreeNodeList.size()==1);
		String value = outTreeNodeList.get(0).getProperty("value").getValue();
		Assert.assertTrue( value.equalsIgnoreCase("TestUIDemo"));
		
		parser = iQueryIdeParser.createParser("UIAWindow > UIASegmentedControl > UIAButton :eq(0) + UIAButton");
		outTreeNodeList = parser.query(candidates);
		Assert.assertTrue( outTreeNodeList.size()==1);
		Assert.assertTrue( outTreeNodeList.get(0).getName().equalsIgnoreCase("Second"));
	}
	
	//"(prev ~ siblings)匹配 prev 元素之后的所有siblings 元素"
	@Test
	public void Test12() throws IOException, RecognitionException
	{
		iQueryParser parser = iQueryIdeParser.createParser("UIAWindow > UIANavigationBar > UIAImage ~ UIAStaticText");
		List<ITreeNode> candidates=  getRoot();
		List<ITreeNode> outTreeNodeList = parser.query(candidates);
		Assert.assertTrue( outTreeNodeList.size()==1);
		String value = outTreeNodeList.get(0).getProperty("value").getValue();
		Assert.assertTrue( value.equalsIgnoreCase("TestUIDemo"));
		
		parser = iQueryIdeParser.createParser("UIAWindow > UIASlider ~ UIAButton");
		outTreeNodeList = parser.query(candidates);
		Assert.assertTrue( outTreeNodeList.size()==3);
		Assert.assertTrue( outTreeNodeList.get(1).getName().equalsIgnoreCase("Table"));
	}

	//"(:first)获取第一个元素"
	@Test
	public void Test13() throws IOException, RecognitionException
	{
		iQueryParser parser = iQueryIdeParser.createParser("UIAWindow > UIAButton:first");
		List<ITreeNode> candidates=  getRoot();
		List<ITreeNode> outTreeNodeList = parser.query(candidates);
		Assert.assertTrue( outTreeNodeList.size()==1);
		Assert.assertTrue( outTreeNodeList.get(0).getName().equalsIgnoreCase("Pickers"));
		
		parser = iQueryIdeParser.createParser("> UIASegmentedControl > UIAButton:first");
		outTreeNodeList = parser.query(candidates);
		Assert.assertTrue( outTreeNodeList.size()==1);
		Assert.assertTrue( outTreeNodeList.get(0).getName().equalsIgnoreCase("First"));
	}
	
	//"(:last)获取最后个元素"
	@Test
	public void Test14() throws IOException, RecognitionException
	{
		iQueryParser parser = iQueryIdeParser.createParser("> UIAButton:last");
		List<ITreeNode> candidates=  getRoot();
		List<ITreeNode> outTreeNodeList = parser.query(candidates);
		Assert.assertTrue( outTreeNodeList.size()==1);
		Assert.assertTrue( outTreeNodeList.get(0).getName().equalsIgnoreCase("Gestures"));
		
		parser = iQueryIdeParser.createParser("> UIASegmentedControl > UIAButton:last");
		outTreeNodeList = parser.query(candidates);
		Assert.assertTrue( outTreeNodeList.size()==1);
		Assert.assertTrue( outTreeNodeList.get(0).getName().equalsIgnoreCase("Third"));
	}
	
	//"(:not(selector))去除所有与给定选择器匹配的元素"
	@Test
	public void Test15() throws IOException, RecognitionException
	{
		iQueryParser parser = iQueryIdeParser.createParser("> UIAButton:not(:first)");
		List<ITreeNode> candidates=  getRoot();
		List<ITreeNode> outTreeNodeList = parser.query(candidates);
		Assert.assertTrue( outTreeNodeList.size()==2);
		Assert.assertTrue( outTreeNodeList.get(1).getName().equalsIgnoreCase("Gestures"));
		
		parser = iQueryIdeParser.createParser("> UIASegmentedControl > UIAButton:not(:button)");
		outTreeNodeList = parser.query(candidates);
		Assert.assertTrue( outTreeNodeList.size()==0);
		
		parser = iQueryIdeParser.createParser("> UIASegmentedControl > UIAButton:not(:text)");
		outTreeNodeList = parser.query(candidates);
		Assert.assertTrue( outTreeNodeList.size()==3);
		Assert.assertTrue( outTreeNodeList.get(2).getName().equalsIgnoreCase("Third"));
		
		parser = iQueryIdeParser.createParser("> UIATextField:not([name='tbTest'])");
		outTreeNodeList = parser.query(candidates);
		Assert.assertTrue( outTreeNodeList.size()==1);
		
		parser = iQueryIdeParser.createParser(">> UIAStaticText:not([name!='TestUIDemo'])");
		outTreeNodeList = parser.query(candidates);
		Assert.assertTrue( outTreeNodeList.size()==1);
		Assert.assertTrue( outTreeNodeList.get(0).getName().equalsIgnoreCase("TestUIDemo"));
	}
	
	//"(:eq(index))匹配一个给定索引值的元素"
	@Test
	public void Test16() throws IOException, RecognitionException
	{
		iQueryParser parser = iQueryIdeParser.createParser("> UIAButton:eq(2)");
		List<ITreeNode> candidates=  getRoot();
		List<ITreeNode> outTreeNodeList = parser.query(candidates);
		Assert.assertTrue( outTreeNodeList.size()==1);
		Assert.assertTrue( outTreeNodeList.get(0).getName().equalsIgnoreCase("Gestures"));
		
		parser = iQueryIdeParser.createParser("> UIAButton:eq(8)");
		outTreeNodeList = parser.query(candidates);
		Assert.assertTrue( outTreeNodeList.size()==0);
		
		parser = iQueryIdeParser.createParser("> UIASegmentedControl > UIAButton:eq(2)");
		outTreeNodeList = parser.query(candidates);
		Assert.assertTrue( outTreeNodeList.size()==1);
		Assert.assertTrue( outTreeNodeList.get(0).getName().equalsIgnoreCase("Third"));
		
		parser = iQueryIdeParser.createParser("> UIATextField:eq(0)");
		outTreeNodeList = parser.query(candidates);
		Assert.assertTrue( outTreeNodeList.size()==1);
		// TODO: Case Issue, 这个UIATextField就没有name属性
		Assert.assertTrue( outTreeNodeList.get(0).getName().equalsIgnoreCase("tbTest"));
	}
	
	//"(:gt(index))匹配所有大于给定索引值的元素"
		@Test
		public void Test17() throws IOException, RecognitionException
		{
			iQueryParser parser = iQueryIdeParser.createParser("> UIAButton:gt(1)");
			List<ITreeNode> candidates=  getRoot();
			List<ITreeNode> outTreeNodeList = parser.query(candidates);
			Assert.assertTrue( outTreeNodeList.size()==1);
			Assert.assertTrue( outTreeNodeList.get(0).getName().equalsIgnoreCase("Gestures"));
			
			parser = iQueryIdeParser.createParser("> UIAButton:gt(5)");
			outTreeNodeList = parser.query(candidates);
			Assert.assertTrue( outTreeNodeList.size()==0);
			
			parser = iQueryIdeParser.createParser("> UIASegmentedControl > UIAButton:gt(1)");
			outTreeNodeList = parser.query(candidates);
			Assert.assertTrue( outTreeNodeList.size()==1);
			Assert.assertTrue( outTreeNodeList.get(0).getName().equalsIgnoreCase("Third"));
		}
	
	//"(:lt(index))匹配所有小于给定索引值的元素"
	@Test
	public void Test18() throws IOException, RecognitionException
	{
		iQueryParser parser = iQueryIdeParser.createParser("> UIAButton:lt(3)");
		List<ITreeNode> candidates=  getRoot();
		List<ITreeNode> outTreeNodeList = parser.query(candidates);
		Assert.assertTrue( outTreeNodeList.size()==3);
		Assert.assertTrue( outTreeNodeList.get(2).getName().equalsIgnoreCase("Gestures"));
		
		parser = iQueryIdeParser.createParser("UIAButton:lt(0)");
		outTreeNodeList = parser.query(candidates);
		Assert.assertTrue( outTreeNodeList.size()==0);
		
		parser = iQueryIdeParser.createParser("> UIASegmentedControl > UIAButton:lt(1)");
		outTreeNodeList = parser.query(candidates);
		Assert.assertTrue( outTreeNodeList.size()==1);
		Assert.assertTrue( outTreeNodeList.get(0).getName().equalsIgnoreCase("First"));
	}
	
	//"(:contains(text))匹配包含给定文本的元素"
	@Test
	public void Test19() throws IOException, RecognitionException
	{
		iQueryParser parser = iQueryIdeParser.createParser("> UIASegmentedControl > UIAButton:contains('Seco')");
		List<ITreeNode> candidates=  getRoot();
		// TODO: Case Issue, 需要修改ITreeNode的getText的实现，针对button的情况不能从value里取值，需要从name里取值。
		List<ITreeNode> outTreeNodeList = parser.query(candidates);
		Assert.assertTrue( outTreeNodeList.size()==1);
		Assert.assertTrue( outTreeNodeList.get(2).getName().equalsIgnoreCase("Second"));
		
		parser = iQueryIdeParser.createParser("> UIASegmentedControl > UIAButton:contains('Eco')");
		outTreeNodeList = parser.query(candidates);
		Assert.assertTrue( outTreeNodeList.size()==0);
	}
	
	//"(:empty)匹配所有不包含子元素或者文本的空元素"
	@Test
	public void Test20() throws IOException, RecognitionException
	{
		iQueryParser parser = iQueryIdeParser.createParser("> UIAButton:empty");
		List<ITreeNode> candidates=  getRoot();
		List<ITreeNode> outTreeNodeList = parser.query(candidates);
		Assert.assertTrue( outTreeNodeList.size()==3);
		
		parser = iQueryIdeParser.createParser("> UIATable:empty");
		outTreeNodeList = parser.query(candidates);
		Assert.assertTrue( outTreeNodeList.size()==0);
		
		parser = iQueryIdeParser.createParser("> UIASegmentedControl:empty");
		outTreeNodeList = parser.query(candidates);
		Assert.assertTrue( outTreeNodeList.size()==0);
	}
	
	/* 
	 * 不打算支持
	//"(:has(selector))匹配含有选择器所匹配的元素的元素"
	@Test
	public void Test21() throws IOException, RecognitionException
	{
		iQueryParser parser = iQueryIdeParser.createParser("#Pickers");
		List<ITreeNode> candidates=  getRoot();
		List<ITreeNode> outTreeNodeList = parser.query(candidates);
	}*/
	
	//iQuery中:parent的实现于jquery不同，否则就没有办法找到父级节点了
	//"(:parent)匹配含有子元素或者文本的元素"
	@Test
	public void Test22() throws IOException, RecognitionException
	{
		iQueryParser parser = iQueryIdeParser.createParser("> UIAButton:parent");
		List<ITreeNode> candidates=  getRoot();
		List<ITreeNode> outTreeNodeList = parser.query(candidates);
		Assert.assertTrue( outTreeNodeList.size()==1);
		
		parser = iQueryIdeParser.createParser("> UIASegmentedControl:parent");
		outTreeNodeList = parser.query(candidates);
		Assert.assertTrue( outTreeNodeList.size()==1);
		String type = outTreeNodeList.get(0).getType();
		Assert.assertTrue( type.equalsIgnoreCase("UIAWindow"));
		
		parser = iQueryIdeParser.createParser("> UIAWindows:parent");
		outTreeNodeList = parser.query(candidates);
		Assert.assertTrue( outTreeNodeList.size()==0);
		
		parser = iQueryIdeParser.createParser("> UIANavigationBar:parent");
		outTreeNodeList = parser.query(candidates);
		Assert.assertTrue( outTreeNodeList.size()==1);
		type = outTreeNodeList.get(0).getType();
		Assert.assertTrue( type.equalsIgnoreCase("UIAWindow"));
		
		parser = iQueryIdeParser.createParser("> UIATextField:parent");
		outTreeNodeList = parser.query(candidates);
		Assert.assertTrue( outTreeNodeList.size()==1);
	}
	
	/*HierarchyViewer for iOS do not support
	//"(:hidden)匹配所有不可见元素"
	@Test
	public void Test23() throws IOException, RecognitionException
	{
		iQueryParser parser = iQueryIdeParser.createParser("#Pickers");
		List<ITreeNode> candidates=  getRoot();
		List<ITreeNode> outTreeNodeList = parser.query(candidates);
	}
	
	//"(:visible)匹配所有的可见元素"
	@Test
	public void Test24() throws IOException, RecognitionException
	{
		iQueryParser parser = iQueryIdeParser.createParser("#Pickers");
		List<ITreeNode> candidates=  getRoot();
		List<ITreeNode> outTreeNodeList = parser.query(candidates);
	}
	*/

	//"([attribute])匹配包含给定属性的元素"
	@Test
	public void Test25() throws IOException, RecognitionException
	{
		iQueryParser parser = iQueryIdeParser.createParser("> UIAButton[name]");
		List<ITreeNode> candidates=  getRoot();
		List<ITreeNode> outTreeNodeList = parser.query(candidates);
		Assert.assertTrue( outTreeNodeList.size()==3);
		
		parser = iQueryIdeParser.createParser("> UIAButton[something]");
		outTreeNodeList = parser.query(candidates);
		Assert.assertTrue( outTreeNodeList.size()==0);
	}
	
	//"([attribute=value])匹配给定的属性是某个特定值的元素"
	@Test
	public void Test26() throws IOException, RecognitionException
	{
		iQueryParser parser = iQueryIdeParser.createParser("> UIASegmentedControl > UIAButton[name='Second']");
		List<ITreeNode> candidates=  getRoot();
		List<ITreeNode> outTreeNodeList = parser.query(candidates);
		Assert.assertTrue( outTreeNodeList.size()==1);
		Assert.assertTrue( outTreeNodeList.get(0).getName().equalsIgnoreCase("Second"));
		
		parser = iQueryIdeParser.createParser("> UIAButton[something='Second']");
		outTreeNodeList = parser.query(candidates);
		Assert.assertTrue( outTreeNodeList.size()==0);
		
		parser = iQueryIdeParser.createParser("> UIATextField[name='Second']");
		outTreeNodeList = parser.query(candidates);
		Assert.assertTrue( outTreeNodeList.size()==0);
	}
	
	//"([attribute!=value])匹配所有不含有指定的属性，或者属性不等于特定值的元素"
	@Test
	public void Test27() throws IOException, RecognitionException
	{
		iQueryParser parser = iQueryIdeParser.createParser("> UIAButton[name!='Second']");
		List<ITreeNode> candidates=  getRoot();
		List<ITreeNode> outTreeNodeList = parser.query(candidates);
		Assert.assertTrue( outTreeNodeList.size()==3);
		Assert.assertTrue( outTreeNodeList.get(0).getName().equalsIgnoreCase("Pickers"));
		
		parser = iQueryIdeParser.createParser("> UIATextField[name!='Second']");
		outTreeNodeList = parser.query(candidates);
		Assert.assertTrue( outTreeNodeList.size()==1);
		
		/*
		parser = iQueryIdeParser.createParser("> UIATextField[someAttribute!='Second']");
		outTreeNodeList = parser.query(candidates);
		Assert.assertTrue( outTreeNodeList.size()==1;

	    // 在ios 5上面没有办法获取name属性
	    
	    Assert.assertTrue(.Equals('tbTest', result[0].name());

	    query = "> UIATextField[name!='tbTest']";
	    result = $(query);
	    Assert.assertTrue(.Equals(0, result.length);
	    */
	}
	
	//"([attribute^=value])匹配给定的属性是以某些值开始的元素"
	@Test
	public void Test28() throws IOException, RecognitionException
	{
		iQueryParser parser = iQueryIdeParser.createParser("> UIASegmentedControl > UIAButton[name^='Second']");
		List<ITreeNode> candidates=  getRoot();
		List<ITreeNode> outTreeNodeList = parser.query(candidates);
		Assert.assertTrue( outTreeNodeList.size()==1);
		Assert.assertTrue( outTreeNodeList.get(0).getName().equalsIgnoreCase("Second"));
		
		parser = iQueryIdeParser.createParser("> UIASegmentedControl > UIAButton[name^='Sec']");
		outTreeNodeList = parser.query(candidates);
		Assert.assertTrue( outTreeNodeList.size()==1);
		Assert.assertTrue( outTreeNodeList.get(0).getName().equalsIgnoreCase("Second"));
		
		parser = iQueryIdeParser.createParser("> UIAButton[name^='sec']");
		outTreeNodeList = parser.query(candidates);
		Assert.assertTrue( outTreeNodeList.size()==0);
		
		parser = iQueryIdeParser.createParser("> UIAButton[name^='ddd']");
		outTreeNodeList = parser.query(candidates);
		Assert.assertTrue( outTreeNodeList.size()==0);
		
		parser = iQueryIdeParser.createParser("> UIAButton[something^='Second']");
		outTreeNodeList = parser.query(candidates);
		Assert.assertTrue( outTreeNodeList.size()==0);
		
		parser = iQueryIdeParser.createParser("> UIATextField[name^='StbTest']");
		outTreeNodeList = parser.query(candidates);
		Assert.assertTrue( outTreeNodeList.size()==0);
	}
	
	//"([attribute$=value])匹配给定的属性是以某些值结尾的元素"
	@Test
	public void Test29() throws IOException, RecognitionException
	{
		iQueryParser parser = iQueryIdeParser.createParser("> UIASegmentedControl > UIAButton[name$='Second']");
		List<ITreeNode> candidates=  getRoot();
		List<ITreeNode> outTreeNodeList = parser.query(candidates);
		Assert.assertTrue( outTreeNodeList.size()==1);
		Assert.assertTrue( outTreeNodeList.get(0).getName().equalsIgnoreCase("Second"));
		
		parser = iQueryIdeParser.createParser("> UIASegmentedControl > UIAButton[name$='nd']");
		outTreeNodeList = parser.query(candidates);
		Assert.assertTrue( outTreeNodeList.size()==1);
		Assert.assertTrue( outTreeNodeList.get(0).getName().equalsIgnoreCase("Second"));
		
		parser = iQueryIdeParser.createParser("> UIASegmentedControl > UIAButton[name$='d']");
		outTreeNodeList = parser.query(candidates);
		Assert.assertTrue( outTreeNodeList.size()==2);  // Second & Third
		
		parser = iQueryIdeParser.createParser("> UIASegmentedControl > UIAButton[name$='D']");
		outTreeNodeList = parser.query(candidates);
		Assert.assertTrue( outTreeNodeList.size()==0);
		
		parser = iQueryIdeParser.createParser("> UIASegmentedControl > UIAButton[something$='Second']");
		outTreeNodeList = parser.query(candidates);
		Assert.assertTrue( outTreeNodeList.size()==0);
		
		parser = iQueryIdeParser.createParser("> UIASegmentedControl > UIATextField[name$='StbTest']");
		outTreeNodeList = parser.query(candidates);
		Assert.assertTrue( outTreeNodeList.size()==0);
	}
	
	//"([attribute*=value])匹配给定的属性是以包含某些值的元素"
	@Test
	public void Test30() throws IOException, RecognitionException
	{
		iQueryParser parser = iQueryIdeParser.createParser("> UIASegmentedControl > UIAButton[name*='Second']");
		List<ITreeNode> candidates=  getRoot();
		List<ITreeNode> outTreeNodeList = parser.query(candidates);
		Assert.assertTrue( outTreeNodeList.size()==1);
		Assert.assertTrue( outTreeNodeList.get(0).getName().equalsIgnoreCase("Second"));
		
		parser = iQueryIdeParser.createParser("> UIASegmentedControl > UIAButton[name*='nd']");
		outTreeNodeList = parser.query(candidates);
		Assert.assertTrue( outTreeNodeList.size()==1);
		Assert.assertTrue( outTreeNodeList.get(0).getName().equalsIgnoreCase("Second"));
		
		parser = iQueryIdeParser.createParser("> UIAButton[name*='Pick']");
		outTreeNodeList = parser.query(candidates);
		Assert.assertTrue( outTreeNodeList.size()==1);
		Assert.assertTrue( outTreeNodeList.get(0).getName().equalsIgnoreCase("Pickers"));
		
		parser = iQueryIdeParser.createParser(">> UIAButton[name*='i']");
		outTreeNodeList = parser.query(candidates);
		Assert.assertTrue( outTreeNodeList.size()==3);   // Pickers & First & Third
		
		parser = iQueryIdeParser.createParser("> UIAButton[name*='I']");
		outTreeNodeList = parser.query(candidates);
		Assert.assertTrue( outTreeNodeList.size()==0);
		
		parser = iQueryIdeParser.createParser("> UIAButton[something*='Second']");
		outTreeNodeList = parser.query(candidates);
		Assert.assertTrue( outTreeNodeList.size()==0);
		
		parser = iQueryIdeParser.createParser("> UIATextField[name*='StbTest']");
		outTreeNodeList = parser.query(candidates);
		Assert.assertTrue( outTreeNodeList.size()==0);
	}
	
	//"([selector1][selector2][selectorN])复合属性选择器，需要同时满足多个条件时使用"
	@Test
	public void Test31() throws IOException, RecognitionException
	{
		iQueryParser parser = iQueryIdeParser.createParser("> UIASegmentedControl > UIAButton[name$='ond'][name^='Sec']");
		List<ITreeNode> candidates=  getRoot();
		List<ITreeNode> outTreeNodeList = parser.query(candidates);
		Assert.assertTrue( outTreeNodeList.size()==1);
		Assert.assertTrue( outTreeNodeList.get(0).getName().equalsIgnoreCase("Second"));
		
		parser = iQueryIdeParser.createParser("> UIASegmentedControl > UIAButton[name$='cond'][name^='Sec']");
		outTreeNodeList = parser.query(candidates);
		Assert.assertTrue( outTreeNodeList.size()==1);
		Assert.assertTrue( outTreeNodeList.get(0).getName().equalsIgnoreCase("Second"));
		
		parser = iQueryIdeParser.createParser("> UIASegmentedControl > UIAButton[name$='Cond'][name^='Sec']");
		outTreeNodeList = parser.query(candidates);
		Assert.assertTrue( outTreeNodeList.size()==0);
		
		parser = iQueryIdeParser.createParser("> UIASegmentedControl > UIAButton[name*='i'][name^='T'][name$='d']");
		outTreeNodeList = parser.query(candidates);
		Assert.assertTrue( outTreeNodeList.size()==1);
		Assert.assertTrue( outTreeNodeList.get(0).getName().equalsIgnoreCase("Third"));
		
		parser = iQueryIdeParser.createParser("> UIASegmentedControl > UIAButton[name*='I'][name^='T'][name$='d']");
		outTreeNodeList = parser.query(candidates);
		Assert.assertTrue( outTreeNodeList.size()==0);

		parser = iQueryIdeParser.createParser("> UIATextField[name$='tbTest'][name!='tbTest'][name^='tbTest']");
		outTreeNodeList = parser.query(candidates);
		Assert.assertTrue( outTreeNodeList.size()==0);
		
		 /* 
	    query = "> UIATextField[name$='tbTest'][name*='tbTest'][name^='tbTest']";
	    result = $(query);
	    Assert.assertTrue(.Equals(1, result.length);
	    Assert.assertTrue(.Equals("tbTest", result[0].name());
	    */
	}
	
	//"(:nth-child)匹配其父元素下的第N个子或奇偶元素，要匹配元素的序号，从1开始"
	@Test
	public void Test32() throws IOException, RecognitionException
	{
		iQueryParser parser = iQueryIdeParser.createParser("> UIASegmentedControl :nth-child(2)");
		List<ITreeNode> candidates=  getRoot();
		List<ITreeNode> outTreeNodeList = parser.query(candidates);
		Assert.assertTrue( outTreeNodeList.size()==1);
		Assert.assertTrue( outTreeNodeList.get(0).getName().equalsIgnoreCase("Second"));
		
		parser = iQueryIdeParser.createParser("> UIASegmentedControl :nth-child(0)");
		outTreeNodeList = parser.query(candidates);
		Assert.assertTrue( outTreeNodeList.size()==0);
		
		parser = iQueryIdeParser.createParser("> UIASegmentedControl :nth-child(10)");
		outTreeNodeList = parser.query(candidates);
		Assert.assertTrue( outTreeNodeList.size()==0);
		
		parser = iQueryIdeParser.createParser(":nth-child(1)");
		outTreeNodeList = parser.query(candidates);
		Assert.assertTrue( outTreeNodeList.size()==1);
	}
	
	//"(:first-child)匹配第一个子元素"
	@Test
	public void Test33() throws IOException, RecognitionException
	{
		iQueryParser parser = iQueryIdeParser.createParser("> UIASegmentedControl :first-child");
		List<ITreeNode> candidates=  getRoot();
		List<ITreeNode> outTreeNodeList = parser.query(candidates);
		Assert.assertTrue( outTreeNodeList.size()==1);
		Assert.assertTrue( outTreeNodeList.get(0).getName().equalsIgnoreCase("First"));
	}
	
	//"(:last-child)匹配最后一个子元素"
	@Test
	public void Test34() throws IOException, RecognitionException
	{
		iQueryParser parser = iQueryIdeParser.createParser("> UIASegmentedControl :last-child");
		List<ITreeNode> candidates=  getRoot();
		List<ITreeNode> outTreeNodeList = parser.query(candidates);
		Assert.assertTrue( outTreeNodeList.size()==1);
		Assert.assertTrue( outTreeNodeList.get(0).getName().equalsIgnoreCase("Third"));
	}
	
	//"(:text)匹配所有的单行文本框"
	@Test
	public void Test35() throws IOException, RecognitionException
	{
		iQueryParser parser = iQueryIdeParser.createParser("> :text", false);
		parser.registerPseudoClass("text", new IPseudoClass() {
			public boolean resolve(ITreeNode node) {
			    return iQueryIdeParser.filterByNameEndsWith(node, "TextField");
			}
		    });
		
		List<ITreeNode> candidates=  getRoot();
		List<ITreeNode> outTreeNodeList = parser.query(candidates);
		Assert.assertTrue( outTreeNodeList.size()==2); // UIASecureTextField，也应该是text
	}
	
	//"(:radio)匹配所有单选按钮"
	@Test
	public void Test36() throws IOException, RecognitionException
	{
		iQueryParser parser = iQueryIdeParser.createParser("> :radio", false);
		parser.registerPseudoClass("radio", new IPseudoClass() {
			public boolean resolve(ITreeNode node) {
			    return iQueryIdeParser.filterByNameEndsWith(node, "UIASwitch");
			}
		    });
		List<ITreeNode> candidates=  getRoot();
		List<ITreeNode> outTreeNodeList = parser.query(candidates);
		Assert.assertTrue( outTreeNodeList.size()==1); 
	}
	
	//"(:button)匹配所有按钮"
	@Test
	public void Test37() throws IOException, RecognitionException
	{
		iQueryParser parser = iQueryIdeParser.createParser(">> :button");
		List<ITreeNode> candidates=  getRoot();
		List<ITreeNode> outTreeNodeList = parser.query(candidates);
		Assert.assertTrue( outTreeNodeList.size()==6); 
	}
	
	//"(:label)匹配所有UIAStaticText"
	@Test
	public void Test38() throws IOException, RecognitionException
	{
		iQueryParser parser = iQueryIdeParser.createParser("> UIANavigationBar > :label", false);
		parser.registerPseudoClass("label", new IPseudoClass() {
			public boolean resolve(ITreeNode node) {
			    return iQueryIdeParser.filterByNameEndsWith(node, "UIAStaticText");
			}
		    });
		List<ITreeNode> candidates=  getRoot();
		List<ITreeNode> outTreeNodeList = parser.query(candidates);
		Assert.assertTrue( outTreeNodeList.size()==1); 
	}
	
	/* HierarchyView do not support it
	//"(:enabled)匹配所有控件的可用状态"
	@Test
	public void Test39() throws IOException, RecognitionException
	{
		var query = "> UIAButton:enabled";
	    var result = $(query);
	    Assert.assertTrue(.Equals(3, result.length);

	    query = "> UIASwitch:enabled";
	    result = $(query);
	    Assert.assertTrue(.Equals(0, result.length);
	}
	
	//"(:enabled)匹配所有控件的可用状态"
	@Test
	public void Test40() throws IOException, RecognitionException
	{
		iQueryParser parser = iQueryIdeParser.createParser("#Pickers");
		List<ITreeNode> candidates=  getRoot();
		List<ITreeNode> outTreeNodeList = parser.query(candidates);
	}
	
	//"(:disabled)匹配所有控件的不可用状态"
	@Test
	public void Test41() throws IOException, RecognitionException
	{
		iQueryParser parser = iQueryIdeParser.createParser("#Pickers");
		List<ITreeNode> candidates=  getRoot();
		List<ITreeNode> outTreeNodeList = parser.query(candidates);
	}
	*/
	
	//TODO no controller in the log
	/*
	//"(:image)匹配所有图像域"
	@Test
	public void Test42() throws IOException, RecognitionException
	{
		iQueryParser parser = iQueryIdeParser.createParser("#Pickers");
		List<ITreeNode> candidates=  getRoot();
		List<ITreeNode> outTreeNodeList = parser.query(candidates);
	}
	*/

	
/*	@Test
	public void TestHello()
	{
		Assert.assertTrue(.Assert.assertTrue(True(1 > 0);
	}*/
}

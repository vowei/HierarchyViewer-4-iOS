var type=function(element)
{
    var elemType = Object.prototype.toString.call(element);
    var spaceIdx = elemType.indexOf(' ');
    elemType = elemType.substring(spaceIdx + 1, elemType.length - 1);
    return elemType;
}

var PropertyToString = function(name,value)
{
	var str="\""+name+"\":";
	str+="\""+value+"\"";
	return str;
};

var ElementToString = function(element)
{
	var str="{";
	
	//type
	str+=PropertyToString("type",type(element));
	str+=",";
	
	//name
	var name = element.name();
	if(name!=null)
	{
		str+=PropertyToString("name",name);
		str+=",";
	}
	
	//value
	var value = element.name();
	if(value!=null)
	{
		str+=PropertyToString("value",value);
		str+=",";
	}
	
	//enabled
	var enabled = element.isEnabled()?"true":"false";
	str+=PropertyToString("enabled",enabled);
	str+=",";
	
	//Visible
	var visible = element.isVisible()?"true":"false";
	str+=PropertyToString("visible",visible);
	str+=",";
	
	//focus
	var focus = element.hasKeyboardFocus()?"true":"false";
	str+=PropertyToString("focus",focus);
	str+=",";
	
	//rect
	var rect1 = element.rect();
	var rectString = "{{"+rect1.origin.x
	+","+rect1.origin.y+"},{"+rect1.size.width
	+","+rect1.size.height+"}}";
	str+=PropertyToString("rect",rectString);
	str+=",";
	
	//:left
	str+=PropertyToString(":left",rect1.origin.x.toString());
	str+=",";
	
	//:right
	str+=PropertyToString(":right",(rect1.origin.x+rect1.size.width).toString());
	str+=",";
	
	//:top
	str+=PropertyToString(":top",rect1.origin.y.toString());
	str+=",";
	
	//:bottom
	str+=PropertyToString(":bottom",(rect1.origin.y+rect1.size.height).toString());
	str+=",";
	
	//:height
	str+=PropertyToString(":height",rect1.size.height.toString());
	str+=",";
	
	//:height
	str+=PropertyToString(":width",rect1.size.width.toString());
	
	str+="}";
	return str;
};

var TreeToString = function( parent )
{
	var str="{\"element\":";
	str+=ElementToString(parent);
	
	var children  = parent.elements();
	if(children.length==0)
	{
		str+="}";
		return str
	}
	
	str+=",\"children\":[";
	for(var i=0;i<children.length;i++)
	{
		var child=children[i];
		var childStr = TreeToString(child);
		str+=childStr;
        if(i!=children.length-1)
        {
            str+=",";
        }
	}
	str+="]}";
	return str;
};

var GetElementTree = function( target )
{
	var window = target.frontMostApp().mainWindow();
	
	//get time tick
	var d =new Date();
	var screenshotName = d.getTime().toString();
	
	UIALogger.logMessage(screenshotName);
	target.captureScreenWithName(screenshotName);
	
	str="{\"type\":\"ElementInfo\",\"screenshot\":\""+screenshotName+"\",\"Tree\":";
	str+=TreeToString(window);
	str+="}";
	return str;
};

var PostElementTree = function(port,target)
{
	var host = target.host();
	var result = host.performTaskWithPathArgumentsTimeout("~/batman.app/Contents/MacOS/JavaApplicationStub",["getcommand",port],15)
    UIALogger.logMessage(result.stdout);
	if(result.stdout=="getelement")
	{
		var log = GetElementTree(target);
		host.performTaskWithPathArgumentsTimeout("~/batman.app/Contents/MacOS/JavaApplicationStub",["sendelement",log,port],15);
	}
};

var GetPort = function(target)
{
	var host = target.host();
	var result = host.performTaskWithPathArgumentsTimeout("~/batman.app/Contents/MacOS/JavaApplicationStub",["getport"],15);
    UIALogger.logMessage(result.stdout);
	return result.stdout;
};

var main = function()
{
	var target = UIATarget.localTarget();
	target.setTimeout(0);
	
	//get port
	var port = GetPort(target);
	
	while(true)
	{
		PostElementTree(port,target);
	}
	
	//UIALogger.logMessage(log);
};

main();
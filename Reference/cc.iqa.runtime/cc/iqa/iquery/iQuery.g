grammar iQuery;

@lexer::header {
package cc.iqa.iquery;
}

@lexer::members {
private List<String> _errors = null;

public iQueryLexer(CharStream input, List<String> errors) {
    this(input);
    _errors = errors;
}

public String getErrorMessage(RecognitionException e,
                              String[] tokenNames)
{
    String error = ErrorMessageHelper.getErrorMessage(e, null, tokenNames, this);

    if ( _errors != null && error != null ) {
        _errors.add(error);   
    }
 
    return error;
}
}

@parser::header {
package cc.iqa.iquery;

import java.util.List;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.Queue;
import java.util.Map;
import java.util.HashMap;
import java.util.logging.*;
import cc.iqa.iquery.*;
}

@parser::members {
private static final Logger _logger = 
    Logger.getLogger(iQueryParser.class.getPackage().getName());
private StringBuilder _indentBuilder = new StringBuilder();
private boolean _debug = false;
private List<String> _errors = null;

public iQueryParser(TokenStream input, List<String> errors, boolean debug) {
    this(input);
    
    _errors = errors;
    _debug = debug;
}

public String getErrorMessage(RecognitionException e,
                              String[] tokenNames)
{
    String error = ErrorMessageHelper.getErrorMessage(e, (CommonTokenStream)this.input, tokenNames, this);

    if ( _errors != null && error != null ) {
        _errors.add(error);   
    }
 
    return error;
}

public List<String> getErrors() { 
    return _errors; 
}

private List<ITreeNode> descendants(List<ITreeNode> nodes, int maxLevel) {
    if ( nodes == null ) {
        return new ArrayList<ITreeNode>();
    } else if ( nodes.size() != 1 ) {
        warning("descendants函数仅接受包含一个控件的数组，当前的nodes数组的元素个数为：" + 
                nodes.size());
        return new ArrayList<ITreeNode>();
    } else {
        return descendants(nodes.get(0), maxLevel);
    }
}

private List<ITreeNode> descendants(ITreeNode anscent) {
    return descendants(anscent, -1);
}

private List<ITreeNode> descendants(ITreeNode anscent, int maxLevel) {
    if ( anscent == null ) 
        return new ArrayList<ITreeNode>();

    Queue<Pair<ITreeNode, Integer>> queue = new LinkedList<Pair<ITreeNode, Integer>>();
    // Queue<ITreeNode> queue = new LinkedList<ITreeNode>();
    List<ITreeNode> result = new ArrayList<ITreeNode>();
    for ( ITreeNode c : anscent.getChildren() ) {
        queue.offer(new Pair<ITreeNode, Integer>(c, new Integer(0)));
    }

    while ( !queue.isEmpty() ) {
        Pair<ITreeNode, Integer> entry = queue.peek();
        queue.poll();

        ITreeNode node = entry.first;
        if ( entry.second.intValue() == maxLevel )
            continue;
        
        for ( int i = 0; i < node.getChildren().size(); ++i ) {
            ITreeNode child = node.getChildren().get(i);
            if ( !result.contains(child) )
                queue.offer(new Pair<ITreeNode, Integer>(child, 
                                                         new Integer(entry.second.intValue() + 1)));
        }
        
        if ( !result.contains(node) )
            result.add(node);
    }

    return result;
}

private String type(ITreeNode node) {
    String name = node.getName();
    int endIdx = name.indexOf('@');
    if ( endIdx == -1 )
            endIdx = name.length();
    
    int startIdx = name.lastIndexOf('.', endIdx);
    if ( startIdx < 0 ) 
        startIdx = -1;
    
    return name.substring(startIdx + 1, endIdx).replace('$', '.');
}

private List<ITreeNode> filterByNameStartsWith(List<ITreeNode> candidates, String ... criterias){
    if ( candidates == null ) 
        return new ArrayList<ITreeNode>();

    List<ITreeNode> result = new ArrayList<ITreeNode>();
    for ( int i = 0; i < candidates.size(); ++i ) {
        ITreeNode node = candidates.get(i);
        String name = type(node);
        if ( name != null ) {
            for (int j = 0; j < criterias.length; ++j ) {
                if ( name.startsWith(criterias[j]) ) {
                    result.add(node);
                    break;
                }
            }
        }
    }

    return result;
}

private List<ITreeNode> filterByNameEndsWith(List<ITreeNode> candidates, String ... criterias){
    if ( candidates == null ) 
        return new ArrayList<ITreeNode>();

    List<ITreeNode> result = new ArrayList<ITreeNode>();
    for ( int i = 0; i < candidates.size(); ++i ) {
        ITreeNode node = candidates.get(i);
        String name = type(node);
        if ( name != null ) {
            for (int j = 0; j < criterias.length; ++j ) {
                if ( name.endsWith(criterias[j]) ) {
                    result.add(node);
                    break;
                }
            }
        }
    }

    return result;
}

private List<ITreeNode> filterByAttributes(List<ITreeNode> candidates, String ... attributes) {
    if ( candidates == null )
        return new ArrayList<ITreeNode>();

    // 对于:text元条件，我们就认为所有包含mText属性的控件都是Text
    List<ITreeNode> result = new ArrayList<ITreeNode>();
    for ( int i = 0; i < candidates.size(); ++i ) {
        ITreeNode node = candidates.get(i);
        boolean isMatched = true;
        for ( int j = 0; j < attributes.length; ++j ) {
            String key = attributes[j];

            if ( !node.containsProperty(key) ) {
                isMatched = false;
                break;
            }
        }

        if ( isMatched ) 
            result.add(node);
    }             
    
    return result;
}

private List<ITreeNode> filterByAttributes(List<ITreeNode> candidates, Map<String, String> attributes) {
    if ( candidates == null )
        return new ArrayList<ITreeNode>();

    // 对于:text元条件，我们就认为所有包含mText属性的控件都是Text
    List<ITreeNode> result = new ArrayList<ITreeNode>();
    for ( int i = 0; i < candidates.size(); ++i ) {
        ITreeNode node = candidates.get(i);
        boolean isMatched = true;
        for ( Map.Entry<String, String> entry : attributes.entrySet() ) {
            if ( !node.containsProperty(entry.getKey()) ) {
                isMatched = false;
                break;
            }

            IProperty property = node.getProperty(entry.getKey());
            if ( property == null ||
                 property.getValue().compareTo(entry.getValue()) != 0 ) {
                isMatched = false;
                break;
            }
        }

        if ( isMatched ) 
            result.add(node);
    }             
    
    return result;
}

private List<ITreeNode> filterByExcludingAttributes(List<ITreeNode> candidates, 
                                                         Map<String, String> attributes) {
    if ( candidates == null )
        return new ArrayList<ITreeNode>();

    List<ITreeNode> result = new ArrayList<ITreeNode>();
    for ( int i = 0; i < candidates.size(); ++i ) {
        ITreeNode node = candidates.get(i);
        boolean isMatched = false;
        for ( Map.Entry<String, String> entry : attributes.entrySet() ) {
            if ( !node.containsProperty(entry.getKey()) ) {
                isMatched = true;
                break;
            }

            IProperty property = node.getProperty(entry.getKey());
            if ( property == null ||
                 property.getValue().compareTo(entry.getValue()) != 0 ) {
                isMatched = true;
                break;
            }
        }

        if ( isMatched ) 
            result.add(node);
    }             
    
    return result;
}

private String indent(String message) {
    return _indentBuilder.toString() + message;
}

private void debug(String message) {    
    if ( _debug ) {
        _logger.info(indent(message));
    }
}

private void verbose(String message) { 
    if ( _debug ) {
        _logger.info(indent(message));
    }
}

private void warning(String message) {
    _logger.warning(_debug ? indent(message) : message);
}

private void increaseIndent() {
    _indentBuilder.append(' ');
}

private void descreaseIndent() {
    _indentBuilder.deleteCharAt(_indentBuilder.length() - 1);
}
}

prog [List<ITreeNode> candidates] returns [List<ITreeNode> survival]  
    : selectors[$candidates] NEWLINE* EOF
        { 
            if ( $selectors.survival != null && (_errors == null || _errors.size() == 0) ) {
                $survival = $selectors.survival;
            } else {
                $survival = new ArrayList<ITreeNode>();
            }            
        }
    | NEWLINE* EOF
        {
            $survival = new ArrayList<ITreeNode>();
        }
    ;

selectors [List<ITreeNode> candidates] returns [List<ITreeNode> survival] 
    : p=selector[$candidates] (c=multi_selectors[$c.survival == null ? $p.survival : $c.survival])*
        {
            if ( $c.survival != null ) {
                $survival = $c.survival; 
            } else {
                $survival = $p.survival;
            }
        }
    ;

multi_selectors [List<ITreeNode> candidates] returns [List<ITreeNode> survival] 
    : selector[$candidates]
        {
            $survival = $selector.survival; 
        }
    | '>' c=selector[$candidates.size() > 0 ? $candidates.get(0).getChildren() : new ArrayList<ITreeNode>()]
        {
            debug("成功匹配\"> " + $selector.text + "\"");
            $survival = $c.survival;
            descreaseIndent();
        }
    | '>' level=DIGIT+ c=selector[descendants($candidates, Integer.parseInt($level.text))]
        {
            debug("成功匹配\">" + $level.text + " " + $selector.text + "\"");
            $survival = $c.survival;
            descreaseIndent();
        }
        // 支持对多个节点的子孙进行查询匹配
    | DESCENDANT c=selector[descendants($candidates, -1)]
        {
            debug("成功匹配\">> " + $selector.text + "\"");
            $survival = $c.survival;
            descreaseIndent();
        }
    ;

selector [List<ITreeNode> candidates] returns [List<ITreeNode> survival] 
    : selector_expression[$candidates]
        {
            increaseIndent();
            $survival = $selector_expression.survival; 
        }
    | multi_attributes[$candidates]
        {
            $survival = $multi_attributes.survival;
        }
    ;

multi_attributes [List<ITreeNode> candidates] returns [List<ITreeNode> survival] 
    : '[' attr=ELEMENT op v=QUOTED_STRING ']'
        {
            debug("成功匹配\"[" + $attr.text + " " + $v.text + "]\"");
         
            List<ITreeNode> result = new ArrayList<ITreeNode>();
            String optext = $op.text;
            String vtext = $v.text;
            String key = $attr.text;
            String method = key + "()";
            String criteria = vtext.substring(1, vtext.length() - 1);

            for ( int i = 0; i < $candidates.size(); ++i ){
                ITreeNode node = $candidates.get(i);
                String qkey = key;
                boolean found = false;
                
                if ( node.containsProperty(key) ) {
                    found = true;
                } else if ( node.containsProperty(method) ) {
                    qkey = method;
                    found = true;
                }

                if ( found ) {
                    IProperty property = node.getProperty(qkey);
                    String value = property.getValue();

                    if ( optext.compareTo("=") == 0 ) {
                        if ( value != null && value.compareTo(criteria) == 0 ) {
                            result.add(node);   
                        }
                    } else if ( optext.compareTo("!=") == 0 ) {
                        if ( value == null || value.compareTo(criteria) != 0 ) {
                            result.add(node);   
                        }
                    } else if ( optext.compareTo("$=") == 0 )  {
                        if ( value != null && value.endsWith(criteria) ) {
                            result.add(node);
                        }
                    } else if ( optext.compareTo("^=") == 0 ) {
                        if ( value != null && value.startsWith(criteria) ) {
                            result.add(node);
                        }
                    }
                }
            }

            $survival = result;
        }
    | '[' attr=ELEMENT ']'
        {
            debug("成功匹配\"[" + $attr.text + "]\"");
            List<ITreeNode> result = new ArrayList<ITreeNode>();
            String key = $attr.text;
            String method = key + "()";

            for ( int i = 0; i < $candidates.size(); ++i ){
                ITreeNode node = $candidates.get(i);
                if ( node.containsProperty(key) || 
                     node.containsProperty(method) ) {
                    result.add(node);
                }
            }

            $survival = result;
        }
    ;

op
    : '='
    | '!='
    | '$='
    | '^='
    ;

indexop
    : EQ
    | GT
    | LT
    | NTH_CHILD
    ;

selector_expression [List<ITreeNode> candidates] returns [List<ITreeNode> survival] 
    : atom[$candidates]
        {
            $survival = $atom.survival;
        }
    | ':' indexop '(' vidx=DIGIT+ ')'
        {
            debug("成功匹配\":" + $indexop.text + "(" + $vidx.text + ")\""); 
            int idx = Integer.parseInt($vidx.text);
            String op = $indexop.text;            
            $survival = new ArrayList<ITreeNode>();

            if ( $candidates != null ) {
                if ( op.compareTo("eq") == 0 ) {
                    if ( idx < $candidates.size() ) {
                        ITreeNode node = $candidates.get(idx);
                        $survival.add(node);
                    }
                } else if ( op.compareTo("gt") == 0 ) {                
                    for ( int i = idx + 1; i < $candidates.size(); ++i ) {
                        $survival.add($candidates.get(i));
                    }
                } else if ( op.compareTo("lt") == 0 ) {
                    for ( int i = 0; i < idx && i < $candidates.size(); ++i ) {
                        $survival.add($candidates.get(i));
                    }
                } else if ( op.compareTo("nth-child") == 0 ) {         
                    for ( int i = 0; i < $candidates.size(); ++i ) {
                        ITreeNode node = $candidates.get(i);
                        List<ITreeNode> children = node.getChildren();
                        
                        if ( idx < children.size() ) {
                            $survival.add(node.getChildren().get(idx));
                        }
                    }
                }
            }
        }
    | ':' NOT '(' selectors[$candidates] ')'
        {
            debug("成功匹配\":not(" + $selectors.text + ")\"");
            List<ITreeNode> allNodes = $candidates;
            allNodes.removeAll($selectors.survival);
            $survival = allNodes;
        }
    | ':' HAS '(' selectors[$candidates] ')'
        {
            debug("成功匹配\":has(" + $selectors.text + ")\"");
            List<ITreeNode> children = $selectors.survival;
            List<ITreeNode> result = new ArrayList<ITreeNode>();
            for ( int i = 0; i < children.size(); ++i ) {
			    ITreeNode p = children.get(i).getParent();
                if ( p != null && result.indexOf(p) == -1 ) {
                    result.add(p);
                }
            }

            $survival = result;
        }
    | ':' CONTAINS '(' text=QUOTED_STRING ')'
        {
            debug("成功匹配\":contains('" + $text.text + "')\"");
            Map<String, String> attributes = new HashMap<String, String>();
            String attribute = $text.text;
            String value = attribute.substring(1, attribute.length() - 1);
            attributes.put("mText", value);

            List<ITreeNode> result = new ArrayList<ITreeNode>();
            for ( int i = 0; i < candidates.size(); ++i ) {
                ITreeNode node = candidates.get(i);
                boolean isMatched = true;
                for ( Map.Entry<String, String> entry : attributes.entrySet() ) {
                    if ( !node.containsProperty(entry.getKey()) ) {
                        isMatched = false;
                        break;
                    }
                    
                    IProperty property = node.getProperty(entry.getKey());
                    if ( property == null ||
                         property.getValue().indexOf(entry.getValue()) < 0 ) {
                        isMatched = false;
                        break;
                    }
                }
                
                if ( isMatched ) 
                    result.add(node);
            }             

            $survival = result;    
        }
    | ':' LAST_CHILD
        {
            debug("成功匹配\":last-child\"");
            // 添加当前匹配的所有的节点的最后一个子节点
            List<ITreeNode> nodes = new ArrayList<ITreeNode>();
            for ( int i = 0; i < $candidates.size(); ++i ) {
                ITreeNode node = $candidates.get(i);
                if ( node.getChildren().size() > 0 ) {
                    nodes.add(node.getChildren().get(node.getChildren().size() - 1));
                }
            }

            $survival = nodes;
        }
    | ':' FIRST_CHILD
        {
            debug("成功匹配\":first-child\"");
            List<ITreeNode> nodes = new ArrayList<ITreeNode>();
            for ( int i = 0; i < $candidates.size(); ++i ) {
                ITreeNode node = $candidates.get(i);
                if ( node.getChildren().size() > 0 ) {
                    nodes.add(node.getChildren().get(0));
                }
            }

            $survival = nodes;
        }
    | ':' FIRST
        {
            debug("成功匹配\":first\"");
            $survival = new ArrayList<ITreeNode>();
            List<ITreeNode> nodes = $candidates;
            if ( nodes.size() == 0 ) {
                warning("前次匹配已经没有留下任何控件，匹配级是空的！");
            } else {
                ITreeNode first = nodes.get(0);
                $survival.add(first);
            }
        }
    | ':' LAST
        {
            debug("成功匹配\":last\"");
            $survival = new ArrayList<ITreeNode>();
            List<ITreeNode> nodes = $candidates;
            if ( nodes.size() == 0 ) {
                warning("前次匹配已经没有留下任何控件，匹配级是空的！");
            } else {
                ITreeNode last = nodes.get(nodes.size() - 1);
                $survival.add(last);
            }
        }
    | ':' TEXT
        {
            debug("成功匹配\":text\"");
            // 对于:text元条件，我们就认为所有包含mText属性的控件都是Text
            $survival = filterByAttributes($candidates, "mText");
        }
    | ':' RADIO
        {
            debug("成功匹配\":radio\"");
            // TODO: 对于android里的RadioGroup来说是否应该当成radio?
            $survival = filterByNameStartsWith($candidates, "RadioButton");
        }
    | ':' EMPTY
        {
            debug("成功匹配\":empty\"");
            List<ITreeNode> nodes = $candidates;
            List<ITreeNode> result = new ArrayList<ITreeNode>();
            for ( int i = 0; i < nodes.size(); ++i ) {
                if ( nodes.get(i).getChildren().size() == 0 )
                    result.add(nodes.get(i));
            }
            
            $survival = result;
        }
    | ':' VISIBLE
        {
            debug("成功匹配\":visible\"");
            Map<String, String> attributes = new HashMap<String, String>();
            attributes.put("getVisibility()", "VISIBLE");
            $survival = filterByAttributes($candidates, attributes);
        }
    | ':' HIDDEN
        {
            debug("成功匹配\":hidden\"");
            Map<String, String> attributes = new HashMap<String, String>();
            attributes.put("getVisibility()", "VISIBLE");
            $survival = filterByExcludingAttributes($candidates, attributes);
        }
    | ':' FOCUS
        {
            debug("成功匹配\":focus\"");
            Map<String, String> attributes = new HashMap<String, String>();
            attributes.put("isFocused()", "true");
            $survival = filterByAttributes($candidates, attributes);
        }
    | ':' CHECKBOX
        {
            debug("成功匹配\":checkbox\"");
            // TODO: Android上好像没有复选框这个概念？
            $survival = new ArrayList<ITreeNode>();
        }
    | ':' BUTTON
        {
            debug("成功匹配\":button\"");
            $survival = filterByNameEndsWith($candidates, "Button");
        }
    | ':' IMAGE
        {
            debug("成功匹配\":image\"");
            $survival = filterByNameStartsWith($candidates, "Image");
        }
    | ':' LABEL
        {
            debug("成功匹配\":label\"");
            $survival = filterByNameStartsWith($candidates, "TextView");
        }
    | ':' PARENT
        {
            debug("成功匹配\":parent\"");            
            ITreeNode candidate = 
                $candidates == null ? null 
                                    : $candidates.size() == 0 ? null : $candidates.get(0);
            $survival = new ArrayList<ITreeNode>();
            if ( candidate != null )
                $survival.add(candidate.getParent());
        }
    ;

atom [List<ITreeNode> candidates] returns [List<ITreeNode> survival] 
    : ASTERISK
        { 
            debug("成功匹配:\"*\"");
            $survival = candidates;
        }
    | ELEMENT
        {
            if ( candidates == null || candidates.size() == 0 ) {
                warning("在ELEMENT规则里，没有candidates对象输入");
                $survival = new ArrayList<ITreeNode>();
            } else {            
                List<ITreeNode> nodes = new ArrayList<ITreeNode>();
                String filter = $ELEMENT.text;
                debug("成功匹配\"" + filter + "\"");
                for ( int i = 0; i < candidates.size(); ++i ) {
                    ITreeNode node = candidates.get(i);        
                    String ctrl = type(node);
                    
                    if ( filter.compareTo(ctrl) == 0 ) {
                        nodes.add(node);
                    }
                }

                $survival = nodes;
            }
        }
    ;

DESCENDANT: '>>';
EQ: 'eq';
GT: 'gt';
LT: 'lt';
NOT: 'not';
CONTAINS: 'contains';
TEXT: 'text';
RADIO: 'radio';
EMPTY: 'empty';
CHECKBOX: 'checkbox';
FOCUS: 'focus';
HAS: 'has';
CHECKED: 'checked';
PREV: 'prev';
NEXT: 'next';
SIBLINGS: 'siblings';
NTH_CHILD: 'nth-child';
PARENT: 'parent';
DISABLED: 'disabled';
VISIBLE: 'visible';
HIDDEN: 'hidden';
BUTTON: 'button';
LABEL: 'label';
IMAGE: 'image';
LAST_CHILD: 'last-child';
FIRST_CHILD: 'first-child';
FIRST: 'first';
LAST: 'last';
DIGIT: ('0' .. '9');
ELEMENT: ('a'..'z'|'A'..'Z'|'_')('a'..'z'|'A'..'Z'|'0'..'9'|'_'|'.')*;
ASTERISK: '*';
QUOTED_STRING: '\'' .+ '\''; 
NEWLINE: '\r'? '\n';
WS: (' ' | '\t') { skip(); };

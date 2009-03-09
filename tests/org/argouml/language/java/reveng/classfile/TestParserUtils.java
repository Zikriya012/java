// $Id: ParseState.java 116 2009-01-06 11:23:31Z thn $
// Copyright (c) 2003-2006 The Regents of the University of California. All
// Rights Reserved. Permission to use, copy, modify, and distribute this
// software and its documentation without fee, and without a written
// agreement is hereby granted, provided that the above copyright notice
// and this paragraph appear in all copies.  This software program and
// documentation are copyrighted by The Regents of the University of
// California. The software program and documentation are supplied "AS
// IS", without any accompanying services from The Regents. The Regents
// does not warrant that the operation of the program will be
// uninterrupted or error-free. The end-user understands that the program
// was developed for research purposes and is advised not to rely
// exclusively on the program for any reason.  IN NO EVENT SHALL THE
// UNIVERSITY OF CALIFORNIA BE LIABLE TO ANY PARTY FOR DIRECT, INDIRECT,
// SPECIAL, INCIDENTAL, OR CONSEQUENTIAL DAMAGES, INCLUDING LOST PROFITS,
// ARISING OUT OF THE USE OF THIS SOFTWARE AND ITS DOCUMENTATION, EVEN IF
// THE UNIVERSITY OF CALIFORNIA HAS BEEN ADVISED OF THE POSSIBILITY OF
// SUCH DAMAGE. THE UNIVERSITY OF CALIFORNIA SPECIFICALLY DISCLAIMS ANY
// WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
// MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE SOFTWARE
// PROVIDED HEREUNDER IS ON AN "AS IS" BASIS, AND THE UNIVERSITY OF
// CALIFORNIA HAS NO OBLIGATIONS TO PROVIDE MAINTENANCE, SUPPORT,
// UPDATES, ENHANCEMENTS, OR MODIFICATIONS.

package org.argouml.language.java.reveng.classfile;

import antlr.ASTFactory;
import antlr.CommonAST;
import antlr.RecognitionException;
import antlr.TokenStreamException;
import antlr.debug.misc.ASTFrame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileInputStream;
import java.io.StringBufferInputStream;
import java.util.List;
import junit.framework.TestCase;

/**
 *
 * @author Alexander Lepekhin
 */
public class TestParserUtils extends TestCase {

    public TestParserUtils(String str) {
        super(str);
    }

    public void testFieldDescriptorLexer() throws Exception {
        ParserUtils.FieldDescriptorLexer lexer = new ParserUtils.FieldDescriptorLexer("Bwhatelse");
        List<ParserUtils.Token> tokens = lexer.parse();
        assertEquals("B", tokens.get(0).getValue());
        assertEquals("whatelse", lexer.getRest());
    }

    public void testParseFieldDescriptor() throws Exception {
        assertEquals("byte", ParserUtils.convertFieldDescriptor("B"));
        assertEquals("char", ParserUtils.convertFieldDescriptor("C"));
        assertEquals("double", ParserUtils.convertFieldDescriptor("D"));
        assertEquals("float", ParserUtils.convertFieldDescriptor("F"));
        assertEquals("int", ParserUtils.convertFieldDescriptor("I"));
        assertEquals("long", ParserUtils.convertFieldDescriptor("J"));
        assertEquals("short", ParserUtils.convertFieldDescriptor("S"));
        assertEquals("boolean", ParserUtils.convertFieldDescriptor("Z"));
        assertEquals("byte[][][]", ParserUtils.convertFieldDescriptor("[[[B"));
        assertEquals("a", ParserUtils.convertFieldDescriptor("La;"));
        assertEquals("a.b", ParserUtils.convertFieldDescriptor("La/b;"));
        assertEquals("java.lang.String[][]", ParserUtils.convertFieldDescriptor("[[Ljava/lang/String;"));
    }

    public void testParseMethodDescriptor() throws RecognitionException, TokenStreamException  {
        String[] result = ParserUtils.convertMethodDescriptor("(ID[[[Ljava/lang/Thread;)Ljava/lang/Object;");
        assertEquals("int", result[0]);
        assertEquals("double", result[1]);
        assertEquals("java.lang.Thread[][][]", result[2]);
        assertEquals("java.lang.Object", result[3]);
        result = ParserUtils.convertMethodDescriptor("()V");
        assertEquals("void", result[0]);
    }

    public void testBalancedBracket() {
    	assertEquals(5, ParserUtils.balancedBracketPosition("<1<>4>234", '<', '>'));
    }
    
    public void testConvertFieldTypeSignature() {
        assertEquals("java.lang.Comparable<?>",
                ParserUtils.convertFieldTypeSignature("Ljava/lang/Comparable<*>;"));
        assertEquals("java.lang.Comparable<? extends a.b.C>",
                ParserUtils.convertFieldTypeSignature("Ljava/lang/Comparable<+La/b/C;>;"));
        assertEquals("java.lang.Comparable<? super a.b.C>",
                ParserUtils.convertFieldTypeSignature("Ljava/lang/Comparable<-La/b/C;>;"));
        assertEquals("java.lang.Comparable<java.lang.String>",
                ParserUtils.convertFieldTypeSignature("Ljava/lang/Comparable<Ljava/lang/String;>;"));
        assertEquals("java.lang.Comparable",
                ParserUtils.convertFieldTypeSignature("Ljava/lang/Comparable;Ljava/lang/String;"));
       assertEquals("java.lang.Map<java.lang.String,java.lang.Integer>",
               ParserUtils.convertFieldTypeSignature("Ljava/lang/Map<Ljava/lang/String;Ljava/lang/Integer;>;"));
       assertEquals("java.lang.Map<byte[],int[][]>",
               ParserUtils.convertFieldTypeSignature("Ljava/lang/Map<[B[[I>;"));
        assertEquals("java.lang.Map<byte[],java.lang.Map<E,E>[][]>",
                ParserUtils.convertFieldTypeSignature("Ljava/lang/Map<[B[[Ljava/lang/Map<TE;TE;>;>;"));
        assertEquals("java.lang.Map<byte[],java.lang.Map<E,E>[][]>.Inner<d.inner>.Inner2",
                ParserUtils.convertFieldTypeSignature("Ljava/lang/Map<[B[[Ljava/lang/Map<TE;TE;>;>.Inner<Ld.inner;>.Inner2;"));
    }

    static String txt = "/home/alepekhin/projects/work_issue3204_lepekhine/build/tests/classes/org/argouml/language/java/reveng/TestClassImportGenerics$TestedClass.class";

    public static void main(String[] args) throws Exception {
        SimpleByteLexer lexer = new SimpleByteLexer(new FileInputStream((txt)));
        ClassfileParser parser = new ClassfileParser(lexer);
        parser.classfile();
        CommonAST t = (CommonAST) parser.getAST();
        ASTFactory factory = parser.getASTFactory();
        CommonAST r = (CommonAST) factory.create(0, "ROOT");
        r.addChild(t);
        ASTFrame frame = new ASTFrame("AST", r);
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent event) {
                System.exit(0);
            }
        });
        frame.setVisible(true);
    }
}
package no.uio.ifi.asp.parser;

import no.uio.ifi.asp.main.Main;
import no.uio.ifi.asp.runtime.RuntimeReturnValue;
import no.uio.ifi.asp.runtime.RuntimeScope;
import no.uio.ifi.asp.runtime.RuntimeValue;
import no.uio.ifi.asp.scanner.Scanner;

import java.util.ArrayList;

import static no.uio.ifi.asp.scanner.TokenKind.*;

/**
 *
 */
public class AspFuncDef extends AspStmt{

    ArrayList<AspName> argNameList = new ArrayList<AspName>();
    AspName funcName;
    AspSuite funcSuite;

    public AspFuncDef(int i) {
        super(i);
    }

    public static AspFuncDef parse(Scanner s) {
        Main.log.enterParser("func def");
        AspFuncDef funcDef = new AspFuncDef(s.curLineNum());

            skip(s, defToken);
            funcDef.funcName = AspName.parse(s);
            skip(s,leftParToken);

            if (s.curToken().kind != rightParToken){
                while (true){
                funcDef.argNameList.add(AspName.parse(s));
                //System.out.println();
                if(s.curToken().kind == commaToken) skip(s, commaToken);
                else break;
                }
            }

            skip(s, rightParToken);
            skip(s,colonToken);
            funcDef.funcSuite = AspSuite.parse(s);

        Main.log.leaveParser("func def");
        return funcDef;
    }

    @Override
    public void prettyPrint() {
        int i = 0;
        Main.log.prettyWrite(defToken.toString()+" ");
        funcName.prettyPrint();
        Main.log.prettyWrite(" "+leftParToken.toString());

        if (argNameList.size() > 0){
            argNameList.get(0).prettyPrint();
            for (int j = 1; j < argNameList.size(); j++) {
                Main.log.prettyWrite(commaToken.toString() + " ");
                argNameList.get(j).prettyPrint();
            }
        }
        Main.log.prettyWrite(rightParToken.toString());
        Main.log.prettyWrite(colonToken.toString());
        //Main.log.prettyWriteLn();
        funcSuite.prettyPrint();
    }

    @Override
    public RuntimeValue eval(RuntimeScope curScope) throws RuntimeReturnValue {
        //-- Must be changed in part 3:
        return null;
    }

}

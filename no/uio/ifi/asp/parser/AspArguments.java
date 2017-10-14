/*
* This program reads from the scanner and
*
 */


package no.uio.ifi.asp.parser;

import no.uio.ifi.asp.main.Main;
import no.uio.ifi.asp.scanner.Scanner;

import java.util.ArrayList;

import static no.uio.ifi.asp.scanner.TokenKind.*;
/*
*This class creates the object of AspArguments and stores the Asp expressions in ArrayList.
* @ extends AspPrimarySuffix
 */
public class AspArguments extends AspPrimarySuffix {

    ArrayList<AspExpr> exprList = new ArrayList<AspExpr>();

    AspArguments(int n) {
        super(n);
    }

    public static AspArguments parse(Scanner s) {
        Main.log.enterParser("AspArguments");
        AspArguments arg = new AspArguments(s.curLineNum());


        if(s.curToken().kind == leftParToken) {
            skip(s,leftParToken);
            if(s.curToken().kind != rightParToken){
                while(true) {
                    arg.exprList.add(AspExpr.parse(s));

                    if(s.curToken().kind == commaToken)
                    {
                        skip(s,commaToken);
                    }
                    else  break;
                }
            }
            skip(s,rightParToken);
        }
        Main.log.leaveParser("AspArguments");
        return arg;
    }

}
